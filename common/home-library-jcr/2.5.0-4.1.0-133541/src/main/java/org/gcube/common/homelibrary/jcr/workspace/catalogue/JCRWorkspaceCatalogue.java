package org.gcube.common.homelibrary.jcr.workspace.catalogue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.common.homelibary.model.items.MetadataProperty;
import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingEntryPaste;
import org.gcube.common.homelibrary.jcr.workspace.accounting.JCRAccountingFolderEntryAdd;
import org.gcube.common.homelibrary.jcr.workspace.lock.JCRLockManager;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class JCRWorkspaceCatalogue extends JCRWorkspaceFolder implements WorkspaceCatalogue {


	public JCRWorkspaceCatalogue(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate);
	}

	public JCRWorkspaceCatalogue(JCRWorkspace workspace, ItemDelegate myCatalogue, String catalogueFolder,
			String owner) throws RepositoryException {
		super(workspace, myCatalogue, catalogueFolder, owner);
	}


	@Override
	public WorkspaceItem getCatalogueItem(String id) throws InternalErrorException {
		Validate.notNull(id, " ID must be not null");

		logger.info("Get Catalogue Item by ID : " + id);

		WorkspaceItem item = null;
		try {
			item = workspace.getItem(id);
			if(!item.getPath().contains(getPath()))
				throw new InternalErrorException(id + " is not in Catalogue");

		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(id + " item ID not found.");
		}
		return item;
	}

	@Override
	public WorkspaceItem getCatalogueItemByPath(String path) throws InternalErrorException {
		Validate.notNull(path, " path must be not null");

		logger.info("Get Catalogue Item by path : " + path);

		WorkspaceItem item = null;
		try {
			item = workspace.getItemByPath(getPath() + path);
		} catch (ItemNotFoundException e) {
			throw new InternalErrorException(path + " path not found.");
		}
		return item;

	}


	@Override
	public WorkspaceItem addWorkspaceItem(String workspaceItemID, String destinationFolderID)
			throws InternalErrorException {
		Validate.notNull(workspaceItemID, "workspaceItem Id must be not null");
		Validate.notNull(destinationFolderID, "destinationFolder Id must be not null");

		WorkspaceItem item = null;
		try {
			WorkspaceItem workspaceItem = workspace.getItem(workspaceItemID);
			WorkspaceItem destinationFolder = workspace.getItem(destinationFolderID);

			if (!destinationFolderID.equals(getId()))
				if(!destinationFolder.getPath().contains(getPath()))
					throw new InternalErrorException(destinationFolderID + " is not a catalogue folder");

			String time = getTimestamp();
			if (workspaceItem.isFolder()){	
				item = copyNoSubgraph(workspaceItemID, workspaceItem.getName()+ "_" + time, destinationFolderID); 
			} else
				item = workspace.copy(workspaceItemID, workspaceItem.getName()+ "_" + time, destinationFolderID);

			item.getProperties().addProperty(MetadataProperty.WORKSPACE_ID.toString(), workspaceItemID);

		} catch (InsufficientPrivilegesException | ItemAlreadyExistException | WrongDestinationException
				| ItemNotFoundException | WorkspaceFolderNotFoundException e) {
			throw new InternalErrorException("Impossible to add workspace item with id: " + workspaceItemID + " to destination folder: " + destinationFolderID);
		}
		return item;
	}


	/**
	 * Get Time Stamp to rename a file
	 * @return
	 */
	private String getTimestamp() {
		//		"2012_04_05_11400029_MyAwesomeFile.txt"
		Date date=new Date();
		//		Timestamp timestamp = new Timestamp(date.getTime());//instead of date put your converted date
		//		Timestamp myTimeStamp= timestamp;
		//		return myTimeStamp.toString();
		String strLong = Long.toString(date.getTime());
		return strLong;

	}

	private WorkspaceItem copyNoSubgraph(String itemId, String newName, String destinationFolderId) throws ItemNotFoundException,
	WrongDestinationException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, InternalErrorException {

		JCRWorkspaceItem newItem = null;

		JCRSession session = null;
		JCRLockManager lm = null;
		ItemDelegate itemDelegate = null;
		ItemDelegate destinationDelegate = null;
		//		String commonPathId = "";
		try{
			session = new JCRSession(getOwner().getPortalLogin(), true);

			try {
				itemDelegate =  session.getItemById(itemId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}

			if(destinationFolderId == null)
				destinationFolderId = itemDelegate.getParentId();

			try {
				destinationDelegate = session.getItemById(destinationFolderId);
			} catch (Exception e) {
				throw new ItemNotFoundException(e.getMessage());
			}
			if(!destinationDelegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_FOLDER)
					&& !destinationDelegate.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {
				throw new WrongDestinationException("Destination is not a folder");
			}


			String path = itemDelegate.getPath();
			String destinpath = destinationDelegate.getPath();
			//			String commonPath = Utils.commonPath(path, destinpath);


			lm = session.getLockManager();
			if (!lm.isLocked(itemId) && !lm.isLocked(destinationFolderId)){

				if (lm.lockItem(itemId))
					logger.info("item " + path + " has been locked");
				else
					logger.info("item " + path + " cannot be locked");

				logger.trace("LOCK on Node id: " + path + ", " + destinpath);

				JCRWorkspaceItem item = workspace.getWorkspaceItem(itemDelegate);

				if (newName == null) 
					newName = item.getName();

				ItemDelegate newNode = item.internalCopy(session, destinationDelegate, newName, true);		

				newItem = workspace.getWorkspaceItem(newNode);

				//TODO temporarily solution to copy all remote content item child nodes.
				//				copyRemoteContent(session, newNode, destinationDelegate);

				Calendar now = Calendar.getInstance();

				// Set paste accounting property
				JCRAccountingEntryPaste entryPaste = new JCRAccountingEntryPaste(newItem.getId(), getOwner().getPortalLogin(),
						now, destinationDelegate.getTitle());
				entryPaste.save(session);


				//Set add entry on destination folder
				if (destinationDelegate!=null){
					logger.debug("Set ADD accounting entry to destination folder " + destinationDelegate.getPath() );
					// Set add accounting entry to destination folder
					JCRAccountingFolderEntryAdd entryAdd = new JCRAccountingFolderEntryAdd(destinationFolderId, getOwner().getPortalLogin(),
							now, newItem.getType(),
							(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getFolderItemType():null,
									newItem.getName(),
									(newItem.getType() == WorkspaceItemType.FOLDER_ITEM)?((FolderItem)newItem).getMimeType():null);
					entryAdd.save(session);
				}

			}else
				throw new InternalErrorException("LockException: Node locked. Impossible to copy itemID " + itemId);

		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}finally{
			//			lm.unlockItem(commonPathId);
			lm.unlockItem(itemId);
			//			lm.unlockItem(destinationFolderId);
			logger.trace("Release LOCK on Node ids : " + itemId + ", "+ destinationFolderId);

			session.releaseSession();
		}
		return newItem;

	}

	@Override
	public WorkspaceItem getWorkspaceItemByCatalogueID(String catalogueItemID) throws InternalErrorException {
		Validate.notNull(catalogueItemID, "Catalogue ID must be not null");

		WorkspaceItem item = null;
		try {
			WorkspaceItem workspaceItem = workspace.getItem(catalogueItemID);
			String originalID = workspaceItem.getProperties().getPropertyValue(MetadataProperty.WORKSPACE_ID.toString());
			item = workspace.getItem(originalID);
		} catch (Exception e) {
			throw new InternalErrorException("Workspace Item not found by catalogue ID " + catalogueItemID);
		}
		return item;

	}

	@Override
	public List<WorkspaceItem> getCatalogueItemByWorkspaceID(String workspaceItemID) throws InternalErrorException {
		Validate.notNull(workspaceItemID, "Workspace ID must be not null");

		List<WorkspaceItem> items = null;
		try {
			SearchQueryBuilder query = new SearchQueryBuilder();
			query.contains(MetadataProperty.WORKSPACE_ID.toString(), workspaceItemID);
			items = workspace.searchByProperties(query.build());
//			System.out.println(items.size());
		} catch (Exception e) {
			throw new InternalErrorException("Catalogue Items not found by Workspace ID " + workspaceItemID);
		}
		return items;
	}

	@Override
	public WorkspaceItem addWorkspaceItem(String workspaceItemID) throws InternalErrorException {
		return addWorkspaceItem(workspaceItemID, getId());
	}





}
