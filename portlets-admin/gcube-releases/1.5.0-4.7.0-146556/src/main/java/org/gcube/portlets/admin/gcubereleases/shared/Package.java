/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.shared;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.gcube.portlets.admin.gcubereleases.server.persistence.PackageEntityListener;

/**
 * The Class Package.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@Entity
@CascadeOnDelete
@EntityListeners(PackageEntityListener.class)
public class Package implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -6248650932442376364L;

	/**
	 *
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY

	private String groupID;
	private String artifactID;
	private String version;
	private String ID;
	private String URL;
	private String javadoc;
	private String status;
	private String Operation;
	private String Timestamp;
	private String wikidoc;
	private String svnpath;
	private String eticsRef;
	private String gitHubPath;

	private String releaseIdRef; // is release id property

	@CascadeOnDelete
	private Release release;

	@OneToOne(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@CascadeOnDelete
	private AccountingPackage accouting;

	public static final String ID_FIELD = "ID";

	public static final String RELEASE_ID_REF = "releaseIdRef";
	public static final String RELEASE = "release";
	public static final String ACCOUNTING = "accouting";
	public static final String GROUPID = "groupID";
	public static final String ARTIFACTID = "artifactID";
	public static final String VERSION = "version";

	/**
	 * Instantiates a new package.
	 */
	public Package() {
	}

	/**
	 * Instantiates a new package.
	 *
	 * @param groupID the group id
	 * @param artifactID the artifact id
	 * @param version the version
	 * @param iD the i d
	 * @param uRL the u rl
	 * @param javadoc the javadoc
	 * @param status the status
	 * @param operation the operation
	 * @param timestamp the timestamp
	 * @param wikidoc the wikidoc
	 * @param svnpath the svnpath
	 * @param eticsRef the etics ref
	 * @param releaseIdRef the release id ref
	 */
	public Package(String groupID, String artifactID, String version,
			String iD, String uRL, String javadoc, String status,
			String operation, String timestamp, String wikidoc, String svnpath,
			String eticsRef, String releaseIdRef) {
		this.groupID = groupID;
		this.artifactID = artifactID;
		this.version = version;
		ID = iD;
		URL = uRL;
		this.javadoc = javadoc;
		this.status = status;
		Operation = operation;
		Timestamp = timestamp;
		this.wikidoc = wikidoc;
		this.svnpath = svnpath;
		this.eticsRef = eticsRef;
		this.releaseIdRef = releaseIdRef;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public int getInternalId() {
		return internalId;
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
	 * Gets the artifact id.
	 *
	 * @return the artifact id
	 */
	public String getArtifactID() {
		return artifactID;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * Gets the javadoc.
	 *
	 * @return the javadoc
	 */
	public String getJavadoc() {
		return javadoc;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	public String getOperation() {
		return Operation;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return Timestamp;
	}

	/**
	 * Gets the wikidoc.
	 *
	 * @return the wikidoc
	 */
	public String getWikidoc() {
		return wikidoc;
	}

	/**
	 * Gets the svnpath.
	 *
	 * @return the svnpath
	 */
	public String getSvnpath() {
		return svnpath;
	}

	/**
	 * Gets the etics ref.
	 *
	 * @return the etics ref
	 */
	public String getEticsRef() {
		return eticsRef;
	}

	/**
	 * Gets the release id ref.
	 *
	 * @return the release id ref
	 */
	public String getReleaseIdRef() {
		return releaseIdRef;
	}

	/**
	 * Gets the accouting.
	 *
	 * @return the accouting
	 */
	public AccountingPackage getAccouting() {
		return accouting;
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
	 * Sets the artifact id.
	 *
	 * @param artifactID the new artifact id
	 */
	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Sets the id.
	 *
	 * @param iD the new id
	 */
	public void setID(String iD) {
		ID = iD;
	}

	/**
	 * Sets the url.
	 *
	 * @param uRL the new url
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * Sets the javadoc.
	 *
	 * @param javadoc the new javadoc
	 */
	public void setJavadoc(String javadoc) {
		this.javadoc = javadoc;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets the operation.
	 *
	 * @param operation the new operation
	 */
	public void setOperation(String operation) {
		Operation = operation;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}

	/**
	 * Sets the wikidoc.
	 *
	 * @param wikidoc the new wikidoc
	 */
	public void setWikidoc(String wikidoc) {
		this.wikidoc = wikidoc;
	}

	/**
	 * Sets the svnpath.
	 *
	 * @param svnpath the new svnpath
	 */
	public void setSvnpath(String svnpath) {
		this.svnpath = svnpath;
	}

	/**
	 * Sets the etics ref.
	 *
	 * @param eticsRef the new etics ref
	 */
	public void setEticsRef(String eticsRef) {
		this.eticsRef = eticsRef;
	}

	/**
	 * Sets the release id ref.
	 *
	 * @param releaseIdRef the new release id ref
	 */
	public void setReleaseIdRef(String releaseIdRef) {
		this.releaseIdRef = releaseIdRef;
	}

	/**
	 * Sets the accouting.
	 *
	 * @param accouting the new accouting
	 */
	public void setAccouting(AccountingPackage accouting) {
		this.accouting = accouting;
	}

	/**
	 * Gets the release.
	 *
	 * @return the release
	 */
	public Release getRelease() {
		return release;
	}

	/**
	 * Sets the release.
	 *
	 * @param release the new release
	 */
	public void setRelease(Release release) {
		this.release = release;
	}

	/**
	 * @param value
	 */
	public void setGithubPath(String value) {
		this.gitHubPath = value;
	}

	/**
	 * @return the gitHubPath
	 */
	public String getGitHubPath() {

		return gitHubPath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Package [internalId=");
		builder.append(internalId);
		builder.append(", groupID=");
		builder.append(groupID);
		builder.append(", artifactID=");
		builder.append(artifactID);
		builder.append(", version=");
		builder.append(version);
		builder.append(", ID=");
		builder.append(ID);
		builder.append(", URL=");
		builder.append(URL);
		builder.append(", javadoc=");
		builder.append(javadoc);
		builder.append(", status=");
		builder.append(status);
		builder.append(", Operation=");
		builder.append(Operation);
		builder.append(", Timestamp=");
		builder.append(Timestamp);
		builder.append(", wikidoc=");
		builder.append(wikidoc);
		builder.append(", svnpath=");
		builder.append(svnpath);
		builder.append(", eticsRef=");
		builder.append(eticsRef);
		builder.append(", releaseIdRef=");
		builder.append(releaseIdRef);
		builder.append(", release=");
		builder.append(release);
		builder.append(", accouting=");
		builder.append(accouting);
		builder.append(", gitHubPath=");
		builder.append(gitHubPath);
		builder.append("]");
		return builder.toString();
	}
}
