package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSearchSelection {
	private static Logger logger = LoggerFactory.getLogger(UserSearchSelection.class);

	public List<String> principalNames = Collections.emptyList();
	public List<String> tenantNames = Collections.emptyList();
	public boolean activePrincipal;
	public boolean activeTenants;

	
	
	public UserSearchSelection() {
		super();
		logger.trace("Initialized default contructor for UserSearchSelection");

	}

	public List<String> getPrincipalNames() {
		return principalNames;
	}

	public void setPrincipalNames(List<String> userNames) {
		this.principalNames = userNames;
	}

	public List<String> getTenantNames() {
		return tenantNames;
	}

	public void setTenantNames(List<String> customerNames) {
		this.tenantNames = customerNames;
	}

	public boolean isActivePrincipal() {
		return activePrincipal;
	}

	public void setActivePrincipal(boolean activeUsers) {
		this.activePrincipal = activeUsers;
	}

	public boolean isActiveTenants() {
		return activeTenants;
	}

	public void setActiveTenants(boolean activeCustomers) {
		this.activeTenants = activeCustomers;
	}
}
