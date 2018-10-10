package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Enum Sync_Status.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2018
 */
public enum Sync_Status implements Serializable {
	
	UP_TO_DATE("No need to sync"),
	OUTDATED_WS("Workspace version is obsolete, need to sync"),
	OUTDATED_REMOTE("Remote version is obsolete, need to sync");
	
	String decription;
	
	/**
	 * Instantiates a new sync status.
	 */
	private Sync_Status() {
	}
	
	/**
	 * Instantiates a new sync status.
	 *
	 * @param description the description
	 */
	Sync_Status(String description){
		this.decription = description;
	}
	
	/**
	 * Gets the decription.
	 *
	 * @return the decription
	 */
	public String getDecription() {
		return decription;
	}

}
