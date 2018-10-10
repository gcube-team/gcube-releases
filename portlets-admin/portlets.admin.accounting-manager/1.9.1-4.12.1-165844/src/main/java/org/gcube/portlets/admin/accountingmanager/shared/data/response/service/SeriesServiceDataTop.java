package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesServiceDataTop implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private FilterValue filterValue;
	private ArrayList<SeriesServiceData> series;

	public SeriesServiceDataTop() {
		super();
	}

	public SeriesServiceDataTop(FilterValue filterValue,
			ArrayList<SeriesServiceData> series) {
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

	public ArrayList<SeriesServiceData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesServiceData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesServiceDataTop [filterValue=" + filterValue + ", series="
				+ series + "]";
	}

}
