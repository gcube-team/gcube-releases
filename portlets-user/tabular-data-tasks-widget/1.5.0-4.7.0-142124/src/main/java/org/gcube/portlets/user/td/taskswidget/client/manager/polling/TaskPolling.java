/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.manager.polling;

import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;
import org.gcube.portlets.user.td.taskswidget.client.manager.TasksCentralManager;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 28, 2013
 *
 */
public class TaskPolling extends Timer{

	private int delaySchedule;
	private int delayRepeating;
	private CachedTaskId cachedTask;
	private TasksCentralManager taskCentralManger;
	
	private static final int MAX_FAULT_TENTATIVE = 2;
	
	private int updateFault = 0;
	 
	@Override
	public void run() {
		updateTaskFromService();
	}
	
	/**
	 * 
	 * @param mng
	 * @param cachedTaskId
	 */
	public TaskPolling(TasksCentralManager mng, CachedTaskId cachedTaskId){
		this.cachedTask = cachedTaskId;
		this.taskCentralManger = mng;
	}
	
	public void updateTaskFromService(){
		
		GWT.log("Polling for Task id: "+cachedTask.getId() +" sending request of updating");
		
		TdTaskController.tdTaskService.getTdTaskForId(cachedTask.getId(), new AsyncCallback<TdTaskModel>() {
			
			@Override
			public void onSuccess(TdTaskModel result) {
				taskCentralManger.updateTask(result, true);
				updateFault = 0;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
				updateFault++;
				
				if(updateFault<=MAX_FAULT_TENTATIVE)
					Info.display("Error", "Sorry, an error occurred on updating task "+cachedTask.getId()+", another try will be submitted");
				else{
					Info.display("Error", "Sorry, an error occurred on updating task "+cachedTask.getId()+", the maximum trials are reached, stop updating");
					stopPolling();
				}
				
			}
		});
	}

	
	public void stopPolling(){
		GWT.log("Polling for Task id: "+cachedTask.getId() +" is stopped");
		cancel();
	}
	
	public void startPolling(){
		GWT.log("Polling for Task id: "+cachedTask.getId() +" is running");
		run();
	}
	
	public void scheduleRepeatingPolling(int milliseconds){
		this.delayRepeating = milliseconds;
		scheduleRepeating(delayRepeating);
	}
	
	public void schedulePolling(int milliseconds){
		this.delaySchedule = milliseconds;
		schedule(delaySchedule);
	}

	public int getDelaySchedule() {
		return delaySchedule;
	}

	public void setDelaySchedule(int delaySchedule) {
		this.delaySchedule = delaySchedule;
	}

	public int getDelayRepeating() {
		return delayRepeating;
	}

	public void setDelayRepeating(int delayRepeating) {
		this.delayRepeating = delayRepeating;
	}

}
