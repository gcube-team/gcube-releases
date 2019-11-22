package org.gcube.portlets.user.td.taskswidget.client.manager;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.panel.result.FlexTableJob;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.JobInfoPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.JobTabularDataProgressBar;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel.ColumnConfigTdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskStatusType;


public class JobsManager {
	
	private JobInfoPanel jobInfoPanel;
	
	private HashMap<String, JobTabularDataProgressBar> hashProgressBars;

	private FlexTableJob flexTableJob;
	
	public JobsManager(ColumnConfigTdJobModel[] columnConfigTdJobModels) {
		hashProgressBars = new HashMap<String, JobTabularDataProgressBar>();
		flexTableJob = new FlexTableJob(columnConfigTdJobModels);
		jobInfoPanel = new JobInfoPanel(flexTableJob);
	}


	/**
	 * 
	 * @param taskModel
	 * @return
	 */
	public void updateListJob(List<TdJobModel> listJobs){
		
		if(listJobs==null)
			return;
		
		for(TdJobModel jobModel : listJobs)
			updateJob(jobModel);
	}
	
	
	public void updateJob(final TdJobModel jobModel){

		flexTableJob.updateStatus(jobModel);
		jobInfoPanel.layout();
		
	}
	

	public JobTabularDataProgressBar updateProgressBarView(JobTabularDataProgressBar jobsBar, TdJobModel jobModel){
		
		
			switch (jobModel.getStatus()) {
			
			case PENDING:{
				jobsBar.updateProgress(0);
				jobsBar.setCompleted(false);
				jobsBar.setProgressText(TdTaskStatusType.PENDING.toString());
				break;
			}
				
				
			case INITIALIZING:{
//				jobsBar.progressStop();
				jobsBar.updateProgress(0);
				jobsBar.setCompleted(false);
				jobsBar.setProgressText(TdTaskStatusType.INITIALIZING.toString());
				break;
			}
			
			
		/*	case FALLBACK: {
				jobsBar.getElement().getStyle().setBorderColor("#f00");
//				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(TdTaskStatusType.FALLBACK.toString());
				break;
			}*/
			
			case COMPLETED:{

				jobsBar.getElement().getStyle().setBorderColor("#000000");
//				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(TdTaskStatusType.COMPLETED.toString());

				break;
			}
			
			case VALIDATING:{
				
				jobsBar.setProgressText(TdTaskStatusType.VALIDATING_RULES.toString());
				jobsBar.updateProgress(jobModel.getProgressPercentage());
	
				break;
			}
			
			
			case RUNNING:{ 

				jobsBar.setProgressText(TdTaskStatusType.RUNNING.toString());
				jobsBar.updateProgress(jobModel.getProgressPercentage());
	
				break;
			}
			
			case FAILED:{ 					
	
				jobsBar.getElement().getStyle().setBorderColor("#f00");
//				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(TdTaskStatusType.FAILED.toString());
		
				break;
			}
			
		}
			
		return jobsBar;
		
	}

	public void removeJob(String hashHPKey) {
		
		hashProgressBars.remove(hashHPKey); //remove progress bar from hash
		
	}
	
	
	public void reset(){
		hashProgressBars.clear();
	}

	public JobInfoPanel getJobInfoPanel() {
//		jobInfoPanel.layout();
		return jobInfoPanel;
	}
}
