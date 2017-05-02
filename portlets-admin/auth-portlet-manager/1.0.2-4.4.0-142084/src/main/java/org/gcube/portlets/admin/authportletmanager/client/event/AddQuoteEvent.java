package org.gcube.portlets.admin.authportletmanager.client.event;


import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteAddDialog;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class AddQuoteEvent extends 
GwtEvent<AddQuoteEvent.AddQuoteEventHandler> {

	
	public static Type<AddQuoteEventHandler> TYPE = new Type<AddQuoteEventHandler>();

	public static Type<AddQuoteEventHandler> getType() {
		return TYPE;
	}
	
	private List<Quote> quote;
	private QuoteAddDialog dialog;
	
	public interface AddQuoteEventHandler extends EventHandler {
		void onAdd(AddQuoteEvent event);
	}

	public interface HasAddQuoteEventHandler extends HasHandlers {
		public HandlerRegistration addAddPoliciesEventHandler(
				AddQuoteEventHandler handler);
	}
	public AddQuoteEvent(List<Quote> quote,QuoteAddDialog dialog){
		super();
		this.quote=quote;
		this.dialog=dialog;
	}
	@Override
	protected void dispatch(AddQuoteEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<AddQuoteEventHandler> getAssociatedType() {
		return TYPE;
	}
	

	public static void fire(HasHandlers source,
			AddQuoteEvent event) {
		source.fireEvent(event);
	}
	public List<Quote> getQuote() {
		return quote;
	}
	
	public QuoteAddDialog getDialog() {
		return dialog;
	}
	@Override
	public String toString() {
		return "AddQuoteEvent [quote=" + quote + ", dialog=" + dialog
				+ "]";
	}
	
}





