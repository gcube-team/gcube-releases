package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;

@SuppressWarnings("serial")
public class VirtualGroupNotExistingException extends Exception {
	public VirtualGroupNotExistingException(String errorMsg,  PortalException e){
		System.out.println(errorMsg);
		e.printStackTrace();
	}
	public VirtualGroupNotExistingException(String errorMsg){
		System.out.println(errorMsg);
	}
}
