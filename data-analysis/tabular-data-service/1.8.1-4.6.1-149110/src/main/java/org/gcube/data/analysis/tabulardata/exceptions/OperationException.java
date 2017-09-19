package org.gcube.data.analysis.tabulardata.exceptions;

import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;

public class OperationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorkerException we;
	
	public OperationException(WorkerException we){
		this.we = we;
	}

	/**
	 * @return the we
	 */
	public WorkerException getWorkerException() {
		return we;
	}
	
	

}
