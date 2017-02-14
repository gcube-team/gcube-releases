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
	private Boolean showOthers;
	private Integer topNumber;
	private ArrayList<SeriesServiceDataTop> seriesServiceDataTopList;

	public SeriesServiceTop() {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = false;
		this.topNumber = 5;
		
	}

	public SeriesServiceTop(Boolean showOthers, Integer topNumber,
			ArrayList<SeriesServiceDataTop> seriesServiceDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.seriesServiceDataTopList = seriesServiceDataTopList;
	}

	public Boolean getShowOthers() {
		return showOthers;
	}

	public void setShowOthers(Boolean showOthers) {
		this.showOthers = showOthers;
	}

	public Integer getTopNumber() {
		return topNumber;
	}

	public void setTopNumber(Integer topNumber) {
		this.topNumber = topNumber;
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
		return "SeriesServiceTop [showOthers=" + showOthers + ", topNumber="
				+ topNumber + ", seriesServiceDataTopList="
				+ seriesServiceDataTopList + "]";
	}

}
