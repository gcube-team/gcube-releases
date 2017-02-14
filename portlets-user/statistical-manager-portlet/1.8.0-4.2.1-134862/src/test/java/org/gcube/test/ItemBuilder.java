/**
 * 
 */
package org.gcube.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it Modified by Francesco
 *         Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class ItemBuilder {

	public static Item purgeEmptyFolders(Item item) {
		for (Item child : item.getChildren())
			purgeEmptyFolders(child);

		List<Item> toRemoveList = new LinkedList<Item>();
		for (Item child : item.getChildren()) {
			boolean toRemove = isAnEmptyFolder(child);
			if (toRemove)
				toRemoveList.add(child);
		}
		for (Item child : toRemoveList)
			item.removeChild(child);

		return item;
	}

	protected static boolean isAnEmptyFolder(Item item) {
		return Util.isFolder(item.getType()) && item.getChildren().size() == 0;
	}

	/**
	 * 
	 * @param parent
	 * @param workspaceItem
	 * @param showableTypes
	 * @param depth
	 * @return
	 * @throws InternalErrorException
	 */
	public static Item getItem(Item parent, WorkspaceItem workspaceItem,
			List<ItemType> showableTypes, FilterCriteria filterCriteria,
			int depth) throws InternalErrorException {
		return null;
	}

	protected static ItemType getItemType(WorkspaceItem item)
			throws InternalErrorException {
		switch (item.getType()) {
		case SHARED_FOLDER:
		case FOLDER:
			return ItemType.FOLDER;
		case FOLDER_ITEM:
			return getFolderItemType((FolderItem) item);
		case SMART_FOLDER:
			break;
		case TRASH_FOLDER:
			break;
		case TRASH_ITEM:
			break;
		default:
			break;
		}
		return null;
	}

	protected static ItemType getFolderItemType(FolderItem item) {
		// System.out.println("getFolderItemType "+item.getFolderItemType().toString());
		return ItemType.valueOf(item.getFolderItemType().toString());
	}

	protected static boolean filterItem(ItemType type, WorkspaceItem item,
			FilterCriteria filterCriteria) throws InternalErrorException {
		boolean mimeTypeCheck = checkAllowedMimeTypes(type, item,
				filterCriteria.getAllowedMimeTypes());
		if (!mimeTypeCheck)
			return false;

		boolean propertiesCheck = checkProperties(item,
				filterCriteria.getRequiredProperties());
		return propertiesCheck;
	}

	protected static boolean checkAllowedMimeTypes(ItemType type,
			WorkspaceItem item, List<String> allowedMimeTypes) {
		if (allowedMimeTypes.size() == 0)
			return true;

		if (type == ItemType.EXTERNAL_FILE || type == ItemType.EXTERNAL_IMAGE
				|| type == ItemType.EXTERNAL_PDF_FILE) {
			ExternalFile externalFile = (ExternalFile) item;
			String mimeType = externalFile.getMimeType();
			return allowedMimeTypes.contains(mimeType);
		}
		return true;
	}

	protected static boolean checkProperties(WorkspaceItem item,
			Map<String, String> requestedProperties)
			throws InternalErrorException {
		if (requestedProperties.size() == 0
				|| item.getType() != WorkspaceItemType.FOLDER_ITEM)
			return true;

		Map<String, String> itemProperties = item.getProperties()
				.getProperties();
		for (Entry<String, String> requestProperty : requestedProperties
				.entrySet()) {
			String propertyValue = itemProperties.get(requestProperty.getKey());
			if (propertyValue == null)
				return false;
			if (!propertyValue.equals(requestProperty.getValue()))
				return false;
		}

		return true;
	}
}
