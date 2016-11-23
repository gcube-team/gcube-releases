package it.eng.rdlab.soa3.connector.service.configuration;

import it.eng.rdlab.soa3.connector.utils.SecurityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityConfiguration 
{
	private static boolean securityInitualized = false;
	
	public static void initSecurity ()
	{
		Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
		
		if (!securityInitualized)
		{
			logger.debug("Initializing the security features");
			SecurityManager.getInstance().setCertFile(Configuration.getInstance().getCertFile());
			SecurityManager.getInstance().setKeyFile(Configuration.getInstance().getKeyFile());
			SecurityManager.getInstance().setTrustDir(Configuration.getInstance().getTrustDir());
			SecurityManager.getInstance().setTrustExt(Configuration.getInstance().getTrustExt());
			
			try 
			{
				SecurityManager.getInstance().loadCertificate(false);
			} catch (Exception e) 
			{
				logger.warn("Unable to load the certificates", e);
			}
			
			securityInitualized = true;
		}
	}

}
