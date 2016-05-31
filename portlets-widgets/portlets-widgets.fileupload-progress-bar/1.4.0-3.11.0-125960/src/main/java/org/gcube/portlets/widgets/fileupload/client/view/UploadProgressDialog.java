package org.gcube.portlets.widgets.fileupload.client.view;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.fileupload.client.bundle.ProgressBarCssAndImages;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class UploadProgressDialog extends GCubeDialog {

	private static final int WIDTH = 400;

	static {
		ProgressBarCssAndImages.INSTANCE.css().ensureInjected();
	}

	private CellPanel mainPanel = new VerticalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private UploadProgressView uploadView ;
	private boolean handleFormSubmit = false;
	/**
	 * regular constructor
	 * 
	 * @param headerText
	 * @param eventBus
	 */
	public UploadProgressDialog(String headerText, HandlerManager eventBus) {
		setText(headerText);

		this.uploadView = new UploadProgressView(this, eventBus);		

		mainPanel.setPixelSize(WIDTH, 80);
		bottomPanel.setPixelSize(WIDTH, 25);

		mainPanel.add(uploadView);
		mainPanel.add(bottomPanel);

		bottomPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		Button close = new Button("Cancel");
		bottomPanel.add(close);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});		
		mainPanel.setCellHeight(bottomPanel, "25px");
		setWidget(mainPanel);
	}
	/**
	 * to be used if you want to handle form submit
	 * 
	 * @param headerText
	 * @param eventBus
	 * @param handleFormSubmit
	 */
	public UploadProgressDialog(String headerText, HandlerManager eventBus, boolean handleFormSubmit) {
		setText(headerText);
		this.handleFormSubmit = handleFormSubmit;
		this.uploadView = new UploadProgressView(this, eventBus);		

		mainPanel.setPixelSize(WIDTH, 80);
		bottomPanel.setPixelSize(WIDTH, 25);

		mainPanel.add(uploadView);
		mainPanel.add(bottomPanel);

		bottomPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		Button close = new Button("Cancel");
		bottomPanel.add(close);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});		
		mainPanel.setCellHeight(bottomPanel, "25px");
		setWidget(mainPanel);

	}

	public void showRegisteringResult(boolean result) {
		uploadView.showRegisteringResult(result, null);
	}
	
	public void showRegisteringResult(boolean result, String customFeedback) {
		uploadView.showRegisteringResult(result, customFeedback);
	}
	
	/**
	 * to call when handle
	 * @throws Exception 
	 */
	public void submitForm() throws Exception {
		if (handleFormSubmit)
			uploadView.submitForm();
		else
			throw new Exception("You must set this widget to handleFormSubmit");
	}

	protected void hideCloseButton() {
		bottomPanel.clear();
	}

	protected boolean isFormSubmitHandled() {
		return handleFormSubmit;
	}

	protected void showFinalCloseButton() {
		bottomPanel.clear();
		Button close = new Button("Close");
		bottomPanel.add(close);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});
	}
}
