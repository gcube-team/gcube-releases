package it.eng.rdlab.soa3.connector.beans;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * Utility abstract class to pack a service response
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public abstract class ServiceResponse 
{
	private int responseCode;
	
	

	/**
	 * 
	 * Setter for the response code
	 * 
	 * @param responseCode
	 */
	public void setResponseCode (int responseCode)
	{
		this.responseCode = responseCode;
	}
	
	/**
	 * 
	 * Getter for the response code
	 * 
	 * @return
	 */
	public int getResponseCode() 
	{
		return responseCode;
	}
	
	/**
	 * 
	 * imports the useful information from the actual response
	 * 
	 * @param clientResponse
	 */
	public abstract void importEntity (ClientResponse clientResponse);

}
