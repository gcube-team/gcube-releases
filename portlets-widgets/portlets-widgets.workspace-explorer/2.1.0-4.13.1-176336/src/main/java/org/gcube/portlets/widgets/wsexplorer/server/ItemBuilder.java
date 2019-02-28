/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.server;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.ExternalURL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.server.stohub.StorageHubServiceUtil;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ItemBuilder.
 *
 * @author Francesco Mangiacrapa, CNR-ISTI
 * @author M. Assante, CNR-ISTI
 */
public class ItemBuilder {

	public static final Logger _log = LoggerFactory.getLogger(ItemBuilder.class);
	/**
	 * Gets the item.
	 *
	 * @param parent the parent
	 * @param workspaceItem the workspace item
	 * @param workspaceItemPath the workspace item path
	 * @param showableTypes the showable types
	 * @param filterCriteria the filter criteria
	 * @param loadChildren the load children
	 * @param loadGcubeProperties the load gcube properties
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 */
	public static org.gcube.portlets.widgets.wsexplorer.shared.Item getItem(org.gcube.portlets.widgets.wsexplorer.shared.Item parent, Item workspaceItem, String workspaceItemPath,
			List<ItemType> showableTypes, FilterCriteria filterCriteria,
			boolean loadChildren, boolean loadGcubeProperties) {

		ItemType type = getItemType(workspaceItem);

		if (!showableTypes.contains(type)) {
			return null;
		}
		if (!filterItem(type, workspaceItem, filterCriteria)) {
			return null;
		}

		boolean isFolder = type.equals(ItemType.PRIVATE_FOLDER)?true:false;
		boolean isSharedFolder = type.equals(ItemType.SHARED_FOLDER) || type.equals(ItemType.VRE_FOLDER ) ? true : false;
		if (isSharedFolder)
			isFolder = true;

		String itemName =  workspaceItem.getName();

		if(isSharedFolder){
			_log.info("Is shared folder: "+workspaceItem.getTitle());
			SharedFolder shared = (SharedFolder) workspaceItem;
			itemName = shared.isVreFolder() ? shared.getDisplayName() : workspaceItem.getTitle();
		}

		org.gcube.portlets.widgets.wsexplorer.shared.Item item = null;
		try{
			item = new org.gcube.portlets.widgets.wsexplorer.shared.Item(parent, workspaceItem.getId(), itemName, type, workspaceItemPath, UserUtil.getUserFullName(workspaceItem.getOwner()), toDate(workspaceItem.getCreationTime()), isFolder, false);
			item.setSharedFolder(isSharedFolder);

			if(loadGcubeProperties){
				Map<String, String> itemProperties = getGcubePropertiesForItem(workspaceItem);
				item.setGcubeProperties(itemProperties);
			}
		}catch(Exception e){
			_log.error("Error on getting item: "+itemName+" with id: "+workspaceItem.getId()+", from HL, so skipping item");
			return null;
		}

		if(loadChildren){
			String itemId = item.getId();
			ItemManagerClient client = AbstractPlugin.item().build();
			List<? extends Item> theChildren = null;
			theChildren = client.getChildren(itemId, StorageHubServiceUtil.ACCOUNTING_HL_NODE_NAME);
			for (Item child : theChildren) {
				String itemPath = workspaceItemPath+"/"+child.getName();
				org.gcube.portlets.widgets.wsexplorer.shared.Item itemChild = getItem(item, child, itemPath, showableTypes, filterCriteria, false, loadGcubeProperties);
				_log.trace("Item: "+child.getName() +" converted!!!");
				if (itemChild!=null){
					item.addChild(itemChild);
				}
			}
		}

		return item;
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
	 * @param loadGcubeProperties the load gcube properties
	 * @param startIdx the start idx
	 * @param limit the limit
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 */
	public static org.gcube.portlets.widgets.wsexplorer.shared.Item getItem(org.gcube.portlets.widgets.wsexplorer.shared.Item parent, Item workspaceItem, String workspaceItemPath,
			List<ItemType> showableTypes, FilterCriteria filterCriteria,
			boolean loadChildren, boolean loadGcubeProperties, int startIdx, int limit)  {

		ItemType type = getItemType(workspaceItem);

		if (!showableTypes.contains(type)) {
			return null;
		}
		if (!filterItem(type, workspaceItem, filterCriteria)) {
			return null;
		}
		boolean isFolder = type.equals(ItemType.PRIVATE_FOLDER)?true:false;
		boolean isSharedFolder = type.equals(ItemType.SHARED_FOLDER) || type.equals(ItemType.VRE_FOLDER ) ? true : false;

		String itemName =  workspaceItem.getName();

		if(isSharedFolder){
			_log.info("Is shared folder: "+workspaceItem.getTitle());
			SharedFolder shared = (SharedFolder) workspaceItem;
			itemName = shared.isVreFolder()?shared.getDisplayName():workspaceItem.getTitle();
			isFolder = true;
		}

		//		_log.debug("Building Item for: "+itemName);
		org.gcube.portlets.widgets.wsexplorer.shared.Item item = null;
		try{
			item = new org.gcube.portlets.widgets.wsexplorer.shared.Item(
					parent, workspaceItem.getId(), itemName, type, workspaceItemPath,
					UserUtil.getUserFullName(workspaceItem.getOwner()), toDate(workspaceItem.getCreationTime()), isFolder, false);
			item.setSharedFolder(isSharedFolder);

			if(loadGcubeProperties){
				Map<String, String> itemProperties = getGcubePropertiesForItem(workspaceItem);
				item.setGcubeProperties(itemProperties);
			}
		}catch(Exception e){
			_log.error("Error on getting item: "+itemName+" with id: "+workspaceItem.getId()+", from HL, so skipping item");
			return null;
		}

		if(loadChildren){
			String itemId = item.getId();
			ItemManagerClient client = AbstractPlugin.item().build();
			List<? extends Item> theChildren = null;
			theChildren = client.getChildren(itemId, startIdx, limit, false, StorageHubServiceUtil.ACCOUNTING_HL_NODE_NAME);
			for (Item child : theChildren) {
				String itemPath = workspaceItemPath+"/"+child.getName();
				org.gcube.portlets.widgets.wsexplorer.shared.Item itemChild = getItem(item, child, itemPath, showableTypes, filterCriteria, false, loadGcubeProperties);
				_log.trace("Item: "+child.getName() +" converted!!!");
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
	public static ItemType getItemType(Item item) {
		if (item instanceof AbstractFileItem) {
			return getFileIconImageType( item);
		}
		else if (item instanceof FolderItem) {
			return getFolderItemType(item);
		}
		_log.warn("Item Type non found: " + item.toString());
		return ItemType.UNKNOWN_TYPE;

	}
	/**
	 *
	 * @param item
	 * @return the item type based on the extension of the file
	 */
	private static ItemType getFileIconImageType(Item item) {
		if (item instanceof ExternalURL)
			return ItemType.EXTERNAL_URL;
		AbstractFileItem aItem = (AbstractFileItem) item;
		if (aItem.getContent().getMimeType().compareTo("application/xml") == 0) {
			return ItemType.XML;
		}
		String[] splits =  item.getName().split("\\.");
		String extension = "";
		if (splits.length > 0) {
			extension = splits[splits.length-1];
		}
		if (extension == null || extension.compareTo("") == 0)
			return ItemType.UNKNOWN_TYPE;
		extension = extension.toLowerCase();
		switch (extension) {
		case "doc":
		case "docx":
			return ItemType.DOCUMENT;
		case "rtf":
		case "txt":
			return ItemType.TEXT_PLAIN;
		case "xls":
		case "xlsx":
			return ItemType.SPREADSHEET;
		case "csv":
			return ItemType.CSV;
		case "ics":
			return ItemType.CALENDAR;
		case "ppt":
		case "pptx":
			return ItemType.PRESENTATION;
		case "pdf":
			return ItemType.PDF_DOCUMENT;
		case "jpg":
		case "jpeg":
		case "gif":
		case "bmp":
		case "png":
		case "tif":
		case "tiff":
			return ItemType.IMAGE_DOCUMENT;
		case "avi":
		case "mp4":
		case "mpeg":
			return ItemType.MOVIE;
		case "html":
		case "htm":
		case "jsp":
			return ItemType.HTML;
		case "rar":
			return ItemType.RAR;
		case "zip":
		case "tar":
		case "tar.gz":
		case ".cpgz":
		case ".gz":
			return ItemType.ZIP;
		default:
			return ItemType.UNKNOWN_TYPE;
		}
	}

	/**
	 * Gets the folder item type.
	 *
	 * @param item the item
	 * @return the folder item type
	 */
	protected static ItemType getFolderItemType(Item item){
		if (item instanceof SharedFolder || item instanceof VreFolder) {
			SharedFolder folder = (SharedFolder) item;
			if (folder.isVreFolder())
				return ItemType.VRE_FOLDER;
			return ItemType.SHARED_FOLDER;
		} else if (item instanceof FolderItem) {
			return ItemType.PRIVATE_FOLDER;
		}
		_log.warn("Item Type non found: ");
		return ItemType.PRIVATE_FOLDER;
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
	protected static boolean filterItem(ItemType type, Item item, FilterCriteria filterCriteria) {
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
	protected static boolean checkAllowedMimeTypes(ItemType type, Item item, List<String> allowedMimeTypes){
		if (allowedMimeTypes==null || allowedMimeTypes.size()==0) {
			return true;
		}

		if (type == ItemType.EXTERNAL_FILE) {
			String mimeType = "unknown"; //TODO
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
	protected static boolean checkAllowedFileExtension(ItemType type, Item item, List<String> allowedFileExtension){
		if (allowedFileExtension==null || allowedFileExtension.size()==0) {
			return true;
		}
		try {
			if (! (item instanceof FolderItem)) {
				String name = item.getName();
				return checkFileExtension(name, allowedFileExtension);
			}
			return true;
		} catch (Exception e) {
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
	protected static boolean checkProperties(Item item, Map<String, String> requestedProperties)
	{
		if (requestedProperties==null || requestedProperties.size()==0 || item instanceof FolderItem) {
			return true;
		}

		Map<String, String> itemProperties = getGcubePropertiesForItem(item);

		if(itemProperties==null)
			return false;

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
	 * Gets the gcube properties for item.
	 *
	 * @param item the item
	 * @return the gcube properties for item
	 * @throws InternalErrorException the internal error exception
	 */
	protected static Map<String,String> getGcubePropertiesForItem(Item item) {
		Map<String, String> itemMap = new HashMap<String, String>();
		try {
			Metadata metadata = item.getMetadata();
			Map<String, Object> metadataMap = metadata.getMap();
			for (String key : metadataMap.keySet()) {
				String s = String.valueOf(metadataMap.get(key));
				itemMap.put(key, s);
			}
			return itemMap;
		}catch (Exception e) {
			_log.warn("An error occurred during get properties for item: "+item.getId()+", returning null");
			return new HashMap<String, String>();
		}
	}



	/**
	 * Builds the folder to breadcrumbs.
	 *
	 * @param wsFolder the ws folder
	 * @param parent the parent
	 * @return the item
	 * @throws InternalErrorException the internal error exception
	 */
	public static org.gcube.portlets.widgets.wsexplorer.shared.Item buildFolderForBreadcrumbs(FolderItem wsFolder, org.gcube.portlets.widgets.wsexplorer.shared.Item parent){

		String name = "";
		boolean isSpecialFolder = false;
		boolean isRoot = false;

		if(wsFolder.getParentId() == null){ //IS ROOT
			name = WorkspaceExplorerConstants.HOME_LABEL;
			isRoot = true;
		}
		if(isSpecialFolder(wsFolder)){
			name = WorkspaceExplorerConstants.VRE_FOLDERS_LABEL;
			isSpecialFolder = true;
		}
		else if(wsFolder.isShared()){ 		//MANAGEMENT SHARED FOLDER NAME
			if (wsFolder instanceof SharedFolder) {
				SharedFolder shared = (SharedFolder) wsFolder;
				name = shared.isVreFolder() ? shared.getDisplayName() : shared.getTitle();
			}
			else {
				FolderItem shared = wsFolder;
				name = shared.getTitle();
			}
			//MANAGEMENT SPECIAL FOLDER
		}
		else {
			name = wsFolder.getName();
		}

		//BUILDS A SIMPLE ITEM FOR BREADCRUMB
		String path = null; //wsFolder.getPath(); FORCED TO NULL BECAUSE IS SLOW CALL
		org.gcube.portlets.widgets.wsexplorer.shared.Item item = new org.gcube.portlets.widgets.wsexplorer.shared.Item(null, wsFolder.getId(), name, ItemType.FOLDER, path, null, null, true, isRoot);
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
	public static boolean isSpecialFolder(FolderItem wsFolder){
		return wsFolder.getName().compareTo(WorkspaceExplorerConstants.SPECIAL_FOLDERS_NAME) == 0;
	}


	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	public static  Date toDate(Calendar calendar) {
		if (calendar == null) return null;
		return calendar.getTime();

	}
	/**
	 * Purge empty folders.
	 *
	 * @param item the item
	 * @return the item
	 */
	public static org.gcube.portlets.widgets.wsexplorer.shared.Item purgeEmptyFolders(org.gcube.portlets.widgets.wsexplorer.shared.Item item) {
		List<org.gcube.portlets.widgets.wsexplorer.shared.Item> toRemoveList = new LinkedList<org.gcube.portlets.widgets.wsexplorer.shared.Item>();
		for (org.gcube.portlets.widgets.wsexplorer.shared.Item child:item.getChildren()) {
			boolean toRemove = isAnEmptyFolder(child);
			if (toRemove) {
				toRemoveList.add(child);
			}
		}
		for (org.gcube.portlets.widgets.wsexplorer.shared.Item child:toRemoveList) {
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
	protected static boolean isAnEmptyFolder(org.gcube.portlets.widgets.wsexplorer.shared.Item item) {
		return Util.isFolder(item.getType()) && item.getChildren().size() == 0;
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
