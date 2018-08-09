package org.gcube.portlets.user.takecourse.dto;

public class UnitProgress {
	private long id;
	private String name;
	private int progressPercentage;
	public UnitProgress(long id, String name, int progressPercentage) {
		super();
		this.id = id;
		this.name = name;
		this.progressPercentage = progressPercentage;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProgressPercentage() {
		return progressPercentage;
	}
	public void setProgressPercentage(int progressPercentage) {
		this.progressPercentage = progressPercentage;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnitProgress [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", progressPercentage=");
		builder.append(progressPercentage);
		builder.append("]");
		return builder.toString();
	}
	
	
}
