package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class ThSynchFolderConfiguration.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2018
 */
public class ThSynchFolderConfiguration implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5807533629170443212L;

	/** The remote path. */
	private String remotePath;

	/** The filter. */
	private String filter;

	/** The target token. */
	private String targetToken;

	/** The to create catalog name. */
	private String toCreateCatalogName;

	/** The remote persistence. */
	private String remotePersistence="thredds";

	private String rootFolderId;


	public ThSynchFolderConfiguration() {
		// TODO Auto-generated constructor stub
	}

	public ThSynchFolderConfiguration(String remotePath, String filter, String targetToken, String toCreateCatalogName,
			String remotePersistence, String rootFolderId) {
		super();
		this.remotePath = remotePath;
		this.filter = filter;
		this.targetToken = targetToken;
		this.toCreateCatalogName = toCreateCatalogName;
		this.remotePersistence = remotePersistence;
		this.rootFolderId = rootFolderId;
	}



	/**
	 * @return the rootFolderId
	 */
	public String getRootFolderId() {

		return rootFolderId;
	}


	/**
	 * @param rootFolderId the rootFolderId to set
	 */
	public void setRootFolderId(String rootFolderId) {

		this.rootFolderId = rootFolderId;
	}


	public String getRemotePath() {
		return remotePath;
	}




	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}




	public String getFilter() {
		return filter;
	}




	public void setFilter(String filter) {
		this.filter = filter;
	}




	public String getTargetToken() {
		return targetToken;
	}




	public void setTargetToken(String targetToken) {
		this.targetToken = targetToken;
	}




	public String getToCreateCatalogName() {
		return toCreateCatalogName;
	}




	public void setToCreateCatalogName(String toCreateCatalogName) {
		this.toCreateCatalogName = toCreateCatalogName;
	}




	public String getRemotePersistence() {
		return remotePersistence;
	}




	public void setRemotePersistence(String remotePersistence) {
		this.remotePersistence = remotePersistence;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ThSynchFolderConfiguration [remotePath=");
		builder.append(remotePath);
		builder.append(", filter=");
		builder.append(filter);
		builder.append(", targetToken=");
		builder.append(targetToken);
		builder.append(", toCreateCatalogName=");
		builder.append(toCreateCatalogName);
		builder.append(", remotePersistence=");
		builder.append(remotePersistence);
		builder.append(", rootFolderId=");
		builder.append(rootFolderId);
		builder.append("]");
		return builder.toString();
	}


}
