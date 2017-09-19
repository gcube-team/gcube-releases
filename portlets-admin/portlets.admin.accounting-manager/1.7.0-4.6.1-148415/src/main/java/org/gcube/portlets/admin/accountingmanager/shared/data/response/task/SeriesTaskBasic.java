package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesTaskBasic extends SeriesTaskDefinition {

	private static final long serialVersionUID = -5327331420724956813L;
	private ArrayList<SeriesTaskData> series;

	public SeriesTaskBasic() {
		super();
		chartType = ChartType.Basic;
	}

	public SeriesTaskBasic(ArrayList<SeriesTaskData> series) {
		super();
		chartType = ChartType.Basic;
		this.series = series;
	}

	public ArrayList<SeriesTaskData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesTaskData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesTaskBasic [series=" + series + "]";
	}

}
