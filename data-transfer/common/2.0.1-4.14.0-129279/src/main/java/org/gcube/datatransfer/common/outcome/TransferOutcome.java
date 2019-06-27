package org.gcube.datatransfer.common.outcome;

import java.io.Serializable;

import org.gcube.datatransfer.common.grs.FileOutcomeRecord.Outcome;

public abstract class TransferOutcome implements Serializable{

	private static final long serialVersionUID = -8081911147698166036L;
	private String exception;
	
	
	/**
	 * 
	 * @return <code>true</code> if the outcome indicates success, <code>false</code> otherwise
	 */
	public boolean isSuccess() {
		return exception.compareTo(Outcome.N_A.name()) == 0;
	}

	/**
	 * 
	 * @return <code>true</code> if the outcome indicates a failure, <code>false</code> otherwise
	 */
	public boolean isFailure() {
		return !(isSuccess());
	}
	
	public String getException() {
		return exception;
	}


	public void setException(String exception) {
		this.exception = exception;
	}
	
	/**
	 * Returns the exception raised when unsuccessfully transfer a file.
	 * @return the exception, or <code>null</code> if the outcome indicates a success
	 */
	public String failure() {
		return exception;
	}
}
