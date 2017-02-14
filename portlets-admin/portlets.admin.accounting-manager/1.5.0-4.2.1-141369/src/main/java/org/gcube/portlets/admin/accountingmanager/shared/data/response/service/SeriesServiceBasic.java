package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesServiceBasic extends SeriesServiceDefinition {

	private static final long serialVersionUID = 6108185252722251397L;
	private ArrayList<SeriesServiceData> series;

	public SeriesServiceBasic() {
		super();
		chartType = ChartType.Basic;
	}

	public SeriesServiceBasic(ArrayList<SeriesServiceData> series) {
		super();
		chartType = ChartType.Basic;
		this.series = series;
	}

	public ArrayList<SeriesServiceData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesServiceData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesServiceBasic [series=" + series + "]";
	}

}
