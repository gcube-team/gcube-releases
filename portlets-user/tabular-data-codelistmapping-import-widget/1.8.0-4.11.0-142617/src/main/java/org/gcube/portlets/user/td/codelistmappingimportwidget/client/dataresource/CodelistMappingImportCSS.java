/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client.dataresource;

import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface CodelistMappingImportCSS extends CssResource {
	
    @ClassName("wizard-title")
    public String getWizardTitle(); 

	@ClassName("wizard-footer")
	public String getWizardFooter(); 
		
	@ClassName("source-selection-hover")
	public String getSourceSelectionHover(); 
	
	@ClassName("column-excluded")
	public String getColumnExcluded(); 

	@ClassName("importSelection-sources")
	public String getImportSelectionSources(); 

	@ClassName("importSelection-source")
	public String getImportSelectionSource();

	@ClassName("sdmxRegistryUrlStyle")
	public String getSDMXRegistryUrlStyle();
	
	@ClassName("sdmxRegistryUrlInputStyle")
	public String getSDMXRegistryUrlInputStyle();
	
	
}
