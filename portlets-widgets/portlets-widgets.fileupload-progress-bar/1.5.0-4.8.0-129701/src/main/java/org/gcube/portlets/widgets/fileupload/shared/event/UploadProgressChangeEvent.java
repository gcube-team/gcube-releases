package org.gcube.portlets.widgets.fileupload.shared.event;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class UploadProgressChangeEvent implements Event, Serializable {
	/**
	 * the file name	
	 */
	private String filename;
	/**
	 * the path of the uploaded file
	 */
	private String absolutePath;
	/**
	 * the current percentage
	 */
	private Integer percentage;

	public UploadProgressChangeEvent() {
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(final Integer percentage) {
		this.percentage = percentage;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public String toString() {
		return filename + " - " + percentage + " path="+absolutePath;
	}
}
