/**
 * 
 */
package org.gcube.common.homelibrary.util;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class FileSystemNameUtil {
	
	/**
	 * A list of illegal chars.
	 */
	public static final char[] FILE_NAME_ILLEGAL_CHARACTERS = {'<', '>', ':', '\"', '/', '\\', '|', '?', '*', '\n', '\r', '\t', '\0', '\f', '`'};
	
	/**
	 * The default used replace char. 
	 */
	public static final char DEFAULT_REPLACE_CHAR = '_';
	
	/**
	 * A list of invalid file names.
	 */
	public static final String[] INVALID_FILE_NAME = new String[]{"..","."};
	
	/**
	 * A list of invalid files names on windows O.S.
	 */
	public static final String[] WINDOWS_INVALID_FILE_NAME = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", 
		"LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
	
	/**
	 * A replace string for names.
	 * 
	 */
	public static final String REPLACE_NAME = "no-name";

	/**
	 * Clean the specified file name.
	 * @param name the name to clean.
	 * @return a cleaned name.
	 */
	public static String cleanFileName(String name)
	{
		String cleanedName = removeInvalidChars(name);
		cleanedName = replaceInvalidName(cleanedName);
		return cleanedName;
	}
	
	protected static String removeInvalidChars(String name)
	{
		//TODO is this method expensive? use regexp? or rewrite the replace code?
		for (char invalidChar:FILE_NAME_ILLEGAL_CHARACTERS) name = name.replace(invalidChar, DEFAULT_REPLACE_CHAR);
		return name;
	}
	
	protected static String replaceInvalidName(String name)
	{
		for (String invalidName:INVALID_FILE_NAME) if (name.equals(invalidName)) return REPLACE_NAME;
		for (String invalidName:WINDOWS_INVALID_FILE_NAME) if (name.equals(invalidName)) return REPLACE_NAME;
		return name;
	}
}
