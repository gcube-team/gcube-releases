package it.eng.rdlab.soa3.assertion.manager;

import it.eng.rdlab.soa3.config.ConfigurationManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SAMLUtils 
{
	private static Log log = LogFactory.getLog(SAMLUtils.class);
	
	
	public static String generateAssertionUrl (String key, String id)
	{
		log.debug("Key = "+key);
		log.debug("ID = "+id);
		String url = ConfigurationManager.getInstance().getAssertionServiceUrl();
		log.debug("Assertion url "+url);
		StringBuilder urlBuilder = new StringBuilder(url);
		urlBuilder.append("?key=").append(key).append("&ID=").append(id);
		String finalUrl = urlBuilder.toString();
		log.debug("Complete url = "+finalUrl);
		return finalUrl;
		
	}
}
