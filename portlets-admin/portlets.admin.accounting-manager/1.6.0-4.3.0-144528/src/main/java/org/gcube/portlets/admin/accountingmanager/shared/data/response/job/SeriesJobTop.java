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
	private Boolean showOthers;
	private Integer topNumber;

	public SeriesJobTop() {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = false;
		this.topNumber = 5;
	}

	public SeriesJobTop(Boolean showOthers, Integer topNumber,
			ArrayList<SeriesJobDataTop> seriesJobDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.seriesJobDataTopList = seriesJobDataTopList;
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

	public ArrayList<SeriesJobDataTop> getSeriesJobDataTopList() {
		return seriesJobDataTopList;
	}

	public void setSeriesJobDataTopList(
			ArrayList<SeriesJobDataTop> seriesJobDataTopList) {
		this.seriesJobDataTopList = seriesJobDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesJobTop [seriesJobDataTopList=" + seriesJobDataTopList
				+ ", showOthers=" + showOthers + ", topNumber=" + topNumber
				+ "]";
	}

}
