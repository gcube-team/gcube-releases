package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.CheckMethodStaging;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.NotificationHelperStaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagingJob extends DMPMJob {



	private Logger logger;

	public StagingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			Cluster stagingCluster, /* Cluster rProtoCluster, */
			String rProtoVREName, String category, String algorithm_type/*, String env*/) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater,DMPMClientConfiguratorManager.getInstance().getStagingConfiguration(),algorithm,stagingCluster,rProtoVREName,category,algorithm_type);
		this.logger = LoggerFactory.getLogger(StagingJob.class);
	}

	@Override
	protected void execute() {

		this.logger.debug("Executing staging job...");
		super.execute(new NotificationHelperStaging(), new CheckMethodStaging());
		
	}



	



	
}
