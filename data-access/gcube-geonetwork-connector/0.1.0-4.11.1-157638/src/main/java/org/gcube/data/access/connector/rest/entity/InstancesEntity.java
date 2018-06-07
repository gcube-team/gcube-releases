package org.gcube.data.access.connector.rest.entity;

import java.util.List;

public class InstancesEntity {

	private String baseEndpoint;
	private List<AccessibleCredentialsEntity> accessibleCredentials;
	private VersionEntity version;
	private String contextGroup;
	private String sharedGroup;
	private String publicGroup;

	public String getBaseEndpoint() {
		return baseEndpoint;
	}

	public void setBaseEndpoint(String baseEndpoint) {
		this.baseEndpoint = baseEndpoint;
	}

	public List<AccessibleCredentialsEntity> getAccessibleCredentials() {
		return accessibleCredentials;
	}

	public void setAccessibleCredentials(List<AccessibleCredentialsEntity> accessibleCredentials) {
		this.accessibleCredentials = accessibleCredentials;
	}

	public VersionEntity getVersion() {
		return version;
	}

	public void setVersion(VersionEntity version) {
		this.version = version;
	}

	public String getContextGroup() {
		return contextGroup;
	}

	public void setContextGroup(String contextGroup) {
		this.contextGroup = contextGroup;
	}

	public String getSharedGroup() {
		return sharedGroup;
	}

	public void setSharedGroup(String sharedGroup) {
		this.sharedGroup = sharedGroup;
	}

	public String getPublicGroup() {
		return publicGroup;
	}

	public void setPublicGroup(String publicGroup) {
		this.publicGroup = publicGroup;
	}

	public class VersionEntity {

		private String major;
		private String minor;
		private String build;

		public String getMajor() {
			return major;
		}

		public void setMajor(String major) {
			this.major = major;
		}

		public String getMinor() {
			return minor;
		}

		public void setMinor(String minor) {
			this.minor = minor;
		}

		public String getBuild() {
			return build;
		}

		public void setBuild(String build) {
			this.build = build;
		}

	}

}
