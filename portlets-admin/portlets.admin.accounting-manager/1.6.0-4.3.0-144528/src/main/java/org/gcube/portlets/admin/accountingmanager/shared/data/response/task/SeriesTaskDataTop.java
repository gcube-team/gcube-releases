package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesTaskDataTop implements Serializable {

	private static final long serialVersionUID = 6799983693606904130L;
	private FilterValue filterValue;
	private ArrayList<SeriesTaskData> series;

	public SeriesTaskDataTop() {
		super();
	}

	public SeriesTaskDataTop(FilterValue filterValue,
			ArrayList<SeriesTaskData> series) {
		super();
		this.filterValue = filterValue;
		this.series = series;
	}

	public FilterValue getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(FilterValue filterValue) {
		this.filterValue = filterValue;
	}

	public ArrayList<SeriesTaskData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesTaskData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesTaskDataTop [filterValue=" + filterValue + ", series="
				+ series + "]";
	}

}
