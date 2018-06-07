package org.gcube.vomanagement.usermanagement.exception;

import java.io.FileNotFoundException;


public class UserManagementFileNotFoundException extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3573055203913190069L;

	public UserManagementFileNotFoundException(String errorMsg, FileNotFoundException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
}
