/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 *
 */
public class AggregationColumnSession implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2174560017436492225L;
	
	private List<ColumnData> groupColumns;
	private List<AggregatePair> aggregateFunctionPairs;
	private TRId trId;
	/**
	 * 
	 */
	public AggregationColumnSession() {
	}
	
	/**
	 * 
	 * @param cd
	 */
	public boolean addGroupColumn(ColumnData cd){
		if(groupColumns==null)
			groupColumns = new ArrayList<ColumnData>();
		
		if(cd!=null){
			groupColumns.add(cd);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param cd
	 */
	public boolean addFunctionOnColumn(AggregatePair aggregate){
		if(aggregateFunctionPairs==null)
			aggregateFunctionPairs = new ArrayList<AggregatePair>();
		
		if(aggregate!=null){
			aggregateFunctionPairs.add(aggregate);
			return true;
		}
		
		return false;
	}

	public List<ColumnData> getGroupColumns() {
		return groupColumns;
	}

	public List<AggregatePair> getAggregateFunctionPairs() {
		return aggregateFunctionPairs;
	}

	public void setGroupColumns(List<ColumnData> groupColumns) {
		this.groupColumns = groupColumns;
	}

	public void setAggregateFunctionPairs(List<AggregatePair> aggregateFunctionPairs) {
		this.aggregateFunctionPairs = aggregateFunctionPairs;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AggregationColumnSession [groupColumns=");
		builder.append(groupColumns);
		builder.append(", aggregateFunctionPairs=");
		builder.append(aggregateFunctionPairs);
		builder.append(", trId=");
		builder.append(trId);
		builder.append("]");
		return builder.toString();
	}
	
	

}
