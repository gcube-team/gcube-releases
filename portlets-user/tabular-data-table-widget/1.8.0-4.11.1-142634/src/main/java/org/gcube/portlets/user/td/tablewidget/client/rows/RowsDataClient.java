package org.gcube.portlets.user.td.tablewidget.client.rows;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RowsDataClient {
	private static final String TDWXROWSDATA = "tdwxrowsdata";

	public void retrievesRowsAsJson() {
		// Send request to server and catch any errors.
		String path = GWT.getModuleBaseURL() + TDWXROWSDATA + "?"
				+ Constants.CURR_GROUP_ID + "="
				+ GCubeClientContext.getCurrentContextId();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, path);
		builder.setHeader(Constants.CURR_GROUP_ID,
				GCubeClientContext.getCurrentContextId());

		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
					} else {
					}
				}
			});
			Log.debug("Request: " + request.toString());
		} catch (RequestException e) {
			e.printStackTrace();
		}

	}
}
