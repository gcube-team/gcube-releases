package org.gcube.portlets.admin.accountingmanager.client.properties;

import org.gcube.portlets.admin.accountingmanager.shared.data.GenresData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface GenresDataPropertiesCombo extends
		PropertyAccess<GenresData> {

	@Path("genre")
	ModelKeyProvider<GenresData> id();

	LabelProvider<GenresData> label();

}