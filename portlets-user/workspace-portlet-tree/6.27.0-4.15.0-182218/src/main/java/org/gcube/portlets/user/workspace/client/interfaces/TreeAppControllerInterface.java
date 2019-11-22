package org.gcube.portlets.user.workspace.client.interfaces;

import java.util.List;

import org.gcube.portlets.user.workspace.client.model.FileModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
//Implements this interface to upgrade the state of the store that was loaded in Async Tree 
public interface TreeAppControllerInterface {
	
	enum VisualizationType {TREE, SHORTCUT};
	
	//Methods returns true if state operation is OK, false otherwise
	boolean renameItem(String itemIdentifier, String newName, String extension);
	boolean deleteItem(String itemIdentifier);
	boolean addFolder(String itemIdentifier, String name, String parentIdentifier);
	boolean addFile(String itemIdentifier, String name, String parentIdentifier);

	boolean reloadFolderChildren(String itemIdentifier);
	List<FileModel> getListParentsByIdentifierFromTree(String itemIdentifier);
	void setVisualizationType(VisualizationType type);
	
	void findItemAndSelectItemInTree(String itemIdentifier); //this method is no longer used
	
	void expandFolder(String itemIdentifier);
	void searching(boolean isSearch);
	void selectRootItem();
	FileModel getSelectedFolderInTree();
}
