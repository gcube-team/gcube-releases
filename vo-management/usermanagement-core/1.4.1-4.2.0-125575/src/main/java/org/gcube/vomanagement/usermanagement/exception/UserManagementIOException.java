package org.gcube.vomanagement.usermanagement.exception;

import java.io.IOException;


public class UserManagementIOException extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7351699091691019047L;

	public UserManagementIOException(String errorMsg, IOException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	
}
