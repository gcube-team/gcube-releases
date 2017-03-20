package it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils;

import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleType;

/**
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyRuleBean 
{
	private PolicyType policyType;
	private RuleType ruleType;
	private PolicySetType policySet;
	
	public PolicyType getPolicyType() {
		return policyType;
	}
	
	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public PolicySetType getPolicySetType() {
		return policySet;
	}

	public void setPolicySetType(PolicySetType policySet) {
		this.policySet = policySet;
	}

	

}
