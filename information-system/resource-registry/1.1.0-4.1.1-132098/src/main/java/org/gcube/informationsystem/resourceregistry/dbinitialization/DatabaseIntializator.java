/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.util.HashSet;
import java.util.Set;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class DatabaseIntializator {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseIntializator.class);
	
	private static final String DATABASE_TYPE = "graph";
	private static final String STORAGE_MODE = "plocal";
	
	private static final String O_RESTRICTED_CLASS = "ORestricted";
	
	
	
	private static Set<Package> packages;
	
	public static void addPackage(Package p){
		packages.add(p);
	}

	static {
		packages = new HashSet<>();
	}
	
	public static boolean initGraphDB() throws Exception {

		logger.trace("Connecting to {} as {} to create new DB", DatabaseEnvironment.URI_DB, DatabaseEnvironment.USERNAME);
		OServerAdmin serverAdmin = new OServerAdmin(DatabaseEnvironment.URI_DB).connect(DatabaseEnvironment.USERNAME,
				DatabaseEnvironment.PASSWORD);

		if (!serverAdmin.existsDatabase()) {

			logger.trace("Creating Database {}", DatabaseEnvironment.URI_DB);
			serverAdmin.createDatabase(DatabaseEnvironment.DB, DATABASE_TYPE, STORAGE_MODE);

			logger.trace(
					"Connecting to newly created database {} as {} with default password",
					DatabaseEnvironment.URI_DB, DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			OrientGraphFactory factory = new OrientGraphFactory(DatabaseEnvironment.URI_DB,
					DatabaseEnvironment.DEFAULT_ADMIN_USERNAME, DatabaseEnvironment.DEFAULT_ADMIN_PASSWORD).setupPool(
					1, 10);

			OrientGraphNoTx orientGraphNoTx = factory.getNoTx();

			OMetadata oMetadata = orientGraphNoTx.getRawGraph().getMetadata();
			OSecurity oSecurity = oMetadata.getSecurity();

			logger.trace("Changing {} password", DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			OUser admin = oSecurity.getUser(DatabaseEnvironment.DEFAULT_ADMIN_USERNAME);
			admin.setPassword(DatabaseEnvironment.CHANGED_ADMIN_PASSWORD);
			admin.save();

			for (PermissionMode permissionMode : DatabaseEnvironment.DEFAULT_PASSWORDS.keySet()) {
				OUser oUser = oSecurity.getUser(permissionMode.toString());
				oUser.setPassword(DatabaseEnvironment.DEFAULT_PASSWORDS.get(permissionMode));
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

			orientGraphNoTx.commit();
			orientGraphNoTx.shutdown();

			factory.close();
			
			return true;
		}
		
		return false;
	}
	
	public static void createEntitiesAndRelations() throws Exception{
		SchemaInitializator.addPackage(Embedded.class.getPackage());
		SchemaInitializator.addPackage(Entity.class.getPackage());
		SchemaInitializator.addPackage(Relation.class.getPackage());
		for(Package p : packages){
			SchemaInitializator.addPackage(p);
		}
		SchemaInitializator.createTypes();
	}
	
}
