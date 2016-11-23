package org.gcube.rest.index.client.tasks;

import java.util.Set;

import org.gcube.rest.index.client.internals.EndpointsHelper;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateEndpoints implements InterruptableJob {

	private static final Logger logger = LoggerFactory.getLogger(UpdateEndpoints.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		SchedulerContext schedulerContext = null;
		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e) {
			logger.warn("UpdateEndpoints could not get the current endpoint instances. Will not update endpoint list!");
		}
		String scope = context.getJobDetail().getJobDataMap().getString("scope");
		Set<String> endpoints = (Set<String>) schedulerContext.get("endpoints-"+scope);
		Set<String> latestEndpoints = EndpointsHelper.getEndpointsOfScope(scope);
		endpoints.clear();
		
		//java 7
		for(String ep: latestEndpoints)
			endpoints.add(ep);
		
		//java 8
//		latestEndpoints.forEach((ep)->{
//				endpoints.add(ep);
//			}
//		);
		
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		Thread.currentThread().interrupt();
	}
	
	
	
}
