package org.gcube.datatransformation.datatransformationlibrary.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Utility class that facilitates local file management.
 */
public class FilesUtils {
	
	private static Logger log = LoggerFactory.getLogger(FilesUtils.class);
	
	/**
	 * Stores an <tt>InputStream</tt> to a local file.
	 * 
	 * @param instream The <tt>InputStream</tt> which will be persisted. 
	 * @param filename The name of the file in which the stream will be persisted. 
	 * @throws Exception If the stream could not be persisted.
	 */
	public static void streamToFile(InputStream instream, String filename) throws Exception{
		OutputStream out=null;
		try{
			out=new FileOutputStream(new File(filename));
			byte[] buf = new byte[4096];
			int len;
			while ((len = instream.read(buf)) >= 0) {
				out.write(buf, 0, len);
			}
			instream.close();
			instream=null;
			out.close();
			out=null;
		}catch(Exception e){
			if(instream!=null) instream.close();
			if(out!=null) out.close();
			log.error("Could not persist stream. Throwing Exception",e);
			throw new Exception("Could not persist stream",e);
		}
	}
}
