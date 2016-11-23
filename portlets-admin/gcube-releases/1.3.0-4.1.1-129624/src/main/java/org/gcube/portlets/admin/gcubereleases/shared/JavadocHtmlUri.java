/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The Class JavadocHtmlUri.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@Entity
public class JavadocHtmlUri {
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY
	
	private String javadocJar;
	private String artifactID;
	private String releaseID;
	private String groupID;
	
	private String javadocHtmlUri;
	
	public static final String JAVADOC_JAR = "javadocJar";
	public static final String ARTIFACT_ID = "artifactID";
	public static final String RELEASE_ID = "releaseID";
	public static final String GROUP_ID = "groupID";
	
	/**
	 * Instantiates a new javadoc html uri.
	 */
	public JavadocHtmlUri(){}

	/**
	 * Instantiates a new javadoc html uri.
	 *
	 * @param javadocJar the javadoc jar
	 * @param artifactID the artifact id
	 * @param releaseID the release id
	 * @param groupID the group id
	 * @param javadocHtmlUri the javadoc html uri
	 */
	public JavadocHtmlUri(String javadocJar, String artifactID,
			String releaseID, String groupID, String javadocHtmlUri) {
		this.javadocJar = javadocJar;
		this.artifactID = artifactID;
		this.releaseID = releaseID;
		this.groupID = groupID;
		this.javadocHtmlUri = javadocHtmlUri;
	}

	/**
	 * Gets the javadoc jar.
	 *
	 * @return the javadoc jar
	 */
	public String getJavadocJar() {
		return javadocJar;
	}

	/**
	 * Gets the artifact id.
	 *
	 * @return the artifact id
	 */
	public String getArtifactID() {
		return artifactID;
	}

	/**
	 * Gets the release id.
	 *
	 * @return the release id
	 */
	public String getReleaseID() {
		return releaseID;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * Gets the javadoc html uri.
	 *
	 * @return the javadoc html uri
	 */
	public String getJavadocHtmlUri() {
		return javadocHtmlUri;
	}

	/**
	 * Sets the javadoc jar.
	 *
	 * @param javadocJar the new javadoc jar
	 */
	public void setJavadocJar(String javadocJar) {
		this.javadocJar = javadocJar;
	}

	/**
	 * Sets the artifact id.
	 *
	 * @param artifactID the new artifact id
	 */
	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}

	/**
	 * Sets the release id.
	 *
	 * @param releaseID the new release id
	 */
	public void setReleaseID(String releaseID) {
		this.releaseID = releaseID;
	}

	/**
	 * Sets the group id.
	 *
	 * @param groupID the new group id
	 */
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	/**
	 * Sets the javadoc html uri.
	 *
	 * @param javadocHtmlUri the new javadoc html uri
	 */
	public void setJavadocHtmlUri(String javadocHtmlUri) {
		this.javadocHtmlUri = javadocHtmlUri;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public int getInternalId() {
		return internalId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JavadocHtmlUri [internalId=");
		builder.append(internalId);
		builder.append(", javadocJar=");
		builder.append(javadocJar);
		builder.append(", artifactID=");
		builder.append(artifactID);
		builder.append(", releaseID=");
		builder.append(releaseID);
		builder.append(", groupID=");
		builder.append(groupID);
		builder.append(", javadocHtmlUri=");
		builder.append(javadocHtmlUri);
		builder.append("]");
		return builder.toString();
	}
	
}
