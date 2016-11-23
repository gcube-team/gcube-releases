/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class AggregateByTimeOperationEvent extends GwtEvent<AggregateByTimeOperationEventHandler>  {
	
	public static final GwtEvent.Type<AggregateByTimeOperationEventHandler> TYPE = new Type<AggregateByTimeOperationEventHandler>();
	private TdColumnDefinition timeColumns;
	private List<TdColumnDefinition> otherColumns;
	

	@Override
	public Type<AggregateByTimeOperationEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(AggregateByTimeOperationEventHandler handler) {
		handler.onAggregateByTimeOp(this);
	}
	
	public AggregateByTimeOperationEvent(TdColumnDefinition timeColumns, List<TdColumnDefinition> others){
		this.timeColumns = timeColumns;
		this.otherColumns = others;
	}

	/**
	 * @return the timeColumns
	 */
	public TdColumnDefinition getTimeColumns() {
		return timeColumns;
	}

	/**
	 * @param timeColumns the timeColumns to set
	 */
	public void setTimeColumns(TdColumnDefinition timeColumns) {
		this.timeColumns = timeColumns;
	}

	/**
	 * @return the otherColumns
	 */
	public List<TdColumnDefinition> getOtherColumns() {
		return otherColumns;
	}

	/**
	 * @param otherColumns the otherColumns to set
	 */
	public void setOtherColumns(List<TdColumnDefinition> otherColumns) {
		this.otherColumns = otherColumns;
	}
}
