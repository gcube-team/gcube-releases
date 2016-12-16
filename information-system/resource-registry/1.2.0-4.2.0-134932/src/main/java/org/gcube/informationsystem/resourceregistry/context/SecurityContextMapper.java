/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseIntializator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.client.remote.OStorageRemote;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public abstract class SecurityContextMapper {

	private static Logger logger = LoggerFactory
			.getLogger(SecurityContextMapper.class);
	
	public static final String ADMIN_SECURITY_CONTEXT = "00000000-0000-0000-0000-000000000000";
	public static final UUID ADMIN_SECURITY_CONTEXT_UUID = UUID.fromString(ADMIN_SECURITY_CONTEXT);

	public static final String MANAGEMENT_SECURITY_CONTEXT = "ffffffff-ffff-ffff-ffff-ffffffffffff";
	public static final UUID MANAGEMENT_SECURITY_CONTEXT_UUID = UUID.fromString(MANAGEMENT_SECURITY_CONTEXT);

	private static final Map<PermissionMode, Map<UUID, OrientGraphFactory>> securityContextFactories;

	static {
		try {
			boolean created = DatabaseIntializator.initGraphDB();

			logger.trace("Creating factory for {} connecting as {}",
					DatabaseEnvironment.DB_URI,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);

			securityContextFactories = new HashMap<>();

			OrientGraphFactory factory = new OrientGraphFactory(
					DatabaseEnvironment.DB_URI,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME,
					DatabaseEnvironment.CHANGED_ADMIN_PASSWORD)
					.setupPool(1, 10);

			factory.setConnectionStrategy(OStorageRemote.CONNECTION_STRATEGY
					.ROUND_ROBIN_CONNECT.toString());
			
			for (PermissionMode p : PermissionMode.values()) {
				OrientGraphFactory f = new OrientGraphFactory(
						DatabaseEnvironment.DB_URI,
						DatabaseEnvironment.DEFAULT_ADMIN_USERNAME,
						DatabaseEnvironment.CHANGED_ADMIN_PASSWORD)
						.setupPool(1, 10);

				f.setConnectionStrategy(OStorageRemote.CONNECTION_STRATEGY
						.ROUND_ROBIN_CONNECT.toString());
				
				Map<UUID, OrientGraphFactory> map = new HashMap<>();
				map.put(ADMIN_SECURITY_CONTEXT_UUID, f);
				securityContextFactories.put(p, map);
			}

			if (created) {
				OrientGraph orientGraph = factory.getTx();
				SecurityContext.createSecurityContext(orientGraph,
						MANAGEMENT_SECURITY_CONTEXT_UUID);

				getSecurityContextFactory(MANAGEMENT_SECURITY_CONTEXT_UUID,
						PermissionMode.READER);
				getSecurityContextFactory(MANAGEMENT_SECURITY_CONTEXT_UUID,
						PermissionMode.WRITER);

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
	public static OrientGraphFactory getSecurityContextFactory(
			UUID context, PermissionMode permissionMode) {
		OrientGraphFactory factory = null;

		Map<UUID, OrientGraphFactory> permissionSecurityContextFactories = securityContextFactories
				.get(permissionMode);

		factory = permissionSecurityContextFactories.get(context);

		if (factory == null) {

			String username = getSecurityRoleOrUserName(permissionMode,
					SecurityType.USER, context);
			String password = DatabaseEnvironment.DEFAULT_PASSWORDS
					.get(permissionMode);

			factory = new OrientGraphFactory(DatabaseEnvironment.DB_URI,
					username, password).setupPool(1, 10);
			factory.setConnectionStrategy(OStorageRemote.CONNECTION_STRATEGY
					.ROUND_ROBIN_CONNECT.toString());

			permissionSecurityContextFactories.put(context, factory);
		}

		return factory;
	}

	public static String getSecurityRoleOrUserName(
			PermissionMode permissionMode, SecurityType securityType,
			UUID context) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(permissionMode);
		stringBuilder.append(securityType);
		stringBuilder.append("_");
		stringBuilder.append(context.toString());
		return stringBuilder.toString();
	}

}
