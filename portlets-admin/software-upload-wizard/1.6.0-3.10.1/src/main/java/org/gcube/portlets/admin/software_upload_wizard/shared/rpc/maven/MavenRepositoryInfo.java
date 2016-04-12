package org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven;

public class MavenRepositoryInfo implements IMavenRepositoryInfo {

	protected String id;
	protected String url;
	
	@SuppressWarnings("unused")
	private MavenRepositoryInfo() {
		// Serialization only
	}

	/**
	 * @param id
	 * @param url
	 */
	public MavenRepositoryInfo(String id, String url) {
		this.url = url;
		this.id = id;
	}

	public MavenRepositoryInfo(IMavenRepositoryInfo obj) {
		this.id = obj.getId();
		this.url = obj.getUrl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.softwaremanagementwidget.server.softwaremanager
	 * .maven.IMavenRepositoryInfo#getUrl()
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.softwaremanagementwidget.server.softwaremanager
	 * .maven.IMavenRepositoryInfo#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MavenRepositoryInfo [url=");
		builder.append(url);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}
}
