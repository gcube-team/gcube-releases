package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesPortletDataContext implements Serializable {

	private static final long serialVersionUID = 6043106605633429465L;
	private String context;
	private ArrayList<SeriesPortletData> series;

	public SeriesPortletDataContext() {
		super();
	}

	public SeriesPortletDataContext(String context,
			ArrayList<SeriesPortletData> series) {
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

	public ArrayList<SeriesPortletData> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<SeriesPortletData> series) {
		this.series = series;
	}

	@Override
	public String toString() {
		return "SeriesPortletDataContext [context=" + context + ", series="
				+ series + "]";
	}

}
