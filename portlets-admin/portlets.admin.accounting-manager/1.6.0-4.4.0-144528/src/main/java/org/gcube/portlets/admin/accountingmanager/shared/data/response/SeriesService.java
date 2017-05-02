package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesService extends SeriesResponse {

	private static final long serialVersionUID = -1311805875898959881L;
	private SeriesServiceDefinition serieServiceDefinition;

	public SeriesService() {
		super();
	}

	public SeriesService(SeriesServiceDefinition serieServiceDefinition) {
		super();
		this.serieServiceDefinition = serieServiceDefinition;
	}

	public SeriesServiceDefinition getSerieServiceDefinition() {
		return serieServiceDefinition;
	}

	public void setSerieServiceDefinition(
			SeriesServiceDefinition serieServiceDefinition) {
		this.serieServiceDefinition = serieServiceDefinition;
	}

	@Override
	public String toString() {
		return "SeriesService [serieServiceDefinition="
				+ serieServiceDefinition + "]";
	}

}
