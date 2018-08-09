package org.gcube.accounting.aggregator.status;

import java.util.Calendar;

import org.gcube.accounting.aggregator.utility.Constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregationStateEvent {
	
	@JsonProperty
	@JsonFormat(shape= JsonFormat.Shape.STRING)
	protected AggregationState aggregationState;
	
	@JsonProperty
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = Constant.DATETIME_PATTERN)
	protected Calendar startTime;
	
	@JsonProperty
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = Constant.DATETIME_PATTERN)
	protected Calendar endTime;
	
	// Needed for Jackon Unmarshalling
	@SuppressWarnings("unused")
	private AggregationStateEvent(){}
	
	public AggregationStateEvent(AggregationState aggregationState, Calendar startTime, Calendar endTime) {
		super();
		this.aggregationState = aggregationState;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public AggregationState getAggregationState() {
		return aggregationState;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}
}
