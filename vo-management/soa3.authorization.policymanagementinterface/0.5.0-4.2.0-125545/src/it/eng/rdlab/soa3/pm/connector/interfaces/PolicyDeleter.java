package it.eng.rdlab.soa3.pm.connector.interfaces;

import it.eng.rdlab.soa3.pm.connector.beans.Status;

/**
 * 
 * This class provides the methods to delete policies and policy sets
 * 
 * @author Ciro Formisano
 *
 */
public interface PolicyDeleter 
{
	/**
	 * 
	 * Clears the repository
	 * 
	 * @return the status object representing the result of the operation
	 */
	public Status clear ();
	
	/**
	 * 
	 * Deletes the policy
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param policyId the id of the policy to be deleted
	 * @return the status object representing the result of the operation
	 */
	public Status deletePolicy (String alias,String policyId);
	
	/**
	 * 
	 * Deletes the policy set
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param policySetId the id of the policy to be deleted
	 * @return the status object representing the result of the operation
	 */
	public Status deletePolicySet (String alias,String policySetId);
	
	/**
	 * 
	 * Deletes the rule
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param ruleId the id of the rule
	 * @return  the status object representing the result of the operation
	 */
	public Status deleteRule (String alias, String ruleId);
	
}
