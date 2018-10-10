package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ClusterBuilder;

public class ClusterBuilderProduction extends ClusterBuilder{

	public ClusterBuilderProduction() {
		super (DMPMClientConfiguratorManager.getInstance().getProductionConfiguration());
		
	}

	
}
