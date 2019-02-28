package org.gcube.data.analysis.statisticalmanager.exception;

import org.gcube.common.core.faults.GCUBEException;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;

public class StatisticalManagerException extends Exception {

	private static final long serialVersionUID = 6751815342337272285L;

	public StatisticalManagerException() {
		super();
	}
	
	public StatisticalManagerException(String message) {
		super(message);
		
	}
	
	public GCUBEException asGCUBEFault(){
		return ServiceContext.getContext().getDefaultException(this);
	}

	public StatisticalManagerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);	
	}

	public StatisticalManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatisticalManagerException(Throwable cause) {
		super(cause);
	}
	
	
	
}
