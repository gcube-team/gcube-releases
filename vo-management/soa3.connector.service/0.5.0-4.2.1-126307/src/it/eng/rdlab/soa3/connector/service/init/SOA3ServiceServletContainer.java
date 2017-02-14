package it.eng.rdlab.soa3.connector.service.init;

import it.eng.rdlab.soa3.connector.service.configuration.SecurityConfiguration;

import javax.servlet.ServletException;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class SOA3ServiceServletContainer extends ServletContainer 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException 
	{
		super.init();
		SecurityConfiguration.initSecurity();
	}
	
	
	

}
