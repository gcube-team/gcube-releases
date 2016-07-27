package org.gcube.portlets.admin.policydefinition.services.restful;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.gcube.portlets.admin.policydefinition.services.informationsystem.InformationSystemClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import com.liferay.util.portlet.PortletProps;
import com.vaadin.data.Item;

/**
 * RESTful client for UserManagement and PolicyManagement.
 *
 */
public class RestfulClient {

	private static ClientExecutor executor;
	private static RestfulClient instance;
	
//	private final static String SOA3_BASE_URL_PARAM_NAME = "soa3.base.url";
	private final static String LIST_ROLES_URL = "/userService/rolemanager/roles/{organizationName}";
	private final static String POLICY_MGMT_CREATE_URL = "/policyService/policymanager/";
	private final static String POLICY_MGMT_GET_UPDATE_DELETE_URL = "/policyService/policymanager/{ruleId}";
	private final static String POLICY_MGMT_GET_RULES_BY_ROLE_URL = "/policyService/policymanager/subject/{subjectid}/{subjectvalue}";
	private final static String POLICY_MGMT_GET_RULES_BY_SERVICE_URL = "/policyService/policymanager/action/{action}";
	private final static String USERMANAGEMENT_ORGANIZATION_NAME_PARAMETER_NAME = "usermanagement.organization";
	
	private RestfulClient(){
		ClientConnectionManager cm = new ThreadSafeClientConnManager();
		HttpClient httpClient = new DefaultHttpClient(cm);
		executor = new ApacheHttpClient4Executor(httpClient);
	}
	
	/**
	 * Return the singleton instance of {@link RestfulClient}
	 * @return {@link RestfulClient}
	 */
	public synchronized static RestfulClient getInstance(){
		if(instance == null)
			instance = new RestfulClient();
		return instance;
	}
	
	/**
	 * Retrieve roles from UserManagement.
	 * @return List of role names
	 * @throws Exception
	 */
	public List<String> getRoles() throws RestfulResponseException{
		ClientResponse<UserMgmtRolesBean> res = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+LIST_ROLES_URL, executor);
			client.pathParameter("organizationName", PortletProps.get(USERMANAGEMENT_ORGANIZATION_NAME_PARAMETER_NAME));
			res = client.get(UserMgmtRolesBean.class);
			checkOkResponse(res);
			List<String> response = res.getEntity().getRoles();
	        return response;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(res != null)
				res.releaseConnection();
		}
	}
	
	/**
	 * Create rule on PolicyManagement.
	 * @param rule
	 * @return the new {@link RuleBean}
	 * @throws Exception
	 */
	public RuleBean createRule(RuleBean rule) throws RestfulResponseException{
		ClientResponse<String> resp = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_CREATE_URL, executor);
			client.body("application/json", rule);
			resp = client.post(String.class);
			checkOkResponse(resp);
			String newRuleId = resp.getEntity();
			rule.setRuleId(newRuleId);
			return rule;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(resp != null)
				resp.releaseConnection();
		}
	}
	
	/**
	 * Create multiple rules.
	 * @param service
	 * @param roles
	 * @param hosts
	 * @param permit
	 * @param timeRange
	 * @param dateRange
	 * @return list of {@link RuleBean}
	 * @throws Exception
	 */
	public List<RuleBean> createRules(String service, Collection<Item> roles, Collection<?> hosts, boolean permit, String timeRange, String dateRange) throws RestfulResponseException{
		ArrayList<RuleBean> result = new ArrayList<RuleBean>();
		for (Item role : roles) {
			if((hosts == null || hosts.size() == 0) ){
				result.add(createRule(createRuleBean(null, service, (String)role.getItemProperty("role").getValue(), ".*", permit, timeRange, dateRange)));
			} else {
				for (Object host : hosts) {
					if(host instanceof String)
						result.add(createRule(createRuleBean(null, service, (String)role.getItemProperty("role").getValue(), (String)host, permit, timeRange, dateRange)));
				}
			}
		}
		return result;		
	}
	
	/**
	 * Retrieve rule by ruleId from PolicyManagement.
	 * @param ruleId
	 * @return the {@link RuleBean}
	 * @throws Exception
	 */
	public RuleBean getRule(String ruleId) throws RestfulResponseException{
		ClientResponse<RuleBean> clientResponse = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_GET_UPDATE_DELETE_URL, executor);
			client.pathParameter("ruleId", ruleId);
			clientResponse = client.get(RuleBean.class);
			checkOkResponse(clientResponse);
			RuleBean response = clientResponse.getEntity();
			return response;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(clientResponse != null)
				clientResponse.releaseConnection();
		}
	}
	
	/**
	 * Retrieve rule by role from PolicyManagement.
	 * @param role
	 * @return list of {@link RuleBean}
	 * @throws Exception
	 */
	public List<RuleBean> getRuleByRole(String role) throws RestfulResponseException{
		ClientResponse<PolicyManagementRulesBean> clientResponse = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_GET_RULES_BY_ROLE_URL, executor);
			client.pathParameter("subjectid", PolicyDecisionEngine.ROLE_DEFAULT_ATTRIBUTE);
			client.pathParameter("subjectvalue", role);
			clientResponse = client.get(PolicyManagementRulesBean.class);
			checkOkResponse(clientResponse);
			List<RuleBean> response = clientResponse.getEntity().getBeanList();
			return response;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(clientResponse != null)
				clientResponse.releaseConnection();
		}
	}
	
	/**
	 * Retrieve rule by Service name from PolicyManagement.
	 * @param service
	 * @return list of {@link RuleBean}
	 * @throws RestfulResponseException 
	 */
	public List<RuleBean> getRuleByService(String service) throws RestfulResponseException{
		ClientResponse<PolicyManagementRulesBean> clientResponse = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_GET_RULES_BY_SERVICE_URL, executor);
			client.pathParameter("action", service);
			clientResponse = client.get(PolicyManagementRulesBean.class);
			checkOkResponse(clientResponse);
			List<RuleBean> response = clientResponse.getEntity().getBeanList();
			return response;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(clientResponse != null)
				clientResponse.releaseConnection();
		}
	}
	
	/**
	 * Update the rule on PolicyManagement.
	 * @param rule
	 * @return the new {@link RuleBean}
	 * @throws Exception
	 */
	public RuleBean updateRule(String ruleId, String service, String role, String host, boolean permit, String timeRange, String dateRange) throws RestfulResponseException{
		return updateRule(createRuleBean(ruleId, service, role, host, permit, timeRange, dateRange));
	}
	
	/**
	 * Update the rule on PolicyManagement.
	 * @param rule {@link RuleBean}
	 * @return the new {@link RuleBean}
	 * @throws Exception
	 */
	public RuleBean updateRule(RuleBean rule) throws RestfulResponseException{
		ClientResponse<String> resp = null;
		try {
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_GET_UPDATE_DELETE_URL, executor);
			client.body("application/json", rule);
			client.pathParameter("ruleId", rule.getRuleId());
			resp = client.put(String.class);
			checkOkResponse(resp);
			String newRuleId = resp.getEntity();
			rule.setRuleId(newRuleId);			
			return rule;
		} catch (Exception e) {
			throw new RestfulResponseException(e);
		} finally {
			if(resp != null)
				resp.releaseConnection();
		}
	}
	
	/**
	 * Delete the rule on PolicyManagement.
	 * @param ruleId
	 * @throws Exception
	 */
	public void deleteRule(String ruleId) throws RestfulResponseException{
		ClientResponse<String> clientResponse = null;
		try{
			String soa3Url = InformationSystemClient.getInstance().retrieveSoa3Url();
			ClientRequest client = new ClientRequest(soa3Url+POLICY_MGMT_GET_UPDATE_DELETE_URL, executor);
			client.pathParameter("ruleId", ruleId);
			clientResponse = client.delete(String.class);
			checkOkResponse(clientResponse);
		} catch (Exception e){
			throw new RestfulResponseException(e);
		} finally {
			if(clientResponse != null)
				clientResponse.releaseConnection();
		}
	}
	
	private static void checkOkResponse(ClientResponse<?> response) throws RestfulResponseException{
		if (response.getStatus() != Response.Status.OK.getStatusCode()
				&& response.getStatus() != Response.Status.CREATED.getStatusCode())
			throw new RestfulResponseException(response.getStatus(), response.getEntity(String.class));
	}

	private static RuleBean createRuleBean(String ruleId, String service, String role, String host, boolean permit, String timeRange, String dateRange){
		RuleBean rule = new RuleBean();
		rule.setRuleId(ruleId);
		rule.setAction(service); //serviceClass:serviceName
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(PolicyDecisionEngine.ROLE_DEFAULT_ATTRIBUTE, role);
		rule.setAttributes(attributes);
		rule.setResource(host == null? ".*" : host);
		rule.setPermitted(permit);
		rule.setTimeRange(timeRange);
		rule.setDateRange(dateRange);
		return rule;
	}

}
