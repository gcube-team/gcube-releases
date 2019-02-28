/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.client.resource;

import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public interface AuthCSS extends CssResource {
	
    @ClassName("ribbon")
    public String getRibbon(); 
	
    @ClassName("tab-content")
    public String getTabContent();
}
