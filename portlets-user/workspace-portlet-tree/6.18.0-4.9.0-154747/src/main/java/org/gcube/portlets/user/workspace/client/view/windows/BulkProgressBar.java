package org.gcube.portlets.user.workspace.client.view.windows;

import com.extjs.gxt.ui.client.widget.ProgressBar;

//import com.google.gwt.user.client.Timer;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class BulkProgressBar extends ProgressBar {

//	private boolean stopProgress = true;
	// private Timer timer;
	private String progressText;
	private String suffixText = "completed";
	float progress;

	public BulkProgressBar(String idBulk, String progressText) {

		this.progressText = progressText;
		setSize(300, 20);
//		setBounds(10, 10, 200, Style.DEFAULT);

		int i = 0;
		updateProgress(progress, i +"% " +suffixText);

	}

	public void updateProgress(float counter) {
		progress = counter / 100;
		updateProgress(progress, counter +"% " + suffixText);  
	}

	public String getProgressText() {
		return progressText;
	}

	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}

	public void setSuffixText(String suffixText) {
		this.suffixText = suffixText;
	}

	public float getProgress() {
		return progress;
	}
}
