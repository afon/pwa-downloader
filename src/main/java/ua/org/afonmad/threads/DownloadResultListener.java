package ua.org.afonmad.threads;

import ua.org.afonmad.Configurator;

public class DownloadResultListener {

	private static Configurator configurator = Configurator.getInstance();
	
	private DownloadResultListener() {
	}
	
	public static DownloadResultListener getInstance() {
		return InstanceHolder.listenerInstance;
	}

	public void handleDownloadResult(DownloadPhotoThread downloadPhotoThread) {
		switch (downloadPhotoThread.getDownloadStatus()) {
			case SUCCESS:
				downloadPhotoThread = null;
				break;
			case FAIL:
				configurator.getDownloadPool().execute(downloadPhotoThread);
				break;
		} 
	}
	
	private static class InstanceHolder {
		private static final DownloadResultListener listenerInstance = new DownloadResultListener();
	}
	

}
