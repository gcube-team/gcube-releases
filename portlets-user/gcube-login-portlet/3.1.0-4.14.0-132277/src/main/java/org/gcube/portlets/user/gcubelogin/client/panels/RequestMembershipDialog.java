package org.gcube.portlets.user.gcubelogin.client.panels;



import org.gcube.portlets.user.gcubelogin.client.GCubeLogin;
import org.gcube.portlets.user.gcubelogin.client.commons.ActionButton;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



public class RequestMembershipDialog extends GCubeDialog {

	private VerticalPanel main_panel = null;
	VerticalPanel vPanel = new VerticalPanel();
	TextArea comment = new TextArea();
	HorizontalPanel buttonsPanel = new HorizontalPanel();

	public RequestMembershipDialog(final ActionButton caller, String vreName, final String scope, boolean pending) {

		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(false);
		super.setAnimationEnabled(false);
		setText("Join request for " + vreName);
		main_panel = new VerticalPanel();
		// PopupPanel is a SimplePanel, so you have to set it's widget property to
		// whatever you want its contents to be.
		Button close = new Button("Back");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();					
			}    	  
		});
		
		if (! pending) {
			
			vPanel.setSpacing(3);
			vPanel.add(new HTML("You are about to ask for access to " + vreName + ", please confirm your request"));
			vPanel.add(new HTML("<br />", true));


			comment.setText("optional comment here");
			comment.setWidth("350px");
			comment.setVisibleLines(4);
			vPanel.add(comment);

			main_panel.add(vPanel);
			t.schedule(500);


			Button confirm = new Button("Confirm Request");
			confirm.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					GCubeLogin.showLoading();	
					GCubeLogin.getService().addMembershipRequest(scope, comment.getText(), new AsyncCallback<Void>() {
						public void onFailure(Throwable arg0) {							
							GCubeLogin.hideLoading();							
							vPanel.clear();
							vPanel.add(new HTML("<h3>An error occurred! Your request has not been sent</h3>"));						
							vPanel.add(new HTML("An email with the cause of the error has been sent to D4Science, we'll be back to you shortly"));
							vPanel.add(new HTML("<br />Data e-Infrastructure gateway support team"));
							buttonsPanel.clear();						
							Button close = new Button("Close Window");
							close.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									hide();					
								}    	  
							});
							buttonsPanel.add(close);
						}

						public void onSuccess(Void arg0) {
							//loadNews();
							GCubeLogin.hideLoading();	
							if (caller != null)
								caller.setPending();
							vPanel.clear();
							vPanel.add(new HTML("<h3>Thank you, your request has been sent successfully</h3>"));						
							vPanel.add(new HTML("You will receive an email as soon as your request will be approved by D4Science"));
							vPanel.add(new HTML("<br />Data e-Infrastructure gateway support team"));
							buttonsPanel.clear();						
							Button close = new Button("Close Window");
							close.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									hide();					
								}    	  
							});
							buttonsPanel.add(close);
						}
					});			
				}    	  
			});
			buttonsPanel.add(confirm);

		}
		else {
			vPanel.setSpacing(3);
			vPanel.add(new HTML("<h3>Your request has not been approved yet</h3>"));						
			vPanel.add(new HTML("You will receive an email as soon as your request will be approved by D4Science"));
			vPanel.add(new HTML("<br />Data e-Infrastructure gateway support team"));
			main_panel.add(vPanel);
		}
		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));


		buttonsPanel.add(close);

		buttonsPanel.setSpacing(5);

		main_panel.add(buttonsPanel);

		main_panel.setPixelSize(400, 200);
		setWidget(main_panel);
	}

	public void show() {
		super.show();
		center();	
	}

	Timer t = new Timer() {
		@Override
		public void run() {
			comment.selectAll();
			comment.setFocus(true);
		}
	};
}
