package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;



// TODO: Auto-generated Javadoc

/**
 * The Class ThSyncFolderDescriptor.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2018
 */
public class ThSyncFolderDescriptor implements Serializable{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4134777763175272691L;

	/** The folder id. */
	private String folderId;

	/** The folder path. */
	private String folderPath;

	/** The configuration. */
	private ThSynchFolderConfiguration configuration;

	/** The is locked. */
	private boolean isLocked=false;
	
	
	/** The local process descriptor. */
	private ThProcessDescriptor localProcessDescriptor=null; 
	
	/**
	 * Instantiates a new s sync folder descriptor.
	 */
	public ThSyncFolderDescriptor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new s sync folder descriptor.
	 *
	 * @param folderId the folder id
	 * @param folderPath the folder path
	 * @param configuration the configuration
	 * @param isLocked the is locked
	 * @param localProcessDescriptor the local process descriptor
	 */
	public ThSyncFolderDescriptor(String folderId, String folderPath, ThSynchFolderConfiguration configuration,
			boolean isLocked, ThProcessDescriptor localProcessDescriptor) {
		super();
		this.folderId = folderId;
		this.folderPath = folderPath;
		this.configuration = configuration;
		this.isLocked = isLocked;
		this.localProcessDescriptor = localProcessDescriptor;
	}

	/**
	 * Gets the folder id.
	 *
	 * @return the folder id
	 */
	public String getFolderId() {
		return folderId;
	}

	/**
	 * Sets the folder id.
	 *
	 * @param folderId the new folder id
	 */
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	/**
	 * Gets the folder path.
	 *
	 * @return the folder path
	 */
	public String getFolderPath() {
		return folderPath;
	}

	/**
	 * Sets the folder path.
	 *
	 * @param folderPath the new folder path
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	public ThSynchFolderConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the configuration.
	 *
	 * @param configuration the new configuration
	 */
	public void setConfiguration(ThSynchFolderConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Sets the locked.
	 *
	 * @param isLocked the new locked
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * Gets the local process descriptor.
	 *
	 * @return the local process descriptor
	 */
	public ThProcessDescriptor getLocalProcessDescriptor() {
		return localProcessDescriptor;
	}

	/**
	 * Sets the local process descriptor.
	 *
	 * @param localProcessDescriptor the new local process descriptor
	 */
	public void setLocalProcessDescriptor(ThProcessDescriptor localProcessDescriptor) {
		this.localProcessDescriptor = localProcessDescriptor;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThSyncFolderDescriptor [folderId=");
		builder.append(folderId);
		builder.append(", folderPath=");
		builder.append(folderPath);
		builder.append(", configuration=");
		builder.append(configuration);
		builder.append(", isLocked=");
		builder.append(isLocked);
		builder.append(", localProcessDescriptor=");
		builder.append(localProcessDescriptor);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
