package org.gcube.portlets.admin.fhn_manager_portlet.shared.communication;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UnexpectedException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229166305795974306L;

	public UnexpectedException(String message) {
		super(message);		
	}
	public UnexpectedException() {
		// TODO Auto-generated constructor stub
	}
	

}
