package org.gcube.applicationsupportlayer.social.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SocialSharedFolder implements Serializable{
	private String id;
	private String name; 
	private String title; 
	private String displayName;  
	private String path;
	private String parentId;
	
	boolean vreFolder;
	
	public SocialSharedFolder()  {
	}
	
	

	public SocialSharedFolder(String id, String name, String title, String displayName, String path, String parentId,
			boolean vreFolder) {
		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.displayName = displayName;
		this.path = path;
		this.parentId = parentId;
		this.vreFolder = vreFolder;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public boolean isVreFolder() {
		return vreFolder;
	}

	public void setVreFolder(boolean vreFolder) {
		this.vreFolder = vreFolder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SocialSharedFolder [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", path=");
		builder.append(path);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", vreFolder=");
		builder.append(vreFolder);
		builder.append("]");
		return builder.toString();
	}

		
}
