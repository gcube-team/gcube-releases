package org.gcube.accounting.aggregator.elaboration;

import java.util.Date;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.documentstore.records.DSMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatorManager {

	private static Logger logger = LoggerFactory.getLogger(AggregatorManager.class);

	public final static String ACCOUNTING_MANAGER_BUCKET_NAME = "AccountingManager";

	protected final AggregationType aggregationType;

	protected Date aggregationStartDate;
	protected Date aggregationEndDate;

	protected final boolean restartFromLastAggregationDate;

	public AggregatorManager(AggregationType aggregationType, boolean restartFromLastAggregationDate,
			Date aggregationStartDate, Date aggregationEndDate) throws Exception {
		this.aggregationType = aggregationType;
		this.aggregationStartDate = aggregationStartDate;
		this.aggregationEndDate = aggregationEndDate;
		this.restartFromLastAggregationDate = restartFromLastAggregationDate;
	}

	protected Date getEndDateFromStartDate() {
		return Utility.getEndDateFromStartDate(aggregationType, aggregationStartDate, 1);
	}

	protected AggregationStatus createAggregationStatus(String recordType) throws Exception {
		Date aggregationEndDate = getEndDateFromStartDate();
		AggregationInfo aggregationInfo = new AggregationInfo(recordType, aggregationType, aggregationStartDate,
				aggregationEndDate);
		AggregationStatus aggregationStatus = new AggregationStatus(aggregationInfo);
		return aggregationStatus;
	}

	public void elaborate(Date persistStartTime, Date persistEndTime, Class<? extends UsageRecord> usageRecordClass)
			throws Exception {

		CouchBaseConnector couchBaseConnector = CouchBaseConnector.getInstance();

		for (String recordType : couchBaseConnector.getRecordTypes()) {

			if (usageRecordClass != null && usageRecordClass.newInstance().getRecordType().compareTo(recordType) != 0) {
				continue;
			}

			if (recordType.compareTo(ACCOUNTING_MANAGER_BUCKET_NAME) == 0) {
				continue;
			}

			AggregationStatus aggregationStatus = null;
			if (restartFromLastAggregationDate) {
				AggregationStatus lastAggregationStatus = AggregationStatus.getLast(recordType, aggregationType,
						aggregationStartDate, aggregationEndDate);
				
				// I don't check if this aggregation is COMPLETED because this
				// is responsibility of Recovery Process
				if (lastAggregationStatus != null) {
					this.aggregationStartDate = lastAggregationStatus.getAggregationInfo().getAggregationEndDate();
					logger.info("Last got AggregationStatus is {}. Restarting from {}",
							DSMapper.getObjectMapper().writeValueAsString(lastAggregationStatus),
							Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate));
				}

			}

			aggregationStatus = AggregationStatus.getAggregationStatus(recordType, aggregationType,
					aggregationStartDate);

			if (aggregationStatus == null) {
				aggregationStatus = createAggregationStatus(recordType);
			}

			if (aggregationEndDate != null && aggregationStartDate.after(aggregationEndDate)) {
				logger.info("StartDate {} is after last Aggregation End Date allowed {}. Nothing to do.",
						Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate),
						Constant.DEFAULT_DATE_FORMAT.format(aggregationEndDate));
				return;
			}

			Elaborator elaborator = new Elaborator(aggregationStatus, persistStartTime, persistEndTime);
			elaborator.elaborate();

		}

	}

}
