package org.gcube.dataanalysis.executor.job.management;

import org.apache.log4j.Logger;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.plugin.PluginState;

public class WorkerWatcher {
	private static int maxTries = 15;
	private int currentTries;

	Logger logger;
	SmartExecutorProxy proxy;
	String excecutionIdentifier;
	
	public WorkerWatcher(SmartExecutorProxy proxy, String excecutionIdentifier, Logger logger){
		this.proxy = proxy;
		this.excecutionIdentifier = excecutionIdentifier;
		this.logger = logger;
		currentTries = 1;
	}
	
	public PluginState getState(){
		try{
			return proxy.getState(excecutionIdentifier);
		}catch(Exception  e){
			logger.error("Error in getting state: recover try number "+currentTries,e);
			currentTries++;
			if (currentTries>maxTries){
				return PluginState.FAILED;
			}
			else return PluginState.RUNNING;
		}
		
	}
	
}
