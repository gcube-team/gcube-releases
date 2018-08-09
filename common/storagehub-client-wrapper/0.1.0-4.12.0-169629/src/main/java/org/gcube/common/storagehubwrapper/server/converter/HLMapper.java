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
import org.gcube.common.storagehub.model.items.GenericFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.PDFFileItem;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehub.model.items.nodes.Accounting;
import org.gcube.common.storagehub.model.items.nodes.Content;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.items.nodes.PDFContent;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.AccountingEntry;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.ImageFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.PDFFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.PropertyMap;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.URLFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceVREFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItemType;


/**
 * The Class HLMapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 20, 2018
 */
public class HLMapper {

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
	public static<T extends WorkspaceItem> T toWorkspaceItem(Item item){

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
	 * @param item the item
	 * @return the property map
	 */
	public static PropertyMap toPropertyMap(Item item){
    	//ADDING (GCUBE) PROPERTIES
    	PropertyMap pm = null;
		Metadata metadata = item.getPropertyMap();
		if(metadata!=null)
			pm = new PropertyMap(metadata.getValues());

		return pm;
	}

	/**
	 * To workspace item.
	 *
	 * @param <T> the generic type
	 * @param item the item
	 * @return the t
	 */
	public static<T extends WorkspaceItem> T toWorkspaceItem(Item item, boolean withAccounting, boolean withFileDetails, boolean withMapProperties){

    	List<org.gcube.common.storagehubwrapper.shared.tohl.AccountingEntry> accountingEntries = null;

		if(withAccounting)
			accountingEntries = toAccountingEntries(item);

    	org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceItem theItem = null;
    	WorkspaceItemType type = null;
    	boolean isFolder = false;

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
				if (sharedfolder.isVreFolder()){
					VreFolder vreFolder = (VreFolder) item;
        			theItem = new WorkspaceVREFolder();
        			((WorkspaceSharedFolder) theItem).setVreFolder(true);
					type = WorkspaceItemType.VRE_FOLDER;
				}
    		}

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
    		theFileItem.setMimeType(aC.getMimeType());
    		theFileItem.setSize(aC.getSize());
    		theFileItem.setFileItemType(fileItemType);

    		//TODO ADD VERSION NAME

    	}

    	//ADDING (GCUBE) PROPERTIES
    	PropertyMap pm = null;
    	if(withMapProperties)
    		pm = toPropertyMap(item);

    	//TRASH //TODO

		if(theItem.isTrashed())
			type = WorkspaceItemType.TRASH_ITEM;

		theItem.setId(item.getId());
		theItem.setName(item.getName());
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
		theItem.setRoot(false);
		theItem.setPropertyMap(pm);

		return (T) theItem;
	}

	/*public static Function<Item, ? extends WorkspaceItem> toWorkspaceItem = new Function<Item, WorkspaceItem>() {

	    public WorkspaceItem apply(Item item) {

	    	Accounting accounting = item.getAccounting();
	    	List<AccountingEntry> accountingEntries = null;
	    	if(accounting!=null && accounting.getEntries().size()>0){
	    		accountingEntries = new ArrayList<AccountingEntry>(accounting.getEntries().size());
	    		for (AccountEntry ae : accounting.getEntries()) {
	    			//TODO
	    			accountingEntries.add(toAccountingEntry.apply(ae));
				}
	    	}

	    	WorkspaceItemImpl theItem = null;
	    	WorkspaceItemType type = null;
	    	boolean isFolder = false;

	    	//IS THE ITEM A FOLDER TYPE?
	    	if (item instanceof FolderItem){
	    		isFolder = true;
	    		FolderItem folderItem = (FolderItem) item; //??
    			theItem = new WorkspaceFolderImpl();
    			type = org.gcube.portal.storagehubwrapper.shared.tohl.WorkspaceItemType.FOLDER_ITEM;
	    		if (item instanceof SharedFolder || item instanceof VreFolder) {
        			SharedFolder sharedfolder = (SharedFolder) item; //??
        			theItem = new WorkspaceSharedFolderImpl();
					type = org.gcube.portal.storagehubwrapper.shared.tohl.WorkspaceItemType.SHARED_FOLDER;
					if (sharedfolder.isVreFolder()){
						VreFolder vreFolder = (VreFolder) item;
	        			theItem = new WorkspaceVREFolderImpl();
						type = org.gcube.portal.storagehubwrapper.shared.tohl.WorkspaceItemType.VRE_FOLDER;
					}
	    		}

	    	}

    		if(theItem.isTrashed())
    			type = org.gcube.portal.storagehubwrapper.shared.tohl.WorkspaceItemType.TRASH_ITEM;

    		theItem.setId(item.getId());
    		theItem.setName(item.getName());
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
    		theItem.setRoot(false);

    		return theItem;
	    }
	};*/


}
