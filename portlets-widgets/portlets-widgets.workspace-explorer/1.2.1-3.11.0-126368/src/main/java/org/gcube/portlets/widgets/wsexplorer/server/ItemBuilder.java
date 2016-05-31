/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.server;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ItemBuilder.
 *
 * @author Federico De Faveri defaveri@isti.cnr.it
 * Modified by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class ItemBuilder {

	public static final Logger _log = LoggerFactory.getLogger(ItemBuilder.class);
	/**
	 * Purge empty folders.
	 *
	 * @param item the item
	 * @return the item
	 */
	public static Item purgeEmptyFolders(Item item)
	{
		//for (Item child:item.getChildren()) purgeEmptyFolders(child); ONLY FIRST LEVEL

		List<Item> toRemoveList = new LinkedList<Item>();
		for (Item child:item.getChildren()) {
			boolean toRemove = isAnEmptyFolder(child);
			if (toRemove) {
				toRemoveList.add(child);
			}
		}
		for (Item child:toRemoveList) {
			item.removeChild(child);
		}

		return item;
	}


	/**
	 * Checks if is an empty folder.
	 *
	 * @param item the item
	 * @return true, if is an empty folder
	 */
	protected static boolean isAnEmptyFolder(Item item)
	{
		return Util.isFolder(item.getType()) && item.getChildren().size() == 0;
	}


	/**
	 * Gets the item.
	 *
	 * @param parent the parent
	 * @param workspaceItem the workspace item
	 * @param workspaceItemPath the workspace item path
	 * @param showableTypes the showable types
	 * @param filterCriteria the filter criteria
	 * @param loadChildren the load children
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 */
	public static Item getItem(Item parent, WorkspaceItem workspaceItem, String workspaceItemPath, List<ItemType> showableTypes, FilterCriteria filterCriteria, boolean loadChildren) throws InternalErrorException
	{

		ItemType type = getItemType(workspaceItem);

		if (!showableTypes.contains(type)) {
			return null;
		}
		if (!filterItem(type, workspaceItem, filterCriteria)) {
			return null;
		}

//		//TODO ADD CONTROL ON THE PATH WHEN WILL BE MORE FAST
//		if (itemName.equals(WorkspaceExplorerConstants.SPECIAL_FOLDERS_LABEL))
//			itemName = WorkspaceExplorerConstants.VRE_FOLDERS_LABEL;

		boolean isFolder = type.equals(ItemType.FOLDER)?true:false;
		boolean isSharedFolder = workspaceItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)?true:false;

		String itemName =  workspaceItem.getName();

		if(isSharedFolder){
			_log.debug("Is shared folder: "+workspaceItem.getName());
			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) workspaceItem;
			itemName = shared.isVreFolder()?shared.getDisplayName():workspaceItem.getName();
		}

//		_log.debug("Building Item for: "+itemName);
		Item item = null;
		try{
			//THIS CALL IS VERY SLOW!!
//			String storageID = null;
//			if(workspaceItem instanceof FolderItem){
//				storageID = workspaceItem.getStorageID();
//			}
			item = new Item(parent, workspaceItem.getId(), itemName, type, workspaceItemPath, UserUtil.getUserFullName(workspaceItem.getOwner().getPortalLogin()), toDate(workspaceItem.getCreationTime()), isFolder, false);
			item.setSharedFolder(isSharedFolder);
		}catch(Exception e){
			_log.error("Error on getting item: "+itemName+" with id: "+workspaceItem.getId()+", from HL, so skipping item");
			return null;
		}

		if(loadChildren){
			//TODO A PATCH TO AVOID A SLOW GETPATH
//			workspaceItemPath = workspaceItem.getPath();
			for (WorkspaceItem child: workspaceItem.getChildren()){
				String itemPath = workspaceItemPath+"/"+child.getName();
//				if(child.isFolder())
//				itemPath+="/"+child.getName();

//				_log.trace("\nConverting child item: "+child.getName());
				Item itemChild = getItem(item, child, itemPath, showableTypes, filterCriteria, false);
//				_log.trace("Item: "+child.getName() +" converted!!!");
				if (itemChild!=null){
					item.addChild(itemChild);
				}
			}
		}

		return item;
	}

	/**
	 * Gets the item type.
	 *
	 * @param item the item
	 * @return the item type
	 * @throws InternalErrorException the internal error exception
	 */
	protected static ItemType getItemType(WorkspaceItem item) throws InternalErrorException
	{
		switch(item.getType())
		{
			case SHARED_FOLDER:
			case FOLDER:{
//				if (item.isRoot()) return ItemType.ROOT;
				return ItemType.FOLDER;
			}
			case FOLDER_ITEM: return getFolderItemType((FolderItem) item);

			default:
				return null;
		}
	}

	/**
	 * Gets the folder item type.
	 *
	 * @param item the item
	 * @return the folder item type
	 */
	protected static ItemType getFolderItemType(FolderItem item){

		try{
			return ItemType.valueOf(item.getFolderItemType().toString());
		}catch (Exception e) {
			_log.error("Item Type non found: ",e);
			return ItemType.UNKNOWN_TYPE;
		}
	}


	/**
	 * Filter item.
	 *
	 * @param type the type
	 * @param item the item
	 * @param filterCriteria the filter criteria
	 * @return true, if successful
	 * @throws InternalErrorException the internal error exception
	 */
	protected static boolean filterItem(ItemType type, WorkspaceItem item, FilterCriteria filterCriteria) throws InternalErrorException {
		if(filterCriteria==null) {
			return true;
		}

		boolean mimeTypeCheck = checkAllowedMimeTypes(type, item, filterCriteria.getAllowedMimeTypes());
		if (!mimeTypeCheck) {
			return false;
		}

		boolean fileExtensionCheck = checkAllowedFileExtension(type, item, filterCriteria.getAllowedFileExtensions());
		if(!fileExtensionCheck) {
			return false;
		}

		boolean propertiesCheck = checkProperties(item, filterCriteria.getRequiredProperties());
		return propertiesCheck;
	}

	/**
	 * Check allowed mime types.
	 *
	 * @param type the type
	 * @param item the item
	 * @param allowedMimeTypes the allowed mime types
	 * @return true, if successful
	 */
	protected static boolean checkAllowedMimeTypes(ItemType type, WorkspaceItem item, List<String> allowedMimeTypes){
		if (allowedMimeTypes==null || allowedMimeTypes.size()==0) {
			return true;
		}

		if (type == ItemType.EXTERNAL_FILE || type == ItemType.EXTERNAL_IMAGE || type == ItemType.EXTERNAL_PDF_FILE) {
			ExternalFile externalFile = (ExternalFile)item;
			String mimeType = externalFile.getMimeType();
			return allowedMimeTypes.contains(mimeType);
		}
		return true;
	}




	/**
	 * Check allowed file extension.
	 *
	 * @param type the type
	 * @param item the item
	 * @param allowedFileExtension the allowed mime types
	 * @return true, if successful
	 */
	protected static boolean checkAllowedFileExtension(ItemType type, WorkspaceItem item, List<String> allowedFileExtension){
		if (allowedFileExtension==null || allowedFileExtension.size()==0) {
			return true;
		}

		try {
			if (type != ItemType.FOLDER) {
				String name = item.getName();
				return checkFileExtension(name, allowedFileExtension);
			}
			return true;
		} catch (InternalErrorException e) {
			_log.error("checkAllowedFileExtension, InternalErrorException: ",e);
			return false;
		}
	}

	/**
	 * Check file extension.
	 *
	 * @param fileName the file name
	 * @param allowedFileExtension the allowed file extension
	 * @return true, if successful
	 */
	protected static boolean checkFileExtension(String fileName, List<String> allowedFileExtension){

		if(fileName==null || fileName.isEmpty()) {
			return false;
		}

		int dot = fileName.lastIndexOf(".");
		if(dot>=0 && dot+1<=fileName.length()){

			String ext = fileName.substring(dot+1, fileName.length());
			_log.trace("Extension found: "+ext +" for: "+fileName);
//			if(ext.isEmpty())
//				return false;
			for (String fe : allowedFileExtension) {
				if(ext.compareTo(fe)==0) {
					return true;
				}
			}
			return false;
		}
		_log.trace("Extension not found for: "+fileName);
		return false;
	}


	/**
	 * Check properties.
	 *
	 * @param item the item
	 * @param requestedProperties the requested properties
	 * @return true, if successful
	 * @throws InternalErrorException the internal error exception
	 */
	protected static boolean checkProperties(WorkspaceItem item, Map<String, String> requestedProperties) throws InternalErrorException
	{
		if (requestedProperties==null || requestedProperties.size()==0 || item.getType()!=WorkspaceItemType.FOLDER_ITEM) {
			return true;
		}

		Map<String, String> itemProperties = item.getProperties().getProperties();
		for (Entry<String, String> requestProperty:requestedProperties.entrySet()) {
			String propertyValue = itemProperties.get(requestProperty.getKey());
			if (propertyValue == null) {
				return false;
			}
			if (!propertyValue.equals(requestProperty.getValue())) {
				return false;
			}
		}

		return true;
	}



	/**
	 * Builds the folder to breadcrumbs.
	 *
	 * @param wsFolder the ws folder
	 * @param parent the parent
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 */
	public static Item buildFolderForBreadcrumbs(WorkspaceFolder wsFolder, Item parent) throws InternalErrorException {

		String name = "";
		boolean isSpecialFolder = false;
		boolean isRoot = false;

		if(wsFolder.isRoot()){ //IS ROOT
			name = WorkspaceExplorerConstants.HOME_LABEL;
			isRoot = true;
		}else if(wsFolder.isShared() && wsFolder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){ 		//MANAGEMENT SHARED FOLDER NAME
	    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
	    	name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();
	    	//MANAGEMENT SPECIAL FOLDER
		}else if(isSpecialFolder(wsFolder)){
			name = WorkspaceExplorerConstants.VRE_FOLDERS_LABEL;
			isSpecialFolder = true;
		}
		else {
			name = wsFolder.getName();
		}

		//BUILDS A SIMPLE ITEM FOR BREADCRUMB
		String path = null; //wsFolder.getPath(); FORCED TO NULL BECAUSE IS SLOW CALL
		Item item = new Item(null, wsFolder.getId(), name, ItemType.FOLDER, path, null, null, true, isRoot);
		item.setSpecialFolder(isSpecialFolder);

		_log.debug("breadcrumb returning: "+item);
		return item;
	}

	/**
	 * Checks if is special folder.
	 *
	 * @param wsFolder the ws folder
	 * @return true, if is special folder
	 */
	public static boolean isSpecialFolder(WorkspaceFolder wsFolder){

		try {
			return wsFolder.getName().compareTo(WorkspaceExplorerConstants.SPECIAL_FOLDERS_NAME)==0 && wsFolder.getParent()!=null && wsFolder.getParent().isRoot();
		} catch (InternalErrorException e) {
			_log.warn("isSpecialFolder exception, returning false");
			return false;
		}
	}


	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	public static  Date toDate(Calendar calendar)
	{
		if (calendar == null) return null;
		return calendar.getTime();

	}
	/*
	public static void main(String[] args) {
		List<String> allowedFileExtension = new ArrayList<String>();
		allowedFileExtension.add("csv");
		allowedFileExtension.add("");

		String fileName = "t";
		System.out.println(checkFileExtension(fileName, allowedFileExtension));
	}*/
}
