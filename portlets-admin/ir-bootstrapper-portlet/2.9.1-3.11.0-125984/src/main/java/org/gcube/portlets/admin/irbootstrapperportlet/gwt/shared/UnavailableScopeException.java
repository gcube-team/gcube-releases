package org.gcube.portlets.admin.irbootstrapperportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UnavailableScopeException extends Exception implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5274157880724785520L;

	@SuppressWarnings("unused")
	private UnavailableScopeException() {}
	
	public UnavailableScopeException(String message) {
		super(message);
	}

}
