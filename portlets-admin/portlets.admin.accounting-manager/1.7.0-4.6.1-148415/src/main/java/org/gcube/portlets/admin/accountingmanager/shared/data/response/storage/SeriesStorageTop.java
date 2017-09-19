package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesStorageTop extends SeriesStorageDefinition {

	private static final long serialVersionUID = -5477545972037227361L;
	private Boolean showOthers;
	private Integer topNumber;
	private ArrayList<SeriesStorageDataTop> seriesStorageDataTopList;

	public SeriesStorageTop() {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = false;
		this.topNumber = 5;
		
	}

	public SeriesStorageTop(Boolean showOthers, Integer topNumber,
			ArrayList<SeriesStorageDataTop> seriesStorageDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.seriesStorageDataTopList = seriesStorageDataTopList;
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

	public ArrayList<SeriesStorageDataTop> getSeriesStorageDataTopList() {
		return seriesStorageDataTopList;
	}

	public void setSeriesStorageDataTopList(
			ArrayList<SeriesStorageDataTop> seriesStorageDataTopList) {
		this.seriesStorageDataTopList = seriesStorageDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesStorageTop [showOthers=" + showOthers + ", topNumber="
				+ topNumber + ", seriesStorageDataTopList="
				+ seriesStorageDataTopList + "]";
	}

}
