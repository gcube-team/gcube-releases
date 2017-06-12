/**
 * 
 */
package org.gcube.portlets.user.td.columnwidget.client.mapping;


import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;

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
public interface ColumnMappingListProperties extends PropertyAccess<DimensionRow> {
	
	@Path("id")
	ModelKeyProvider<ColumnMappingList> id();
	
	LabelProvider<ColumnMappingList> name();
	
	
}
