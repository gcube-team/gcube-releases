package org.gcube.portlets.admin.vredefinition.shared.exception;

/**
 * VRE definition bean missing exception
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class VREDefinitionException extends Exception {

	private static final long serialVersionUID = -8266001298734768421L;
	
	// message to show
	private String error;
	
	public VREDefinitionException(){
	
		this.error = "VRE Definition Error :";
	}
	
	public VREDefinitionException(String message){
		this.error = message;
	}
	
	public String getError() {
		return this.error;
	}
}
