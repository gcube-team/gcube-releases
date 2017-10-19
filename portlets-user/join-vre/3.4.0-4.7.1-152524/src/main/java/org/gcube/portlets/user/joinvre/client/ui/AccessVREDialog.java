package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AccessVREDialog extends Composite {
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	private final static boolean IS_INVITATION = false;
	private static AccessVREDialogUiBinder uiBinder = GWT
			.create(AccessVREDialogUiBinder.class);

	interface AccessVREDialogUiBinder extends UiBinder<Widget, AccessVREDialog> {
	}
	@UiField Modal m;
	@UiField Button close;
	@UiField Button confirmRequest;
	@UiField HelpBlock helpBlock;
	@UiField HelpBlock touGatewayBlock;	
	@UiField Icon loading;
	@UiField HTML touText;

	VRE myVRE = null;
	public AccessVREDialog(final VRE vre) {
		initWidget(uiBinder.createAndBindUi(this));
		this.myVRE = vre;
	}

	public void show() {
		joinService.getTermsOfUse(this.myVRE.getId(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				m.setTitle("Ops, an error occurred please check your connection and try again");
				confirmRequest.setText("Try again");
				confirmRequest.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Location.reload();						
					}
				});
				m.show();				
			}
			@Override
			public void onSuccess(String result) {
				if (result == null)  { //no terms of use
					String headerText = "Join VRE request for " + myVRE.getName();
					String text = "You are about to enter " + myVRE.getName() + ", please confirm your request.";
					m.setTitle(headerText);
					helpBlock.setHTML(text);
					touGatewayBlock.setVisible(true);
					String buttonText = "Confirm Request";
					confirmRequest.setText(buttonText);					
				}
				else {// terms of use exist		
					String headerText = "Terms of Use for " + myVRE.getName();
					String text = "By using <b>" + myVRE.getName() + "</b>  VRE services, you are agreeing to these terms. Please read them carefully:";
					m.setTitle(headerText);
					helpBlock.setHTML(text);
					String buttonText = "Accept Terms of Use";
					confirmRequest.setText(buttonText);		
					m.addStyleName("modal-custom");
					((Element)m.getElement().getChildNodes().getItem(1)).addClassName("modal-body-custom");
					touText.setHTML(result);
				}
				
				m.show();
			}
		});

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
