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
public interface Image extends File {

	/**
	 * The image width.
	 * @return the width.
	 */
	public int getWidth();
	
	/**
	 * The image height.
	 * @return the height.
	 */
	public int getHeight();
	
	/**
	 * The image thumbnail.
	 * @return the thumbnail.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public InputStream getThumbnail() throws InternalErrorException;
	
	/**
	 * The image thumbanil width.
	 * @return the thumbnail width.
	 */
	public int getThumbnailWidth();
	
	/**
	 * The image thumbnail height.
	 * @return the thumbnail height.
	 */
	public int getThumbnailHeight();
	
//	/**
//	 * The image thumbnail length.
//	 * @return the thumbnail length.
//	 * @throws InternalErrorException if an internal error occurs. 
//	 */
//	public long getThumbnailLength() throws InternalErrorException;
	
}
