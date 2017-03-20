package org.gcube.vomanagement.vomsapi.impl;

/**
 * This exception is thrown when the VOMS-API library is not properly
 * configured.
 * 
 * @author Paolo Roccetti
 */
public class VOMSAPIConfigurationException extends Exception {

	
	/**
	 * Constructor
	 * 
	 * @param string the exception message
	 * @param e the originating exception
	 */
	public VOMSAPIConfigurationException(String string, Exception e) {
		super(string, e);
	}

	/**
	 * Constructor
	 * 
	 * @param string the exception message
	 */
	public VOMSAPIConfigurationException(String string) {
		super(string);
	}

}
