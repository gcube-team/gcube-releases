/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

/**
 * The Interface LoaderTdColumnData.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 25, 2015
 */
public interface LoaderTimeDimensionColumnData {

	/**
	 * Load.
	 *
	 * @param list the list
	 * @return true, if successful
	 */
	public boolean loadTimeDimensionColumns(List<TdColumnData> list);
	

	/**
	 * Load other columns.
	 *
	 * @param list the list
	 * @return true, if successful
	 */
	public boolean loadOtherColumns(List<TdColumnData> list);
}
