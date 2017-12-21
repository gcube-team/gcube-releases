package org.gcube.data.spd.model.service.exceptions;

import javax.xml.ws.WebFault;

@WebFault
public class InvalidJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected SpeciesExceptionBean faultInfo ;
		
	public InvalidJobException(){}
	
	public InvalidJobException(String message, SpeciesExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public InvalidJobException(String message, SpeciesExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public SpeciesExceptionBean getFaultInfo(){
		return faultInfo;
	}

	public InvalidJobException(String message, Throwable cause) {
		super(message, cause);
		this.faultInfo = new SpeciesExceptionBean(message, cause);
	}

	public InvalidJobException(String message) {
		super(message);
		this.faultInfo = new SpeciesExceptionBean(message);
	}
	

	
}
