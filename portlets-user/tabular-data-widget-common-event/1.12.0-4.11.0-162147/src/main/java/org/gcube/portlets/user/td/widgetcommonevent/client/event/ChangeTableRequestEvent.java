package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;


/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class ChangeTableRequestEvent extends GwtEvent<ChangeTableRequestEvent.ChangeTableRequestEventHandler> {

	public static Type<ChangeTableRequestEventHandler> TYPE = new Type<ChangeTableRequestEventHandler>();
	private ChangeTableRequestType changeTableRequestType;
	private TRId trId;
	private ChangeTableWhy why;

	public interface ChangeTableRequestEventHandler extends EventHandler {
		void onChangeTableRequestEvent(ChangeTableRequestEvent event);
	}
	
	public interface HasChangeTableRequestEventHandler extends HasHandlers{
		public HandlerRegistration addChangeTableRequestEventHandler(ChangeTableRequestEventHandler handler);
	}
	
	public static void fire(HasHandlers source, ChangeTableRequestType operationCompleteType, TRId trId, ChangeTableWhy why) {
		source.fireEvent(new ChangeTableRequestEvent(operationCompleteType,trId, why));
	}

	public ChangeTableRequestEvent(ChangeTableRequestType changeTableRequestType, TRId trId, ChangeTableWhy why) {
		this.changeTableRequestType = changeTableRequestType;
		this.trId = trId;
		this.why=why;
	}

	public static Type<ChangeTableRequestEventHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeTableRequestEventHandler handler) {
		handler.onChangeTableRequestEvent(this);
	}

	@Override
	public Type<ChangeTableRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public ChangeTableRequestType getOperationCompleteType() {
		return changeTableRequestType;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ChangeTableWhy getWhy() {
		return why;
	}

	public void setWhy(ChangeTableWhy why) {
		this.why = why;
	}

	@Override
	public String toString() {
		return "ChangeTableRequestEvent [changeTableRequestType="
				+ changeTableRequestType + ", trId=" + trId + ", why=" + why
				+ "]";
	}

	
	

	
	
	
}
