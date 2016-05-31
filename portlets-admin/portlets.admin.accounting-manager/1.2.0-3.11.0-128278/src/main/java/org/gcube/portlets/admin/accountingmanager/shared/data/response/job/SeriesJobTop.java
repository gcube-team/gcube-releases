package org.gcube.portlets.admin.accountingmanager.shared.data.response.job;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJobTop extends SeriesJobDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private ArrayList<SeriesJobDataTop> seriesJobDataTopList;

	public SeriesJobTop() {
		super();
		this.chartType = ChartType.Top;
	}

	public SeriesJobTop(ArrayList<SeriesJobDataTop> seriesJobDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.seriesJobDataTopList = seriesJobDataTopList;
	}

	public ArrayList<SeriesJobDataTop> getSeriesJobDataTopList() {
		return seriesJobDataTopList;
	}

	public void setSeriesJobDataTopList(ArrayList<SeriesJobDataTop> seriesJobDataTopList) {
		this.seriesJobDataTopList = seriesJobDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesJobTop [seriesJobDataTopList=" + seriesJobDataTopList + "]";
	}

}
