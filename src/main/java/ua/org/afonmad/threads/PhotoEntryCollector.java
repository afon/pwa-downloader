package ua.org.afonmad.threads;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import ua.org.afonmad.exchange.PicasaProxy;
import ua.org.afonmad.utils.InformationCounter;
import ua.org.afonmad.wrapper.SimpleAlbumEntry;
import ua.org.afonmad.wrapper.SimplePhotoEntry;

import com.google.gdata.data.photos.PhotoEntry;

/**
 * Walk through album's photo collection and check, whether each photo already downloaded earlier or not, 
 * and if not, adds it to queue for downloading during this session of work.  
 *
 * @author Andrew Afanasenko, 2013, Kiev, Ukraine
 */
public class PhotoEntryCollector implements Callable<Set<SimplePhotoEntry>> {

    private SimpleAlbumEntry simpleAlbum;
    private PicasaProxy picasa;
    private File outputDirectory;
    
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public PhotoEntryCollector(PicasaProxy picasa, SimpleAlbumEntry simpleAlbum, File outputDirectory) {
    	logger.debug("Collecting photo entries from album [" + simpleAlbum.getOriginalName() + "]");
    	this.picasa = picasa;
		this.simpleAlbum = simpleAlbum;
		this.outputDirectory = outputDirectory;
    }

    @Override
    public Set<SimplePhotoEntry> call() throws Exception {
		List<PhotoEntry> photos = picasa.getPhotos(simpleAlbum);
		Set<SimplePhotoEntry> photoEntries = new HashSet<SimplePhotoEntry>();
		
		/* hook for testing repeatable names in same album
		List<String> photoFileNames = new ArrayList<String>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (PhotoEntry fE : photos) {
			
		}
		
		return photoEntries;*/
		
		for (int i = 0; i < photos.size(); i++) {
		    SimplePhotoEntry simplePhotoEntry = new SimplePhotoEntry(photos.get(i), simpleAlbum);
		    if (!isAlreadyDownloaded(simplePhotoEntry)) {
		    	photoEntries.add(simplePhotoEntry);
		    	InformationCounter.increaseQueuedPhotos();
		    	logger.debug(simplePhotoEntry + " added to download by collector");
		    }	
		}
		if (!photoEntries.isEmpty()) {
			simpleAlbum.setPhotos(photoEntries);
			simpleAlbum.setOutputDirectory(outputDirectory);
			
			logger.debug("Collected for downloading [" + photoEntries.size() + "] photo meta data from album [" + simpleAlbum.getOriginalName() + "]");
			InformationCounter.increaseProcessedAlbums();
		}
		return photoEntries;
    }

	private boolean isAlreadyDownloaded(SimplePhotoEntry simplePhotoEntry) {
		File destFile = new File(outputDirectory.getAbsolutePath() + File.separator + simplePhotoEntry.getAlbumName(), simplePhotoEntry.getFileName());
		if (destFile.exists()) {
			InformationCounter.increaseAlreadyDownloadedPhotos();
			logger.debug(simplePhotoEntry + " alredy downloaded, so skipped by collector");
			return true;
		}
		return false;
	}



}
