package it.eng.rdlab.soa3.connector.beans;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class UserBeanResponse extends ServiceResponse 
{
	private UserBean response;
	
	public UserBeanResponse ()
	{
		super ();
	}
	
	public UserBeanResponse (UserBean response,int status)
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
	public UserBean getResponse ()
	{
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importEntity(ClientResponse clientResponse) 
	{
		this.response = clientResponse.getEntity(UserBean.class);
		
	}

	/**
	 * 
	 * Provides the information in string format
	 * 
	 */
	@Override
	public String toString() {
	
		return "Status = "+ super.getResponseCode()+" : "+ response.getUserName();
	}
	
	

}
