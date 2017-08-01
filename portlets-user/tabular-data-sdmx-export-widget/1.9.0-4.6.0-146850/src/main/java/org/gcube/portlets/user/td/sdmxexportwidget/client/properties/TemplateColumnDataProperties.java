package org.gcube.portlets.user.td.sdmxexportwidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TemplateColumnDataProperties extends
		PropertyAccess<TemplateColumnData> {
	
	@Path("id")
	ModelKeyProvider<TemplateColumnData> id();
	
	ValueProvider<TemplateColumnData,String> label();
	

}
