package org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface HomeToolBarMessages extends Messages {

	//
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

	//
	@DefaultMessage("Project")
	String projectGroupHeadingText();

	@DefaultMessage("Create")
	String btnCreateProject();

	@DefaultMessage("Create Project")
	String btnCreateProjectToolTip();

	@DefaultMessage("Open")
	String btnOpenProject();

	@DefaultMessage("Open Project")
	String btnOpenProjectToolTip();

	@DefaultMessage("Save")
	String btnSaveProject();

	@DefaultMessage("Save Project")
	String btnSaveProjectToolTip();

	//
	@DefaultMessage("Resource")
	String resourceGroupHeadingText();

	@DefaultMessage("Add")
	String btnAddResourceText();

	@DefaultMessage("Add Resource")
	String btnAddResourceToolTip();

	//
	@DefaultMessage("Software")
	String softwareGroupHeadingText();

	@DefaultMessage("Create")
	String btnCreateSoftwareText();

	@DefaultMessage("Create Software")
	String btnCreateSoftwareToolTip();

	@DefaultMessage("Publish")
	String btnPublishSoftwareText();

	@DefaultMessage("Publish Software")
	String btnPublishSoftwareToolTip();

	@DefaultMessage("Repackage")
	String btnRepackageSoftwareText();
	
	@DefaultMessage("Script Repackage")
	String btnRepackageSoftwareToolTip();

	
	//
	@DefaultMessage("Help")
	String helpGroupHeadingText();

	@DefaultMessage("Help")
	String helpButton();

	@DefaultMessage("Help")
	String helpButtonToolTip();
	
	
}