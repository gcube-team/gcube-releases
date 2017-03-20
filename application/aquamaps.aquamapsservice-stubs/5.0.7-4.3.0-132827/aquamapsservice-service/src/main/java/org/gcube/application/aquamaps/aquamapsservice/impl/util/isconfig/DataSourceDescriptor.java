package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;


public class DataSourceDescriptor {

	private String entryPoint;
	private String user;
	private String password;
	
	
	
	public DataSourceDescriptor(String entryPoint, String user, String password) {
		super();
		this.entryPoint = entryPoint;
		this.user = user;
		this.password = password;
	}
	public String getEntryPoint() {
		return entryPoint;
	}
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entryPoint == null) ? 0 : entryPoint.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		DataSourceDescriptor other = (DataSourceDescriptor) obj;
		if (entryPoint == null) {
			if (other.entryPoint != null)
				return false;
		} else if (!entryPoint.equals(other.entryPoint))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSourceDescriptor [entryPoint=");
		builder.append(entryPoint);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append(password);
		builder.append("]");
		return builder.toString();
	}
}
