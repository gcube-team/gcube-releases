package org.gcube.soa3.connector;

import java.util.List;
import java.util.Map;

public interface Authorize 
{
	/**
	 * 
	 * Setter for the endpoint to Soa3
	 * 
	 * @param soa3Endpoint
	 */
	public void setSoa3Endpoint (String soa3Endpoint);
	
	/**
	 * Authentication service
	 * 
	 * @param parameter the authentication parameter
	 * @return
	 */
	public boolean authorize (Map<String, List<String>> attributes, String action, String respource);
}
