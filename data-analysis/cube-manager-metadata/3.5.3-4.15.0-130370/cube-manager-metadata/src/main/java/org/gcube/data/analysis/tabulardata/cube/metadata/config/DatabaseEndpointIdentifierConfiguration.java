package org.gcube.data.analysis.tabulardata.cube.metadata.config;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseEndpointIdentifier;

@Singleton
public class DatabaseEndpointIdentifierConfiguration {

	private static final DatabaseEndpointIdentifier metaAdmin = new DatabaseEndpointIdentifier("TabularData Database",
			"Metadata-Admin");

	@Produces
	@Named("Metadata-Admin")
	public DatabaseEndpointIdentifier getMetaAdmin() {
		return metaAdmin;
	}

}
