/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.util;

import java.util.ArrayList;

import org.gcube.portlets.user.tdwx.shared.FilterInformation;
import org.gcube.portlets.user.tdwx.shared.RequestData;
import org.gcube.portlets.user.tdwx.shared.SortInformation;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.writer.DataWriter;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class PagingLoadUrlEncoder implements
// DataWriter<PagingLoadConfig, String> {

		DataWriter<FilterPagingLoadConfig, String> {

	// protected UrlBuilder urlBuilder = new UrlBuilder();
	protected RequestData requestData;
	protected ArrayList<StaticFilterInformation> staticFilters;
	
	/**
	 * 
	 * @param staticFilters
	 */
	public PagingLoadUrlEncoder(ArrayList<StaticFilterInformation> staticFilters){
		this.staticFilters=staticFilters;
	}

	public String write(FilterPagingLoadConfig config) {

		requestData = new RequestData();

		requestData.setOffset(String.valueOf(config.getOffset()));
		requestData.setLimit(String.valueOf(config.getLimit()));

		ArrayList<SortInformation> sorts = new ArrayList<SortInformation>();

		SortInformation si;
		for (SortInfo info : config.getSortInfo()) {
			si = new SortInformation(info.getSortField(),
					(info.getSortDir() == null) ? null : info.getSortDir()
							.toString());
			sorts.add(si);
		}
		requestData.setSorts(sorts);

		ArrayList<FilterInformation> filters = new ArrayList<FilterInformation>();
		FilterInformation fi;

		for (FilterConfig filterConfig : config.getFilters()) {
			fi = new FilterInformation(filterConfig.getField(),
					filterConfig.getType(), filterConfig.getComparison(),
					filterConfig.getValue());
			filters.add(fi);
		}
		requestData.setFilters(filters);	
		
		if(staticFilters!=null){
			requestData.setStaticFilters(staticFilters);
		}
		
		
		Log.debug("JSON request:"+requestData.toJsonObject());
		return requestData.toJsonObject();

		/*
		 * Log.debug("Offset: " + config.getOffset()); Log.debug("Limit: " +
		 * config.getLimit());
		 * 
		 * urlBuilder.clear();
		 * 
		 * urlBuilder.addParameter(ServletParameters.OFFSET,
		 * String.valueOf(config.getOffset()));
		 * urlBuilder.addParameter(ServletParameters.LIMIT,
		 * String.valueOf(config.getLimit())); if(config.getSortInfo()!=null &&
		 * !config.getSortInfo().isEmpty()){ SortInfo info =
		 * config.getSortInfo().get(0); Log.debug("SortField: " +
		 * info.getSortField()); Log.debug("SortDir: " + info.getSortDir());
		 * urlBuilder.addParameter(ServletParameters.SORTFIELD,
		 * info.getSortField());
		 * urlBuilder.addParameter(ServletParameters.SORTDIR, (info
		 * .getSortDir() == null) ? null : info.getSortDir() .toString()); }
		 */

		/*
		 * for (FilterConfig filterConfig : config.getFilters()) {
		 * Log.debug("FilterField: " + filterConfig.getField());
		 * Log.debug("FilterType: " + filterConfig.getType());
		 * Log.debug("FilterComparison: " + filterConfig.getComparison());
		 * Log.debug("FilterValue: " + filterConfig.getValue());
		 * 
		 * urlBuilder.addParameter(ServletParameters.FILTERFIELD,
		 * filterConfig.getField());
		 * urlBuilder.addParameter(ServletParameters.FILTERTYPE,
		 * filterConfig.getType());
		 * urlBuilder.addParameter(ServletParameters.FILTERCOMPARISON,
		 * filterConfig.getComparison());
		 * urlBuilder.addParameter(ServletParameters.FILTERVALUE,
		 * filterConfig.getValue());
		 * 
		 * }
		 */

		// return urlBuilder.toString();

	}
	
	
	
	
}
