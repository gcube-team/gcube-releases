package org.gcube.portlets.user.speciesdiscovery.client.job.gislayer;


import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class GisLayerWindowInfoJobsSpecies extends Window{

	private GisLayerJobsInfoContainer jobInfo = new GisLayerJobsInfoContainer("");

	public GisLayerWindowInfoJobsSpecies(GisLayerJobsInfoContainer jobInfoContainer) {
		setModal(true);
		setSize(450, 300);
		this.jobInfo = jobInfoContainer;
		add(jobInfo);
	}

	public GisLayerWindowInfoJobsSpecies() {
		setModal(true);
		setSize(450, 300);
		setLayout(new FitLayout());
		add(jobInfo);
	}


	public void setWindowTitle(String title) {
		this.setHeading(title);

	}

	public void updateDescription(JobGisLayerModel jobModel) {

		String description = "\nName: \n" + jobModel.getJobName() + "\n";
		if(jobModel.getLayerUUID()!=null)
			description += "\n\nLayer UUID:\n" + jobModel.getLayerUUID();
		if(jobModel.getGisViewerAppLink()!=null)
			description += "\n\nGis Viewer App Link:\n" + jobModel.getGisViewerAppLink();
		description += "\n\nOccurrence Points (in total): \n" + jobModel.getTotalPoints();
		description += "\n\nOccurrence Points (used to generate the layer): \n" + jobModel.getCompletedPoints();
		description += "\n\nStart Time: \n" + jobModel.getStartTime();
		description += "\n\nEnd Time: \n" + jobModel.getEndTime();
		description += "\n\nElapsed Time: \n" + jobModel.getElapsedTime();

		this.jobInfo.updateDescription(description);
	}

}
