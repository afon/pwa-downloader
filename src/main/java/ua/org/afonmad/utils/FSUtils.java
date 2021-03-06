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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

/**
 * Support class for handling different work with file system. 
 * Mostly used for checking and fixing dir/file names of albums/photos before placing them on local hdd.  
 */
public class FSUtils {

	private static final String ILLEGAL_FILENAME_SYMBOLS_REGEX = "[\\\\/:*?\"<>|]";

	public static boolean validateDirName(String dir) {
		if (dir.endsWith(".")) {
			return false;
		}
		
		File testDir = new File(getTempDirPath() + File.separator + dir);
		if (!testDir.mkdir()) {
			return false;
		}
		
		testDir.delete();
		return true;
	}

	public static String fixDirName(String dirName) throws Exception {
		String fixedDirName = dirName.replaceAll(ILLEGAL_FILENAME_SYMBOLS_REGEX, "");
		while(fixedDirName.endsWith(".")) {
			fixedDirName = fixedDirName.substring(0, fixedDirName.length() - 1);
		}
		if (fixedDirName.trim().isEmpty()) {
			throw new IllegalArgumentException("Cant create dir [" + dirName + "] even with replaced symbols [" + fixedDirName + "]");
		}
		fixedDirName = fixedDirName.trim();
		
		File fixedDir = new File(getTempDirPath() + File.separator + fixedDirName);
		if (!fixedDir.mkdir()) {
			InformationCounter.increaseDownloadErrors();
			throw new IllegalArgumentException("Cant create dir [" + dirName + "] even with replaced symbols [" + fixedDirName + "]");
		}
		
		fixedDir.delete();
		return fixedDirName;
	}
	
	public static boolean validateFileName(String fileName) {
		if (fileName.endsWith(".") 
			|| !fileName.trim().equals(fileName)
			|| !fileName.replaceAll(ILLEGAL_FILENAME_SYMBOLS_REGEX, "").equals(fileName)) {
			return false;
		}
		
		File testFile = new File(getTempDirPath() + File.separator + fileName);
		try {
			if (!testFile.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		
		testFile.delete();
		return true;
	}
	
	public static String fixFileName(String fileName) {
		String fixedFileName = fileName.replaceAll(ILLEGAL_FILENAME_SYMBOLS_REGEX, "");
		while(fixedFileName.endsWith(".")) {
			fixedFileName = fixedFileName.substring(0, fixedFileName.length() - 1);
		}
		if (fixedFileName.trim().isEmpty()) {
			throw new IllegalArgumentException("Cant create file [" + fileName + "] even with replaced symbols [" + fixedFileName + "]");
		}
		fixedFileName = fixedFileName.trim();		
		
		File fixedFile = new File(getTempDirPath() + File.separator + fixedFileName);
		try {
			if (!fixedFile.createNewFile()) {
				throw new IllegalArgumentException("Cant create file [" + fileName + "] even with replaced symbols [" + fixedFileName + "]");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Cant create file [" + fileName + "] even with replaced symbols [" + fixedFileName + "]", e);
		}
		
		fixedFile.delete();
		return fixedFileName;
	}
	
	public static File createValidatedOutputDirectory(String dirPath) throws Exception {
		File outputDir = new File(dirPath);
		if (!outputDir.exists()) {
			if (!outputDir.mkdirs()) {
				throw new IllegalArgumentException("Can't create output directory [" + dirPath + "]. Check name symbols and write permissions");
			}
		}
		if (!canWrite(outputDir)) {
			throw new IllegalArgumentException("No write permissions for output directory [" + dirPath + "]");
		}
		
		return outputDir;
		
	}

	private static boolean canWrite(File outputDir) {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("pwad-", ".tmp", outputDir);
		} catch (IOException e) {
			return false;
		}
		tmpFile.delete();
		return true;
	}

	public static void cleanOutputDirectory(File outputDirectory) {
		FilenameFilter logFilesFilter = new FilenameFilter() {
			@Override
			public boolean accept(File parentDir, String fileName) {
				if (fileName.endsWith("log") && new File(parentDir, fileName).isFile()) {
					return true;
				}
				return false;
			}
		};
		
		String[] previousLogs = outputDirectory.list(logFilesFilter);
		
		for (String prevLog : previousLogs) {
			new File(outputDirectory, prevLog).delete();
		}
	}
	
	public static String getTempDirPath() {
		String tmpDirPath = FileUtils.getTempDirectoryPath() + "pwad-" + UUID.randomUUID();
		File tmpDir = new File(tmpDirPath);
		tmpDir.mkdirs();
		tmpDir.deleteOnExit();
		return tmpDirPath;
	}

}
