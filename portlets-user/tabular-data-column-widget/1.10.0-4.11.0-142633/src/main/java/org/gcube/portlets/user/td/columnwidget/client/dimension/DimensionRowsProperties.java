/**
 * 
 */
package org.gcube.portlets.user.td.columnwidget.client.dimension;


import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface DimensionRowsProperties extends PropertyAccess<DimensionRow> {
	
	@Path("rowId")
	ModelKeyProvider<DimensionRow> rowId();
	
	LabelProvider<DimensionRow> value();
	
	
}
