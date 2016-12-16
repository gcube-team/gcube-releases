/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;

import java.io.Serializable;

/**
 * The Class AggregatePair.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 */
public class AggregatePair implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3595300734435300803L;
	
	private TdAggregateFunction aggegrateFunction;
	private TdColumnData columnData;

	/**
	 * Instantiates a new aggregate pair.
	 */
	public AggregatePair(){
		
	}

	/**
	 * Gets the aggegrate function.
	 *
	 * @return the aggegrate function
	 */
	public TdAggregateFunction getAggegrateFunction() {
		return aggegrateFunction;
	}

	/**
	 * Gets the column data.
	 *
	 * @return the column data
	 */
	public TdColumnData getColumnData() {
		return columnData;
	}

	/**
	 * Sets the aggegrate function.
	 *
	 * @param aggegrateFunction the new aggegrate function
	 */
	public void setAggegrateFunction(TdAggregateFunction aggegrateFunction) {
		this.aggegrateFunction = aggegrateFunction;
	}

	/**
	 * Sets the column data.
	 *
	 * @param columnData the new column data
	 */
	public void setColumnData(TdColumnData columnData) {
		this.columnData = columnData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
