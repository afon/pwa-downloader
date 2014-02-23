/*
 *	Copyright 2013-2014, Andrew Afanasenko, Kiev, Ukraine.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package ua.org.afonmad;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ua.org.afonmad.exchange.PicasaProxy;
import ua.org.afonmad.utils.FSUtils;
import ua.org.afonmad.utils.InformationCounter;

/**
 * Configure all program parameters before beginning main job.<br/>
 * Parses cli arguments, prepare logging, threadpools and output directory.<br/>
 * Also it call logging in method  to Google Pacasa.<br/>
 * See {@link #prepareSession(String[])} method that is an entry point of this class.
 */
public class Configurator {
	private static Configurator instance;

	private File outputDirectory;
	private ExecutorService albumPool;
	private ExecutorService downloadPool;
	private PicasaProxy picasaProxy;

	private static int N_THREADS = 20;
	private static String ACCOUNT_NAME;
	private static String ACCOUNT_PASSWORD;
	private static String OUTPUT_PATH;
	private static String APP_VERSION;

	private static final Logger logger = Logger.getLogger(Configurator.class);

	private Configurator() {
	}

	public static Configurator getInstance() {
		if (instance == null) {
			instance = new Configurator();
		}
		return instance;
	}

	public void prepareSession(String[] args) throws Exception {
		prepareProperties();
		prepareParameters(args);
		prepareConsoleLogging();
		prepareOutputDirectory();
		prepareFileLogging();

		logger.info(APP_VERSION + ". Session started");

		preparePicasaProxy();
		prepareThreadPools();
	}

	private void prepareProperties() throws IOException {
		Properties p = new Properties();
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("ua/org/afonmad/version.properties");
		if (inputStream == null) {
			throw new RuntimeException("property file version.properties not found in the classpath");
		}
		p.load(inputStream);
		APP_VERSION = p.getProperty("app.name") + " " + p.getProperty("app.version") + ", built at "
				+ p.getProperty("app.date");
	}

	private void prepareConsoleLogging() {
		ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %C{1} %x - %m%n"));
		consoleAppender.setName("consoleAppender");
		consoleAppender.setThreshold(Level.INFO);
		Logger.getRootLogger().addAppender(consoleAppender);
	}

	private void prepareParameters(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("t", true, "Optional. Threads quantity used for setting up simultaneous download threads");
		options.addOption("l", true, "Required. Login used to log in into Picasa");
		options.addOption("p", true, "Required. Password used to log in into Picasa");
		options.addOption("d", true, "Required. Local path to directory where to download albums");

		CommandLineParser parser = new BasicParser();
		CommandLine cli = null;
		try {
			cli = parser.parse(options, args);
			if (!cli.hasOption("l") || !cli.hasOption("p") || !cli.hasOption("d")) {
				throw new ParseException("You should specify Login, Password and Directory in arguments");
			}
		} catch (Exception e) {
			System.out.println(APP_VERSION);
			HelpFormatter formatter = new HelpFormatter();
			formatter.setWidth(120);
			formatter.printHelp(
					"java -jar pwa-downloader.jar -l <login> -p <password> -d <output dir> [-t <threads count>]",
					options);
			throw new ParseException("\n" + e.getMessage());
		}

		if (cli.hasOption("t")) {
			try {
				N_THREADS = new Integer(cli.getOptionValue("t"));
			} catch (NumberFormatException e) {
				// live N_TREADS as 20 by default
			}
		}

		ACCOUNT_NAME = cli.getOptionValue("l");
		ACCOUNT_PASSWORD = cli.getOptionValue("p");
		OUTPUT_PATH = cli.getOptionValue("d");
	}

	private File prepareOutputDirectory() throws Exception {
		logger.debug("Validating output directory ...");
		outputDirectory = FSUtils.createValidatedOutputDirectory(OUTPUT_PATH);
		FSUtils.cleanOutputDirectory(outputDirectory);
		return outputDirectory;
	}

	private void prepareFileLogging() throws IOException {
		String logFileName = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss").format(new Date()) + ".log";
		FileAppender fileAppender = new FileAppender(new PatternLayout("%d [%t] %-5p %C{1} - %m%n"),
				outputDirectory.getAbsolutePath() + File.separator + logFileName);
		fileAppender.setName("fileAppender");
		Logger.getRootLogger().addAppender(fileAppender);
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	private void preparePicasaProxy() throws Exception {
		logger.debug("Prepare access to PicasawebService ...");
		picasaProxy = new PicasaProxy();
		picasaProxy.login(ACCOUNT_NAME, ACCOUNT_PASSWORD);
		logger.debug("PicasawebService accessed successfully");
	}

	private void prepareThreadPools() {
		albumPool = Executors.newFixedThreadPool(N_THREADS);
		downloadPool = Executors.newFixedThreadPool(N_THREADS);
		logger.debug("Prepared " + N_THREADS + " threads for photo downloading");
	}

	public void shutdownSession() {
		logger.debug("Shutting down " + N_THREADS + " threads..");
		if (albumPool != null)
			albumPool.shutdown();
		if (downloadPool != null)
			downloadPool.shutdown();

		while (downloadPool != null && !downloadPool.isTerminated()) {
			// just waiting wile pool will complete all download work
		}
		logger.info("Session completed.");
		logger.info("Status: \n" + "\t" + InformationCounter.getQueuedPhotos() + " photos queued to download, \n"
				+ "\t" + InformationCounter.getCompletedPhotos() + " photos downloaded successfully, \n" + "\t"
				+ InformationCounter.getAlreadyDownloadedPhotos() + " photos skipped because downloaded earlier \n"
				+ "\tand " + InformationCounter.getDownloadErrors() + " errors registered during download.");
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public ExecutorService getAlbumPool() {
		return albumPool;
	}

	public ExecutorService getDownloadPool() {
		return downloadPool;
	}

	public PicasaProxy getPicasaProxy() {
		return picasaProxy;
	}

}
