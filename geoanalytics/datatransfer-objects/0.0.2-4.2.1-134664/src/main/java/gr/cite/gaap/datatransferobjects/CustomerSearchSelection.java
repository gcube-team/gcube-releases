package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CustomerSearchSelection {
	public List<String> tenantNames = Collections.emptyList();
	public boolean activeTenants;

	public long start;
	public long end;

	public List<String> getTenantNames() {
		return tenantNames;
	}

	public void setTenantNames(List<String> tenantNames) {
		this.tenantNames = tenantNames;
	}

	public boolean isActiveTenants() {
		return activeTenants;
	}

	public void setActiveTenants(boolean activeCustomers) {
		this.activeTenants = activeCustomers;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
}
