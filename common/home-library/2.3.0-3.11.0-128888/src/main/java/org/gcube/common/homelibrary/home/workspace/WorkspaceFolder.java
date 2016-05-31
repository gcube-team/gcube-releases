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

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderBulkCreator;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceFolder extends WorkspaceItem {
	
	
	
	/**
	 * Get children of the current folder
	 * @return a list of children
	 * @throws InternalErrorException if an error occurs.
	 */
	public List<WorkspaceItem> getChildren() throws InternalErrorException;

	/**
	 * Get hidden children of the current folder
	 * @return a list of hidden children
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> getHiddenChildren() throws InternalErrorException;
	
	/**
	 * Check if an item with the specified name exists.
	 * @param name the name to check.
	 * @return <code>true</code> if the item exists, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public boolean exists(String name) throws InternalErrorException;

	/**
	 * Get an item with the specified name.
	 * @param name the item name to find.
	 * @return the item if is found, <code>null</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public WorkspaceItem find(String name) throws InternalErrorException;

	/**
	 * Create a new folder into this folder.
	 * @param name the folder name.
	 * @param description the folder description.
	 * @return the new folder.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists in this folder.
	 */
	public WorkspaceFolder createFolder(String name, String description) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * Create a new External Image into this folder.
	 * @param name the external image name.
	 * @param description the external image description.
	 * @param mimeType the external image mime type.
	 * @param imageData the external image data.
	 * @return the new external image.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalImage createExternalImageItem(String name, String description, String mimeType, InputStream imageData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a new External Image into this folder.
	 * @param name the external image name.
	 * @param description the external image description.
	 * @param mimeType the external image mime type.
	 * @param imageData the external image data.
	 * @return the new external image.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalImage createExternalImageItem(String name, String description, String mimeType, File imageData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create an External File into this folder.
	 * @param name the external file name.
	 * @param description the external file description.
	 * @param mimeType the external file mime type.
	 * @param fileData the external file data.
	 * @return the new external file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalFile createExternalFileItem(String name, String description, String mimeType, File fileData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	
	/**
	 * Create a new External PDF file into this folder.
	 * @param name the external PDF name.
	 * @param description the external PDF description.
	 * @param mimeType the external PDF mime type.
	 * @param tmpFile the PDF tmpFile
	 * @return the new external PDF.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalPDFFile createExternalPDFFileItem(String name, String description, String mimeType, File tmpFile) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;


	/**
	 * Create an External URL into this folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the URL.
	 * @return the new URL file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 * @throws IOException 
	 */
	public ExternalUrl createExternalUrlItem(String name, String description, File tmpFile) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	
	/**
	 * Create an External File into this folder.
	 * @param name the external file name.
	 * @param description the external file description.
	 * @param mimeType the external file mime type.
	 * @param storageId the storage ID used to save temporarily the file
	 * @return the new external file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public FolderItem createExternalFileItem(String name, String description, String mimeType, String storageId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	
	/**
	 * Create an External File into this folder.
	 * @param name the external file name.
	 * @param description the external file description.
	 * @param mimeType the external file mime type.
	 * @param fileData the external file data.
	 * @return the new external file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalFile createExternalFileItem(String name, String description, String mimeType, InputStream fileData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a new External PDF file into this folder.
	 * @param name the external PDF name.
	 * @param description the external PDF description.
	 * @param mimeType the external PDF mime type.
	 * @param fileData the external PDF data.
	 * @return the new external PDF.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ExternalPDFFile createExternalPDFFileItem(String name, String description, String mimeType, InputStream fileData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;


	/**
	 * Create an External URL into this folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the URL.
	 * @return the new URL file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 * @throws IOException 
	 */
	public ExternalUrl createExternalUrlItem(String name, String description, String url) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create an External URL into this folder.
	 * @param name the external URL name.
	 * @param description the external URL description.
	 * @param url the URL.
	 * @return the new URL file.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 * @throws IOException 
	 */
	public ExternalUrl createExternalUrlItem(String name, String description, InputStream url) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;


	/**
	 * Create a Report Template into this folder.
	 * @param name the template name.
	 * @param description the template description.
	 * @param created the template creation time.
	 * @param lastEdit the last edit time.
	 * @param author the template author.
	 * @param lastEditBy the last template editor.
	 * @param numberOfSections the number of sections.
	 * @param status the template status.
	 * @param templateData the template data.
	 * @return the template.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public ReportTemplate createReportTemplateItem(String name, String description, Calendar created, Calendar lastEdit, String author, String lastEditBy, int numberOfSections, String status, InputStream templateData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a Report into this folder.
	 * @param name the report name.
	 * @param description the report description.
	 * @param created the report creation time.
	 * @param lastEdit the last edit time.
	 * @param author the report author.
	 * @param lastEditBy the last report editor.
	 * @param templateName the source template name.
	 * @param numberOfSections the number of sections.
	 * @param status the report status.
	 * @param reportData the report data.
	 * @return the report.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public Report createReportItem(String name, String description, Calendar created, Calendar lastEdit, String author, String lastEditBy, String templateName, int numberOfSections, 
			String status, InputStream reportData) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a new Query into this folder.
	 * @param name the query name.
	 * @param description the query description.
	 * @param query the query.
	 * @param queryType the query type.
	 * @return the new query.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public Query createQueryItem(String name, String description, String query, QueryType queryType) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a new Query into this folder.
	 * @param name the query name.
	 * @param description the query description.
	 * @param query the query.
	 * @param queryType the query type.
	 * @return the new query.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public Query createQueryItem(String name, String description, InputStream query, QueryType queryType) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

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
	 * @return the created Time Series.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 * @throws ItemAlreadyExistException if an item with the specified name already exists.
	 */
	public TimeSeries createTimeSeries(String name, String description, String timeseriesId, String title, String creator, String timeseriesDescription, String timeseriesCreationDate, 
			String publisher, String sourceId, String sourceName, String rights, long dimension, List<String> headerLabels, InputStream compressedCSV) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;


	/**
	 * Create a new FolderBulkCreator for this folder.
	 * @return the new FolderBulkCreator.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public FolderBulkCreator getNewFolderBulkCreator() throws InternalErrorException;



	/**
	 * @param useers
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws WrongDestinationException
	 * @throws InternalErrorException
	 */
	public WorkspaceSharedFolder share(List<String> users) throws InsufficientPrivilegesException,
	WrongDestinationException, InternalErrorException;

	/**
	 * Set a privilege to a list of users
	 * @param users
	 * @param privilege
	 * @throws InternalErrorException
	 */
	public void setACL(List<String> users, ACLType privilege)
			throws InternalErrorException;

	/**
	 * Get an unique name for an item
	 * @param initialName
	 * @return 
	 * @throws InternalErrorException
	 */
	public String getUniqueName(String initialName, boolean b) throws InternalErrorException;

	/**
	 * Get the size of a folder
	 * @return folder size in bytes
	 * @throws InternalErrorException
	 */
	public long getSize() throws InternalErrorException;

	/**
	 * Get the number of items in a folder
	 * @return the number of items in a folder
	 * @throws InternalErrorException
	 */
	public int getCount() throws InternalErrorException;

	/**
	 * Get items ordered by jcr:lastModified
	 * @param limit: The maximum result size
	 * @return
	 * @throws InternalErrorException
	 */
	public List<WorkspaceItem> getLastItems(int limit)
			throws InternalErrorException;

	/**
	 * Create a generic item
	 * @param name
	 * @param description
	 * @param mimeType
	 * @param is
	 * @return the created item
	 * @throws InternalErrorException 
	 * @throws ItemAlreadyExistException 
	 * @throws InsufficientPrivilegesException 
	 */
	public FolderItem createExternalGenericItem(String name,
			String description, InputStream is) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * Create a generic item with properties by inpustream
	 * @param name
	 * @param description
	 * @param is
	 * @param properties
	 * @return the created item
	 */
	public FolderItem createExternalGenericItem(String name,
			String description, InputStream is, Map<String, String> properties) throws InternalErrorException, InsufficientPrivilegesException, ItemAlreadyExistException;

	/**
	 * Create a generic item with properties by storage id
	 * @param name
	 * @param description
	 * @param mimeType
	 * @param storageId
	 * @param properties
	 * @return the created item
	 */
	public FolderItem createExternalGenericItem(String name, String description, String storageId, Map<String, String> properties) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;

	/**
	 * Create a generic item by storage id
	 * @param name
	 * @param description
	 * @param storageId
	 * @return the created item
	 */
	public FolderItem createExternalGenericItem(String name,
			String description, String storageId) throws InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException;



}
