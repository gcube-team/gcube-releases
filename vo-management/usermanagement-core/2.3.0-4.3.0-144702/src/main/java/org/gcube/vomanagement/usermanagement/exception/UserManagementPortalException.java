package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;




public class UserManagementPortalException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4958787007913556095L;

	
	public UserManagementPortalException(String errorMsg, PortalException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	
	public UserManagementPortalException(String errorMsg, String orgId , PortalException e){
		System.out.println(errorMsg + orgId);
		e.printStackTrace();
	}
}
