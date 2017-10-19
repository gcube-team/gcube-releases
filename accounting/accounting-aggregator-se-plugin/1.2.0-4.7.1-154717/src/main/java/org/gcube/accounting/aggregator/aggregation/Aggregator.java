package org.gcube.accounting.aggregator.aggregation;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Aggregator {

	private static Logger logger = LoggerFactory.getLogger(Aggregator.class);

	private static final String TMP_SUFFIX = ".tmp";

	protected final AggregationStatus aggregationStatus;
	protected final Bucket bucket;
	protected final File originalRecordsbackupFile;
	protected final File aggregateRecordsBackupFile;
	protected final File malformedRecordsFile;
	protected int malformedRecordNumber;
	
	protected Calendar startTime;

	public Aggregator(AggregationStatus aggregationStatus, Bucket bucket, File originalRecordsbackupFile, File aggregateRecordsBackupFile) {
		this.aggregationStatus = aggregationStatus;
		
		this.bucket = bucket;
		this.originalRecordsbackupFile = originalRecordsbackupFile;
		this.aggregateRecordsBackupFile = aggregateRecordsBackupFile;
		this.malformedRecordsFile = Utility.getMalformatedFile(aggregateRecordsBackupFile);
	}

	public void aggregate() throws Exception {
		if(AggregationState.canContinue(aggregationStatus.getAggregationState(),AggregationState.STARTED)) {
			startTime = Utility.getUTCCalendarInstance();
			ViewResult viewResult = getViewResult();
			retrieveAndAggregate(viewResult);
		}
	}
	
	/**
	 * Generate a key for map-reduce
	 * @param key 
	 * @return
	 */
	protected JsonArray generateKey(String key){		
		JsonArray arrayKey = JsonArray.create();
		for (String value : key.split("/")){
			if (!value.toString().isEmpty()){
				arrayKey.add(Integer.parseInt(value));
			}
		}		
		return arrayKey;

	}

	protected ViewResult getViewResult() throws Exception {

		DateFormat dateFormat = aggregationStatus.getAggregationInfo().getAggregationType().getDateFormat();

		String dateStartKey = dateFormat.format(aggregationStatus.getAggregationInfo().getAggregationStartDate());
		String dateEndKey = dateFormat.format(aggregationStatus.getAggregationInfo().getAggregationEndDate());

		JsonArray startKey = generateKey(dateStartKey);
		JsonArray endKey = generateKey(dateEndKey);

		DesignID designid = DesignID.valueOf(bucket.name());
		String designDocId = designid.getDesignName();

		String viewName = designid.getViewName();

		ViewQuery query = ViewQuery.from(designDocId, viewName);
		query.startKey(startKey);
		query.endKey(endKey);
		query.reduce(false);
		query.inclusiveEnd(false);

		logger.debug("View Query: designDocId:{} - viewName:{}, startKey:{} - endKey:{} ", 
				designDocId, viewName, startKey, endKey);

		try {
			return bucket.query(query);
		} catch (Exception e) {
			logger.error("Exception error VIEW", e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	private static final String USAGE_RECORD_TYPE = "usageRecordType";
	private static final String SINGLE = "Single";
	private static final String SIMPLE = "Simple";
	
	
	protected int elaborateRow(ViewRow row, AggregatorBuffer aggregatorBuffer, int originalRecordsCounter) throws Exception {
		try {
			JsonObject content = row.document().content();

			if(content.containsKey(USAGE_RECORD_TYPE)){
				String recordType = content.getString(USAGE_RECORD_TYPE);
				content.removeKey(USAGE_RECORD_TYPE);
				content.put(Record.RECORD_TYPE, recordType);
			}
			
			Boolean aggregated = false;
			if(content.containsKey(AggregatedRecord.AGGREGATED)){
				aggregated = content.getBoolean(AggregatedRecord.AGGREGATED);
			}
			
			if(!aggregated){
				String recordType = content.getString(Record.RECORD_TYPE);
				content.put(Record.RECORD_TYPE, SINGLE + recordType);
			}
			
			
			String recordType = content.getString(Record.RECORD_TYPE);
			if(recordType.contains(SIMPLE)){
				recordType.replace(SIMPLE, SINGLE);
			}
			
			String record = content.toString();
			
			// Backup the Record on local file
			Utility.printLine(originalRecordsbackupFile, record);

			// Aggregate the Record
			aggregateRow(aggregatorBuffer, record);

			++originalRecordsCounter;
			if(originalRecordsCounter%1000==0){
				int aggregatedRecordsNumber = aggregatorBuffer.getAggregatedRecords().size();
				int diff = originalRecordsCounter - aggregatedRecordsNumber;
				float percentage = (100 * diff) / originalRecordsCounter;
				logger.info("{} At the moment, the elaborated original records are {}. The Aggregated records are {}. Difference {}. We are recovering {}% of Documents",
						aggregationStatus.getAggregationInfo(), originalRecordsCounter, aggregatedRecordsNumber, diff, percentage);
			}
			
			return originalRecordsCounter;
		}catch (Exception e) {
			throw e;
		}
	}
	
	private static final int MAX_RETRY = 3;
	
	protected void retrieveAndAggregate(ViewResult viewResult) throws Exception {
		AggregatorBuffer aggregatorBuffer = new AggregatorBuffer();

		Calendar start = Utility.getUTCCalendarInstance();
		logger.debug("Elaboration of Records started at {}", Constant.DEFAULT_DATE_FORMAT.format(start.getTime()));
		
		originalRecordsbackupFile.delete();
		aggregateRecordsBackupFile.delete();
		malformedRecordsFile.delete();
		
		malformedRecordNumber = 0;
		int originalRecordsCounter = 0;
		for (ViewRow row : viewResult) {
			for(int i=1; i<=MAX_RETRY; i++){
				try {
					originalRecordsCounter = elaborateRow(row, aggregatorBuffer, originalRecordsCounter);
					break;
				}catch (RuntimeException e) {
					if(i==2){
						logger.error("Unable to elaborate {} {}. Tryed {} times.", ViewRow.class.getSimpleName(), row, i, e);
					}
				}
			}
		}

		Calendar end = Utility.getUTCCalendarInstance();
		long duration = end.getTimeInMillis() - start.getTimeInMillis();
		String durationForHuman = Utility.getHumanReadableDuration(duration);
		logger.debug("{} Elaboration of Records terminated at {}. Duration {}",
				aggregationStatus.getAggregationInfo(), Constant.DEFAULT_DATE_FORMAT.format(end.getTime()), durationForHuman);

		File aggregateRecordsBackupFileTmp = new File(aggregateRecordsBackupFile.getParent(),
				aggregateRecordsBackupFile.getName() + TMP_SUFFIX);
		aggregateRecordsBackupFileTmp.delete();
		
		// Saving Aggregated Record on local file
		logger.debug("Going to save {} to file {}", AggregatedUsageRecord.class.getSimpleName(),
				aggregateRecordsBackupFile);

		
		
		List<AggregatedRecord<?, ?>> aggregatedRecords = aggregatorBuffer.getAggregatedRecords();
		for (AggregatedRecord<?, ?> aggregatedRecord : aggregatedRecords) {
			String marshalled = DSMapper.marshal(aggregatedRecord);
			JsonObject jsonObject = JsonObject.fromJson(marshalled);
			Utility.printLine(aggregateRecordsBackupFileTmp, jsonObject.toString());
		}

		aggregateRecordsBackupFileTmp.renameTo(aggregateRecordsBackupFile);
		
		aggregationStatus.setRecordNumbers(originalRecordsCounter, aggregatedRecords.size(), malformedRecordNumber);
		aggregationStatus.setState(AggregationState.AGGREGATED, startTime, true);
	}


	protected void aggregateRow(AggregatorBuffer aggregatorBuffer, String json) throws Exception {
		Record record = RecordUtility.getRecord(json);
		try {
			record.validate();
		}catch (InvalidValueException e) {
			++malformedRecordNumber;
			Utility.printLine(malformedRecordsFile, json);
			
			if(record instanceof AggregatedServiceUsageRecord){
				if(record.getResourceProperty(AggregatedServiceUsageRecord.MIN_INVOCATION_TIME)==null){
					record.setResourceProperty(AggregatedServiceUsageRecord.MIN_INVOCATION_TIME, record.getResourceProperty(ServiceUsageRecord.DURATION));
				}
				if(record.getResourceProperty(AggregatedServiceUsageRecord.MAX_INVOCATION_TIME)==null) {
					record.setResourceProperty(AggregatedServiceUsageRecord.MAX_INVOCATION_TIME, record.getResourceProperty(ServiceUsageRecord.DURATION));
				}
				if(record.getResourceProperty(AggregatedServiceUsageRecord.CALLER_QUALIFIER)==null) {
					record.setResourceProperty(AggregatedServiceUsageRecord.CALLER_QUALIFIER, AbstractServiceUsageRecord.UNKNOWN);
				}
			}
			record.validate();
		}
		record.setId(UUID.randomUUID().toString());
		
		@SuppressWarnings("rawtypes")
		AggregatedRecord aggregatedRecord = AggregatorBuffer.getAggregatedRecord(record);
		aggregatorBuffer.aggregate(aggregatedRecord);
	}

	protected JsonDocument getJsonDocument(ViewRow row) {
		String identifier = (String) row.document().content().get("id");
		JsonDocument jsonDocument = JsonDocument.create(identifier, row.document().content());
		logger.trace("{}", jsonDocument.toString());
		return jsonDocument;
	}

}
