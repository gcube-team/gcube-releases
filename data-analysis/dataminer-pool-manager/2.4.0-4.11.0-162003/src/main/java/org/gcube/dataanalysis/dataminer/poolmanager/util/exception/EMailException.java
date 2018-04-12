package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

public class EMailException extends Exception {

	
	private static final String MESSAGE = "Unable to send email notification";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EMailException() {
		super (MESSAGE);
	}
	
	public EMailException(Throwable cause) {
		super (MESSAGE,cause);
	}


	
	
}
