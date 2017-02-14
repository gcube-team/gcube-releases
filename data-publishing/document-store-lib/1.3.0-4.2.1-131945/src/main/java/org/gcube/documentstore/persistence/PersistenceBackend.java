/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.aggregation.AggregationScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public abstract class PersistenceBackend {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceBackend.class);

	protected FallbackPersistenceBackend fallbackPersistence;
	protected AggregationScheduler aggregationScheduler;

	protected PersistenceBackendMonitor persistenceBackendMonitor;

	public static final Integer MAX_FALLBACK = 3; //max fallback with reload a configuration
	protected static Integer countFallback; 
	protected boolean closed;

	protected PersistenceBackend(){
		if(!(this instanceof FallbackPersistenceBackend)){
			this.persistenceBackendMonitor = new PersistenceBackendMonitor(this);
		}
		countFallback=0;
		closed = true;
	}

	protected PersistenceBackend(FallbackPersistenceBackend fallback){		
		this();
		logger.trace("PersistenceBackend-");
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
	 * @param fallback the fallback to set
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
	 * @param aggregationScheduler the aggregationScheduler to set
	 */
	protected void setAggregationScheduler(AggregationScheduler aggregationScheduler) {
		this.aggregationScheduler = aggregationScheduler;
	}

	/**
	 * Prepare the connection to persistence.
	 * This method must be used by implementation class to open 
	 * the connection with the persistence storage, DB, file etc.
	 * @param configuration The configuration to create the connection
	 * @throws Exception if fails
	 */
	protected abstract void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception;

	/**
	 * This method contains the code to save the {@link Record}
	 * 
	 */
	protected abstract void reallyAccount(Record record) throws Exception;
	
	
	
	/***
	 * 
	 * @param records
	 */
	protected void accountWithFallback(Record... records) {
		String persistenceName = this.getClass().getSimpleName();
		logger.trace("Going to account {} using {} : {}", Arrays.toString(records), persistenceName, this);
		for(Record record : records){
			try {
				logger.trace("Going to account {} using {} : {}", record, persistenceName, this);
				this.reallyAccount(record);
				logger.trace("{} accounted succesfully from {}.", record.toString(), persistenceName);

			} catch (Exception e) {
				// TODO Insert Renew HERE
				if((! (this instanceof FallbackPersistenceBackend)) && e.getCause()!=null && e.getCause() instanceof TimeoutException){
					logger.error("TimeoutException number:{} to  {}.", countFallback, MAX_FALLBACK);
					countFallback++;	
					if (countFallback.equals(MAX_FALLBACK)){						
						PersistenceBackendFactory.renew(this);
						countFallback=0;
					}
				}
				else{
					logger.trace("Fallback is same instance:{}"+this.getClass().getSimpleName());
				}
				try {
					String fallabackPersistenceName = FallbackPersistenceBackend.class.getSimpleName();
					logger.error("{} was not accounted succesfully from {}. Trying to use {}.", 
							record.toString(), persistenceName, fallabackPersistenceName, e);
					fallbackPersistence.reallyAccount(record);
					logger.trace("{} accounted succesfully from {}", 
							record.toString(), fallabackPersistenceName);
				}catch(Exception ex){
					logger.error("{} was not accounted at all", record.toString(), e);
				}
			}
		}
	}

	/**
	 * 
	 * @param record
	 * @param validate
	 * @param aggregate
	 */
	protected void accountValidateAggregate(final Record record, boolean validate, boolean aggregate){
		try {
			logger.trace("Received {} to account : {}", record.getClass().getSimpleName(), record);
			if(validate){
				record.validate();
				logger.trace("{} {} valid", record.getClass().getSimpleName(), record);
			}
			if(aggregate){
				try {
					aggregationScheduler.aggregate(record, new DefaultPersitenceExecutor(this));
				} catch(Exception e){
					this.accountWithFallback(record);
				}
			}else{
				this.accountWithFallback(record);	
			}

		} catch (InvalidValueException e) {
			logger.error("Error validating {}", record.getClass().getSimpleName(), e);
		} catch (Exception e) {
			logger.error("Error recording {}", record.getClass().getSimpleName(), e);
		} 
	}

	/**
	 * Persist the {@link #UsageRecord}.
	 * The Record is validated first, then accounted, in a separated thread. 
	 * So that the program can continue the execution.
	 * If the persistence fails the class write that the record in a local file
	 * so that the {@link #UsageRecord} can be recorder later.
	 * @param usageRecord the {@link #UsageRecord} to persist
	 * @throws InvalidValueException if the Record Validation Fails
	 */
	public void account(final Record record) throws InvalidValueException{
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				accountValidateAggregate(record, true, true);
			}
		};
		//pool.execute(runnable);
		ExecutorUtils.threadPool.execute(runnable);
	}

	public void flush(long timeout, TimeUnit timeUnit) throws Exception {
		//pool.awaitTermination(timeout, timeUnit);
		aggregationScheduler.flush(new DefaultPersitenceExecutor(this));
	}

	public boolean isOpen(){
		return !closed;
	}

	protected void setOpen(){
		this.closed = false;
	}

	public abstract void close() throws Exception;

}
