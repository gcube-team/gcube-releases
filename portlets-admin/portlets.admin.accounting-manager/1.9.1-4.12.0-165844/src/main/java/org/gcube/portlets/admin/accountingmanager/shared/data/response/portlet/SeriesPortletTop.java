package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesPortletTop extends SeriesPortletDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private Boolean showOthers;
	private Integer topNumber;
	private ArrayList<SeriesPortletDataTop> seriesPortletDataTopList;

	public SeriesPortletTop() {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = false;
		this.topNumber = 5;
	}

	public SeriesPortletTop(Boolean showOthers, Integer topNumber,
			ArrayList<SeriesPortletDataTop> seriesPortletDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.seriesPortletDataTopList = seriesPortletDataTopList;
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

	public ArrayList<SeriesPortletDataTop> getSeriesPortletDataTopList() {
		return seriesPortletDataTopList;
	}

	public void setSeriesPortletDataTopList(
			ArrayList<SeriesPortletDataTop> seriesPortletDataTopList) {
		this.seriesPortletDataTopList = seriesPortletDataTopList;
	}

	@Override
	public String toString() {
		return "SeriesPortletTop [showOthers=" + showOthers + ", topNumber="
				+ topNumber + ", seriesPortletDataTopList="
				+ seriesPortletDataTopList + "]";
	}

}
