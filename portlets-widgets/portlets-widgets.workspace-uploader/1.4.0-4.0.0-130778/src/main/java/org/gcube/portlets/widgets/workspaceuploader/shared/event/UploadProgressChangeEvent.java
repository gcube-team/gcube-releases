package org.gcube.portlets.widgets.workspaceuploader.shared.event;

import java.io.Serializable;


/**
 * The Class UploadProgressChangeEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 1, 2015
 */
public final class UploadProgressChangeEvent implements UploadEvent, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3445585716145899197L;
	private Integer readPercentage = -1;
	private long readTime = 0;

	/**
	 * Instantiates a new upload progress change event.
	 */
	public UploadProgressChangeEvent() {
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#getReadPercentage()
	 */
	public Integer getReadPercentage() {
		return readPercentage;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#setReadPercentage(java.lang.Integer)
	 */
	public void setReadPercentage(Integer percentage) {
		this.readPercentage = percentage;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#setReadTime(long)
	 */
	@Override
	public void setReadTime(long time) {
		this.readTime = time;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#getReadTime()
	 */
	@Override
	public long getReadTime() {
		return readTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UploadProgressChangeEvent [readPercentage=");
		builder.append(readPercentage);
		builder.append(", readTime=");
		builder.append(readTime);
		builder.append("]");
		return builder.toString();
	}
}
