package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class FileManager {

	public static boolean FileCopy(String origin, String destination) {
		try {
	                    //create FileInputStream object for source file
                        FileInputStream fin = new FileInputStream(origin);
                       
                        //create FileOutputStream object for destination file
                        FileOutputStream fout = new FileOutputStream(destination);
                       
                        byte[] b = new byte[1024];
                        int noOfBytes = 0;
                       
                        AnalysisLogger.getLogger().debug("Copying file using streams. O: "+origin+" D: "+destination);
                       
                        //read bytes from source file and write to destination file
                        while( (noOfBytes = fin.read(b)) != -1 )
                        {
                                fout.write(b, 0, noOfBytes);
                        }
                       
                        AnalysisLogger.getLogger().debug("File copied!");
                        //close the streams
                        fin.close();
                        fout.close();                  
                        return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
