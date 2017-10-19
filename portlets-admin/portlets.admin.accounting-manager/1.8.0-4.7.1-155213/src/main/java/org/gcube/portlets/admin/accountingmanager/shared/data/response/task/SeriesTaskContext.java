package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesTaskContext extends SeriesTaskDefinition {

	private static final long serialVersionUID = 6805210072384752359L;
	private Context context;
	private ArrayList<SeriesTaskDataContext> seriesTaskDataContextList;

	public SeriesTaskContext() {
		super();
		this.chartType = ChartType.Context;

	}

	public SeriesTaskContext(Context context,
			ArrayList<SeriesTaskDataContext> seriesTaskDataTopList) {
		super();
		this.chartType = ChartType.Context;
		this.context = context;
		this.seriesTaskDataContextList = seriesTaskDataTopList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<SeriesTaskDataContext> getSeriesTaskDataContextList() {
		return seriesTaskDataContextList;
	}

	public void setSeriesTaskDataContextList(
			ArrayList<SeriesTaskDataContext> seriesTaskDataContextList) {
		this.seriesTaskDataContextList = seriesTaskDataContextList;
	}

	@Override
	public String toString() {
		return "SeriesTaskContext [context=" + context
				+ ", seriesTaskDataContextList=" + seriesTaskDataContextList
				+ "]";
	}

}
