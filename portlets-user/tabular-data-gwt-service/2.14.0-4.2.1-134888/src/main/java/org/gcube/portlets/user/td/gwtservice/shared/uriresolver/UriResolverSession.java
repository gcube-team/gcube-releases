package org.gcube.portlets.user.td.gwtservice.shared.uriresolver;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class UriResolverSession implements Serializable {

	private static final long serialVersionUID = -8088974004393312527L;

	private String uuid;
	private ApplicationType applicationType;
	private String fileName;
	private String mimeType;

	public UriResolverSession() {
		super();
	}

	public UriResolverSession(String uuid, ApplicationType applicationType) {
		super();
		this.uuid = uuid;
		this.applicationType = applicationType;
		this.fileName = null;
		this.mimeType = null;
	}

	public UriResolverSession(String uuid, ApplicationType applicationType,
			String fileName, String mimeType) {
		super();
		this.uuid = uuid;
		this.applicationType = applicationType;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "UriResolverSession [uuid=" + uuid + ", applicationType="
				+ applicationType + ", fileName=" + fileName + ", mimeType="
				+ mimeType + "]";
	}

	
	
}
