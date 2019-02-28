package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.perspective.PerspectiveType;
import org.gcube.portlets.user.dataminermanager.client.type.OperatorsClassificationRequestType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OperatorsClassificationRequestEvent
		extends GwtEvent<OperatorsClassificationRequestEvent.OperatorsClassificationRequestEventHandler> {

	public static Type<OperatorsClassificationRequestEventHandler> TYPE = new Type<OperatorsClassificationRequestEventHandler>();
	private OperatorsClassificationRequestType operatorsClassificationRequestType;
	private PerspectiveType perspectiveType;
	private boolean operatorId;
	private boolean refresh;

	public interface OperatorsClassificationRequestEventHandler extends EventHandler {
		void onRequest(OperatorsClassificationRequestEvent event);
	}

	public interface HasOperatorsClassificationRequestEventHandler extends HasHandlers {
		public HandlerRegistration addOperatorsClassificationRequestEventHandler(
				OperatorsClassificationRequestEventHandler handler);
	}

	
	public OperatorsClassificationRequestEvent(OperatorsClassificationRequestType operatorsClassificationRequestType, PerspectiveType perspectiveType, boolean operatorId, boolean refresh) {
		this.operatorsClassificationRequestType = operatorsClassificationRequestType;
		this.perspectiveType = perspectiveType;
		this.operatorId = operatorId;
		this.refresh = refresh;
	}

	@Override
	protected void dispatch(OperatorsClassificationRequestEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<OperatorsClassificationRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<OperatorsClassificationRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, OperatorsClassificationRequestEvent event) {
		source.fireEvent(event);
	}

	public OperatorsClassificationRequestType getOperatorsClassificationRequestType() {
		return operatorsClassificationRequestType;
	}

	public PerspectiveType getPerspectiveType() {
		return perspectiveType;
	}

	public boolean isOperatorId() {
		return operatorId;
	}

	public boolean isRefresh() {
		return refresh;
	}

	@Override
	public String toString() {
		return "OperatorsClassificationRequestEvent [operatorsClassificationRequestType="
				+ operatorsClassificationRequestType + ", perspectiveType=" + perspectiveType + ", operatorId="
				+ operatorId + ", refresh=" + refresh + "]";
	}

}
