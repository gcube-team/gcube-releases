package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesStorageTop extends SeriesStorageDefinition {

	private static final long serialVersionUID = -5477545972037227361L;
	private ArrayList<SeriesStorageDataTop> seriesStorageDataTopList;

	public SeriesStorageTop() {
		super();
		this.chartType = ChartType.Top;
	}

	public SeriesStorageTop(
			ArrayList<SeriesStorageDataTop> seriesStorageDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.seriesStorageDataTopList = seriesStorageDataTopList;
	}

	public ArrayList<SeriesStorageDataTop> getSeriesStorageDataTopList() {
		return seriesStorageDataTopList;
	}

	public void setSeriesStorageDataTopList(
			ArrayList<SeriesStorageDataTop> seriesStorageDataTopList) {
		this.seriesStorageDataTopList = seriesStorageDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesStorageTop [seriesStorageDataTopList="
				+ seriesStorageDataTopList + "]";
	}

	

}
