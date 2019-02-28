package org.gcube.data.access.connector.rest.entity;

import java.util.List;

public class GeoserverClusterConfigurationEntity {

	private List<InstancesEntity> availableInstances;

	public List<InstancesEntity> getAvailableInstances() {
		return availableInstances;
	}

	public void setAvailableInstances(List<InstancesEntity> availableInstances) {
		this.availableInstances = availableInstances;
	}

}
