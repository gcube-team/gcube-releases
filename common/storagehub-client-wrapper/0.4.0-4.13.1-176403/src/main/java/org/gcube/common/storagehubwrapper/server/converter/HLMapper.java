/**
 *
 */
package org.gcube.common.storagehubwrapper.server.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.ExternalURL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.GenericFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.PDFFileItem;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehub.model.items.nodes.Accounting;
import org.gcube.common.storagehub.model.items.nodes.Content;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.items.nodes.PDFContent;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehub.model.service.Version;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.AccountingEntry;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.GcubeItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.ImageFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.PDFFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.PropertyMap;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.URLFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class HLMapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 20, 2018
 */
public class HLMapper {

	private static Logger logger = LoggerFactory.getLogger(HLMapper.class);

	public static Function<org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry, AccountingEntry> toAccountingEntry = new Function<org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry, AccountingEntry>() {

	    public AccountingEntry apply(org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry accountingEntry) {

	    	return new AccountingEntry(
	    		accountingEntry.getUser(),
	    		accountingEntry.getDate(),
	    		accountingEntry.getType().name(),
	    		accountingEntry.getVersion(),
	    		accountingEntry.getPrimaryType());
	    }
	};


	/**
	 * To workspace item.
	 *
	 * @param <T> the generic type
	 * @param item the item
	 * @return the t
	 */
	public static<T extends WorkspaceItem> T toWorkspaceItem(Item item) throws Exception{

		return toWorkspaceItem(item, false, false, false);
	}


	/**
	 * To accounting entries.
	 *
	 * @param item the item
	 * @return the list
	 */
	public static List<org.gcube.common.storagehubwrapper.shared.tohl.AccountingEntry> toAccountingEntries(Item item){

    	List<org.gcube.common.storagehubwrapper.shared.tohl.AccountingEntry> accountingEntries = null;

		Accounting accounting = item.getAccounting();
    	if(accounting!=null && accounting.getEntries().size()>0){
    		accountingEntries = new ArrayList<org.gcube.common.storagehubwrapper.shared.tohl.AccountingEntry>(accounting.getEntries().size());
    		for (AccountEntry ae : accounting.getEntries()) {
    			accountingEntries.add(toAccountingEntry.apply(ae));
			}
    	}

    	return accountingEntries;
	}



	/**
	 * To property map.
	 *
	 * @param metadata the metadata
	 * @return the property map
	 */
	public static PropertyMap toPropertyMap(Metadata metadata){

    	PropertyMap pm = null;
		if(metadata!=null)
			pm = new PropertyMap(metadata.getMap());

		//IN CASE OF EMPTY MAP RETURNS NULL
		if(pm!=null && pm.getValues()!=null && pm.getValues().isEmpty())
			pm = null;

		return pm;
	}


	/**
	 * To workspace file version.
	 *
	 * @param fileVersion the file version
	 * @return the workspace file version
	 */
	public static WorkspaceFileVersion toWorkspaceFileVersion(Version fileVersion){
		WorkspaceFileVersion wsFileVersion = new WorkspaceFileVersion();
		wsFileVersion.setId(fileVersion.getId());
		wsFileVersion.setName(fileVersion.getName());
		wsFileVersion.setCreated(fileVersion.getCreated());
		wsFileVersion.setCurrentVersion(fileVersion.isCurrent());
		//TODO MUST BE TERMINATED
		return wsFileVersion;
	}

	/**
	 * To workspace item.
	 *
	 * @param <T> the generic type
	 * @param item the item
	 * @param withAccounting the with accounting
	 * @param withFileDetails the with file details
	 * @param withMapProperties the with map properties
	 * @return the t
	 */
	public static<T extends WorkspaceItem> T toWorkspaceItem(Item item, boolean withAccounting, boolean withFileDetails, boolean withMapProperties) throws Exception{

		try{
	    	List<org.gcube.common.storagehubwrapper.shared.tohl.AccountingEntry> accountingEntries = null;

			if(withAccounting)
				accountingEntries = toAccountingEntries(item);

	    	org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceItem theItem = null;
	    	WorkspaceItemType type = null;
	    	Boolean isFolder = false;

	    	String itemName = item.getName();
	    	boolean isRoot = false;

	    	logger.trace("Converting Item: "+item.getName() + " with id: "+item.getId());

	    	//THE ITEM IS A KIND OF FOLDER
	    	if (item instanceof FolderItem){
	    		isFolder = true;
	    		FolderItem folderItem = (FolderItem) item; //??
				theItem = new WorkspaceFolder();
				type = WorkspaceItemType.FOLDER;
	    		if (item instanceof SharedFolder || item instanceof VreFolder) {
	    			SharedFolder sharedfolder = (SharedFolder) item; //??
	    			theItem = new WorkspaceSharedFolder();
					type = WorkspaceItemType.SHARED_FOLDER;
					itemName = sharedfolder.getTitle(); //IN CASE OF SHARED FOLDER THE NAME IS AN UUID, I'M USING THE TITLE

					if (sharedfolder.isVreFolder()){
						//logger.debug("Converting shared folder: "+theItem.getClass());
						//VreFolder vreFolder = (VreFolder) item;
	        			//theItem = new WorkspaceVREFolder(); //NEVER INSTANCE THE WorkspaceVREFolder because VreFolder is never used by HL/StorageHub
	        			itemName = sharedfolder.getDisplayName(); //IN CASE OF VRE FOLDER I'M USING THE DISPLAYNAME
						((WorkspaceSharedFolder) theItem).setVreFolder(true);
						type = WorkspaceItemType.VRE_FOLDER;
					}
	    		}

	    		boolean isPublicItem = folderItem.isPublicItem();
	    		//((WorkspaceFolder) theItem).setPublicFolder(folderItem.isPublicItem());
	    		//logger.debug("Wrapped as Folder");

	    		//TODO THIS MUST BE REMOVED. Checking the old property isPublic added as "<boolean>true</boolean>" by HL
	    		if(!isPublicItem){
	    			try{
	    				//Map<String, Object> map = item.getPropertyMap().getValues();
//	    				logger.debug("Property Map size: "+map.size());
//	    				for (String key : map.keySet()) {
//	    					logger.debug("Key: "+key+ "value: "+map.get(key));
//						}
	    				String isPublic = (String) item.getMetadata().getMap().get("isPublic");
	    				logger.debug("The item name: "+item.getName()+ " has isPublic property like: "+isPublic);
	    				isPublicItem = isPublic!=null?isPublic.toLowerCase().contains("true"):false;
	    			}catch(Exception e){
	    				//silent
	    			}
	    		}

	    		((WorkspaceFolder) theItem).setPublicFolder(isPublicItem);

	    	}

	    	//THE ITEM IS A KIND OF FILE
	    	if(item instanceof AbstractFileItem){
	    		theItem = new FileItem();
	    		type = WorkspaceItemType.FILE_ITEM;
	    		FileItemType fileItemType = null;
	    		if(item instanceof ExternalURL){
	    			ExternalURL eURL = (ExternalURL) item; //??
	    			theItem = new URLFile();
	    			fileItemType = FileItemType.URL_DOCUMENT;
	    		}else if(item instanceof GenericFileItem){
	    			GenericFileItem gFI = (GenericFileItem) item; //??
	    			fileItemType = FileItemType.DOCUMENT;
	    		}else if(item instanceof PDFFileItem){
	    			theItem = new PDFFile();
	    			fileItemType = FileItemType.PDF_DOCUMENT;
	    			if(withFileDetails){
	        			PDFFileItem pdfFI = (PDFFileItem) item;
		    			PDFContent pdfContent = pdfFI.getContent();
		    			PDFFile thePDFFileItem = (PDFFile) theItem;
		    			thePDFFileItem.setNumberOfPages(pdfContent.getNumberOfPages());
		    			thePDFFileItem.setProducer(pdfContent.getProducer());
		    			thePDFFileItem.setVersion(pdfContent.getVersion());
		    			thePDFFileItem.setTitle(pdfContent.getTitle());
		    			thePDFFileItem.setAuthor(pdfContent.getAuthor());
	    			}
	    		}else if(item instanceof org.gcube.common.storagehub.model.items.ImageFile){
	    			theItem = new ImageFile();
	    			fileItemType = FileItemType.IMAGE_DOCUMENT;
	    			if(withFileDetails){
	        			org.gcube.common.storagehub.model.items.ImageFile imgFI = (org.gcube.common.storagehub.model.items.ImageFile) item; //??
	        			ImageFile theImageFileItem = (ImageFile) theItem;
	        			ImageContent content = imgFI.getContent();
	        			theImageFileItem.setHeight(content.getHeight());
	         			theImageFileItem.setWidth(content.getWidth());
	         			theImageFileItem.setThumbnailWidth(content.getThumbnailHeight());
	         			theImageFileItem.setThumbnailHeight(content.getThumbnailHeight());
	    			}
	    		}

	    		AbstractFileItem itemAb =  (AbstractFileItem) item;
	    		Content aC = itemAb.getContent();
	    		FileItem theFileItem = (FileItem) theItem;
	    		theFileItem.setFileItemType(fileItemType);

	    		if(aC!=null){
	    			logger.debug("The content is not null for item: "+itemAb.getId());
		    		theFileItem.setMimeType(aC.getMimeType());
		    		theFileItem.setSize(aC.getSize());
	    		}else
	    			logger.warn("The content is null for item: "+itemAb.getId());

	    		//TODO ADD VERSION NAME

	    	}

	    	//ADDING (GCUBE) PROPERTIES
	    	PropertyMap pm = null;
	    	if(withMapProperties || isFolder){
	    		//System.out.println("Setting map: "+item.getPropertyMap().getValues());
	    		pm = toPropertyMap(item.getMetadata());
	    	}

	    	//CONVERTING TRASH ITEM
			if(item.isTrashed()){
				type = WorkspaceItemType.TRASH_ITEM;
				TrashItem trashItem = (TrashItem) item; //??
				theItem = new org.gcube.common.storagehubwrapper.shared.tohl.impl.TrashItem();
				org.gcube.common.storagehubwrapper.shared.tohl.impl.TrashItem theTrashItem = (org.gcube.common.storagehubwrapper.shared.tohl.impl.TrashItem) theItem;
				theTrashItem.setDeletedBy(trashItem.getDeletedBy());
				theTrashItem.setDeletedTime(trashItem.getDeletedTime());
				theTrashItem.setOriginalParentId(trashItem.getOriginalParentId());
				theTrashItem.setDeletedFrom(trashItem.getDeletedFrom());
				theTrashItem.setLenght(trashItem.getLenght());
				theTrashItem.setMimeType(trashItem.getMimeType());
				isFolder = trashItem.isFolder(); //DO NOT MOVE THIS SET
				if(isFolder) //Avoiding null exception on
					type = WorkspaceItemType.TRASH_FOLDER;

				logger.debug("Wrapped as TrashItem");
			}

			if(item instanceof GCubeItem){
				type = WorkspaceItemType.FILE_ITEM;
				GCubeItem gcubeItem = (GCubeItem) item; //??
				theItem = new GcubeItem();
				GcubeItem theGcubeItem = (GcubeItem) theItem;
				theGcubeItem.setFileItemType(FileItemType.GCUBE_ITEM);
				theGcubeItem.setScopes(gcubeItem.getScopes());
				theGcubeItem.setCreator(gcubeItem.getCreator());
				theGcubeItem.setItemType(gcubeItem.getItemType());
				theGcubeItem.setShared(gcubeItem.isShared());
				if(withMapProperties){
					PropertyMap property = toPropertyMap(gcubeItem.getProperty());
					theGcubeItem.setProperty(property);
				}

				logger.debug("Wrapped as GcubeItem");
			}

			logger.trace("The item: "+item);
			logger.trace("The item id: "+item.getId());

			if(theItem==null){
				logger.info("Mapping unknown object as simple FileItem");
				theItem = new FileItem();
				type = WorkspaceItemType.FILE_ITEM;
				FileItem theFileItem = (FileItem) theItem;
	    		theFileItem.setFileItemType(FileItemType.DOCUMENT);
			}

			theItem.setId(item.getId());
			theItem.setName(itemName);
			theItem.setPath(item.getPath());
			theItem.setParentId(item.getParentId());
			theItem.setTrashed(item.isTrashed());
			theItem.setShared(item.isShared());
			theItem.setLocked(item.isLocked());
			theItem.setTitle(item.getTitle());
			theItem.setDescription(item.getDescription());
			theItem.setLastModifiedBy(item.getLastModifiedBy());
			theItem.setLastModificationTime(item.getLastModificationTime());
			theItem.setCreationTime(item.getCreationTime());
			theItem.setOwner(item.getOwner());
			theItem.setHidden(item.isHidden());
			theItem.setAccounting(accountingEntries);
			theItem.setType(type);
			theItem.setFolder(isFolder);
			theItem.setRoot(isRoot); //IS ALWAYS FALSE
			theItem.setPropertyMap(pm);

			logger.debug("Wrapped WsItem: "+theItem);

			return (T) theItem;

		}catch (Exception e){
			logger.error("Error on converting the item with id: "+item.getId(), e);
			throw e;
		}


	}

}
