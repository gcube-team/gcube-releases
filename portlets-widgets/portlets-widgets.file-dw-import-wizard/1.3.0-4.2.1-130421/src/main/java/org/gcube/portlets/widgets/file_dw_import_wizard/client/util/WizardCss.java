/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.util;

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
	
	@ClassName("source-selection-hover")
	public String getSourceSelectionHover(); 

}
