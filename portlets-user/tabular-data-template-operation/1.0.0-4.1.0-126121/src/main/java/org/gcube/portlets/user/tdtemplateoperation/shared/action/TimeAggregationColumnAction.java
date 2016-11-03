/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;

/**
 * The Class AggregationColumnSession.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 */
public class TimeAggregationColumnAction implements TabularDataAction, Serializable{

	/**
	 * 
	 */
	public static final String TIME_AGGREGATION = "Time Aggregation";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8403381408297240780L;
	private List<TdColumnData> groupColumns;
	private List<AggregatePair> aggregateFunctionPairs;
	private ServerObjectId serverObjectId;
	private List<TdColumnData> timeColumns;
	private TdPeriodType periodType;
	
	/**
	 * @param timeColumns
	 * @param periodType
	 */
	public TimeAggregationColumnAction(List<TdColumnData> timeColumns,
			TdPeriodType periodType) {
		this.timeColumns = timeColumns;
		this.periodType = periodType;
	}
	
	

	/**
	 * @param groupColumns
	 * @param aggregateFunctionPairs
	 * @param serverObjectId
	 * @param timeColumns
	 * @param periodType
	 */
	public TimeAggregationColumnAction(List<TdColumnData> groupColumns,
			List<AggregatePair> aggregateFunctionPairs,
			ServerObjectId serverObjectId, List<TdColumnData> timeColumns,
			TdPeriodType periodType) {
		this.groupColumns = groupColumns;
		this.aggregateFunctionPairs = aggregateFunctionPairs;
		this.serverObjectId = serverObjectId;
		this.timeColumns = timeColumns;
		this.periodType = periodType;
	}



	/**
	 * @return the periodType
	 */
	public TdPeriodType getPeriodType() {
		return periodType;
	}

	/**
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(TdPeriodType periodType) {
		this.periodType = periodType;
	}

	/**
	 * @return the timeColumns
	 */
	public List<TdColumnData> getTimeColumns() {
		return timeColumns;
	}

	/**
	 * @param timeColumns the timeColumns to set
	 */
	public void setTimeColumns(List<TdColumnData> timeColumns) {
		this.timeColumns = timeColumns;
	}


	/**
	 * Instantiates a new aggregation column session.
	 */
	public TimeAggregationColumnAction() {
	}
	
	/**
	 * Adds the group column.
	 *
	 * @param cd the cd
	 * @return true, if successful
	 */
	public boolean addGroupColumn(TdColumnData cd){
		if(groupColumns==null)
			groupColumns = new ArrayList<TdColumnData>();
		
		if(cd!=null){
			groupColumns.add(cd);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds the function on column.
	 *
	 * @param aggregate the aggregate
	 * @return true, if successful
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

	/**
	 * Gets the group columns.
	 *
	 * @return the group columns
	 */
	public List<TdColumnData> getGroupColumns() {
		return groupColumns;
	}

	/**
	 * Gets the aggregate function pairs.
	 *
	 * @return the aggregate function pairs
	 */
	public List<AggregatePair> getAggregateFunctionPairs() {
		return aggregateFunctionPairs;
	}

	/**
	 * Sets the group columns.
	 *
	 * @param groupColumns the new group columns
	 */
	public void setGroupColumns(List<TdColumnData> groupColumns) {
		this.groupColumns = groupColumns;
	}

	/**
	 * Sets the aggregate function pairs.
	 *
	 * @param aggregateFunctionPairs the new aggregate function pairs
	 */
	public void setAggregateFunctionPairs(List<AggregatePair> aggregateFunctionPairs) {
		this.aggregateFunctionPairs = aggregateFunctionPairs;
	}

	/**
	 * @return the serverObjectId
	 */
	public ServerObjectId getServerObjectId() {
		return serverObjectId;
	}

	/**
	 * @param serverObjectId the serverObjectId to set
	 */
	public void setServerObjectId(ServerObjectId serverObjectId) {
		this.serverObjectId = serverObjectId;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.TemplateAction#getId()
	 */
	@Override
	public String getId() {
		return TimeAggregationColumnAction.class.getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AggregationColumnSession [groupColumns=");
		builder.append(groupColumns);
		builder.append(", aggregateFunctionPairs=");
		builder.append(aggregateFunctionPairs);
		builder.append(", serverObjectId=");
		builder.append(serverObjectId);
		builder.append(", timeColumns=");
		builder.append(timeColumns);
		builder.append(", periodType=");
		builder.append(periodType);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getDescription()
	 */
	@Override
	public String getDescription() {
		return TIME_AGGREGATION;
	}
}
