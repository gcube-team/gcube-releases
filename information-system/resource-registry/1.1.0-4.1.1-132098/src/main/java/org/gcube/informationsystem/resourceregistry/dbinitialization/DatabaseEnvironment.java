/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class DatabaseEnvironment {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseIntializator.class);
	
	public static final String PROPERTY_FILENAME = "config.properties"; 

	public static final String HOST;
	public static final String HOST_VARNAME = "HOST";
	
	public static final String REMOTE_PROTOCOL;
	public static final String REMOTE_PROTOCOL_VARNAME = "REMOTE_PROTOCOL";
	
	public static final String REMOTE_URI;
	
	public static final String HTTP_PROTOCOL;
	public static final String HTTP_PROTOCOL_VARNAME = "HTTP_PROTOCOL";
	
	public static final String HTTP_PORT;
	public static final String HTTP_PORT_VARNAME = "HTTP_PORT";
	
	public static final String HTTP_URL_STRING;
	
	public static final String DB;
	public static final String DB_VARNAME = "DB";
	
	public static final String URI_DB;
	
	public static final String USERNAME;
	public static final String USERNAME_VARNAME = "USERNAME";
	
	public static final String PASSWORD;
	public static final String PASSWORD_VARNAME = "PASSWORD";
	
	public static final String CHANGED_ADMIN_PASSWORD;
	public static final String CHANGED_ADMIN_PASSWORD_VARNAME = "CHANGED_ADMIN_PASSWORD";
	
	private static final String DEFAULT_CREATED_WRITER_USER_PASSWORD;
	private static final String DEFAULT_CREATED_WRITER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_WRITER_USER_PASSWORD";
	
	private static final String DEFAULT_CREATED_READER_USER_PASSWORD;
	private static final String DEFAULT_CREATED_READER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_READER_USER_PASSWORD";
	
	public static final String DEFAULT_ADMIN_USERNAME;
	public static final String DEFAULT_ADMIN_USERNAME_VARNAME = "DEFAULT_ADMIN_USERNAME";
	
	public static final String DEFAULT_ADMIN_PASSWORD;
	public static final String DEFAULT_ADMIN_PASSWORD_VARNAME = "DEFAULT_ADMIN_PASSWORD";
	
	public static final Map<PermissionMode, String> DEFAULT_PASSWORDS;
	
	static {
		Properties properties = new Properties();
		InputStream input = null;

		
		try {

			input = DatabaseEnvironment.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);

			// load a properties file
			properties.load(input);
			
			HOST = properties.getProperty(HOST_VARNAME);
			REMOTE_PROTOCOL = properties.getProperty(REMOTE_PROTOCOL_VARNAME);
			REMOTE_URI = REMOTE_PROTOCOL + HOST;
			
			HTTP_PROTOCOL = properties.getProperty(HTTP_PROTOCOL_VARNAME);
			HTTP_PORT = properties.getProperty(HTTP_PORT_VARNAME);
			HTTP_URL_STRING = HTTP_PROTOCOL + HOST + HTTP_PORT;
			
			DB = properties.getProperty(DB_VARNAME);
			URI_DB = REMOTE_URI + "/" + DB;
			
			USERNAME = properties.getProperty(USERNAME_VARNAME);
			PASSWORD = properties.getProperty(PASSWORD_VARNAME);
			
			CHANGED_ADMIN_PASSWORD = properties.getProperty(CHANGED_ADMIN_PASSWORD_VARNAME);
			
			DEFAULT_CREATED_WRITER_USER_PASSWORD = properties.getProperty(DEFAULT_CREATED_WRITER_USER_PASSWORD_VARNAME);
			DEFAULT_CREATED_READER_USER_PASSWORD = properties.getProperty(DEFAULT_CREATED_READER_USER_PASSWORD_VARNAME);
			
			
			DEFAULT_ADMIN_USERNAME = properties.getProperty(DEFAULT_ADMIN_USERNAME_VARNAME);
			DEFAULT_ADMIN_PASSWORD = properties.getProperty(DEFAULT_ADMIN_PASSWORD_VARNAME);
			
			
			DEFAULT_PASSWORDS = new HashMap<SecurityContextMapper.PermissionMode, String>();
		
			DEFAULT_PASSWORDS.put(PermissionMode.WRITER, DEFAULT_CREATED_WRITER_USER_PASSWORD);
			DEFAULT_PASSWORDS.put(PermissionMode.READER, DEFAULT_CREATED_READER_USER_PASSWORD);
		} catch(Exception e){
			logger.error("Unable to load properties from {}", PROPERTY_FILENAME);
			throw new RuntimeException("Unable to load properties", e);
		}
	}
	
}
