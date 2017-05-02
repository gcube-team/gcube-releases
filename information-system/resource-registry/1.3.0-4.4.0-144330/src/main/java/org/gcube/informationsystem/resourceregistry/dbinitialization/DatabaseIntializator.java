/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.impl.utils.discovery.ERDiscovery;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.resourceregistry.context.SecurityContextMapper.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class DatabaseIntializator {

	private static Logger logger = LoggerFactory
			.getLogger(DatabaseIntializator.class);

	private static final String DATABASE_TYPE = "graph";
	private static final String STORAGE_MODE = "plocal";

	public static final String O_RESTRICTED_CLASS = "ORestricted";

	public static boolean initGraphDB() throws Exception {
		
		OLogManager.instance().setWarnEnabled(false);
		OLogManager.instance().setErrorEnabled(false);
		OLogManager.instance().setInfoEnabled(false);
		OLogManager.instance().setDebugEnabled(false);
		
		logger.trace("Connecting to {} as {} to create new DB",
				DatabaseEnvironment.SERVER_URI, DatabaseEnvironment.USERNAME);
		OServerAdmin serverAdmin = new OServerAdmin(DatabaseEnvironment.SERVER_URI)
				.connect(DatabaseEnvironment.USERNAME, 
						 DatabaseEnvironment.PASSWORD);

		if (!serverAdmin.existsDatabase(DatabaseEnvironment.DB, STORAGE_MODE)) {

			logger.trace("Creating Database {}", DatabaseEnvironment.DB_URI);
			serverAdmin.createDatabase(DatabaseEnvironment.DB, DATABASE_TYPE,
					STORAGE_MODE);

			logger.trace(
					"Connecting to newly created database {} as {} with default password",
					DatabaseEnvironment.DB_URI,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);

			OrientGraphFactory factory = new OrientGraphFactory(
					DatabaseEnvironment.DB_URI,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME,
					DatabaseEnvironment.DEFAULT_ADMIN_PASSWORD)
					.setupPool(1, 10);

			OrientGraphNoTx orientGraphNoTx = factory.getNoTx();

			OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
			OSecurity oSecurity = oMetadata.getSecurity();

			logger.trace("Changing {} password",
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			OUser admin = oSecurity
					.getUser(DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			admin.setPassword(DatabaseEnvironment.CHANGED_ADMIN_PASSWORD);
			admin.save();

			for (PermissionMode permissionMode : DatabaseEnvironment.DEFAULT_PASSWORDS
					.keySet()) {
				OUser oUser = oSecurity.getUser(permissionMode.toString());
				oUser.setPassword(DatabaseEnvironment.DEFAULT_PASSWORDS
						.get(permissionMode));
				oUser.save();
				logger.trace("Updating password for user {}",
						permissionMode.toString());
			}

			logger.trace("Setting Record-level Security (see https://orientdb.com/docs/last/Database-Security.html)");
			OSchema oSchema = oMetadata.getSchema();
			OClass oRestricted = oSchema.getClass(O_RESTRICTED_CLASS);

			OrientVertexType v = orientGraphNoTx.getVertexBaseType();
			v.addSuperClass(oRestricted);

			OrientEdgeType e = orientGraphNoTx.getEdgeBaseType();
			e.addSuperClass(oRestricted);

			//orientGraphNoTx.commit();
			orientGraphNoTx.shutdown();

			factory.close();

			return true;
		}

		return false;
	}

	public static void createEntitiesAndRelations() throws Exception {
		ERDiscovery erDiscovery = ISMapper.getErdiscovery();
		SchemaActionImpl entityRegistrationAction = new SchemaActionImpl();
		entityRegistrationAction.manageEmbeddedClass(Embedded.class);
		entityRegistrationAction.manageEmbeddedClass(ValueSchema.class);
		erDiscovery.manageDiscoveredERTypes(entityRegistrationAction);
	}

}
