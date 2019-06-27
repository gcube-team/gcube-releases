package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.client.type.OperatorsClassificationRequestType;
import org.gcube.data.analysis.dataminermanagercl.shared.perspective.PerspectiveType;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OperatorsClassificationEvent
		extends GwtEvent<OperatorsClassificationEvent.OperatorsClassificationEventHandler> {

	public static Type<OperatorsClassificationEventHandler> TYPE = new Type<OperatorsClassificationEventHandler>();
	private OperatorsClassificationRequestType operatorsClassificationRequestType;
	private PerspectiveType perspectiveType;
	private OperatorsClassification operatorsClassification;
	private String operatorId;
	private boolean refresh;

	public interface OperatorsClassificationEventHandler extends EventHandler {
		void onOperatorsClassification(OperatorsClassificationEvent event);
	}

	public interface HasOperatorsClassificationEventHandler extends HasHandlers {
		public HandlerRegistration addOperatorsClassificationEventHandler(OperatorsClassificationEventHandler handler);
	}

	public OperatorsClassificationEvent(OperatorsClassification operatorsClassification, boolean refresh) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.Default;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = null;
		this.perspectiveType = null;
		this.refresh = refresh;
	}

	public OperatorsClassificationEvent(PerspectiveType perspectiveType,
			OperatorsClassification operatorsClassification, boolean refresh) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.perspectiveType = perspectiveType;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = null;
		this.refresh = refresh;
	}

	public OperatorsClassificationEvent(OperatorsClassification operatorsClassification, String operatorId,
			boolean refresh) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.Default;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = operatorId;
		this.perspectiveType = null;
		this.refresh = refresh;
	}

	public OperatorsClassificationEvent(PerspectiveType perspectiveType,
			OperatorsClassification operatorsClassification, String operatorId, boolean refresh) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.perspectiveType = perspectiveType;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = operatorId;
		this.refresh = refresh;
	}

	@Override
	protected void dispatch(OperatorsClassificationEventHandler handler) {
		handler.onOperatorsClassification(this);
	}

	@Override
	public Type<OperatorsClassificationEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<OperatorsClassificationEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, OperatorsClassificationEvent event) {
		source.fireEvent(event);
	}

	public OperatorsClassificationRequestType getOperatorsClassificationRequestType() {
		return operatorsClassificationRequestType;
	}

	public PerspectiveType getPerspectiveType() {
		return perspectiveType;
	}

	public OperatorsClassification getOperatorsClassification() {
		return operatorsClassification;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public boolean isRefresh() {
		return refresh;
	}

	@Override
	public String toString() {
		return "OperatorsClassificationEvent [operatorsClassificationRequestType=" + operatorsClassificationRequestType
				+ ", perspectiveType=" + perspectiveType + ", operatorsClassification=" + operatorsClassification
				+ ", operatorId=" + operatorId + ", refresh=" + refresh + "]";
	}

}
