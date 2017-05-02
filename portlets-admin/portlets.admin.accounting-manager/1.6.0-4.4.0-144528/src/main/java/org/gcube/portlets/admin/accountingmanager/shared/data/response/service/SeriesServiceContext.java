package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesServiceContext extends SeriesServiceDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private Context context;
	private ArrayList<SeriesServiceDataContext> seriesServiceDataContextList;

	public SeriesServiceContext() {
		super();
		this.chartType = ChartType.Context;

	}

	public SeriesServiceContext(Context context,
			ArrayList<SeriesServiceDataContext> seriesServiceDataContextList) {
		super();
		this.chartType = ChartType.Context;
		this.context = context;
		this.seriesServiceDataContextList = seriesServiceDataContextList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<SeriesServiceDataContext> getSeriesServiceDataContextList() {
		return seriesServiceDataContextList;
	}

	public void setSeriesServiceDataContextList(
			ArrayList<SeriesServiceDataContext> seriesServiceDataContextList) {
		this.seriesServiceDataContextList = seriesServiceDataContextList;
	}

	@Override
	public String toString() {
		return "SeriesServiceContext [context=" + context
				+ ", seriesServiceDataContextList="
				+ seriesServiceDataContextList + "]";
	}

}
