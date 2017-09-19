package org.gcube.portlets.user.td.expressionwidget.client.properties;


import org.gcube.portlets.user.td.expressionwidget.client.store.LocaleTypeElement;

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
public interface LocaleTypeProperties extends
		PropertyAccess<LocaleTypeElement> {
	
	@Path("id")
	ModelKeyProvider<LocaleTypeElement> id();
	
	LabelProvider<LocaleTypeElement> label();
	

}
