package org.gcube.portlets.user.td.mainboxwidget.client.grid;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface GridContextMenuMessages extends Messages {

	//
	@DefaultMessage("Add Row")
	String addRowItem();
	
	@DefaultMessage("Add a row to tabular resource")
	String addRowItemToolTip();
	
	@DefaultMessage("Edit Row")
	String editRowItem();
	
	@DefaultMessage("Change the selected rows")
	String editRowItemToolTip();
	
	@DefaultMessage("Delete Row")
	String deleteRowItem();
	
	@DefaultMessage("Delete the selected rows")
	String deleteRowItemToolTip();
	
	@DefaultMessage("Replace All")
	String replaceRowsItem();
	
	@DefaultMessage("Replaces all rows with the same content")
	String replaceRowsItemToolTip();
	
}