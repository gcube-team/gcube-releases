package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.OperatorsClassificationRequestType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Operators Classification Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OperatorsClassificationEvent
		extends
		GwtEvent<OperatorsClassificationEvent.OperatorsClassificationEventHandler> {

	public static Type<OperatorsClassificationEventHandler> TYPE = new Type<OperatorsClassificationEventHandler>();
	private OperatorsClassificationRequestType operatorsClassificationRequestType;
	private String classificationName;
	private OperatorsClassification operatorsClassification;
	private String operatorId;

	public interface OperatorsClassificationEventHandler extends EventHandler {
		void onOperatorsClassification(OperatorsClassificationEvent event);
	}

	public interface HasOperatorsClassificationEventHandler extends HasHandlers {
		public HandlerRegistration addOperatorsClassificationEventHandler(
				OperatorsClassificationEventHandler handler);
	}

	public OperatorsClassificationEvent(
			OperatorsClassification operatorsClassification) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.Default;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = null;
	}

	public OperatorsClassificationEvent(String classificationName,
			OperatorsClassification operatorsClassification) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.classificationName = classificationName;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = null;
	}

	public OperatorsClassificationEvent(
			OperatorsClassification operatorsClassification, String operatorId) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.Default;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = operatorId;
	}

	public OperatorsClassificationEvent(String classificationName,
			OperatorsClassification operatorsClassification, String operatorId) {
		this.operatorsClassificationRequestType = OperatorsClassificationRequestType.ByName;
		this.classificationName = classificationName;
		this.operatorsClassification = operatorsClassification;
		this.operatorId = operatorId;
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

	public static void fire(HasHandlers source,
			OperatorsClassificationEvent event) {
		source.fireEvent(event);
	}

	public OperatorsClassificationRequestType getOperatorsClassificationRequestType() {
		return operatorsClassificationRequestType;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public OperatorsClassification getOperatorsClassification() {
		return operatorsClassification;
	}

	public String getOperatorId() {
		return operatorId;
	}

	@Override
	public String toString() {
		return "OperatorsClassificationEvent [operatorsClassificationRequestType="
				+ operatorsClassificationRequestType
				+ ", classificationName="
				+ classificationName
				+ ", operatorsClassification="
				+ operatorsClassification + ", operatorId=" + operatorId + "]";
	}

}
