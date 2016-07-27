package it.eng.rdlab.soa3.pm.connector.javaapi.engine;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.AuthZRequestBean;

/**
 * 
 * This interface allows policy decision queries
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface PolicyDecisionEngine 
{
	public String ROLE_DEFAULT_ATTRIBUTE = "role";
	
	
	/**
	 * 
	 * Policy decision query endpoint
	 * 
	 * @param ruleBean
	 * @return
	 */
	public boolean getDecision (AuthZRequestBean requestBean);

}
