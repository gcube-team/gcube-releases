package org.gcube.portlets.user.joinnew.client.panels;



import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.joinnew.client.Joinnew;
import org.gcube.portlets.user.joinnew.client.commons.ActionButton;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;



public class AccessVREDialog extends GCubeDialog {

	private VerticalPanel main_panel = null;
	VerticalPanel vPanel = new VerticalPanel();

	HorizontalPanel buttonsPanel = new HorizontalPanel();

	public AccessVREDialog(final ActionButton caller, final VRE vre, final String scope, boolean isInvitation) {

		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(false);
		super.setAnimationEnabled(false);
		String headerText = isInvitation ? "Invitation to " + vre.getName() : "Join request for " + vre.getName();
		setText(headerText);
		main_panel = new VerticalPanel();
		// PopupPanel is a SimplePanel, so you have to set it's widget property to
		// whatever you want its contents to be.
		Button close = new Button("Cancel");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();					
			}    	  
		});

		String text = isInvitation ? "<h3>To accept the invite, please click on the accept invite button below</h3>" : "<h3>You are about to enter the " + vre.getName() + ", please confirm your request</h3>";

		vPanel.setSpacing(3);
		HTML join = new HTML(text);
		join.setStyleName("font_family");
		join.addStyleName("font_12");
		vPanel.add(join);
		vPanel.add(new HTML("<br />", true));

		HTML warning = new HTML();
		warning.setHTML("By entering this VRE you agree to the terms "
				+ "indicated in the <a href=\"/web/guest/terms-of-use\" target=\"_blank\">Terms of Use</a> of this gateway.");
		warning.setWidth("350px");

		vPanel.add(warning);

		main_panel.add(vPanel);

		String buttonText = isInvitation ? "Accept invite" : "Confirm Request";
		
		Button confirm = new Button(buttonText);
		confirm.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Joinnew.showLoading();	
				Joinnew.getService().registerUser(vre.getGroupName(), vre.getId(), new AsyncCallback<Boolean>() {
					public void onFailure(Throwable arg0) {							
						showError();
					}

					public void onSuccess(Boolean result) {
						if (!result) {
							showError();
						} else {
							Joinnew.hideLoading();	
							if (caller != null)
								caller.setPending();
							vPanel.clear();
							vPanel.add(new HTML("<h3>Thank you, you are now registered to "+vre.getName()+" </h3>"));	
							HTML feedback = new HTML();
							feedback.setHTML("<div>You are now being redirected to the selected environmment</div>");
							vPanel.add(feedback);
							feedback.setStyleName("feedback");
							buttonsPanel.clear();						
							Button close = new Button("Enter");
							close.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									hide();
									redirect(vre);
								}    	  
							});
							buttonsPanel.add(close);
							Timer t = new Timer() {								
								@Override
								public void run() {
									redirect(vre);									
								}
							};
							t.schedule(2000);
							
						}
					}
				});			
			}    	  
		});
		buttonsPanel.add(confirm);


		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));


		buttonsPanel.add(close);

		buttonsPanel.setSpacing(5);

		main_panel.add(buttonsPanel);

		main_panel.setPixelSize(400, 200);
		setWidget(main_panel);
	}

	private void showError() {
		Joinnew.hideLoading();							
		vPanel.clear();
		vPanel.add(new HTML("<h3>An error occurred! Your request has not been sent</h3>"));	
		HTML feedback = new HTML("<div>An email with the cause of the error has been sent to the support team, we'll be back to you shortly.</div> " +
				"<div style=\"margin-top: 10px;\">Support team</div>");
		vPanel.add(feedback);
		buttonsPanel.clear();		
		feedback.setStyleName("feedback");
		Button close = new Button("Close Window");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();					
			}    	  
		});
		buttonsPanel.add(close);
	}

	public void show() {
		super.show();
		center();	
	}

	private void redirect(final VRE vre) {
		hide();
		Joinnew.showLoading();	
		String scope = vre.getGroupName();
		Joinnew.getService().loadLayout(scope,  vre.getFriendlyURL(), new AsyncCallback<Void>() {
			public void onFailure(Throwable arg0) {							
				Joinnew.hideLoading();	
				Window.open( vre.getFriendlyURL(), "_self", "");
			}
			public void onSuccess(Void arg0) {
				Joinnew.hideLoading();
				Window.open( vre.getFriendlyURL(), "_self", "");
			}
		});			
	}

}
