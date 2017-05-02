package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;

import java.util.HashMap;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ISQueryConstants;

public class DBDescriptor extends DataSourceDescriptor {

	public static final String TABLESPACE_PREFIX=ISQueryConstants.get().getDBTableSpacePrefix();
	public static final String TABLESPACE_COUNT=ISQueryConstants.get().getDBTableSpaceCount();
	public static final String AQUAMAPS_WORLD_TABLE=ISQueryConstants.get().getDBAquaMapsWorldTable();
	
	
	public static enum DBType {
		mysql,postgres
	}
	
	
	private DBType type=DBType.postgres;
	
	private Integer     maxConnection=20;
	
	private HashMap<String, String> properties=new HashMap<String, String>();
	
	public DBDescriptor(String entryPoint, String user, String password,
			DBType type, Integer maxConnection) {
		super(entryPoint, user, password);
		this.type = type;
		this.maxConnection = maxConnection;
	}
	public DBType getType() {
		return type;
	}
	public Integer getMaxConnection() {
		return maxConnection;
	}
	
	public String getProperty(String name){
		return properties.get(name);
	}
	
	public void setProperty(String name,String value){
		properties.put(name, value);
	}
	
	public Set<String> getPropertyNames(){
		return properties.keySet();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((maxConnection == null) ? 0 : maxConnection.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBDescriptor other = (DBDescriptor) obj;
		if (maxConnection == null) {
			if (other.maxConnection != null)
				return false;
		} else if (!maxConnection.equals(other.maxConnection))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBDescriptor [type=");
		builder.append(type);
		builder.append(", maxConnection=");
		builder.append(maxConnection);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", getEntryPoint()=");
		builder.append(getEntryPoint());
		builder.append(", getUser()=");
		builder.append(getUser());
		builder.append(", getPassword()=");
		builder.append(getPassword());
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}