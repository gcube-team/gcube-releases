package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManager {

	private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
	
	public static boolean fileCopy(String origin, String destination) {
		try {
			//create FileInputStream object for source file
			FileInputStream fin = new FileInputStream(origin);

			//create FileOutputStream object for destination file
			FileOutputStream fout = new FileOutputStream(destination);

			byte[] b = new byte[1024];
			int noOfBytes = 0;

			logger.debug("Copying file using streams. O: "+origin+" D: "+destination);

			//read bytes from source file and write to destination file
			while( (noOfBytes = fin.read(b)) != -1 )
			{
				fout.write(b, 0, noOfBytes);
			}

			logger.debug("File copied!");
			//close the streams
			fin.close();
			fout.close();                  
			return true;
		} catch (Exception e) {
			logger.error("error copying file {}",origin,e);
			return false;
		}
	}
}
