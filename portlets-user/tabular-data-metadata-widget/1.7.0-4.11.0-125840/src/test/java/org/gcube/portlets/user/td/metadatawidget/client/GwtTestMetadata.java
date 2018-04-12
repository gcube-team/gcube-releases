package org.gcube.portlets.user.td.metadatawidget.client;


import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
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
public class GwtTestMetadata extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.gcube.portlets.user.td.metadatawidget.MetadataJUnit";
  }

 
  /**
   * This test will send a request to the server
   */
  public void testInformation() {
    // Create the service that we will test.
    TDGWTServiceAsync tdGWTService = TDGWTServiceAsync.INSTANCE;
    ServiceDefTarget target = (ServiceDefTarget) tdGWTService;
    System.out.println(GWT.getModuleBaseURL() + "TDGWTService");
    target.setServiceEntryPoint(GWT.getModuleBaseURL() + "TDGWTService");
    
    // Send a request to the server.
    /*tdGWTService.getTabResourceMetadata(new AsyncCallback<TabResource>(){

		@Override
		public void onFailure(Throwable caught) {
	        fail("Request failure: " + caught.getMessage());
	    	
		}

		@Override
		public void onSuccess(TabResource result) {
			assertTrue(result!=null);
			System.out.println(result);
	        finishTest();
		}
    
    });*/
    delayTestFinish(1000);
    finishTest();
  
  }



}
