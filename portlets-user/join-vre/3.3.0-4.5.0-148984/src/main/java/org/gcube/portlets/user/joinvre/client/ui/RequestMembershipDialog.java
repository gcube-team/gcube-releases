package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.client.responsive.ResponsivePanel;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RequestMembershipDialog extends Composite  {
	private final JoinServiceAsync joinService = GWT.create(JoinService.class);
	
	private static RequestAccessModalUiBinder uiBinder = GWT
			.create(RequestAccessModalUiBinder.class);

	interface RequestAccessModalUiBinder extends
	UiBinder<Widget, RequestMembershipDialog> {
	}
	@UiField Modal m;
	@UiField Button close;
	@UiField Button confirmRequest;
	@UiField TextArea optionalText;
	@UiField HelpBlock helpBlock;
	private VRE myVRE = null;
	private ResponsivePanel responsivePanel;

	public RequestMembershipDialog(ResponsivePanel responsivePanel, VRE myVRE) {
		initWidget(uiBinder.createAndBindUi(this));
		this.myVRE = myVRE;
		this.responsivePanel = responsivePanel;
		optionalText.setWidth("95%");
		optionalText.setPlaceholder("You can add an optional comment here");
	}

	public void show() {
		m.setTitle("Join request for " +myVRE.getName());
		m.show();
	}

	@UiHandler("close")
	void handleClick(ClickEvent e) {
		m.hide();
	}
	@UiHandler("confirmRequest")
	void confirm(ClickEvent e) {
		String text = optionalText.getText();	
		confirmRequest.setEnabled(false);
		joinService.addMembershipRequest(myVRE.getinfraScope(), text, new AsyncCallback<Void>() {			
			@Override
			public void onSuccess(Void result) {
				confirmRequest.removeFromParent();
				optionalText.removeFromParent();
				m.setTitle("Thank you, your request has been sent successfully");
				helpBlock.setText("You will receive an email as soon as your request will be processed.");
				responsivePanel.setPending(myVRE);
				confirmRequest.setEnabled(false);
			}
			@Override
			public void onFailure(Throwable caught) {
				confirmRequest.removeFromParent();
				optionalText.removeFromParent();
				m.setTitle("An error occurred! Your request has not been sent");
				helpBlock.setText("An email with the cause of the error has been sent to the support team, we'll be back to you shortly.");
			}
		});
	}
}
