package org.gcube.portlets.user.td.tablewidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.history.OpHistory;

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
public interface OpHistoryProperties extends
		PropertyAccess<OpHistory> {
	
	@Path("historyId")
	ModelKeyProvider<OpHistory> id();
	
	ValueProvider<OpHistory,String> name();
	ValueProvider<OpHistory, String> description();
	ValueProvider<OpHistory, String> date();
	
}
