package ua.org.afonmad.exchange;

import org.apache.log4j.Logger;

import ua.org.afonmad.utils.PhotoEntryUtils;

import com.google.gdata.data.media.mediarss.MediaContent;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;

/**
 * Just static tracer for logging additional entries information  
 * 
 * @author Andrew Afanasenko, 2013, Kiev, Ukraine
 */
public class MetadataInfo {
	
	private static final Logger logger = Logger.getLogger(MetadataInfo.class.getName()); 

    public static void printAlbumInfo(AlbumEntry album) {
    	logger.trace("Title:\t" + album.getTitle().getPlainText());
        logger.trace("Местоположение:\t" + album.getLocation());
        logger.trace("Короткое Имя:\t" + album.getName());  
        logger.trace("Ссылка Id\t" + album.getUsername());
        logger.trace("Автор Альбома:\t" + album.getNickname());                  		
        logger.trace("Размер альбома:\t" + album.getBytesUsed());                 		
        logger.trace("Дата создания:\t" + album.getDate());		          		
        logger.trace("Описание:\t" + album.getDescription().getPlainText());
        if (album.getGeoLocation() != null) {
            logger.trace("Широта:\t" + album.getGeoLocation().getLatitude());
            logger.trace("Долгота\t" + album.getGeoLocation().getLongitude());
        }
    }

    public static void printPhotoInfo(PhotoEntry photo) throws Exception {
		logger.trace("OrigPhoto Title:\t" + photo.getTitle().getPlainText());
		logger.trace("PhotoId:\t" + photo.getGphotoId());
		logger.trace("OrigPhoto PhotoSize:\t" + photo.getSize());
		logger.trace("OrigPhoto Date Exif:\t" + PhotoEntryUtils.getPhotoDateExif(photo));
		logger.trace("OrigPhoto Date Published:\t" + PhotoEntryUtils.getPhotoDatePublished(photo));
		logger.trace("OrigPhoto Date Timestamp:\t" + PhotoEntryUtils.getPhotoDateTimestamp(photo));
		logger.trace("Description:\t" + photo.getDescription().getPlainText());
                                                            
        logger.trace("Media Contents:");                                         
        for (MediaContent mc : photo.getMediaContents()) {                            
            logger.trace("\tMedium Type: " + mc.getMedium());                                       
            logger.trace("\tWidth: " + mc.getWidth());                                        
            logger.trace("\tURL: " + mc.getUrl());            
            logger.trace("\tX-Type: " + mc.getType());
        }                                                                             
    }

}
