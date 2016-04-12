package it.eng.rdlab.soa3.connector.beans;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SessionBeanResponse extends ServiceResponse 
{
	private SessionBean response;
	
	public SessionBeanResponse ()
	{
		super ();
	}
	
	public SessionBeanResponse (SessionBean response,int status)
	{
		super ();
		this.response = response;
		setResponseCode(status);
	}
	
	/**
	 * 
	 * Provides the response usre bean
	 * 
	 * @return the user bean
	 */
	public SessionBean getResponse ()
	{
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importEntity(ClientResponse clientResponse) 
	{
		this.response = clientResponse.getEntity(SessionBean.class);
		
	}

	/**
	 * 
	 * Provides the information in string format
	 * 
	 */
	@Override
	public String toString() {
	
		return "Status = "+ super.getResponseCode()+", userid "+ response.getUserId()+" session end "+response.getSessionEnd();
	}
	
	

}
