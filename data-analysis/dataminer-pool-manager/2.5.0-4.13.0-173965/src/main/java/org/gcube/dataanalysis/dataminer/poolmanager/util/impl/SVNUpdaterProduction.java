package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.tmatesoft.svn.core.SVNException;

/**
 * Created by ggiammat on 5/9/17.
 */
public class SVNUpdaterProduction extends SVNUpdater{




	public SVNUpdaterProduction() throws SVNException {
		super (DMPMClientConfiguratorManager.getInstance().getProductionConfiguration());
	}

	
	
}
