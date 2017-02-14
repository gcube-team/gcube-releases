/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class DatabaseEnvironment {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseIntializator.class);
	
	protected static final String PROPERTY_FILENAME = "config.properties"; 

	protected static final String HOST_VARNAME = "HOST";
	
	protected static final String REMOTE_PROTOCOL;
	protected static final String REMOTE_PROTOCOL_VARNAME = "REMOTE_PROTOCOL";
	
	protected static final String HTTP_PROTOCOL;
	protected static final String HTTP_PROTOCOL_VARNAME = "HTTP_PROTOCOL";
	
	protected static final String HTTP_PORT;
	protected static final String HTTP_PORT_VARNAME = "HTTP_PORT";
	
	public static final String DB;
	protected static final String DB_VARNAME = "DB";
	
	protected static final String USERNAME;
	protected static final String USERNAME_VARNAME = "USERNAME";
	
	protected static final String PASSWORD;
	protected static final String PASSWORD_VARNAME = "PASSWORD";
	
	public static final String CHANGED_ADMIN_PASSWORD;
	protected static final String CHANGED_ADMIN_PASSWORD_VARNAME = "CHANGED_ADMIN_PASSWORD";
	
	protected static final String DEFAULT_CREATED_WRITER_USER_PASSWORD;
	protected static final String DEFAULT_CREATED_WRITER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_WRITER_USER_PASSWORD";
	
	protected static final String DEFAULT_CREATED_READER_USER_PASSWORD;
	protected static final String DEFAULT_CREATED_READER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_READER_USER_PASSWORD";
	
	public static final String DEFAULT_ADMIN_USERNAME;
	protected static final String DEFAULT_ADMIN_USERNAME_VARNAME = "DEFAULT_ADMIN_USERNAME";
	
	protected static final String DEFAULT_ADMIN_PASSWORD;
	protected static final String DEFAULT_ADMIN_PASSWORD_VARNAME = "DEFAULT_ADMIN_PASSWORD";
	
	public static final Map<PermissionMode, String> DEFAULT_PASSWORDS;
	
	
	protected static final String HOSTS;
	protected static final String[] HOST_ARRAY;
	
	public static final String SERVER_URI;
	public static final String DB_URI;
	
	public static final String[] HTTP_URL_STRINGS;
	
	
	static {
		Properties properties = new Properties();
		InputStream input = null;
		
		try {

			input = DatabaseEnvironment.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);

			// load a properties file
			properties.load(input);
			
			HOSTS = properties.getProperty(HOST_VARNAME);
			
			REMOTE_PROTOCOL = properties.getProperty(REMOTE_PROTOCOL_VARNAME);
			
			HTTP_PROTOCOL = properties.getProperty(HTTP_PROTOCOL_VARNAME);
			HTTP_PORT = properties.getProperty(HTTP_PORT_VARNAME);
			
			DB = properties.getProperty(DB_VARNAME);
			SERVER_URI = REMOTE_PROTOCOL + HOSTS;
			DB_URI = SERVER_URI + "/" + DB;
			
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
			
			
			HOST_ARRAY = HOSTS.split(";");
			HTTP_URL_STRINGS = new String[HOST_ARRAY.length];
			
			for(int i=0; i<HOST_ARRAY.length; i++){
				HTTP_URL_STRINGS[i] = HTTP_PROTOCOL + HOST_ARRAY[i] + HTTP_PORT;
			}
			
		} catch(Exception e){
			logger.error("Unable to load properties from {}", PROPERTY_FILENAME);
			throw new RuntimeException("Unable to load properties", e);
		}
	}
	
}
