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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents counters for different events inside system per session of work
 */
public class InformationCounter {

	/**
	 * A number of photo entries that are queued to download during current session of work.
	 */
	private static AtomicInteger queuedPhotos = new AtomicInteger(0);
	
	
	/**
	 * A number of photos that have been downloaded successfully during current session of work.
	 */
	private static AtomicInteger completedPhotos = new AtomicInteger(0);
	
	/**
	 * A number of albums been analyzed during current session of work. 
	 * Actually it duplicates quantity of all albums in user account, 
	 * but this field is increased each time one particular album was analyzed. 
	 */
	private static AtomicInteger processedAlbums = new AtomicInteger(0);
	
	/**
	 * A number of photos that was analyzed but was checked that they are already exists locally, 
	 * i.e. were downloaded earlier during any of previous session of work.  
	 
	 */
	private static AtomicInteger alreadyDownloadedPhotos = new AtomicInteger(0);
	
	/**
	 * A number of errors that may occur during downloading. 
	 * If error occurs, then partially downloaded photo entry will be deleted and error will registered. 
	 */
	private static AtomicInteger downloadErrors = new AtomicInteger(0);	
	
	private static AtomicInteger completedThreads = new AtomicInteger(0);
	
	public static void increaseQueuedPhotos() {
		queuedPhotos.incrementAndGet();
	}
	
	public static int getQueuedPhotos() {
		return queuedPhotos.get();
	}
	
	public static void increaseCompletedPhotos() {
		completedPhotos.incrementAndGet();
	}
	
	public static int getCompletedPhotos() {
		return completedPhotos.get();
	}

	public static void increaseProcessedAlbums() {
		processedAlbums.incrementAndGet();
	}
	
	public static int getProcessedAlbums() {
		return processedAlbums.get();
	}
	
	public static void increaseAlreadyDownloadedPhotos() {
		alreadyDownloadedPhotos.incrementAndGet();
	}
	
	public static int getAlreadyDownloadedPhotos() {
		return alreadyDownloadedPhotos.get();
	}
	
	public static void increaseDownloadErrors() {
		downloadErrors.incrementAndGet();
	}
	
	public static int getDownloadErrors() {
		return downloadErrors.get();
	}
	
	public static void increaseCompletedThreads() {
		completedThreads.incrementAndGet();
	}
	
	public static int getCompletedThreads() {
		return completedThreads.get();
	}
	
	
}
