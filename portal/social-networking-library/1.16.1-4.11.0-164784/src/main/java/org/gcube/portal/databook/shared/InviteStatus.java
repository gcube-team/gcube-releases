package org.gcube.portal.databook.shared;

public enum InviteStatus {
	/**
	 * First status of anyh invite
	 */
	PENDING,
	/**
	 * User accepted the invite
	 */
	ACCEPTED,
	/**
	 * User rejected the invite
	 */
	REJECTED, 
	/**
	 * Manager withdrawed the invite 
	 */
	RETRACTED;
}
