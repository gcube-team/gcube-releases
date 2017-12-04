package org.gcube.data.analysis.tabulardata.cube.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.Credential;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.endpoint.DatabaseProperty;
import org.gcube.common.database.engine.DatabaseInstance;
import org.gcube.common.database.engine.HostingNode;
import org.gcube.common.database.engine.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Alternative
public class TestDatabaseProvider implements DatabaseProvider {

	private static final Logger log = LoggerFactory.getLogger(TestDatabaseProvider.class);

	
	
	private static DatabaseInstance dbInstance;

	static {
		Platform platform = new Platform("test", new Short("1"), new Short("0"), new Short("0"), new Short("0"));
		Map<String, DatabaseEndpoint> endpoints = new HashMap<String, DatabaseEndpoint>();

		endpoints.put("Metadata-Admin", createMetadataAdminEndpoint());
		endpoints.put("Data-Admin", createDataAdminEndpoint());
		endpoints.put("Data-User", createDataUserEndpoint());

		HostingNode hostingNode = new HostingNode("localhost", "none", "none");
		dbInstance = new DatabaseInstance("TabularData Database", endpoints, platform, hostingNode);
		log.info("Created dbinstance:\n" + dbInstance);
	}

	private static DatabaseEndpoint createMetadataAdminEndpoint() {
		Collection<DatabaseProperty> properties = new ArrayList<DatabaseProperty>();
		properties.add(new DatabaseProperty("driver", "org.postgresql.Driver"));
		properties.add(new DatabaseProperty("eclipselink.target-database",
				"org.eclipse.persistence.platform.database.PostgreSQLPlatform"));
		properties.add(new DatabaseProperty("eclipselink.ddl-generation", "drop-and-create-tables"));

		return new DatabaseEndpoint("Metadata-Admin", "Metadata-Admin",
				"jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/tabularmetadatatest",
				new Credential("tabulardataadmin", "gcube2010"), properties);
	}

	private static DatabaseEndpoint createDataUserEndpoint() {
		Collection<DatabaseProperty> properties = new ArrayList<DatabaseProperty>();
		properties.add(new DatabaseProperty("driver", "org.postgresql.Driver"));
		return new DatabaseEndpoint("Data-User", "test description",
				"jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/tabulardatatest", new Credential(
						"tabulardataadmin", "gcube2010"), properties);
	}

	private static DatabaseEndpoint createDataAdminEndpoint() {
		Collection<DatabaseProperty> properties = new ArrayList<DatabaseProperty>();
		properties.add(new DatabaseProperty("driver", "org.postgresql.Driver"));
		return new DatabaseEndpoint("Data-Admin", "test description",
				"jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/tabulardatatest", new Credential(
						"tabulardataadmin", "gcube2010"), properties);
	}

	@Override
	public DatabaseInstance get(String databaseEngineId) {
		return dbInstance;
	}

	@Override
	public DatabaseEndpoint get(DatabaseEndpointIdentifier endpointIdentifier) {
		return get(endpointIdentifier.getDatabaseId()).getEndpoint(endpointIdentifier.getEndpointId());
	}

	@Override
	public DatabaseEndpoint get(String databaseInstanceId, String endpointId) {
		return get(new DatabaseEndpointIdentifier(databaseInstanceId, endpointId));
	}

}
