package org.gcube.common.messaging.endpoints;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;

/**
 * 
 * @author andrea
 *
 */
public class ScheduledRetriever {

	
	EndpointRetriever retriever = null;
	
	ScheduledExecutorService scheduler = null;
	
	public ScheduledRetriever (String scope,long intervalTime){
		retriever =  new EndpointRetriever(scope);
		ScopeProvider.instance.set(scope);
		  scheduler =
		      Executors.newSingleThreadScheduledExecutor();
		
		 scheduler.scheduleWithFixedDelay(retriever,0,intervalTime, TimeUnit.SECONDS);  
		 
		Runtime.getRuntime().addShutdownHook(new Thread() {
				    public void run() {
				    	scheduler.shutdownNow();
				    }
				});
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public void setScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}

	public ArrayList<String> getEndpoints (){
		return retriever.getEndpoints();
	}
	
	public String getFailoverEndpoint(){
		
		String endpointString = "";
		for (String endpoint : retriever.getEndpoints())
		{
			if (endpointString.isEmpty())
				endpointString+="("+endpoint;
			else
				endpointString+=","+endpoint;
		}
		endpointString+=")";
		return Constants.failoverPrefix+endpointString;
		
	}
}
