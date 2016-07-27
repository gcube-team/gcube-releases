package org.gcube.data.spd.utils;


import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RetryCall<T, E extends Throwable>{

	Logger logger = LoggerFactory.getLogger(RetryCall.class);
	
	private int retries;
	private long waitTimeInMillis;
		
	protected RetryCall(int retries, long waitTimeInMillis) {
		super();
		this.retries = retries;
		this.waitTimeInMillis = waitTimeInMillis;
	}
	
	public RetryCall() {
		super();
	}

	public  T call() throws MaxRetriesReachedException, E {
		int retry = 0;
		do {
			try{
				return execute();
			}catch (ExternalRepositoryException e) {
				logger.warn("error on external repository, "+(retry<retries?" ":"not ")+"retrying",e);
				retry++;
				try {
					Thread.sleep(getWaitTime(retry, waitTimeInMillis));
				} catch (InterruptedException e1) {}
			}
		}while(retry<retries);
		throw new MaxRetriesReachedException();		
	}

	protected abstract T execute() throws ExternalRepositoryException,E;
	
	protected abstract long getWaitTime(int retry, long waitTimeInMillis);
		
	
	
}
