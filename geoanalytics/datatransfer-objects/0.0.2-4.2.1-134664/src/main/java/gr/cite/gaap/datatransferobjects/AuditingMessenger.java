package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;

public class AuditingMessenger {
	private String id = null;
	private long date = -1;
	private AuditingType type = null;
	private String principal = null;
	private String tenant = null;
	private String data = null;

	private AuditingMessenger() {
	}

	private AuditingMessenger(Auditing auditing) {
		this.id = auditing.getId().toString();
		this.date = auditing.getDate().getTime();
		this.type = auditing.getType();
		this.principal = auditing.getPrincipal().getName();
		this.tenant = auditing.getTenant().getName();
		this.data = auditing.getData();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public AuditingType getType() {
		return type;
	}

	public void setType(AuditingType type) {
		this.type = type;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
