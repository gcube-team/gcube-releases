package org.gcube.portlets.user.trainingcourse.shared;

import java.io.Serializable;
import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;

public class WorkspaceItemInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static enum Type{FOLDER,FILE}
	
	private String id;
	private String name;
	private String decription;
	private String mimeType;
	private Type itemType;
	private List<String> sharedWith;
	private String parentId;
	
	private String publicLink;
	private boolean isFolder;
	
	private TrainingUnitDTO unit;

	public WorkspaceItemInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public WorkspaceItemInfo(String id, String name, String decription, String mimeType, Type itemType, List<String> sharedWith, String publicLink, String parentId, boolean isFolder) {
		super();
		this.id = id;
		this.name = name;
		this.decription = decription;
		this.mimeType = mimeType;
		this.itemType = itemType;
		this.sharedWith = sharedWith;
		this.publicLink = publicLink;
		this.parentId = parentId;
		this.isFolder = isFolder;
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

	public String getDecription() {
		return decription;
	}

	public void setDecription(String decription) {
		this.decription = decription;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Type getItemType() {
		return itemType;
	}

	public void setItemType(Type itemType) {
		this.itemType = itemType;
	}

	public List<String> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(List<String> sharedWith) {
		this.sharedWith = sharedWith;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPublicLink() {
		return publicLink;
	}

	public void setPublicLink(String publicLink) {
		this.publicLink = publicLink;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	
	public void setUnit(TrainingUnitDTO unit) {
		this.unit = unit;
	}

	public TrainingUnitDTO getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkspaceItemInfo [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", decription=");
		builder.append(decription);
		builder.append(", mimeType=");
		builder.append(mimeType);
		builder.append(", itemType=");
		builder.append(itemType);
		builder.append(", sharedWith=");
		builder.append(sharedWith);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", publicLink=");
		builder.append(publicLink);
		builder.append(", isFolder=");
		builder.append(isFolder);
		builder.append("]");
		return builder.toString();
	}

	

	

}
