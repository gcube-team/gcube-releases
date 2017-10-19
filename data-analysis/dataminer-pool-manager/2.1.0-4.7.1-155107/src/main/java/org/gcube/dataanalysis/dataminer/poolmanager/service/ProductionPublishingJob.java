package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckPermission;
import org.gcube.dataanalysis.dataminer.poolmanager.util.NotificationHelper;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SendMail;

public class ProductionPublishingJob extends DMPMJob {
	
	private Algorithm algorithm;
	//private Cluster prodCluster;
	private String targetVREName;
	private String category;
	private String algorithm_type;
	//private String targetVREToken;
	//private String env;


	public ProductionPublishingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			/*Cluster prodCluster,*/ String targetVREName, String category,String algorithm_type/*, String env*/) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater);
		this.algorithm = algorithm;
		//this.prodCluster = prodCluster;
		this.targetVREName = targetVREName;
		this.category = category;
		this.algorithm_type = algorithm_type;
		//this.targetVREToken = targetVREToken;
		//this.env= env;
		
		
		this.jobLogs = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager" + File.separator + "jobs");
		this.jobLogs.mkdirs();
	}
		
	@Override
	protected void execute() {
		SendMail sm = new SendMail();
		NotificationHelper nh = new NotificationHelper();
		try {
						
			//if (CheckPermission.apply(targetVREToken,targetVREName)){
				
				//this.svnUpdater.updateProdDeps(this.algorithm);
				this.svnUpdater.updateSVNProdAlgorithmList(this.algorithm, this.targetVREName, this.category,this.algorithm_type, this.algorithm.getFullname()/*, env*/);
				this.getStatus(9);
				sm.sendNotification(nh.getSuccessSubjectRelease() + " for "+this.algorithm.getName()+ " algorithm", nh.getSuccessBodyRelease("\n\n"+this.buildInfo()));
				return;
			//}
			//else this.getStatus(0);
			//sm.sendNotification(nh.getFailedSubjectRelease() + " for "+this.algorithm.getName()+ " algorithm", nh.getFailedBodyRelease(" The user "+this.algorithm.getFullname()+ " is not authorized to access to the "+ targetVREName+ " VRE"+"\n\n"+this.buildInfo()));
			//return;
//			int ret = this.executeAnsibleWorker(
//					createWorker(this.algorithm, this.prodCluster, false, "gcube"));
		
		} catch (Exception e) {
			try {
				this.getStatus(0);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}		
	}

	
	public String getStatus(int exitstatus) throws FileNotFoundException, UnsupportedEncodingException {

		File m = new File(this.jobLogs + File.separator + this.id+"_exitStatus");
		PrintWriter writer = new PrintWriter(m, "UTF-8");

		String response = "";

		if (exitstatus == 9) {
			response = "COMPLETED";
			writer.println(response);
		}
		
		if (exitstatus == 0) {
			response = "FAILED";
			writer.println(response);
			//writer.close();
		}
		
		
		writer.close();
		return response;
	}
	
	
public String buildInfo(){
		return
				"\n"+
				"Algorithm details:\n"+"\n"+
				"User: "+this.algorithm.getFullname()+"\n"+
				"Algorithm name: "+this.algorithm.getName()+"\n"+
				"Caller VRE: "+ScopeProvider.instance.get()+"\n"+
				"Target VRE: "+targetVREName+"\n";
	}
	
	
	
	
}
