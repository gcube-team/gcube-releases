/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

import java.io.InputStream;


import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface ExternalFile extends FolderItem, File {
	
	/**
	 * @param data
	 * @throws InternalErrorException
	 */
	void setData(InputStream data) throws InternalErrorException;


}
