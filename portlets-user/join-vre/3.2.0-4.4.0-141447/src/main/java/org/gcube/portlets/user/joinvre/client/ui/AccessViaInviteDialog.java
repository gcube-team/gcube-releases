package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AccessViaInviteDialog extends Composite {
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	private final static boolean IS_INVITATION = true;
	private static AccessViaInviteDialogUiBinder uiBinder = GWT.create(AccessViaInviteDialogUiBinder.class);

	interface AccessViaInviteDialogUiBinder extends
			UiBinder<Widget, AccessViaInviteDialog> {
	}
	@UiField Modal m;
	@UiField Button close;
	@UiField Button confirmRequest;
	@UiField HelpBlock helpBlock;
	@UiField Icon loading;
	@UiField Image avatarImage;
	VRE myVRE = null;
	public AccessViaInviteDialog(VRE vre, String inviteId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.myVRE = vre;	
		//String text = "To accept the invite, please click on the accept invite button below";
		String text = "Retrieving your invite, please wait ...";
		helpBlock.setText(text);
		String buttonText = "Accept invite";
		confirmRequest.setText(buttonText);
		
		joinService.readInvite(inviteId, new AsyncCallback<UserInfo>() {			
			@Override
			public void onSuccess(UserInfo invitingUser) {
				helpBlock.setText("You have been invited by " +invitingUser.getFullName() + ", to accept the invite, please click on the accept invite button below.");		
				avatarImage.setUrl(invitingUser.getAvatarId());
				avatarImage.setVisible(true);
				confirmRequest.setType(ButtonType.PRIMARY);
				confirmRequest.setEnabled(true);
				loading.removeFromParent();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				loading.removeFromParent();
				helpBlock.setText("We're sorry, an error occured while trying to retrieve your invite, please report the issue: " + caught.getMessage());			
			}
		});
	}

	public void show() {
		String headerText = "Invitation to " + myVRE.getName();
		m.setTitle(headerText);
		m.show();
	}
	@UiHandler("close")
	void handleClick(ClickEvent e) {
		m.hide();
	}
	@UiHandler("confirmRequest")
	void confirm(ClickEvent e) {
		helpBlock.setText("Registering to " + myVRE.getName() + " please wait ... ");
		loading.setVisible(true);
		confirmRequest.setEnabled(false);
		joinService.registerUser(myVRE.getinfraScope(), myVRE.getId(), IS_INVITATION, new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				Location.assign(myVRE.getFriendlyURL());				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				confirmRequest.removeFromParent();
				m.setTitle("An error occurred! Your request has not been sent");
				helpBlock.setText("An email with the cause of the error has been sent to the support team, we'll be back to you shortly.");
			}
		});
	}

}
