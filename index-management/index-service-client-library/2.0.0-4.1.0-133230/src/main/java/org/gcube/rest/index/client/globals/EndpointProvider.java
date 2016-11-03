package org.gcube.rest.index.client.globals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.gcube.rest.index.client.internals.EndpointsHelper;
import org.gcube.rest.index.client.tasks.UpdateEndpoints;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class EndpointProvider {

	private static final int UPDATE_EVERY_SECS = 300;
	private static final Logger logger = LoggerFactory.getLogger(EndpointProvider.class);

	private String scope;
	
	private Set<String> endpoints = null;

	private Scheduler scheduler = null;
	
	
	public EndpointProvider(String scope) {
		if(scheduler != null)
			try { scheduler.shutdown();	} catch (SchedulerException e) {}
		else{
			this.scope = scope;
			endpoints = Collections.synchronizedSet(new HashSet<String>());
			try {
				initScheduler();
			} catch (SchedulerException e) {
				logger.warn("Index client could not enable its smart update feature of the service endpoints of scope: "+scope);			
			}
			endpoints = EndpointsHelper.getEndpointsOfScope(scope);
		}
	}
	
	
	private void initScheduler() throws SchedulerException {
		
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDetail job = newJob(UpdateEndpoints.class)
		    .withIdentity("endpoint-updater-job-" + scope, "endpoint-updater-job")
		    .build();
		
		Trigger trigger = newTrigger()
		    .withIdentity("endpoint-updater-trigger" + scope, "endpoint-updater-trigger")
		    .startNow()
		          .withSchedule(simpleSchedule()
		          .withIntervalInSeconds(UPDATE_EVERY_SECS)
		          .repeatForever())
		    .build();
		
		scheduler.getContext().put("endpoints-" + scope, endpoints);
		job.getJobDataMap().put("scope", scope);
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
		
	}
	
	public int endpointsNumber(){
		return endpoints.size();
	}
	
	public String getAnEndpoint() {
		return endpoints.toArray(new String[endpoints.size()])[new Random().nextInt(endpoints.size())];
	}
	
	
	public void remove(String endpoint){
		endpoints.remove(endpoint);
	}
	
	
	public void stopForCurrentScope(){
		if(scheduler==null)
			return;
		try {
			boolean status = scheduler.deleteJob(jobKey("endpoint-updater-job-" + scope, "endpoint-updater-job"));
			logger.debug("Removing smart updater of endpoints of scope: "+scope + "... STATUS: "+ status);
		}
		catch (SchedulerException e) {
			logger.debug("Filed to stop the smart update feature of the service endpoints of scope: "+scope);	
		}
	}
	
	public void terminate(){
		if(scheduler==null)
			return;
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			logger.debug("Terminated smart update of the index endpoints feature");	
		}
	}
	
	
	
}

