/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.util.HashMap;
import java.util.Map;

import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class DatabaseEnvironment {

	/* --- TODO START OF VARIABLES TO GET FROM CONFIGURATION FILE */
	// public static final String HOST = "orientdb01-d-d4s.d4science.org";
	public static final String HOST = "pc-frosini.isti.cnr.it";
	public static final String REMOTE_PROTOCOL = "remote:";
	public static final String REMOTE_URI = REMOTE_PROTOCOL + HOST;
	public static final String HTTP_PROTOCOL = "http://";
	public static final String HTTP_PORT = ":2480";
	public static final String HTTP_URL_STRING = HTTP_PROTOCOL + HOST + HTTP_PORT;
	public static final String DB = "IS";
	public static final String URI_DB = REMOTE_URI + "/" + DB;
	public static final String USERNAME = "root";
	public static final String PASSWORD = "testpwd"; //"D4S*ll2T16";
	public static final String CHANGED_ADMIN_PASSWORD = "D4S*ll2T16";
	/* --- END OF VARIABLES TO GET FROM CONFIGURATION FILE */
	
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "admin";
	
	private static final String DEFAULT_CREATED_WRITER_USER_PASSWORD = "Choomae5";
	private static final String DEFAULT_CREATED_READER_USER_PASSWORD = "Chetho4s";
	
	public static final Map<PermissionMode, String> DEFAULT_PASSWORDS;
	
	static {
		DEFAULT_PASSWORDS = new HashMap<SecurityContextMapper.PermissionMode, String>();
		
		DEFAULT_PASSWORDS.put(PermissionMode.WRITER, DEFAULT_CREATED_WRITER_USER_PASSWORD);
		DEFAULT_PASSWORDS.put(PermissionMode.READER, DEFAULT_CREATED_READER_USER_PASSWORD);
	}
	
}
