package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.HAProxy;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;

public abstract class ClusterBuilder {

	private Configuration configuration;
	
	public ClusterBuilder (Configuration configuration)
	{
		this.configuration = configuration;
	}
	
	//1. to complete
	public Cluster getDataminerCluster() throws FileNotFoundException{
		Cluster cluster = new Cluster();
		
		Host h = new Host();
		h.setName(this.configuration.getHost());
		cluster.addHost(h);
		

		
		return cluster;
	}
	

	
	
	
	public Cluster getVRECluster(String targetVREToken, String targetVRE) throws IOException{
		Cluster cluster = new Cluster();
		for (Host h : new HAProxy().listDataMinersByCluster(targetVREToken,targetVRE)) {
			cluster.addHost(h);
		}
		return  cluster;
	}
	
	public Cluster getRProtoCluster() throws IOException{
		//Assumes the service is running in RPrototypingLab

		String token = SecurityTokenProvider.instance.get();
		String targetVRE = ScopeProvider.instance.get();
		
		return this.getVRECluster(token, targetVRE);
	}
	
	
	
}
