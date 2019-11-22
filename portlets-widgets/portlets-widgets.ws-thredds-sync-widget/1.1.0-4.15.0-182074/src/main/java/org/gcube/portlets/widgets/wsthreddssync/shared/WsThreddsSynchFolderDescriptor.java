package org.gcube.portlets.widgets.wsthreddssync.shared;

import java.io.Serializable;

import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;


// TODO: Auto-generated Javadoc
/**
 * The Class WsThreddsSynchFolderDescriptor.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2018
 */
public class WsThreddsSynchFolderDescriptor implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5395986188613871699L;

	private GcubeScope selectedScope;

	private ThSyncFolderDescriptor serverFolderDescriptor;

	private Sync_Status syncStatus;

	/**
	 * Instantiates a new ws thredds synch folder configuration.
	 */
	public WsThreddsSynchFolderDescriptor() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * Instantiates a new ws thredds synch folder descriptor.
	 *
	 * @param selectedScope the selected scope
	 * @param serverFolderDescriptor the server folder descriptor
	 * @param syncStatus the sync status
	 */
	public WsThreddsSynchFolderDescriptor(GcubeScope selectedScope, ThSyncFolderDescriptor serverFolderDescriptor,
			Sync_Status syncStatus) {
		super();
		this.selectedScope = selectedScope;
		this.serverFolderDescriptor = serverFolderDescriptor;
		this.syncStatus = syncStatus;
	}



	/**
	 * Gets the selected scope.
	 *
	 * @return the selectedScope
	 */
	public GcubeScope getSelectedScope() {

		return selectedScope;
	}

	/**
	 * Sets the sync status.
	 *
	 * @param syncStatus
	 *            the new sync status
	 */
	public void setSyncStatus(Sync_Status syncStatus) {
		this.syncStatus = syncStatus;
	}

	/**
	 * Gets the sync status.
	 *
	 * @return the sync status
	 */
	public Sync_Status getSyncStatus() {
		return syncStatus;
	}


	/**
	 * Sets the selected scope.
	 *
	 * @param selectedVRE the new selected scope
	 */
	public void setSelectedScope(GcubeScope selectedVRE) {
		this.selectedScope = selectedVRE;
	}

	/**
	 * Gets the server folder descriptor.
	 *
	 * @return the server folder descriptor
	 */
	public ThSyncFolderDescriptor getServerFolderDescriptor() {
		return serverFolderDescriptor;
	}

	/**
	 * Sets the server folder descriptor.
	 *
	 * @param serverFolderDescriptor
	 *            the new server folder descriptor
	 */
	public void setServerFolderDescriptor(ThSyncFolderDescriptor serverFolderDescriptor) {
		this.serverFolderDescriptor = serverFolderDescriptor;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WsThreddsSynchFolderDescriptor [selectedVRE=");
		builder.append(selectedScope);
		builder.append(", serverFolderDescriptor=");
		builder.append(serverFolderDescriptor);
		builder.append(", syncStatus=");
		builder.append(syncStatus);
		builder.append("]");
		return builder.toString();
	}


}
