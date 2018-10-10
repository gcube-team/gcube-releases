package org.gcube.common.homelibary.model.items;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

import lombok.Data;

@Data
public class SearchItemDelegate {

	//properties
	String id;

	String name;

	String parentId;

	Calendar lastModificationTime;

	Calendar creationTime;

	String path;

	String owner;

	WorkspaceItemType type;

	boolean isVreFolder;

	boolean isShared;

//	FolderItemType folderItemType;

	String mimeType;

	long size;
	
	String primaryType;

}
