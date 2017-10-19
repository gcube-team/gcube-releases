/**
 * 
 */
package org.gcube.common.homelibrary.util;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceTypes {
	
	/**
	 * @param item the item to convert.
	 * @return the folder item.
	 * @throws WrongItemTypeException if the item is not a folder item.
	 */
	public static FolderItem getFolderItem(WorkspaceItem item) throws WrongItemTypeException{
		if (item.getType()!=WorkspaceItemType.FOLDER_ITEM) throw new WrongItemTypeException("The item type is "+item.getType()+" expected "+WorkspaceItemType.FOLDER_ITEM);
		return (FolderItem) item;
	}
	
	/**
	 * @param item the item where to get the type.
	 * @return the resulting type string.
	 */
	public static String getItemTypeAsString(WorkspaceItem item)
	{
		WorkspaceItemType type = item.getType();
		if (type!=WorkspaceItemType.FOLDER_ITEM) return type.toString();
		
		FolderItem folderItem = (FolderItem) item;
		FolderItemType folderItemType = folderItem.getFolderItemType();
		
		return folderItemType.toString();
			
	}

}
