/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

import java.io.InputStream;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface File {
	
	/**
	 * The file name.
	 * @return the file name.
	 * @throws InternalErrorException if an error occurs.
	 */
	public String getName() throws InternalErrorException;
	
	/**
	 * The file mime type.
	 * @return the f type.
	 */
	public String getMimeType();
	
	/**
	 * The file data.
	 * @return the inputstream representing the data. 
	 * @throws InternalErrorException if an error occurs. 
	 */
	public InputStream getData() throws InternalErrorException;
	
	/**
	 * The public link
	 * @return public link
	 * @throws InternalErrorException if an error occurs. 
	 */
	public String getPublicLink() throws InternalErrorException;
		
	/**
	 * The file length.
	 * @return the length.
	 * @throws InternalErrorException if an error occurs.
	 */
	public long getLength() throws InternalErrorException;

	/**
	 * The hard link
	 * @return hard link
	 * @throws InternalErrorException if an error occurs. 
	 */
	public void getHardLink(String linkName) throws InternalErrorException;

	/**
	 * @return
	 */
	/**
	 * The storage ID
	 * @return the storage ID
	 */
	public String getStorageId() throws InternalErrorException;

//	/**
//	 * @param mimeType
//	 * @param size
//	 * @throws InternalErrorException
//	 */
//	public void updateInfo(java.io.File file) throws InternalErrorException;

}
