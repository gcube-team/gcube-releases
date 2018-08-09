/**
 *
 */
package org.gcube.portlets.user.wswidget;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.ExternalURL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.portal.stohubicons.IconsManager;
import org.gcube.portal.stohubicons.shared.MDIcon;
import org.gcube.portlets.user.wswidget.shared.ItemType;
import org.gcube.portlets.user.wswidget.shared.WSItem;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemBuilder {

	public static final Logger _log = LoggerFactory.getLogger(ItemBuilder.class);
	private static UserManager UMAN = new LiferayUserManager();
	/**
	 * 
	 * @param parent
	 * @param workspaceItem
	 * @param workspaceItemPath
	 * @param currentUserName
	 * @return
	 */
	public static WSItem getItem(WSItem parent, Item workspaceItem, String workspaceItemPath, String currentUserName)  {
		ItemType type = getItemType(workspaceItem);
		boolean isFolder = type.equals(ItemType.PRIVATE_FOLDER)?true:false;
		boolean isSharedFolder = (type.equals(ItemType.SHARED_FOLDER) || type.equals(ItemType.VRE_FOLDER )) ? true : false;

		String itemName =  workspaceItem.getName();
		if(isSharedFolder){
			_log.info("Is shared folder: "+workspaceItem.getTitle());
			SharedFolder shared = (SharedFolder) workspaceItem;
			itemName = shared.isVreFolder()?shared.getDisplayName():workspaceItem.getTitle();
		}

		_log.debug("Building Item for: "+itemName + " id="+workspaceItem.getId());
		WSItem item = null;
		try{
			String fullName = "me";
			if (currentUserName.compareTo(workspaceItem.getOwner()) != 0) {
				fullName = workspaceItem.getOwner();
				try {
					fullName = UMAN.getUserByUsername(workspaceItem.getOwner()).getFullname();
				}
				catch (UserRetrievalFault f) {
					_log.warn("The user does not exist in this portal: " + fullName);
				}
			}
			item = new WSItem(parent, workspaceItem.getId(), itemName, type, workspaceItemPath, fullName, toDate(workspaceItem.getCreationTime()), toDate(workspaceItem.getLastModificationTime()), isFolder, false);
			item.setSharedFolder(isSharedFolder);
			if (isSharedFolder) {
				item.setIconNameAndColor("folder_shared", "#8F8F8F"); //gray
			} else if (isFolder) {
				item.setIconNameAndColor("folder", "#8F8F8F");// #gray 
			}
			else { //is a file, a Link or an XML
				MDIcon mdIcon = IconsManager.getDefault();
				if (workspaceItem instanceof ExternalURL) {
					mdIcon = IconsManager.getIconTypeLink();
				} else {
					AbstractFileItem aItem = (AbstractFileItem) workspaceItem;
					try {
						String mimeType = aItem.getContent().getMimeType();
						if (mimeType.compareTo("application/xml") == 0) {
							mdIcon = IconsManager.getXMLTypeLink();
						}
						else if (mimeType.compareTo("application/zip") == 0 
								|| mimeType.compareTo("application/tar") == 0
								|| mimeType.compareTo("application/x-gzip") == 0
								|| mimeType.compareTo("application/tar+gzip") == 0
								|| mimeType.compareTo("application/octet-stream") == 0
								|| mimeType.compareTo("application/x-rar-compressed") == 0
								|| mimeType.compareTo("application/x-tgz") == 0) {
							mdIcon = new MDIcon("archive", "#ffc107");  //amber
						}
						else {
							String[] splits =  item.getName().split("\\.");
							String extension = "";
							if (splits.length > 0) {
								extension = splits[splits.length-1];
							}
							mdIcon = IconsManager.getMDIconTextualName(extension);					
						}
					} catch (NullPointerException e) {
						_log.warn("could not get mimeType for " + itemName);
						String[] splits =  item.getName().split("\\.");
						String extension = "";
						if (splits.length > 0) {
							extension = splits[splits.length-1];
						}
						mdIcon = IconsManager.getMDIconTextualName(extension);			
					}
				}
				item.setIconNameAndColor(mdIcon.getTextualName(), mdIcon.getColor());
			} 
		} catch(Exception e){
			_log.error("Error on getting item: "+itemName+" with id: "+workspaceItem.getId()+", from StorageHub, so skipping item " + e.getMessage());
			e.printStackTrace();
			return null;
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
	protected static ItemType getItemType(Item item) {
		if (item instanceof AbstractFileItem) {
			return ItemType.EXTERNAL_FILE;
		}
		else if (item instanceof FolderItem) {
			return getFolderItemType(item);
		}
		_log.warn("Item Type non found: ");
		return ItemType.UNKNOWN_TYPE;
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
		return ItemType.UNKNOWN_TYPE;
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
		if (type == ItemType.EXTERNAL_FILE || type == ItemType.EXTERNAL_IMAGE || type == ItemType.EXTERNAL_PDF_FILE) {
			AbstractFileItem externalFile = (AbstractFileItem)item;
			String mimeType = externalFile.getContent().getMimeType();
			return allowedMimeTypes.contains(mimeType);
		}
		return true;
	}
	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	public static Date toDate(Calendar calendar) {
		if (calendar == null) return null;
		return calendar.getTime();

	}


}
