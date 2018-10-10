package org.gcube.data.access.connector.rest.entity;

public class SDIEntity {

	private String contextName;
	private InstancesEntity geonetworkConfiguration;
	private GeoserverClusterConfigurationEntity geoserverClusterConfiguration;

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public InstancesEntity getGeonetworkConfiguration() {
		return geonetworkConfiguration;
	}

	public void setGeonetworkConfiguration(InstancesEntity geonetworkConfiguration) {
		this.geonetworkConfiguration = geonetworkConfiguration;
	}

	public GeoserverClusterConfigurationEntity getGeoserverClusterConfiguration() {
		return geoserverClusterConfiguration;
	}

	public void setGeoserverClusterConfiguration(GeoserverClusterConfigurationEntity geoserverClusterConfiguration) {
		this.geoserverClusterConfiguration = geoserverClusterConfiguration;
	}

	@Override
	public String toString() {
		return "SDI [contextName=" + contextName + ", GeonetworkConfiguration=" + geonetworkConfiguration
				+ ", GeoserverClusterConfiguration=" + geoserverClusterConfiguration + "]";
	}
}
