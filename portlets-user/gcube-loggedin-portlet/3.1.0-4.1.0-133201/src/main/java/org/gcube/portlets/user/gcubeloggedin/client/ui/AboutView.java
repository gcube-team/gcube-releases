package org.gcube.portlets.user.gcubeloggedin.client.ui;

import org.gcube.portlets.user.gcubeloggedin.client.LoggedinService;
import org.gcube.portlets.user.gcubeloggedin.client.LoggedinServiceAsync;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;
import org.gcube.portlets.user.gcubewidgets.client.elements.Span;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AboutView extends Composite {

	private static int MAX_CHAR_DESC = 400;
	private static String SEE_LESS = "See less";
	private static String SEE_MORE = "See more";
	private Button leaveVreButton = new Button("Leave Group");
	private AlertBlock alertBlockOnLeave = new AlertBlock();
	// more options label to show the other ones (if present)
	final Button showMoreOptions = new Button("Other options ...");
	private static final String leaveAlertMessage = "Are you sure you want to leave this group? "
			+ "By leaving this group you will no longer receive updates and lose the workspace folder related to the group.";

	// panel for leave group option
	private VerticalPanel leaveVREOption = new VerticalPanel();

	private final LoggedinServiceAsync service = GWT.create(LoggedinService.class);
	private static AboutViewUiBinder uiBinder = GWT
			.create(AboutViewUiBinder.class);

	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
	}


	private String vreDescription;
	@UiField Image vreImage;
	@UiField Heading vreName;
	@UiField HTML description;
	@UiField Button seeMore;
	@UiField Button editButton;
	@UiField Hero mainPanel;
	private EditDescriptionModal mod;

	public AboutView(VObject vobj, LoggedinServiceAsync loggedinService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.vreDescription = vobj.getDescription();
		vreName.setText(vobj.getName());
		vreImage.setUrl(vobj.getImageURL());
		String desc = vreDescription = vobj.getDescription();
		if (desc.length() > MAX_CHAR_DESC) {
			desc = desc.substring(0, MAX_CHAR_DESC) + " ...";
			//description.getElement().setInnerHTML(desc);
			description.setHTML(desc);
			seeMore.setVisible(true);
			seeMore.setText(SEE_MORE);
		} else 
			description.setHTML(desc);
		description.addStyleName("vre-description");
		if (vobj.isManager()) {
			editButton.setVisible(true);
		}
		mod = new EditDescriptionModal(vobj.getName(), vobj.getDescription());

		service.isLeaveButtonAvailable(Location.getHref(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				leaveVREOption.clear();
				leaveVREOption.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				if(result)
					addLeaveVREButton();
				showMoreOptions.setVisible(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				leaveVREOption.clear();				
			}
		});

	
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
	}	
	boolean open = false;
	@UiHandler("seeMore")
	void onSeemore(ClickEvent e) {
		GWT.log(seeMore.getText());
		if (!open) {
			description.setHTML(vreDescription);
			seeMore.setText(SEE_LESS);
			open = true;
		} else {
			description.setHTML(vreDescription.substring(0, MAX_CHAR_DESC) + " ...");
			seeMore.setText(SEE_MORE);
			open = false;
		}
	}

	@UiHandler("editButton")
	void onEditButton(ClickEvent e) {
		mod.show();
	}
	private void addLeaveVREButton(){

		// add leave VRE button
		leaveVreButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// show alert block
				alertBlockOnLeave.setVisible(true);
				leaveVreButton.setVisible(false);
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
				leaveVreButton.setVisible(true);
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


	/**
	 * 
	 * @return
	 */
	public static Widget getLoadingHTML() {
		return new LoadingText();
	}

}
