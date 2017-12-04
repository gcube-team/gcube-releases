package org.gcube.portlets.admin.ishealthmonitor.client.highchartsjs;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Extend the {@link ClientBundle} to provide JS resource link.
 *
 * @author Massimiliano Assante 
 */
public interface HighchartsBundle extends ClientBundle {
	@Source("jquery.min.js")
	TextResource jQueryJS();
	
	@Source("highcharts.js")
	TextResource highchartsJS();
	
	@Source("gxt-adapter.js")
	TextResource gxtAdapaterJS();
}