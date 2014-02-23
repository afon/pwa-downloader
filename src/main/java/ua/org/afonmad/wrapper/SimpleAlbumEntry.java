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
package ua.org.afonmad.wrapper;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ua.org.afonmad.utils.FSUtils;

import com.google.gdata.data.photos.AlbumEntry;

/**
 * Wrapper on Picasa's Album class that use only valuable for downloading information about album.
 */
public class SimpleAlbumEntry {

	private URL photoFeedUrl;
	private String originalName;
	private String name;
	private Date date;
	private File outputDirectory;
	private Set<SimplePhotoEntry> photos; 
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public SimpleAlbumEntry(AlbumEntry album) throws Exception {
		this.photoFeedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/"
						+ album.getUsername() + "/albumid/"
						+ album.getGphotoId() + "?imgmax=d");
		
		this.originalName = album.getTitle().getPlainText();
		this.name = extractAndValidateName(album);
		this.date = album.getDate();
	}
	
	public URL getPhotoFeedUrl() {
		return photoFeedUrl;
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getName() {
		return name;
	}

	private String extractAndValidateName(AlbumEntry album) {
		String result = album.getTitle().getPlainText();
		logger.trace("Validating directory for album [" + result + "]");
		if (!FSUtils.validateDirName(result)) {
			try {
				result = FSUtils.fixDirName(result);
				logger.trace("Passed directory for album [" + result + "]");
			} catch (Exception e) {
				logger.warn("Can't create directory for album [" + result + "]. Using albumId [" + album.getGphotoId() + "]");
				logger.warn("Cause: " + e.getMessage());
				result = album.getGphotoId();				
			}
		}
		
		return result;
	}

	public Date getDate() {
		return this.date;
	}
	
	public void setPhotos(Collection<SimplePhotoEntry> photos) {
		if (!photos.isEmpty()) {
			this.photos = new HashSet<SimplePhotoEntry>();
			this.photos.addAll(photos);
		}
	}
	
	public void markPhotoCompleted(SimplePhotoEntry photo) {
		this.photos.remove(photo);
		if (this.photos.size() == 0) {
			File albumDir = new File(outputDirectory.getAbsolutePath() + File.separator + name);
			albumDir.setLastModified(date.getTime());
			logger.debug("Album [" + originalName + "] download completed.");
		}
	}
		
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
}
