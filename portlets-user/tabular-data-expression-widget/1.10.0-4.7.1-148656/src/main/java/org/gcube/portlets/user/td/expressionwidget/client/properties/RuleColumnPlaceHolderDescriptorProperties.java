package org.gcube.portlets.user.td.expressionwidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

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
public interface RuleColumnPlaceHolderDescriptorProperties extends
		PropertyAccess<RuleColumnPlaceHolderDescriptor> {
	
	@Path("id")
	ModelKeyProvider<RuleColumnPlaceHolderDescriptor> id();
	
	ValueProvider<RuleColumnPlaceHolderDescriptor,String> label();
	
	ValueProvider<RuleColumnPlaceHolderDescriptor,ColumnDataType> columnDataType();
	

}
