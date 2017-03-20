package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesServiceDataContext implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private String context;
	private ArrayList<SeriesServiceData> series;

	public SeriesServiceDataContext() {
		super();
	}

	public SeriesServiceDataContext(String context,
			ArrayList<SeriesServiceData> series) {
		super();
		this.context = context;
		this.series = series;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public ArrayList<SeriesServiceData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesServiceData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesServiceDataContext [context=" + context + ", series="
				+ series + "]";
	}

}
