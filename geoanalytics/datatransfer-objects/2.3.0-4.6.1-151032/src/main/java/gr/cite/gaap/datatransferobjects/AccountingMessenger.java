package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting.AccountingType;

public class AccountingMessenger {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingMessenger.class);
	
	private String id = null;
	private AccountingType type = null;
	private Long date = null;
	private String principal = null;
	private String tenant = null;
	private Float units = 0.0f;
	private boolean valid = false;
	private String referenceData = null;

	public AccountingMessenger() {
	}

	public AccountingMessenger(Accounting acc) {
		logger.trace("Initializing AccountingMessenger...");
		this.id = acc.getId().toString();
		this.type = acc.getType();
		this.units = acc.getUnits();
		this.date = acc.getDate().getTime();
		this.principal = acc.getPrincipal().getName();
		this.tenant = acc.getTenant().getName();
		this.valid = acc.getIsValid();
		this.referenceData = acc.getReferenceData();
		logger.trace("Initialized AccountingMessenger");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AccountingType getType() {
		return type;
	}

	public void setType(AccountingType type) {
		this.type = type;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Float getUnits() {
		return units;
	}

	public void setUnits(Float units) {
		this.units = units;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getReferenceData() {
		return referenceData;
	}

	public void setReferenceData(String referenceData) {
		this.referenceData = referenceData;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
}
