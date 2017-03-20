/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.event.shared.GwtEvent;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 29, 2013
 *
 */
public class OpenResultEvent extends GwtEvent<OpenResultEventHandler> {
	
	public static final GwtEvent.Type<OpenResultEventHandler> TYPE = new Type<OpenResultEventHandler>();

	public static enum ResultType {TABULARTABLE, COLLATERALTABLE}
	private TRId trId;
	private ResultType resultType;
	
	@Override
	public Type<OpenResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenResultEventHandler handler) {
		handler.onResultOpenSelect(this);	
	}
	
	/**
	 * 
	 */
	public OpenResultEvent(ResultType type, TRId trId) {
		this.trId = trId;
		this.resultType = type;
	}


	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpenResultEvent [trId=");
		builder.append(trId);
		builder.append(", resultType=");
		builder.append(resultType);
		builder.append("]");
		return builder.toString();
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

}
