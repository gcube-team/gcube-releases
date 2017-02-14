package org.gcube.portlets.user.td.columnwidget.client.create;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface CreateDefColumnMessages extends Messages {
	
	@DefaultMessage("Column Definition")
	String dialogHead();
	
	@DefaultMessage("Column Label")
	String columnLabel();
	
	@DefaultMessage("Default Value")
	String defaultValueLabel();
	
	@DefaultMessage("Select a column type...")
	String comboColumnTypeCodeEmptyText();
	
	@DefaultMessage("Column Type")
	String comboColumnTypeCodeLabel();
	
	@DefaultMessage("Select a locale type...")
	String comboLocaleTypeEmptyText();
	
	@DefaultMessage("Locale")
	String comboLocaleTypeLabel();
	
	@DefaultMessage("Save")
	String btnSaveText();
	
	@DefaultMessage("Save")
	String btnSaveToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Add a label")
	String addALabel();
	
	@DefaultMessage("No type selected")
	String noTypeSelected();
	
	@DefaultMessage("No locale selected")
	String noLocaleSelected();
	
	@DefaultMessage("Error retrieving locales")
	String errorRetrievingLocaleHead();

	
	
}
