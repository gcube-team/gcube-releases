/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 29, 2016
 */
public class AllowAccess implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2180345568585856822L;

	private String itemId;
	private Boolean accessGranted = false;
	private String accessAllowDenyMotivation;
	private String error;

	/**
	 *
	 */
	public AllowAccess() {
	}

	public AllowAccess(String itemId){

	}

	/**
	 * @param itemId
	 * @param accessGranted
	 * @param accessAllowDenyMotivation
	 * @param error
	 */
	public AllowAccess(
		String itemId, Boolean accessGranted, String accessAllowDenyMotivation,
		String error) {

		super();
		this.itemId = itemId;
		this.accessGranted = accessGranted;
		this.accessAllowDenyMotivation = accessAllowDenyMotivation;
		this.error = error;
	}


	/**
	 * @return the itemId
	 */
	public String getItemId() {

		return itemId;
	}


	/**
	 * @return the accessGranted
	 */
	public Boolean getAccessGranted() {

		return accessGranted;
	}


	/**
	 * @return the accessAllowDenyMotivation
	 */
	public String getAccessAllowDenyMotivation() {

		return accessAllowDenyMotivation;
	}


	/**
	 * @return the error
	 */
	public String getError() {

		return error;
	}


	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {

		this.itemId = itemId;
	}


	/**
	 * @param accessGranted the accessGranted to set
	 */
	public void setAccessGranted(Boolean accessGranted) {

		this.accessGranted = accessGranted;
	}


	/**
	 * @param accessAllowDenyMotivation the accessAllowDenyMotivation to set
	 */
	public void setAccessAllowDenyMotivation(String accessAllowDenyMotivation) {

		this.accessAllowDenyMotivation = accessAllowDenyMotivation;
	}


	/**
	 * @param error the error to set
	 */
	public void setError(String error) {

		this.error = error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("AllowAccess [itemId=");
		builder.append(itemId);
		builder.append(", accessGranted=");
		builder.append(accessGranted);
		builder.append(", accessAllowDenyMotivation=");
		builder.append(accessAllowDenyMotivation);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}




}
