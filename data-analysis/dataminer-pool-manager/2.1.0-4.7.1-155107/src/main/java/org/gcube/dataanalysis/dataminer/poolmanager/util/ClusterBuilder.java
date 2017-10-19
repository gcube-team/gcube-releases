package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.HAProxy;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;

public class ClusterBuilder {

	
	//1. to complete
	public static Cluster getStagingDataminerCluster() throws FileNotFoundException{
		Cluster cluster = new Cluster();
		ServiceConfiguration p = new ServiceConfiguration();
		Host h = new Host();
		h.setName(p.getStagingHost());
		cluster.addHost(h);
		
//		if (env.equals("Dev")){
//			h.setName(p.getDevStagingHost());
//			cluster.addHost(h);
//		}
//		
//		if ((env.equals("Prod")||(env.equals("Proto")))){
//			h.setName(p.getProtoProdStagingHost());
//			cluster.addHost(h);
//		}
		
		return cluster;
	}
	
	public static Cluster getVRECluster(String targetVREToken, String targetVRE) throws IOException{
		Cluster cluster = new Cluster();
		for (Host h : new HAProxy().listDataMinersByCluster(targetVREToken,targetVRE)) {
			cluster.addHost(h);
		}
		return  cluster;
	}
	
	public static Cluster getRProtoCluster() throws IOException{
		//Assumes the service is running in RPrototypingLab

		String token = SecurityTokenProvider.instance.get();
		String targetVRE = ScopeProvider.instance.get();
		
		return getVRECluster(token, targetVRE);
	}
	
	
	
}
