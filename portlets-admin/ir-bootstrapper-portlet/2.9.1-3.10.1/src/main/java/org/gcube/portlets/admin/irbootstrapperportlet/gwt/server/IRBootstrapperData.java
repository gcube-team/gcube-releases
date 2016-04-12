/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.threads.TaskExecutionListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.threads.TaskToExecute;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class IRBootstrapperData {

	/** The singleton {@link IRBootstrapperData} object */
	private static IRBootstrapperData instance = null;
	
	/** A map that contains a reference to a {@link BootstrappingConfiguration} object per scope */
	private Map<String, BootstrappingConfiguration> configurations;
	
	/** The thread pool that is used in order to execute jobs */
	private ExecutorService threadPool;
	
	/** A map containing (taskUID, Task) pairs that describe the currently running tasks */
	private Map<String, TaskToExecute> runningTasks;
	
	/** The administrator's email */
	private String adminEmail;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(IRBootstrapperData.class);
	
	/**
	 * Protected class constructor, for implementing the singleton pattern
	 */
	protected IRBootstrapperData() { 
		configurations = new HashMap<String, BootstrappingConfiguration>();
		threadPool = Executors.newCachedThreadPool();
		runningTasks = new HashMap<String, TaskToExecute>();
	}

	/**
	 * Returns the singleton instance of this class.
	 * @param scope the scope for which 
	 * @return
	 */
	public static IRBootstrapperData getInstance() {
		if(instance == null)
			instance = new IRBootstrapperData();
		return instance;
	}

	/**
	 * Set the {@link BootstrappingConfiguration} object for a given scope
	 * @param scope the scope
	 * @param conf the {@link BootstrappingConfiguration} object
	 */
	public void setBootstrappingConfiguration(String scope, BootstrappingConfiguration conf) {
		configurations.put(scope, conf);
	}
	
	/**
	 * Returns the {@link BootstrappingConfiguration} for a given scope
	 * @param scope the scope
	 * @return the {@link BootstrappingConfiguration} object for the given scope
	 */
	public BootstrappingConfiguration getBootstrappingConfiguration(String scope) {
		return configurations.get(scope);
	}
	
	/**
	 * Sets the administrator's email address
	 * @param adminEmail
	 */
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	
	/**
	 * Returns the administrator's email address
	 * @return
	 */
	public String getAdminEmail() {
		return this.adminEmail;
	}
	
	/**
	 * Submits a Runnable for execution in the thread pool and returns a TaskToExecute object
	 * representing the status of this Runnable's execution
	 * @param r the Runnable to execute
	 * @return the Future object representing the Runnable's execution status
	 */
	public TaskToExecute submitRunnableForExecution(final Runnable r, final String newTaskUID, final TaskExecutionListener listener) {
		TaskToExecute task = new TaskToExecute(r, newTaskUID, new TaskExecutionListener() {
			public void onTaskExecutionCompleted(String taskUID) {	
				logger.debug("Task with ID -> " + newTaskUID + " finished its execution");
				synchronized (runningTasks) {
					runningTasks.remove(taskUID);
				}
				listener.onTaskExecutionCompleted(taskUID);
			}
		});
		
		synchronized (runningTasks) {
			runningTasks.put(newTaskUID, task);
		}
		threadPool.execute(task);
		
		return task;
	}
	
	/**
	 * Cancels the tasks identified by the given taskIDs
	 * @param taskIDs
	 */
	public void cancelTasks(Collection<String> taskIDs) {
		synchronized (runningTasks) {
			for (String taskID :  taskIDs) {
				TaskToExecute task = runningTasks.get(taskID);
				if (task != null) {
					logger.debug("Task with ID -> " + taskID + " is cancelled");
					task.cancel(true);
					runningTasks.remove(taskID);
				}
			}
		}
	}
	
	/**
	 * Submits a collection of Runnables for execution in the thread pool and 
	 * blocks until all of them are over (either with success or failed).
	 * representing the status of this Runnable's execution
	 * @param runnables the Runnables to execute
	 */
	public void submitRunnablesForExecutionAndBlock(Collection<Runnable> runnables, Collection<String> newTaskUIDs) {
		if (runnables.size() != newTaskUIDs.size())
			return;
		
		final CountDownLatch latch = new CountDownLatch(runnables.size());
		Iterator<Runnable> rIter = runnables.iterator();
		Iterator<String> sIter = newTaskUIDs.iterator();

		TaskExecutionListener taskExecListener = new TaskExecutionListener() {

			public void onTaskExecutionCompleted(String taskUID) {
				synchronized(runningTasks) {
					runningTasks.remove(taskUID);
				}
				latch.countDown();
			}
			
		};
		
		/* Create a new task for each runnable and assign the corresponding taskID to it */
		while (rIter.hasNext()) {
			Runnable r = rIter.next();
			String newTaskUID = sIter.next();
			TaskToExecute task = new TaskToExecute(r, newTaskUID, taskExecListener);
			synchronized (runningTasks) {
				runningTasks.put(newTaskUID, task);
			}
			threadPool.execute(task);
		}
		
		try {
			latch.await();
		} catch (Exception e) {
			logger.error("Exception is thrown in task's submission", e);
		}
	}

}
