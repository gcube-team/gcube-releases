package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.WidgetRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
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
public class WidgetRequestEvent extends
		GwtEvent<WidgetRequestEvent.WidgetRequestEventHandler> {

	public static Type<WidgetRequestEventHandler> TYPE = new Type<WidgetRequestEventHandler>();
	private WidgetRequestType widgetRequestType;
	private TRId trId;
	private String columnLocalId;
	private String columnName;
	private RequestProperties requestProperties;

	public interface WidgetRequestEventHandler extends EventHandler {
		void onWidgetRequest(WidgetRequestEvent event);
	}

	public interface HasWidgetRequestEventHandler extends HasHandlers {
		public HandlerRegistration addWidgetRequestEventHandler(
				WidgetRequestEventHandler handler);
	}

	public WidgetRequestEvent(WidgetRequestType closeType) {
		this.widgetRequestType = closeType;
	}

	public WidgetRequestType getWidgetRequestType() {
		return widgetRequestType;
	}

	@Override
	protected void dispatch(WidgetRequestEventHandler handler) {
		handler.onWidgetRequest(this);
	}

	@Override
	public Type<WidgetRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<WidgetRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			WidgetRequestType widgetRequestType) {
		source.fireEvent(new WidgetRequestEvent(widgetRequestType));
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}
	
	
	public String getColumnLocalId() {
		return columnLocalId;
	}

	public void setColumnLocalId(String columnLocalId) {
		this.columnLocalId = columnLocalId;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public RequestProperties getRequestProperties() {
		return requestProperties;
	}

	public void setRequestProperties(RequestProperties requestProperties) {
		this.requestProperties = requestProperties;
	}

	@Override
	public String toString() {
		return "WidgetRequestEvent [trId=" + trId + ", columnLocalId="
				+ columnLocalId + ", columnName=" + columnName
				+ ", requestProperties=" + requestProperties + "]";
	}

	

}
