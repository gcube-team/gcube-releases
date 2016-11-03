package org.gcube.resourcemanagement.whnmanager.api.exception;

import javax.xml.ws.WebFault;

import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableExceptionInfo;

@WebFault(name="GCUBEUnrecoverableException")
public class GCUBEUnrecoverableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GCUBEUnrecoverableExceptionInfo info ;
		
	public GCUBEUnrecoverableException(GCUBEUnrecoverableExceptionInfo faultInfo){
		info = faultInfo;
	}
	
	public GCUBEUnrecoverableException(GCUBEUnrecoverableExceptionInfo faultInfo, Throwable cause){
		info = faultInfo;
	}
	
	public GCUBEUnrecoverableExceptionInfo getFaultInfo(){
		return info;
	}

}
