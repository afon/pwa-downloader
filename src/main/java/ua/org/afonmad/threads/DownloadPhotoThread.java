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
package ua.org.afonmad.threads;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ua.org.afonmad.utils.InformationCounter;
import ua.org.afonmad.wrapper.SimplePhotoEntry;

/**
 * <p>Exactly as it named. Represents one particular thread that downloads 
 * particular photo entry from Picasa on to local hdd. If some photo can't be downloaded 
 * (i.e error occurred) it stops, delete partially downloaded file and register and error.</p> 
 * 
 * <p>But <b>you should be warned</b> that if file was downloaded partially and no error was registered 
 * (this means for example power outage) then file will not be downloaded next run.</p>
 *  
 * <p>See {@link PhotoEntryCollector#isAlreadyDownloaded}  
 * method that just check whether file is already exists locally.</p>
 */
public class DownloadPhotoThread extends Thread {

	private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
	private static final int READ_TIMEOUT = 30000; // 30 seconds	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private SimplePhotoEntry photo;
	private File parentFolder;
	private int restarts = 0;
	private DownloadStatus downloadStatus = DownloadStatus.FAIL;
	private DownloadResultListener downloadResultListener = DownloadResultListener.getInstance();

	public DownloadPhotoThread(SimplePhotoEntry photo, File parentFolder) {
		this.photo = photo;
		this.parentFolder = parentFolder;
		this.setName("Thread [" + photo.getFileName() + "]");
	}

	@Override
	public void run() {
		restarts++;
		File destinationFile = prepareDestinationFile();
		try {
			logger.debug(photo + " download started");
			FileUtils.copyURLToFile(new URL(photo.getUrl()), destinationFile, CONNECTION_TIMEOUT, READ_TIMEOUT);
			destinationFile.setLastModified(photo.getDate().getTime());
			photo.getAlbum().markPhotoCompleted(photo);
			InformationCounter.increaseCompletedPhotos();
			logger.debug(photo + " downloaded successfully");
			downloadStatus = DownloadStatus.SUCCESS;
		} catch (Exception e) {
			logger.warn(photo + " can't be downloaded for reasons below. Partially downloaded file will be deleted", e);
			destinationFile.delete();
			InformationCounter.increaseDownloadErrors();
			downloadStatus = DownloadStatus.FAIL;
		}
		notifyResultListener();
	}

	public int getRestartCounter() {
		return restarts;
	}
	
	public DownloadStatus getDownloadStatus() {
		return downloadStatus;
	}
	
	private File prepareDestinationFile() {
		File destFile = new File(parentFolder.getAbsolutePath() + File.separator + photo.getAlbumName(), photo.getFileName());
		return destFile;
	}
	
	private void notifyResultListener() {
		downloadResultListener.handleDownloadResult(this);
	}

}
