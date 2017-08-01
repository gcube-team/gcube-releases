package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface TemplateToolBarMessages extends Messages {

	@DefaultMessage("Manage")
	String templateGroupHeadingText();

	@DefaultMessage("New")
	String templateNewButton();

	@DefaultMessage("New")
	String templateNewButtonToolTip();

	@DefaultMessage("Open")
	String templateOpenButton();

	@DefaultMessage("Open")
	String templateOpenButtonToolTip();

	@DefaultMessage("Delete")
	String templateDeleteButton();

	@DefaultMessage("Delete")
	String templateDeleteButtonToolTip();

	@DefaultMessage("Apply")
	String templateApplyButton();

	@DefaultMessage("Apply")
	String templateApplyButtonToolTip();

	@DefaultMessage("Share")
	String templateShareButton();

	@DefaultMessage("Share")
	String templateShareButtonToolTip();
	
	@DefaultMessage("Export")
	String templateExportGroupHeadingText();
	
	@DefaultMessage("SDMX")
	String templateExportSDMXButton();

	@DefaultMessage("SDMX Export")
	String templateExportSDMXButtonToolTip();
	

}