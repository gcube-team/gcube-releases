package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;


/**
 * 
 * Detects if a file is part of the gCore distribution package
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class GCOREFileList {

	static Set<String> files = new HashSet<String>();
	static GCUBELog logger = new GCUBELog(GCOREFileList.class);
	
	static {		
		File gcorefile = GHNContext.getContext().getFile("gcore-filelist.txt",false);
		
		try {
			logger.debug("Load gcore-filelist from: "+gcorefile.getAbsolutePath());
			FileInputStream fstream = new FileInputStream(gcorefile);

		    // Convert our input stream to a BufferedReader
		    BufferedReader in = new BufferedReader (new InputStreamReader(fstream));

		    // Continue to read lines while there are still some left to read
		    String inputLine;
		    StringBuilder content = new StringBuilder();
//Parsing file using comma
//		    while ((inputLine = in.readLine()) != null )
//		    	content.append(inputLine);
//		    
//			String[] templist = content.toString().split(",");
//			for (String file : templist){ 
//				logger.debug("added file: "+file.trim()+" on gcore file list");
//				files.add(file.trim());
//			}
//Parsing file using
		    while ((inputLine = in.readLine()) != null )
		    	files.add(inputLine.trim());
// End parsing		    
			logger.debug("gCore file list correctly loaded");
			in.close();
		} catch (IOException e) {
			logger.error("Unable to read the gCore file list", e);			
		}
		
		
	}
		
	static boolean isAgCoreFile(String file) {
		if (files.isEmpty())
			logger.warn("the gCore file list is empty");
		if (files.contains(file.trim()))
			return true;
		return false;
	}		
	
}
