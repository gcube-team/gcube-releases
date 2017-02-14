package gr.cite.gaap.datatransferobjects;

public class TenantActivationMessenger {
	private String id = null;
	private String tenant = null;
	private long startDate = -1;
	private long endDate = -1;
	private String shape = null;
	private String activationConfig = null;
	private boolean active = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getActivationConfig() {
		return activationConfig;
	}

	public void setActivationConfig(String activationConfig) {
		this.activationConfig = activationConfig;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
