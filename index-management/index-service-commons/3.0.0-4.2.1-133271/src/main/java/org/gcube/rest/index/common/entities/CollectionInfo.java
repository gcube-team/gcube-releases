package org.gcube.rest.index.common.entities;

import java.util.Date;

public class CollectionInfo {

	private String id;
	private String title;
	private String description;
	private Date date;
	private String type;
	
	
	public CollectionInfo(String id, String title, String description, Date date, String type){
		this.id = id;
		this.title = title;
		this.description = description;
		this.date = date;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString(){
		return "[id: "+id+"\tTitle: "+title+"\tDescription: "+description+"\tType: "+type+"]";
	}
	
}
