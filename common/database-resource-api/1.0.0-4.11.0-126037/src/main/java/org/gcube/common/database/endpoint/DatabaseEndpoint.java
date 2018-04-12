package org.gcube.common.database.endpoint;

import java.util.ArrayList;
import java.util.Collection;



/**
 * A database descriptor describe properties of a database endpoint.
 * 
 * @author "Luigi Fortunati"
 *
 */
public class DatabaseEndpoint {
	
	private String id;
	
	//Database properties
	private String description;
	private String connectionString;
	private Credential credentials;
	private Collection<DatabaseProperty> properties = new ArrayList<DatabaseProperty>();
	
	public DatabaseEndpoint() {
	}
	
	public DatabaseEndpoint(String endpointId, String description,
			String connectionString, Credential credentials, Collection<DatabaseProperty> properties) {
		super();
		this.id = endpointId;
		this.description = description;
		this.connectionString = connectionString;
		this.credentials = credentials;
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public Credential getCredentials() {
		return credentials;
	}

	public void setCredentials(Credential credentials) {
		this.credentials = credentials;
	}

	public Collection<DatabaseProperty> getProperties() {
		return properties;
	}

	public void setProperties(Collection<DatabaseProperty> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBDescriptor [endpointIdentifier=");
		builder.append(id);
		builder.append(", description=");
		builder.append(description);
		builder.append(", connectionString=");
		builder.append(connectionString);
		builder.append(", credentials=");
		builder.append(credentials);
		builder.append(", properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}
	
}
