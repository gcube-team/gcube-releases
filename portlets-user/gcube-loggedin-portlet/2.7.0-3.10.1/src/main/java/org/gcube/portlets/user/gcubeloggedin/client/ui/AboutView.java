package org.gcube.portlets.user.gcubeloggedin.client.ui;

import org.gcube.portlets.user.gcubeloggedin.client.LoggedinServiceAsync;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;
import org.gcube.portlets.user.gcubeloggedin.shared.VREClient;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AboutView extends Composite {

	private static int MAX_CHAR_DESC = 700;
	
	private static AboutViewUiBinder uiBinder = GWT
			.create(AboutViewUiBinder.class);

	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	private WarningAlert wa;
	private String vreDescription;
	private String vreImage;
	@UiField HTMLPanel htmlPanel;
	@UiField HTML description;
	@UiField Anchor backButton;
	@UiField Anchor leaveButton;
	@UiField Button seeMore;


	private  LoggedinServiceAsync loggedinService;

	public AboutView(VObject vobj, LoggedinServiceAsync loggedinService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.loggedinService = loggedinService;
		vreImage = vobj.getImageURL();
		String desc = vreDescription = vobj.getDescription();
		if (desc.length() > MAX_CHAR_DESC) {
			desc = desc.substring(0, MAX_CHAR_DESC) + " ...";
			seeMore.setVisible(true);
		}
		description.setHTML("<img class=\"imageVRE\" src=\"" +  vreImage + "\" />" + desc);

		leaveButton.setStyleName("leave-group");
		

		if (vobj instanceof VREClient && !vobj.isMandatory()) {
			wa = new WarningAlert("Are you sure you want to leave this group? "
					+ "By leaving this group you will no longer receive updates and lose the workspace folder related to the group.", this);
		} 
		else {
			//remove the login button
			Scheduler.get().scheduleDeferred(new Command() {
				public void execute () {
					DOM.getElementById("removable-item-li").removeFromParent();
				}
			});
		}


	}
	
	@UiHandler("seeMore")
	void onSeeMoreClick(ClickEvent e) {
		description.setHTML("<img class=\"imageVRE\" src=\"" +  vreImage + "\" />" + vreDescription);
		seeMore.removeFromParent();
	}

	@UiHandler("backButton")
	void onClick(ClickEvent e) {
		loggedinService.getDefaultCommunityURL(new AsyncCallback<String>() {
			public void onSuccess(String url) {
				Window.open(url, "_self", "");									
			}	
			public void onFailure(Throwable arg0) {
				Window.alert("We're sorry we couldn't reach the server, try again later ... " + arg0.getMessage());
			}								
		});
	}

	@UiHandler("leaveButton")
	void onUnsubscribe(ClickEvent e) {

		htmlPanel.add(wa);
	}


	protected void abandonGroup() {
		htmlPanel.remove(wa);
		final Widget loading = getLoadingHTML();
		htmlPanel.add(loading);
		loggedinService.removeUserFromVRE(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if (result != null)
					Location.assign(result);
				else 
					CheckSession.showLogoutDialog();

			}
			@Override
			public void onFailure(Throwable caught) {
				htmlPanel.remove(loading);
				Window.alert("We're sorry we couldn't reach the server, try again later ... " + caught.getMessage());				
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
