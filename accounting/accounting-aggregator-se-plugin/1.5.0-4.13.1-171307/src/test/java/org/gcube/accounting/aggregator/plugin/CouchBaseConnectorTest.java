package org.gcube.accounting.aggregator.plugin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.records.DSMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchBaseConnectorTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(AccountingAggregatorPluginTest.class);

	@Test
	public void getLastTest() throws Exception {
		AggregationStatus aggregationStatus = CouchBaseConnector.getLast(ServiceUsageRecord.class.getSimpleName(), AggregationType.DAILY, null, null);
		logger.debug("Last : {}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
	}
	
	@Test
	public void getUnterminatedTest() throws Exception{
		List<AggregationStatus> aggregationStatuses = CouchBaseConnector.getUnterminated(ServiceUsageRecord.class.getSimpleName(), AggregationType.DAILY, null, null);
		for(AggregationStatus aggregationStatus : aggregationStatuses){
			logger.debug("Unterminated : {}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		}
	}
	
	@Test
	public void getLastTestWithConstraint() throws Exception {
		Calendar aggregationStart = Utility.getAggregationStartCalendar(2017, Calendar.JANUARY, 1);
		Calendar aggregationEnd = Utility.getAggregationStartCalendar(2017, Calendar.JANUARY, 31);
		
		AggregationStatus aggregationStatus = CouchBaseConnector.getLast(ServiceUsageRecord.class.getSimpleName(), AggregationType.DAILY, aggregationStart.getTime(), aggregationEnd.getTime());
		logger.info("Last : {}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
	}
	
	@Test
	public void getUnterminatedTestWithConstraint() throws Exception{
		Calendar aggregationStart = Utility.getAggregationStartCalendar(2017, Calendar.APRIL, 1);
		Calendar aggregationEnd = Utility.getAggregationStartCalendar(2017, Calendar.APRIL, 30);
		
		List<AggregationStatus> aggregationStatuses = CouchBaseConnector.getUnterminated(ServiceUsageRecord.class.getSimpleName(), AggregationType.DAILY, aggregationStart.getTime(), aggregationEnd.getTime());
		for(AggregationStatus aggregationStatus : aggregationStatuses){
			logger.info("Unterminated : {}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		}
	}
	
	@Test
	public void getAggregationStatusTest() throws Exception{
		Calendar aggregationStartCalendar = Utility.getAggregationStartCalendar(2017, Calendar.JUNE, 15);
		AggregationStatus aggregationStatus = CouchBaseConnector.getAggregationStatus(ServiceUsageRecord.class.getSimpleName(), AggregationType.DAILY, aggregationStartCalendar.getTime());
		logger.debug("{}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
	}
	
	@Test
	public void aggregationStatusTest() throws Exception {
		int toRemove = -36;
		
		Calendar today = Utility.getUTCCalendarInstance();
		today.add(Calendar.DAY_OF_YEAR, toRemove);
		
		String aggregationStartDateString = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.format(today.getTime());
		Date aggregationStartDate = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.parse(aggregationStartDateString);
		
		Calendar tomorrow = Utility.getUTCCalendarInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, toRemove+1);
		String aggregationEndDateString = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.format(tomorrow.getTime());
		Date aggregationEndDate = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.parse(aggregationEndDateString);
		
		
		AggregationInfo aggregation = new AggregationInfo(ServiceUsageRecord.class.newInstance().getRecordType(), AggregationType.DAILY, aggregationStartDate, aggregationEndDate);
		String aggregationString = DSMapper.getObjectMapper().writeValueAsString(aggregation);
		logger.debug("{} : {}", AggregationInfo.class.getSimpleName(), aggregationString);
		
		AggregationStatus aggregationStatus = new AggregationStatus(aggregation);
		aggregationStatus.setContext("TEST_CONTEXT");
		
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		// Set to true just for one test and restore to false
		boolean sync = true;
		
		Calendar startedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.STARTED, startedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		aggregationStatus.setRecordNumbers(100, 72, 0);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		Calendar aggregatedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.AGGREGATED, aggregatedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		Calendar addedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.ADDED, addedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		Calendar deletedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.DELETED, deletedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		Calendar completedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.COMPLETED, completedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
	}
	
	@Test
	public void createStartedElaboration() throws Exception {
		
		Calendar start = Utility.getAggregationStartCalendar(2017, Calendar.JUNE, 15);
		String aggregationStartDateString = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.format(start.getTime());
		Date aggregationStartDate = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.parse(aggregationStartDateString);
		
		Calendar end = Utility.getUTCCalendarInstance();
		end.setTime(aggregationStartDate);
		end.add(Calendar.DAY_OF_MONTH, 1);
		String aggregationEndDateString = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.format(end.getTime());
		Date aggregationEndDate = AccountingAggregatorPlugin.AGGREGATION_START_DATE_DATE_FORMAT.parse(aggregationEndDateString);
		
		
		AggregationInfo aggregation = new AggregationInfo(ServiceUsageRecord.class.newInstance().getRecordType(), AggregationType.DAILY, aggregationStartDate, aggregationEndDate);
		String aggregationString = DSMapper.getObjectMapper().writeValueAsString(aggregation);
		logger.debug("{} : {}", AggregationInfo.class.getSimpleName(), aggregationString);
		
		AggregationStatus aggregationStatus = new AggregationStatus(aggregation);
		aggregationStatus.setContext("TEST_CONTEXT");
		
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
		// Set to true just for one test and restore to false
		boolean sync = true;
		
		Calendar startedStart = Utility.getUTCCalendarInstance();
		aggregationStatus.setAggregationState(AggregationState.STARTED, startedStart, sync);
		logger.debug("{} : {}", AggregationStatus.class.getSimpleName(), DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		
	}
}
