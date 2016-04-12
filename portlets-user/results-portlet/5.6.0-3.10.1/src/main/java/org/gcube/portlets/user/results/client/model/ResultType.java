package org.gcube.portlets.user.results.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum ResultType implements IsSerializable{

	/**
	 * RESULTS
	 */
	RESULTS, 

	/**
	 * NO RESULTS
	 */
	NO_RESULTS,

	/**
	 * ERROR
	 */
	ERROR;	
	
	private ResultType() {
	
	}
}

