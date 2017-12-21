package org.gcube.dataanalysis.dataminerpoolmanager;

import java.io.IOException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.util.AlgorithmBuilder;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.SVNUpdaterProduction;
import org.tmatesoft.svn.core.SVNException;

public class JobTest {
	
	public static void main(String[] args) throws SVNException, IOException, InterruptedException{
		
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");

		SVNUpdater svnUpdater = new SVNUpdaterProduction();
		Algorithm algo = AlgorithmBuilder.create("http://data.d4science.org/dENQTTMxdjNZcGRpK0NHd2pvU0owMFFzN0VWemw3Zy9HbWJQNStIS0N6Yz0");
		
		//test phase
		//Cluster stagingCluster = ClusterBuilder.getStagingDataminerCluster();
		//Cluster rProtoCluster = ClusterBuilder.getRProtoCluster();		
		//DMPMJob job = new StagingJob(svnUpdater, algo, stagingCluster, /*rProtoCluster,*/ ScopeProvider.instance.get());
		//job.start();
		
		//release phase
		//Cluster prodCluster = ClusterBuilder.getVRECluster(targetVREToken, targetVRE);		
		//DMPMJob job2 = new ProductionPublishingJob(svnUpdater, algo, prodCluster);	
		//job2.start();
		
	}

}
