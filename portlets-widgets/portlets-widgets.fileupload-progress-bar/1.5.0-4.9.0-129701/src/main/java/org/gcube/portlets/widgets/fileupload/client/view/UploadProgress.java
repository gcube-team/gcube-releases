package org.gcube.portlets.widgets.fileupload.client.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.gcube.portlets.widgets.fileupload.client.state.UploadProgressState;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


public final class UploadProgress extends Composite {

	private Panel panel;

	private ProgressBar bar = new ProgressBar();
	/**
	 * 
	 */
	public UploadProgress() {

		panel = new VerticalPanel();
		panel.setStyleName("progressBarContainer");
		this.initWidget(panel);

		UploadProgressState.INSTANCE.addPropertyChangeListener("uploadProgress", new UploadProgressListener());
	}

	private final class UploadProgressListener implements PropertyChangeListener {

		private static final int COMPLETE_PERECENTAGE = 100;
		private static final int REMOVE_DELAY = 3000;

		@Override
		public void propertyChange(final PropertyChangeEvent event) {

			Map<String, Integer> uploadPercentage = (Map<String, Integer>) event.getNewValue();

			for (Map.Entry<String, Integer> entry : uploadPercentage.entrySet()) {
				String file = entry.getKey();
				Integer percentage = entry.getValue();

				panel.add(bar);

				bar.update(percentage);

				if (percentage == COMPLETE_PERECENTAGE) {
					Timer timer = new Timer() {

						@Override
						public void run() {
							panel.remove(bar);
						}
					};
					//timer.schedule(REMOVE_DELAY);
				}
			}
		}
	}

}
