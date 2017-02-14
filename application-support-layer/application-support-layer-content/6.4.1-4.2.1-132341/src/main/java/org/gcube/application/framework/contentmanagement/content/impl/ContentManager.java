package org.gcube.application.framework.contentmanagement.content.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentManager {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ContentManager.class);
	
	protected static Thread thread = new CleanContentThread();
	protected static ContentManager contentManager = new ContentManager();
	protected InputStream rafile = null;
	
	
	protected ContentManager() {
		thread.setDaemon(true);
		thread.start();
	}
	
	public static ContentManager getInstance() {
		return contentManager;
	}
	
	public String getObject(DigitalObject d_o, String elementType) throws IllegalStateException, IOException {
		return d_o.getContent();
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		thread.interrupt();
		logger.info(new Date(System.currentTimeMillis()) + " clean thread was interrupted");
		thread.join();
		logger.info(new Date(System.currentTimeMillis()) + " clean thread was joint");
		super.finalize();
	}
	
	
	
	protected static class CleanContentThread extends Thread {
		public void run() 
		{
			while (true)
			{
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					logger.error("Exception:", e);
					logger.info(new Date(System.currentTimeMillis()) + " clean content thread was interrupted (in clean content thread)");
					break;
				}
				// TODO: cleanup old files 
				File dir = new File(System.getProperty("java.io.tmpdir") + "/content/");
				File[] filePaths = dir.listFiles();
				HashMap<File, Long> filesMap = new HashMap<File, Long>();
				HashMap<File, Long> tempMap = new HashMap<File, Long>();
				logger.info("In content thread size: " + filePaths.length);
				
				for (int i = 0; i < filePaths.length; i++) {
					filesMap.put(filePaths[i], new Long(filePaths[i].lastModified()));
				}
				
				// Sort the files based on the last modified date
				
				for (File tmpFile:filesMap.keySet()) {
					tempMap.put(tmpFile, filesMap.get(tmpFile));
				}
				
				List<File> mapKeys = new ArrayList<File>(tempMap.keySet());
				List<Long> mapValues = new ArrayList<Long>(tempMap.values());
				HashMap<File, Long> sortedMap = new LinkedHashMap<File, Long>();
				TreeSet<Long> sortedSet = new TreeSet<Long>(mapValues);
				Object[] sortedArray = sortedSet.toArray();
				int size = sortedArray.length;
				for (int i = 0; i <size; i++) {
					sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), (Long)sortedArray[i]);
				}
				
				
				// Delete 10 older files
				int k = 0;
				mapKeys = new ArrayList<File>(sortedMap.keySet());
				for (int i = 0; i < mapKeys.size(); i++) {
					if (k < 10) {
						logger.info("Deleting file: " + mapKeys.get(i));
						mapKeys.get(i).delete();
					}
					else 
						break;
					k++;
				}
				
			}
		}
	}
	
	
	
	
/*	protected static class CleanSessionThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				try {
					Thread.sleep(600000);
				} catch (InterruptedException e) {
					logger.error("Exception:", e);
					logger.info(new Date(System.currentTimeMillis()) + " clean thread was interrupted (in clean thread)");
					break;
				}
				//TODO: cleanup invalid sessions: add locks...
				Set<String> keys = sessionManager.sessions.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext())
				{
					String extSessionID = iter.next();
					if(!sessionManager.sessions.get(extSessionID).isValid())
					{
						sessionManager.sessions.remove(extSessionID);
					}
				}
			}
			logger.info(new Date(System.currentTimeMillis()) + " clean thread was terminated");
		}

	}
} */

}
