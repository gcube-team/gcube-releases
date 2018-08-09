package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ClusterBuilder;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.ClusterBuilderProduction;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.ClusterBuilderStaging;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.SVNUpdaterProduction;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.SVNUpdaterStaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;


public class DataminerPoolManager {

	private Logger logger;
	private SVNUpdaterStaging svnUpdaterStaging;	
	private SVNUpdaterProduction svnUpdaterProduction;

	public DataminerPoolManager() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		try {
			//TODO: read this from configuration
			this.svnUpdaterStaging = new SVNUpdaterStaging();
			this.svnUpdaterProduction = new SVNUpdaterProduction();
		} catch (SVNException e) {
			this.logger.error("SVN Exception",e);
		}
	}


	public String stageAlgorithm(Algorithm algo,String targetVRE, String category, String algorithm_type/*,String env*/) throws IOException, InterruptedException 
	{
		this.logger.debug("Stage algorithm");
		this.logger.debug("Algo "+algo);
		this.logger.debug("Category "+category);
		this.logger.debug("Algo type "+algorithm_type);
		ClusterBuilder stagingClusterBuilder = new ClusterBuilderStaging();
		Cluster stagingCluster = stagingClusterBuilder.getDataminerCluster();
		//Cluster rProtoCluster = ClusterBuilder.getRProtoCluster();
		DMPMJob job = new StagingJob(this.svnUpdaterStaging, algo, stagingCluster, /*rProtoCluster,*/ targetVRE, category, algorithm_type/*,env*/);
		String id = job.start();
		return id;
	}

	public String publishAlgorithm(Algorithm algo, String targetVRE, String category, String algorithm_type/*, String env*/) throws IOException, InterruptedException 
	{
		this.logger.debug("publish algorithm");		
		this.logger.debug("Algo "+algo);
		this.logger.debug("Category "+category);
		this.logger.debug("Algo type "+algorithm_type);
		ClusterBuilder productionClusterBuilder = new ClusterBuilderProduction();
		Cluster prodCluster = productionClusterBuilder.getDataminerCluster();
		DMPMJob job = new ProductionPublishingJob(this.svnUpdaterProduction, algo, prodCluster, targetVRE, category, algorithm_type/*,env*/);
		String id = job.start();
		return id;
	}


	public String getLogById(String id) throws FileNotFoundException{
		
		//TODO: load dir from configuration file
		this.logger.debug("Getting log by id "+id);
		File path = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager/jobs/"
				+ id);
		
		Scanner scanner = new Scanner(path);
		String response =  scanner.useDelimiter("\\Z").next();
		this.logger.debug("Response "+response);
		scanner.close();
		return response;
	}
	
	
	public String getMonitorById(String id) throws FileNotFoundException{
		
		this.logger.debug("Getting monitor by id "+id);
		//TODO: load dir from configuration file
		File path = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager/jobs/"
				+ id + "_exitStatus");
		Scanner scanner = new Scanner(path);
		String response=  scanner.useDelimiter("\\Z").next();
		this.logger.debug("Response "+response);
		scanner.close();
		return response;
	}
	
	
	
	
}
