package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.gcube.informationsystem.impl.utils.discovery.ISMDiscovery;
import org.gcube.informationsystem.impl.utils.discovery.SchemaAction;
import org.gcube.informationsystem.model.ISConstants;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.context.security.AdminSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.ContextSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SchemaSecurityContext;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.client.remote.OStorageRemote.CONNECTION_STRATEGY;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DatabaseEnvironment {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseEnvironment.class);
	
	private static final String PROPERTY_FILENAME = "config.properties";
	
	private static final String HOST_VARNAME = "HOST";
	
	private static final String REMOTE_PROTOCOL;
	private static final String REMOTE_PROTOCOL_VARNAME = "REMOTE_PROTOCOL";
	
	private static final String DB;
	private static final String DB_VARNAME = "DB";
	
	private static final String ROOT_USERNAME;
	private static final String ROOT_USERNAME_VARNAME = "ROOT_USERNAME";
	
	private static final String ROOT_PASSWORD;
	private static final String ROOT_PASSWORD_VARNAME = "ROOT_PASSWORD";
	
	private static final String DEFAULT_ADMIN_USERNAME;
	private static final String DEFAULT_ADMIN_USERNAME_VARNAME = "DEFAULT_ADMIN_USERNAME";
	
	public static final String DEFAULT_ADMIN_ROLE = "admin";
	
	private static final String CHANGED_ADMIN_USERNAME;
	private static final String CHANGED_ADMIN_USERNAME_VARNAME = "CHANGED_ADMIN_USERNAME";
	
	private static final String DEFAULT_ADMIN_PASSWORD;
	private static final String DEFAULT_ADMIN_PASSWORD_VARNAME = "DEFAULT_ADMIN_PASSWORD";
	
	private static final String CHANGED_ADMIN_PASSWORD;
	private static final String CHANGED_ADMIN_PASSWORD_VARNAME = "CHANGED_ADMIN_PASSWORD";
	
	private static final String DEFAULT_CREATED_WRITER_USER_PASSWORD;
	private static final String DEFAULT_CREATED_WRITER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_WRITER_USER_PASSWORD";
	
	private static final String DEFAULT_CREATED_READER_USER_PASSWORD;
	private static final String DEFAULT_CREATED_READER_USER_PASSWORD_VARNAME = "DEFAULT_CREATED_READER_USER_PASSWORD";
	
	public static final Map<PermissionMode,String> DEFAULT_PASSWORDS;
	
	private static final String HOSTS;
	
	private static final String SERVER_URI;
	public static final String DB_URI;
	
	private static final String DATABASE_TYPE = "graph";
	private static final String STORAGE_MODE = "plocal";
	
	public static final String O_RESTRICTED_CLASS = "ORestricted";
	
	public static final CONNECTION_STRATEGY CONNECTION_STRATEGY_PARAMETER = CONNECTION_STRATEGY.ROUND_ROBIN_CONNECT;
	
	private static final String ALTER_DATETIME_FORMAT_QUERY_TEMPLATE = "ALTER DATABASE DATETIMEFORMAT \"%s\"";
	
	// Used to indicate virtual admin security context
	private static final String ADMIN_SECURITY_CONTEXT;
	public static final UUID ADMIN_SECURITY_CONTEXT_UUID;
	
	// Used to persist Schemas
	private static final String SCHEMA_SECURITY_CONTEXT;
	public static final UUID SCHEMA_SECURITY_CONTEXT_UUID;
	
	// Used to Persist Context and their relations
	private static final String CONTEXT_SECURITY_CONTEXT;
	public static final UUID CONTEXT_SECURITY_CONTEXT_UUID;
	
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
				if(changedAdminUsername == null) {
					// To be compliant with old configuration.properties which does not have
					// CHANGED_ADMIN_USERNAME property we use the db name as admin username
					changedAdminUsername = DB;
				}
			} catch(Exception e) {
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
			
			DEFAULT_PASSWORDS = new HashMap<PermissionMode,String>();
			
			DEFAULT_PASSWORDS.put(PermissionMode.WRITER, DEFAULT_CREATED_WRITER_USER_PASSWORD);
			DEFAULT_PASSWORDS.put(PermissionMode.READER, DEFAULT_CREATED_READER_USER_PASSWORD);
			
		} catch(Exception e) {
			logger.error("Unable to load properties from {}", PROPERTY_FILENAME);
			throw new RuntimeException("Unable to load properties", e);
		}
		
		ADMIN_SECURITY_CONTEXT = "00000000-0000-0000-0000-000000000000";
		ADMIN_SECURITY_CONTEXT_UUID = UUID.fromString(ADMIN_SECURITY_CONTEXT);
		
		// Used to persist Schemas
		SCHEMA_SECURITY_CONTEXT = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";
		SCHEMA_SECURITY_CONTEXT_UUID = UUID.fromString(SCHEMA_SECURITY_CONTEXT);
		
		// Used to Persist Context and their relations
		CONTEXT_SECURITY_CONTEXT = "ffffffff-ffff-ffff-ffff-ffffffffffff";
		CONTEXT_SECURITY_CONTEXT_UUID = UUID.fromString(CONTEXT_SECURITY_CONTEXT);
		
		try {
			boolean created = initGraphDB();
			
			ContextUtility contextUtility = ContextUtility.getInstance();
			
			AdminSecurityContext adminSecurityContext = new AdminSecurityContext();
			contextUtility.addSecurityContext(adminSecurityContext.getUUID().toString(), adminSecurityContext);
			
			ContextSecurityContext contextSecurityContext = new ContextSecurityContext();
			contextUtility.addSecurityContext(contextSecurityContext.getUUID().toString(), contextSecurityContext);
			
			SchemaSecurityContext schemaSecurityContext = new SchemaSecurityContext();
			contextUtility.addSecurityContext(schemaSecurityContext.getUUID().toString(), schemaSecurityContext);
			
			if(created) {
				OrientGraphFactory factory = new OrientGraphFactory(DB_URI, CHANGED_ADMIN_USERNAME,
						CHANGED_ADMIN_PASSWORD).setupPool(1, 10);
				OrientGraph orientGraph = factory.getTx();
				adminSecurityContext.create(orientGraph);
				orientGraph.commit();
				orientGraph.shutdown();
				factory.close();
				
				contextSecurityContext.create();
				
				schemaSecurityContext.create();
				
				SchemaAction schemaAction = new SchemaActionImpl();
				ISMDiscovery.manageISM(schemaAction);
				
			}
			
		} catch(Exception e) {
			logger.error("Error initializing database connection", e);
			throw new RuntimeException("Error initializing database connection", e);
		}
	}
	
	private static boolean initGraphDB() throws Exception {
		
		OLogManager.instance().setWarnEnabled(false);
		OLogManager.instance().setErrorEnabled(false);
		OLogManager.instance().setInfoEnabled(false);
		OLogManager.instance().setDebugEnabled(false);
		
		logger.info("Connecting as {} to {}", ROOT_USERNAME, DB_URI);
		OServerAdmin serverAdmin = new OServerAdmin(SERVER_URI).connect(ROOT_USERNAME, ROOT_PASSWORD);
		
		if(!serverAdmin.existsDatabase(DB, STORAGE_MODE)) {
			
			logger.info("The database {} does not exist. Going to create it.", DB_URI);
			serverAdmin.createDatabase(DB, DATABASE_TYPE, STORAGE_MODE);
			
			logger.trace("Connecting to newly created database {} as {} with default password", DB_URI,
					DEFAULT_ADMIN_USERNAME);
			
			OrientGraphFactory factory = new OrientGraphFactory(DB_URI, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD)
					.setupPool(1, 10);
			
			OrientGraphNoTx orientGraphNoTx = factory.getNoTx();
			
			/* Updating DateTimeFormat to be aligned with IS model definition */
			/*
			 * This solution does not work OStorageConfiguration configuration =
			 * orientGraphNoTx.getRawGraph().getStorage().getConfiguration();
			 * configuration.dateTimeFormat = ISConstants.DATETIME_PATTERN;
			 * configuration.update();
			 */
			String query = String.format(ALTER_DATETIME_FORMAT_QUERY_TEMPLATE, ISConstants.DATETIME_PATTERN);
			OCommandSQL preparedQuery = new OCommandSQL(query);
			orientGraphNoTx.getRawGraph().command(preparedQuery).execute();
			
			OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
			OSecurity oSecurity = oMetadata.getSecurity();
			
			logger.trace("Changing {} password", DEFAULT_ADMIN_USERNAME);
			
			OUser admin = oSecurity.getUser(DEFAULT_ADMIN_USERNAME);
			admin.setPassword(CHANGED_ADMIN_PASSWORD);
			admin.save();
			
			logger.trace("Creating new admin named '{}'", CHANGED_ADMIN_USERNAME);
			ORole adminRole = oSecurity.getRole(DEFAULT_ADMIN_ROLE);
			OUser newAdminUser = oSecurity.createUser(CHANGED_ADMIN_USERNAME, CHANGED_ADMIN_PASSWORD, adminRole);
			newAdminUser.save();
			
			for(PermissionMode permissionMode : DEFAULT_PASSWORDS.keySet()) {
				OUser oUser = oSecurity.getUser(permissionMode.toString());
				oUser.setPassword(DEFAULT_PASSWORDS.get(permissionMode));
				oUser.save();
				logger.trace("Updating password for user {}", permissionMode.toString());
			}
			
			logger.trace("Setting Record-level Security (see https://orientdb.com/docs/last/Database-Security.html)");
			OSchema oSchema = oMetadata.getSchema();
			OClass oRestricted = oSchema.getClass(O_RESTRICTED_CLASS);
			
			OrientVertexType v = orientGraphNoTx.getVertexBaseType();
			v.addSuperClass(oRestricted);
			
			OrientEdgeType e = orientGraphNoTx.getEdgeBaseType();
			e.addSuperClass(oRestricted);
			
			// orientGraphNoTx.commit();
			orientGraphNoTx.shutdown();
			
			factory.close();
			
			return true;
		}
		
		serverAdmin.close();
		
		return false;
	}
	
}
