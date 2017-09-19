package org.gcube.dataanalysis.dataminer.poolmanager.service;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckMethod;
import org.gcube.dataanalysis.dataminer.poolmanager.util.NotificationHelper;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SendMail;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ServiceConfiguration;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;

public class StagingJob extends DMPMJob {

	private Algorithm algorithm;
	private Cluster stagingCluster;
	// private Cluster rProtoCluster;
	private String rProtoVREName;
	private String env;
	private String category;
	private String algorithm_type;


	public StagingJob(SVNUpdater svnUpdater, Algorithm algorithm,
			Cluster stagingCluster, /* Cluster rProtoCluster, */
			String rProtoVREName, String category, String algorithm_type, String env) throws FileNotFoundException, UnsupportedEncodingException {
		super(svnUpdater);
		this.jobLogs = new File(
				System.getProperty("user.home") + File.separator + "dataminer-pool-manager" + File.separator + "jobs");
		this.jobLogs.mkdirs();

		this.algorithm = algorithm;
		this.stagingCluster = stagingCluster;
		// this.rProtoCluster = rProtoCluster;
		this.rProtoVREName = rProtoVREName;
		this.env = env;
		this.category = category;
		this.algorithm_type = algorithm_type;


		//File m = new File(this.jobLogs + File.separator + this.id + "_exitStatus");
		//PrintWriter writer = new PrintWriter(m, "UTF-8");
		this.getStatus(0);
		//writer.close();

	}

	@Override
	protected void execute() {
		ServiceConfiguration a = new ServiceConfiguration();
		CheckMethod b = new CheckMethod();
		SendMail sm = new SendMail();
		NotificationHelper nh = new NotificationHelper();

		try {

			Collection<String> undefinedDependencies = this.svnUpdater.getUndefinedDependencies(
					this.svnUpdater.getDependencyFile(this.algorithm.getLanguage(),env),
					this.algorithm.getDependencies());

			if (!undefinedDependencies.isEmpty()) {

				String message = "Following dependencies are not defined:\n";
				for (String n : undefinedDependencies) {
					message += "\n" + n +"\n";
				}
				this.getStatus(2);

				sm.sendNotification(nh.getFailedSubject() +" for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody(message+"\n\n"+this.buildInfo()));
				return;
			}

			//before the installation to check if the files exist
			b.deleteFiles(this.algorithm, env);;
			
			int ret = this.executeAnsibleWorker(createWorker(this.algorithm, this.stagingCluster, false, "root"));


			if (ret != 0) {
				this.getStatus(2);
				sm.sendNotification(nh.getFailedSubject() + " for "+this.algorithm.getName()+ " algorithm", nh.getFailedBody("Installation failed. Return code=" + ret)+"\n\n"+this.buildInfo());
				return;
			}

			if (ret == 0) {
				this.getStatus(0);
				System.out.println("1 - Checking existing in env: "+ env);
				
				System.out.println("2 - Checking existing in env: "+ this.env);


				if (b.checkMethod(a.getHost(env), SecurityTokenProvider.instance.get())&&(b.algoExists(this.algorithm, env))) {

					System.out.println("Interface check ok!");
					System.out.println("Both the files exist at the correct path!");

					this.svnUpdater.updateSVNAlgorithmList(this.algorithm, this.rProtoVREName,this.category, this.algorithm_type,
							this.algorithm.getFullname(), env);

					this.getStatus(9);
					sm.sendNotification(nh.getSuccessSubject() + " for "+this.algorithm.getName()+ " algorithm", nh.getSuccessBody("\n\n"+this.buildInfo()));
					return;
				} else
					this.getStatus(2);
				sm.sendNotification(nh.getFailedSubject() + " for "+this.algorithm.getName()+ " algorithm",
						nh.getFailedBody(
								"\n"+
								"Installation completed but DataMiner Interface not working correctly or files "
										+ this.algorithm.getName() + ".jar and " + this.algorithm.getName()
										+ "_interface.jar not availables at the expected path")+"\n\n"+this.buildInfo());
				return;

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

	
	public String buildInfo(){
		ServiceConfiguration a = new ServiceConfiguration();
		return
				"\n"+
				"Algorithm details:\n"+"\n"+
				"User: "+this.algorithm.getFullname()+"\n"+
				"Algorithm name: "+this.algorithm.getName()+"\n"+
				"Staging DataMiner Host: "+ a.getHost(this.env)+"\n"+
				"Caller VRE: "+ScopeProvider.instance.get()+"\n"+
				"Target VRE: "+rProtoVREName+"\n";
	}


	
}
