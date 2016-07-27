/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public abstract class SecurityContextMapper {

	private static Logger logger = LoggerFactory.getLogger(SecurityContextMapper.class);

	public static final String MANAGEMENT_SECURITY_CONTEXT = "ManagementSecurityContext";
	
	private static final Map<PermissionMode, Map<String, OrientGraphFactory>> securityContextFactories;
	
	static {
		try {
			boolean created = DatabaseIntializator.initGraphDB();

			logger.trace("Creating factory for {} connecting as {}",
					DatabaseEnvironment.URI_DB,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			
			securityContextFactories = new HashMap<>();
			
			OrientGraphFactory factory = new OrientGraphFactory(
					DatabaseEnvironment.URI_DB,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME,
					DatabaseEnvironment.CHANGED_ADMIN_PASSWORD)
					.setupPool(1, 10);
			
			for(PermissionMode p : PermissionMode.values()){
				Map<String, OrientGraphFactory> map = new HashMap<>(); 
				map.put(null, factory);
				securityContextFactories.put(p, map);
				map.put(null, factory);
			}
			
			if(created){
				SecurityContext.createSecurityContext(factory.getTx(), MANAGEMENT_SECURITY_CONTEXT);
				getSecurityContextFactory(MANAGEMENT_SECURITY_CONTEXT, PermissionMode.READER);
				getSecurityContextFactory(MANAGEMENT_SECURITY_CONTEXT, PermissionMode.WRITER);
				
				DatabaseIntializator.createEntitiesAndRelations();
			}

		} catch (Exception e) {
			logger.error("Error initializing database connection", e);
			throw new RuntimeException(
					"Error initializing database connection", e);
		}
	}

	public enum SecurityType {
		ROLE("Role"), USER("User");

		private final String name;

		private SecurityType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public enum PermissionMode {
		READER("Reader"), WRITER("Writer");

		private final String name;

		private PermissionMode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * @param contextID
	 *            use null for no context (used for admin operations)
	 * @return
	 */
	public static OrientGraphFactory getSecurityContextFactory(String contextID, PermissionMode permissionMode) {
		OrientGraphFactory factory = null;
		
		Map<String, OrientGraphFactory> permissionSecurityContextFactories = 
				securityContextFactories.get(permissionMode);
		
		factory = permissionSecurityContextFactories.get(contextID);
		
		if (factory == null) {
			
			String username = getSecurityRoleOrUserName(permissionMode,
							SecurityType.USER, contextID);
			String password = DatabaseEnvironment.DEFAULT_PASSWORDS.get(permissionMode);
			
			factory = new OrientGraphFactory(DatabaseEnvironment.URI_DB, 
							username, password).setupPool(1, 10);
			
			permissionSecurityContextFactories.put(contextID, factory);
		}
		
		return factory;
	}
	
	public static String getSecurityRoleOrUserName(
			PermissionMode permissionMode, SecurityType securityType,
			String contextID) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(permissionMode);
		stringBuilder.append(securityType);
		stringBuilder.append("_");
		stringBuilder.append(contextID);
		return stringBuilder.toString();
	}

}
