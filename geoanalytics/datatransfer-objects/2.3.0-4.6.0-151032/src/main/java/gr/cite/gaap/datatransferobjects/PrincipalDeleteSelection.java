package gr.cite.gaap.datatransferobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalDeleteSelection {
	private static Logger logger = LoggerFactory.getLogger(PrincipalDeleteSelection.class);
	private List<String> principals = Collections.emptyList();
	private List<String> tenants = Collections.emptyList();

	public PrincipalDeleteSelection(List<String> principals, List<String> tenants) {
		logger.trace("Initializing PrincipalDeleteSelection...");
		this.principals = principals;
		this.tenants = tenants;
		logger.trace("Initialized PrincipalDeleteSelection");
	}

	public PrincipalDeleteSelection() {
		logger.trace("Initialized default contructor for PrincipalDeleteSelection");
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
