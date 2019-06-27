package org.gcube.documentstore.records.aggregation;

import java.io.File;
import java.io.FileInputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public abstract class AggregationScheduler implements Runnable {

	public static final Logger logger = LoggerFactory.getLogger(AggregationScheduler.class);

	public static final String AGGREGATION_SCHEDULER_TIME = "AggregationSchedulerTime";

	public static final String BUFFER_RECORD_TIME = "BufferRecordTime";
	public static final String BUFFER_RECORD_NUMBER = "BufferRecordNumber";

	public static final int RANDOM_INIT_START = 5;
	
	/**
	 * The Max amount of time for reload a configuration Get from configuration
	 */
	public static final long TIME_RELOAD_CONFIGURATION = 720; // 720 minutes (12 Hours)

	public static final String CONFIG_DIRECTORY_NAME = "config";
	public static final String FILE_NAME = "accounting.properties";
	
	public static final File AGGREGATION_PROPERTIES_FILE;
	
	static {
		File file = new File(".");
		file = new File(file, CONFIG_DIRECTORY_NAME);
		AGGREGATION_PROPERTIES_FILE = new File(file, FILE_NAME);
	}
	
	
	
	protected int totalBufferedRecords;
	
	protected Map<String, List<Record>> bufferedRecords;

	protected final PersistenceExecutor persistenceExecutor;

	public boolean changeConfiguration = false;

	private AggregationConfiguration config;

	// Schedule for flush and reload configuration
	protected ScheduledFuture<?> futureFlush;
	protected ScheduledFuture<?> futureReload;
	
	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor) {
		return new BufferAggregationScheduler(persistenceExecutor);
	}

	public static AggregationScheduler newInstance(PersistenceExecutor persistenceExecutor,
			PersistenceBackendConfiguration configuration) throws NumberFormatException, Exception {
		AggregationConfiguration config = CheckConfiguration(configuration);
		return new BufferAggregationScheduler(persistenceExecutor, config);
	}

	protected AggregationScheduler(PersistenceExecutor persistenceExecutor) {
		this(persistenceExecutor, AggregationConfiguration.getDefaultConfiguration());
	}

	protected AggregationScheduler(PersistenceExecutor persistenceExecutor, AggregationConfiguration config) {
		this.config = config;
		
		this.bufferedRecords = new HashMap<String, List<Record>>();
		this.totalBufferedRecords = 0;
		this.persistenceExecutor = persistenceExecutor;
		
		schedule();
		reloadConfiguration();
	}

	private void schedule() {
		if (futureFlush != null) {
			futureFlush.cancel(false);
		}
		futureFlush = ExecutorUtils.FUTURE_FLUSH_POOL.scheduleAtFixedRate(this, config.getInitialDelay(), config.getDelay(), AggregationConfiguration.TIME_UNIT);
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
					logger.trace("Aggregated Record is {}", bufferedAggregatedRecord);
					found = true;
					break;
				} catch (NotAggregatableRecordsExceptions e) {
					logger.trace("{} is not usable for aggregation", bufferedRecord);
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

	protected synchronized void aggregate(Record record, PersistenceExecutor persistenceExecutor, boolean forceFlush)
			throws Exception {

		if (record != null) {
			madeAggregation(record);
		}
		
		if (isTimeToPersist(config.getMaxRecordsNumber(), config.getMaxTimeElapsed())
				|| forceFlush) {
			reallyFlush(persistenceExecutor);
		}

	}

	protected void reallyFlush(PersistenceExecutor persistenceExecutor) throws Exception {

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
		logger.trace("It is time to persist buffered records {}", Arrays.toString(recordToPersist));
		persistenceExecutor.persist(recordToPersist);
		clear();
	}

	/**
	 * Get a record and try to aggregate with other buffered Records.
	 * @param record The Record
	 * @param persistenceExecutor
	 * @throws Exception
	 */
	public void aggregate(Record record, PersistenceExecutor persistenceExecutor) throws Exception {
		aggregate(record, persistenceExecutor, false);
	}

	protected abstract boolean isTimeToPersist(int maxRecordNumber, long oldRecordMaxTime);

	/**
	 * reloadConfiguration
	 * 
	 * @throws Exception
	 */
	protected void reloadConfiguration() {
		Random random = new Random();
		Integer randStart = Math.abs(random.nextInt(RANDOM_INIT_START));
		futureReload = ExecutorUtils.CONFIGURATION_REDISCOVERY_POOL.scheduleAtFixedRate(new ReloaderThread(this),
				TIME_RELOAD_CONFIGURATION + randStart, TIME_RELOAD_CONFIGURATION, TimeUnit.MINUTES);
	}

	/**
	 * Get Configuration (used from reload configuration)
	 * @return PersistenceBackendConfiguration
	 */
	protected PersistenceBackendConfiguration getConfiguration() {
		ServiceLoader<PersistenceBackend> serviceLoader = ServiceLoader.load(PersistenceBackend.class);
		PersistenceBackendConfiguration configuration = null;
		for (PersistenceBackend found : serviceLoader) {
			Class<? extends PersistenceBackend> foundClass = found.getClass();
			try {
				String foundClassName = foundClass.getSimpleName();
				logger.trace("getConfiguration - foundClassName {}", foundClassName);
				configuration = PersistenceBackendConfiguration.getInstance(foundClass);
				if (configuration == null) {
					continue;
				}
				logger.debug("{} will be used.", foundClassName);
			} catch (Exception e) {
				logger.error(
						String.format("%s not initialized correctly. It will not be used. Trying the next one if any.",
								foundClass.getSimpleName()),
						e);
			}
		}
		return configuration;
	}

	public static Properties getPropertiesFromFile() throws IOException {
		Properties properties = null;
		

		logger.trace("Looking for properties in file " + AGGREGATION_PROPERTIES_FILE.getAbsolutePath());

		try (FileInputStream inputStream = new FileInputStream(AGGREGATION_PROPERTIES_FILE)) {
			if (inputStream != null) {
				properties = new Properties();
				properties.load(inputStream);
			}
		} catch (Exception e) {
			logger.trace(
					"ConfigurationGetPropertyValues -property file error on input stream" + e.getLocalizedMessage());
		}
		return properties;
	}
	
	protected static AggregationConfiguration CheckConfiguration(PersistenceBackendConfiguration configuration)
			throws IOException {
		Integer delay = null;
		Integer maxRecordNumber = null;
		Integer maxRecordTime = null;
		
		try {
			Properties properties = AggregationScheduler.getPropertiesFromFile();

			if (properties != null) {
				// get value from properties file
				logger.trace("Configuration from properties file");
				try {
					delay = Integer.parseInt(properties.getProperty("delay"));
				} catch (Exception e) {
					logger.trace("Configuration from properties file, not found a delay value");
				}
				try {
					maxRecordNumber = Integer.parseInt(properties.getProperty("maxrecordnumber"));
				} catch (Exception e) {
					logger.trace("Configuration from properties file, not found a maxRecordNumber value");
				}
				try {
					maxRecordTime = Integer.parseInt(properties.getProperty("maxtimenumber")) * 1000 * 60;
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
			logger.error(String.format("%s not initialized correctly. It will not be used. Trying the next one if any.",
					e.getLocalizedMessage()), e);
		}

		AggregationConfiguration config = AggregationConfiguration.getDefaultConfiguration();
		if (delay != null) {
			config.setDelay(delay);
			config.setInitialDelay(delay);
		}

		if (maxRecordNumber != null) {
			config.setMaxRecordsNumber(maxRecordNumber);
		}
		if (maxRecordTime != null) {
			config.setMaxTimeElapsed(maxRecordTime);
		}
		return config;
	}

	public AggregationConfiguration getConfig() {
		return config;
	}

	public void setConfig(AggregationConfiguration newConfig) {
		this.config = newConfig;
	}

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
			try {
				logger.trace("Going to reload aggregation configuration");
				PersistenceBackendConfiguration configuration = getConfiguration();
				AggregationConfiguration agConf = CheckConfiguration(configuration);
				if (!agScheduler.config.equals(agConf)) {
					logger.trace("reloadConfiguration changeConfiguration " + "old config:{} newconfig:{}",
							agScheduler.config.toString(), agConf.toString());
					agScheduler.setConfig(agConf);
					agScheduler.run();
					agScheduler.schedule();
				} else {
					logger.trace("reloadConfiguration  no changeConfiguration");
				}
			} catch (IOException e) {
				logger.warn("error retrieving configuration", e);
			} catch (Throwable t) {
				logger.error("", t);
			}
		}

	}

	public void shutdown() {
		futureReload.cancel(false);
		this.run();
		futureFlush.cancel(true);
	}
}
