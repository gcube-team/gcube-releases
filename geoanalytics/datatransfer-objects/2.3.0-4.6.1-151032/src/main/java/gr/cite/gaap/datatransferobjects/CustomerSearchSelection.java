package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerSearchSelection {
	private static Logger logger = LoggerFactory.getLogger(CustomerSearchSelection.class);
	public List<String> tenantNames = Collections.emptyList();
	public boolean activeTenants;

	public long start;
	public long end;
	
	

	public CustomerSearchSelection() {
		super();
		logger.trace("Initialized default constructor for CustomerSearchSelection");
	}

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
