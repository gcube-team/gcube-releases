/**
 * 
 */
package org.gcube.portlets.user.td.columnwidget.client.batch;

import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.Occurrences;

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
public interface OccurrencesProperties extends PropertyAccess<Occurrences> {
	
	@Path("value")
	ModelKeyProvider<Occurrences> id();
	
	ValueProvider<Occurrences, String> value();
	ValueProvider<Occurrences, Integer> number();
	
}
