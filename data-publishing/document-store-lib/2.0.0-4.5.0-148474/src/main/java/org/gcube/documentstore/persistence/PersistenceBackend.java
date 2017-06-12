/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	protected FallbackPersistenceBackend fallbackPersistence;
	protected AggregationScheduler aggregationScheduler;

	protected PersistenceBackendMonitor persistenceBackendMonitor;

	public static final Integer MAX_FALLBACK = 3; //max fallback with reload a configuration
	protected static Integer countFallback;
	
	//add to control a timeout execption
	protected boolean timeoutFallback=false;
	protected long timerToFallback;
	public static final long MAX_TIME_TO_FALLBACK=1000*60*60; //60 min;


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
		this.fallbackPersistence = fallback;
		this.aggregationScheduler = AggregationScheduler.newInstance(new DefaultPersitenceExecutor(this), "FALLBACK");

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
	 * This method must be used by implementation class to prepare  
	 * the connection with the persistence storage, DB, file etc.
	 * @param configuration The configuration to create the connection
	 * @throws Exception if fails
	 */
	protected abstract void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception;

	/**
	 * This method is used to open db connection 
	 * @throws Exception
	 */
	protected abstract  void openConnection() throws Exception ;
	
	/**
	 * This method is used to open db connection 
	 * @throws Exception
	 */
	protected abstract void closeConnection() throws Exception ;
	
	/**
	 * This method is used to close  
	 * @throws Exception
	 */
	public abstract void close() throws Exception;
	
	/**
	 * This method is used to close db and clean the configuration.
	 * It is used when there are too much TimeoutException trying to persist
	 * records.
	 * @throws Exception
	 */
	protected void closeAndClean() throws Exception {}
	;
	
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
		
		this.openConnection();
		
		for(Record record : records){
			try {
				//old code
				//this.reallyAccount(record);
				long now = Calendar.getInstance().getTimeInMillis();
				if((timeoutFallback)){ 
					fallbackPersistence.reallyAccount(record);
					logger.trace("accountWithFallback for timeout, now:{} and timerToFallback:{}", now,timerToFallback );
					if ((now - timerToFallback) > MAX_TIME_TO_FALLBACK){	
						logger.debug("accountWithFallback MAX_TIME_TO_FALLBACK is conclused");
						timeoutFallback=false;
						timerToFallback=0;
					}
				}
				else{
					this.reallyAccount(record);
					logger.trace("accountWithFallback {} accounted succesfully from {}.", record.toString(), persistenceName);
					timeoutFallback=false;
					timerToFallback=0;
				}

			} catch (Exception e) {
				// TODO Insert Renew HERE
				if((! (this instanceof FallbackPersistenceBackend)) && e.getCause()!=null && e.getCause() instanceof TimeoutException){
					logger.warn("accountWithFallback TimeoutException number:{} to  {}.", countFallback, MAX_FALLBACK);
					countFallback++;	
					if (countFallback.equals(MAX_FALLBACK)){								
						timeoutFallback=true;
						timerToFallback = Calendar.getInstance().getTimeInMillis();
						logger.trace("accountWithFallback Going to account {} in fallback for too many timeout", record);
						//old code
						//PersistenceBackendFactory.renew(this);
						countFallback=0;
						//disconnect 	
						this.closeAndClean();
					}
				}
				else{
					logger.trace("accountWithFallback Fallback is same instance:{}"+this.getClass().getSimpleName());
				}
				try {
					String fallabackPersistenceName = FallbackPersistenceBackend.class.getSimpleName();
					logger.error("accountWithFallback {} was not accounted succesfully from {}. Trying to use {}.", 
							record.toString(), persistenceName, fallabackPersistenceName, e);
					fallbackPersistence.reallyAccount(record);
					logger.trace("accountWithFallback {} accounted succesfully from {}", 
							record.toString(), fallabackPersistenceName);
				}catch(Exception ex){
					logger.error("accountWithFallback {} was not accounted at all", record.toString(), e);
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
	protected void accountValidateAggregate(final Record record, boolean validate, boolean aggregate){
		try {
			logger.trace("Received {} to account : {}", record.getClass().getSimpleName(), record);
			if(validate){
				record.validate();
				//logger.trace("{} {} valid", record.getClass().getSimpleName(), record);
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
		ExecutorUtils.threadPool.execute(runnable);
	}

	public void flush(long timeout, TimeUnit timeUnit) throws Exception {		
		aggregationScheduler.flush(new DefaultPersitenceExecutor(this));
	}

	public boolean isOpen(){
		return !closed;
	}

	protected void setOpen(){
		this.closed = false;
	}

	

	



}
