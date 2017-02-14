package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyDecisionEngine;

import java.util.Collection;
import java.util.List;

import org.gcube.portlets.admin.policydefinition.common.util.RoleHelper;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulClient;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

public class PoliciesContainer {

//	public static final String SERVICE 			= "Service";
	public static final String ROLE 			= "Role";
	public static final String SERVICE_CATEGORY = "Service Category";
	public static final String HOST 			= "gHN";
	public static final String PERMITTED 		= "Permitted";
	public static final String DATE_RANGE 		= "Date range";
	public static final String TIME_RANGE 		= "Time range";
	
	public static Container getPoliciesContainer(String serviceNameAndClass) throws Exception{
		// Create a container
		Container container = new IndexedContainer();

		// Define the properties (columns) if required by container
//		container.addContainerProperty(SERVICE, String.class, "none");
		container.addContainerProperty(ROLE, String.class, "none");
		container.addContainerProperty(SERVICE_CATEGORY, String.class, "none");
		container.addContainerProperty(HOST, String.class, "all");
		container.addContainerProperty(PERMITTED, Boolean.class, "none");
		container.addContainerProperty(DATE_RANGE, String.class, "none");
		container.addContainerProperty(TIME_RANGE, String.class, "none");
		
		RestfulClient client = RestfulClient.getInstance();
		List<RuleBean> ruleByService = client.getRuleByService(serviceNameAndClass);
		for (RuleBean ruleBean : ruleByService) {
			addItem(container, ruleBean);
		}
		
		return container;
	}
	
	public static Container getPoliciesContainer(Table policyTable){
		// Create a container
		Container container = new IndexedContainer();

		// Define the properties (columns) if required by container
//		container.addContainerProperty(SERVICE, String.class, "none");
		container.addContainerProperty(ROLE, String.class, "none");
		container.addContainerProperty(SERVICE_CATEGORY, String.class, "none");
		container.addContainerProperty(HOST, String.class, "all");
		container.addContainerProperty(PERMITTED, Boolean.class, "none");
		container.addContainerProperty(DATE_RANGE, String.class, "none");
		container.addContainerProperty(TIME_RANGE, String.class, "none");
		
		if(policyTable.getValue() instanceof Collection<?>){
			Collection<?> policies = (Collection<?>)policyTable.getValue();
			for (Object policy : policies) {
				addItem(container, policy, policyTable.getContainerDataSource().getItem(policy));
			}
		}
		return container;
	}
	
	public static void addItem(Container policiesContainer, Object id, Item policyItem){
		Item item = policiesContainer.addItem(id);
//		item.getItemProperty(SERVICE).setValue(policyItem.getItemProperty(SERVICE).getValue());
		item.getItemProperty(ROLE).setValue(policyItem.getItemProperty(ROLE).getValue());
		item.getItemProperty(SERVICE_CATEGORY).setValue(policyItem.getItemProperty(SERVICE_CATEGORY).getValue());
		item.getItemProperty(HOST).setValue(policyItem.getItemProperty(HOST).getValue());
		item.getItemProperty(PERMITTED).setValue(policyItem.getItemProperty(PERMITTED).getValue());
		item.getItemProperty(DATE_RANGE).setValue(policyItem.getItemProperty(DATE_RANGE).getValue());
		item.getItemProperty(TIME_RANGE).setValue(policyItem.getItemProperty(TIME_RANGE).getValue());
	}
	
	public static void addItem(Container policiesContainer, RuleBean ruleBean){
		Item item = policiesContainer.addItem(ruleBean.getRuleId());
//		item.getItemProperty(SERVICE).setValue(ruleBean.getAction());
		String role = ruleBean.getAttributes().get(PolicyDecisionEngine.ROLE_DEFAULT_ATTRIBUTE);
		item.getItemProperty(ROLE).setValue(RoleHelper.getRole(role));
		item.getItemProperty(SERVICE_CATEGORY).setValue(RoleHelper.getServiceCategory(role));
		item.getItemProperty(HOST).setValue(ruleBean.getResource());
		item.getItemProperty(PERMITTED).setValue(ruleBean.isPermitted());
		item.getItemProperty(DATE_RANGE).setValue(ruleBean.getDateRange());
		item.getItemProperty(TIME_RANGE).setValue(ruleBean.getTimeRange());
	}

}
