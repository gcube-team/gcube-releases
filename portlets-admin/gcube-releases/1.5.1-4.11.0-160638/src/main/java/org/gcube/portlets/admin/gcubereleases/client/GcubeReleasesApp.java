package org.gcube.portlets.admin.gcubereleases.client;

import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;


/**
 * The Class GcubeReleasesApp.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class GcubeReleasesApp implements EntryPoint {
	
	/** The Constant SERVER_ERROR. */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/** The greeting service. */
	private final GcubeReleasesServiceAsync greetingService = GWT.create(GcubeReleasesService.class);

	/** The root panel. */
	private GcubeReleasesAppController rootPanel;

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {

//		boolean jQueryLoaded = isjQueryLoaded();
//		GWT.log("Injected : "+Resources.RESOURCES.jquery().getText());
//		GWT.log("jQueryLoaded: "+jQueryLoaded);
//		
//		if (!isjQueryLoaded()) {
//			ScriptInjector.fromString(Resources.RESOURCES.jquery().getText())
//			.setWindow(ScriptInjector.TOP_WINDOW)
//			.inject();
//		}
		
//			ScriptInjector.fromString(Resources.RESOURCES.jquery().getText())
//			.setWindow(ScriptInjector.TOP_WINDOW)
//			.inject();
//			 
			
//		
		this.rootPanel = new GcubeReleasesAppController();
	}
	
	/**
	 * Checks if is j query loaded.
	 *
	 * @return true, if is j query loaded
	 */
	 private native boolean isjQueryLoaded() /*-{
		return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;
}
