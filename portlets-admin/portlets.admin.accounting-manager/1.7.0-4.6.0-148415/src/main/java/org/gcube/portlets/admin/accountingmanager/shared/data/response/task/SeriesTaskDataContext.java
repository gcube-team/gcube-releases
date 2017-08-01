package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesTaskDataContext implements Serializable {

	private static final long serialVersionUID = 6799983693606904130L;
	private String context;
	private ArrayList<SeriesTaskData> series;

	public SeriesTaskDataContext() {
		super();
	}

	public SeriesTaskDataContext(String context,
			ArrayList<SeriesTaskData> series) {
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

	public ArrayList<SeriesTaskData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesTaskData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesTaskDataContext [context=" + context + ", series="
				+ series + "]";
	}

}
