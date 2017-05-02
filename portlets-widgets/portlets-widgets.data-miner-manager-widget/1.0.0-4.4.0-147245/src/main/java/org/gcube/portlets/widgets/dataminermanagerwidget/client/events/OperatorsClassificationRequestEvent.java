package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.OperatorsClassificationRequestType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Request Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OperatorsClassificationRequestEvent
		extends
		GwtEvent<OperatorsClassificationRequestEvent.OperatorsClassificationRequestEventHandler> {

	public static Type<OperatorsClassificationRequestEventHandler> TYPE = new Type<OperatorsClassificationRequestEventHandler>();
	private OperatorsClassificationRequestType operatorsClassificationRequestType;
	private String classificationName;
	private boolean operatorId;

	public interface OperatorsClassificationRequestEventHandler extends
			EventHandler {
		void onRequest(OperatorsClassificationRequestEvent event);
	}

	public interface HasOperatorsClassificationRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addOperatorsClassificationRequestEventHandler(
				OperatorsClassificationRequestEventHandler handler);
	}

	/*public OperatorsClassificationRequestEvent() {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.Default;
		this.operatorId = false;
	}*/

	public OperatorsClassificationRequestEvent(String classificationName,
			boolean operatorId) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.classificationName = classificationName;
		this.operatorId = operatorId;
	}

	/*public OperatorsClassificationRequestEvent(String classificationName) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.classificationName = classificationName;
		this.operatorId = false;
	}*/

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

	public static void fire(HasHandlers source,
			OperatorsClassificationRequestEvent event) {
		source.fireEvent(event);
	}

	public OperatorsClassificationRequestType getOperatorsClassificationRequestType() {
		return operatorsClassificationRequestType;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public boolean isOperatorId() {
		return operatorId;
	}

	@Override
	public String toString() {
		return "OperatorsClassificationRequestEvent [operatorsClassificationRequestType="
				+ operatorsClassificationRequestType
				+ ", classificationName="
				+ classificationName + ", operatorId=" + operatorId + "]";
	}

}
