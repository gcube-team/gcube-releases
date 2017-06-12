package org.gcube.accounting.aggregator.madeaggregation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.accounting.aggregator.plugin.Utility;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.documentstore.records.aggregation.AggregationUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 *
 */
public  class Aggregation  {

	public static Logger logger = LoggerFactory.getLogger(Aggregation.class);

	//count buffer records
	protected int totalBufferedRecords;

	//list Aggregate record	
	protected Map<String, List<AggregatedRecord<?,?>>>  bufferedRecords = new HashMap<String, List<AggregatedRecord<?,?>>>();

	public Aggregation() {
		super();
	}

	@SuppressWarnings("rawtypes")
	protected static AggregatedRecord instantiateAggregatedRecord(Record record) throws Exception{

		String recordType = record.getRecordType();
		Class<? extends AggregatedRecord> clz = RecordUtility.getAggregatedRecordClass(recordType);
		Class[] argTypes = { record.getClass() };
		Constructor<? extends AggregatedRecord> constructor = clz.getDeclaredConstructor(argTypes);
		Object[] arguments = {record};
		return constructor.newInstance(arguments);
	}

	@SuppressWarnings("rawtypes")
	public static AggregatedRecord getAggregatedRecord(Record record) throws Exception {

		AggregatedRecord aggregatedRecord;
		if(record instanceof AggregatedRecord){
			// the record is already an aggregated version
			aggregatedRecord = (AggregatedRecord) record;
		}else{
			aggregatedRecord = instantiateAggregatedRecord(record);
		}

		return aggregatedRecord;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void madeAggregation(AggregatedRecord<?,?> record) throws InvalidValueException{
		String recordType = record.getRecordType();
		List<AggregatedRecord<?,?>> records;
		if(this.bufferedRecords.containsKey(recordType)){
			records = this.bufferedRecords.get(recordType);
			boolean found = false;
			for(AggregatedRecord bufferedRecord : records){
				if(!(bufferedRecord instanceof AggregatedRecord)){
					continue;
				}
				AggregationUtility util = new AggregationUtility(bufferedRecord);
				//verify a record is aggregable						
				if (util.isAggregable(record)){
					try {
						AggregatedRecord bufferedAggregatedRecord = (AggregatedRecord) bufferedRecord;
						//logger.debug("if -- madeAggregation aggregate");
						bufferedAggregatedRecord.aggregate((AggregatedRecord) record);						
						//patch for not changed a creation time
						bufferedAggregatedRecord.setCreationTime(record.getCreationTime());
						found = true;
						break;
					} catch(NotAggregatableRecordsExceptions e) {
						logger.debug("{} is not usable for aggregation", bufferedRecord);
					} 
				}
			}
			if(!found){
				records.add(record);
				totalBufferedRecords++;
				return;
			}

		}else{
			records = new ArrayList<AggregatedRecord<?,?>>();
			try {
				records.add(getAggregatedRecord(record));
			} catch (Exception e) {
				logger.debug("pre Exception but records");	
				records.add(record);
				logger.debug("Exception but records Add e:{}",e);
			}
			totalBufferedRecords++;
			this.bufferedRecords.put(recordType, records);
		}		
	}

	/**
	 * Reset buffer records
	 */
	protected void clear(){
		totalBufferedRecords=0;
		bufferedRecords.clear();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<JsonDocument> reallyFlush() throws Exception{
		if(totalBufferedRecords==0){
			return null;
		}
		List<JsonDocument> listDocumentToPersist = new ArrayList<JsonDocument>();
		Collection<List<AggregatedRecord<?,?>>> collectionValuesRecord = bufferedRecords.values();
		for(List<AggregatedRecord<?,?>> records : collectionValuesRecord){
			for(Record thisRecord: records){

				String id=thisRecord.getId();
				JsonObject accounting = JsonObject.empty();
				for (String key : thisRecord.getResourceProperties().keySet()){
					Object value=thisRecord.getResourceProperty(key);
					if (!Utility.checkType(value)) 
						value=(String)value.toString();
					accounting.put(key, value);	
				}
				JsonDocument document = JsonDocument.create(id, accounting);	
				listDocumentToPersist.add(document);


			}
		}
		clear();
		return listDocumentToPersist;
	}

	/**
	 * Get an usage records and try to aggregate with other buffered
	 * Usage Record.
	 * @param singleRecord the Usage Record To Buffer 
	 * @throws Exception if fails
	 */
	public void aggregate(AggregatedRecord<?,?> record) throws Exception {
		if(record!=null){
			//logger.debug("aggregate:{}",record.toString());
			madeAggregation(record);
		}
	}
}

