package org.gcube.portlets.user.tdwx.client.resources;

import com.google.gwt.resources.client.CssResource;


/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TDGridCSS  extends CssResource {
	
	@ClassName("grid-row-red")
    public String getGridRowRed(); 
	
	@ClassName("grid-row-no-color")
    public String getGridRowNoColor(); 
}
