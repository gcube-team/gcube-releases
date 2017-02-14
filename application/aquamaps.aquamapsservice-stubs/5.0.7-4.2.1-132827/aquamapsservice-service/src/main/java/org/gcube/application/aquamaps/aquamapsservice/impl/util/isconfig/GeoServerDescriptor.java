package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;


public class GeoServerDescriptor extends DataSourceDescriptor{
	private String workspace;
	private String datastore;
	private String defaultDistributionStyle;
	public GeoServerDescriptor(String entryPoint, String user, String password,
			String workspace, String datastore, String defaultDistributionStyle) {
		super(entryPoint, user, password);
		this.workspace = workspace;
		this.datastore = datastore;
		this.defaultDistributionStyle = defaultDistributionStyle;
	}
	public String getWorkspace() {
		return workspace;
	}
	public String getDatastore() {
		return datastore;
	}
	public String getDefaultDistributionStyle() {
		return defaultDistributionStyle;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((datastore == null) ? 0 : datastore.hashCode());
		result = prime
				* result
				+ ((defaultDistributionStyle == null) ? 0
						: defaultDistributionStyle.hashCode());
		result = prime * result
				+ ((workspace == null) ? 0 : workspace.hashCode());
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
		GeoServerDescriptor other = (GeoServerDescriptor) obj;
		if (datastore == null) {
			if (other.datastore != null)
				return false;
		} else if (!datastore.equals(other.datastore))
			return false;
		if (defaultDistributionStyle == null) {
			if (other.defaultDistributionStyle != null)
				return false;
		} else if (!defaultDistributionStyle
				.equals(other.defaultDistributionStyle))
			return false;
		if (workspace == null) {
			if (other.workspace != null)
				return false;
		} else if (!workspace.equals(other.workspace))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoServerDescriptor [workspace=");
		builder.append(workspace);
		builder.append(", datastore=");
		builder.append(datastore);
		builder.append(", defaultDistributionStyle=");
		builder.append(defaultDistributionStyle);
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
