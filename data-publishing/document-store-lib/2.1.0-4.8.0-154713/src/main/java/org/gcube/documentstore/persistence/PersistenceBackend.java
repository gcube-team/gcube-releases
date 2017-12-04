/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.aggregation.AggregationScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Alessandro Pieve (ISTI - CNR)
 */
public abstract class PersistenceBackend {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceBackend.class);

	/**
	 * Max Time Period Used during while the usage of FallbackPersistenceBackend is forced  
	 */
	public static final long MAX_TIME_TO_FALLBACK = 1000 * 60 * 30; // 30 min;
	/**
	 * Max Times of Retry before forcing the usage of FallbackPersistenceBackend
	 */
	public static final int MAX_FALLBACK_RETRY = 3;

	protected boolean forceFallbackUse;
	protected int fallbackUseCounter;
	protected long fallbackUseStartTime;
	
	protected FallbackPersistenceBackend fallbackPersistence;
	protected AggregationScheduler aggregationScheduler;

	protected FallbackMonitor persistenceBackendMonitor;

	protected PersistenceBackend() {
		if (!(this instanceof FallbackPersistenceBackend)) {
			this.persistenceBackendMonitor = new FallbackMonitor(this);
		}
		forceFallbackUse = false;
		fallbackUseCounter = 0;
		fallbackUseStartTime = 0;
	}

	protected PersistenceBackend(FallbackPersistenceBackend fallback) {
		this();
		this.fallbackPersistence = fallback;
		this.aggregationScheduler = AggregationScheduler.newInstance(new DefaultPersitenceExecutor(this));
	}

	/**
	 * @return the fallbackPersistence
	 */
	public FallbackPersistenceBackend getFallbackPersistence() {
		return fallbackPersistence;
	}

	/**
	 * @param fallback
	 *            the fallback to set
	 */
	protected void setFallback(FallbackPersistenceBackend fallback) {
		this.fallbackPersistence = fallback;
	}

	/**
	 * @return the aggregationScheduler
	 */
	public AggregationScheduler getAggregationScheduler() {
		return aggregationScheduler;
	}

	/**
	 * @param aggregationScheduler
	 *            the aggregationScheduler to set
	 */
	protected void setAggregationScheduler(AggregationScheduler aggregationScheduler) {
		this.aggregationScheduler = aggregationScheduler;
	}

	/**
	 * Prepare the connection to persistence. This method must be used by
	 * implementation class to prepare the connection with the persistence
	 * storage, DB, file etc.
	 * 
	 * @param configuration
	 *            The configuration to create the connection
	 * @throws Exception
	 *             if fails
	 */
	protected abstract void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception;

	/**
	 * This method is used to open db connection
	 * 
	 * @throws Exception
	 */
	protected abstract void openConnection() throws Exception;

	/**
	 * This method is used to close db connection
	 * 
	 * @throws Exception
	 */
	protected abstract void closeConnection() throws Exception;

	/**
	 * This method is used to close
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		flush();
		closeConnection();
	}

	/**
	 * This method is used to allow PersistenceBackend implementations 
	 * to clean a global status if any (or to renew it) when to much
	 * exceptions occurs trying to persist Records.
	 * @throws Exception
	 */
	protected abstract void clean() throws Exception;
	
	/**
	 * Check the Connection state
	 * 
	 * @return true if the connection is active, false otherwise
	 * @throws Exception
	 */
	public abstract boolean isConnectionActive() throws Exception;

	/**
	 * This method contains the code to save the {@link Record}
	 * 
	 */
	protected abstract void reallyAccount(Record record) throws Exception;

	/***
	 * 
	 * @param records
	 * @throws Exception
	 */
	protected void accountWithFallback(Record... records) throws Exception {
		String persistenceName = this.getClass().getSimpleName();
		String fallbackPersistenceName = FallbackPersistenceBackend.class.getSimpleName();
		
		this.openConnection();

		for (Record record : records) {
			String recordString = null;
			try {
				recordString = record.toString();
				
				if (forceFallbackUse) {
					
					logger.trace("Forcing the use of {} to account {}", fallbackPersistenceName, recordString);
					fallbackPersistence.reallyAccount(record);
					
					long now = Calendar.getInstance().getTimeInMillis();
					long diff = now - fallbackUseStartTime;
					logger.trace("{} forced use started at {}. {} seconds were elapsed", 
							fallbackPersistenceName, fallbackUseStartTime, (long) diff/1000);
					
					if (diff > MAX_TIME_TO_FALLBACK) {
						logger.debug("The time to force the usage of {} is terminated. Trying to restore the use of {}", 
								fallbackPersistenceName, persistenceName);
						forceFallbackUse = false;
						fallbackUseCounter = 0;
						fallbackUseStartTime = 0;
					}
					
				} else {
					this.reallyAccount(record);
					logger.trace("{} accounted succesfully from {}.", record.toString(), persistenceName);
				}

			} catch (Throwable t) {

				try {
					logger.warn("{} was not accounted succesfully using {}. Trying to use {}.", recordString,
							persistenceName, fallbackPersistenceName, t);
					
					fallbackPersistence.reallyAccount(record);
					
					logger.trace("{} accounted succesfully from {}", recordString, fallbackPersistenceName);
				} catch (Throwable th) {
					logger.error("{} was not accounted at all", recordString, t);
				}

				if (!(this instanceof FallbackPersistenceBackend) ){ 
					// && (t.getCause() != null && t.getCause() instanceof TimeoutException)) {
					
					fallbackUseCounter++;
					
					logger.warn("Exception number is {}. Max Retry number is {}. After that the use of {} will be forced", 
							fallbackUseCounter, MAX_FALLBACK_RETRY, fallbackPersistenceName);
					
					if (fallbackUseCounter == MAX_FALLBACK_RETRY) {
						forceFallbackUse = true;
						fallbackUseStartTime = Calendar.getInstance().getTimeInMillis();
						logger.trace("Going to force {} for too many Exceptions",
								fallbackPersistenceName);
						
						aggregationScheduler.flush(new DefaultPersitenceExecutor(fallbackPersistence));
						
						this.close();
						this.clean();
					}
				}
			}
		}

		this.closeConnection();
	}

	/**
	 * 
	 * @param record
	 * @param validate
	 * @param aggregate
	 */
	protected void accountValidateAggregate(final Record record, boolean validate, boolean aggregate) {
		try {
			logger.trace("Received {} to account : {}", record.getClass().getSimpleName(), record);
			if (validate) {
				record.validate();
			}
			if (aggregate) {
				try {
					aggregationScheduler.aggregate(record, new DefaultPersitenceExecutor(this));
				} catch (Exception e) {
					this.accountWithFallback(record);
				}
			} else {
				this.accountWithFallback(record);
			}

		} catch (InvalidValueException e) {
			logger.error("Error validating {}", record.getClass().getSimpleName(), e);
		} catch (Exception e) {

			logger.error("Error recording {}", record.getClass().getSimpleName(), e);
		}
	}

	/**
	 * Persist the {@link #UsageRecord}. The Record is validated first, then
	 * accounted, in a separated thread. So that the program can continue the
	 * execution. If the persistence fails the class write that the record in a
	 * local file so that the {@link #UsageRecord} can be recorder later.
	 * 
	 * @param usageRecord
	 *            the {@link #UsageRecord} to persist
	 * @throws InvalidValueException
	 *             if the Record Validation Fails
	 */
	public void account(final Record record) throws InvalidValueException {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				accountValidateAggregate(record, true, true);
			}
		};
		ExecutorUtils.threadPool.execute(runnable);
	}

	/**
	 * Use {@link PersistenceBackend#flush()} instead
	 * @param timeout
	 * @param timeUnit
	 * @throws Exception
	 */
	@Deprecated
	public void flush(long timeout, TimeUnit timeUnit) throws Exception {
		flush();
	}
	
	public void flush() throws Exception {
		aggregationScheduler.flush(new DefaultPersitenceExecutor(this));
	}

}
