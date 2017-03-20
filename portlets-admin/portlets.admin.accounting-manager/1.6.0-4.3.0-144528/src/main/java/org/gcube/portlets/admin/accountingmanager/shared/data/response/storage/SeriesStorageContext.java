package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesStorageContext extends SeriesStorageDefinition {

	private static final long serialVersionUID = -5477545972037227361L;
	private Context context;
	private ArrayList<SeriesStorageDataContext> seriesStorageDataContextList;

	public SeriesStorageContext() {
		super();
		this.chartType = ChartType.Context;

	}

	public SeriesStorageContext(Context context,
			ArrayList<SeriesStorageDataContext> seriesStorageDataContextList) {
		super();
		this.chartType = ChartType.Context;
		this.context=context;
		this.seriesStorageDataContextList = seriesStorageDataContextList;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<SeriesStorageDataContext> getSeriesStorageDataContextList() {
		return seriesStorageDataContextList;
	}

	public void setSeriesStorageDataContextList(
			ArrayList<SeriesStorageDataContext> seriesStorageDataContextList) {
		this.seriesStorageDataContextList = seriesStorageDataContextList;
	}

	@Override
	public String toString() {
		return "SeriesStorageContext [context=" + context
				+ ", seriesStorageDataContextList="
				+ seriesStorageDataContextList + "]";
	}

	
}
