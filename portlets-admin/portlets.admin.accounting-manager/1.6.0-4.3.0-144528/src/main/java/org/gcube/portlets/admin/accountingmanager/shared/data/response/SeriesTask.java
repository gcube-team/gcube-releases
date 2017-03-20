package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesTask extends SeriesResponse {

	private static final long serialVersionUID = -1215710427019099089L;

	private SeriesTaskDefinition seriesTaskDefinition;

	public SeriesTask() {
		super();
	}

	public SeriesTask(SeriesTaskDefinition seriesTaskDefinition) {
		super();
		this.seriesTaskDefinition = seriesTaskDefinition;
	}

	public SeriesTaskDefinition getSeriesTaskDefinition() {
		return seriesTaskDefinition;
	}

	public void setSeriesTaskDefinition(
			SeriesTaskDefinition seriesTaskDefinition) {
		this.seriesTaskDefinition = seriesTaskDefinition;
	}

	@Override
	public String toString() {
		return "SeriesTask [seriesTaskDefinition=" + seriesTaskDefinition + "]";
	}

}
