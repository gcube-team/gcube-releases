package org.gcube.portlets.user.performfish.bean;

import java.io.Serializable;

import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.user.performfish.util.Utils;

@SuppressWarnings("serial")
public class SubmittedForm implements Serializable {
	public static String NOT_YET_PUBLISHER = "No one";
	private static String UNPUBLISHED_LABEL = "Unpublished";
	private static String SUBMITTER_NONE = null;
		
	private ExternalFile file;
	private String status;
	private String submitterIdentity;
	private long endTimeinMillis = -1;
	
	public SubmittedForm() {
		this.status = UNPUBLISHED_LABEL;
		this.submitterIdentity = SUBMITTER_NONE;
	}
	
	public SubmittedForm(ExternalFile file, String status, String submitterIdentity, long endTimeinMillis) {
		super();
		this.file = file;
		this.status = status;
		this.submitterIdentity = submitterIdentity;
		this.endTimeinMillis = endTimeinMillis;
	}

	public SubmittedForm(ExternalFile file) {
		super();
		this.file = file;
		this.status = UNPUBLISHED_LABEL;
		this.submitterIdentity = SUBMITTER_NONE;
		this.endTimeinMillis = -1;
	}
	
	public String getBatchType() {
		String batchType = "unknown";
		try {
			batchType = Utils.getBatchTypeName(Utils.getPhaseByFileName(this.file.getName()), this.file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return batchType;
	}
	
	public ExternalFile getFormFile() {
		return file;
	}

	public void setFormFile(ExternalFile file) {
		this.file = file;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmitterIdentity() {
		return submitterIdentity;
	}

	public void setSubmitterIdentity(String submitterIdentity) {
		this.submitterIdentity = submitterIdentity;
	}

	public long getEndTimeinMillis() {
		return endTimeinMillis;
	}

	public void setEndTimeinMillis(long endTimeinMillis) {
		this.endTimeinMillis = endTimeinMillis;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmittedForm [file=");
		builder.append(file);
		builder.append(", status=");
		builder.append(status);
		builder.append(", submitterIdentity=");
		builder.append(submitterIdentity);
		builder.append(", endTimeinMillis=");
		builder.append(endTimeinMillis);
		builder.append("]");
		return builder.toString();
	}

	
}
