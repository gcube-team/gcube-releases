package org.gcube.portlets.admin.authportletmanager.client.event;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteAddDialog;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class UpdateQuoteEvent extends GwtEvent<UpdateQuoteEvent.UpdateQuoteEventHandler> {

	
	
	public static Type<UpdateQuoteEventHandler> TYPE = new Type<UpdateQuoteEventHandler>();

	public static Type<UpdateQuoteEventHandler> getType() {
		return TYPE;
	}
	
	private Quote quote;
	private QuoteAddDialog dialog;

	public interface UpdateQuoteEventHandler extends EventHandler {
		void onAdd(UpdateQuoteEvent event);
	}

	public interface HasUpdateQuoteEventHandler extends HasHandlers {
		public HandlerRegistration addUpdateQuoteEventHandler(
				UpdateQuoteEventHandler handler);
	}
	
	public UpdateQuoteEvent(Quote quote,QuoteAddDialog dialog){
		super();
		this.quote=quote;
		this.dialog=dialog;
	}
	
	@Override
	protected void dispatch(UpdateQuoteEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}
	
	
	@Override
	public Type<UpdateQuoteEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			UpdateQuoteEvent event) {
		source.fireEvent(event);
	}

	public Quote getQuote() {
		return quote;
	}

	public QuoteAddDialog getDialog() {
		return dialog;
	}

	@Override
	public String toString() {
		return "UpdateQuoteEvent [quote=" + quote + ", dialog=" + dialog
				+ "]";
	}


	

	

	
}
