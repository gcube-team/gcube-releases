package org.gcube.soa3.connector.exceptions;

public class SOA3ConnectorException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SOA3ConnectorException(String cause) 
	{
		super ("SOA3 Connector problem: "+cause);
	}
	
}
