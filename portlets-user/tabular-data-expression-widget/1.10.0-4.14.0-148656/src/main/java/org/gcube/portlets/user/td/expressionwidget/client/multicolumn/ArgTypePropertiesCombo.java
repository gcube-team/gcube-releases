package org.gcube.portlets.user.td.expressionwidget.client.multicolumn;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface ArgTypePropertiesCombo extends
		PropertyAccess<ArgType> {
	
	@Path("name")
	ModelKeyProvider<ArgType> id();
	
	LabelProvider<ArgType> label();
	

}
