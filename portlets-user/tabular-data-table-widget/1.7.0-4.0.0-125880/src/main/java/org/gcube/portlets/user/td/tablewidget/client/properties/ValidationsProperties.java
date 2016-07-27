package org.gcube.portlets.user.td.tablewidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ValidationsProperties extends
		PropertyAccess<Validations> {
	
	@Path("id")
	ModelKeyProvider<Validations> id();
	
	ValueProvider<Validations,String> description();
	ValueProvider<Validations, Boolean> valid();

}
