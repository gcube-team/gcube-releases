package org.gcube.portlets.user.speciesdiscovery.client.job.gislayer;

import com.extjs.gxt.ui.client.widget.ProgressBar;

public class GisLayerJobSpeciesProgressBar extends ProgressBar {

	private String progressText;
	private float progress;
	private boolean isCompleted = false;

	public GisLayerJobSpeciesProgressBar(String idJob, final String text) {

		this.progressText = text;
		setSize(140, 20);
		updateProgress(progress, text);
	}
	public void updateProgressWithoutPercentage(float counter) {
		progress = counter / 100;
		updateProgress(progress, progressText);
	}
	
	
	public void updateProgressWithPercentage(float counter) {
		progress = counter / 100;
		updateProgress(progress, counter +"% " + progressText);  
	}

	public String getProgressText() {
		return progressText;
	}

	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}

	public float getProgress() {
		return progress;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
}