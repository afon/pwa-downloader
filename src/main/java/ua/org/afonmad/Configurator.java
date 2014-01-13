package ua.org.afonmad;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private static String APP_VERSION = "PicasawebDownloader v0.91, built at December 2013";
	
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
		prepareParameters(args);
		prepareConsoleLogging();
		logger.info(APP_VERSION + ". Session started");
		
		prepareOutputDirectory();
		prepareFileLogging();
		preparePicasaProxy();
		prepareThreadPools();
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
			formatter.printHelp("java -jar pwa-downloader.jar -l <login> -p <password> -d <output dir> [-t <threads count>]", options );
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
		logger.info("Output directory pointed to [" + outputDirectory.getAbsolutePath() + "]");
		return outputDirectory;
	}
	
	private void prepareFileLogging() throws IOException {
		String logFileName = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss").format(new Date()) + ".log";
		FileAppender fileAppender = new FileAppender(new PatternLayout("%d [%t] %-5p %C{1} - %m%n"), outputDirectory.getAbsolutePath() + File.separator + logFileName);
		fileAppender.setName("fileAppender");
		fileAppender.setThreshold(Level.DEBUG);
		Logger.getLogger("fileAppender").setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(fileAppender);
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
		if (albumPool != null) albumPool.shutdown();
		if (downloadPool != null) downloadPool.shutdown();
		
		while (downloadPool != null && !downloadPool.isTerminated()) {
			//just waiting wile pool will complete all download work
		}
		logger.info("Session completed.");
		logger.info("Status: \n" 
				+ "\t" + InformationCounter.getQueuedPhotos() + " photos queued to download, \n" 
				+ "\t" + InformationCounter.getCompletedPhotos() + " photos downloaded successfully, \n"
				+ "\t" + InformationCounter.getAlreadyDownloadedPhotos() + " photos skipped because downloaded earlier \n"
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
