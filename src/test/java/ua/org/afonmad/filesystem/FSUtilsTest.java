package ua.org.afonmad.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import ua.org.afonmad.utils.FSUtils;

public class FSUtilsTest {

	private static final String NORMAL_FNAME = "a file without strange symbols";
	private static final String FIXED_FNAME = "a file with ... )";
	private static final String WRONG_NAME = "a file\\ with .*.. :)";
	private static final String WRONG_NAME_W_DOTS = "a file with ...";
	private static final String FIXED_NAME_W_DOTS = "a file with";
	
	@Test
	public void testValidateWrongDir() throws Exception {
		assertFalse(FSUtils.validateDirName(WRONG_NAME));
	}
	
	@Test
	public void testFixWrongDir() throws Exception {
		assertEquals(FIXED_FNAME, FSUtils.fixDirName(WRONG_NAME));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + FIXED_FNAME).exists());
	}
	
	@Test
	public void testValidateFileAndDirWithEndingDots() throws Exception {
		assertFalse(FSUtils.validateDirName(WRONG_NAME_W_DOTS));
		assertFalse(FSUtils.validateFileName(WRONG_NAME_W_DOTS));
	}
	
	@Test
	public void testFixFileAndDirWithEndingDots() throws Exception {
		assertEquals(FIXED_NAME_W_DOTS, FSUtils.fixFileName(WRONG_NAME_W_DOTS));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + FIXED_NAME_W_DOTS).exists());
		
		assertEquals(FIXED_NAME_W_DOTS, FSUtils.fixDirName(WRONG_NAME_W_DOTS));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + FIXED_NAME_W_DOTS).exists());
	}
	
	@Test
	public void testValidateWrongFile() throws Exception {
		assertFalse(FSUtils.validateFileName(WRONG_NAME));
	}
	
	@Test
	public void testFixWrongFile() throws Exception {
		assertEquals(FIXED_FNAME, FSUtils.fixFileName(WRONG_NAME));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + FIXED_FNAME).exists());
	}
	
	@Test
	public void testGoodDir() throws Exception {
		assertTrue(FSUtils.validateDirName(NORMAL_FNAME));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + NORMAL_FNAME).exists());
	}

	@Test
	public void testGoodFile() throws Exception {
		assertTrue(FSUtils.validateFileName(NORMAL_FNAME));
		assertFalse(new File(FileUtils.getTempDirectoryPath() + File.separator + NORMAL_FNAME).exists());
	}
	
	
}
