package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesJob extends SeriesResponse {

	private static final long serialVersionUID = 8054723198014713937L;
	private SeriesJobDefinition seriesJobDefinition;

	public SeriesJob() {
		super();
	}

	public SeriesJob(SeriesJobDefinition seriesJobDefinition) {
		super();
		this.seriesJobDefinition = seriesJobDefinition;
	}

	public SeriesJobDefinition getSeriesJobDefinition() {
		return seriesJobDefinition;
	}

	public void setSeriesJobDefinition(SeriesJobDefinition seriesJobDefinition) {
		this.seriesJobDefinition = seriesJobDefinition;
	}

	@Override
	public String toString() {
		return "SeriesJob [seriesJobDefinition=" + seriesJobDefinition + "]";
	}

}
