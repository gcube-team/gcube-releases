/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;

/**
 * The Interface File.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface File {


	/**
	 * Gets the folder item type.
	 *
	 * @return the folder item type
	 */
	public FileItemType getFileItemType();


	/**
	 * Gets the size.
	 *
	 * @return the size
	 * @throws InternalErrorException the internal error exception
	 */
	public Long getSize() throws InternalErrorException;

	/**
	 * The folder myme type.
	 *
	 * @return the myme type
	 * @throws InternalErrorException the internal error exception
	 */
	public String getMimeType() throws InternalErrorException;


}
