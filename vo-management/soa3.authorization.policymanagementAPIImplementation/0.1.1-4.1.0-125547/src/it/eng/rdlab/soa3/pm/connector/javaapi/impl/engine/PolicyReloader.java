package it.eng.rdlab.soa3.pm.connector.javaapi.impl.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

class PolicyReloader 
{
	private Logger logger;
	private String policyLoaderUrl;
	
	private final String PATH = "/reloadPolicy";
	
	public PolicyReloader (String policyLoaderUrl)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.policyLoaderUrl = policyLoaderUrl;
		
		if (this.policyLoaderUrl == null)
		{
			this.logger.warn("No real time policy reloading");
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean reloadPolicies ()
	{
		if (this.policyLoaderUrl != null)
		{
			try
			{
				logger.debug("Reloading policies");
				ClientConfig config = new DefaultClientConfig();
				Client client = Client.create(config);
				WebResource service = client.resource(this.policyLoaderUrl);
				logger.debug("refreshing policies explicitly.. ");
				ClientResponse response = service.path(PATH).get(ClientResponse.class);
				Status status = response.getClientResponseStatus();
				logger.debug("Response = "+status);
				return status.getStatusCode() >=200 && status.getStatusCode()<=300;
			} catch (RuntimeException e)
			{
				logger.debug("No policy reloaded",e);
				return false;
			}
			

		}
		else
		{
			logger.debug("No policy reload");
			return true;
		}

	}
	
}
