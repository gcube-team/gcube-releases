package org.gcube.data.analysis.tabulardata.cube.metadata;

import java.util.Map;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.endpoint.DatabaseProperty;

import com.google.common.collect.Maps;

@Singleton
public class ISEntityManagerProvider {

	private DatabaseEndpointIdentifier metaDBEndpointId;

	private DatabaseProvider dbProvider;

	private EntityManagerFactory emf;

	public ISEntityManagerProvider(DatabaseEndpointIdentifier metaDBEndpointId,
			DatabaseProvider dbProvider) {
		this.metaDBEndpointId = metaDBEndpointId;
		this.dbProvider = dbProvider;
	}

	public EntityManagerFactory get() {
		if (!isInitialized()){
			DatabaseEndpoint dbDescriptor = dbProvider.get(metaDBEndpointId);

			// Retrieve driver from properties
			String driver = null;
			String eclipseLinkTargetDb = null;
			String ddlGenerationStrategy = null;
			for (DatabaseProperty p : dbDescriptor.getProperties()) {
				if (p.getKey().equals("driver"))
					driver = p.getValue();
				if (p.getKey().equals("eclipselink.target-database"))
					eclipseLinkTargetDb = p.getValue();
				if (p.getKey().equals("eclipselink.ddl-generation"))
					ddlGenerationStrategy = p.getValue();
			}
			if (driver == null)
				throw new RuntimeException("Unable to find the right driver for the connection to the DB: "
						+ dbDescriptor);

			Map<String, String> properties = Maps.newHashMap();
			properties.put("javax.persistence.jdbc.user", dbDescriptor.getCredentials().getUsername());
			properties.put("javax.persistence.jdbc.password", dbDescriptor.getCredentials().getPassword());
			properties.put("javax.persistence.jdbc.driver", driver);
			properties.put("javax.persistence.jdbc.url", dbDescriptor.getConnectionString());
			properties.put("eclipselink.target-database", eclipseLinkTargetDb);
			if (ddlGenerationStrategy != null)
				properties.put("eclipselink.ddl-generation", ddlGenerationStrategy);
			else
				properties.put("eclipselink.ddl-generation", "create-tables");
			// properties.put("eclipselink.ddl-generation","create-or-extend-tables");
			// properties.put("eclipselink.ddl-generation","drop-and-create-tables");
			emf =  Persistence.createEntityManagerFactory("default", properties);
		}
		return emf;
	}

	public void close(){
		emf.close();
		emf = null;
	}

	public boolean isInitialized(){
		return emf!=null;
	}

}
