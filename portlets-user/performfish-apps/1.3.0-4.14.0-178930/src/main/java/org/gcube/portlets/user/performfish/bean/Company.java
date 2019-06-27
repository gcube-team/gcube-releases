package org.gcube.portlets.user.performfish.bean;

public class Company {
	
	private long companyId;
	private String name;
	private long associatationId;
	private String imageUrl; 
	private boolean staged;
	
	public Company(long companyId, long associatationId, boolean staged) {
		super();
		this.companyId = companyId;
		this.associatationId = associatationId;
		this.staged = staged;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAssociatationId() {
		return associatationId;
	}

	public void setAssociatationId(long associatationId) {
		this.associatationId = associatationId;
	}

	public boolean isStaged() {
		return staged;
	}

	public void setStaged(boolean staged) {
		this.staged = staged;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Company [companyId=");
		builder.append(companyId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", associatationId=");
		builder.append(associatationId);
		builder.append(", imageUrl=");
		builder.append(imageUrl);
		builder.append(", staged=");
		builder.append(staged);
		builder.append("]");
		return builder.toString();
	}

	

}
