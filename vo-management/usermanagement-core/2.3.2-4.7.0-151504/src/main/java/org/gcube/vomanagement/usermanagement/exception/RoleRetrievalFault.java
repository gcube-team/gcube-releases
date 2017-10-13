package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;



public class RoleRetrievalFault extends Exception{


	private static final long serialVersionUID = 2824756378739545315L;
	public RoleRetrievalFault(String errorMsg) {
		System.out.println(errorMsg);
	}
	/**
	 * 
	 */
	public RoleRetrievalFault(String errorMsg, SystemException e){
		System.out.println(errorMsg + e);
	}

	public RoleRetrievalFault(String errorMsg, PortalException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	
	public RoleRetrievalFault(String errorMsg, String roleId , PortalException e){
		System.out.println(errorMsg + roleId);
		e.printStackTrace();
	}
}