package it.eng.rdlab.soa3.pm.connector.interfaces;

public interface PolicyManagerConstants 
{
	
	public static final String 	PAP_SERVICE_NAMESPACE = "http://services.pap.authz.glite.org",
								PAP_HLPS_NAMESPACE = "http://org.glite.authz/wsdl/pap/services/highlevel_policy_management",
								XACML_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:policy:schema:os",
								PAP_ALIAS_TAG ="alias",
								PAP_ADD_RULE_TAG = "addRule",
								PAP_ADD_POLICY_TAG = "addPolicy",
								PAP_ADD_POLICY_RESPONSE = "addPolicyResponse",
								PAP_UPDATE_POLICY_TAG = "updatePolicy",
								PAP_UPDATE_POLICY_RESPONSE = "updatePolicyReturn",
								PAP_ADD_POLICY_SET_TAG = "addPolicySet",
								PAP_ADD_POLICY_SET_RESPONSE = "addPolicySetResponse",
								PAP_IS_PERMIT_TAG = "isPermit",
								PAP_ATTRIBUTE_LIST_TAG = "attributeList",
								PAP_POLICY_ID_TAG = "policyId",
								PAP_OBJECT_ID_TAG = "id",
								PAP_ACTION_VALUE_TAG = "actionValue",
								PAP_RESOURCE_VALUE_TAG = "resourceValue",
								PAP_ACTION_IDENTIFIER_TAG = "actionIdentifier",
								PAP_RULE_IDENTIFIER_TAG = "ruleIdentifier",
								PAP_OBLIGATION_VALUE_TAG = "obligationValue",
								PAP_OBLIGATION_SCOPE_TAG = "obligationScope",
								PAP_MOVE_AFTER_TAG = "moveAfter",
								
								PAP_ADD_RULE_RESPONSE_TAG = "addRuleResponse",
								PAP_ADD_RULE_RETURN_TAG = "addRuleReturn",
								
								PAP_ERASE_REPOSITORY_TAG = "eraseRepository",
								PAP_ERASE_REPOSITORY_RESPONSE_TAG = "eraseRepository",
								PAP_LIST_POLICIES_TAG = "listPolicies",
								PAP_LIST_POLICIES_RESPONSE = "listPoliciesResponse",
								PAP_LIST_POLICIES_RETURN_TAG = "listPoliciesReturn",
								
								PAP_GET_POLICY_TAG = "getPolicy",
								PAP_GET_POLICY_RETURN_TAG = "getPolicyResponse",
								
								PAP_LIST_POLICY_SETS_TAG = "listPolicySets",
								PAP_LIST_POLICY_SETS_RESPONSE_TAG = "listPolicySetsResponse",
								PAP_LIST_POLICY_SETS_RETURN_TAG = "listPolicySetsReturn",
								
								PAP_GET_POLICY_SET_TAG = "getPolicySet",
								PAP_GET_POLICY_SET__RETURN_TAG = "getPolicySetReturn",
								PAP_POLICY_SET_ID_TAG = "policySetId",
								
								PAP_REMOVE_POLICY_TAG = "removePolicy",
								PAP_REMOVE_POLICY_RETURN_TAG = "removePolicyReturn",
								PAP_REMOVE_POLICY_SET_TAG = "removePolicySet",
								PAP_REMOVE_POLICY_SET_RETURN_TAG = "removePolicySetReturn",
																	
								
								PAP_REMOVE_OBJECT_TAG = "removeObjectByIdAndReferences",
								PAP_REMOVE_OBJECT_RETURN_TAG = "removeObjectByIdAndReferencesReturn",

								
								PAP_POLICY_ELEMENT_TAG = "Policy",
								PAP_POLICY_SET_ELEMENT_TAG = "PolicySet",
								
								PAP_INDEX_TAG = "index",
								PAP_POLICY_ID_PREFIX_TAG = "policyIdPrefix",
								PAP_VERSION_TAG = "version";

	
	
	
	public static final String 	SIMPLE_POLICY_MANAGEMENT_SERVICE = "HighLevelPolicyManagementService",
								XACML_POLICY_MANAGEMENT_SERVICE = "XACMLPolicyManagementService";
								

}
