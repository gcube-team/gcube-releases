package org.gcube.vremanagement.vremodeler.impl.deploy;

import java.util.Arrays;

import net.java.dev.jaxb.array.StringArray;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.eolusclient.Eolus;
import org.gcube.common.eolusclient.EolusServiceLocator;
import org.gcube.common.eolusclient.startContainer;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.utils.reports.GHNonCloudReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;



public class DeployGHNsOnCloud{

	private static GCUBELog logger= new GCUBELog(DeployGHNsOnCloud.class);
	
	int numberOfVMs;
	private String vreName;
	private GHNonCloudReport report;
	
	public DeployGHNsOnCloud(int numberOfVMs, String vreName) {
		super();
		this.numberOfVMs = numberOfVMs;
		this.vreName= vreName;
		this.report= new GHNonCloudReport();
		this.report.setStatus(Status.Waiting);
	}
	
	public GHNonCloudReport getReport() {
		return report;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] run() throws Exception{
		this.report.setStatus(Status.Running);
		String username = "gcube";
		String password = "gcube.cloud.2010";
		
		EolusServiceLocator servicelocator = new EolusServiceLocator();
		Eolus eolus = servicelocator.getEolusPort();
		
		((Stub) eolus)._setProperty(Call.USERNAME_PROPERTY, username);
		((Stub) eolus)._setProperty(Call.PASSWORD_PROPERTY, password);
			
		//filling the report
		report.setDeployingState(new Status[numberOfVMs]);
		Arrays.fill(report.getDeployingState(), Status.Waiting);
		
		VM[] hostnames=createVMs(eolus);
		configureAndStartContainers(eolus, hostnames);
		
		//waiting few seconds for GHN registration
		Thread.sleep(60000);
		checkGHNAvailability(hostnames);
		
		//now we can be sure that the GHNs are registered on the IS
		//and the return type can be created
				
		String[] ghns= new String[hostnames.length];
		for (int i=0; i<hostnames.length; i++)
			ghns[i]=hostnames[i].getName();
		
		report.setStatus(Status.Finished);
		return ghns;
	}
	
	private VM[] createVMs(Eolus eolus) throws Exception{
		String[] nets = {"public"};
		StringArray vnets = new StringArray();
		vnets.setItem(nets);
				
		//TODO: change this code to support more VMs templates
		String template=eolus.getTemplates().getItem()[0];
		logger.trace("got the template: "+template);
		VM[] vmsNames= new VM[numberOfVMs];
		//the first VM will be set with limited resource (1Gb of ram) for the RM 
		eolus.createVM(template, vreName+"ResourceManager", 2, 1024, vnets);
		vmsNames[0]= new VM(vreName+"ResourceManager");
		logger.trace("creating the first VM for ResourceManager "+vreName+"ResourceManager");
		report.getDeployingState()[0]=Status.Running;
		//the others VMs will be created with 2 GB of ram
		for (int i=1; i<numberOfVMs; i++){
			eolus.createVM(template, vreName+i, 2, 2048, vnets);
			vmsNames[i]= new VM(vreName+i);
			report.getDeployingState()[i]=Status.Running;
			logger.trace("creating the VM"+i+"  "+ vreName+i);
		}
		
		logger.trace("checking the VMs Availability");
		
		//waiting few seconds
		Thread.sleep(30000);
				
		//check if the VMs are ready
		boolean[] arrayCheck= new boolean[numberOfVMs];
		Arrays.fill(arrayCheck, false);
		while (!and(arrayCheck)){
			//waiting few seconds
			Thread.sleep(30000);
			
			//TODO: this cycle cannot continue forever
			for (int i=0; i<numberOfVMs; i++){
				try{
					if (arrayCheck[i]) continue;
					String vmState= eolus.getVMStatus(vmsNames[i].getName());
					if(vmState.equalsIgnoreCase("running")){
						arrayCheck[i]=true;
						vmsNames[i].setIp(eolus.getVMIP(vmsNames[i].getName()));
						//report.getDeployingState().set(i, State.Finished);
						logger.trace("the VM "+vmsNames[i].getName()+" is RUNNING with ip "+vmsNames[i].getIp());
					} 
					//else if(!vmState.equalsIgnoreCase("staging"));
				}catch (Exception e) {
					//if one fails i cannot continue
					logger.error("error deploying "+vmsNames[i].getName(),e);
					report.getDeployingState()[i]=Status.Failed;
					throw e;
				}
			}
		}
		//waiting 1 minute for the boot of the VMs
		Thread.sleep(180000);
		
		return vmsNames;
	}
	
	private void configureAndStartContainers(Eolus eolus, VM[] hostnames) throws Exception {
		/*
		String[] wgetRes;
		for (int i=0; i<hostnames.length; i++){
			wgetRes = eolus.execCMD("wget --no-check-certificate  http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/ghn-distribution/ServiceMaps/ServiceMap_devNext.xml -O /root/gCore/config/ServiceMap_devNext.xml", hostnames[i].getName()).getItem();
			if (wgetRes.length > 1)
				System.out.println("Stdout: "+wgetRes[0]);				
			if (wgetRes.length > 2){
				this.report.getDeployingState()[i]= State.Failed;
				logger.trace("std error "+wgetRes[1]);
			}
		}*/
		
		//waiting few seconds before start the container
		Thread.sleep(30000);
		
		String[] scopes=ScopeProvider.instance.get().split("/");
		String cmdtorun = "configureGHN.sh "+scopes[1]+" "+scopes[2];
		logger.trace("configuring ghns with command "+cmdtorun);
		String[] res;
		for (int i=0; i<hostnames.length; i++){
			res = eolus.execCMD(cmdtorun, hostnames[i].getName()).getItem();
			logger.trace("configuring the GHN on host for VM "+hostnames[i].getName()+" got the following report:");
			logger.trace("std output "+res[0]);
			//terrible... waiting for enhancement
			if (res.length > 2) {
				this.report.getDeployingState()[i]= Status.Failed;
				logger.trace("std error "+res[1]);
			}
		}
		
		//waiting few seconds before start the container
		Thread.sleep(60000);
		//starting the containers
		for (int i=0; i<hostnames.length; i++){
			Thread startcontainerthread = new startContainer(eolus,hostnames[i].getName());
			startcontainerthread.start();
		}
		
	}
	
	private void checkGHNAvailability(VM[] hostnames) throws Exception{
		boolean[] arrayCheck= new boolean[hostnames.length];
		Arrays.fill(arrayCheck, false);
		while (!and(arrayCheck)){
			logger.trace("checking published GHNs");
			Thread.sleep(40000);
			
			for (int i=0; i<hostnames.length; i++){
				if (arrayCheck[i]) continue;
				/*
				queryRes= DBInterface.queryDB("select id from ghn where host LIKE '"+hostnames[i].getIp()+"%'");
				if (queryRes.next()) {
					hostnames[i].setGhnId(queryRes.getString(1));
					logger.trace("the host "+hostnames[i].getName()+" has been retrieved with ghn id "+hostnames[i].getGhnId());
					arrayCheck[i]=true;
					this.report.getDeployingState()[i]= State.Finished;
					DBInterface.ExecuteUpdate("update GHN set isoncloud='true' where id='"+hostnames[i].getGhnId()+"'");
				}*/
			}
		}
	}
	
	private boolean and(boolean ... array){
		for (int i=0; i<array.length; i++)
			if(!array[i]) return false;
		return true;
	}
	
	/**
	 * 
	 * @author lucio
	 *
	 */
	public class VM{
		private String ip;
		private String name;
		private String ghnId;
		
		/**
		 * 
		 * @param name 
		 */
		public VM(String name) {
			super();
			this.ip=null;
			this.ghnId=null;
			this.name = name;
		}
		
		/**
		 * 
		 * @return the ip
		 */
		public String getIp() {
			return ip;
		}
		
		/**
		 * 
		 * @param ip the VM's ip
		 */
		public void setIp(String ip) {
			this.ip = ip;
		}
		
		/**
		 * 
		 * @return the VM name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * 
		 * @param name the VM name
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * 
		 * @return the ghn ID
		 */
		public String getGhnId() {
			return ghnId;
		}
		
		/**
		 * 
		 * @param ghnId
		 */
		public void setGhnId(String ghnId) {
			this.ghnId = ghnId;
		}
	}
			
}
