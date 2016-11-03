package org.gcube.portlets.admin.accountingmanager.shared.data.response.job;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJobDataTop implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private FilterValue filterValue;
	private ArrayList<SeriesJobData> series;

	public SeriesJobDataTop() {
		super();
	}

	public SeriesJobDataTop(FilterValue filterValue,
			ArrayList<SeriesJobData> series) {
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

	public ArrayList<SeriesJobData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesJobData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesJobDataTop [filterValue=" + filterValue + ", series="
				+ series + "]";
	}

}
