package org.gcube.portlets.user.takecourse.dto;

import java.util.List;

public class StudentProgressDTO {
	private long userId;
	private String username;
	private String fullName;
	private List<UnitProgress> units;
	public StudentProgressDTO(long userId, String username, String fullName, List<UnitProgress> units) {
		super();
		this.userId = userId;
		this.username = username;
		this.fullName = fullName;
		this.units = units;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<UnitProgress> getUnits() {
		return units;
	}
	public void setUnits(List<UnitProgress> units) {
		this.units = units;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudentProgressDTO [username=");
		builder.append(username);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", units=");
		builder.append(units);
		builder.append("]");
		return builder.toString();
	}
	
}
