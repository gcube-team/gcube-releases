package org.gcube.accounting.aggregator.elaboration;

import java.util.Date;
import java.util.List;

import org.gcube.accounting.aggregator.aggregation.AggregationInfo;
import org.gcube.accounting.aggregator.persistence.CouchBaseConnector;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.documentstore.records.DSMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecoveryManager {
	
	private static Logger logger = LoggerFactory.getLogger(RecoveryManager.class);
	
	protected final Date persistStartTime;
	protected final Date persistEndTime;
	protected final Date aggregationStartDate;
	protected final Date aggregationEndDate;
	
	public RecoveryManager(Date persistStartTime, Date persistEndTime, Date aggregationStartDate, Date aggregationEndDate){
		super();
		this.persistStartTime = persistStartTime;
		this.persistEndTime = persistEndTime;
		this.aggregationStartDate = aggregationStartDate;
		this.aggregationEndDate = aggregationEndDate;
	}
	
	public void recovery() throws Exception {
		List<AggregationStatus> aggregationStatusList = CouchBaseConnector.getUnterminated(aggregationStartDate, aggregationEndDate);
		if(aggregationStatusList.size()==0){
			logger.info("Nothing to recover :)");
		}
		
		for(AggregationStatus as : aggregationStatusList){
			AggregationInfo aggregationInfo = as.getAggregationInfo();
			AggregationStatus aggregationStatus = AggregationStatus.getAggregationStatus(aggregationInfo.getRecordType(), aggregationInfo.getAggregationType(),
					aggregationInfo.getAggregationStartDate());
			logger.info("Going to Recover unterminated elaboration {}", DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
			
			Elaborator elaborator = new Elaborator(aggregationStatus, persistStartTime, persistEndTime);
			elaborator.elaborate();
		}
		
	}
	
}
