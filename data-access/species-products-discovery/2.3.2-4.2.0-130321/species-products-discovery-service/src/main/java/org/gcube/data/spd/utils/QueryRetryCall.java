package org.gcube.data.spd.utils;
import org.gcube.common.core.types.VOID;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;

public abstract class QueryRetryCall extends RetryCall<VOID, Exception>{

	public QueryRetryCall(){
		super(Constants.QUERY_CALL_RETRIES, Constants.RETRY_QUERY_MILLIS);
	}
	
		
	@Override
	public VOID call() throws MaxRetriesReachedException {
		try{
			return super.call();
		}catch (MaxRetriesReachedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("unexpected error",e);
		}
		return new VOID();
	}

	
		
	@Override
	protected abstract VOID execute() throws ExternalRepositoryException ;


	@Override
	protected long getWaitTime(int retry, long waitTimeInMillis) {
		return waitTimeInMillis;
	}

}
