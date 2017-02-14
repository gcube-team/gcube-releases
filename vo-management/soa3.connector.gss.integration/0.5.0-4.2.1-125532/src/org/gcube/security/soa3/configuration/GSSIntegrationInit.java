package org.gcube.security.soa3.configuration;

public class GSSIntegrationInit 
{
	private static boolean init = false;
	
	public static void init ()
	{
		if (!init) ConfigurationManagerFactory.setConfigurationManager(new ISBasedConfiguration( new GSSConfigurationManagerImpl(true)));
	}

}
