package org.gcube.common.database;

/**
 * Identifies a database endpoint. 
 * A database endpoint identifies:
 * <ul>
 * <li>Database engine</li>
 * <li>Database instance available</li>
 * <li>Database access privileges / credentials</li>
 * </ul>
 * 
 * @author "Luigi Fortunati"
 *
 */
public class DatabaseEndpointIdentifier {

	private String databaseId;

	private String endpointId;

	public DatabaseEndpointIdentifier(String databaseName, String endpointName) {
		super();
		this.databaseId = databaseName;
		this.endpointId = endpointName;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getEndpointId() {
		return endpointId;
	}

	public void setEndpointId(String endpointId) {
		this.endpointId = endpointId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((databaseId == null) ? 0 : databaseId.hashCode());
		result = prime * result + ((endpointId == null) ? 0 : endpointId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatabaseEndpointIdentifier other = (DatabaseEndpointIdentifier) obj;
		if (databaseId == null) {
			if (other.databaseId != null)
				return false;
		} else if (!databaseId.equals(other.databaseId))
			return false;
		if (endpointId == null) {
			if (other.endpointId != null)
				return false;
		} else if (!endpointId.equals(other.endpointId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatabaseEndpointIdentifier [databaseName=");
		builder.append(databaseId);
		builder.append(", endpointName=");
		builder.append(endpointId);
		builder.append("]");
		return builder.toString();
	}

}
