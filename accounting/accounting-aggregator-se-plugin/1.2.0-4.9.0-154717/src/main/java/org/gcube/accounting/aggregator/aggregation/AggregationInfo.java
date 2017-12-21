package org.gcube.accounting.aggregator.aggregation;

import java.util.Date;

import org.gcube.accounting.aggregator.utility.Constant;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregationInfo {

	protected String recordType;
	protected AggregationType aggregationType;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=Constant.DATETIME_PATTERN)
	protected Date aggregationStartDate;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=Constant.DATETIME_PATTERN)
	protected Date aggregationEndDate;
	
	// Needed for Jackon Unmarshalling
	@SuppressWarnings("unused")
	private AggregationInfo(){}
	
	public AggregationInfo(String recordType, AggregationType aggregationType, Date aggregationStartDate,
			Date aggregationEndDate) {
		super();
		this.recordType = recordType;
		this.aggregationType = aggregationType;
		this.aggregationStartDate = aggregationStartDate;
		this.aggregationEndDate = aggregationEndDate;
	}
	
	public Date getAggregationStartDate() {
		return aggregationStartDate;
	}


	public Date getAggregationEndDate() {
		return aggregationEndDate;
	}

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public String getRecordType() {
		return recordType;
	}
	
	@Override
	public String toString(){
		return String.format("[%s %s %s -> %s]",	 
			recordType, aggregationType,
			aggregationType.getDateFormat().format(aggregationStartDate), 
			aggregationType.getDateFormat().format(aggregationEndDate));
	}

}
