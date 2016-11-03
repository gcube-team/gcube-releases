package org.gcube.data.analysis.tabulardata.operation.worker.exceptions;

public class WorkerException extends Exception {

	private static final long serialVersionUID = 7992314300234460689L;
	

	public WorkerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkerException(String message) {
		super(message);
	}

}
