package org.gcube.portlets.user.td.gwtservice.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTService;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class GwtTestTDGWTService extends GWTTestCase {

	private static Logger logger = LoggerFactory.getLogger(GwtTestTDGWTService.class);

	// protected static final Logger logger=
	// LoggerFactory.getLogger(GwtTestTDGWTService.class);
	/**
	 * Must refer to a valid module that sources this class.
	 */
	@Override
	public String getModuleName() {
		return "org.gcube.portlets.user.td.gwtservice.TDGWTServiceJUnit";
	}

	/**
	 * This test will send a request to the server
	 */
	public void testListTabularResource() {
		logger.debug("---------TEST List Tabular Resource--------");

		// Create the service that we will test.
		TDGWTServiceAsync tdGWTService = GWT.create(TDGWTService.class);
		ServiceDefTarget target = (ServiceDefTarget) tdGWTService;
		logger.debug(GWT.getModuleBaseURL() + "TDGWTService");
		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "TDGWTService");

		// Since RPC calls are asynchronous, we will need to wait for a response
		// after this test method returns. This line tells the test runner to
		// wait
		// up to 7 seconds before timing out.
		delayTestFinish(7000);

		// Send a request to the server.
		TDGWTServiceAsync.INSTANCE
				.getTabularResources(new AsyncCallback<ArrayList<TabResource>>() {

					public void onFailure(Throwable caught) {
						// The request resulted in an unexpected error.
						fail("Request failure: " + caught.getMessage());

					}

					public void onSuccess(ArrayList<TabResource> result) {
						assertTrue(result != null);
						logger.debug("");
						if (result.size() <= 0) {
							logger.debug("----------------->No tabular Resources for this user.");
						} else {
							for (TabResource tr : result) {
								logger.debug("--------------->TR: " + tr);
							}
						}
						finishTest();

					}

				});

	}

}
