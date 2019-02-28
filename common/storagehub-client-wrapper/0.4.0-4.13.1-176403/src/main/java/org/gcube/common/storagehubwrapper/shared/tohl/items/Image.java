/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import java.io.InputStream;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 * The Interface Image.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface Image extends File {

	/**
	 * The image width.
	 * @return the width.
	 */
	public Long getWidth();

	/**
	 * The image height.
	 * @return the height.
	 */
	public Long getHeight();

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
	public Long getThumbnailWidth();

	/**
	 * The image thumbnail height.
	 * @return the thumbnail height.
	 */
	public Long getThumbnailHeight();


}
