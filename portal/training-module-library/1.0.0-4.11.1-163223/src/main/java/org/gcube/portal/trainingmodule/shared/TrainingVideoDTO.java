package org.gcube.portal.trainingmodule.shared;

import java.io.Serializable;


public class TrainingVideoDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1067266025273228801L;

	private long internalId;
	
	private String title;
	
	private String description;
	
	private String url;
	
	
	public TrainingVideoDTO() {
		// TODO Auto-generated constructor stub
	}


	public TrainingVideoDTO(long internalId, String title, String description, String url) {
		super();
		this.internalId = internalId;
		this.title = title;
		this.description = description;
		this.url = url;
	}


	public long getInternalId() {
		return internalId;
	}


	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingVideoDTO [internalId=");
		builder.append(internalId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
