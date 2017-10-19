package org.gcube.portlets.widgets.fileupload.client.view;

import org.gcube.portlets.widgets.fileupload.client.bundle.ProgressBarCssAndImages;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public final class UploadProgressPanel extends Composite {
	
	static {
		ProgressBarCssAndImages.INSTANCE.panelCss().ensureInjected();
	}

	private HandlerManager eventBus;

	private FileSubmit fileSubmit;
	
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/**
	 * as a Panel
	 * @param eventBus
	 */
	public UploadProgressPanel(HandlerManager eventBus) {
		this.eventBus = eventBus; 
		mainPanel.setStyleName("progressContainer");
		initWidget(mainPanel);
	}
	
	/**
	 * Used when attaching files via button
	 * @return
	 */
	public FileUpload initialize() {	
		mainPanel.clear();
		fileSubmit = new FileSubmit(eventBus, false);
		mainPanel.add(fileSubmit);
		mainPanel.add(new UploadProgress());
		//return the fileupload so that you cane set it hidden
		return fileSubmit.getFileUpload();
	}
	
	/**
	 * Used when attaching files via dnd
	 * @return
	 */
	public void initializeDND(){
		mainPanel.clear();
		fileSubmit = new FileSubmit(eventBus, true);
		mainPanel.add(fileSubmit);
		mainPanel.add(new UploadProgress());
	}

	public void showRegisteringResult(boolean result) {
		fileSubmit.showRegisteringResult(result, null);
	}
	
	public void showRegisteringResult(boolean result, String customFeedback) {
		fileSubmit.showRegisteringResult(result, customFeedback);
	}
	
	public FileUpload getFileUpload() {
		return fileSubmit.getFileUpload();
	}
}
