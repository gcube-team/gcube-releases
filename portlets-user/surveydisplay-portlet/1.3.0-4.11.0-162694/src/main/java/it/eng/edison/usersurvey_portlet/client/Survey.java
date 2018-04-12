package it.eng.edison.usersurvey_portlet.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Survey implements EntryPoint {

	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		SurveyStart surveyStart = new SurveyStart();
		
	}

}
