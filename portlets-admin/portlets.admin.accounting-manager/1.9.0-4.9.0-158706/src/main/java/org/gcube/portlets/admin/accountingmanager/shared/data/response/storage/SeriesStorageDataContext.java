package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesStorageDataContext implements Serializable {

	private static final long serialVersionUID = -627227653308818605L;
	private String context;
	private ArrayList<SeriesStorageData> series;

	public SeriesStorageDataContext() {
		super();
	}

	public SeriesStorageDataContext(String context,
			ArrayList<SeriesStorageData> series) {
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

	public ArrayList<SeriesStorageData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesStorageData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesStorageDataContext [context=" + context + ", series="
				+ series + "]";
	}

}
