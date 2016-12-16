package org.gcube.portlets.user.td.openwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * 
 * If you run the tests using the Maven command line, you will have to 
 * navigate with your browser to a specific url given by Maven. 
 * See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html 
 * for details.
 */
public class GwtTestTDOpen extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.gcube.portlets.user.td.openwidget.TDOpenJUnit";
  }

  /**
   * This test will send a request to the server
   */
  public void testResource() {
    // Create the service that we will test.
    TDGWTServiceAsync tdGWTService = TDGWTServiceAsync.INSTANCE;
    ServiceDefTarget target = (ServiceDefTarget) tdGWTService;
    System.out.println(GWT.getModuleBaseURL() + "TDGWTService");
    target.setServiceEntryPoint(GWT.getModuleBaseURL() + "TDGWTService");

    // Since RPC calls are asynchronous, we will need to wait for a response
    // after this test method returns. This line tells the test runner to wait
    // up to 7 seconds before timing out.
    delayTestFinish(7000);

    // Send a request to the server.
    tdGWTService.getTabularResources(new AsyncCallback<ArrayList<TabResource>>() {

		
		public void onFailure(Throwable caught) {
			  // The request resulted in an unexpected error.
	        fail("Request failure: " + caught.getMessage());
			
		}

		
		public void onSuccess(ArrayList<TabResource> result) {
			 // Shows the first three resources.
			int i=0;
	        for(TabResource tr:result){
	        	i++;
	        	System.out.println(tr.toString());
	        	if(i>3){
	        		break;
	        	}
			}
			
			assertTrue(result.size()>0);

	        // Now that we have received a response, we need to tell the test runner
	        // that the test is complete. You must call finishTest() after an
	        // asynchronous test finishes successfully, or the test will time out.
	        finishTest();
			
		}
	});
    
  }


}
