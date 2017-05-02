package org.gcube.documentstore.records.aggregation;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.persistence.ExecutorUtils;
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
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public abstract class AggregationScheduler implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(AggregationScheduler.class);

	protected int totalBufferedRecords;
	protected Map<String, List<Record>> bufferedRecords;

	protected final PersistenceExecutor persistenceExecutor;


	public final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

	public static final String AGGREGATION_SCHEDULER_TIME = "AggregationSchedulerTime";

	public static final String BUFFER_RECORD_TIME = "BufferRecordTime";
	public static final String BUFFER_RECORD_NUMBER = "BufferRecordNumber";

	
	public static final Integer RANDOM_INIT_START=5;
	
	/**
	 * The Max amount of time for reload a configuration Get from
	 * configuration
	 */
	public static long TIME_RELOAD_CONFIGURATION = 720  ; //720 minutes (12 Hours)

	public boolean changeConfiguration = false;

	private String name;

	private AggregationConfig config;
	
	//Schedule for flush and reload configuration
	protected ScheduledFuture<?> futureFlush = null;
	protected ScheduledFuture<?> futureReload = null;

	public static AggregationScheduler newInstance(
			PersistenceExecutor persistenceExecutor, String name) {
		return new BufferAggregationScheduler(persistenceExecutor, name);
	}

	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor,
			PersistenceBackendConfiguration configuration, String name)throws NumberFormatException, Exception {
		AggregationConfig config = CheckConfiguration(configuration);
		BufferAggregationScheduler bas = new BufferAggregationScheduler(persistenceExecutor, config, name );
		return bas;
	}

	protected AggregationScheduler(PersistenceExecutor persistenceExecutor, String name) {
		this(persistenceExecutor, AggregationConfig.getDefaultConfiguration(), name);
		
	}

	protected AggregationScheduler(PersistenceExecutor persistenceExecutor, AggregationConfig config, String name) {
		this.config = config;
		this.name = name;
		
		this.bufferedRecords = new HashMap<String, List<Record>>();
		this.totalBufferedRecords = 0;
		this.persistenceExecutor = persistenceExecutor;
		schedule();
		reloadConfiguration();		
	}

	private void schedule() {
		//logger.trace("TestingThread AggregationScheduler schedule");

		if (futureFlush!=null)
			futureFlush.cancel(false);
		if ((config.getInitialDelaySet() == 0) || (config.getDelaySet() == 0)) {
			futureFlush = ExecutorUtils.scheduler.scheduleAtFixedRate(this, 1,1, TIME_UNIT);
		}
		else{
			Random random = new Random();
			Integer randStart= Math.abs(random.nextInt(RANDOM_INIT_START));
			futureFlush = ExecutorUtils.scheduler.scheduleAtFixedRate(this,config.getInitialDelaySet()+randStart, config.getDelaySet(), TIME_UNIT);
		}
		logger.trace("[{}] AggregationScheduler- Thread scheduler created in {} ",name, this.toString());
		logger.trace("[{}] AggregationScheduler- Load configuration every {}",name, TIME_RELOAD_CONFIGURATION);
		logger.trace("[{}] AggregationScheduler- Aggregated for max record {}", name, config.getMaxRecordsNumberSet());
		logger.trace("[{}] AggregationScheduler- Aggregated for max time {}", name, config.getOldRecordMaxTimeElapsedSet());
	}

	@SuppressWarnings("rawtypes")
	protected static AggregatedRecord instantiateAggregatedRecord(Record record)
			throws Exception {

		String recordType = record.getRecordType();
		Class<? extends AggregatedRecord> clz = RecordUtility.getAggregatedRecordClass(recordType);
		Class[] argTypes = { record.getClass() };
		Constructor<? extends AggregatedRecord> constructor = clz.getDeclaredConstructor(argTypes);
		Object[] arguments = { record };
		return constructor.newInstance(arguments);
	}

	@SuppressWarnings("rawtypes")
	public static AggregatedRecord getAggregatedRecord(Record record)
			throws Exception {

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
	protected void madeAggregation(Record record) {

		String recordType = record.getRecordType();
		List<Record> records;

		if (this.bufferedRecords.containsKey(recordType)) {
			records = this.bufferedRecords.get(recordType);
			boolean found = false;

			for (Record bufferedRecord : records) {
				if (!(bufferedRecord instanceof AggregatedRecord)) {
					continue;
				}
				try {
					AggregatedRecord bufferedAggregatedRecord = (AggregatedRecord) bufferedRecord;

					if (record instanceof AggregatedRecord) {
						// TODO check compatibility using getAggregable
						bufferedAggregatedRecord.aggregate((AggregatedRecord) record);
					} else {
						bufferedAggregatedRecord.aggregate((Record) record);
					}
					logger.trace("Aggregated Record is {}",bufferedAggregatedRecord);
					found = true;
					break;
				} catch (NotAggregatableRecordsExceptions e) {
					logger.trace("{} is not usable for aggregation",bufferedRecord);
				}
			}

			if (!found) {
				try {
					records.add(getAggregatedRecord(record));
				} catch (Exception e) {
					records.add(record);
				}
				totalBufferedRecords++;
				return;
			}

		} else {
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

	public void flush(PersistenceExecutor persistenceExecutor) throws Exception {		
		aggregate(null, persistenceExecutor, true);
	}

	protected abstract void schedulerSpecificClear();

	protected void clear() {
		totalBufferedRecords = 0;
		bufferedRecords.clear();
		schedulerSpecificClear();
	}

	protected synchronized void aggregate(Record record,
			PersistenceExecutor persistenceExecutor, boolean forceFlush)
					throws Exception {

		if (record != null) {
			madeAggregation(record);
		}
		if (isTimeToPersist(this.config.getMaxRecordsNumberSet(), this.config.getOldRecordMaxTimeElapsedSet())|| forceFlush) {
			//logger.trace("TestingThread AggregationScheduler aggregate PersistenceExecutor:{}",persistenceExecutor.getClass().getName());
			reallyFlush(persistenceExecutor);
		}

	}


	protected void reallyFlush(PersistenceExecutor persistenceExecutor)
			throws Exception {
		if (totalBufferedRecords == 0) {
			return;
		}
		Record[] recordToPersist = new Record[totalBufferedRecords];
		int i = 0;
		Collection<List<Record>> values = bufferedRecords.values();
		for (List<Record> records : values) {
			for (Record thisRecord : records) {
				recordToPersist[i] = thisRecord;
				i++;
			}
		}
		logger.trace("[{}]reallyFlush It is time to persist buffered records {}",name, Arrays.toString(recordToPersist));
		persistenceExecutor.persist(recordToPersist);
		clear();
	}

	/**
	 * Get an usage records and try to aggregate with other buffered Usage
	 * Record.
	 * 
	 * @param singleRecord
	 *            the Usage Record To Buffer
	 * @return true if is time to persist the buffered Usage Record
	 * @throws Exception
	 *             if fails
	 */
	public void aggregate(Record record, PersistenceExecutor persistenceExecutor)
			throws Exception {
		aggregate(record, persistenceExecutor, false);
	}

	protected abstract boolean isTimeToPersist(int maxRecordNumber,
			long oldRecordMaxTime);

	/**
	 * reloadConfiguration
	 * @throws Exception
	 */
	protected void reloadConfiguration() {
		Random random = new Random();
		Integer randStart= Math.abs(random.nextInt(RANDOM_INIT_START));	
		futureReload = ExecutorUtils.scheduler.scheduleAtFixedRate(new ReloaderThread(this),TIME_RELOAD_CONFIGURATION+randStart, TIME_RELOAD_CONFIGURATION, TIME_UNIT);
	}

	/**
	 * Get Configuration (used from reload configuration)
	 * @return
	 */
	protected PersistenceBackendConfiguration getConfiguration(){
		ServiceLoader<PersistenceBackend> serviceLoader = ServiceLoader
				.load(PersistenceBackend.class);
		PersistenceBackendConfiguration configuration = null;
		for (PersistenceBackend found : serviceLoader) {
			Class<? extends PersistenceBackend> foundClass = found
					.getClass();
			try {
				String foundClassName = foundClass.getSimpleName();
				logger.trace("getConfiguration - foundClassName {}", foundClassName);
				configuration = PersistenceBackendConfiguration.getInstance(foundClass);
				if (configuration == null) {
					continue;
				}
				logger.debug("{} will be used.", foundClassName);
			} catch (Exception e) {
				logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.",foundClass.getSimpleName()), e);
			}
		}
		return configuration;
	}


	protected static AggregationConfig CheckConfiguration(PersistenceBackendConfiguration configuration) throws IOException{
		Integer delay = null;
		Integer maxRecordNumber = null;
		Integer maxRecordTime = null;
		try {
			ConfigurationGetPropertyValues properties = new ConfigurationGetPropertyValues();
			Properties prop = properties.getPropValues();

			if (prop != null) {
				// get value from properties file
				logger.trace("Configuration from properties file");
				try {
					delay = Integer.parseInt(prop.getProperty("delay"));
				} catch (Exception e) {
					logger.trace("Configuration from properties file, not found a delay value");
				}
				try {
					maxRecordNumber = Integer.parseInt(prop.getProperty("maxrecordnumber"));
				} catch (Exception e) {
					logger.trace("Configuration from properties file, not found a maxRecordNumber value");
				}
				try {
					maxRecordTime = Integer.parseInt(prop.getProperty("maxtimenumber")) * 1000 * 60;
				} catch (Exception e) {
					logger.trace("Configuration from properties file, not found a maxRecordTime value");
				}

			} else {
				if (configuration != null) {
					// get value from service end point
					logger.trace("Configuration from service end point");
					try {
						delay = Integer.parseInt(configuration.getProperty(AGGREGATION_SCHEDULER_TIME));
					} catch (Exception e) {
						logger.trace("Configuration from service end point, not found delay value");
					}
					try {
						maxRecordTime = Integer.parseInt(configuration.getProperty(BUFFER_RECORD_TIME)) * 1000 * 60;
					} catch (Exception e) {
						logger.trace("Configuration from service end point, not found maxRecordTime value");
					}
					try {
						maxRecordNumber = Integer.parseInt(configuration.getProperty(BUFFER_RECORD_NUMBER));
					} catch (Exception e) {
						logger.trace("Configuration from service end point, not found maxRecordNumber value");
					}
				}
			}
		} catch (Exception e) {
			logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.",e.getLocalizedMessage()), e);
		}

		AggregationConfig config = AggregationConfig.getDefaultConfiguration();
		if (delay != null) {
			config.setDelaySet(delay);
			config.setInitialDelaySet(delay);
		} 

		if (maxRecordNumber != null) {
			config.setMaxRecordsNumberSet(maxRecordNumber);
		} 
		if (maxRecordTime != null) {
			config.setOldRecordMaxTimeElapsedSet(maxRecordTime);
		} 
		return config;
	}

	public AggregationConfig getConfig() {
		return config;
	}

	public void setConfig(AggregationConfig newConfig) {
		this.config = newConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {			
			this.flush(persistenceExecutor);			
		} catch (Exception e) {
			logger.error("Error flushing Buffered Records", e);
		}

	}

	public class ReloaderThread extends Thread {

		private AggregationScheduler agScheduler;

		public ReloaderThread(AggregationScheduler agScheduler) {
			super();
			this.agScheduler = agScheduler;
		}

		public void run() {			
			logger.trace("[{}] reloadConfiguration",agScheduler.name );
			PersistenceBackendConfiguration configuration=getConfiguration();
			try {
				AggregationConfig agConf = CheckConfiguration(configuration);
				if (!agScheduler.config.equals(agConf)) {
					logger.trace("[{}] reloadConfiguration changeConfiguration "
							+ "old config:{} newconfig:{}",agScheduler.name,agScheduler.config.toString(),agConf.toString());
					agScheduler.setConfig(agConf);
					agScheduler.run();
					agScheduler.schedule();
				}
				else{
					logger.trace("[{}] reloadConfiguration  no changeConfiguration",agScheduler.name );
				}
			} catch (IOException e) {
				logger.warn("error retrieving configuration",e);
			}
		}

	}

	public void shutdown(){
		futureReload.cancel(false);
		this.run();
		futureFlush.cancel(true);
	}
}
