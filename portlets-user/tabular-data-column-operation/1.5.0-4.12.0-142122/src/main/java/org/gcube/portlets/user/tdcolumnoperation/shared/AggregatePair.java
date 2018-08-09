/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 *
 */
public class AggregatePair implements Serializable{



	/**
	 * 
	 */
	private static final long serialVersionUID = -418852144259831391L;
	
	
	private TdAggregateFunction aggegrateFunction;
	private ColumnData columnData;

	public AggregatePair(){
		
	}

	public TdAggregateFunction getAggegrateFunction() {
		return aggegrateFunction;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setAggegrateFunction(TdAggregateFunction aggegrateFunction) {
		this.aggegrateFunction = aggegrateFunction;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AggregatePair [aggegrateFunction=");
		builder.append(aggegrateFunction);
		builder.append(", columnData=");
		builder.append(columnData);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
