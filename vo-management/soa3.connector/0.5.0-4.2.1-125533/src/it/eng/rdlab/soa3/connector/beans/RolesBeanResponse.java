package it.eng.rdlab.soa3.connector.beans;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class RolesBeanResponse extends ServiceResponse 
{
	private RolesBean response;
	
	public RolesBeanResponse ()
	{
		super ();
	}
	
	public RolesBeanResponse (RolesBean response,int status)
	{
		super ();
		this.response = response;
		setResponseCode(status);
	}
	
	/**
	 * 
	 * Provides the response roles bean
	 * 
	 * @return the user bean
	 */
	public RolesBean getResponse ()
	{
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importEntity(ClientResponse clientResponse) 
	{
		this.response = clientResponse.getEntity(RolesBean.class);
		
	}

	/**
	 * 
	 * Provides the information in string format
	 * 
	 */
	@Override
	public String toString() {
	
		return "Status = "+ super.getResponseCode()+" : "+ response.getRoles().size()+" roles";
	}
	
	

}
