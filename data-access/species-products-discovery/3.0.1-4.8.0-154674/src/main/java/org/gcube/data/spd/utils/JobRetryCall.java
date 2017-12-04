package org.gcube.data.spd.utils;

import org.gcube.data.spd.Constants;

public abstract class JobRetryCall<T,E extends Throwable> extends RetryCall<T, E> {

	public JobRetryCall(){
		super(Constants.JOB_CALL_RETRIES, Constants.RETRY_JOBS_MILLIS);
	}
	
	@Override
	protected long getWaitTime(int retry, long waitTimeInMillis) {
		return retry*waitTimeInMillis;
	}
	
}
