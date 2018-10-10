package org.gcube.portlets.user.accountingdashboard.client.resources;

import com.google.gwt.core.client.ScriptInjector;
import com.google.inject.Inject;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ResourceLoader {

	@Inject
	ResourceLoader(AppResources appResources) {
		appResources.uiDataCss().ensureInjected();
		ScriptInjector.fromString(appResources.chartJS().getText())
	    .inject();
		ScriptInjector.fromString(appResources.hammerJS().getText())
	    .inject();
		ScriptInjector.fromString(appResources.chartJSPluginZoom().getText())
	    .inject();
		ScriptInjector.fromString(appResources.jsPDF().getText())
	    .inject();
		//scopeTreeResources.cellTreeStyle().ensureInjected();
		
		/*appResources.bootstrapCss().ensureInjected();
		appResources.style().ensureInjected();
		appResources.pageTable().ensureInjected();*/

	}

}
