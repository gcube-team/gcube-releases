package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.SystemException;


public class UserManagementSystemException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7775831372490099004L;

	
	public UserManagementSystemException(String errorMsg, SystemException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	public UserManagementSystemException(String errorMsg,String orgId, Exception e){
		System.out.println(errorMsg + orgId);
		e.printStackTrace();
	}
}
