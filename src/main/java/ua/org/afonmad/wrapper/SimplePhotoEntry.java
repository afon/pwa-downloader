package ua.org.afonmad.wrapper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import ua.org.afonmad.exchange.MetadataInfo;
import ua.org.afonmad.utils.FSUtils;

import com.google.gdata.data.media.mediarss.MediaContent;
import com.google.gdata.data.photos.ExifTags;
import com.google.gdata.data.photos.PhotoEntry;

/**
 * The wrapper on PhotoEntry but
 * containing only useful for downloading information such as URL,
 * filename, creation date etc. 
 * 
 * @author Andrew Afanasenko, 2013, Kiev, Ukraine 
 */
public class SimplePhotoEntry {

	private static final String VIDEO_MEDIUM = "video";
	private final Logger logger = Logger.getLogger(this.getClass().getName());	
	
	@SuppressWarnings("serial")
	public static final Map<String, String> TYPE_TO_EXTENSION = new HashMap<String, String>() {
		{
			put("application/x-shockwave-flash", "flv");
			put("video/mpeg4", "mpg");
			put("image/jpeg", "jpg");
			put("image/bmp", "jpg");
			put("image/gif", "gif");
			put("image/png", "png");
		}
	};

	/**
	 * The URL from which file should be downloaded
	 */
	private String url;

	/**
	 * A date of file/photo creation. Grabbed either from EXIF info or
	 * photoEntry
	 */
	private Date date;

	private String fileName;
	
	private SimpleAlbumEntry album;

	public SimplePhotoEntry(PhotoEntry photo, SimpleAlbumEntry simpleAlbum) throws Exception {
		MetadataInfo.printPhotoInfo(photo);
		
		String mediaType = setUrl(photo);
		setFileName(photo, mediaType);
		
		setAlbum(simpleAlbum);
		setDate(photo);		
	}

	public String getUrl() {
		return url;
	}

	private String setUrl(PhotoEntry photo) {
		MediaContent downloadableMediaContent = photo.getMediaContents().get(0);
		if (photo.getMediaContents().size() > 1) {
			downloadableMediaContent = findBiggestVideo(photo.getMediaContents());
		}
		this.url = downloadableMediaContent.getUrl();
		return downloadableMediaContent.getType();
	}

	public Date getDate() {
		return date;
	}

	private void setDate(PhotoEntry photo) throws Exception {
		Calendar relevantDate = Calendar.getInstance();
		relevantDate.set(Calendar.YEAR, 1990);
		ExifTags exifTags = photo.getExifTags();
		
		if (photo.getTimestamp() != null && photo.getTimestamp().after(relevantDate.getTime())) {
			this.date = photo.getTimestamp(); 
		} else if (exifTags != null && exifTags.getTime() != null && exifTags.getTime().after(relevantDate.getTime())) {
			this.date = exifTags.getTime();
		} else {
			this.date = new Date(photo.getPublished().getValue());
		}	
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String newName) {
		fileName = newName;
	}
	
	private void setFileName(PhotoEntry photo, String mediaType) {
		String fileBaseName = FilenameUtils.getBaseName(photo.getTitle().getPlainText());
		String fileExt = TYPE_TO_EXTENSION.get(mediaType);
		String result = fileBaseName + "." + fileExt;
		
		if (!FSUtils.validateFileName(result)) {
			try {
				result = FSUtils.fixFileName(result);
			} catch (Exception e) {
				logger.warn("Can't use orig photo name as filename, [" + result + "]. Using photoId [" + photo.getGphotoId() + "]");
				logger.warn("Cause: " + e.getMessage());
				result = photo.getGphotoId() + "." + fileExt;
			}
		}
		
		this.fileName = result;
	}

	public String getAlbumName() {
		return album.getName();
	}

	public SimpleAlbumEntry getAlbum() {
		return album;
	}
	
	private void setAlbum(SimpleAlbumEntry album) {
		this.album = album;
	}
	
	private MediaContent findBiggestVideo(List<MediaContent> mediaContents) {
		MediaContent biggestVideo = mediaContents.get(0);
		for (MediaContent mediaContent : mediaContents) {
			if (isBiggerVideo(biggestVideo, mediaContent)) {
				biggestVideo = mediaContent;
			}
		}
		return biggestVideo;
	}

	private boolean isBiggerVideo(MediaContent biggestVideo, MediaContent mediaContent) {
		boolean isWidthBigger = mediaContent.getWidth() >= biggestVideo.getWidth();
		boolean isVideoMediumType = VIDEO_MEDIUM.equals(mediaContent.getMedium());
		
		return isVideoMediumType && isWidthBigger;		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(date).append(url).append(fileName).append(album.getName()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		SimplePhotoEntry rhs = (SimplePhotoEntry) obj;
		return new EqualsBuilder().append(date, rhs.date).append(url, rhs.url).append(fileName, rhs.fileName)
				.append(album.getName(), rhs.album.getName()).isEquals();
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName())
			.append(" [").append(album.getName()).append(", ").append(fileName).append("]").toString();
	}

}
