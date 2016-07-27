package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.user.questions.client.resources.Images;
import org.gcube.portlets.user.questions.client.ui.DisplayBadge;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;
import org.gcube.portlets.widgets.wsmail.client.forms.MailForm;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VREManagersPanel  extends Composite {

	private final QuestionsServiceAsync service = GWT.create(QuestionsService.class);

	private Image loadingImage;
	private Image postToImage;	
	private Button messageManagers = new Button();
	private Button leaveVreButton = new Button("Leave Group");
	private AlertBlock alertBlockOnLeave = new AlertBlock();

	private static final String leaveAlertMessage = "Are you sure you want to leave this group? "
			+ "By leaving this group you will no longer receive updates and lose the workspace folder related to the group.";

	// main panel
	private VerticalPanel mainPanel = new VerticalPanel();

	// panel for Questions? Ask the managers option
	private VerticalPanel askManagersOption = new VerticalPanel();

	// panel for leave group option
	private VerticalPanel leaveVREOption = new VerticalPanel();

	// list of managers
	private ArrayList<UserInfo> managers;

	public VREManagersPanel() {
		super();
		initWidget(mainPanel);
		
		// set main panel width
		mainPanel.setWidth("100%");

		Images images = GWT.create(Images.class);
		loadingImage = new Image(images.membersLoader().getSafeUri());
		postToImage =  new Image(images.postToIcon().getSafeUri());

		// add options subpanels to the main one
		mainPanel.add(askManagersOption); // this is always present ...

		// more options label to show the other ones (if present)
		final Button showMoreOptions = new Button("Show more options ...");
		showMoreOptions.setType(ButtonType.LINK);

		// handler
		showMoreOptions.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// hide the button itself
				showMoreOptions.setVisible(false);

				// show other options
				leaveVREOption.setVisible(true);

			}
		});

		mainPanel.add(showMoreOptions);
		mainPanel.add(leaveVREOption);

		// hide options but askManagersOption
		leaveVREOption.setVisible(false);

		// show loaders for the panels
		showLoader(askManagersOption);
		showLoader(leaveVREOption);

		service.getManagers(new AsyncCallback<ArrayList<UserInfo>>() {

			@Override
			public void onSuccess(ArrayList<UserInfo> users) {
				askManagersOption.clear();
				managers = users;
				askManagersOption.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				askManagersOption.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				postToImage.setStyleName("manager-post-image");
				postToImage.setTitle("Message privately to the Managers");
				if (users == null || users.isEmpty()) {
					askManagersOption.add(new HTML("<div class=\"frame\" style=\"font-size: 16px;\">Ops, something went wrong. Please <a href=\"javascript: location.reload();\">reload<a/> this page.</div>"));
				} else {
					for (int i = 0; i < users.size(); i++) {
						askManagersOption.add(new DisplayBadge(users.get(i)));
					}
					if (users.size() > 1)
						messageManagers.setText("Message managers");
					else 
						messageManagers.setText("Message manager");
				}
				SimplePanel bPanel = new SimplePanel();
				bPanel.setStyleName("manager-action");
				bPanel.setWidget(messageManagers);
				askManagersOption.add(bPanel);
			}

			@Override
			public void onFailure(Throwable caught) {
				askManagersOption.clear();
				askManagersOption.add(new HTML("<div class=\"nofeed-message\">" +
						"Sorry, looks like something is broken with the server connection<br> " +
						"Please check your connection and try refresh this page.</div>"));

			}
		});

		messageManagers.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final List<String> listToLogin = new ArrayList<String>();
				for (UserInfo user : managers) {
					listToLogin.add(user.getUsername());
				}

				GWT.runAsync(new RunAsyncCallback() {
					@Override
					public void onSuccess() {
						new MailForm(listToLogin);
					}
					public void onFailure(Throwable reason) {
						Window.alert("Could not load this component: " + reason.getMessage());
					}   
				});				
			}
		});

		service.isLeaveButtonAvailable(Location.getHref(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				leaveVREOption.clear();
				leaveVREOption.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				if(result)
					addLeaveVREButton();

			}

			@Override
			public void onFailure(Throwable caught) {
				leaveVREOption.clear();				
			}
		});
	}

	private void addLeaveVREButton(){

		// add leave VRE button
		leaveVreButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// show alert block
				alertBlockOnLeave.setVisible(true);

			}
		});

		// Add Cancel and Confirm Leave buttons
		Span cancel = new Span("Cancel");
		Span confirmLeave = new Span("Confirm Leave");
		cancel.setStyleName("cancel-leave-button");
		confirmLeave.setStyleName("cancel-leave-button");

		// add to main panel
		alertBlockOnLeave.setHTML(leaveAlertMessage + "<br><br>");
		alertBlockOnLeave.add(cancel);
		alertBlockOnLeave.add(new InlineHTML(" or "));
		alertBlockOnLeave.add(confirmLeave);
		alertBlockOnLeave.setType(AlertType.WARNING);
		alertBlockOnLeave.setHeading("WARNING!");
		alertBlockOnLeave.setClose(false);
		alertBlockOnLeave.setVisible(false);
		leaveVREOption.add(alertBlockOnLeave);
		leaveVREOption.add(leaveVreButton);

		// add handlers
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// just hide alertBlock
				alertBlockOnLeave.setVisible(false);
			}
		});

		confirmLeave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event){
				service.removeUserFromVRE(new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if (result != null)
							Location.assign(result);
						else 
							CheckSession.showLogoutDialog();

					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("We're sorry we couldn't reach the server, try again later ... " + caught.getMessage());				
					}			
				});

			}
		});
	}

	private void showLoader(VerticalPanel panel) {
		panel.clear();
		panel.setWidth("100%");
		panel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(loadingImage);
	}
}
