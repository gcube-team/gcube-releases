
package org.gcube.portlets.user.performfishanalytics.client;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.POPULATION_LEVEL;
import org.gcube.portlets.user.performfishanalytics.client.annualcontrollers.PerformFishAnnualAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsViewController;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.resources.PerformFishResources;
import org.gcube.portlets.user.performfishanalytics.client.view.util.DecodeParameterUtil;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PerformFishAnalytics implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				
				Map<String, String> initParams = new HashMap<String, String>();
				for (String key : Window.Location.getParameterMap().keySet()) {
					try {
						String dKeY = DecodeParameterUtil.base64Decode(key);
						String eValue = Window.Location.getParameter(key);
						String dParam = DecodeParameterUtil.base64Decode(eValue);
						initParams.put(dKeY, dParam);
					} catch (Exception e) {
						GWT.log("Error: "+e.getMessage());
					}
				}
				
				GWT.log("Decoded parameters: "+initParams);
				
				
				final String farmidParam = initParams.get(PerformFishAnalyticsConstant.QUERY_STRING_FARMID_PARAM);

				if(farmidParam==null || farmidParam.isEmpty()){
					String msg = "No '"+PerformFishAnalyticsConstant.QUERY_STRING_FARMID_PARAM+"' param detected";
					showErrorPage(msg);
					Window.alert("Error: "+msg);
					return;
				}
				
				final String annual = initParams.get(PerformFishAnalyticsConstant.QUERY_STRING_ANNUAL_PARAMETER);
				
				boolean isAnnualCall = false;
				
				try {
					isAnnualCall = Boolean.parseBoolean(annual);
					GWT.log("Read decoded isAnnual at: "+isAnnualCall);
				}catch (Exception e) {
					//silent
				}
				
				String batchtypeParam = initParams.get(PerformFishAnalyticsConstant.QUERY_STRING_BATCHTYPE_PARAM);
				
				//IS ANNUAL CALL
				if(isAnnualCall) {
					//CALLING THE PORTLET WITH ANNUAL CONFIGURATION

					if(batchtypeParam==null || batchtypeParam.isEmpty()) {
						batchtypeParam = PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_AGGREGATED_CLOSED_BATCHES.name();
						GWT.log("Hard cabling batchtypeParam as: "+batchtypeParam);
					}

					PerformFishInitParameter performFishInitParams = new PerformFishInitParameter();
					performFishInitParams.addParameter(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM, batchtypeParam);
					performFishInitParams.addParameter(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM, farmidParam);

					PerformFishAnalyticsServiceAsync.Util.getInstance().validParameters(performFishInitParams, new AsyncCallback<PerformFishInitParameter>() {

						@Override
						public void onSuccess(PerformFishInitParameter result) {
							PerformFishAnnualAnalyticsController controller = new PerformFishAnnualAnalyticsController();
							controller.setInitParmaters(result);
							PerformFishAnnualAnalyticsController.eventBus.fireEvent(new LoadPopulationTypeEvent(POPULATION_LEVEL.FARM.name(), null));
						}

						@Override
						public void onFailure(Throwable caught) {

							Window.alert(caught.getMessage());
						}
					});
					
				}else {
				
					//CALLING THE PORTLET WITHOUT ANNUAL CONFIGURATION
					if(batchtypeParam==null || batchtypeParam.isEmpty()){
						String msg = "No '"+PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM+"' param detected";
						showErrorPage(msg);
						Window.alert("Error: "+msg);
						return;
					}
					
					PerformFishInitParameter performFishInitParams = new PerformFishInitParameter();
					performFishInitParams.addParameter(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM, batchtypeParam);
					performFishInitParams.addParameter(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM, farmidParam);
	
					PerformFishAnalyticsServiceAsync.Util.getInstance().validParameters(performFishInitParams, new AsyncCallback<PerformFishInitParameter>() {
	
						@Override
						public void onSuccess(PerformFishInitParameter result) {
							PerformFishAnalyticsController controller = new PerformFishAnalyticsController();
							controller.setInitParmaters(result);
							PerformFishAnalyticsController.eventBus.fireEvent(new LoadPopulationTypeEvent(POPULATION_LEVEL.BATCH.name(), null));
						}
	
						@Override
						public void onFailure(Throwable caught) {
	
							Window.alert(caught.getMessage());
						}
					});
				}
			}
		});
	}
	
	private void showErrorPage(String msg) {
		TextResource errorHtmlPage = PerformFishResources.INSTANCE.errorPage();
		HTML asHtml = new HTML(errorHtmlPage.getText());
		//asHtml.asWidget().getElement().
		RootPanel.get(PerformFishAnalyticsViewController.PERFORM_FISH_ANALYTICS_DIV).add(asHtml);
		DivElement errorID = (DivElement) Document.get().getElementById("error-page");
		errorID.setInnerHTML(msg);
	}
}
