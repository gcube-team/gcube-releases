package org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Utility class which facilitates temporary file management. Each temporary file created under the <tt>DTSTMPDIR</tt> is garbage collected by a {@link GarbageCollector} instance.
 */
public class TempFileManager {

	/**
	 * The root directory under which the <tt>TempFileManager</tt> stores files.  
	 */
	public static final String DTSTMPDIR;
	/**
	 * The file extention of DTS temporary files.
	 */
	public static final String DTSExtention=".dts";
	/**
	 * Boolean that denotes if the <tt>TempFileManager</tt> runs under windows OS. 
	 */
	public static boolean win;
	private static Logger log = LoggerFactory.getLogger(TempFileManager.class);
	
	static {
		String tmpDIR = System.getProperty("java.io.tmpdir");
		if(tmpDIR!=null && tmpDIR.trim().length()>0){
			DTSTMPDIR = tmpDIR+File.separator+"dts"+File.separator;
		} else {
			DTSTMPDIR="/tmp/dts/";
		}
		log.debug("Temporary file is "+DTSTMPDIR);
		win = File.separator.equals("\\");
		if(win){
			log.debug("Running in windows");
		}
		File tmpDTSDIRF = new File(DTSTMPDIR);
		if(!tmpDTSDIRF.exists()){
			if(!tmpDTSDIRF.mkdirs()){
				log.error("Did not manage to create DTS's temporary directory");
			}
		}
		dtsGarbageCollector = new GarbageCollector(tmpDTSDIRF);
	}
	private static GarbageCollector dtsGarbageCollector;
	/**
	 * <p>Generates a file name under the <tt>DTSTMPDIR/tmpSubDir</tt> directory.</p>
	 * <p>If <tt>tmpSubDir</tt> is null then this method creates a new one.</p>
	 * 
	 * @param tmpSubDir The sub-directory name.
	 * @return The temporary file name.
	 * @throws Exception If <tt>tmpSubDir</tt> is null and there was a failure at creating a random sub-directory.
	 */
	public static String generateTempFileName(String tmpSubDir) throws Exception {
		if(tmpSubDir==null){
			tmpSubDir = genarateTempSubDir();
		}
		File tmpSubDirFile = new File(tmpSubDir);
		if(!tmpSubDirFile.exists()){
			log.warn("Temp subdirectory does not exist recreating it...");
			if(!tmpSubDirFile.mkdirs()){
				throw new Exception("Temp subdirectory could not be recreated");
			}
		}
		return tmpSubDir+nextUUID()+DTSExtention;
	}
	
	/**
	 * Creates a sub-directory under the <tt>DTSTMPDIR</tt> directory.
	 * 
	 * @return The name of the new sub-directory.
	 * @throws Exception If the sub-directory could not be created.
	 */
	public static String genarateTempSubDir() throws Exception {
		try {
			File dtsTmpDir = new File(DTSTMPDIR);
			if(!dtsTmpDir.exists()){
				boolean succeeded=dtsTmpDir.mkdirs();
				if(!win){
					Runtime.getRuntime().exec("chmod 0777 "+dtsTmpDir.getAbsolutePath());
				}
				if(!succeeded){
					log.error("Could not create DTS TMP Directory "+DTSTMPDIR+" Throwing Exception");
					throw new Exception("Could not create DTS TMP Directory "+DTSTMPDIR);
				}
			}
			String subDirName = nextUUID();

			File subDirFile = new File(DTSTMPDIR+subDirName);
			if(!subDirFile.exists()){
				boolean succeeded=subDirFile.mkdirs();
				if(!win){
					Runtime.getRuntime().exec("chmod 0777 "+subDirFile.getAbsolutePath());
				}
				if(!succeeded){
					if(!subDirFile.exists()){
						log.error("Could not create subase directory "+DTSTMPDIR+subDirName+" Throwing Exception");
						throw new Exception("Could not create subase directory "+DTSTMPDIR+subDirName);
					}
				}
			}
			
			return DTSTMPDIR+subDirName+File.separatorChar;
		}catch(Exception e){
			log.error("Could not create temporary sub directory in "+DTSTMPDIR+" Throwing Exception", e);
			throw new Exception("Could not create temporary sub directory in "+DTSTMPDIR);
		}
	}
	
	/**
	 * Deletes all files contained under the <tt>DTSTMPDIR</tt>.  
	 */
	public static void clearTemporaryDirectory(){
		dtsGarbageCollector.forceTempFilesDeletion();
	}
	
//	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	private static String nextUUID(){
		return UUID.randomUUID().toString();
	}

}
