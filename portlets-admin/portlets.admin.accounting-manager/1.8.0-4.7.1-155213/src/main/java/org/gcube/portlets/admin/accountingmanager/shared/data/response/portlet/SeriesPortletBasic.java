package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesPortletBasic extends SeriesPortletDefinition {

	private static final long serialVersionUID = 6108185252722251397L;
	private ArrayList<SeriesPortletData> series;

	public SeriesPortletBasic() {
		super();
		chartType = ChartType.Basic;
	}

	public SeriesPortletBasic(ArrayList<SeriesPortletData> series) {
		super();
		chartType = ChartType.Basic;
		this.series = series;
	}

	public ArrayList<SeriesPortletData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesPortletData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesPortletBasic [series=" + series + "]";
	}

}
