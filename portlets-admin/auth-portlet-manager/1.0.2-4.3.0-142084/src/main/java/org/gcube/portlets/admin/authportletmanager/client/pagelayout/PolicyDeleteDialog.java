package org.gcube.portlets.admin.authportletmanager.client.pagelayout;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.event.RemovePoliciesEvent;
import org.gcube.portlets.admin.authportletmanager.client.resource.AuthResources;
import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Row;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog Box for confirm delete policy 
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class PolicyDeleteDialog extends WindowBox   {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, PolicyDeleteDialog> {
	}

	private ArrayList<PolicyAuth> deletePolicy;
	//private Boolean error=false;


	@UiField
	Row  r_loader_space;

	@UiField
	Button b_confirm_delete;

	@UiField
	Button b_confirm_exit;

	public PolicyDeleteDialog(ArrayList<PolicyAuth> listDeletePolicy) {

		super(true, false);
		
		this.setWidget(binder.createAndBindUi(this));
		//this.setAutoHideEnabled(true);
		this.setGlassEnabled(true);
		this.setWidth("400px");
		this.setHeight("200px");
		this.setAnimationEnabled(isVisible());
		//this.center();
		this.setText("Delete Policy");		
		this.setStyleName("modal_delete");
		this.setPopupPosition(((Window.getClientWidth() - 400) / 2),
				((Window.getClientHeight()-200)/2) );



		this.deletePolicy=listDeletePolicy;
		for (PolicyAuth policy:listDeletePolicy){
			String caller=policy.getCallerAsString();
			Label textpolicy =new Label();
			textpolicy.setText(caller);
			textpolicy.addStyleName("text_label_delete_policy");
			r_loader_space.add(textpolicy);
		}

	}

	/***
	 * Handler on click for delete policy 
	 * @param event
	 */
	@UiHandler("b_confirm_delete")
	void onClickConfirmDeletePolicy(ClickEvent event) {
		List<Long> listIdentifier=new  ArrayList<Long>();
		for (PolicyAuth policy:deletePolicy){
			GWT.log("AuthManager - Delete: "+policy.getIdpolicy());
			listIdentifier.add(policy.getIdpolicy());
		}
		AuthManagerController.eventBus.fireEvent(new RemovePoliciesEvent(listIdentifier,this));
	}
	
	
	/***
	 * Handler on click for close dialog
	 * @param event
	 */
	@UiHandler("b_confirm_exit")
	void onClickConfirmExitPolicy(ClickEvent event) {
		this.setAnimationEnabled(true);
		this.hide();
		this.clear();	
	}


	/**
	 * Method for loader animation
	 */
	public void AppLoadingView()
	{        
		Image imgLoading = new Image(AuthResources.INSTANCE.loaderIcon());
		b_confirm_delete.setEnabled(false);
		b_confirm_exit.setEnabled(false);
		r_loader_space.clear();
		r_loader_space.add(imgLoading);
	}
	public void StopAppLoadingView(){
		b_confirm_delete.setEnabled(true);
		b_confirm_exit.setEnabled(true);
		deletePolicy.clear();
		this.hide();
		this.clear();

	}

}
