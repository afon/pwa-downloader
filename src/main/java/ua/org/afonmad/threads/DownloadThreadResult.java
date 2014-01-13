package ua.org.afonmad.threads;

public interface DownloadThreadResult {
	
	void onSuccess(DownloadPhotoThread process);
	
	void onFailure(DownloadPhotoThread process);

}
