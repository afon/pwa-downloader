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

import org.apache.log4j.Logger;

import ua.org.afonmad.Configurator;
import ua.org.afonmad.utils.InformationCounter;

/**
 * When the {@link DownloadPhotoThread} completed its work he notifies this Listener about that fact - either success or fail.
 * If thread was fail to download photo entry, then this Listener put thread back to download pool in order to try download once again, 
 * but no more then 3 times.
 */
public class DownloadResultListener {

	private static Configurator configurator = Configurator.getInstance();
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private DownloadResultListener() {
	}
	
	public static DownloadResultListener getInstance() {
		return InstanceHolder.listenerInstance;
	}

	public void handleDownloadResult(DownloadPhotoThread downloadPhotoThread) {
		switch (downloadPhotoThread.getDownloadStatus()) {
			case SUCCESS:
				InformationCounter.increaseCompletedThreads();
				break;
			case FAIL:
				if (downloadPhotoThread.getRestartCounter() == 3) {
					logger.error(downloadPhotoThread.getName() + " was restarted 3 times with FAIL result and will be stopped permanently");
					InformationCounter.increaseCompletedThreads();
					break;
				}
				logger.debug("Restarting [" + downloadPhotoThread.getName() + "] thread");
				configurator.getDownloadPool().execute(downloadPhotoThread);
				break;
		} 
	}
	
	private static class InstanceHolder {
		private static final DownloadResultListener listenerInstance = new DownloadResultListener();
	}
	

}
