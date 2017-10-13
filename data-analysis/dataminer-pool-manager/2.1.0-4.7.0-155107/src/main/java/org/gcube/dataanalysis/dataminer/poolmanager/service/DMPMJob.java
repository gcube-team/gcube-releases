package org.gcube.dataanalysis.dataminer.poolmanager.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.AnsibleWorker;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.tmatesoft.svn.core.SVNException;


public abstract class DMPMJob {
	
	
	protected SVNUpdater svnUpdater;
	protected File jobLogs;
	
	protected String id;
	
	public DMPMJob(SVNUpdater svnUpdater){
		this.svnUpdater = svnUpdater;
		this.id = UUID.randomUUID().toString();
		
		//TODO: dmpm work directory should be loaded from configuration file
		this.jobLogs = new File(System.getProperty("user.home")+File.separator+"dataminer-pool-manager"+File.separator+"jobs");
	
		this.jobLogs.mkdirs();
	}

	
	public String start(){
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
		AnsibleBridge ansibleBridge = new AnsibleBridge();
		try {
			return ansibleBridge.createWorker(algo, dataminerCluster, includeAlgorithmDependencies, user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	protected abstract void execute();
	
	protected int executeAnsibleWorker(AnsibleWorker worker) throws IOException, InterruptedException, SVNException{
		File path = new File(worker.getWorkdir() + File.separator + "jobs");
		path.mkdirs();
		
		File n = new File(this.jobLogs + File.separator + this.id);
		FileOutputStream fos = new FileOutputStream(n, true);
		PrintStream ps = new PrintStream(fos);	
		
//		File m = new File(this.jobLogs + File.separator + this.id + "_exitStatus");
//		PrintWriter fos2 = new PrintWriter(m, "UTF-8");
				
		return worker.execute(ps);
	}

}
