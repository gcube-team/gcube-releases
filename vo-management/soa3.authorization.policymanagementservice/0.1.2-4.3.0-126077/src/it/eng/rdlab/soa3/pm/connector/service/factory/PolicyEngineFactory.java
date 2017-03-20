package it.eng.rdlab.soa3.pm.connector.service.factory;

import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyEngine;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.engine.PolicyEngineImpl;

/**
 * 
 * Generic factory for the policy engine
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyEngineFactory 
{
	/**
	 * 
	 * @return a new instance of the Policy Engine
	 */
	public static PolicyEngine getPolicyEngine ()
	{
		return new PolicyEngineImpl();
	}
}
