package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class ExceptionAlertWindow extends GCubeDialog implements ClickHandler {
	
	private String friendlyErrorMessage;
	
	public ExceptionAlertWindow(String userMessage, boolean autoHide) {
		super(autoHide);
		this.friendlyErrorMessage = userMessage;
		setText("Error Information");

	}

	public void onClick(ClickEvent event) {
		hide();
	}

	public void addDock(final Throwable caught)
	{
		// if there is something on the GCube dialog clear it and then add the new dock
		clear();
		
		/* Button to display the simple message */
		Button emailBtn = new Button("[Email support team]"); 
		emailBtn.setStyleName("email-button");
		String msg;
		String cause;
		if (caught.getMessage() != null)
			msg = caught.getMessage();
		else
			msg = "An unexpected error occured. Please try again.";
		
		if (caught.getCause() != null)
			if (caught.getCause().getMessage() != null)
				cause = caught.getCause().getMessage();
			else
				cause = "";
		else
			cause = "";
		
		add(createDockForErrorMessage(msg, cause, emailBtn));
		setWidth("450px");

		emailBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Send an email to support team with the error
				AsyncCallback<Void> sendEmailToSupportTeamCallback = new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						SearchManager.hideLoading();
					}

					public void onSuccess(Void result) {
						SearchManager.hideLoading();
						Window.alert("Mail to support team was succesfully sent");
					}
					
				};SearchManager.smService.sendEmailWithErrorToSupport(caught, sendEmailToSupportTeamCallback);
				SearchManager.showLoading();
				
			}
		});
	}

	/**
	 * Creates the extended error message
	 * 
	 * @param errorMsg The error message to display
	 * @param errorCause The error cause of the exception
	 * @param stackTrace The stack trace of the exception
	 * @param b The button to be added
	 * @return A Flex table with the full representation of the message
	 */
	private FlexTable createDockForErrorMessage(String errorMsg, String errorCause, Button b) 
	{
		FlexTable infoTable = new FlexTable();
		infoTable.setWidget(0, 0, new HTML("<center><span style=\"color:red\">"+ this.friendlyErrorMessage + "</span></center>"));
		infoTable.getFlexCellFormatter().setColSpan(0, 0, 4);
		infoTable.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		infoTable.setWidget(1, 0, new HTML("<b>Error message:</b>"));
		infoTable.setText(1, 1, errorMsg);
		infoTable.getFlexCellFormatter().setColSpan(1, 1, 3);
		infoTable.getFlexCellFormatter().setAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		infoTable.setWidget(2, 0, new HTML("<br>"));
		infoTable.setWidget(3, 0, new HTML("<b>Error cause:</b>"));
		infoTable.setText(3, 1, errorCause);
		infoTable.getFlexCellFormatter().setColSpan(3, 1, 3);
		infoTable.getFlexCellFormatter().setAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		infoTable.setWidget(4, 0, new HTML("<br><br>"));
		infoTable.getFlexCellFormatter().setColSpan(4, 0, 4);
		infoTable.setWidget(5, 3, b);
		infoTable.getFlexCellFormatter().setAlignment(5, 3, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_BOTTOM);
		infoTable.setCellSpacing(3);
		infoTable.setCellPadding(2);
		infoTable.setBorderWidth(0);
		infoTable.setWidth("450px");
		return infoTable;
	}

}

