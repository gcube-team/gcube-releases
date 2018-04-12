package org.gcube.portlets.user.gcubelogin.client.commons;

import org.gcube.portlets.user.gcubelogin.client.GCubeLogin;
import org.gcube.portlets.user.gcubelogin.client.panels.RequestMembershipDialog;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

public class ActionButton extends Button {

	public enum ButtonType {
		ENTER, ASK_4_REG, PENDING;
	}

	private ButtonType type = ButtonType.ENTER;
	
	private ClickHandler myClickhandler; 
	HandlerRegistration handleReg; 

	private void openDialog(VRE vre) {
		String scope = vre.getGroupName();
		RequestMembershipDialog dlg = new RequestMembershipDialog(this, vre.getName(), scope, false);
		dlg.show();
	}

	public ActionButton(final VRE vre, ButtonType type) {
		this.type = type;
		if (this.type == ButtonType.PENDING) {
			this.setStyleName("odlbutton_pending vertical_top");
			this.setWidth("60px");
			setText("pending");

			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					RequestMembershipDialog dlg = new RequestMembershipDialog(null, vre.getName(), "", true);
					dlg.show();
				}
			});
		}else if (this.type == ButtonType.ASK_4_REG) {
			this.setStyleName("odlbutton_ask vertical_top");
			this.setWidth("60px");
			setText("sign up");
			myClickhandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					openDialog(vre);

				}
			};
			handleReg = addClickHandler(myClickhandler);

		} else {
			this.setStyleName("button_enter vertical_top");
			this.setWidth("60px");
			//this.setHeight("16px");
			setText("enter this VRE");

			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					GCubeLogin.showLoading();	
					String scope = vre.getGroupName();
					GCubeLogin.getService().loadLayout(scope,  vre.getFriendlyURL(), new AsyncCallback<Void>() {
						public void onFailure(Throwable arg0) {							
							GCubeLogin.hideLoading();	
							Location.assign(vre.getFriendlyURL());
						}
						public void onSuccess(Void arg0) {
							GCubeLogin.hideLoading();
							Location.assign(vre.getFriendlyURL());
						}
					});

				}
			});

		}
	}

	public ButtonType getType() {
		return type;
	}

	public void setType(ButtonType type) {
		this.type = type;
		if (this.type == ButtonType.PENDING) {
			this.setStyleName("odlbutton_pending vertical_top");

		}else if (this.type == ButtonType.ASK_4_REG) {
			this.setStyleName("odlbutton_ask vertical_top");

		} else {
			this.setStyleName("odlbutton_enter vertical_top");

		}
	}

	public void setPending() {
		setType(ButtonType.PENDING);
		this.setStyleName("odlbutton_pending vertical_top");
		setText("pending");
				
		handleReg.removeHandler();		
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				RequestMembershipDialog dlg = new RequestMembershipDialog(null, "", "", true);
				dlg.show();
			}
		});
	}
	
}