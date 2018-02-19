package org.gcube.portlets.admin.accountingmanager.client.properties;

import org.gcube.portlets.admin.accountingmanager.shared.data.GenresData;

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
public interface GenresDataProperties extends PropertyAccess<GenresData> {
	@Path("genre")
	ModelKeyProvider<GenresData> id();

	ValueProvider<GenresData, String> label();
}
