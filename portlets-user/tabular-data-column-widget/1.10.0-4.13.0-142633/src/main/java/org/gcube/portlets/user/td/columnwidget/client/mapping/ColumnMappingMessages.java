package org.gcube.portlets.user.td.columnwidget.client.mapping;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ColumnMappingMessages extends Messages {
	
	@DefaultMessage("Column Mapping")
	String dialogHead();
	
	@DefaultMessage("Save")
	String btnSaveText();
	
	@DefaultMessage("Save")
	String btnSaveToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Select rows")
	String rowsLabel();

	@DefaultMessage("Creates a valid mapping!")
	String createAValidMapping();
	
	@DefaultMessage("Select a source...")
	String comboSourceValueEmptyText();
	
	@DefaultMessage("Select a target...")
	String comboTargetValueEmptyText();
	
}
