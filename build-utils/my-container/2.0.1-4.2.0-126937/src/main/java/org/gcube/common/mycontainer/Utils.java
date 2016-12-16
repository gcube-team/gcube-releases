/**
 * 
 */
package org.gcube.common.mycontainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * API-wide utilities and constants.
 *  
 * @author Fabio Simeoni
 *
 */
public class Utils {

	private static Logger logger =  Logger.getLogger(Utils.class);
	
	/** The environment variable that Globus uses to identifies the install location of the container */
	static final String GLOBUS_LOCATION = "GLOBUS_LOCATION";
	
	/** The system property that identifies the install location of the container. */
	public static final String CONTAINER_LOCATION_PROPERTY = "my-container.location";
	
	public static final String DEFAULT_INSTALL_DIRECTORY = "my-container";
	public static final String PROPERTY_FILE = "my-container.properties";
	
	/** The name of the property for the timeout on startup*/
	public static final String STARTUP_TIMEOUT_PROPERTY = "my-container.timeout";
	/** The default timeout on startup.*/
	public static final int DEFAULT_STARTUP_TIMEOUT = 15000;

	/** The local name of the configuration folder inside the install location of the container. */
	public static final String CONFIG_DIR = "config";

	/** The local name of the deployment folder inside the install location of the container. */
	public static final String ETC_DIR = "etc";
	
	/** The local name of the library folder inside the install location of the container. */
	public static final String LIB_DIR = "lib";
	
	/** The local name of the storage folder inside the install location of the container. */
	public static final String STORAGE_DIR = "store";
	
	/** The name of the property for the timeout on startup*/
	public static final String PORT_PROPERTY = "port";
	/** The default timeout on startup.*/
	public static final int DEFAULT_PORT = 9999;
	
	static final String STORAGE_LOCATION_PROPERTY = "storage.root";
	
	
	/**
	 * Returns a singleton {@link Properties} with the {@link Utils#CONTAINER_LOCATION_PROPERTY} set to the installation of a container found on
	 * the local file system.
	 * 
	 * <p>
	 * The search for the install location follows this strategy:
	 * <ul>
	 * <li> the value of {@link #CONTAINER_LOCATION_PROPERTY} system property.
	 * <li> a {@link #DEFAULT_INSTALL_DIRECTORY} in the working directory.
	 * <li> a {@link #DEFAULT_INSTALL_DIRECTORY} in the parent directory of the working directory.
	 * <li> a {@link #DEFAULT_INSTALL_DIRECTORY} in the user's home.
	 * </ul>
	 * 
	 * @return a singleton {@link Properties} with the {@link Utils#CONTAINER_LOCATION_PROPERTY} set to the installation of a container.
	 */
	public static Properties findContainerLocation() {
		
		logger.trace("locating installation of globus container...");
		
		File location = null;
		
		String path = System.getProperty(CONTAINER_LOCATION_PROPERTY);
		
		if (path!=null)
			location = new File(path);
		else
			location = new File(DEFAULT_INSTALL_DIRECTORY); //current location
		
		if (!location.exists()) 
			location = new File(new File("").getAbsoluteFile().getParentFile(),DEFAULT_INSTALL_DIRECTORY); //parent location
		
		
		if (!location.exists())
			location = new File(System.getProperty("user.home"),DEFAULT_INSTALL_DIRECTORY); //parent location	
		
		if (!location.exists())
			throw new RuntimeException("could not find install location");
		
		Properties ps = new Properties();
		ps.setProperty(CONTAINER_LOCATION_PROPERTY, location.getAbsolutePath());
		
		return ps;
		
	}
	
	/**
	 * Returns the {@link Properties} in a {@link #PROPERTY_FILE}.
	 * 
	 * <p>
	 * 
	 * The search for the {@link #PROPERTY_FILE} considers the following locations, in the order in which they are listed:
	 * 
	 * </ul>
	 * <li> on the classpath.
	 * <li> in the working directory.
	 * <li> in the user's home.
	 * </ul>
	 * 
	 * @return the properties
	 * @throws IllegalStateException if {@link #PROPERTY_FILE} cannot be found 
	 */
	public static Properties findContainerProperties() throws IllegalStateException {
		
		logger.trace("locating "+PROPERTY_FILE+" to configure globus container");
		
		//look on classpath first
		InputStream stream = Utils.class.getResourceAsStream("/"+PROPERTY_FILE);
		
		//check on file system next
		if (stream == null) {
			
			//current directory first
			File file = new File(PROPERTY_FILE);
			
			//user home next
			if (!file.exists())
				file = new File(System.getProperty("user.home"),PROPERTY_FILE);
			
			if (file.exists())
				try {
					stream = new FileInputStream(file);
				}
				catch(Exception swallow) {}
	
		}
		
		if (stream==null)
			throw new IllegalStateException("could not find "+PROPERTY_FILE);
			
		Properties ps = new Properties();
		try{
			ps.load(stream);
		}
		catch(IOException e) {
			throw new RuntimeException("could not load container configuration",e);
		}
		
		return ps;
	}

	/**
	 * Checks the existence and usability of given directory
	 * @param dir the directory
	 * @throws IllegalArgumentException if the directory does not exist or is not readable or writable.
	 */
	static void checkFile(File file) throws IllegalArgumentException  {
		if (file==null || !file.exists() || file.isDirectory() || !file.canRead())
			throw new IllegalArgumentException(file + " is invalid, does not exist, is not a directory, or is not readable");
	}
	
	/**
	 * Checks the existence and usability of given directory
	 * @param dir the directory
	 * @throws IllegalArgumentException if the directory does not exist or is not readable or writable.
	 */
	static void checkDir(File dir) throws IllegalArgumentException  {
		if (dir==null || !dir.exists() || !dir.isDirectory() || !dir.canRead() || !dir.canWrite())
			throw new IllegalArgumentException(dir + " is invalid, does not exist, is not a directory, or is not readable");
	}
	
	//helper
	static long lastModified(File file) {
		
		if (file.isDirectory()) {
			long max=0L;
			for (File inner : file.listFiles())
				max = Math.max(lastModified(inner),max); 
			return max;
		}
		else 
			return file.lastModified();
		
	}
	
	static File tempDir() throws IOException {
		File tmpDir = File.createTempFile("tmplibs",null);
		tmpDir.delete();
		if (!tmpDir.mkdirs())
			throw new IOException("cannot create temporary dir "+tmpDir);
		return tmpDir;
	}
}
