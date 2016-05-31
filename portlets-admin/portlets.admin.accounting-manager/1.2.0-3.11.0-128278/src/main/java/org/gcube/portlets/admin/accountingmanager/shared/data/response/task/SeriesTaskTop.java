package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesTaskTop extends SeriesTaskDefinition {

	private static final long serialVersionUID = 6805210072384752359L;
	private ArrayList<SeriesTaskDataTop> seriesTaskDataTopList;

	public SeriesTaskTop() {
		super();
		this.chartType = ChartType.Top;
	}

	public SeriesTaskTop(ArrayList<SeriesTaskDataTop> seriesTaskDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.seriesTaskDataTopList = seriesTaskDataTopList;
	}

	public ArrayList<SeriesTaskDataTop> getSeriesTaskDataTopList() {
		return seriesTaskDataTopList;
	}

	public void setSeriesTaskDataTopList(
			ArrayList<SeriesTaskDataTop> seriesTaskDataTopList) {
		this.seriesTaskDataTopList = seriesTaskDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesTaskTop [seriesTaskDataTopList=" + seriesTaskDataTopList
				+ "]";
	}

}
