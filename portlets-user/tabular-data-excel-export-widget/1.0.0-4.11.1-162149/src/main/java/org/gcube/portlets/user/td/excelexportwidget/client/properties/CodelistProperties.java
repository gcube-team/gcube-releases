/**
 * 
 */
package org.gcube.portlets.user.td.excelexportwidget.client.properties;



import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;

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
public interface CodelistProperties extends PropertyAccess<Codelist> {
	
	@Path("id")
	ModelKeyProvider<Codelist> key();
	
	ValueProvider<Codelist, String> name();
	ValueProvider<Codelist, String> agencyId();
	ValueProvider<Codelist, String> version();
	ValueProvider<Codelist, String> description();

}
