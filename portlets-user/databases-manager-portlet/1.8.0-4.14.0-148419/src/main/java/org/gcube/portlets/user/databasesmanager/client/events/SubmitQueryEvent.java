package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SubmitQueryEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SubmitQueryEvent extends GwtEvent<SubmitQueryEventHandler> {

	public static Type<SubmitQueryEventHandler> TYPE = new Type<SubmitQueryEventHandler>();
	private int dialogID;
	
	public SubmitQueryEvent(int ID){
		dialogID=ID;		
	}

	@Override
	public Type<SubmitQueryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SubmitQueryEventHandler handler) {
		handler.onSubmitQuery(this);
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SUBMIT_QUERY_EVENT;
	}
	
	public int getDialogID(){
		return dialogID;
	}
}
