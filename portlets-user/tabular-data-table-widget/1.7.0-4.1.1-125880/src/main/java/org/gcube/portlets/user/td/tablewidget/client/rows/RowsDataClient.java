package org.gcube.portlets.user.td.tablewidget.client.rows;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class RowsDataClient {
	public void retrievesRowsAsJson()  {
		// Send request to server and catch any errors.
		String path = GWT.getModuleBaseURL() + "tdwxrowsdata";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, path);
		
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
			Log.debug("Request: "+request.toString());
		} catch (RequestException e) {
			e.printStackTrace();
		}

	}
}
