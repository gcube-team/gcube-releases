package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.client.JoinService;
import org.gcube.portlets.user.joinvre.client.JoinServiceAsync;
import org.gcube.portlets.user.joinvre.client.responsive.ResponsivePanel;
import org.gcube.portlets.user.joinvre.shared.VRE;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
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

public class RequestMembershipDialog extends Composite  {
	
	private static final String OPTIONAL_COMMENT_TEXT = "You can add an optional comment here, it will be attached to your request and read by this VRE Moderators.";

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
	@UiField Icon loading;
	private VRE myVRE = null;
	private ResponsivePanel responsivePanel;
	
	@UiField HelpBlock touGatewayBlock;	
	@UiField HTML touText;

	public RequestMembershipDialog(ResponsivePanel responsivePanel, VRE myVRE) {
		initWidget(uiBinder.createAndBindUi(this));
		this.myVRE = myVRE;
		this.responsivePanel = responsivePanel;
		optionalText.setWidth("95%");
		optionalText.setPlaceholder(OPTIONAL_COMMENT_TEXT);
	}

	public void show() {
		m.setTitle("Join request for " +myVRE.getName());
		loading.setVisible(true);
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
				loading.setVisible(false);
				touGatewayBlock.setVisible(true);
				m.show();				
			}
			@Override
			public void onSuccess(String result) {
				loading.setVisible(false);
				if (result != null)  { // terms of use exist		
					String text = "By using <b>" + myVRE.getName() + "</b>  VRE services, you agree to the Terms of Use below. Please read it carefully.";
					helpBlock.setHTML(text);
					String buttonText = "Accept Terms of Use & Request Access";
					confirmRequest.setText(buttonText);		
					m.addStyleName("modal-custom");
					((Element)m.getElement().getChildNodes().getItem(1)).addClassName("modal-body-custom");
					touText.setHTML(result);
				}
				
				m.show();
			}
		});

		
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
		joinService.addMembershipRequest(myVRE, text, new AsyncCallback<Void>() {			
			@Override
			public void onSuccess(Void result) {
				confirmRequest.removeFromParent();
				optionalText.removeFromParent();
				m.setTitle("Thank you, your request has been sent successfully");
				helpBlock.setText("You will receive an email as soon as your request will be processed.");
				responsivePanel.setPending(myVRE);
				confirmRequest.setEnabled(false);
				touText.removeFromParent();
			}
			@Override
			public void onFailure(Throwable caught) {
				confirmRequest.removeFromParent();
				optionalText.removeFromParent();
				m.setTitle("An error occurred! Your request has not been sent");
				helpBlock.setText("An email with the cause of the error has been sent to the support team, we'll be back to you shortly.");
				touText.removeFromParent();
			}
		});
	}
}
