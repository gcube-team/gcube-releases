package org.gcube.accounting.aggregator.plugin;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.elaboration.Elaborator;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTest {

	private static Logger logger = LoggerFactory.getLogger(Elaborator.class);

	@Test
	public void test() throws InterruptedException {
		Calendar start = Utility.getUTCCalendarInstance();
		logger.debug("Elaboration of Records started at {}", Constant.DEFAULT_DATE_FORMAT.format(start.getTime()));
		// Thread.sleep(TimeUnit.MINUTES.toMillis(2) +
		// TimeUnit.SECONDS.toMillis(2));
		Thread.sleep(TimeUnit.SECONDS.toMillis(12));
		Calendar end = Utility.getUTCCalendarInstance();

		long duration = end.getTimeInMillis() - start.getTimeInMillis();
		String durationForHuman = Utility.getHumanReadableDuration(duration);
		logger.debug("Elaboration of Records ended at {}. Duration {}", Constant.DEFAULT_DATE_FORMAT.format(end.getTime()),
				durationForHuman);

	}

	@Test
	public void classesTest() throws InstantiationException, IllegalAccessException {
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());

		Map<String, Class<? extends Record>> recordClasses = RecordUtility.getRecordClassesFound();
		for (String recordType : recordClasses.keySet()) {
			Class<? extends Record> recordClass = recordClasses.get(recordType);
			if (recordClass.newInstance() instanceof UsageRecord
					&& !(recordClass.newInstance() instanceof AggregatedUsageRecord<?, ?>)) {
				@SuppressWarnings("unchecked")
				Class<? extends UsageRecord> usageRecordClazz = (Class<? extends UsageRecord>) recordClass;
				logger.debug("Getting {} : {}", usageRecordClazz, recordType);
			} else {
				logger.debug("Discarding {} : {}", recordClass, recordType);
			}
		}
	}

	@Test
	public void cicleWithPercentage() {
		int rowToBeElaborated = 76543;
		int tenPercentOfNumberOfRows = (rowToBeElaborated / 10) + 1;

		int elaborated;
		for (elaborated = 0; elaborated < rowToBeElaborated; elaborated++) {
			if (elaborated % tenPercentOfNumberOfRows == 0) {
				int elaboratedPercentage = elaborated * 100 / rowToBeElaborated;
				logger.debug("Elaborated {} of {} (about {}%)", elaborated, rowToBeElaborated, elaboratedPercentage);
			}
		}
		logger.debug("Elaborated {} of {} ({}%)", elaborated, rowToBeElaborated, 100);
	}

	private static final String ZIP_SUFFIX = ".zip";

	@Test
	public void testStringFormatter() {
		String name = "filename";
		int count = 1;
		String formatted = String.format("%s-%02d%s", name, count, ZIP_SUFFIX);
		logger.debug("{}", formatted);
	}

	@Test
	public void testCalendarDisplayName() {

		for (AggregationType aggregationType : AggregationType.values()) {
			logger.info("{} Aggregation is not allowed for the last {} {}", aggregationType,
					aggregationType.getNotAggregableBefore(),
					aggregationType.name().toLowerCase().replace("ly", "s").replaceAll("dais", "days"));
		}

	}

	@Test
	public void elaboratorTest() throws Exception {
		for (AggregationType aggregationType : AggregationType.values()) {

			Calendar aggregationStartTime = Utility.getUTCCalendarInstance();
			switch (aggregationType) {
			case DAILY:
				break;

			case MONTHLY:
				aggregationStartTime.set(Calendar.DAY_OF_MONTH, 1);
				break;

			case YEARLY:
				aggregationStartTime.set(Calendar.DAY_OF_MONTH, 1);
				aggregationStartTime.set(Calendar.MONTH, Calendar.JANUARY);
				break;

			default:
				break;
			}

			aggregationStartTime.set(Calendar.HOUR_OF_DAY, 0);
			aggregationStartTime.set(Calendar.MINUTE, 0);
			aggregationStartTime.set(Calendar.SECOND, 0);
			aggregationStartTime.set(Calendar.MILLISECOND, 0);

			aggregationStartTime.add(aggregationType.getCalendarField(), -aggregationType.getNotAggregableBefore());

			Date aggregationEndTime = Utility.getEndDateFromStartDate(aggregationType, aggregationStartTime.getTime(),
					1);

			AggregationInfo aggregationInfo = new AggregationInfo("ServiceUsageRecord", aggregationType,
					aggregationStartTime.getTime(), aggregationEndTime);
			AggregationStatus aggregationStatus = new AggregationStatus(aggregationInfo);

			Elaborator elaborator = new Elaborator(aggregationStatus, Utility.getPersistTimeDate("8:00"), Utility.getPersistTimeDate("18:00"));

			boolean allowed = elaborator.isAggregationAllowed();
			if (!allowed) {
				logger.info("AggregationStartTime {}. {} Aggregation is not allowed for the last {} {}",
						aggregationType.getDateFormat().format(aggregationStartTime.getTime()), aggregationType,
						aggregationType.getNotAggregableBefore(),
						aggregationType.name().toLowerCase().replace("ly", "s").replaceAll("dais", "days"));
			}
		}

	}
	
	@Test
	public void testEnd(){
		Calendar aggregationStartCalendar = Utility.getAggregationStartCalendar(2017, Calendar.MARCH, 1);
		Date aggregationStartDate = aggregationStartCalendar.getTime();
		Date aggregationEndDate = Utility.getEndDateFromStartDate(AggregationType.MONTHLY, aggregationStartDate, 1);
		
		logger.info("{} -> {}", 
				Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate), 
				Constant.DEFAULT_DATE_FORMAT.format(aggregationEndDate));
		
	}
		
	@Test
	public void testUTCStartAndTime() throws ParseException{
		String persistTimeString = Utility.getPersistTimeParameter(8, 00);
		Date endTime = Utility.getPersistTimeDate(persistTimeString);
		
		Calendar now = Calendar.getInstance();
		
		Utility.isTimeElapsed(now, endTime);
	}

}
