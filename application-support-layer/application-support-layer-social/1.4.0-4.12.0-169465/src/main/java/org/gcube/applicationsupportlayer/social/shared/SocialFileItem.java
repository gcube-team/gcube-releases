package org.gcube.applicationsupportlayer.social.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SocialFileItem implements Serializable{

	private String id;
	private String name; 
	private String title; 
	private String path; 
	private SocialSharedFolder parent;
	
	public SocialFileItem() {
	}

	
	public SocialFileItem(String id, String name, String title, String path, SocialSharedFolder parent) {
		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.path = path;
		this.parent = parent;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SocialSharedFolder getParent() {
		return parent;
	}

	public void setParent(SocialSharedFolder parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SocialFileItem [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", title=");
		builder.append(title);
		builder.append(", path=");
		builder.append(path);
		builder.append(", parent=");
		builder.append(parent);
		builder.append("]");
		return builder.toString();
	}



}
