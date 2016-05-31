package org.gcube.portlets.admin.accountingmanager.shared.data.response;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDefinition;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesStorage extends SeriesResponse {

	private static final long serialVersionUID = 4519497775591158592L;
	private SeriesStorageDefinition seriesStorageDefinition;

	public SeriesStorage() {
		super();
	}

	public SeriesStorage(SeriesStorageDefinition seriesStorageDefinition) {
		super();
		this.seriesStorageDefinition = seriesStorageDefinition;
	}

	public SeriesStorageDefinition getSeriesStorageDefinition() {
		return seriesStorageDefinition;
	}

	public void setSeriesStorageDefinition(
			SeriesStorageDefinition seriesStorageDefinition) {
		this.seriesStorageDefinition = seriesStorageDefinition;
	}

	@Override
	public String toString() {
		return "SeriesStorage [seriesStorageDefinition="
				+ seriesStorageDefinition + "]";
	}

}
