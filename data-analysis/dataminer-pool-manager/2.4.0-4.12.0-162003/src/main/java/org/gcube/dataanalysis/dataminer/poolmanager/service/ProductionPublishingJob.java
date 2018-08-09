package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.CheckMethodProduction;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.NotificationHelperProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductionPublishingJob extends DMPMJob {
	

	//private String targetVREToken;
	//private String env;
	private Logger logger;

	public ProductionPublishingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			Cluster prodCluster, String targetVREName, String category,String algorithm_type/*, String env*/) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater,DMPMClientConfiguratorManager.getInstance().getProductionConfiguration(),algorithm,prodCluster,targetVREName,category,algorithm_type);
		this.logger = LoggerFactory.getLogger(StagingJob.class);//		this.jobLogs = new File(


	}
		
	@Override
	protected void execute() {
		this.logger.debug("Executing staging job...");
		super.execute(new NotificationHelperProduction(), new CheckMethodProduction());	
	}


	


	

	
	
	
}
