package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;


/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface FileToolBarMessages extends Messages {

	//
	@DefaultMessage("Tabular Resource")
	String fileGroupHeadingText();
	
	@DefaultMessage("Open")
	String openButton();
	
	@DefaultMessage("Open Tabular Resource")
	String openButtonToolTip();
	
	@DefaultMessage("Close All")
	String closeButton();
	
	@DefaultMessage("Close All Tabular Resources")
	String closeButtonToolTip();
	
	@DefaultMessage("Clone")
	String cloneButton();
	
	@DefaultMessage("Clone Tabular Resource")
	String cloneButtonToolTip();
	
	@DefaultMessage("Share")
	String shareButton();
	
	@DefaultMessage("Share Tabular Resource")
	String shareButtonToolTip();
	
	@DefaultMessage("Delete")
	String deleteButton();
	
	@DefaultMessage("Delete Tabular Resource")
	String deleteButtonToolTip();
	
	@DefaultMessage("Properties")
	String propertiesButton();
	
	@DefaultMessage("Show properties")
	String propertiesButtonToolTip();
	
	
	//
	@DefaultMessage("Import")
	String importGroupHeadingText();
	
	@DefaultMessage("SDMX")
	String importSDMXButton();
	
	@DefaultMessage("Import table from SDMX source")
	String importSDMXButtonToolTip();
	
	@DefaultMessage("CSV")
	String importCSVButton();
	
	@DefaultMessage("Import table from CSV source")
	String importCSVButtonToolTip();
	
	@DefaultMessage("JSON")
	String importJSONButton();
	
	@DefaultMessage("Import table from JSON source")
	String importJSONButtonToolTip();
	
	
	//
	@DefaultMessage("Export")
	String exportGroupHeadingText();
	
	@DefaultMessage("SDMX")
	String exportSDMXButton();
	
	@DefaultMessage("Export SDMX document")
	String exportSDMXButtonToolTip();
	
	@DefaultMessage("CSV")
	String exportCSVButton();
	
	@DefaultMessage("Export CSV document")
	String exportCSVButtonToolTip();
	
	@DefaultMessage("JSON")
	String exportJSONButton();
	
	@DefaultMessage("Export JSON document")
	String exportJSONButtonToolTip();
	
	//
	@DefaultMessage("Tasks")
	String taskGroupHeadingText();
	
	@DefaultMessage("Timeline")
	String timelineButton();
	
	@DefaultMessage("Timeline")
	String timelineButtonToolTip();
	
	@DefaultMessage("Background")
	String backgroundButton();
	
	@DefaultMessage("Tasks in background")
	String backgroundButtonToolTip();
	
	
	//
	@DefaultMessage("History")
	String historyGroupHeadingText();
	
	@DefaultMessage("History")
	String historyButton();
	
	@DefaultMessage("Show history")
	String historyButtonToolTip();
	
	@DefaultMessage("Undo")
	String undoButton();
	
	@DefaultMessage("Discard last operation")
	String undoButtonToolTip();
	
	
	//
	@DefaultMessage("Help")
	String helpGroupHeadingText();
	
	@DefaultMessage("Help")
	String helpButton();
	
	@DefaultMessage("Help")
	String helpButtonToolTip();
	
	@DefaultMessage("Language")
	String languageButton();
	
	@DefaultMessage("Language")
	String languageButtonToolTip();
	
	//
	@DefaultMessage("English")
	String english();
	
	@DefaultMessage("Italian")
	String italian();
	
	@DefaultMessage("Spanish")
	String spanish();
	
}