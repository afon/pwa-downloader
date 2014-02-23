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
	
	private static final Logger logger = Logger.getLogger(MetadataInfo.class); 

    public static void printAlbumInfo(AlbumEntry album) {
    	logger.trace("Title:\t" + album.getTitle().getPlainText());
        logger.trace("Location:\t" + album.getLocation());
        logger.trace("Short Name:\t" + album.getName());  
        logger.trace("Username Id\t" + album.getUsername());
        logger.trace("Author Name:\t" + album.getNickname());                  		
        logger.trace("Album Size in bytes:\t" + album.getBytesUsed());                 		
        logger.trace("Creation Date:\t" + album.getDate());		          		
        logger.trace("Description:\t" + album.getDescription().getPlainText());
        if (album.getGeoLocation() != null) {
            logger.trace("Latitude:\t" + album.getGeoLocation().getLatitude());
            logger.trace("Longitude\t" + album.getGeoLocation().getLongitude());
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
            logger.trace("\tBitrate: " + mc.getBitrate());
            logger.trace("\tFramerate: " + mc.getFramerate());
            logger.trace("\tSamplingrate: " + mc.getSamplingrate());
            logger.trace("\tURL: " + mc.getUrl());            
            logger.trace("\tX-Type: " + mc.getType());
        }                                                                             
    }

}

