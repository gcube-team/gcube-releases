package org.gcube.portlets.user.dataminermanager.server.dmservice.wps;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ResponseWPS implements Serializable {
	private static final long serialVersionUID = 3449817768583395068L;
	private String data;
	private String description;
	private String mimeType;

	public ResponseWPS() {
		super();
	}

	public ResponseWPS(String data, String description, String mimeType) {
		super();
		this.data = data;
		this.description = description;
		this.mimeType = mimeType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "ResponseWPS [data=" + data + ", description=" + description
				+ ", mimeType=" + mimeType + "]";
	}

}
