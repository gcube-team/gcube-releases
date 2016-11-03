package org.gcube.documentstore.records.aggregation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.persistence.ExecutorUtils;
//import org.gcube.documentstore.persistence.DefaultPersitenceExecutor;
import org.gcube.documentstore.persistence.PersistenceBackend;
import org.gcube.documentstore.persistence.PersistenceBackendConfiguration;
import org.gcube.documentstore.persistence.PersistenceExecutor;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.documentstore.records.implementation.ConfigurationGetPropertyValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class AggregationScheduler implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(AggregationScheduler.class);

	protected int totalBufferedRecords;
	protected Map<String, List<Record>> bufferedRecords;

	protected final PersistenceExecutor persistenceExecutor;
	
	//protected final ScheduledExecutorService scheduler;

	public static int INITIAL_DELAY = 30;
	public static int DELAY = 30;
	public final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

	public static final String AGGREGATION_SCHEDULER_TIME="AggregationSchedulerTime";

	public static final String BUFFER_RECORD_TIME="BufferRecordTime";
	public static final String BUFFER_RECORD_NUMBER="BufferRecordNumber";

	/**
	 * The Max amount of time for reload a configuration
	 * TODO Get from configuration
	 */
	public static long TIME_RELOAD_CONFIGURATION =1000*60*60*12; // 12 hour
	

	/**
	 * The time for first 
	 */
	public static long TIME_LOAD_CONFIGURATION=0L;

	/**
	 * Define the MAX number of Record to buffer.
	 * TODO Get from configuration
	 */
	protected  static int MAX_RECORDS_NUMBER = 100;



	/**
	 * The Max amount of time elapsed form last record before after that
	 * the buffered record are persisted even if  
	 * TODO Get from configuration
	 */
	protected  static long OLD_RECORD_MAX_TIME_ELAPSED = 1000*60*30; // 10 min  



	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor){


		return new BufferAggregationScheduler(persistenceExecutor);
	}



	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor, PersistenceBackendConfiguration configuration) throws NumberFormatException, Exception{

		ConfigurationGetPropertyValues properties = new ConfigurationGetPropertyValues();
		Properties prop =properties.getPropValues();
		Integer delay=0;
		Integer maxRecordNumber=0;
		Integer maxRecordTime=0;
		
		
		if (prop==null){
			//get value from service end point
			logger.trace("Configuration from service end point");
			delay=Integer.parseInt(configuration.getProperty(AGGREGATION_SCHEDULER_TIME)); 
			maxRecordTime=Integer.parseInt(configuration.getProperty(BUFFER_RECORD_TIME)); 
			maxRecordNumber=Integer.parseInt(configuration.getProperty(BUFFER_RECORD_NUMBER));
		}
		else{
			//get value from properties file
			logger.trace("Configuration from properties file");
			delay=Integer.parseInt(prop.getProperty("delay"));
			maxRecordNumber=Integer.parseInt(prop.getProperty("maxrecordnumber")); 
			maxRecordTime=Integer.parseInt(prop.getProperty("maxtimenumber"));
		}

		if (delay != 0){
			DELAY=delay;
			INITIAL_DELAY=delay;
		}
		if (maxRecordNumber != 0)
			MAX_RECORDS_NUMBER=maxRecordNumber;

		if (maxRecordTime != 0)
			OLD_RECORD_MAX_TIME_ELAPSED=maxRecordTime*1000*60;	

		TIME_LOAD_CONFIGURATION = Calendar.getInstance().getTimeInMillis();
		logger.trace("Start Instance for time load configuration {}", TIME_LOAD_CONFIGURATION);
		
		
		
		return new BufferAggregationScheduler(persistenceExecutor);
	}


	protected AggregationScheduler(PersistenceExecutor persistenceExecutor){
		this.bufferedRecords = new HashMap<String, List<Record>>();
		this.totalBufferedRecords = 0;
		this.persistenceExecutor = persistenceExecutor;
		//this.scheduler = Executors.newScheduledThreadPool(1);
		//this.scheduler.scheduleAtFixedRate(this, INITIAL_DELAY, DELAY, TIME_UNIT);
		ScheduledFuture<?> future =ExecutorUtils.scheduler.scheduleAtFixedRate(this, INITIAL_DELAY, DELAY, TIME_UNIT);
		logger.trace("Thread scheduler created in {} ", this.toString());
		logger.trace("Reload configuration every {}", TIME_RELOAD_CONFIGURATION);
		logger.trace("Aggregated for max record {}", MAX_RECORDS_NUMBER);
		logger.trace("Aggregated for max time {}", OLD_RECORD_MAX_TIME_ELAPSED);
		
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
			madeAggregation(record);
		}
		if(isTimeToPersist( MAX_RECORDS_NUMBER ,  OLD_RECORD_MAX_TIME_ELAPSED ) || forceFlush){
			reallyFlush(persistenceExecutor);
			
		}

		/**
		 * reload a configuration
		 */ 
		long now = Calendar.getInstance().getTimeInMillis();
		if((now - TIME_LOAD_CONFIGURATION) >= TIME_RELOAD_CONFIGURATION){
			ReloadConfiguration();
		}
	}

	protected void ReloadConfiguration()throws Exception{

		new Thread(){  

			public void run(){

				Integer delay=0;
				Integer maxRecordNumber=0;
				Integer maxRecordTime=0;
				try {

					ConfigurationGetPropertyValues properties = new ConfigurationGetPropertyValues();
					Properties prop =properties.getPropValues();
					if (prop!=null){
						//get value from properties file
						logger.trace("Reload Configuration from properties file");
						delay=Integer.parseInt(prop.getProperty("delay"));
						maxRecordNumber=Integer.parseInt(prop.getProperty("maxrecordnumber")); 
						maxRecordTime=Integer.parseInt(prop.getProperty("maxtimenumber"));
					}
					else{
						ServiceLoader<PersistenceBackend> serviceLoader = ServiceLoader.load(PersistenceBackend.class);
						PersistenceBackendConfiguration configuration =null;
						for (PersistenceBackend found : serviceLoader) {
							Class<? extends PersistenceBackend> foundClass = found.getClass();
							try {
								String foundClassName = foundClass.getSimpleName();
								logger.trace("Testing {}", foundClassName);

								configuration = PersistenceBackendConfiguration.getInstance(foundClass);
								if(configuration==null){
									continue;
								}
								logger.debug("{} will be used.", foundClassName);


							} catch (Exception e) {
								logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.", foundClass.getSimpleName()), e);
							}
						}

						if (configuration!=null){
							//get value from service end point
							logger.trace("Reload Configuration from service end point");
							delay=Integer.parseInt(configuration.getProperty(AGGREGATION_SCHEDULER_TIME));
							maxRecordTime=Integer.parseInt(configuration.getProperty(BUFFER_RECORD_TIME));
							maxRecordNumber=Integer.parseInt(configuration.getProperty(BUFFER_RECORD_NUMBER));

						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.", e.getLocalizedMessage()), e);
				
				}
				//configure new value
				if (delay != 0){
					DELAY=delay;
					INITIAL_DELAY=delay;
				}
				if (maxRecordNumber != 0)
					MAX_RECORDS_NUMBER=maxRecordNumber;

				if (maxRecordTime != 0)
					OLD_RECORD_MAX_TIME_ELAPSED=maxRecordTime*1000*60;	
				//reset a timer
				TIME_LOAD_CONFIGURATION = Calendar.getInstance().getTimeInMillis();

				logger.trace("Aggregated for max record {}", MAX_RECORDS_NUMBER);
				logger.trace("Aggregated for max time {}", OLD_RECORD_MAX_TIME_ELAPSED);
			}


		}.start();


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
		logger.trace("reallyFlush It is time to persist buffered records {}", Arrays.toString(recordToPersist));
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
		aggregate(record, persistenceExecutor, false);
	}


	protected abstract boolean isTimeToPersist(int maxRecordNumber, long oldRecordMaxTime);

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.flush(persistenceExecutor);
		} catch (Exception e) {
			logger.error("Error flushing Buffered Records",e);
		}
	}

}

