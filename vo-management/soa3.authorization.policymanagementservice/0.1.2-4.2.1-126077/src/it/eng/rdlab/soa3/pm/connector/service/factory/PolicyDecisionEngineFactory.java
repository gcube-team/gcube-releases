package it.eng.rdlab.soa3.pm.connector.service.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.engine.PolicyDecisionEngineImpl;

public class PolicyDecisionEngineFactory 
{
	
	public static PolicyDecisionEngine getPolicyDecisionEngine ()
	{
		Logger logger = LoggerFactory.getLogger(PolicyDecisionEngineFactory.class);
		
		try
		{
			return new PolicyDecisionEngineImpl();
		}
		catch (Exception e)
		{
			logger.error("Unable to create a policy decision engine", e);
			return null;
		}
		
		
	}

}
