package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class FileManager {
		
	private static Logger logger = LoggerFactory.getLogger(FileManager.class);
	
	private static final String TMP_FOLDER = "tmp/";
	private static final String PERSISTENCE_FOLDER = "persistence/";
	
	private String tmpFolder;
	private String persistenceFolder;
	
	private String initializeFolder(String persistencePath) {
		
		File f=new File(persistencePath);
		if(!f.exists()){
			logger.debug("Creating persistence folder "+persistencePath);
			f.mkdirs();
			try {
				Process proc=Runtime.getRuntime().exec("chmod -R 777 "+persistencePath);
				try{
					proc.waitFor();
				}catch(InterruptedException e){
					int exitValue=proc.exitValue();
					logger.debug("Permission execution exit value = "+exitValue);
				}
			} catch (IOException e) {
				logger.warn("Unexpected Exception", e);
			}
		}
		return persistencePath;
	}
	
	public FileManager(String persistenceFolder){	
		this.tmpFolder = persistenceFolder +
				File.separator + TMP_FOLDER;
		initializeFolder(this.tmpFolder);
		
		this.persistenceFolder = persistenceFolder +
				File.separator + PERSISTENCE_FOLDER;
		initializeFolder(this.persistenceFolder);
	}
	
	public  String serializeObject(Object object, String fileExtension) 
	throws SMFileManagerException {
		
		String fileName = UUID.randomUUID().toString() + fileExtension; 
		String filePath =  tmpFolder + fileName; 
		
		FileOutputStream fos = null;
		
		try{
			fos = new FileOutputStream (filePath);
			XStream xstream = new XStream();
			xstream.toXML(object, fos);
		}
		catch (Exception e){
			throw new SMFileManagerException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}
		
		return filePath;	
	}
	
	public String createFile(InputStream is) throws SMFileManagerException {
		try {

			String fileName = UUID.randomUUID().toString();
			// write the inputStream to a FileOutputStream
			File file = new File(this.tmpFolder + File.separator + fileName);
			OutputStream out = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			is.close();
			out.flush();
			out.close();

			return file.getAbsolutePath();
		} catch (IOException e) {
			throw new SMFileManagerException(e.getMessage());
		}
		
	}
	
}
