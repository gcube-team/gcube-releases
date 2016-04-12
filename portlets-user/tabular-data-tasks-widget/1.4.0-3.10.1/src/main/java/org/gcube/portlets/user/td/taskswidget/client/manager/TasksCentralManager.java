package org.gcube.portlets.user.td.taskswidget.client.manager;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.manager.polling.CachedTaskId;
import org.gcube.portlets.user.td.taskswidget.client.manager.polling.TaskPolling;
import org.gcube.portlets.user.td.taskswidget.client.panel.TaskPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.TaskViewer;
import org.gcube.portlets.user.td.taskswidget.client.panel.TaskViewerIterface;
import org.gcube.portlets.user.td.taskswidget.client.panel.TdTaskManagerMainPanel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel.ColumnConfigTdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;


public class TasksCentralManager {
	
	private HashMap<String, TaskViewerIterface> hashTaskViewer;

	private HashMap<String, TaskPolling> cacheTaskIds = new HashMap<String, TaskPolling>();
	
	private TdTaskManagerMainPanel taskMangerMainPanel;

	public TasksCentralManager(TdTaskManagerMainPanel mainPanel) {
		hashTaskViewer = new HashMap<String, TaskViewerIterface>();
		this.taskMangerMainPanel = mainPanel;
	}


	/**
	 * 
	 * @param listTasksModel
	 * @return
	 */
	public boolean addListTask(List<TdTaskModel> listTasksModel){

		for(TdTaskModel task : listTasksModel)
			updateTask(task, false);

		taskMangerMainPanel.layout();
		taskMangerMainPanel.updateDispalyingAndSeeMore();
		
		return true;
		
	}
	
	public void maskMainPanel(boolean bool){
		if(bool)
			taskMangerMainPanel.mask("Loading", "x-mask-loading");
		else
			taskMangerMainPanel.unmask();
	}
	
	

	/**
	 * 
	 * @param taskModel
	 * @return true if is update, false otherwise
	 */
	public boolean updateTask(final TdTaskModel taskModel, boolean updateLayout){
		
		System.out.println("taskModel:" +taskModel);
		
		TaskViewerIterface taskViewer = hashTaskViewer.get(taskModel.getTaskId());
		
		System.out.println("taskViewer : "+taskViewer);
		System.out.println("hashTaskViewer : "+hashTaskViewer.size());
		
		if(taskViewer!=null){ //is update

			taskViewer.getTaskInfo().updateField(taskModel);
			
			JobsManager jobManger = taskViewer.getJobsManager();
			jobManger.updateListJob(taskModel.getListJobs());

			taskViewer.getResultManager().updateResults(taskModel.getListCollateralTRModel(), taskModel.getTdTableModel());
			
			if(taskModel.isCompleted()){ //if task is completed
				stopPolling(taskViewer.getTaskId());
//				hashTaskViewer.remove(taskModel.getTaskId()); //remove TaskViewer if exists
			}
			
			if(updateLayout)
				taskMangerMainPanel.layout();
			
			return false;

		}
		else{ //create new Task Panel
			
			if(taskModel.getTaskId()!=null && !taskModel.getTaskId().isEmpty()){

				TaskPanel taskPanel = new TaskPanel();

				//New task manger
				TaskManager taskManager = new TaskManager(taskModel);
			
//				ColumnConfigTdJobModel[] config = {TdJobModel.ColumnConfigTdJobModel.Classifier, TdJobModel.ColumnConfigTdJobModel.Type, TdJobModel.ColumnConfigTdJobModel.Progress , TdJobModel.ColumnConfigTdJobModel.StatusIcon, TdJobModel.ColumnConfigTdJobModel.Time};
				
				ColumnConfigTdJobModel[] config = {TdJobModel.ColumnConfigTdJobModel.Type, TdJobModel.ColumnConfigTdJobModel.Progress, TdJobModel.ColumnConfigTdJobModel.StatusIcon, TdJobModel.ColumnConfigTdJobModel.OperationInfo, TdJobModel.ColumnConfigTdJobModel.ValidationJobs};
				JobsManager jobsManager = new JobsManager(config);
				jobsManager.updateListJob(taskModel.getListJobs());
	
				//updating view
				taskPanel.addTaskInfoContainer(taskManager.getTaskInfo(), false);
//				//updating view
				taskPanel.addJobInfoContainer(jobsManager.getJobInfoPanel(), false);
				
				//Instancing new Result Loader and update listener
				ResultsLoader loader = new ResultsLoader();
				loader.addListner(taskPanel);
				//Instancing new ResultManger
				ResultsManager resultManager = new ResultsManager(loader);
				resultManager.updateResults(taskModel.getListCollateralTRModel(), taskModel.getTdTableModel());
				
				//updating view results
//				if(taskModel.getTdTableModel()!=null)
				taskPanel.addResultTabularContainer(resultManager.getResTabularDataPanel());

//				if(taskModel.getListCollateralTRModel()!=null && taskModel.getListCollateralTRModel().size()>0)
				taskPanel.addResultCollateralContainer(resultManager.getResCollateralTablePanel());
				
				taskMangerMainPanel.addTaskPanel(taskPanel);
		
				taskViewer = new TaskViewer(taskModel.getTaskId(), taskManager, jobsManager, resultManager, taskModel.isCompleted());
				
				//Start Polling if task is not completed
				if(!taskModel.isCompleted()){
					startPolling(taskModel.getTaskId());
				}

				hashTaskViewer.put(taskModel.getTaskId(), taskViewer); //add TaskViewer into hash
				
//				if(taskModel.isCompleted())
//					hashTaskViewer.remove(taskModel.getTaskId()); //remove TaskViewer if exists
			}else
				return false;

			
			if(updateLayout)
				taskMangerMainPanel.layout();
			
			return true;
		}
	}
	
	
	/**
	 * @param taskId
	 */
	public synchronized void stopPolling(String taskId) {
		
		TaskPolling cacheTimer = cacheTaskIds.get(taskId);
		
		if(cacheTimer==null)
			return;
		
		cacheTimer.stopPolling();
		cacheTaskIds.remove(cacheTimer);
		
	}

	public synchronized void startPolling(String taskId){
		
		CachedTaskId cachedTask = new CachedTaskId(taskId);
		TaskPolling taskPolling = new TaskPolling(this, cachedTask);
		taskPolling.scheduleRepeating(10000);
		taskPolling.startPolling();
		cacheTaskIds.put(taskId, taskPolling);
	}
	
	

	/**
	 * 
	 * @param hashHPKey
	 */
	public void removeTaskFromCacheForId(String hashHPKey) {
		
		hashTaskViewer.remove(hashHPKey); //remove task panel from cache if exists
		cacheTaskIds.remove(new CachedTaskId(hashHPKey));
	}
	
	
	public void reset(){
		hashTaskViewer.clear();
		taskMangerMainPanel.reset();
		cacheTaskIds.clear();
	}


	public TdTaskManagerMainPanel getFilledTaskMangerMainPanel() {
		return taskMangerMainPanel;
	}
	
}
