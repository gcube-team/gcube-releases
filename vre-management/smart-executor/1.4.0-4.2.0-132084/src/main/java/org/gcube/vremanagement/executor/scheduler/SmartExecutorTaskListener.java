/**
 * 
 */
package org.gcube.vremanagement.executor.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class SmartExecutorTaskListener implements JobListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void jobToBeExecuted(JobExecutionContext context) {
		//SmartExecutorJob smartExecutorJob = (SmartExecutorJob) context.getJobInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void jobExecutionVetoed(JobExecutionContext context) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		SmartExecutorTask smartExecutorJob = (SmartExecutorTask) context.getJobInstance();
		smartExecutorJob.finished(context);
	}

	
}
