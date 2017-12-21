package org.gcube.accounting.aggregator.aggregation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.documentstore.records.aggregation.AggregationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini  (ISTI - CNR)
 */
public class AggregatorBuffer {

	public static Logger logger = LoggerFactory.getLogger(AggregatorBuffer.class);

	protected List<AggregatedRecord<?, ?>> aggregatedRecords;

	public AggregatorBuffer() {
		aggregatedRecords = new ArrayList<AggregatedRecord<?, ?>>();
	}

	@SuppressWarnings("rawtypes")
	protected static AggregatedRecord instantiateAggregatedRecord(Record record) throws Exception {
		String recordType = record.getRecordType();
		Class<? extends AggregatedRecord> clz = RecordUtility.getAggregatedRecordClass(recordType);
		Class[] argTypes = { record.getClass() };
		Constructor<? extends AggregatedRecord> constructor = clz.getDeclaredConstructor(argTypes);
		Object[] arguments = { record };
		return constructor.newInstance(arguments);
	}

	@SuppressWarnings("rawtypes")
	public static AggregatedRecord getAggregatedRecord(Record record) throws Exception {

		AggregatedRecord aggregatedRecord;
		if (record instanceof AggregatedRecord) {
			// the record is already an aggregated version
			aggregatedRecord = (AggregatedRecord) record;
		} else {
			aggregatedRecord = instantiateAggregatedRecord(record);
		}

		return aggregatedRecord;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void madeAggregation(AggregatedRecord<?, ?> record) throws InvalidValueException {
		boolean found = false;
		for (AggregatedRecord aggregatedRecord : aggregatedRecords) {
			if (!(aggregatedRecord instanceof AggregatedRecord)) {
				continue;
			}
			AggregationUtility aggregationUtility = new AggregationUtility(aggregatedRecord);
			// verify a record is aggregable
			if (aggregationUtility.isAggregable(record)) {
				try {
					Calendar aggregatedRecordCreationTime = aggregatedRecord.getCreationTime();
					Calendar recordCreationTime = record.getCreationTime();
					Calendar creationtime = aggregatedRecordCreationTime.before(recordCreationTime) ? aggregatedRecordCreationTime : recordCreationTime;
					
					aggregatedRecord.aggregate((AggregatedRecord) record);
					// Patch to maintain earlier creation time
					aggregatedRecord.setCreationTime(creationtime);
					found = true;
					break;
				} catch (NotAggregatableRecordsExceptions e) {
					logger.debug("{} is not usable for aggregation", aggregatedRecord);
				}
			}
		}
		if (!found) {
			aggregatedRecords.add(record);
			return;
		}
	}

	public List<AggregatedRecord<?, ?>> getAggregatedRecords() {
		return aggregatedRecords;
	}

	public void aggregate(AggregatedRecord<?, ?> record) throws Exception {
		if (record != null) {
			madeAggregation(record);
		}
	}
}
