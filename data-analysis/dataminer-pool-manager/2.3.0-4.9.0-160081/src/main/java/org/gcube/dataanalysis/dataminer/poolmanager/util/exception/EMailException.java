package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

public class EMailException extends Exception {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EMailException() {
		super ("Unable to send email notification");
	}
	
	public EMailException(Throwable cause) {
		super ("Unable to send email notification",cause);
	}
}
