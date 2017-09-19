package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ClusterBuilder;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ServiceConfiguration;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.tmatesoft.svn.core.SVNException;

public class DataminerPoolManager {


	private SVNUpdater svnUpdater;

	public DataminerPoolManager() {
		try {
			//TODO: read this from configuration
			this.svnUpdater = new SVNUpdater(new ServiceConfiguration());
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}


	public String stageAlgorithm(Algorithm algo,String targetVRE, String category, String algorithm_type,String env) throws IOException, InterruptedException {
		
			Cluster stagingCluster = ClusterBuilder.getStagingDataminerCluster(env);

		
		//Cluster rProtoCluster = ClusterBuilder.getRProtoCluster();
		
		DMPMJob job = new StagingJob(this.svnUpdater, algo, stagingCluster, /*rProtoCluster,*/ targetVRE, category, algorithm_type,env);
		String id = job.start();
		return id;
	}

	public String publishAlgorithm(Algorithm algo, String targetVRE, String category, String algorithm_type, String env) throws IOException, InterruptedException {
		
		//Cluster prodCluster = ClusterBuilder.getVRECluster(targetVREToken, targetVRE);
		
		DMPMJob job = new ProductionPublishingJob(this.svnUpdater, algo, /*prodCluster,*/ targetVRE, category, algorithm_type,env);
		String id = job.start();
		return id;
	}


	public String getLogById(String id) throws FileNotFoundException{
		
		//TODO: load dir from configuration file
		File path = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager/jobs/"
				+ id);
		
		return new Scanner(path).useDelimiter("\\Z").next();
	}
	
	
	public String getMonitorById(String id) throws FileNotFoundException{
		
		//TODO: load dir from configuration file
		File path = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager/jobs/"
				+ id + "_exitStatus");
		
		return new Scanner(path).useDelimiter("\\Z").next();
	}
	
	
	
	
}
