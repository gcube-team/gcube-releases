package org.gcube.portlets.admin.authportletmanager.client.pagelayout;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.event.RemoveQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.resource.AuthResources;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog Box for confirm delete quote 
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class QuoteDeleteDialog extends DialogBox   {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, QuoteDeleteDialog> {
	}

	private ArrayList<Quote> deleteQuote;
	//private Boolean error=false;


	@UiField
	Row  r_loader_space;

	@UiField
	Button b_confirm_delete;

	@UiField
	Button b_confirm_exit;

	public QuoteDeleteDialog(ArrayList<Quote> listDeleteQuote) {

		this.setWidget(binder.createAndBindUi(this));
		//this.setAutoHideEnabled(true);
		this.setGlassEnabled(true);
		this.setWidth("400px");
		this.setHeight("200px");
		this.setAnimationEnabled(isVisible());
		//this.center();
		this.setStyleName("modal_delete");
		this.setPopupPosition(((Window.getClientWidth() - 400) / 2),
				((Window.getClientHeight()-200)/2) );



		this.deleteQuote=listDeleteQuote;
		for (Quote quote:listDeleteQuote){
			String caller=quote.getCallerAsString();
			Label textquote =new Label();
			textquote.setText("Caller:"+caller);
			r_loader_space.add(textquote);
		}

	}

	/***
	 * Handler on click for delete quote 
	 * @param event
	 */
	@UiHandler("b_confirm_delete")
	void onClickConfirmDeleteQuote(ClickEvent event) {
		List<Long> listIdentifier=new  ArrayList<Long>();
		for (Quote quote:deleteQuote){
			GWT.log("AuthManager - Delete Quote: "+quote.getIdQuote());
			listIdentifier.add(quote.getIdQuote());
		}
		AuthManagerController.eventBus.fireEvent(new RemoveQuoteEvent(listIdentifier,this));
	}
	
	
	/***
	 * Handler on click for close dialog
	 * @param event
	 */
	@UiHandler("b_confirm_exit")
	void onClickConfirmExitQuote(ClickEvent event) {
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
		deleteQuote.clear();
		this.hide();
		this.clear();

	}

}
