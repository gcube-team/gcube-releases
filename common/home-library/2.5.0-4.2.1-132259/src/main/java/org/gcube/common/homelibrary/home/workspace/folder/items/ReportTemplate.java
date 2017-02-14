/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

import java.io.InputStream;
import java.util.Calendar;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */

public interface ReportTemplate extends FolderItem {
	
	/**
	 * Return the template file data.
	 * @return the data.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	InputStream getData() throws InternalErrorException;
	
	/**
	 * Returns the template creation time.
	 * @return the creation time.
	 */
	public Calendar getCreated();

	/**
	 * Returns the last edit time.
	 * @return the lastEdit
	 */
	public Calendar getLastEdit();

	/**
	 * Returns the template author.
	 * @return the author
	 */
	public String getAuthor();

	/**
	 * Returns the last template editor.
	 * @return the lastEditBy
	 */
	public String getLastEditBy();

	/**
	 * Returns the number of sections.
	 * @return the numberOfSections
	 */
	public int getNumberOfSections();

	/**
	 * Returns the template status.
	 * @return the status
	 */
	public String getStatus();

}
