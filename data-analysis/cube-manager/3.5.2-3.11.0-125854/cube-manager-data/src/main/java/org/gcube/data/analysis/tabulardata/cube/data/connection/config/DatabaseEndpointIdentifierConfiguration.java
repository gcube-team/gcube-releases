package org.gcube.data.analysis.tabulardata.cube.data.connection.config;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseEndpointIdentifier;

@Singleton
public class DatabaseEndpointIdentifierConfiguration {

	private static final DatabaseEndpointIdentifier dataAdmin = new DatabaseEndpointIdentifier("TabularData Database",
			"Data-Admin");
	private static final DatabaseEndpointIdentifier dataClient = new DatabaseEndpointIdentifier("TabularData Database",
			"Data-User");

	@Produces
	@Named("Data-Admin")
	public DatabaseEndpointIdentifier getDataAdmin() {
		return dataAdmin;
	}

	@Produces
	@Named("Data-User")
	public DatabaseEndpointIdentifier getDataUser() {
		return dataClient;
	}

}
