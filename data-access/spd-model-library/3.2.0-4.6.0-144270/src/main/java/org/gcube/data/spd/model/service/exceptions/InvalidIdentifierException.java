package org.gcube.data.spd.model.service.exceptions;

import javax.xml.ws.WebFault;


@WebFault
public class InvalidIdentifierException extends Exception {

		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected SpeciesExceptionBean faultInfo ;
		
	public InvalidIdentifierException(){}
	
	public InvalidIdentifierException(String message, SpeciesExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public InvalidIdentifierException(String message, SpeciesExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public SpeciesExceptionBean getFaultInfo(){
		return faultInfo;
	}

	public InvalidIdentifierException(String message, Throwable cause) {
		super(message, cause);
		this.faultInfo = new SpeciesExceptionBean(message, cause);
	}

	public InvalidIdentifierException(String message) {
		super(message);
		this.faultInfo = new SpeciesExceptionBean(message);
	}
}
