package org.gcube.portlets.user.performfish.bean;

import java.util.Date;

public class Association {
	
	private long associationId;
	private String shortName;
	private String fullName;
	private String imageUrl; 
	private Date dateCreated;
	private String createdBy;
	
	public Association(long associationId, String createdBy) {
		super();
		this.associationId = associationId;
		this.createdBy = createdBy;
	}

	public Association() {
		super();
	}


	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getAssociationId() {
		return associationId;
	}

	public void setAssociationId(long associationId) {
		this.associationId = associationId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Association [associationId=");
		builder.append(associationId);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", imageUrl=");
		builder.append(imageUrl);
		builder.append(", dateCreated=");
		builder.append(dateCreated);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append("]");
		return builder.toString();
	}


		
	
	

}
