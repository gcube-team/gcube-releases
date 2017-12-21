package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesPortletContext extends SeriesPortletDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private Context context;
	private ArrayList<SeriesPortletDataContext> seriesPortletDataContextList;

	public SeriesPortletContext() {
		super();
		this.chartType = ChartType.Context;
	}

	public SeriesPortletContext(Context context,
			ArrayList<SeriesPortletDataContext> seriesPortletDataContextList) {
		super();
		this.chartType = ChartType.Context;
		this.context = context;
		this.seriesPortletDataContextList = seriesPortletDataContextList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<SeriesPortletDataContext> getSeriesPortletDataContextList() {
		return seriesPortletDataContextList;
	}

	public void setSeriesPortletDataContextList(
			ArrayList<SeriesPortletDataContext> seriesPortletDataContextList) {
		this.seriesPortletDataContextList = seriesPortletDataContextList;
	}

	@Override
	public String toString() {
		return "SeriesPortletContext [context=" + context
				+ ", seriesPortletDataContextList="
				+ seriesPortletDataContextList + "]";
	}

}
