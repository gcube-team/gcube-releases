package org.gcube.portlets.user.speciesdiscovery.client;

import org.gcube.portlets.user.speciesdiscovery.client.rpc.GISInfoServiceAsync;
import org.gcube.portlets.user.speciesdiscovery.client.rpc.GISInfoService;
import org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService;
import org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchServiceAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class SpeciesDiscovery implements EntryPoint {

	public static final String SPECIES_DISCOVERY_DIV = "SpeciesDiscovery";
	
	public static TaxonomySearchServiceAsync taxonomySearchService = GWT.create(TaxonomySearchService.class);	

	public static GISInfoServiceAsync gisInfoService=GWT.create(GISInfoService.class);
	
	protected ContentPanel mainPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Log.setUncaughtExceptionHandler();
		
		boolean jQueryLoaded = isjQueryLoaded();
		GWT.log("jQueryLoaded: "+jQueryLoaded);
//		GWT.log("Injected : "+Resources.RESOURCES.jquery().getText());
		
		if (!isjQueryLoaded()) {
			ScriptInjector.fromString(Resources.RESOURCES.jquery().getText())
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onModuleLoad2();
				
				Window.addResizeHandler(new ResizeHandler() {

					@Override
					public void onResize(ResizeEvent event) {
						updateSize();

					}
				});
				
			}
		});
	}
	
	/**
	 * Checks if is j query loaded.
	 *
	 * @return true, if is j query loaded
	 */
	 private native boolean isjQueryLoaded() /*-{
		return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;

	public void onModuleLoad2() {

		RootPanel root = RootPanel.get(SPECIES_DISCOVERY_DIV);
		createSearchPanel();

		if (root!=null){
			GWT.log("SPECIES_DISCOVERY_DIV div found, we are on portal");
			updateSize();
			root.add(mainPanel);

		} else{
			GWT.log("SPECIES_DISCOVERY_DIV div not found, we are out of the portal");
			//mainPanel.setWidth("100%");
			mainPanel.setHeight(600);
//			mainPanel.setWidth(930);
			RootPanel.get().add(mainPanel);
		}
		

	}

	protected void createSearchPanel()
	{
		mainPanel = SearchBorderLayoutPanel.getInstance();
	}

	public void updateSize() {
		GWT.log("Resizing");
		RootPanel discovery = RootPanel.get(SPECIES_DISCOVERY_DIV);

		int topBorder = discovery.getAbsoluteTop();
		int leftBorder = discovery.getAbsoluteLeft();

		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 34;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;

		GWT.log("new size "+rootWidth+"x"+rootHeight);
		mainPanel.setPixelSize(rootWidth, rootHeight);
	}
}
