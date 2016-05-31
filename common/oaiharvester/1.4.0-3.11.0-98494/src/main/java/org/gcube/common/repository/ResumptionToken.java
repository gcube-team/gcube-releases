package org.gcube.common.repository;


public class ResumptionToken {
	private String expirationDate;
	private String completeListSize;
	private String cursor;
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @param completeListSize the completeListSize to set
	 */
	public void setCompleteListSize(String completeListSize) {
		this.completeListSize = completeListSize;
	}
	/**
	 * @param cursor the cursor to set
	 */
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @return the completeListSize
	 */
	public String getCompleteListSize() {
		return completeListSize;
	}
	/**
	 * @return the cursor
	 */
	public String getCursor() {
		return cursor;
	}
	
}
