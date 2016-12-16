package org.gcube.rest.index.service.jobs;

import org.gcube.elasticsearch.FullTextNode;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexReplicationUpdater implements org.quartz.Job {

	private static final Logger logger = LoggerFactory.getLogger(IndexReplicationUpdater.class);

	public IndexReplicationUpdater() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Running IndexReplicationUpdater");
		SchedulerContext schedulerContext = null;
		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e) {
			logger.warn("IndexReplicationUpdater could not get the indexClient instance. Cannot update replication!");
		}
		FullTextNode ftn = (FullTextNode) schedulerContext.get("ftn");
		ftn.smartUpdateReplication();
	}
}
