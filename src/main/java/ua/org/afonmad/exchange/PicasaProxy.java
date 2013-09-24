package ua.org.afonmad.exchange;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.org.afonmad.wrapper.SimpleAlbumEntry;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;

/**
 * Proxy for talking to Picasa web service at Google.
 * 
 * @author Andrew Afanasenko, 2013, Kiev, Ukraine
 */
public class PicasaProxy {

	private PicasawebService service;
	private String accountName;
	private List<SimpleAlbumEntry> albums = new ArrayList<SimpleAlbumEntry>();
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public PicasawebService login(String accountName, String accountPassword) throws Exception {
		this.service = new PicasawebService("pwa-downloader");
		service.setUserCredentials(accountName + "@gmail.com", accountPassword);
		logger.info("Logged in PicasawebService as " + accountName);
		this.accountName = accountName;

		return service;
	}

	public List<SimpleAlbumEntry> getAlbums() throws Exception {
		logger.debug("Retrieving album meta data from PicasawebService ...");
		if (!this.albums.isEmpty()) {
			logger.debug("Albums already exists. Returning available list");
			return this.albums;
		}
		
		URL userFeed = new URL("https://picasaweb.google.com/data/feed/api/user/"+ accountName + "?kind=album");
		UserFeed albumFeed = service.getFeed(userFeed, UserFeed.class);
		List<AlbumEntry> albumEntries = albumFeed.getAlbumEntries();
		
		List<SimpleAlbumEntry> simpleAlbumEntries = new ArrayList<SimpleAlbumEntry>();
		for (AlbumEntry album : albumEntries) {
			MetadataInfo.printAlbumInfo(album);
			simpleAlbumEntries.add(new SimpleAlbumEntry(album));
		}
		this.albums = simpleAlbumEntries;
		
		logger.debug("Retrieved [" + albumEntries.size() + "] album meta data");
		return simpleAlbumEntries;
	}

	public SimpleAlbumEntry getAlbum(String albumName) throws Exception {
		SimpleAlbumEntry result = null;
		for (SimpleAlbumEntry album : getAlbums()) {
			if (album.getOriginalName().equals(albumName)) {
				result = album;
				break;
			}
		}
		
		if (result == null) {
			logger.warn("Can't find album with name [" + albumName + "]");
		}
		return result;
	}

	public List<PhotoEntry> getPhotos(SimpleAlbumEntry album) throws Exception {
		logger.debug("Retrieving photo meta data from album [" + album.getOriginalName() + "]");
		AlbumFeed feed = service.getFeed(album.getPhotoFeedUrl(), AlbumFeed.class);
		List<PhotoEntry> photoEntries = feed.getPhotoEntries();
		logger.debug("Retrieved [" + photoEntries.size() + "] photo meta data from album [" + album.getOriginalName() + "]");
		return photoEntries;
	}

}
