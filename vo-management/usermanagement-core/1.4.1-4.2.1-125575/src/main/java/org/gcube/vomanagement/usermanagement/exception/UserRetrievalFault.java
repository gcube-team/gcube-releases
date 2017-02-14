package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;



public class UserRetrievalFault extends Exception{

	private static final long serialVersionUID = 4384964298359631619L;

	/**
	 * 
	 */


	public UserRetrievalFault(String errorMsg, PortalException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	
	public UserRetrievalFault(String errorMsg, String userId , PortalException e){
		System.out.println(errorMsg + userId);
		e.printStackTrace();
	}
}