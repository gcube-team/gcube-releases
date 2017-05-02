package org.gcube.portlets.admin.accountingmanager.shared.data.response.job;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJobContext extends SeriesJobDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private Context context;
	private ArrayList<SeriesJobDataContext> seriesJobDataContextList;

	public SeriesJobContext() {
		super();
		this.chartType = ChartType.Context;
	}

	public SeriesJobContext(Context context,
			ArrayList<SeriesJobDataContext> seriesJobDataContextList) {
		super();
		this.chartType = ChartType.Context;
		this.context = context;
		this.seriesJobDataContextList = seriesJobDataContextList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<SeriesJobDataContext> getSeriesJobDataContextList() {
		return seriesJobDataContextList;
	}

	public void setSeriesJobDataContextList(
			ArrayList<SeriesJobDataContext> seriesJobDataContextList) {
		this.seriesJobDataContextList = seriesJobDataContextList;
	}

	@Override
	public String toString() {
		return "SeriesJobContext [context=" + context
				+ ", seriesJobDataContextList=" + seriesJobDataContextList
				+ "]";
	}

}
