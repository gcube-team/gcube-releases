/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.util;

import com.google.gwt.resources.client.CssResource;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WizardCss extends CssResource {
	
	@ClassName("wizard-title")
	public String getWizardTitle(); 
	
	@ClassName("wizard-footer")
	public String getWizardFooter(); 

}
