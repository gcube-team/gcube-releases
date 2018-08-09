/**
 * 
 */
package org.gcube.portlets.user.td.excelexportwidget.client.properties;


import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Dataset;

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
public interface DatasetProperties extends PropertyAccess<Dataset> {
	
	@Path("id")
	ModelKeyProvider<Dataset> key();
	
	ValueProvider<Dataset, String> name();
	ValueProvider<Dataset, String> agencyId();
	ValueProvider<Dataset, String> version();
	ValueProvider<Dataset, String> description();

}
