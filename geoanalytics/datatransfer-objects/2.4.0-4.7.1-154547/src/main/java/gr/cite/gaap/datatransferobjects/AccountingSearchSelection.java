package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting.AccountingType;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingSearchSelection {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingSearchSelection.class);
	public List<String> principalNames = Collections.emptyList();
	public List<String> tenantNames = Collections.emptyList();
	public Long fromDate = null;
	public Long toDate = null;
	public AccountingType type = null;
	
	

	public AccountingSearchSelection() {
		super();
		logger.trace("Initialized default constructor for AccountingSearchSelection");
	}

	public List<String> getPrincipalNames() {
		return principalNames;
	}

	public void setPrincipalNames(List<String> principalNames) {
		this.principalNames = principalNames;
	}

	public List<String> getTenantNames() {
		return tenantNames;
	}

	public void setTenantNames(List<String> tenantNames) {
		this.tenantNames = tenantNames;
	}

	public Long getFromDate() {
		return fromDate;
	}

	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}

	public Long getToDate() {
		return toDate;
	}

	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}

	public AccountingType getType() {
		return type;
	}

	public void setType(AccountingType type) {
		this.type = type;
	}

}
