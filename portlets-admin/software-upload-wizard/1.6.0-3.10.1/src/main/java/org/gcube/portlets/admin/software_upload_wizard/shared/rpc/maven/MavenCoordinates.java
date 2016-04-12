/**
 * 
 */
package org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven;

import java.io.Serializable;

public class MavenCoordinates implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2776857418948222557L;
	
	protected String groupId;
	protected String artifactId;
	protected String version;
	protected String packaging = "jar";
	
	@SuppressWarnings("unused")
	private MavenCoordinates() {
		// Serialization only
	}

	/**
	 * @param groupId
	 * @param artifactId
	 * @param version
	 */
	public MavenCoordinates(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public MavenCoordinates(String groupId, String artifactId, String version,
			String packaging) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.packaging = packaging;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return artifactId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	public String getPackaging() {
		return packaging;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MavenCoordinates) {
			MavenCoordinates tmp = (MavenCoordinates) obj;
			if (this.getArtifactId().equals(tmp.getArtifactId())
					&& this.getGroupId().equals(tmp.getGroupId())
					&& this.getVersion().equals(tmp.getVersion()))
				return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MavenCoordinates [groupId=");
		builder.append(getGroupId());
		builder.append(", artifactId=");
		builder.append(getArtifactId());
		builder.append(", version=");
		builder.append(getVersion());
		builder.append(", packaging=");
		builder.append(getPackaging());
		builder.append("]");
		return builder.toString();
	}
}
