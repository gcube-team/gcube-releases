package org.gcube.portlets.user.td.taskswidget.client;

import org.gcube.portlets.user.td.taskswidget.client.rpc.TdTasksWidgetServiceAsync;

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
public class GwtTestTdTasksWidget extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.gcube.portlets.user.td.taskswidget.TdTasksWidgetJUnit";
  }

  /**
   * Tests the FieldVerifier.
   */
  public void testFieldVerifier() {
  
  }

  /**
   * This test will send a request to the server using the greetServer method in
   * GreetingService and verify the response.
   */
  public void testGreetingService() {
	  
	  TdTasksWidgetServiceAsync service = TdTaskController.getTdTaskService();
	  ServiceDefTarget target = (ServiceDefTarget) service; 
	  target.setServiceEntryPoint(GWT.getModuleBaseURL() + "TdTasksWidget/tabularDataTasksService");
      
  }


}
