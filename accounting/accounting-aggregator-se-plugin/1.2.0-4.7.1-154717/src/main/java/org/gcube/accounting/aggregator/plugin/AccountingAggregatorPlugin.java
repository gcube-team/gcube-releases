package org.gcube.accounting.aggregator.plugin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.elaboration.AggregatorManager;
import org.gcube.accounting.aggregator.elaboration.RecoveryManager;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingAggregatorPlugin extends Plugin<AccountingAggregatorPluginDeclaration> {

	private static Logger logger = LoggerFactory.getLogger(AccountingAggregatorPlugin.class);

	static {
		/// One Record per package is enough
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
	}
	
	/**
	 * Key to indicate {@link AggregationType}
	 */
	public static final String AGGREGATION_TYPE_INPUT_PARAMETER = "aggregationType";
	
	/**
	 * Day is ignored for MONTHLY aggregation
	 * Month and Day are ignored for YEARLY aggregation
	 */
	public static final String AGGREGATION_START_DATE_INPUT_PARAMETER = "aggregationStartDate";
	public static final String AGGREGATION_END_DATE_INPUT_PARAMETER = "aggregationEndDate";
	
	public static final String RESTART_FROM_LAST_AGGREGATION_DATE_INPUT_PARAMETER = "restartFromLastAggregationDate";
	
	public static final String AGGREGATION_START_DATE_DATE_FORMAT_PATTERN = "yyyy/MM/dd";
	public static final DateFormat AGGREGATION_START_DATE_DATE_FORMAT;
	
	private static final String AGGREGATION_START_END_DATE_UTC_DATE_FORMAT_PATTERN = "yyyy/MM/dd Z";
	private static final DateFormat AGGREGATION_START_END_DATE_UTC_DATE_FORMAT;
	private static final String UTC = "+0000";
	
	
	public enum ElaborationType {
		AGGREGATE, // Aggregate
		RECOVERY // Recover unterminated executions
	}
	/**
	 * Indicate which types of elaboration the plugin must perform
	 */
	public static final String ELABORATION_TYPE_INPUT_PARAMETER = "elaborationType";
	
	/**
	 * Start Day Time in UTC when the plugin is allowed to write in buckets
	 */
	public static final String PERSIST_START_TIME_INPUT_PARAMETER = "persistStartTime";
	
	public static final String PERSIST_END_TIME_INPUT_PARAMETER = "persistEndTime";
	
	public static final String PERSIST_TIME_DATE_FORMAT_PATTERN = "HH:mm";
	public static final DateFormat PERSIST_TIME_DATE_FORMAT; 
	
	public static final String LOCAL_TIME_DATE_FORMAT_PATTERN = "HH:mm Z";
	public static final DateFormat LOCAL_TIME_DATE_FORMAT;
	
	public static final String RECORD_TYPE_INPUT_PARAMETER = Record.RECORD_TYPE;
	
	static {
		AGGREGATION_START_DATE_DATE_FORMAT = Utility.getUTCDateFormat(AGGREGATION_START_DATE_DATE_FORMAT_PATTERN);
		AGGREGATION_START_END_DATE_UTC_DATE_FORMAT = Utility.getUTCDateFormat(AGGREGATION_START_END_DATE_UTC_DATE_FORMAT_PATTERN);
		
		PERSIST_TIME_DATE_FORMAT = new SimpleDateFormat(PERSIST_TIME_DATE_FORMAT_PATTERN);
		
		LOCAL_TIME_DATE_FORMAT = new SimpleDateFormat(LOCAL_TIME_DATE_FORMAT_PATTERN);
	}	

	/**
	 * @param runningPluginEvolution
	 */
	public AccountingAggregatorPlugin(AccountingAggregatorPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
	}

	private Date getPersistTime(Map<String, Object> inputs, String parameterName) throws ParseException{
		Date persistTime = null;
		if (inputs.containsKey(parameterName)) {
			String persistTimeString = (String) inputs.get(parameterName);
			persistTime = Utility.getPersistTimeDate(persistTimeString);
		}
		
		if(persistTime==null){
			throw new IllegalArgumentException("Please set a valid '" + parameterName +"' by using " + PERSIST_TIME_DATE_FORMAT_PATTERN + " format.");
		}
		
		return persistTime;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		AggregationType aggregationType = null;
		Date aggregationStartDate = null;
		Date aggregationEndDate = null;
		boolean restartFromLastAggregationDate = false;
		ElaborationType elaborationType = ElaborationType.AGGREGATE;
		
		Date persistStartTime = null;
		Date persistEndTime = null;
		
		Class<? extends UsageRecord> usageRecordClass = null;
		
		if (inputs == null || inputs.isEmpty()) {
			throw new IllegalArgumentException("The can only be launched providing valid input parameters");
		}

		if (inputs.containsKey(ELABORATION_TYPE_INPUT_PARAMETER)) {
			elaborationType = ElaborationType.valueOf((String) inputs.get(ELABORATION_TYPE_INPUT_PARAMETER));
		}
		
		persistStartTime = getPersistTime(inputs, PERSIST_START_TIME_INPUT_PARAMETER);
		
		persistEndTime = getPersistTime(inputs, PERSIST_END_TIME_INPUT_PARAMETER);
		
		if (inputs.containsKey(AGGREGATION_START_DATE_INPUT_PARAMETER)) {
			String aggregationStartDateString = (String) inputs.get(AGGREGATION_START_DATE_INPUT_PARAMETER);
			aggregationStartDate = AGGREGATION_START_END_DATE_UTC_DATE_FORMAT.parse(aggregationStartDateString + " " + UTC);
		}
		
		if (inputs.containsKey(AGGREGATION_END_DATE_INPUT_PARAMETER)) {
			String aggregationEndDateString = (String) inputs.get(AGGREGATION_END_DATE_INPUT_PARAMETER);
			aggregationEndDate = AGGREGATION_START_END_DATE_UTC_DATE_FORMAT.parse(aggregationEndDateString + " " + UTC);
		}
		
		
		switch (elaborationType) {
			case AGGREGATE:
				if (!inputs.containsKey(AGGREGATION_TYPE_INPUT_PARAMETER)) {
					throw new IllegalArgumentException("Please set required parameter '" + AGGREGATION_TYPE_INPUT_PARAMETER +"'");
				}
				aggregationType = AggregationType.valueOf((String) inputs.get(AGGREGATION_TYPE_INPUT_PARAMETER));
				
				if (inputs.containsKey(AGGREGATION_START_DATE_INPUT_PARAMETER)) {
					String aggregationStartDateString = (String) inputs.get(AGGREGATION_START_DATE_INPUT_PARAMETER);
					aggregationStartDate = AGGREGATION_START_END_DATE_UTC_DATE_FORMAT.parse(aggregationStartDateString + " " + UTC);
				}
				
				if(inputs.containsKey(RESTART_FROM_LAST_AGGREGATION_DATE_INPUT_PARAMETER)){
					restartFromLastAggregationDate = (boolean) inputs.get(RESTART_FROM_LAST_AGGREGATION_DATE_INPUT_PARAMETER);
				}
				
				if(restartFromLastAggregationDate==false && aggregationStartDate==null){
					throw new IllegalArgumentException("Aggregation Start Date cannot be found. Please provide it as parameter or set '" + RESTART_FROM_LAST_AGGREGATION_DATE_INPUT_PARAMETER + "' input parameter to 'true'.");
				}
				
				if (inputs.containsKey(RECORD_TYPE_INPUT_PARAMETER)) {
					usageRecordClass = (Class<? extends UsageRecord>) RecordUtility.getRecordClass((String) inputs.get(RECORD_TYPE_INPUT_PARAMETER));
				}
				
				AggregatorManager aggregatorManager = new AggregatorManager(aggregationType, restartFromLastAggregationDate, aggregationStartDate, aggregationEndDate);
				aggregatorManager.elaborate(persistStartTime, persistEndTime, usageRecordClass);
				
				break;

			case RECOVERY:
				RecoveryManager recoveryManager = new RecoveryManager(persistStartTime, persistEndTime, aggregationStartDate, aggregationEndDate);
				recoveryManager.recovery();
				break;
				
			default:
				throw new IllegalArgumentException("No ElaborationType provided. You should not be here. Please Contact the administrator");
		}
		
	}

	/** {@inheritDoc} */
	@Override
	protected void onStop() throws Exception {
		logger.trace("Stopping execution of {}, UUID : {}", AccountingAggregatorPluginDeclaration.NAME, this.uuid);
		Thread.currentThread().interrupt();
	}

	
	
}
