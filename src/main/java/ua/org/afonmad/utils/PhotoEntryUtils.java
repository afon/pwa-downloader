package ua.org.afonmad.utils;

import java.util.Date;

import com.google.gdata.data.photos.PhotoEntry;

/**
 * Simple utility methods. Used in tracing additional entrie's information.
 *
 * @author Andrew Afanasenko, 2013, Kiev, Ukraine
 */
public class PhotoEntryUtils {

	public static Date getPhotoDateExif(PhotoEntry photo) throws Exception {
		if (photo.getExifTags() != null && photo.getExifTags().getTime() != null) {
			return photo.getExifTags().getTime();
		}
		return null;
	}
	
	public static Date getPhotoDateTimestamp(PhotoEntry photo) throws Exception {
		return photo.getTimestamp();
	}
	
	public static Date getPhotoDatePublished(PhotoEntry photo) throws Exception {
		return new Date(photo.getPublished().getValue());
	}

}
