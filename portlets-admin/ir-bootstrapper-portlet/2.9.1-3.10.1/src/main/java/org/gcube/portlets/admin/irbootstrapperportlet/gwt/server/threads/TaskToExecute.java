/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.threads;

import java.util.concurrent.FutureTask;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class TaskToExecute extends FutureTask<Void> {
	
	public TaskToExecute(final Runnable r, final String taskID, final TaskExecutionListener listener) {
		super(new Runnable() {
			
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				r.run();
				listener.onTaskExecutionCompleted(taskID);
			}
		}, null);
	}
}
