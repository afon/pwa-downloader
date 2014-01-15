package ua.org.afonmad.threads;

import org.apache.log4j.Logger;

import ua.org.afonmad.Configurator;
import ua.org.afonmad.utils.InformationCounter;

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
