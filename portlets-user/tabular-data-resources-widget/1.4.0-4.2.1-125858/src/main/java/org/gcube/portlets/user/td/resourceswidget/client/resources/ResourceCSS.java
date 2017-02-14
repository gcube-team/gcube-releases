/**
 * 
 */
package org.gcube.portlets.user.td.resourceswidget.client.resources;

import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceCSS extends CssResource {
	
	 @ClassName("cursor-zoom-in")
	 public String getCursorZoomIn(); 

	 @ClassName("cursor-zoom-out")
	 public String getCursorZoomOut(); 
	
}
