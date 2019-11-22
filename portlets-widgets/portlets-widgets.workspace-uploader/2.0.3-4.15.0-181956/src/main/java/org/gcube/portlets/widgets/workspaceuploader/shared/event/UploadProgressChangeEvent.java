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
	private int readPercentage = -1;
	private long readTime = 0L;
	private long readBytes = 0L;
	private long totalBytes = 0L;

	/**
	 * Instantiates a new upload progress change event.
	 */
	public UploadProgressChangeEvent() {
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#getReadPercentage()
	 */
	public int getReadPercentage() {
		return readPercentage;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent#setReadPercentage(java.lang.Integer)
	 */
	public void setReadPercentage(int percentage) {
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


	/**
	 * Sets the read bytes.
	 *
	 * @param readBytes the readBytes to set
	 */
	public void setReadBytes(long readBytes) {

		this.readBytes = readBytes;
	}


	/**
	 * Sets the total bytes.
	 *
	 * @param totalBytes the totalBytes to set
	 */
	public void setTotalBytes(long totalBytes) {

		this.totalBytes = totalBytes;
	}


	/**
	 * Gets the read bytes.
	 *
	 * @return the readBytes
	 */
	public long getReadBytes() {

		return readBytes;
	}


	/**
	 * Gets the total bytes.
	 *
	 * @return the totalBytes
	 */
	public long getTotalBytes() {

		return totalBytes;
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
		builder.append(", readBytes=");
		builder.append(readBytes);
		builder.append(", totalBytes=");
		builder.append(totalBytes);
		builder.append("]");
		return builder.toString();
	}


}
