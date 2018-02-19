package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ClusterBuilder;

public class ClusterBuilderStaging extends ClusterBuilder{

	public ClusterBuilderStaging() {
		super (DMPMClientConfiguratorManager.getInstance().getStagingConfiguration());
	}
	
	
}
