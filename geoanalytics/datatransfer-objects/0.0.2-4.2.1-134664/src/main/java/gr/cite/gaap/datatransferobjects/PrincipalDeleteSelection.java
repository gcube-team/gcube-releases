package gr.cite.gaap.datatransferobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PrincipalDeleteSelection {
	private List<String> principals = Collections.emptyList();
	private List<String> tenants = Collections.emptyList();

	public PrincipalDeleteSelection(List<String> principals, List<String> tenants) {
		this.principals = principals;
		this.tenants = tenants;
	}

	public PrincipalDeleteSelection() {
	}

	public List<String> getPrincipals() {
		return principals;
	}

	public void setPrincipals(List<String> users) {
		this.principals = users;
	}

	public List<String> getTenants() {
		return tenants;
	}

	public void setTenants(List<String> customers) {
		this.tenants = customers;
	}

	public List<PrincipalTenantPair> toPairs() throws Exception {
		List<PrincipalTenantPair> res = new ArrayList<PrincipalTenantPair>();
		if (principals.size() != tenants.size())
			throw new Exception("Principals/Tenants mismatch");
		Iterator<String> uIt = principals.iterator();
		Iterator<String> cIt = tenants.iterator();
		while (uIt.hasNext()) {
			String usr = uIt.next();
			String cus = cIt.next();
			if (cus.equalsIgnoreCase("None"))
				cus = null;
			res.add(new PrincipalTenantPair(usr, cus));
		}
		return res;
	}
}
