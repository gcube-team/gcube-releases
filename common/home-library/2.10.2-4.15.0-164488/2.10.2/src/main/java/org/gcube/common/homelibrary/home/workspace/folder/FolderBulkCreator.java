/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder;

import java.net.URI;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface FolderBulkCreator {
	
	/**
	 * Return this FolderBulkCreator id.
	 * @return the id.
	 */
	public String getId();

	/**
	 * Create a new metadata into this folder.
	 * @param uri the metadata uri.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void createMetadata(URI uri) throws InsufficientPrivilegesException, InternalErrorException;
	
	/**
	 * Create a new annotation into this folder.
	 * @param uri the annotation uri.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void createAnnotation(URI uri) throws InsufficientPrivilegesException, InternalErrorException;
	
	/**
	 * Create a new document part into this folder. The item name is retrieved from the document name.
	 * @param uri the part uri.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void createDocumentPartItem(URI uri) throws InsufficientPrivilegesException, InternalErrorException;
	
	/**
	 * Create a new document alternative into this folder. The item name is retrieved from the document name.
	 * @param uri the alternative uri.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void createDocumentAlternativeItem(URI uri) throws InsufficientPrivilegesException, InternalErrorException;

	/**
	 * Create a new document into this folder.
	 * @param uri the document uri.
	 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public void createDocumentItem(URI uri) throws InsufficientPrivilegesException, InternalErrorException;

	/**
	 * Create an external url.
	 * @param url the url.
	 * @throws InternalErrorException 
	 */
	public void createExternalUrl(String url) throws InternalErrorException;
	
	/**
	 * Create a new query.
	 * @param name the item name.
	 * @param query the query.
	 * @param queryType the query type.
	 * @throws InternalErrorException 
	 */
	public void createQuery(String name, String query, QueryType queryType) throws InternalErrorException;
	
	/**
	 * Commit all requested changes. 
	 * @throws InternalErrorException  if an internal error occurs.
	 */
	public void commit() throws InternalErrorException;
	
	/**
	 * Return the destination folder where all items will be created.
	 * @return the destination folder.
	 */
	public WorkspaceFolder getDestinationFolder();
	
	/**
	 * Return the number of requests processed by this FolderBulkCreator.
	 * @return the number of requests.
	 * @throws InternalErrorException 
	 */
	public int getNumberOfRequests() throws InternalErrorException;

	/**
	 * @return
	 * @throws InternalErrorException 
	 */
	float getStatus() throws InternalErrorException;

	/**
	 * @throws InternalErrorException
	 */
	void remove() throws InternalErrorException;

	/**
	 * @return
	 * @throws InternalErrorException
	 */
	int getFailures() throws InternalErrorException;
	
}
