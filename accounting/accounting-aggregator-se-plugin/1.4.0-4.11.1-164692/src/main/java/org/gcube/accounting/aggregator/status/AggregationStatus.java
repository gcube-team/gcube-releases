package org.gcube.accounting.aggregator.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregationStatus {
	
	private static Logger logger = LoggerFactory.getLogger(AggregationStatus.class);
	
	protected AggregationInfo aggregationInfo;
	
	@JsonProperty
	protected UUID uuid;
	
	@JsonProperty
	protected int originalRecordsNumber;
	
	@JsonProperty
	protected int aggregatedRecordsNumber;
	
	@JsonProperty
	protected int recoveredRecordNumber;
	
	@JsonProperty
	protected int malformedRecordNumber;
	
	@JsonProperty
	protected float percentage;
	
	@JsonProperty(required=false)
	protected String context;
	
	@JsonProperty(required=false)
	protected AggregationStatus previous;
	
	// Last observed status
	@JsonFormat(shape= JsonFormat.Shape.STRING)
	@JsonProperty
	protected AggregationState aggregationState;
	
	@JsonProperty
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = Constant.DATETIME_PATTERN)
	protected Calendar lastUpdateTime;
	
	// List of Status Event Changes
	@JsonProperty
	protected List<AggregationStateEvent> aggregationStateEvents;
	
	// Needed for Jackon Unmarshalling
	@SuppressWarnings("unused")
	private AggregationStatus(){}
	
	public static AggregationStatus getLast(String recordType, AggregationType aggregationType, Date aggregationStartDate, Date aggregationEndDate) throws Exception{
		return CouchBaseConnector.getLast(recordType, aggregationType, aggregationStartDate, aggregationEndDate);
	}
	
	public static List<AggregationStatus> getUnterminated(String recordType, AggregationType aggregationType) throws Exception{
		return CouchBaseConnector.getUnterminated(recordType, aggregationType, null, null);
	}
	
	public static AggregationStatus getAggregationStatus(String recordType, AggregationType aggregationType, Date aggregationStartDate) throws Exception{
		return CouchBaseConnector.getAggregationStatus(recordType, aggregationType, aggregationStartDate);
	}
	
	public AggregationStatus(AggregationInfo aggregationInfo) throws Exception {
		this.aggregationInfo = aggregationInfo;
		this.aggregationStateEvents = new ArrayList<>();
		this.uuid = UUID.randomUUID();
		this.malformedRecordNumber = 0;
		this.previous = null;
	}
	
	public AggregationStatus(AggregationStatus aggregationStatus) throws Exception {
		this.aggregationInfo = new AggregationInfo(aggregationStatus.getAggregationInfo());
		this.aggregationStateEvents = new ArrayList<>();
		this.uuid = aggregationStatus.getUUID();
		this.malformedRecordNumber = 0;
		this.previous = aggregationStatus;
	}
	
	public AggregationInfo getAggregationInfo() {
		return aggregationInfo;
	}
	
	public synchronized void setAggregationState(AggregationState aggregationState, Calendar startTime, boolean sync) throws Exception {
		Calendar endTime = Utility.getUTCCalendarInstance();
		
		logger.info("Going to Set {} for {} to {}. StartTime {}, EndTime {} [Duration : {}]",
				AggregationState.class.getSimpleName(),
				aggregationInfo, aggregationState.name(), 
				Constant.DEFAULT_DATE_FORMAT.format(startTime.getTime()),
				Constant.DEFAULT_DATE_FORMAT.format(endTime.getTime()), 
				Utility.getHumanReadableDuration(endTime.getTimeInMillis() - startTime.getTimeInMillis()));
		
		this.aggregationState = aggregationState;
		this.lastUpdateTime = endTime;
		
		AggregationStateEvent aggregationStatusEvent = new AggregationStateEvent(aggregationState, startTime, endTime);
		aggregationStateEvents.add(aggregationStatusEvent);
		
		if(sync){
			CouchBaseConnector.upsertAggregationStatus(this);
		}
	}

	public void setRecordNumbers(int originalRecordsNumber, int aggregatedRecordsNumber, int malformedRecordNumber) {
		this.recoveredRecordNumber = originalRecordsNumber - aggregatedRecordsNumber;
		this.percentage = originalRecordsNumber!=0 ? (100 * recoveredRecordNumber) / originalRecordsNumber : 0;
		logger.info("Original records are {} ({} were malformed). Aggregated records are {}. Difference {}. We recover {}% of Documents",
				originalRecordsNumber, malformedRecordNumber, aggregatedRecordsNumber, recoveredRecordNumber, percentage);
		this.malformedRecordNumber = malformedRecordNumber;
		this.originalRecordsNumber = originalRecordsNumber;
		this.aggregatedRecordsNumber = aggregatedRecordsNumber;
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public void setAggregation(AggregationInfo aggregation) {
		this.aggregationInfo = aggregation;
	}
	
	public int getOriginalRecordsNumber() {
		return originalRecordsNumber;
	}

	public int getAggregatedRecordsNumber() {
		return aggregatedRecordsNumber;
	}

	public AggregationState getAggregationState() {
		return aggregationState;
	}

	public List<AggregationStateEvent> getAggregationStateEvents() {
		return aggregationStateEvents;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getMalformedRecordNumber() {
		return malformedRecordNumber;
	}

	public void setMalformedRecordNumber(int malformedRecordNumber) {
		this.malformedRecordNumber = malformedRecordNumber;
	}

	public Calendar getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void updateLastUpdateTime(boolean sync) throws Exception {
		this.lastUpdateTime = Utility.getUTCCalendarInstance();
		if(sync){
			CouchBaseConnector.upsertAggregationStatus(this);
		}
	}

}
