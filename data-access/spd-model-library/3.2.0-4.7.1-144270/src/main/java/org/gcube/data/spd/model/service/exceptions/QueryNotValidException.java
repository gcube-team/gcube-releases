package org.gcube.data.spd.model.service.exceptions;

import javax.xml.ws.WebFault;

@WebFault
public class QueryNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected SpeciesExceptionBean faultInfo ;
		
	public QueryNotValidException(){}
	
	public QueryNotValidException(String message, SpeciesExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public QueryNotValidException(String message, SpeciesExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public SpeciesExceptionBean getFaultInfo(){
		return faultInfo;
	}

	public QueryNotValidException(String message, Throwable cause) {
		super(message, cause);
		this.faultInfo = new SpeciesExceptionBean(message, cause);
	}

	public QueryNotValidException(String message) {
		super(message);
		this.faultInfo = new SpeciesExceptionBean(message);
	}
	
}
