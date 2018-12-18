/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import java.io.InputStream;


/**
 * The Interface ItemStreamDescriptor.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 4, 2018
 */
public interface ItemStreamDescriptor {

	/**
	 * Gets the stream.
	 *
	 * @return the stream
	 */
	public InputStream getStream();


	/**
	 * Gets the item name.
	 * It can be a file or a folder
	 *
	 * @return the item name
	 */
	public String getItemName();


	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public Long getSize();


	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType();


}
