package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import com.extjs.gxt.ui.client.widget.ProgressBar;

public class JobTabularDataProgressBar extends ProgressBar {

	private String progressText;
	private float progress;
//	private final Timer timer;
	private boolean isCompleted = false;


	public JobTabularDataProgressBar(String idJob, final String text) {

		this.progressText = text;
		setSize(140, 18);

		int i = 0;
		updateProgress(progress, text);

//		timer = new Timer() {
//			float i;
//
//			@Override
//			public void run() {
//				updateProgress(i / 100, progressText);
//				i += 5;
//				if (i > 105) {
//					if(!isCompleted)
//					i = 0;
//				}
//			}
//		};
	}
	
//	public void progressStart(){
//		timer.scheduleRepeating(500);
//	}
//	
//	public void progressStop(){
//		timer.cancel();
//	}

	public void updateProgress(float counter) {
		progress = counter / 100;
		updateProgress(progress, progressText);
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