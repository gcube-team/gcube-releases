package org.gcube.portlets.user.td.wizardwidget.client;


//import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;

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
public class GwtTestWizard extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.gcube.portlets.user.td.information.WizardJUnit";
  }

 
  /**
   * This test will send a request to the server
   */
  public void testWizard() {
    // Create the service that we will test.
    //TDGWTServiceAsync tdGWTService = TDGWTServiceAsync.INSTANCE;
    //ServiceDefTarget target = (ServiceDefTarget) tdGWTService;
    //System.out.println(GWT.getModuleBaseURL() + "/TDGWTService");
    //target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/TDGWTService");

    // Since RPC calls are asynchronous, we will need to wait for a response
    // after this test method returns. This line tells the test runner to wait
    // up to 10 seconds before timing out.
    delayTestFinish(2000);
    finishTest();
    
    // Send a request to the server.
    /*try {
		TDGWTServiceAsync.INSTANCE.getTabResource( 
				new  AsyncCallback<TabResource>() {
		
			
			@Override
			public void onSuccess(TabResource result) {
				assertTrue(result!=null);
				System.out.println(result);
				  // Now that we have received a response, we need to tell the test runner
		        // that the test is complete. You must call finishTest() after an
		        // asynchronous test finishes successfully, or the test will time out.
		        finishTest();
			} 

			@Override
			public void onFailure(Throwable caught) {
				 // The request resulted in an unexpected error.
		        fail("Request failure: " + caught.getMessage());
			    
				

			}

		});
	} catch (TDGWTServiceException e) {
		System.out.println("Error in comunications: "+e.getLocalizedMessage());
		e.printStackTrace();
	}
    */
    
    
  }



}
