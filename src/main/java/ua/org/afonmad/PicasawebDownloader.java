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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import ua.org.afonmad.threads.DownloadPhotoThread;
import ua.org.afonmad.threads.PhotoEntryCollector;
import ua.org.afonmad.utils.InformationCounter;
import ua.org.afonmad.wrapper.SimpleAlbumEntry;
import ua.org.afonmad.wrapper.SimplePhotoEntry;

/**
 * The main worker. Prepare and shutdown multiple threads for analyzing albums, collecting photos and running download itself.
 * Also it prepare logging and read command line parameters. 
 * By default thread pools uses 20 threads, you can change it by -t parameter.
 */
public class PicasawebDownloader {
	
	private Configurator configurator = Configurator.getInstance();
	static final Logger logger = Logger.getLogger(PicasawebDownloader.class);
	
	public static void main(String[] args) throws Exception {
		PicasawebDownloader imp = new PicasawebDownloader();
		try {
			imp.configurator.prepareSession(args);
			imp.runImport();
		}	catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(1); 
		}	catch (Exception e) {
			logger.fatal("A process stopped with following message:");
			logger.fatal(e);
		}
		imp.configurator.shutdownSession();
	}

	public void runImport() throws Exception {
//		List<SimpleAlbumEntry> albums = Lists.newArrayList(configurator.getPicasaProxy().getAlbum("Юльчик, видео"));
		List<SimpleAlbumEntry> albums = configurator.getPicasaProxy().getAlbums();
		Set<SimplePhotoEntry> parsedEntries = collectEntriesToDownload(albums);
		downloadEntries(parsedEntries);
	}

	private Set<SimplePhotoEntry> collectEntriesToDownload(List<SimpleAlbumEntry> albums) throws Exception {
		logger.info("Running album information analyzer ...");
		Set<Future<Set<SimplePhotoEntry>>> futureEntries = new HashSet<Future<Set<SimplePhotoEntry>>>();
		for (SimpleAlbumEntry album : albums) {
			Future<Set<SimplePhotoEntry>> simpleEntriesFromAlbum = configurator.getAlbumPool().submit(
					new PhotoEntryCollector(configurator.getPicasaProxy(), album, configurator.getOutputDirectory())
			);
			futureEntries.add(simpleEntriesFromAlbum);
		}
		configurator.getAlbumPool().shutdown();
		while (!configurator.getAlbumPool().isTerminated()) {
			//just wait for all photos collected
		}
		
		Set<SimplePhotoEntry> parsedEntries = new HashSet<SimplePhotoEntry>();
		for (Future<Set<SimplePhotoEntry>> futureResult : futureEntries) {
			parsedEntries.addAll(futureResult.get());
		}
		
		return parsedEntries;
	}

	private void downloadEntries(Set<SimplePhotoEntry> simplePhotoEntries) 
			throws InterruptedException, ExecutionException {
		if (InformationCounter.getQueuedPhotos() > 0) {
			logger.info("[" + InformationCounter.getQueuedPhotos() + "] photos from ["
					+ InformationCounter.getProcessedAlbums() + "] albums queued to download");
			logger.info("Running photo entries downloading ...");

			for (SimplePhotoEntry simpleEntry : simplePhotoEntries) {
				DownloadPhotoThread command = new DownloadPhotoThread(simpleEntry, configurator.getOutputDirectory());
				configurator.getDownloadPool().execute(command);
			}
		} else {
			logger.info("Noting to download now. Probably all photos are already downloaded.");
		}
		
		while (InformationCounter.getCompletedThreads() < InformationCounter.getQueuedPhotos()) {
			// wait while all threads completed
		}
	}
}
