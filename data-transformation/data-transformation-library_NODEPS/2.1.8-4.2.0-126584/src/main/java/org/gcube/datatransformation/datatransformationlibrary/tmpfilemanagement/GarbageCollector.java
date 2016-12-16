package org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;

/**
 * @author Dimitris Katris, NKUA
 *
 * Class which garbage collects temporary files.
 */
public class GarbageCollector extends Thread {

	private File tmpDirectory;
	
	/**
	 * Initializes the <tt>GarbageCollector</tt>
	 * 
	 * @param tmpDirectory The directory in which the <tt>GarbageCollector</tt> will garbage collect the files. 
	 */
	public GarbageCollector(File tmpDirectory){
		this.tmpDirectory=tmpDirectory;
		this.setDaemon(true);
		this.setPriority(MIN_PRIORITY);
		log.info("Garbage collector created to collect from: "+tmpDirectory.getAbsolutePath());
		this.start();
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		Thread.currentThread().setName("DTS Garbage Collector");
		while(true){
			try {
				log.trace("Garbage collector sleeping for "+ reGCollectInterval/1000 +" secs");
				Thread.sleep(reGCollectInterval);
				log.trace("Garbage collector starts checking for files to delete in "+tmpDirectory.getAbsolutePath());
				collect();
			} catch (Exception e) {
				log.error("Exception at garbage collecting ", e);
			}
		}
	}
	
	protected void forceTempFilesDeletion(){
		File [] subdirectories = tmpDirectory.listFiles();
		if(subdirectories==null){
			log.warn("Temporary directory does not exist");
			return;
		}
		if(subdirectories.length==0){
			log.trace("Temporary directory does not contain any files or subdirectories");
			return;
		}
		for(File subdir: subdirectories){
			try {
				if(subdir.isDirectory()){
					File [] sudirfiles = subdir.listFiles();
					for(File file: sudirfiles){
						if(file.delete()){
							log.trace("File "+file.getAbsolutePath()+" deleted successfully");
						}else{
							log.error("File "+file.getAbsolutePath()+" could not be deleted");
						}
					}
				}
				if(subdir.delete()){
					log.trace("File "+subdir.getAbsolutePath()+" deleted successfully");
				}else{
					log.error("File "+subdir.getAbsolutePath()+" could not be deleted");
				}
			} catch (Exception e) {
				log.error("Unexpected error in trying to delete files in dts temporary directory", e);
			}
		}
	}
	
	private void collect(){
		File [] subdirectories = tmpDirectory.listFiles();
		if(subdirectories==null){
			log.warn("Temporary directory does not exist");
			return;
		}
		if(subdirectories.length==0){
			log.trace("Temporary directory does not contain any files or subdirectories");
			return;
		}
		for(File subdir: subdirectories){
			try {
				if(subdir.isDirectory()){
					File [] sudirfiles = subdir.listFiles();
					for(File file: sudirfiles){
						if(isFileForDeletion(file)){
							if(file.delete()){
								log.trace("File "+file.getAbsolutePath()+" deleted successfully");
							}else{
								log.error("File "+file.getAbsolutePath()+" could not be deleted");
							}
						}else{
							log.trace("File "+file.getAbsolutePath()+" is not going to be deleted. (yet...)");
						}
					}
				}
				if(isFileForDeletion(subdir)){//Checking to delete sub-directories and also any other files may exist there... 
					if(subdir.delete()){
						log.trace("File "+subdir.getAbsolutePath()+" deleted successfully");
					}else{
						log.error("File "+subdir.getAbsolutePath()+" could not be deleted");
					}
				}else{
					log.trace("File "+subdir.getAbsolutePath()+" is not going to be deleted. (yet...)");
				}
			} catch (Exception e) {
				log.error("Unexpected error in trying to delete files in dts temporary directory", e);
			}
		}
	}
	
	private boolean isFileForDeletion(File file){
		return file.lastModified() < System.currentTimeMillis() - filelifetime;
	}
	
	private static Logger log = LoggerFactory.getLogger(GarbageCollector.class);

	private static long filelifetime = PropertiesManager.getInMillisPropertyValue("gcollector.filelifetime", "1800");//30 mins
	
	/**
	 * Sets the lifetime of each temporary file.
	 * 
	 * @param filelifetime The lifetime of each temporary file.
	 */
	public static void configGCFileLifetime(long filelifetime) {
		GarbageCollector.filelifetime = filelifetime;
	}

	/**
	 * Sets the interval in which the <tt>GarbageCollector</tt> searches for files to delete.
	 * 
	 * @param reGCollectInterval The interval in which the <tt>GarbageCollector</tt> searches for files to delete.
	 */
	public static void configReGCollectInterval(long reGCollectInterval) {
		GarbageCollector.reGCollectInterval = reGCollectInterval;
	}

	private static long reGCollectInterval = PropertiesManager.getInMillisPropertyValue("gcollector.gcollectinterval", "600");//10 mins
}
