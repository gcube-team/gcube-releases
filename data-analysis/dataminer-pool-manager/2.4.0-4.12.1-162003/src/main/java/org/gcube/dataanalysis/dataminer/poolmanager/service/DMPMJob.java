package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.AnsibleWorker;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.service.exceptions.AnsibleException;
import org.gcube.dataanalysis.dataminer.poolmanager.service.exceptions.UndefinedDependenciesException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckMethod;
import org.gcube.dataanalysis.dataminer.poolmanager.util.NotificationHelper;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SendMail;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.DMPMException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.EMailException;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class DMPMJob {
	
	private Configuration configuration;
	private String dmpmHomeDirectory;
	private SVNUpdater svnUpdater;
	private File jobLogs;
	private String id;
	private Algorithm algorithm;
	private Cluster cluster;
	private String vREName;
	private String category;
	private String algorithm_type;
	private Logger logger;
	
	private enum STATUS
	{
		PROGRESS ("IN PROGRESS"),
		COMPLETED ("COMPLETED"),
		FAILED ("FAILED");
		
		private String status;
		
		STATUS (String status)
		{
			this.status = status;
		}
	}
	
	public DMPMJob(SVNUpdater svnUpdater,Configuration configuration,Algorithm algorithm, Cluster cluster,String vREName, 
			String category, String algorithm_type){
		this.logger = LoggerFactory.getLogger(DMPMJob.class);
	
		this.configuration = configuration;
		this.algorithm = algorithm;
		this.cluster = cluster;
		this.vREName = vREName;
		this.category = category;
		this.algorithm_type = algorithm_type;
		
		this.svnUpdater = svnUpdater;
		this.dmpmHomeDirectory = new String (System.getProperty("user.home")+File.separator+"dataminer-pool-manager"); 
		this.id = UUID.randomUUID().toString();
		
		//TODO: dmpm work directory should be loaded from configuration file
		this.jobLogs = new File(this.dmpmHomeDirectory+File.separator+"jobs");
		this.jobLogs.mkdirs();
	}

	
	public String start()
	{		
		setStatusInformation(STATUS.PROGRESS);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		return this.id;
	}
	
	protected AnsibleWorker createWorker(Algorithm algo,
			Cluster dataminerCluster,
			boolean includeAlgorithmDependencies,
			String user){
		AnsibleBridge ansibleBridge = new AnsibleBridge(this.dmpmHomeDirectory);
		try {
			return ansibleBridge.createWorker(algo, dataminerCluster, includeAlgorithmDependencies, user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void setStatusInformation(STATUS exitStatus) {
		
		
		try
		{
			File statusFile = new File (this.jobLogs,this.id + "_exitStatus");
			//File m = new File ( this.jobLogs + File.separator + this.id + "_exitStatus");
			PrintWriter writer = new PrintWriter(statusFile, "UTF-8");
			writer.println(exitStatus.status);
			writer.close();
		} catch (Exception e)
		{
			this.logger.error ("Unable to update exit status file with status "+exitStatus.status,e);
		}
		


	}
	
	private void updateLogFile (File logFile, String message)
	{
		try
		{
			PrintWriter writer = new PrintWriter(logFile,"UTF-8");
			writer.print(message);
			writer.close();
		} catch (Exception e)
		{
			this.logger.error("Unable to log the error message: "+message,e);
		}
		

	}
	
	protected abstract void execute ();
	
	private void preInstallation (SendMail sm,NotificationHelper nh, File logFile ) throws GenericException, EMailException,UndefinedDependenciesException
	{
		
		this.logger.debug("Checking dependencies...");
		Collection<String> undefinedDependencies = this.svnUpdater.getUndefinedDependencies(
				this.svnUpdater.getDependencyFile(this.algorithm.getLanguage()),
				this.algorithm.getDependencies());
		
		if (!undefinedDependencies.isEmpty()) 
		{
			this.logger.debug("Some dependencies are not defined");
			throw new UndefinedDependenciesException(undefinedDependencies);
			
		}
	
	}
	
	private String installation (SendMail sm,NotificationHelper nh,CheckMethod methodChecker,File logFile ) throws DMPMException
	{
		this.logger.debug("Installation process started");
		methodChecker.deleteFiles(this.algorithm/*, env*/);
		int ret = this.executeAnsibleWorker(createWorker(this.algorithm, this.cluster, false, "root"),logFile);
		this.logger.debug("Return code= "+ret);
		
		if (ret != 0) throw new AnsibleException(ret);
		
		else 
		{
			this.logger.debug("Operation completed");
			//this.setStatusInformation(STATUS.PROGRESS);
			this.logger.debug("Checking the method...");
			methodChecker.checkMethod(this.configuration.getHost(), SecurityTokenProvider.instance.get());
			methodChecker.copyAlgorithms(this.algorithm); 
			this.logger.debug("Method OK and algo exists");
			this.logger.debug("Interface check ok!");
			this.logger.debug("Both the files exist at the correct path!");
			boolean algorithmListResult = this.svnUpdater.updateSVNAlgorithmList(this.algorithm, this.vREName,this.category, this.algorithm_type,
					this.algorithm.getFullname());
			this.setStatusInformation(STATUS.COMPLETED);
			return algorithmListResult ?"":"\nWARNING: algorithm list could not be updated on SVN";
		}
	}
	
	protected void execute(NotificationHelper nh, CheckMethod methodChecker)
	{
		
		
		SendMail sm = new SendMail();
		File logFile = new File(this.jobLogs,this.id);

		try
		{
			
			try {

				this.logger.debug("Pre installation operations");
				preInstallation(sm, nh, logFile);
				this.logger.debug("Pre installation operation completed");
				this.logger.debug("Installation...");
				String warning = installation(sm, nh, methodChecker, logFile);
				this.logger.debug("Installation completed");
				this.logger.debug("Warning message "+warning);
				this.setStatusInformation(STATUS.COMPLETED);
				String bodyResponse = NotificationHelper.getSuccessBody(warning+"\n\n"+this.buildInfo());
				sm.sendNotification(nh.getSuccessSubject() + " for "+this.algorithm.getName()+ " algorithm", bodyResponse);
				

				
			} catch (DMPMException dmpme)
			{
				this.logger.error("Operation failed: "+dmpme.getMessage());
				this.logger.error("Exception: ",dmpme);			
				this.setStatusInformation(STATUS.FAILED);
				String errorMessage = "\n"+NotificationHelper.getFailedBody(dmpme.getErrorMessage()+"\n\n"+this.buildInfo());
				this.updateLogFile(logFile, errorMessage);
				sm.sendNotification(nh.getFailedSubject() +" for "+this.algorithm.getName()+ " algorithm", errorMessage);
				
			}
			
		} catch (EMailException eme)
		{
			this.logger.error("Unable to send notification email",eme);
		}
		

		
	

	}
	

	
	protected int executeAnsibleWorker(AnsibleWorker worker, File logFile) throws GenericException
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(logFile, true);
			PrintStream ps = new PrintStream(fos);	
			
//			File m = new File(this.jobLogs + File.separator + this.id + "_exitStatus");
//			PrintWriter fos2 = new PrintWriter(m, "UTF-8");
					
			return worker.execute(ps);
		} catch (Exception e)
		{
			throw new GenericException(e);
		}
		

	}
	
	public String buildInfo() {

		return
				"\n"+
				"Algorithm details:\n"+"\n"+
				"User: "+this.algorithm.getFullname()+"\n"+
				"Algorithm name: "+this.algorithm.getName()+"\n"+
				"Staging DataMiner Host: "+ this.configuration.getHost()+"\n"+
				"Caller VRE: "+ScopeProvider.instance.get()+"\n"+
				"Target VRE: "+this.vREName+"\n";
	}



}
