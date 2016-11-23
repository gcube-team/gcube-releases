/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.catalogue;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author valentina
 *
 */
public interface WorkspaceCatalogue extends WorkspaceFolder {

	/**
	 * Retrieve a catalogue item by id
	 * @param id the ID of a catalogue item
	 * @return a catalogue item
	 * @throws InternalErrorException
	 */
	public WorkspaceItem getCatalogueItem(String id) throws InternalErrorException;
	
	/**
	 * Retrieve a catalogue item by relative path
	 * @param path the relative path of a catalogue item
	 * @return a catalogue item
	 * @throws InternalErrorException
	 */
	public WorkspaceItem getCatalogueItemByPath(String path) throws InternalErrorException;
	
	/**
	 * Copy item into Catalogue Folder
	 * @param workspaceItemID the ID of the workspaceItem to copy
	 * @param destinationFolderID the ID of an existing catalogue folder
	 * @return the copied item
	 * @throws InternalErrorException
	 */
	public WorkspaceItem addWorkspaceItem(String workspaceItemID, String destinationFolderID) throws InternalErrorException;

	/**catalogueItemID
	 * Copy item into Catalogue root
	 * @param workspaceItemID the ID of the workspaceItem to copy
	 * @return the copied item
	 * @throws InternalErrorException
	 */
	public WorkspaceItem addWorkspaceItem(String workspaceItemID) throws InternalErrorException;

	
	/**
	 * Get a WorkspaceItem by a catalogue item ID
	 * @param catalogueItemID ID of an element of the catalogue
	 * @return a WorkspaceItem
	 * @throws InternalErrorException
	 */
	public WorkspaceItem getWorkspaceItemByCatalogueID(String catalogueItemID) throws InternalErrorException;
	
	/**
	 * Get all catalogue Items by a workspace item ID
	 * @param workspaceItemID ID of an workspace item
	 * @return a WorkspaceItem
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> getCatalogueItemByWorkspaceID(String workspaceItemID) throws InternalErrorException;
}
