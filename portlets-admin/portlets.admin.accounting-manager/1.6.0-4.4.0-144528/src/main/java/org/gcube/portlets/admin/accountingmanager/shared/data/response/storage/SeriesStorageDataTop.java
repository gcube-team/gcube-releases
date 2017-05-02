package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesStorageDataTop implements Serializable {

	private static final long serialVersionUID = -627227653308818605L;
	private FilterValue filterValue;
	private ArrayList<SeriesStorageData> series;

	public SeriesStorageDataTop() {
		super();
	}

	public SeriesStorageDataTop(FilterValue filterValue,
			ArrayList<SeriesStorageData> series) {
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

	public ArrayList<SeriesStorageData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesStorageData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesStorageDataTop [filterValue=" + filterValue + ", series="
				+ series + "]";
	}

}
