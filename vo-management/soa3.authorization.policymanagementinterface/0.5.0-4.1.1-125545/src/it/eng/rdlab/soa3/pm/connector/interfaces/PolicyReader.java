package it.eng.rdlab.soa3.pm.connector.interfaces;

import it.eng.rdlab.soa3.pm.connector.beans.Status;

/**
 * 
 * This class provides the methods to read and get policies and policy sets
 * 
 * @author Ciro Formisano
 *
 */
public interface PolicyReader 
{
	/**
	 * 
	 * Lists all the policies in the PAP
	 * 
	 * @param alias the PAP alias (can be null)
	 * @return the status object representing the result of the operation
	 */
	public Status listPolicies (String alias);
	
	/**
	 * 
	 * Gets a policy
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param policyId the id of the policy
	 * @return the status object representing the result of the operation
	 */
	public Status getPolicy (String alias,String policyId);
	
	/**
	 * 
	 * Lists all the policy sets in the PAP
	 * 
	 * @param alias the PAP alias (can be null)
	 * @return the status object representing the result of the operation
	 */
	public Status listPolicySets (String alias);
	
	/**
	 * 
	 * Gets a policy set
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param policySetId the id of the policy
	 * @return the status object representing the result of the operation
	 */
	public Status getPolicySet (String alias,String policySetId);
	
}
