package org.gcube.portlets.admin.accountingmanager.shared.data.response.job;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJobBasic extends SeriesJobDefinition {

	private static final long serialVersionUID = 6108185252722251397L;
	private ArrayList<SeriesJobData> series;

	public SeriesJobBasic() {
		super();
		chartType = ChartType.Basic;
	}

	public SeriesJobBasic(ArrayList<SeriesJobData> series) {
		super();
		chartType = ChartType.Basic;
		this.series = series;
	}

	public ArrayList<SeriesJobData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesJobData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesJobBasic [series=" + series + "]";
	}

}
