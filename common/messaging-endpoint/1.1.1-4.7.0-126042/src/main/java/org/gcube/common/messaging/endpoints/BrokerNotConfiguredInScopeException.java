package org.gcube.common.messaging.endpoints;

/**
 * 
 * @author andrea
 *
 */
public class BrokerNotConfiguredInScopeException extends Exception {
	
	public BrokerNotConfiguredInScopeException(String string) {
		super(string);
	}
	
	public BrokerNotConfiguredInScopeException(Exception e) {
		super(e);
	}
	
	public BrokerNotConfiguredInScopeException(String string,Exception e) {
		super(string,e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}

