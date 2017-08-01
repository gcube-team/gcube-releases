/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 8, 2015
 */
public class ReadFile {

	public static String read(File file){
		StringBuilder builder = new StringBuilder();
		
		try (FileInputStream fis = new FileInputStream(file)) {
			System.out.println("Total file size to read (in bytes) : "+ fis.available());
			int content;
			while ((content = fis.read()) != -1) {
				// convert to char and display it
//				System.out.print((char) content);
				builder.append((char) content);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
}
