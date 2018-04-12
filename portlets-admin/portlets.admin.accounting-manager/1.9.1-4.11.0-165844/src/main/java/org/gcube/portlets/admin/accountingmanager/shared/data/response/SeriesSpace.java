package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.space.SeriesSpaceDefinition;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesSpace extends SeriesResponse {

	private static final long serialVersionUID = -1311805875898959881L;
	private SeriesSpaceDefinition serieSpaceDefinition;

	public SeriesSpace() {
		super();
	}

	public SeriesSpace(SeriesSpaceDefinition serieSpaceDefinition) {
		super();
		this.serieSpaceDefinition = serieSpaceDefinition;
	}

	public SeriesSpaceDefinition getSerieSpaceDefinition() {
		return serieSpaceDefinition;
	}

	public void setSerieSpaceDefinition(
			SeriesSpaceDefinition serieSpaceDefinition) {
		this.serieSpaceDefinition = serieSpaceDefinition;
	}

	@Override
	public String toString() {
		return "SeriesSpace [serieSpaceDefinition=" + serieSpaceDefinition
				+ "]";
	}

}
