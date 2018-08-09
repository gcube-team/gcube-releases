package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;

/**
 * Series Response Specification
 * 
  * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SeriesResponseSpec {
	private SeriesResponse sr;
	private ArrayList<SeriesResponse> srs;

	public SeriesResponse getSr() {
		return sr;
	}

	public void setSr(SeriesResponse sr) {
		this.sr = sr;
	}

	public ArrayList<SeriesResponse> getSrs() {
		return srs;
	}

	public void setSrs(ArrayList<SeriesResponse> srs) {
		this.srs = srs;
	}

}
