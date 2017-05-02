package org.gcube.data.spd.model.service.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name="UnsupportedCapabilityFault")
public class UnsupportedCapabilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected SpeciesExceptionBean faultInfo ;
		
	public UnsupportedCapabilityException(){}
	
	public UnsupportedCapabilityException(String message, SpeciesExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public UnsupportedCapabilityException(String message, SpeciesExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public SpeciesExceptionBean getFaultInfo(){
		return faultInfo;
	}

	public UnsupportedCapabilityException(String message, Throwable cause) {
		super(message, cause);
		this.faultInfo = new SpeciesExceptionBean(message, cause);
	}

	public UnsupportedCapabilityException(String message) {
		super(message);
		this.faultInfo = new SpeciesExceptionBean(message);
	}
	
}
