/**
 * 
 */
package org.gcube.portlets.user.td.columnwidget.client.batch;

import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceEntry;

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
public interface ReplaceEntryProperties extends PropertyAccess<ReplaceEntry> {
	
	@Path("value")
	ModelKeyProvider<ReplaceEntry> id();
	
	ValueProvider<ReplaceEntry, String> value();
	ValueProvider<ReplaceEntry, Integer> number();
	ValueProvider<ReplaceEntry, String> replacementValue();
	ValueProvider<ReplaceEntry, DimensionRow> replacementDimensionRow();
	
}
