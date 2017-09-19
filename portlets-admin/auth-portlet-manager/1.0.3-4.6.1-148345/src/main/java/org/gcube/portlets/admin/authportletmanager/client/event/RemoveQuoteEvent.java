package org.gcube.portlets.admin.authportletmanager.client.event;

import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.pagelayout.QuoteDeleteDialog;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class RemoveQuoteEvent extends 
GwtEvent<RemoveQuoteEvent.RemoveQuoteEventHandler> {

	public static Type<RemoveQuoteEventHandler> TYPE = new Type<RemoveQuoteEventHandler>();

	public static Type<RemoveQuoteEventHandler> getType() {
		return TYPE;
	}

	private List<Long> identifier;

	private QuoteDeleteDialog dialog;

	public interface RemoveQuoteEventHandler extends EventHandler {
		void onAdd(RemoveQuoteEvent event);
	}

	public interface HasRemoveQuoteEventHandler extends HasHandlers {
		public HandlerRegistration addRemoveQuoteEventHandler(
				RemoveQuoteEventHandler handler);
	}

	public RemoveQuoteEvent(List<Long> identifier,QuoteDeleteDialog modal) {
		super();
		this.identifier = identifier;
		this.dialog=modal;

	}
	@Override
	protected void dispatch(RemoveQuoteEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onAdd(this);
	}


	@Override
	public Type<RemoveQuoteEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			RemoveQuoteEvent event) {
		source.fireEvent(event);
	}
	public List<Long> getIdentifier() {
		return identifier;
	}
	public QuoteDeleteDialog getDialog() {
		return dialog;
	}
	@Override
	public String toString() {
		return "RemoveQuoteEvent [identifier=" + identifier + ", dialog="
				+ dialog + "]";
	}

}








