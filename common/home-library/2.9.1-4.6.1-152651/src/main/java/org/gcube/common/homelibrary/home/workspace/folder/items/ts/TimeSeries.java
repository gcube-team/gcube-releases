/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items.ts;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */

public interface TimeSeries extends FolderItem {

	/**
	 * Return this Time Series number of columns.
	 * @return the number of columns.
	 */
	public int getNumberOfColumns();
	
	/**
	 * Return this TimeSeries info.
	 * @return the Time Series info.
	 */
	public TimeSeriesInfo getTimeSeriesInfo();
	
	/**
	 * Return the Time Series header labels.
	 * @return a list of labels.
	 */
	public List<String> getHeaderLabels();
	
	/**
	 * Return the Time Series as csv stream.
	 * @return the csv stream.
	 * @throws InternalErrorException if an error occurs.
	 */
	public InputStream getData() throws InternalErrorException;
	
	/**
	 * Return the Time Series as compressed csv stream.
	 * @return the compressed csv stream.
	 * @throws InternalErrorException if an error occurs.
	 */
	public InputStream getCompressedData() throws InternalErrorException;

}
