package org.gcube.portlets.user.joinnew.client.commons;

import org.gcube.portlets.user.joinnew.client.Joinnew;
import org.gcube.portlets.user.joinnew.client.panels.AccessVREDialog;
import org.gcube.portlets.user.joinnew.client.panels.RequestMembershipDialog;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

public class ActionButton extends Button {

	public enum ButtonType {
		ENTER, ASK_4_REG, PENDING, FREE;
	}

	private ButtonType type = ButtonType.ENTER;

	private ClickHandler myClickhandler; 
	HandlerRegistration handleReg; 

	
	public ActionButton(final VRE vre, ButtonType type) {
	
		GWT.log("vre.isUponRequest() "+vre.getName() + "?"+vre.isUponRequest());
		if (!vre.isUponRequest()) {
		
			setText(UIConstants.REGISTER_FREE);
			this.addStyleName("free-access-button");
			myClickhandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					openAccessDialog(vre);
					
				}

				
			};
			handleReg = addClickHandler(myClickhandler);
		}
		else {
			this.type = type;
			if (this.type == ButtonType.PENDING) {
				this.addStyleName("pending-button");
				setText(UIConstants.PENDING);

				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						RequestMembershipDialog dlg = new RequestMembershipDialog(null, vre.getName(), "", true);
						dlg.show();
					}
				});
			}else if (this.type == ButtonType.ASK_4_REG) {
				setText(UIConstants.SIGN_UP);
				this.addStyleName("require-access-button");
			
				myClickhandler = new ClickHandler() {
					public void onClick(ClickEvent event) {
						openDialog(vre);

					}
				};
				handleReg = addClickHandler(myClickhandler);

			}

			else {
				this.setStyleName("button_enter vertical_top");
				this.setWidth("60px");
				//this.setHeight("16px");
				setText("enter");

				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
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
				});

			}
		}
	}

	public ButtonType getType() {
		return type;
	}

	private void openDialog(VRE vre) {
		String scope = vre.getGroupName();
		RequestMembershipDialog dlg = new RequestMembershipDialog(this, vre.getName(), scope, false);
		dlg.show();
	}
	
	private void openAccessDialog(VRE vre) {
		String scope = vre.getGroupName();
		AccessVREDialog dlg = new AccessVREDialog(this, vre, scope, false);
		dlg.show();
	}


	public void setPending() {

		this.addStyleName("pending-button");

		setText(UIConstants.PENDING);

		handleReg.removeHandler();		
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				RequestMembershipDialog dlg = new RequestMembershipDialog(null, "", "", true);
				dlg.show();
			}
		});
	}
	public static native String getURL()/*-{
			return $wnd.location;
			}-*/;

}