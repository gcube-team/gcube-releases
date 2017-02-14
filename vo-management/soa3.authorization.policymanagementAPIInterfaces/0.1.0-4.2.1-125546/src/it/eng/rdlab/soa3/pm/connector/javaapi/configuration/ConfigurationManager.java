package it.eng.rdlab.soa3.pm.connector.javaapi.configuration;

public interface ConfigurationManager 
{
	/**
	 * 
	 * Provides the url of the Policy Manager
	 * 
	 * @return
	 */
	public String getPolicyManagerUrl ();
	
	/**
	 * 
	 * Provides the url of the Authorization Query Endpoint
	 * 
	 * @return
	 */
	public String getAuthQueryEndpoint ();
	
	/**
	 * 
	 * Provides the url of the endpoint to call for reloading policies
	 * 
	 * @return
	 */
	public String getPolicyLoaderUrl ();
	
	/**
	 * 
	 * Provides the behaviour on indeterminate decisions
	 * 
	 * @return true if permit, false if deny
	 */
	public boolean getIndeterminateDecision ();
	
	/**
	 * 
	 * @return
	 */
	public boolean explicitFinalStatement ();
	

}
