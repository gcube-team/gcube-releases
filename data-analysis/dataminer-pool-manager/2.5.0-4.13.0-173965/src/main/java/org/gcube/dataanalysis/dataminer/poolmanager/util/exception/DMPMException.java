package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

public abstract class DMPMException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DMPMException (String errorMessage)
	{
		super (errorMessage);
	}
	
	public DMPMException(String errorMessage,Throwable cause) {
		super (errorMessage,cause);
	}
	
	public abstract String getErrorMessage ();
}
