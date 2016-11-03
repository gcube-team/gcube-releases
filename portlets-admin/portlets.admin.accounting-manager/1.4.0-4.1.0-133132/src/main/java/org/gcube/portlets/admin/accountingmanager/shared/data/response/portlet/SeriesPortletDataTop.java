package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesPortletDataTop implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private FilterValue filterValue;
	private ArrayList<SeriesPortletData> series;

	public SeriesPortletDataTop() {
		super();
	}

	public SeriesPortletDataTop(FilterValue filterValue,
			ArrayList<SeriesPortletData> series) {
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

	public ArrayList<SeriesPortletData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesPortletData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesPortletDataTop [filterValue=" + filterValue + ", series="
				+ series + "]";
	}

}
