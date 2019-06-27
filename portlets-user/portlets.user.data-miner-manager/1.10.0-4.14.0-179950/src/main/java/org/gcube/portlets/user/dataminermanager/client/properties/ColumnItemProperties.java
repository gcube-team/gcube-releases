package org.gcube.portlets.user.dataminermanager.client.properties;


import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;

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
public interface ColumnItemProperties extends PropertyAccess<ColumnItem> {

	@Path("id")
	ModelKeyProvider<ColumnItem> id();

	ValueProvider<ColumnItem, String> label();

}