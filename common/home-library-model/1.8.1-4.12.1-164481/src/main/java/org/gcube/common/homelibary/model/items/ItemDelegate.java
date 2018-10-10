package org.gcube.common.homelibary.model.items;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;

@Data
public class ItemDelegate {

	String id;

	String name;

	String title;

	String description;

	String lastModifiedBy;

	String parentId;

	String parentPath;

	Calendar lastModificationTime;

	Calendar creationTime;

	String path;

	String owner;

	String primaryType;

	WorkspaceItemAction lastAction;

	boolean trashed;
	
	boolean shared;

	boolean locked;
	
	boolean hidden;

	List <AccountingDelegate> accounting;

	Map<String,String> metadata;

	Map<NodeProperty, String> content;
	
	Map<NodeProperty, String> properties;

}
