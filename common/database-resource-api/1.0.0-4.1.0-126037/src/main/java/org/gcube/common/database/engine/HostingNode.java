package org.gcube.common.database.engine;

public class HostingNode {
	
	private String hostingURL;
	
	private String ghnUniqueId;
	
	private String status;
	
	public HostingNode() {}

	public HostingNode(String hostingURL, String ghnUniqueId, String status) {
		super();
		this.hostingURL = hostingURL;
		this.ghnUniqueId = ghnUniqueId;
		this.status = status;
	}

	public String getHostingURL() {
		return hostingURL;
	}

	public String getGhnUniqueId() {
		return ghnUniqueId;
	}

	public String getStatus() {
		return status;
	}

}
