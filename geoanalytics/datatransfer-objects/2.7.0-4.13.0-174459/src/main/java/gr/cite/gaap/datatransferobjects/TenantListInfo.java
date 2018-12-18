package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantListInfo {
	private static Logger logger = LoggerFactory.getLogger(TenantListInfo.class);

	private String name = null;
	private String eMail = null;
	private String code = null;
	private boolean active = true;

	public TenantListInfo() {
		logger.trace("Initialized default contructor for TenantListInfo");

	}

	public TenantListInfo(String name, String eMail, String code, boolean active) {
		logger.trace("Initializing TenantListInfo...");

		this.name = name;
		this.eMail = eMail;
		this.code = code;
		this.active = active;
		logger.trace("Initialized TenantListInfo");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
