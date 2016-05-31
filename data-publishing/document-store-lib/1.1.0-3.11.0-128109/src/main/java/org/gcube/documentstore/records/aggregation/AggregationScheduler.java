package org.gcube.documentstore.records.aggregation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.persistence.PersistenceExecutor;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class AggregationScheduler implements Runnable {
	
	public static Logger logger = LoggerFactory.getLogger(AggregationScheduler.class);
	
	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor){
		return new BufferAggregationScheduler(persistenceExecutor);
	}
	
	protected int totalBufferedRecords;
	protected Map<String, List<Record>> bufferedRecords;
	
	protected final PersistenceExecutor persistenceExecutor;
	protected final ScheduledExecutorService scheduler;
	
	public final static int INITIAL_DELAY = 10;
	public final static int DELAY = 10;
	public final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	
	protected AggregationScheduler(PersistenceExecutor persistenceExecutor){
		this.bufferedRecords = new HashMap<String, List<Record>>();
		this.totalBufferedRecords = 0;
		this.persistenceExecutor = persistenceExecutor;
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.scheduler.scheduleAtFixedRate(this, INITIAL_DELAY, DELAY, TIME_UNIT);
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
	protected void madeAggregation(Record record){
		String recordType = record.getRecordType();
	
		List<Record> records;
		
		if(this.bufferedRecords.containsKey(recordType)){
			records = this.bufferedRecords.get(recordType);
			boolean found = false;
			
			for(Record bufferedRecord : records){
				if(!(bufferedRecord instanceof AggregatedRecord)){
					continue;
				}
				
				try {
					AggregatedRecord bufferedAggregatedRecord = (AggregatedRecord) bufferedRecord;
					logger.trace("Trying to use {} for aggregation.", bufferedAggregatedRecord);
					
					if(record instanceof AggregatedRecord){
						// TODO check compatibility using getAggregable
						bufferedAggregatedRecord.aggregate((AggregatedRecord) record);
					}else{
						bufferedAggregatedRecord.aggregate((Record) record);
					}
					
					logger.trace("Aggregated Record is {}", bufferedAggregatedRecord);
					found = true;
					break;
				} catch(NotAggregatableRecordsExceptions e) {
					logger.trace("{} is not usable for aggregation", bufferedRecord);
				} 
			}
			
			if(!found){
				try {
					records.add(getAggregatedRecord(record));
				} catch (Exception e) {
					records.add(record);
				}
				totalBufferedRecords++;
				return;
			}
			
			
		}else{
			records = new ArrayList<Record>();
			try {
				records.add(getAggregatedRecord(record));
			} catch (Exception e) {
				records.add(record);
			}
			totalBufferedRecords++;
			this.bufferedRecords.put(recordType, records);
		}
		
	}
	
	public void flush(PersistenceExecutor persistenceExecutor) throws Exception{
		aggregate(null, persistenceExecutor, true);
	}
	
	protected abstract void schedulerSpecificClear();
	
	protected void clear(){
		totalBufferedRecords=0;
		bufferedRecords.clear();
		schedulerSpecificClear();
	}
	
	protected synchronized void aggregate(Record record, PersistenceExecutor persistenceExecutor, boolean forceFlush) throws Exception {
		if(record!=null){
			logger.trace("Trying to aggregate {}", record);
			madeAggregation(record);
		}
		
		if(isTimeToPersist() || forceFlush){
			reallyFlush(persistenceExecutor);
		}
	}
	
	protected void reallyFlush(PersistenceExecutor persistenceExecutor) throws Exception{
		if(totalBufferedRecords==0){
			return;
		}
		Record[] recordToPersist = new Record[totalBufferedRecords];
		int i = 0;
		Collection<List<Record>> values = bufferedRecords.values();
		for(List<Record> records : values){
			for(Record thisRecord: records){
				recordToPersist[i] = thisRecord;
				i++;
			}
		}
		
		logger.trace("It is time to persist buffered records {}", Arrays.toString(recordToPersist));
		persistenceExecutor.persist(recordToPersist);
		
		clear();
	}
	
	/**
	 * Get an usage records and try to aggregate with other buffered
	 * Usage Record.
	 * @param singleRecord the Usage Record To Buffer
	 * @return true if is time to persist the buffered Usage Record
	 * @throws Exception if fails
	 */
	public void aggregate(Record record, PersistenceExecutor persistenceExecutor) throws Exception {
		logger.trace("Going to aggregate {}", record);
		aggregate(record, persistenceExecutor, false);
	}
	
	
	protected abstract boolean isTimeToPersist();

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.flush(persistenceExecutor);
		} catch (Exception e) {
			logger.error("Error flushing Buffered Records");
		}
	}
	
}
