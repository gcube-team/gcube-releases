/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.GenericItemType;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.acl.Capabilities;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEventSource;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongParentTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreatorManager;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.WorkflowReport;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.home.workspace.search.SearchItemByOperator;
import org.gcube.common.homelibrary.home.workspace.search.SearchFolderItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQuery;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;

/**
 * Represents a user workspace.
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 */

public interface Workspace extends WorkspaceEventSource {

	/**
	 * Returns the item path separator.
	 * @return the path separator.
	 */
	public String getPathSeparator();

	/**
	 * Returns the user home.
	 * @return the home.
	 */
	public Home getHome();

	/**
	 * Returns the workspace owner.
	 * @return the owner.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public User getOwner() throws InternalErrorException;

	/**
	 * Returns the workspace root.
	 * @return the root.
	 * @throws InternalErrorException 
	 */
	public WorkspaceFolder getRoot();

	/**
	 * Get a list of workspaceItem parents by id
	 * @param id: the item id
	 * @return a list of parents
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> getParentsById(String id) throws InternalErrorException;
	
	/**
	 * Get the WorkspaceTree
	 * @param item
	 * @return WorkspaceTree
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> getWorkspaceTree(WorkspaceItem item) throws InternalErrorException;

	/**
	 * Add a Bookmark
	 * @param name
	 * @param folderId
	 * @throws ItemAlreadyExistException
	 * @throws InternalErrorException
	 * @throws RepositoryException 
	 * @throws PathNotFoundException 
	 */
	public void addBookmark(String itemId, String destinationFolderId)
			throws ItemAlreadyExistException, InternalErrorException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Returns all bookmarks
	 * @return a list of bookmarks
	 * @throws InternalErrorException
	 */
	public List<Object> getBookmarks(String bookmarkFolderId) throws InternalErrorException;

	/**
	 * Create a smart folder
	 * @param name the smart folder name
	 * @param description the smart folder description
	 * @param query the term to search
	 * @param folderId the folder where to search the query
	 * @return a workspace smart folder
	 * @throws ItemAlreadyExistException
	 * @throws InternalErrorException
	 */
	public WorkspaceSmartFolder createSmartFolder(String name, String description, String query, String folderId) throws ItemAlreadyExistException,
	InternalErrorException;

	/**
	 * Returns all user smart folders
	 * @return a list of WorkspaceSmartFolder
	 * @throws InternalErrorException
	 */
	public List<WorkspaceSmartFolder> getAllSmartFolders() throws InternalErrorException;


	/**
	 * Get Smart Folder
	 * @param folderId
	 * @return Smart Folder
	 * @throws ItemNotFoundException
	 * @throws InternalErrorException
	 */
	public WorkspaceSmartFolder getSmartFolder(String folderId) throws ItemNotFoundException,
	InternalErrorException;  

	/**
	 * Create a new folder with specified name.
	 * The new folder is created into the specified folder.
	 * @param name the folder name.
	 * @param description the folder description.
	 * @param destinationFolderId the destination folder.
	 * @return the new folder.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws ItemNotFoundException if the destination folder has not been found.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException if an item with the same exist in the destination folder.
	 * @throws WrongDestinationException if the destination item is not a folder. 
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found. 
	 */
	public WorkspaceFolder createFolder(String name, String description, String destinationFolderId) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Create a new External Image into a folder.
	 * @param name the external image name.
	 * @param description the external image description.
	 * @param mimeType the image mime type.
	 * @param imageData the external image data.
	 * @param destinationFolderId the destination folder.
	 * @return the new external image.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public ExternalImage createExternalImage(String name, String description, String mimeType, InputStream imageData, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Create a new External File into a folder.
	 * @param name the external file name.
	 * @param description the external file description.
	 * @param mimeType the external file mime type.
	 * @param fileData the external file content.
	 * @param destinationFolderId the destination folder.
	 * @return the new external file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public ExternalFile createExternalFile(String name, String description, String mimeType, InputStream fileData, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Create a new External PDF File into a folder.
	 * @param name the external PDF file name.
	 * @param description the external PDF file description.
	 * @param mimeType the external PDF file mime type.
	 * @param fileData the external PDF file content.
	 * @param destinationFolderId the destination folder.
	 * @return the new external PDF file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public ExternalPDFFile createExternalPDFFile(String name, String description, String mimeType, File tmpFile, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException;


	/**
	 * Create a new External PDF File into a folder.
	 * @param name the external PDF file name.
	 * @param description the external PDF file description.
	 * @param mimeType the external PDF file mime type.
	 * @param fileData the external PDF file content.
	 * @param destinationFolderId the destination folder.
	 * @return the new external PDF file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public ExternalPDFFile createExternalPDFFile(String name, String description, String mimeType, InputStream fileData, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Create a new External URL into a folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the external URL value.
	 * @param destinationFolderId the destination folder.
	 * @return the new external URL.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 * @throws IOException 
	 */
	public ExternalUrl createExternalUrl(String name, String description, String url, String destinationFolderId) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, IOException;

	/**
	 * Create a new External URL into a folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the external URL.
	 * @param destinationfolderId the destination folder.
	 * @return the new external URL.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 * @throws IOException 
	 */
	public ExternalUrl createExternalUrl(String name, String description, InputStream url, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException;

	/**
	 * Create a new Report Template into a folder.
	 * @param name the template name.
	 * @param description the template description.
	 * @param created the template creation time.
	 * @param lastEdit the last edit time.
	 * @param author the template author.
	 * @param lastEditBy the last template editor.
	 * @param numberOfSections the number of sections.
	 * @param status the template status.
	 * @param templateData the template content.
	 * @param destinationfolderId the destination folder.
	 * @return the new template.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public ReportTemplate createReportTemplate(String name, String description, Calendar created, Calendar lastEdit, String author, String lastEditBy, int numberOfSections, String status, InputStream templateData, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException;


	/**
	 * Create a new Report into a folder.
	 * @param name the report name.
	 * @param description the report description.
	 * @param created the report creation time.
	 * @param lastEdit the last edit time.
	 * @param author the report author.
	 * @param lastEditBy the last report editor.
	 * @param templateName the source template name.
	 * @param numberOfSections the number of sections.
	 * @param status the report status.
	 * @param reportData the report content.
	 * @param destinationfolderId the destination folder.
	 * @return the new report.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public Report createReport(String name, String description, Calendar created, Calendar lastEdit, String author, String lastEditBy, String templateName, int numberOfSections, 
			String status, InputStream reportData, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException;


	/**
	 * Create a new query into a folder.
	 * @param name the query name.
	 * @param description the query description.
	 * @param query the query value.
	 * @param queryType the query type.
	 * @param destinationfolderId the destination folder.
	 * @return the new query.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public Query createQuery(String name, String description, String query, QueryType queryType, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException;

	/**
	 * Create a new query into a folder.
	 * @param name the query name.
	 * @param description the query description.
	 * @param query the query.
	 * @param queryType the query type.
	 * @param destinationfolderId the destinatin folder.
	 * @return the new query.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if a folder item with same name already exist.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public Query createQuery(String name, String description, InputStream query, QueryType queryType, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException;

	/**
	 * Create a new Time Series.
	 * @param name the item name.
	 * @param description the item description.
	 * @param timeseriesId the Time Series id.
	 * @param title the Time Series title.
	 * @param creator the Time Series creator.
	 * @param timeseriesDescription the Time Series description. 
	 * @param timeseriesCreationDate the Time Series creation date.
	 * @param publisher the Time Series publisher.
	 * @param sourceId the Time Series source id.
	 * @param sourceName the Time Series source name.
	 * @param rights the Time Series rights.
	 * @param dimension the Time Series dimension.
	 * @param headerLabels the Time Series headers label.
	 * @param compressedCSV the Time Series csv compressed representation (with labels and UTF-8 encoded).
	 * @param destinationfolderId the time series destination folder.
	 * @return the created Time Series.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 * @throws WorkspaceFolderNotFoundException  if the destination folder has not been found.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public TimeSeries createTimeSeries(String name, String description, String timeseriesId, String title, String creator, String timeseriesDescription,
			String timeseriesCreationDate, String publisher, String sourceId, String sourceName, String rights, long dimension, 
			List<String> headerLabels, InputStream compressedCSV,
			String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WorkspaceFolderNotFoundException, WrongDestinationException;


	/**
	 * Remove an item.
	 * @param itemId the item to remove.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 */
	public void removeItem(String itemId) throws ItemNotFoundException, InternalErrorException, InsufficientPrivilegesException;

	/**
	 * Move a workspaceItem to a specified destination.
	 * @param itemId the item to move.
	 * @param destinationFolderId the destination folder. 
	 * @return 
	 * @throws ItemNotFoundException if the specified item has not been found.
	 * @throws WrongDestinationException if the specified destination has not been found.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if the destination folder have a children with same name.
	 * @throws WorkspaceFolderNotFoundException if the destination folder is not found.
	 */
	public WorkspaceItem moveItem(String itemId, String destinationFolderId) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WorkspaceFolderNotFoundException;

	/**
	 * Rename an item.
	 * @param itemId the item id.
	 * @param newName the new name.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if the user don't have sufficient privileges to perform this operation.
	 * @throws InsufficientPrivilegesException 
	 */
	public void renameItem(String itemId, String newName) throws ItemNotFoundException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException;

	/**
	 * Change an item description.
	 * @param itemId the item to update.
	 * @param newDescription the new item description.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void changeDescription(String itemId, String newDescription) throws ItemNotFoundException, InternalErrorException;

	/**
	 * Return the item with the specified id.
	 * @param itemId the item id.
	 * @return the item.
	 * @throws ItemNotFoundException if the item has not been found.
	 */
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException;

	/**
	 * Return the item with the specified path.
	 * @param path the item path.
	 * @return the item.
	 * @throws ItemNotFoundException if the item has not been found.
	 */
	public WorkspaceItem getItemByPath(String path) throws ItemNotFoundException;
	/**
	 * Return all capabilities associated with the item.
	 * @param itemId the item to retrieve.
	 * @return the capabilities.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public Capabilities getCapabilities(String itemId) throws ItemNotFoundException, InternalErrorException; 

	/**
	 * Remove an item from a folder.
	 * @param childId the item to remove.
	 * @param folderId the folder.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WrongParentTypeException if the specified folder is neither a workspace nor a folder.
	 */
	public void removeChild(String childId, String folderId) throws ItemNotFoundException, InternalErrorException, InsufficientPrivilegesException, WrongParentTypeException;

	/**
	 * Remove an item from a folder.
	 * @param itemName the item name.
	 * @param folderId the folder id.
	 * @throws ItemNotFoundException if the folder has not been found.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WrongItemTypeException if the specified folder is neither a workspace nor a folder. 
	 */
	public void remove(String itemName, String folderId) throws ItemNotFoundException, InternalErrorException, InsufficientPrivilegesException, WrongItemTypeException;

	/**
	 * Copy an item from a folder to another folder.
	 * @param itemId the item to copy.
	 * @param newName the item new name.
	 * @param destinationFolderId the destination folder id.
	 * @return the item copy.
	 * @throws ItemNotFoundException if the item has not been found.
	 * @throws WrongDestinationException if the destination have a wrong type.
	 * @throws ItemAlreadyExistException if an item with same name already exist in the destination folder.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 */
	public WorkspaceItem copy(String itemId, String newName, String destinationFolderId) throws ItemNotFoundException, WrongDestinationException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException;

	/**
	 * Copy an item from a folder to another folder. The item copy have the same name of the original.
	 * @param itemId the item to copy.
	 * @param destinationFolderId the destination folder id, can't be the same of the item (can't have the same name).
	 * @return the item copy.
	 * @throws ItemNotFoundException if the item has not been found. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation. 
	 * @throws ItemAlreadyExistException if an item with same name already exist in the destination folder. 
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws WrongDestinationException if the destination have a wrong type. 
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 */
	public WorkspaceItem copy(String itemId, String destinationFolderId) throws ItemNotFoundException, WrongDestinationException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException;

	/**
	 * Clone an item in the same folder.
	 * This is a particular version of copy method where the destination folder is the same of original item. 
	 * @param itemId the item to clone.
	 * @param cloneName the clone name.
	 * @return the clone.
	 * @throws ItemNotFoundException if the item has not been found. 
	 * @throws ItemAlreadyExistException if an item with same name already exist in the destination folder. 
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws WrongDestinationException if the destination have a wrong type. 
	 * @throws WorkspaceFolderNotFoundException if the destination folder has not been found.
	 */
	public WorkspaceItem cloneItem(String itemId, String cloneName) throws ItemNotFoundException, ItemAlreadyExistException, InsufficientPrivilegesException, InternalErrorException, WrongDestinationException, WorkspaceFolderNotFoundException;

	/**
	 * Check if an item with the specified name exists in the specified folder.
	 * @param name the name to check.
	 * @param folderId the folder where to search the item.
	 * @return <code>true</code> if the item exists, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ItemNotFoundException if the folder has not been found. 
	 * @throws WrongItemTypeException if the folderId referrer to an item with type different from Workspace or folder.
	 */
	public boolean exists(String name, String folderId) throws InternalErrorException, ItemNotFoundException, WrongItemTypeException;

	/**
	 * Check if an item with the specified id exists.
	 * @param itemId the item id to check.
	 * @return <code>true</code> if the item exists, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public boolean exists(String itemId) throws InternalErrorException;

	/**
	 * Get an item with the specified name in the specified folder.
	 * @param name the item name to find.
	 * @param folderId the folder where to search the item.
	 * @return the item if the item is found, <code>null</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws ItemNotFoundException if the folder has not been found. 
	 * @throws WrongItemTypeException if the folderId referrer to an item with type different from Workspace or folder.
	 */
	public WorkspaceItem find(String name, String folderId) throws InternalErrorException, ItemNotFoundException, WrongItemTypeException;


	/**
	 * Find an item using the specified path.
	 * @param path the item path.
	 * @return the item if the item is found, <code>null</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public WorkspaceItem find(String path) throws InternalErrorException;


	/**
	 * Search by Name
	 * @param name
	 * @return a list of SearchItem
	 * @throws InternalErrorException
	 */
	public List<SearchItem> searchByName(String name, String folderId) throws InternalErrorException;

	/**
	 * Search By MimeType
	 * @param mimeType
	 * @returnSearchIte a list of SearchFolderItem
	 * @throws InternalErrorException
	 */
	public List<SearchFolderItem> searchByMimeType(String mimeType)
			throws InternalErrorException;

	/**
	 * Get items by type
	 * @param type
	 * @return a list of SearchItem 
	 * @throws InternalErrorException
	 */
	public List<SearchItem> getFolderItems(GenericItemType type) throws InternalErrorException;

	/**
	 * Check if the specified name is a valid name.
	 * @param name the name to check.
	 * @return <code>true</code> if the name is valid, <code>false</code> otherwise.
	 */
	public boolean isValidName(String name);

	/**
	 * Create a new folderBulkCreator for the specified folder.
	 * @param folderId the target folder.
	 * @return a new folderBulkCreator.
	 * @throws WorkspaceFolderNotFoundException if the target folder has not been found.
	 * @throws WrongItemTypeException if the specified target is not a folder.
	 * @throws InternalErrorException if an internal error occurs. 
	 */
	public FolderBulkCreator getNewFolderBulkCreator(String folderId) throws WorkspaceFolderNotFoundException, WrongItemTypeException, InternalErrorException;

	/**
	 * Return this workspace folder bulk creator manager.
	 * @return the manager.
	 */
	public FolderBulkCreatorManager getFolderBulkCreatorManager();

	/**
	 * SendRequest manager.
	 * @return the manager.
	 */
	public WorkspaceMessageManager getWorkspaceMessageManager();

	/**
	 * Decompose the specified AquaMaps item.
	 * @param itemId the aquamaps item id.
	 * @param folderName the destination folder name.
	 * @param destinationWorkspaceId the destination workspace name.
	 * @return the created folder-
	 * @throws WrongItemTypeException if the specified item is not a GPod item.
	 * @throws WorkspaceFolderNotFoundException if the specified workspace is not found.
	 * @throws WrongDestinationException if the specified target is not a workspace.
	 * @throws InternalErrorException if an internal error occurs. 
	 * @throws ItemAlreadyExistException if a item with that name already exists into the specified workspace.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation. 
	 * @throws ItemNotFoundException if the aquamaps item has not found.
	 */
	public WorkspaceFolder decomposeAquaMapsItem(String itemId, String folderName, String destinationWorkspaceId) throws WrongItemTypeException, WorkspaceFolderNotFoundException, WrongDestinationException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, ItemNotFoundException;

	/**
	 * Get Folder Items
	 * @param types
	 * @return a list of SearchItem
	 * @throws InternalErrorException
	 */
	public List<SearchItem> getFolderItems(GenericItemType... types)
			throws InternalErrorException;


	/**
	 * Get WebDav url
	 * @return the WebDav url
	 * @throws InternalErrorException
	 */
	public String getUrlWebDav() throws InternalErrorException;



	/**
	 * Create a shared folder with a list of users
	 * @param name
	 * @param description
	 * @param users. A list of portal logins.
	 * @param destinationFolderId
	 * @return the shared folder
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	public WorkspaceSharedFolder createSharedFolder(String name, String description,
			List<String> users, String destinationFolderId)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Create a shared folder associated with a groupId 
	 * @param name the name of the folder
	 * @param description
	 * @param groupId: an existing groupId to associate with the folder
	 * @param destinationFolderId (not used)
	 * @param diplayName a friendly name for the folder
	 * @param isVREFolder a flag to indicate the folder is a VRE Folder
	 * @return the shared folder
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	public WorkspaceSharedFolder createSharedFolder(String name, String description,
			String groupId, String destinationFolderId, String displayName, boolean isVREFolder)
					throws InternalErrorException, InsufficientPrivilegesException,
					ItemAlreadyExistException, WrongDestinationException,
					ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Shared an exist {@link WorkspaceFolder} with a list of users
	 * @param users. A list of portal logins. 
	 * @param destinationFolderId
	 * @return the shared folder
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	public WorkspaceSharedFolder shareFolder(List<String> users, String destinationFolderId)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException, WrongDestinationException,
			ItemNotFoundException, WorkspaceFolderNotFoundException;


	/**
	 * Shared an exist {@link WorkspaceFolder} with a list of users
	 * @param users. A list of portal logins. 
	 * @param destinationFolderId
	 * @return the shared folder
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	public WorkspaceSharedFolder share(List<String> users, String itemId)
			throws InternalErrorException, InsufficientPrivilegesException,
			ItemAlreadyExistException, WrongDestinationException,
			ItemNotFoundException, WorkspaceFolderNotFoundException;

	/**
	 * Update an item
	 * @param itemId
	 * @param fileData
	 * @throws InsufficientPrivilegesException
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 * @throws ItemNotFoundException 
	 */
	void updateItem(String itemId, InputStream fileData)
			throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException;

	/**
	 * Create a GCubeItem
	 * @param name
	 * @param description
	 * @param scopes
	 * @param creator
	 * @param itemType
	 * @param destinationFolderId
	 * @return a GCubeItem
	 */
	public WorkspaceItem createGcubeItem(String name, String description,
			List<String> scopes, String creator, String itemType, Map<String, String> properties, 
			String destinationFolderId) throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException;

	/**
	 * Unshare a shared item
	 * @param itemId
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 */
	public WorkspaceItem unshare(String itemId) throws InternalErrorException, ItemNotFoundException;

	/**
	 * Get Trash Folder
	 * @return the trash folder
	 * @throws ItemNotFoundException 
	 * @throws RepositoryException 
	 */
	public WorkspaceTrashFolder getTrash() throws InternalErrorException, ItemNotFoundException;

	/**
	 * Get MySpecialFolders
	 * @return
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 */
	public WorkspaceFolder getMySpecialFolders() throws InternalErrorException,
	ItemNotFoundException;

	/**
	 * Advanced Search
	 * @param name
	 * @param date
	 * @param size
	 * @return a list of SearchItem
	 * @throws InternalErrorException
	 */
	public List<SearchItem> advancedSearch(String name, SearchItemByOperator date,
			SearchItemByOperator size) throws InternalErrorException;

	/**
	 * Search By Properties
	 * @param properties
	 * @return a list of WorkspaceItem
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> searchByProperties(List<String> properties)
			throws InternalErrorException;

	/**
	 * Search FullText
	 * @param text
	 * @return a list of SearchFolderItem
	 * @throws InternalErrorException
	 */
	public List<SearchFolderItem> searchFullText(String text)
			throws InternalErrorException;

	/**
	 * Get VRE Folder By Scope
	 * @param scope
	 * @return the scope
	 */
	public WorkspaceSharedFolder getVREFolderByScope(String scope) throws ItemNotFoundException, InternalErrorException;

	/**
	 * Create an external image
	 * @param name
	 * @param description
	 * @param mimeType
	 * @param imageData
	 * @param destinationFolderId
	 * @param tmpFile
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 */
	ExternalImage createExternalImage(String name, String description,
			String mimeType, String destinationFolderId,
			File tmpFile) throws InsufficientPrivilegesException,
			WorkspaceFolderNotFoundException, InternalErrorException,
			ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Create an External Url
	 * @param name
	 * @param description
	 * @param tmpFile
	 * @param destinationFolderId
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 */
	ExternalUrl createExternalUrl(String name, String description,
			File tmpFile, String destinationFolderId)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Create an external file
	 * @param name
	 * @param description
	 * @param mimeType
	 * @param tmpFile
	 * @param destinationFolderId
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws ItemAlreadyExistException
	 * @throws WrongDestinationException
	 */
	ExternalFile createExternalFile(String name, String description,
			String mimeType, File tmpFile, String destinationFolderId)
					throws InsufficientPrivilegesException,
					WorkspaceFolderNotFoundException, InternalErrorException,
					ItemAlreadyExistException, WrongDestinationException;

	/**
	 * Creates a Workflow Report.
	 * @param name the workflow Report name.
	 * @param description the workflow Report description.
	 * @param workflowId the workflow id.
	 * @param workflowStatus the workflow status.
	 * @param workflowData the workflow data.
	 * @param destinationfolderId the Workflow Report destination folder.
	 * @return the created Workflow Report.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 * @throws WorkspaceFolderNotFoundException  if the destination folder has not been found.
	 * @throws WrongDestinationException if the destination type is not a folder.
	 */
	public WorkflowReport createWorkflowReport(String name, String description, String workflowId, String workflowStatus, String workflowData, String destinationfolderId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, WorkspaceFolderNotFoundException, WrongDestinationException;

	/**
	 * Get the disk usage of a worskpace
	 * @return the disk usage
	 * @throws InternalErrorException
	 */
	long getDiskUsage() throws InternalErrorException;

	/**
	 * Get Total Items in a workspace
	 * @return the numer of total Items
	 * @throws InternalErrorException
	 */
	int getTotalItems() throws InternalErrorException;

	/**
	 * Search GCubeItems
	 * @param query
	 * @return a list of GCubeItem
	 * @throws InternalErrorException
	 */
	List<GCubeItem> searchGCubeItems(SearchQuery query)
			throws InternalErrorException;

	/**
	 * Get Application Area
	 * @return Application Area Folder
	 * @throws InternalErrorException
	 */
	public WorkspaceFolder getApplicationArea() throws InternalErrorException;


	/**
	 * Create a link of a given item in a destination folder
	 * @param itemId
	 * @param destinationFolderId
	 * @return the link
	 * @throws InternalErrorException 
	 * @throws  
	 */
	WorkspaceItem createReference(String itemId, String destinationFolderId) throws InternalErrorException;

	/**
	 * Remove a list of items identified by ids
	 * @param ids
	 * @return a map of errors: Map<id, error>
	 * @throws ItemNotFoundException
	 * @throws InternalErrorException
	 * @throws InsufficientPrivilegesException
	 */
	Map<String, String> removeItems(String... id) throws ItemNotFoundException,
			InternalErrorException, InsufficientPrivilegesException;


}
