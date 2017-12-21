package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalTenantPair {
	private static Logger logger = LoggerFactory.getLogger(PrincipalTenantPair.class);

	private String principal = null;
	private String tenant = null;

	public PrincipalTenantPair(String principal, String tenant) {
		logger.trace("Initializing PrincipalTenantPair...");
		this.principal = principal;
		this.tenant = tenant;
		logger.trace("Initialized PrincipalTenantPair");
	}

	public PrincipalTenantPair() {
		logger.trace("Initialized default contructor for PrincipalTenantPair");
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String user) {
		this.principal = user;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String customer) {
		this.tenant = customer;
	}
}
