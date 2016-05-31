package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesPortletTop extends SeriesPortletDefinition {

	private static final long serialVersionUID = -2350334263342186590L;
	private ArrayList<SeriesPortletDataTop> seriesPortletDataTopList;

	public SeriesPortletTop() {
		super();
		this.chartType = ChartType.Top;
	}

	public SeriesPortletTop(
			ArrayList<SeriesPortletDataTop> seriesPortletDataTopList) {
		super();
		this.chartType = ChartType.Top;
		this.seriesPortletDataTopList = seriesPortletDataTopList;
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
		return "SeriesPortletTop [seriesPortletDataTopList="
				+ seriesPortletDataTopList + "]";
	}

}
