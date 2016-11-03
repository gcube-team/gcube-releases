package org.gcube.portlets.widgets.workspaceuploader.shared.event;

/**
 * The Interface UploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 1, 2015
 */
public interface UploadEvent {

	/**
	 * Gets the read percentage.
	 *
	 * @return the read percentage
	 */
	Integer getReadPercentage();

	/**
	 * Sets the read percentage.
	 *
	 * @param percentage the new read percentage
	 */
	void setReadPercentage(Integer percentage);
	
	
	/**
	 * Sets the read time. (in millisecond)
	 * 
	 * the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 *
	 * @param time the new read time
	 */
	void setReadTime(long time);
	
	/**
	 * Gets the read time (in millisecond)
	 *
	 * @return the read time
	 */
	long getReadTime();
}
