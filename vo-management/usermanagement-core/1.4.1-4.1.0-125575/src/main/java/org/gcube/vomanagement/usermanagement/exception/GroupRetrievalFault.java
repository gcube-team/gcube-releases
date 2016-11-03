package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;



public class GroupRetrievalFault extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809619725100397844L;
	
	public GroupRetrievalFault(String errorMsg, String orgId ){
		System.out.println(errorMsg + orgId);
	}

	public GroupRetrievalFault(String errorMsg, PortalException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	
	public GroupRetrievalFault(String errorMsg, String orgId , PortalException e){
		System.out.println(errorMsg + orgId);
		e.printStackTrace();
	}
}
