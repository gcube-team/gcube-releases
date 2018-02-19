package org.gcube.portlets.user.td.excelexportwidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface TemplateColumnDataProperties extends
		PropertyAccess<TemplateColumnData> {
	
	@Path("id")
	ModelKeyProvider<TemplateColumnData> id();
	
	ValueProvider<TemplateColumnData,String> label();
	

}
