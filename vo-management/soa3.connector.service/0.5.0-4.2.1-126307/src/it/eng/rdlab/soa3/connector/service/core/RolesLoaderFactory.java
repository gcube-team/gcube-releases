package it.eng.rdlab.soa3.connector.service.core;

import it.eng.rdlab.soa3.connector.service.configuration.Configuration;

import org.gcube.soa3.connector.RolesLoader;
import org.gcube.soa3.connector.impl.RolesLoaderImpl;

public class RolesLoaderFactory 
{
	
	public static RolesLoader getRoleLoader ()
	{
		return new RolesLoaderImpl(Configuration.getInstance().getSoa3Endpoint());
	}

}
