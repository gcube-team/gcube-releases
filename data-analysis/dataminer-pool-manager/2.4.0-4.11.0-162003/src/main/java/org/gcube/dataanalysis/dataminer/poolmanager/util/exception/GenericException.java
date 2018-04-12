package org.gcube.dataanalysis.dataminer.poolmanager.util.exception;

public class GenericException extends DMPMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6772009633547404120L;
	


	public GenericException(Throwable cause) {
		super ("Generic exception",cause);
	
	}



	@Override
	public String getErrorMessage() {
		return this.getCause().getMessage();
	}
	
	

}
