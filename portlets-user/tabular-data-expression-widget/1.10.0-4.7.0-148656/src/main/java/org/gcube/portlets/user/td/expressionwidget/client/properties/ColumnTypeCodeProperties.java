package org.gcube.portlets.user.td.expressionwidget.client.properties;



import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface ColumnTypeCodeProperties extends
		PropertyAccess<ColumnTypeCodeElement> {

	@Path("id")
	ModelKeyProvider<ColumnTypeCodeElement> id();

	LabelProvider<ColumnTypeCodeElement> label();

}
