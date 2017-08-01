package org.gcube.common.database;

import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.engine.DatabaseInstance;


/**
 * Provider of {@link DatabaseEndpoint} instances. Exposes methods for retrieving database properties by providing db identifiers.
 * 
 * @author "Luigi Fortunati"
 *
 */
public interface DatabaseProvider {

	public DatabaseInstance get(String databaseEngineId);
	
	/**
	 * Retrieve a {@link DatabaseEndpoint} by the means of a {@link DatabaseEndpointIdentifier} 
	 * 
	 * @param endpointIdentifier
	 * @return
	 */
	public DatabaseEndpoint get(DatabaseEndpointIdentifier endpointIdentifier);
	
	/**
	 * Retrieves a {@link DatabaseEndpoint} by providing a database and endpoint name without the need to instantiate a {@link DatabaseEndpointIdentifier}.
	 * 
	 * @param databaseInstanceId
	 * @param endpointId
	 * @return
	 */
	public DatabaseEndpoint get(String databaseInstanceId, String endpointId);
	
}
