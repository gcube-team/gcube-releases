package it.eng.rdlab.soa3.connector.beans;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * Specification of {@link ServiceResponse} class to be used for string responses
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class StringResponse extends ServiceResponse 
{
	private String response;
	
	public StringResponse ()
	{
		super ();
	}
	
	public StringResponse (String response,int status)
	{
		super ();
		this.response = response;
		setResponseCode(status);
	}
	
	/**
	 * 
	 * Provides the response in String format
	 * 
	 * @return the response
	 */
	public String getResponse ()
	{
		return response;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importEntity(ClientResponse clientResponse) 
	{
		this.response = clientResponse.getEntity(String.class);
		
	}

	/**
	 * 
	 * Provides the information in string format
	 * 
	 */
	@Override
	public String toString() {
	
		return "Status = "+ super.getResponseCode()+" : "+ response;
	}
	
	

}
