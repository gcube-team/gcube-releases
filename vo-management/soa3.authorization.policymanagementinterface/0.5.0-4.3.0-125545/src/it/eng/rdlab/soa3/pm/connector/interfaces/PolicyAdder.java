package it.eng.rdlab.soa3.pm.connector.interfaces;

import it.eng.rdlab.soa3.pm.connector.beans.AttributeBean;
import it.eng.rdlab.soa3.pm.connector.beans.Obligation;
import it.eng.rdlab.soa3.pm.connector.beans.Status;

import java.util.List;

import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.w3c.dom.Element;

/**
 * 
 * This class provides the methods to add policies or simple rules to the Policy Manager
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface PolicyAdder 
{

	/**
	 * 
	 * Adds a new simple rule
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param subjectAttributes a map (key - value) of the subject attributes
	 * @param action action id
	 * @param resource resource id
	 * @param permitted true if it is a permit rule, false otherwise
	 * @param obligation an obligation
	 * @param moveAfter boolean value indicating if the rule must be set as last rule of the policy
	 * @return the status object representing the result of the operation
	 */
	public Status addNewRule (String alias,List<AttributeBean> subjectAttributes, String action, String resource, boolean permitted, Obligation obligation, boolean moveAfter);
	
	/**
	 * 
	 * Adds a policy in XACML format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param index the index of the policy in the policy set
	 * @param policySetId the policy set id
	 * @param policyIdPrefix the policy id prefix
	 * @param xacmlPolicy the policy in XACML format
	 * @return the status object representing the result of the operation
	 */
	public Status addXACMLPolicy (String alias, int index, String policySetId, String policyIdPrefix, PolicyType xacmlPolicy);
	
	/**
	 * 
	 * Adds a policy in Element format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param index the index of the policy in the policy set
	 * @param policySetId the policy set id
	 * @param policyIdPrefix the policy id prefix
	 * @param xacmlPolicy the policy in XACML format as a DOM element
	 * @return the status object representing the result of the operation
	 * @return
	 */
	public Status addXACMLPolicy (String alias, int index, String policySetId, String policyIdPrefix, Element xacmlPolicy);
	
	/**
	 * 
	 * Adds a policy set in XACML format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param index the index of the policy set
	 * @param xacmlPolicySet the policy set in XACML format
	 * @return the status object representing the result of the operation
	 */
	public Status addXACMLPolicySet(String alias, int index, PolicySetType xacmlPolicySet);
	
	
	/**
	 * 
	 * Adds a policy set in Element format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param index the index of the policy set
	 * @param xacmlPolicySet the policy set  as a DOM element
	 * @return the status object representing the result of the operation
	 */
	public Status addXACMLPolicySet(String alias, int index, Element xacmlPolicySet);
	
	/**
	 * 
	 * Updates a Policy in XACML format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param version the version of the old policy
	 * @param xacmlPolicy the policy in XACML format
	 * @return  the status object representing the result of the operation
	 */
	public Status updateXACMLPolicy (String alias, int version, PolicyType xacmlPolicy);
	
	/**
	 * 
	 * Updates a Policy in Element format
	 * 
	 * @param alias the PAP alias (can be null)
	 * @param version the version of the old policy
	 * @param xacmlPolicy the policy as a DOM element
	 * @return  the status object representing the result of the operation
	 */
	public Status updateXACMLPolicy (String alias, int version, Element policy);
	


	

}
