package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestsMonitor extends Thread {
	final static Logger logger= LoggerFactory.getLogger(RequestsMonitor.class);
	
	private boolean object;
	
	private static RequestsMonitor jobInstance=null;
	private static RequestsMonitor objInstance=null;
	
	
	public static RequestsMonitor get(boolean object){
		if(object){
			if(objInstance==null) objInstance=new RequestsMonitor(object);
			return objInstance;
		}else{
			if(jobInstance==null) jobInstance=new RequestsMonitor(object);
			return jobInstance;
		}
	}
	
	private RequestsMonitor(boolean object) {
		super((object?"OBJ":"JOB")+"_REQUESTS_MONITOR");
		this.object=object;
	}
	
	
	@Override
	public void run() {
		while(true){
			try{
				List<Submitted> foundList=JobExecutionManager.getAvailableRequests(object,1);
				if(foundList.size()>0)logger.debug("FOUND "+foundList.size()+" PENDING "+(object?"OBJ":"JOB")+" REQUESTS ");
				for(Submitted found:foundList){
						logger.trace("handling pending "+(object?"OBJ ":"JOB ")+found.getTitle()+", ID : "+found.getSearchId());
						try{
							JobExecutionManager.start(found);
						}catch(Exception e){
							logger.error("Unable to execute "+found,e);
							if(found!=null){
								SubmittedManager.updateStatus(found.getSearchId(),SubmittedStatus.Error);
							}
						}
				}
			}catch(Exception e){
				logger.error("Unexpected exception", e);
			}finally{
				try{
					Thread.sleep(2*1000);
				}catch(InterruptedException e){}
			}
		}
	}
	
}
