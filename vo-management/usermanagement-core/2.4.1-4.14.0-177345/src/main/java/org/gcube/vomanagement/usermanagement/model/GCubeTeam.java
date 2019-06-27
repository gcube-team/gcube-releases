package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class GCubeTeam implements Serializable {
	private long groupId;
	private long teamId;
	private	String teamName;	
	private	String description;
	private long userId;
	private Date createdate;
	private Date modifiedDate;
	
		
	public GCubeTeam(){

	}


	public GCubeTeam(long groupId, long teamId, String teamName,
			String description, long userId, Date createdate, Date modifiedDate) {
		super();
		this.groupId = groupId;
		this.teamId = teamId;
		this.teamName = teamName;
		this.description = description;
		this.userId = userId;
		this.createdate = createdate;
		this.modifiedDate = modifiedDate;
	}


	public long getGroupId() {
		return groupId;
	}


	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}


	public long getTeamId() {
		return teamId;
	}


	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}


	public String getTeamName() {
		return teamName;
	}


	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}


	public Date getCreatedate() {
		return createdate;
	}


	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}


	public Date getModifiedDate() {
		return modifiedDate;
	}


	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
