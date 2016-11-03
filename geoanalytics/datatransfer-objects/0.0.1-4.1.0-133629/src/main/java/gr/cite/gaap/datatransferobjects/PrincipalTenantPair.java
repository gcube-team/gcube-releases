package gr.cite.gaap.datatransferobjects;

public class PrincipalTenantPair {
	private String principal = null;
	private String tenant = null;

	public PrincipalTenantPair(String principal, String tenant) {
		this.principal = principal;
		this.tenant = tenant;
	}

	public PrincipalTenantPair() {
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
