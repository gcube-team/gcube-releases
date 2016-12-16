package it.eng.rdlab.soa3.pm.connector.javaapi.engine;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.ResponseBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;

import java.util.List;

/**
 * 
 * This interface provides methods for managing rules
 * 
 * @author Ciro Formisano (ENG)
 *
 */

public interface PolicyEngine 
{
	/**
	 * 
	 * Creates a new rule
	 * 
	 * @param bean the rule
	 * @return a {@link ResponseBean} with the response status and, if ok, the rule id
	 */
	public ResponseBean createRule (RuleBean bean);
	
	/**
	 * 
	 * gets a policy
	 * 
	 * @param policyId the id of the rule
	 * @return the rule
	 */
	public RuleBean getRule (String ruleId);
	
	/**
	 * 
	 * Update rule
	 * 
	 * @param ruleId the id of the policy to be updated
	 * @param RuleBean the new rule
	 * @return a {@link ResponseBean} with the response status and, if ok, the new rule id
	 */
	public ResponseBean updateRule (String ruleId, RuleBean ruleBean);

	/**
	 * 
	 * @param ruleBean
	 * @return
	 */
	public String getRuleId (RuleBean ruleBean);

	
	/**
	 * 
	 * @param ruleId
	 * @return true if the creation succeeded, false otherwise
	 */
	public boolean deleteRule (String ruleId);
	
	/**
	 * 
	 * Returns a list of rules with given attributes map
	 * 
	 * @param subjects the subject attributes
	 * @return the rules
	 */
	public List<RuleBean> listRulesBySubjects  (List<Attribute> subjects);
	
	/**
	 * 
	 * Returns a list of rules with given action
	 * 
	 * @param action the action
	 * @return the rules
	 */
	public List<RuleBean>  listRulesByAction  (String action);
	
	/**
	 * 
	 * Deletes the complete repository
	 * 
	 * @return
	 */
	public boolean deleteAll ();
	
	/**
	 * 
	 * Returns a list of rules with given resource
	 * 
	 * @param respurce the resource
	 * @return the rules
	 */
	public List<RuleBean>   listRulesByResource  (String resource);

	/**
	 * Returns all the rules
	 * @return the rules
	 */
	public List<RuleBean> listRules ();
	

}
