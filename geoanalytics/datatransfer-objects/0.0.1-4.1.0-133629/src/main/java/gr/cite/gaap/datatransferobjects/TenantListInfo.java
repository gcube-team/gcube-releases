package gr.cite.gaap.datatransferobjects;

public class TenantListInfo {
	private String name = null;
	private String eMail = null;
	private String code = null;
	private boolean active = true;

	public TenantListInfo() {
	}

	public TenantListInfo(String name, String eMail, String code, boolean active) {
		this.name = name;
		this.eMail = eMail;
		this.code = code;
		this.active = active;
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
