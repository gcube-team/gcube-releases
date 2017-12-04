package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckMethod;
import org.gcube.dataanalysis.dataminer.poolmanager.util.NotificationHelper;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SendMail;

public class ProductionPublishingJob extends DMPMJob {
	
	private Algorithm algorithm;
	private Cluster prodCluster;
	private String targetVREName;
	private String category;
	private String algorithm_type;
	//private String targetVREToken;
	//private String env;


	public ProductionPublishingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			Cluster prodCluster, String targetVREName, String category,String algorithm_type/*, String env*/) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater);
		this.jobLogs = new File(
				System.getProperty("user.home") + File.separator + "dataminer-pool-manager" + File.separator + "jobs");
		this.jobLogs.mkdirs();
		this.algorithm = algorithm;
		this.prodCluster = prodCluster;
		this.targetVREName = targetVREName;
		this.category = category;
		this.algorithm_type = algorithm_type;
		//this.targetVREToken = targetVREToken;
		//this.env= env;
		this.getStatus(0);

	}
		
	@Override
	protected void execute() {
		SendMail sm = new SendMail();
		NotificationHelper nh = new NotificationHelper();
		CheckMethod b = new CheckMethod();
		try {
			
			Collection<String> undefinedDependencies = this.svnUpdater.getUndefinedDependencies(
					this.svnUpdater.getDependencyFileProd(this.algorithm.getLanguage()/*,env*/),
					this.algorithm.getDependencies());

			if (!undefinedDependencies.isEmpty()) {

				String message = "Following dependencies are not defined:\n";
				for (String n : undefinedDependencies) {
					message += "\n" + n +"\n";
				}
				this.getStatus(2);

				sm.sendNotification(nh.getFailedSubjectRelease() +" for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody(message+"\n\n"+this.buildInfo()));
				return;
			}
			b.deleteFilesProd(this.algorithm);

			int ret = this.executeAnsibleWorker(createWorker(this.algorithm, this.prodCluster, false, "root"));

			if (ret != 0) {
				this.getStatus(2);
				sm.sendNotification(nh.getFailedSubjectRelease() + " for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody("Installation failed. Return code=" + ret)+"\n\n"+this.buildInfo());
				return;
			}
			
			
			if (ret == 0) {
				this.getStatus(0);

				if (b.checkMethod(DMPMClientConfiguratorManager.getInstance().getProductionConfiguration().getHost(), SecurityTokenProvider.instance.get())&&(b.algoExistsProd(this.algorithm/*, env*/))) {

					System.out.println("Interface check ok!");
					System.out.println("Both the files exist at the correct path!");

					this.svnUpdater.updateSVNProdAlgorithmList(this.algorithm, this.targetVREName,this.category, this.algorithm_type,
							this.algorithm.getFullname()/*, env*/);

					this.getStatus(9);
					sm.sendNotification(nh.getSuccessSubjectRelease() + " for "+this.algorithm.getName()+ " algorithm", nh.getSuccessBody("\n\n"+this.buildInfo()));
					return;
				} else
					this.getStatus(2);
				sm.sendNotification(nh.getFailedSubjectRelease() + " for "+this.algorithm.getName()+ " algorithm",
						nh.getFailedBody(
								"\n"+
								"Installation completed but DataMiner Interface not working correctly or files "
										+ this.algorithm.getName() + ".jar and " + this.algorithm.getName()
										+ "_interface.jar not availables at the expected path")+"\n\n"+this.buildInfo());
				return;

			}
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

		File m = new File(this.jobLogs + File.separator + this.id + "_exitStatus");
		PrintWriter writer = new PrintWriter(m, "UTF-8");

		String response = "";

		if (exitstatus == 0) {
			response = "IN PROGRESS";
			writer.println(response);
			//writer.close();
		}

		if (exitstatus == 9) {
			response = "COMPLETED";
			writer.println(response);
			//writer.close();
		}

		if (exitstatus == 2) {
			response = "FAILED";
			writer.println(response);
			//writer.close();
		}
		writer.close();
		return response;
	}

	
	public String buildInfo() throws UnsupportedEncodingException{
		
		return
				"\n"+
				"Algorithm details:\n"+"\n"+
				"User: "+this.algorithm.getFullname()+"\n"+
				"Algorithm name: "+this.algorithm.getName()+"\n"+
				"Staging DataMiner Host: "+ DMPMClientConfiguratorManager.getInstance().getProductionConfiguration().getHost()+"\n"+
				"Caller VRE: "+ScopeProvider.instance.get()+"\n"+
				"Target VRE: "+this.targetVREName+"\n";
	}
	
	
	
}
