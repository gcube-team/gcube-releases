package it.eng.rdlab.soa3.test;

import it.eng.rdlab.soa3.pm.connector.javaapi.configuration.ConfigurationManager;

public class TestConfigurationManager implements ConfigurationManager {

	@Override
	public String getPolicyManagerUrl() 
	{
		return "https://CiroMobile.eng.it:8150/pap/services";
	}

	@Override
	public String getAuthQueryEndpoint() 
	{
		return "https://argus.vision.res.eng.it:8154/authz";
	}

	@Override
	public String getPolicyLoaderUrl() 
	{
		return "http://CiroMobile.eng.it:8153";
	}

	@Override
	public boolean getIndeterminateDecision() 
	{
		return false;
	}

	@Override
	public boolean explicitFinalStatement() {
		return false;
	}

}
