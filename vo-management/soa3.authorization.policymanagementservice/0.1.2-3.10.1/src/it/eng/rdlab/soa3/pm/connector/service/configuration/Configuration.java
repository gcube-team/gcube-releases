package it.eng.rdlab.soa3.pm.connector.service.configuration;

import java.security.Security;

import org.opensaml.DefaultBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.rdlab.soa3.connector.utils.SecurityManager;
import it.eng.rdlab.soa3.pm.connector.javaapi.configuration.ConfigurationManagerBuilder;

public class Configuration 
{
	private static boolean initializationOK = false;
	
	public static void init ()
	{
		if (!initializationOK)
		{
		
			Logger logger = LoggerFactory.getLogger(Configuration.class);
			
			try
			{
				DefaultBootstrap.bootstrap();
			}
			catch (Exception e)
			{
				logger.error("Unable to bootstrap OpenSAML library",e);
			}
			
			ConfigurationManagerBuilder.setConfigurationManagerInstance(new ConfigurationManagerRestImpl());
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			try {
				SecurityManager.getInstance().loadCertificate(false);
			} 
			catch (Exception e)
			{
				logger.error("Unable to load the libraries",e);
			}
			initializationOK = true;
		}
	}

}
