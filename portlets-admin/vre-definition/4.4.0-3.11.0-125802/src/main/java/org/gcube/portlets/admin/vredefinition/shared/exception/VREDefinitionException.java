package org.gcube.portlets.admin.vredefinition.shared.exception;

public class VREDefinitionException extends Exception {

	private String error;
	public VREDefinitionException(){
	
		error = "VRE Definition Error :";
	}
	
	public VREDefinitionException(String message){
		error += message;
	}
	
	public String getError() {
		return this.error;
	}
}
