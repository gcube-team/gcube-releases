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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagingJob extends DMPMJob {

	private Algorithm algorithm;
	private Cluster stagingCluster;
	// private Cluster rProtoCluster;
	private String rProtoVREName;
	//private String env;
	private String category;
	private String algorithm_type;

	private Logger logger;

	public StagingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			Cluster stagingCluster, /* Cluster rProtoCluster, */
			String rProtoVREName, String category, String algorithm_type/*, String env*/) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater);
		this.logger = LoggerFactory.getLogger(StagingJob.class);
		
		this.jobLogs = new File(
				System.getProperty("user.home") + File.separator + "dataminer-pool-manager" + File.separator + "jobs");
		this.jobLogs.mkdirs();

		this.algorithm = algorithm;
		this.stagingCluster = stagingCluster;
		// this.rProtoCluster = rProtoCluster;
		this.rProtoVREName = rProtoVREName;
		//this.env = env;
		this.category = category;
		this.algorithm_type = algorithm_type;


		//File m = new File(this.jobLogs + File.separator + this.id + "_exitStatus");
		//PrintWriter writer = new PrintWriter(m, "UTF-8");
		this.getStatus(0);
		//writer.close();

	}

	@Override
	protected void execute() {

		this.logger.debug("Executing staging job...");
		
		CheckMethod methodChecker = new CheckMethod();
		SendMail sm = new SendMail();
		NotificationHelper nh = new NotificationHelper();

		try {

			this.logger.debug("Checking dependencies...");
			Collection<String> undefinedDependencies = this.svnUpdater.getUndefinedDependencies(
					this.svnUpdater.getDependencyFile(this.algorithm.getLanguage()/*,env*/),
					this.algorithm.getDependencies());

			if (!undefinedDependencies.isEmpty()) 
			{
				this.logger.debug("Some dependencies are not defined");
				String message = "Following dependencies are not defined:\n";
				for (String n : undefinedDependencies) {
					message += "\n" + n +"\n";
				}
				this.getStatus(2);

				sm.sendNotification(nh.getFailedSubject() +" for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody(message+"\n\n"+this.buildInfo()));
				return;
			}

			//before the installation to check if the files exist
			methodChecker.deleteFiles(this.algorithm/*, env*/);
			
			int ret = this.executeAnsibleWorker(createWorker(this.algorithm, this.stagingCluster, false, "root"));
			System.out.println("Return code= "+ret);

			if (ret != 0) 
			{
				this.logger.debug("Ansible work failed, return code "+ret);
				this.getStatus(2);
				sm.sendNotification(nh.getFailedSubject() + " for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody("Installation failed. Return code=" + ret)+"\n\n"+this.buildInfo());
				return;
			}

			if (ret == 0) 
			{
				this.logger.debug("Operation completed");
				this.getStatus(0);
				//System.out.println("1 - Checking existing in env: "+ env);
				
				//System.out.println("2 - Checking existing in env: "+ this.env);
				this.logger.debug("Checking the method...");


				if (methodChecker.checkMethod(DMPMClientConfiguratorManager.getInstance().getStagingConfiguration().getHost(), SecurityTokenProvider.instance.get())&&(methodChecker.algoExists(this.algorithm))) 
				{	
					this.logger.debug("Method OK and algo exists");
					System.out.println("Interface check ok!");
					System.out.println("Both the files exist at the correct path!");

					this.svnUpdater.updateSVNStagingAlgorithmList(this.algorithm, this.rProtoVREName,this.category, this.algorithm_type,
							this.algorithm.getFullname()/*, env*/);

					this.getStatus(9);
					sm.sendNotification(nh.getSuccessSubject() + " for "+this.algorithm.getName()+ " algorithm", nh.getSuccessBody("\n\n"+this.buildInfo()));
					return;
				} else
				{
					this.logger.debug("Operation failed");
					this.getStatus(2);
					sm.sendNotification(nh.getFailedSubject() + " for "+this.algorithm.getName()+ " algorithm",
						nh.getFailedBody(
								"\n"+
								"Installation completed but DataMiner Interface not working correctly or files "
										+ this.algorithm.getName() + ".jar and " + this.algorithm.getName()
										+ "_interface.jar not availables at the expected path")+"\n\n"+this.buildInfo());
				return;
				}
			}
		} catch (Exception e) {
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
				"Staging DataMiner Host: "+ DMPMClientConfiguratorManager.getInstance().getStagingConfiguration().getHost()+"\n"+
				"Caller VRE: "+ScopeProvider.instance.get()+"\n"+
				"Target VRE: "+rProtoVREName+"\n";
	}


	
}
