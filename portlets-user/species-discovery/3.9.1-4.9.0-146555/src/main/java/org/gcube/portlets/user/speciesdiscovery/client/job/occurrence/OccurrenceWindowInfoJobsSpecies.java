package org.gcube.portlets.user.speciesdiscovery.client.job.occurrence;


import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class OccurrenceWindowInfoJobsSpecies extends Window{

	private OccurrenceJobsInfoContainer jobInfo = new OccurrenceJobsInfoContainer("");
	
	public OccurrenceWindowInfoJobsSpecies(OccurrenceJobsInfoContainer jobInfoContainer) {
		setModal(true);
		setSize(450, 300);
		this.jobInfo = jobInfoContainer;
		add(jobInfo);
	}
	
	public OccurrenceWindowInfoJobsSpecies() {
		setModal(true);
		setSize(450, 300);
		setLayout(new FitLayout());
		add(jobInfo);
	}
	

	public void setWindowTitle(String title) {
		this.setHeading(title);
		
	}

	public void updateDescription(JobOccurrencesModel jobModel) {
		
		String description = "\nName: \n" + jobModel.getJobName() + "\n";
		
//		description += "Description: \n" + Util.cleanValue(jobModel.getDescription());
		description += "\n\nData Sources: \n";
		
		for (String dataSource : jobModel.getDataSourcesNameAsString()) {
			description += dataSource + ", ";
		}
		//remove last comma char
		description = description.substring(0, description.lastIndexOf(","));
		
		description += "\n\nStart Time: \n" + jobModel.getSubmitTime();
		description += "\n\nEnd Time: \n" + jobModel.getEndTime();
		description += "\n\nItems collected: \n" + jobModel.getNodeCompleted() + " of " + jobModel.getTotalOccurrences();
		
		if(jobModel.getDownloadState().equals(DownloadState.COMPLETED) && jobModel.getNodeCompleted()<jobModel.getTotalOccurrences()){
			int difference = jobModel.getTotalOccurrences() - jobModel.getNodeCompleted();
			
			description += " (" + difference + " duplicate or not available occurrence points were discarded)";
		}
		
		jobInfo.updateDescription(description);
	}

}
