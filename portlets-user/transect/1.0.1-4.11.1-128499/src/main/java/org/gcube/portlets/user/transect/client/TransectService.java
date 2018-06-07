package org.gcube.portlets.user.transect.client;

import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Transect")
public interface TransectService extends RemoteService {
	GraphGroups getChartData(
			String scope,
			String x1,
			String y1,
			String x2,
			String y2,
			String SRID,
			String maxelements,
			String minumumGap,
			String biodiversityfield,
			String tablename) throws Exception;
}
