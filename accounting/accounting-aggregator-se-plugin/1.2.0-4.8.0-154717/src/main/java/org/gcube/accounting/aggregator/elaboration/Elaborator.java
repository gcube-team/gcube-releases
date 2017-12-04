package org.gcube.accounting.aggregator.elaboration;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.aggregation.Aggregator;
import org.gcube.accounting.aggregator.directory.FileSystemDirectoryStructure;
import org.gcube.accounting.aggregator.persist.Persist;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector.SUFFIX;
import org.gcube.accounting.aggregator.plugin.AccountingAggregatorPlugin;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.documentstore.records.DSMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Elaborator {

	private static Logger logger = LoggerFactory.getLogger(Elaborator.class);

	public final static String ORIGINAL_SUFFIX = ".original.json";
	public final static String AGGREGATED_SUFFIX = ".aggregated.json";
	
	protected final AggregationStatus aggregationStatus;
	protected final Date persistStartTime;
	protected final Date persistEndTime;
	
	public Elaborator(AggregationStatus aggregationStatus, Date persistStartTime, Date persistEndTime) throws Exception {
		this.aggregationStatus = aggregationStatus;
		this.persistStartTime = persistStartTime;
		this.persistEndTime = persistEndTime;
	}

	public boolean isAggregationAllowed(){
		AggregationInfo aggregationInfo = aggregationStatus.getAggregationInfo(); 
		Date aggregationStartDate  = aggregationInfo.getAggregationStartDate();
		AggregationType aggregationType = aggregationInfo.getAggregationType();
		
		boolean allowed = false;
		
		Calendar calendar = Utility.getUTCCalendarInstance();
		switch (aggregationType) {
			case DAILY:
				break;
				
			case MONTHLY:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				break;
	
			case YEARLY:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, Calendar.JANUARY);
				break;
				
			default:
				break;
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(aggregationType.getCalendarField(), -aggregationType.getNotAggregableBefore());
		
		logger.trace("Checking if {} is before {}", 
				aggregationType.getDateFormat().format(aggregationStartDate), 
				aggregationType.getDateFormat().format(calendar.getTime()));
		
		if(aggregationStartDate.before(calendar.getTime())){
			allowed = true;
		}
		
		return allowed;
	}
	
	public void elaborate() throws Exception {
		Calendar startTime = Utility.getUTCCalendarInstance();
		
		AggregationInfo aggregationInfo = aggregationStatus.getAggregationInfo();
		Date aggregationStartDate = aggregationInfo.getAggregationStartDate(); 
		AggregationType aggregationType = aggregationInfo.getAggregationType();
		
		if(!isAggregationAllowed()){
			logger.info("Too early to start aggregation {}. {} Aggregation is not allowed for the last {} {}", 
					DSMapper.getObjectMapper().writeValueAsString(aggregationStatus), 
					aggregationType, 
					aggregationType.getNotAggregableBefore(), 
					aggregationType.name().toLowerCase().replace("ly", "s").replaceAll("dais", "days"));
			return;
		}
		
		if(aggregationStatus.getAggregationState()==null){
			aggregationStatus.setState(AggregationState.STARTED, startTime, true);
		}else{
			if(aggregationStatus.getAggregationState()==AggregationState.COMPLETED){
				logger.info("{} is {}. Nothing to do :-). \n Details {}", 
						AggregationStatus.class.getSimpleName(),
						aggregationStatus.getAggregationState(),
						DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
				return;
			}
			
			Calendar now = Utility.getUTCCalendarInstance();
			now.add(Constant.CALENDAR_FIELD_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED, -Constant.UNIT_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED);
			if(aggregationStatus.getLastUpdateTime().after(now)){
				logger.info("Cannot elaborate {} because has been modified in the last {} ", 
						DSMapper.getObjectMapper().writeValueAsString(aggregationStatus),
						Constant.UNIT_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED, Constant.CALENDAR_FIELD_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED==Calendar.HOUR_OF_DAY? "hours" : "unit");
				return;
			}
			
			aggregationStatus.updateLastUpdateTime(true);
			
		}
		
		String recordType = aggregationInfo.getRecordType();
		
		FileSystemDirectoryStructure fileSystemDirectoryStructure = new FileSystemDirectoryStructure();
		File elaborationDirectory = fileSystemDirectoryStructure.getTargetFolder(aggregationType, aggregationStartDate);
		
		
		Bucket srcBucket = CouchBaseConnector.getInstance().getBucket(recordType, aggregationInfo.getAggregationType(), SUFFIX.src);
		Bucket dstBucket = CouchBaseConnector.getInstance().getBucket(recordType, aggregationInfo.getAggregationType(), SUFFIX.dst);
		
		File originalRecordsbackupFile = getOriginalRecordsBackupFile(elaborationDirectory, recordType);
		File aggregateRecordsBackupFile = getAggregatedRecordsBackupFile(originalRecordsbackupFile);

		Aggregator aggregator = new Aggregator(aggregationStatus, srcBucket, originalRecordsbackupFile,
				aggregateRecordsBackupFile);
		aggregator.aggregate();
		
		
		
		Calendar now = Utility.getUTCCalendarInstance();
		/*
		 * now is passed as argument to isTimeElapsed function to avoid situation
		 * (even rare) where both check are valid because the first invocation happen 
		 * before midnight and the second after midnight (so in the next day).
		 */
		if (Utility.isTimeElapsed(now, persistStartTime) && !Utility.isTimeElapsed(now, persistEndTime)) {
			Persist persist = new Persist(aggregationStatus, srcBucket, dstBucket, originalRecordsbackupFile, aggregateRecordsBackupFile);
			persist.recover();
		}else{
			logger.info("Cannot delete/insert document before {} and after {}.", AccountingAggregatorPlugin.LOCAL_TIME_DATE_FORMAT.format(persistStartTime), AccountingAggregatorPlugin.LOCAL_TIME_DATE_FORMAT.format(persistEndTime));
		}
	}
	
	protected File getOriginalRecordsBackupFile(File elaborationDirectory, String name) throws Exception {
		AggregationInfo aggregationInfo = aggregationStatus.getAggregationInfo(); 
		Date aggregationStartDate  = aggregationInfo.getAggregationStartDate();
		AggregationType aggregationType = aggregationInfo.getAggregationType();
		
		DateFormat dateFormat  = aggregationType.getDateFormat();
		String dateString = dateFormat.format(aggregationStartDate);
		String[] splittedDate = dateString.split(AggregationType.DATE_SEPARATOR);
		
		String backupFileName = splittedDate[splittedDate.length-1] + "-" +name;
		File originalRecordsbackupFile = new File(elaborationDirectory, backupFileName + ORIGINAL_SUFFIX);
		return originalRecordsbackupFile;
	}

	protected File getAggregatedRecordsBackupFile(File originalRecordsbackupFile) throws Exception {
		File aggregateRecordsBackupFile = new File(originalRecordsbackupFile.getParentFile(),
				originalRecordsbackupFile.getName().replace(ORIGINAL_SUFFIX, AGGREGATED_SUFFIX));
		return aggregateRecordsBackupFile;
	}

}
