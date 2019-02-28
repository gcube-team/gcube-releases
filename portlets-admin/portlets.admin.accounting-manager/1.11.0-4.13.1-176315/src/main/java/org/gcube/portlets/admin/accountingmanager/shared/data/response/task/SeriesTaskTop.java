package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesTaskTop extends SeriesTaskDefinition {

	private static final long serialVersionUID = 6805210072384752359L;
	private Boolean showOthers;
	private Integer topNumber;
	private ArrayList<SeriesTaskDataTop> seriesTaskDataTopList;

	public SeriesTaskTop() {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = false;
		this.topNumber = 5;

	}

	public SeriesTaskTop(Boolean showOthers, Integer topNumber,
			ArrayList<SeriesTaskDataTop> seriesTaskDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.seriesTaskDataTopList = seriesTaskDataTopList;
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

	public ArrayList<SeriesTaskDataTop> getSeriesTaskDataTopList() {
		return seriesTaskDataTopList;
	}

	public void setSeriesTaskDataTopList(
			ArrayList<SeriesTaskDataTop> seriesTaskDataTopList) {
		this.seriesTaskDataTopList = seriesTaskDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesTaskTop [showOthers=" + showOthers + ", topNumber="
				+ topNumber + ", seriesTaskDataTopList="
				+ seriesTaskDataTopList + "]";
	}

}
