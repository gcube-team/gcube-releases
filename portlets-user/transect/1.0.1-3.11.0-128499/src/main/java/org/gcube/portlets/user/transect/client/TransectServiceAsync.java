/**
 * 
 */
package org.gcube.portlets.user.transect.client;

import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 18, 2015
 */
public interface TransectServiceAsync {

	void getChartData(
			String scope,
			String x1,
			String y1,
			String x2,
			String y2,
			String SRID,
			String maxelements,
			String minumumGap,
			String biodiversityfield,
			String tablename, AsyncCallback<GraphGroups> callback);
}
