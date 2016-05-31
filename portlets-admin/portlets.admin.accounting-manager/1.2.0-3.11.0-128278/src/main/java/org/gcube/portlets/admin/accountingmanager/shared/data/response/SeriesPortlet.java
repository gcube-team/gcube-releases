package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesPortlet extends SeriesResponse {

	private static final long serialVersionUID = -1215710427019099089L;

	private SeriesPortletDefinition seriesPortletDefinition;

	public SeriesPortlet() {
		super();
	}

	public SeriesPortlet(SeriesPortletDefinition seriesPortletDefinition) {
		super();
		this.seriesPortletDefinition = seriesPortletDefinition;
	}

	public SeriesPortletDefinition getSeriesPortletDefinition() {
		return seriesPortletDefinition;
	}

	public void setSeriesPortletDefinition(
			SeriesPortletDefinition seriesPortletDefinition) {
		this.seriesPortletDefinition = seriesPortletDefinition;
	}

	@Override
	public String toString() {
		return "SeriesPortlet [seriesPortletDefinition="
				+ seriesPortletDefinition + "]";
	}

}
