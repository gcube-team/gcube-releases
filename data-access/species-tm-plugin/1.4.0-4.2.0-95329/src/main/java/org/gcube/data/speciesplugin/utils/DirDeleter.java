/**
 * 
 */
package org.gcube.data.speciesplugin.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DirDeleter implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(DirDeleter.class);	
	
	protected File directory;

	/**
	 * @param directory
	 */
	public DirDeleter(File directory) {
		this.directory = directory;
	}

	@Override
	public void run() {
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			log.warn("Delete of directory {} failed", directory.getAbsolutePath());
		}		
	}

}
