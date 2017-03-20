/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.service.dao.DaoManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 30, 2013
 * 
 */
public class DatabaseManagerFile {

	public static final String SUFFIXONE = ".h2.db";
	public static final String SUFFIXTWO = ".trace.db";
	
	public static Logger logger = Logger.getLogger(DatabaseManagerFile.class);

	public static void deleteFile(final String sessionId, final String scope) {

		new Thread(){
			@Override
			public void run() {
				
				// Delete if tempFile (database) exists

				String tempDb = DaoManager.getStingConnectionToDatabase(sessionId, scope);

				logger.trace("Tentative deleting...: "+tempDb);
		
				File fileTemp = new File(tempDb+SUFFIXONE);

				boolean result = deleteFileIfExists(fileTemp);
				
				//TODO COMMENT THIS IN PRODUCTION
//				writeStringToFile(fileTemp+" was deleted: "+result);
				
				logger.trace(fileTemp+" was deleted: "+result);
				
				fileTemp = new File(tempDb+SUFFIXTWO);

				deleteFileIfExists(fileTemp);
				
				
				//TODO COMMENT THIS IN PRODUCTION
//				writeStringToFile(fileTemp+" was deleted: "+result);
				
				logger.trace(fileTemp+" was deleted: "+result);

			}
		}.start();
		
		
		

	}
	
	private static void writeStringToFile(String line){
		
		try {
			File temp = new File("/tmp/testdelete/tracedelete");
			FileUtils.writeStringToFile(temp, line, "UTF-8");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean deleteFileIfExists(File file) {

		if (file.exists()) {
			return file.delete();
		}
		return false;
	}
	
	public static void main(String[] args) {
		String sessionId = "9igj7p2evicu"; 
		String scope = Constants.defaultScope;
		DatabaseManagerFile.deleteFile(sessionId, scope);
	}

}
