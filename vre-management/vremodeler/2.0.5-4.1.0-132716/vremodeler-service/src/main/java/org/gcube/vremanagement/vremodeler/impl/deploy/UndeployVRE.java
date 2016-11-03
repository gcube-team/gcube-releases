package org.gcube.vremanagement.vremodeler.impl.deploy;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.impl.util.ResourceManagerPorts;
import org.gcube.vremanagement.vremodeler.impl.util.Util;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;



public class UndeployVRE extends Thread {

	private static GCUBELog logger = new GCUBELog(UndeployVRE.class);
	
	//private ModelerResource wsResource= null;
	
	private ResourceManagerPorts ports;
	
	String resourceId;
	public UndeployVRE(String resourceId){
		this.resourceId = resourceId;
	}
			
	public void run(){
		try{
			logger.trace("called undeploy with scope "+ServiceContext.getContext().getScope());
			Dao<VRE, String> vreDao=  DaoManager.createDao(DBInterface.connect(), VRE.class);
			VRE vre =vreDao.queryForId(resourceId);			
			if (vre==null) throw new Exception("vre with id "+resourceId+" not found");
			if (Status.valueOf(vre.getStatus())!=Status.Deployed) throw new Exception("the vre "+vre.getName()+" cannot be undeployed (the status isn't Deployed)");
			
			logger.trace("undeploying vre "+vre.getName());
			
			ports =ResourceManagerPorts.get(ServiceContext.getContext().getScope());
			
			String scopeToDispose = ServiceContext.getContext().getScope()+"/"+vre.getName();
			
			logger.trace("scope to dispose is "+scopeToDispose);
			
			String reportId = ports.getScopeController().disposeScope(scopeToDispose);
			
			String finalReport =getUndeployReport(reportId);
			
			logger.trace("UndeployReport; "+finalReport);			
			
			if (Util.isSomethingFailed(finalReport)) 
				throw new Exception("something is FAILED deploying the vre "+vre.getName());
			
			vre.setStatus(Status.Disposed.toString());
			vreDao.update(vre);
			
			logger.trace("the vre "+vre.getName()+" has been undeplyed");
			
		}catch (Exception e) {
			logger.error("error undeploying the VRE",e);
		}
	}
	
	private String getUndeployReport(String reportId) throws Exception{
		String report = null;
		do{
			try{
				Thread.sleep(20000);
			}catch (Exception e) {}
			report=ports.getReporter().getReport(reportId);
		}while (!(Util.isDeploymentStatusFinished(report)));
		return report;
		
	}
	
}
