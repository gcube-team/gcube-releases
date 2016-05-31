package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesServiceTop extends SeriesServiceDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private ArrayList<SeriesServiceDataTop> seriesServiceDataTopList;

	public SeriesServiceTop() {
		super();
		this.chartType = ChartType.Top;
	}

	public SeriesServiceTop(ArrayList<SeriesServiceDataTop> seriesServiceDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.seriesServiceDataTopList = seriesServiceDataTopList;
	}

	public ArrayList<SeriesServiceDataTop> getSeriesServiceDataTopList() {
		return seriesServiceDataTopList;
	}

	public void setSeriesServiceDataTopList(
			ArrayList<SeriesServiceDataTop> seriesServiceDataTopList) {
		this.seriesServiceDataTopList = seriesServiceDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesServiceTop [seriesServiceDataTopList=" + seriesServiceDataTopList
				+ "]";
	}

}
