package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesStorageBasic extends SeriesStorageDefinition {

	private static final long serialVersionUID = -5477545972037227361L;
	private ArrayList<SeriesStorageData> series;

	public SeriesStorageBasic() {
		super();
		chartType = ChartType.Basic;
	}

	public SeriesStorageBasic(ArrayList<SeriesStorageData> series) {
		super();
		chartType = ChartType.Basic;
		this.series = series;
	}

	public ArrayList<SeriesStorageData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesStorageData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesStorageBasic [series=" + series + "]";
	}

}
