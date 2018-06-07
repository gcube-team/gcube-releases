package org.gcube.portlets.widgets.wsthreddssync.shared;

import java.io.Serializable;

import org.gcube.portal.wssynclibrary.shared.thredds.Status;


// TODO: Auto-generated Javadoc
/**
 * The Class WsThreddsSynchFolderConfiguration.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2018
 */
public class WsThreddsSynchFolderConfiguration implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = -1140248888970305126L;

	/** The remote path. */
	private String remotePath;

	private GcubeScope selectedScope;

	/** The filter. */
	private String filter;

	/** The to create catalog name. */
	private String catalogName;

	private Status status; //just for serialization

	private String rootFolderId;


	/**
	 * Instantiates a new ws thredds synch folder configuration.
	 */
	public WsThreddsSynchFolderConfiguration() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * Instantiates a new ws thredds synch folder configuration.
	 *
	 * @param remotePath the remote path
	 * @param theVRE the the vre
	 * @param filter the filter
	 * @param catalogName the catalog name
	 */
	public WsThreddsSynchFolderConfiguration(String remotePath, GcubeScope theVRE, String filter, String catalogName, String rootFolderId) {
		super();
		this.remotePath = remotePath;
		this.selectedScope = theVRE;
		this.filter = filter;
		this.catalogName = catalogName;
		this.rootFolderId = rootFolderId;
	}


	/**
	 * @param rootFolderId the rootFolderId to set
	 */
	public void setRootFolderId(String rootFolderId) {

		this.rootFolderId = rootFolderId;
	}


	/**
	 * @return the rootFolderId
	 */
	public String getRootFolderId() {

		return rootFolderId;
	}


	/**
	 * Gets the remote path.
	 *
	 * @return the remote path
	 */
	public String getRemotePath() {
		return remotePath;
	}

	/**
	 * Sets the remote path.
	 *
	 * @param remotePath the new remote path
	 */
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}


	/**
	 * @return the selectedScope
	 */
	public GcubeScope getSelectedScope() {

		return selectedScope;
	}


	/**
	 * @return the status
	 */
	public Status getStatus() {

		return status;
	}



	/**
	 * @param selectedScope the selectedScope to set
	 */
	public void setSelectedScope(GcubeScope selectedScope) {

		this.selectedScope = selectedScope;
	}



	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {

		this.status = status;
	}


	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Sets the filter.
	 *
	 * @param filter the new filter
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Gets the catalog name.
	 *
	 * @return the catalog name
	 */
	public String getCatalogName() {
		return catalogName;
	}

	/**
	 * Sets the catalog name.
	 *
	 * @param catalogName the new catalog name
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WsThreddsSynchFolderConfiguration [remotePath=");
		builder.append(remotePath);
		builder.append(", selectedScope=");
		builder.append(selectedScope);
		builder.append(", filter=");
		builder.append(filter);
		builder.append(", catalogName=");
		builder.append(catalogName);
		builder.append(", status=");
		builder.append(status);
		builder.append(", rootFolderId=");
		builder.append(rootFolderId);
		builder.append("]");
		return builder.toString();
	}


}
