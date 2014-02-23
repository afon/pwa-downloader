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
package ua.org.afonmad.utils;

import java.util.Date;

import com.google.gdata.data.photos.PhotoEntry;

/**
 * Simple utility methods for extraction reasonable date of photo/video creation.
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
