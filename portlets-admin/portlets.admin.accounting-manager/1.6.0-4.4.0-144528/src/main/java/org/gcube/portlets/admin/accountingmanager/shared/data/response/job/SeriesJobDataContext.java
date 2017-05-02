package org.gcube.portlets.admin.accountingmanager.shared.data.response.job;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJobDataContext implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private String context;
	private ArrayList<SeriesJobData> series;

	public SeriesJobDataContext() {
		super();
	}

	public SeriesJobDataContext(String context, ArrayList<SeriesJobData> series) {
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

	public ArrayList<SeriesJobData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesJobData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesJobDataContext [context=" + context + ", series="
				+ series + "]";
	}

}
