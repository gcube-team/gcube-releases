package org.gcube.portlets.user.td.rulewidget.client.multicolumn.data;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface MapPlaceHolderToColumnRowProperties extends PropertyAccess<MapPlaceHolderToColumnRow> {
	
	@Path("id")
	ModelKeyProvider<MapPlaceHolderToColumnRow> id();
	
	ValueProvider<MapPlaceHolderToColumnRow,String> placeHolderLabel();
	
	ValueProvider<MapPlaceHolderToColumnRow,ColumnData> column();
	
}
