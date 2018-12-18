package org.gcube.portlets.user.performfish.bean;

import java.util.Date;
import java.util.List;

import org.gcube.vomanagement.usermanagement.model.GCubeUser;

public class Farm {
	
	private long farmId;
	private String name;
	private long companyId;
	private String creatorFullname; 
	private String imageUrl; 
	private String location; 
	private Date dateCreated;
	private Date dateLastActivity;
	private List<GCubeUser> administrators;
	
	public Farm(long farmId, String location, long companyId, String creatorFullname) {
		super();
		this.farmId = farmId;
		this.location = location;
		this.companyId = companyId;
		this.creatorFullname = creatorFullname;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getCreatorFullname() {
		return creatorFullname;
	}

	public void setCreatorFullname(String creatorFullname) {
		this.creatorFullname = creatorFullname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getFarmId() {
		return farmId;
	}

	public void setFarmId(long farmId) {
		this.farmId = farmId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public List<GCubeUser> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(List<GCubeUser> administrators) {
		this.administrators = administrators;
	}

	public Date getDateLastActivity() {
		return dateLastActivity;
	}

	public void setDateLastActivity(Date dateLastActivity) {
		this.dateLastActivity = dateLastActivity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Farm [farmId=");
		builder.append(farmId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", companyId=");
		builder.append(companyId);
		builder.append(", creatorFullname=");
		builder.append(creatorFullname);
		builder.append(", imageUrl=");
		builder.append(imageUrl);
		builder.append(", location=");
		builder.append(location);
		builder.append(", dateCreated=");
		builder.append(dateCreated);
		builder.append(", dateLastActivity=");
		builder.append(dateLastActivity);
		builder.append(", administrators=");
		builder.append(administrators);
		builder.append("]");
		return builder.toString();
	}

	
}
