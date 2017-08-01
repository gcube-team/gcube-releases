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
	
	public static final String DB;
	protected static final String DB_VARNAME = "DB";
	
	protected static final String ROOT_USERNAME;
	protected static final String ROOT_USERNAME_VARNAME = "ROOT_USERNAME";
	
	protected static final String ROOT_PASSWORD;
	protected static final String ROOT_PASSWORD_VARNAME = "ROOT_PASSWORD";
	
	protected static final String DEFAULT_ADMIN_USERNAME;
	protected static final String DEFAULT_ADMIN_USERNAME_VARNAME = "DEFAULT_ADMIN_USERNAME";
	
	protected static final String DEFAULT_ADMIN_ROLE = "admin";
	
	public static final String CHANGED_ADMIN_USERNAME;
	protected static final String CHANGED_ADMIN_USERNAME_VARNAME = "CHANGED_ADMIN_USERNAME";
	
	protected static final String DEFAULT_ADMIN_PASSWORD;
	protected static final String DEFAULT_ADMIN_PASSWORD_VARNAME = "DEFAULT_ADMIN_PASSWORD";
	
	public static final String CHANGED_ADMIN_PASSWORD;
	protected static final String CHANGED_ADMIN_PASSWORD_VARNAME = "CHANGED_ADMIN_PASSWORD";
	
	protected static final String DEFAULT_CREATED_WRITER_USER_PASSWORD;
	protected static final String DEFAULT_CREATED_WRITER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_WRITER_USER_PASSWORD";
	
	protected static final String DEFAULT_CREATED_READER_USER_PASSWORD;
	protected static final String DEFAULT_CREATED_READER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_READER_USER_PASSWORD";
	
	public static final Map<PermissionMode, String> DEFAULT_PASSWORDS;
	
	protected static final String HOSTS;
	
	public static final String SERVER_URI;
	public static final String DB_URI;
	
	static {
		Properties properties = new Properties();
		InputStream input = null;
		
		try {

			input = DatabaseEnvironment.class.getClassLoader().getResourceAsStream(PROPERTY_FILENAME);

			// load a properties file
			properties.load(input);
			
			HOSTS = properties.getProperty(HOST_VARNAME);
			
			REMOTE_PROTOCOL = properties.getProperty(REMOTE_PROTOCOL_VARNAME);

			DB = properties.getProperty(DB_VARNAME);
			SERVER_URI = REMOTE_PROTOCOL + HOSTS;
			DB_URI = SERVER_URI + "/" + DB;
			
			ROOT_USERNAME = properties.getProperty(ROOT_USERNAME_VARNAME);
			ROOT_PASSWORD = properties.getProperty(ROOT_PASSWORD_VARNAME);
			
			
			String changedAdminUsername = null;
			try {
				changedAdminUsername = properties.getProperty(CHANGED_ADMIN_USERNAME_VARNAME);
				if(changedAdminUsername==null){
					// To be compliant with old configuration.properties which does not have 
					// CHANGED_ADMIN_USERNAME property we use the db name as admin username
					changedAdminUsername = DB;
				}
			}catch (Exception e) {
				// To be compliant with old configuration.properties which does not have 
				// CHANGED_ADMIN_USERNAME property we use the db name as admin username
				changedAdminUsername = DB;
			}
			CHANGED_ADMIN_USERNAME = changedAdminUsername;
			
			
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
