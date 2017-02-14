package it.eng.rdlab.soa3.pm.connector.javaapi.impl.factory;

import it.eng.rdlab.soa3.pm.connector.impl.PolicyAdderImpl;
import it.eng.rdlab.soa3.pm.connector.impl.PolicyDeleterImpl;
import it.eng.rdlab.soa3.pm.connector.impl.PolicyReaderImpl;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyAdder;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyDeleter;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyReader;
import it.eng.rdlab.soa3.pm.connector.javaapi.configuration.ConfigurationManagerBuilder;

/**
 * 
 * Policy Manager Factory
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyManagerFactory 
{
	
	/**
	 * 
	 * Returns a new instance of Policy Adder
	 * 
	 * @return the Policy Adder
	 */
	public static PolicyAdder getPolicyAdder ()
	{
		return new PolicyAdderImpl(ConfigurationManagerBuilder.getConfigurationManager().getPolicyManagerUrl());
	}
	
	
	/**
	 * 
	 * Returns a new instance of Policy Deleter
	 * 
	 * @return the Policy Deleter
	 */
	public static PolicyDeleter getPolicyDeleter ()
	{
		return new PolicyDeleterImpl(ConfigurationManagerBuilder.getConfigurationManager().getPolicyManagerUrl());
	}
	
	
	/**
	 * 
	 * Returns a new instance of Policy Reader
	 * 
	 * @return the Policy Reader
	 */
	public static PolicyReader getPolicyReader ()
	{
		return new PolicyReaderImpl(ConfigurationManagerBuilder.getConfigurationManager().getPolicyManagerUrl());
	}

}
