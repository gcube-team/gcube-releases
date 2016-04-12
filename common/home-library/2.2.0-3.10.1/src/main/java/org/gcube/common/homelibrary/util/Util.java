/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class Util {
	
	protected static File tmpMagic;
	protected static File tmpGlobs;
	

	/**
	 * Clean and delete the specified dir.
	 * @param dir the target dir.
	 */
	public static void cleanDir(File dir)
	{
		for (File child:dir.listFiles()){
			
			if (child.isDirectory()) cleanDir(child);
			else child.delete();
		}
		
		dir.delete();
	}
	
	/**
	 * Read the entire input stream as string. The system encoding is used.
	 * @param is the input stream.
	 * @return the read string.
	 * @throws java.io.IOException if an error occurs.
	 */
	public static String readStreamAsString(InputStream is) throws java.io.IOException{
		StringBuilder sb = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			sb.append(buf, 0, numRead);
		}
		reader.close();
		return sb.toString();
	}

}
