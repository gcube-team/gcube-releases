package org.gcube.portlets.user.td.resourceswidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface ResourcesMessages extends Messages {

	@DefaultMessage("Resources List View")
	String resourcesListViewDialogHead();

	@DefaultMessage("Filter:")
	String toolBarFilterLabel();

	@DefaultMessage("Sort By:")
	String toolBarSortBy();

	@DefaultMessage("Error retrieving resources")
	String errorRetrievingResourcesHead();

	@DefaultMessage("Number of Resources: {0}")
	String statusBarNumberOfResources(int size);

	@DefaultMessage("No Resource")
	String statusBarNoResource();

	@DefaultMessage("Open")
	String itemOpenText();

	@DefaultMessage("Save")
	String itemSaveText();

	@DefaultMessage("Delete")
	String itemDeleteText();

	@DefaultMessage("Save Resource")
	String saveResourceWizardHead();

	@DefaultMessage("Error removing the resource: ")
	String errorRemovingTheResource();

	@DefaultMessage("Error retrieving uri from resolver!")
	String errorRetrievingURIFromResolver();

	@DefaultMessage("Error no valid InternalUri!")
	String errorNoValidInternalUri();

	@DefaultMessage("Name")
	String nameLabel();

	@DefaultMessage("Description")
	String descriptionLabel();

	@DefaultMessage("Creation Date")
	String creationDateLabel();

	@DefaultMessage("Open")
	String btnOpenText();

	@DefaultMessage("Open")
	String btnOpenToolTip();

	@DefaultMessage("Delete")
	String btnDeleteText();

	@DefaultMessage("Delete")
	String btnDeleteToolTip();

	@DefaultMessage("Resources")
	String resourcesDialogHead();

	@DefaultMessage("Error retrieving User Info")
	String errorRetrievingUserInfo();

	@DefaultMessage("Name: ")
	String nameLabelFixed();

	@DefaultMessage("Description: ")
	String descriptionLabelFixed();

	@DefaultMessage("Creation Date: ")
	String creationDateLabelFixed();

	@DefaultMessage("Creator Id: ")
	String creatorIdLabelFixed();

	@DefaultMessage("Type: ")
	String typeLabelFixed();

	@DefaultMessage("Value: ")
	String valueLabelFixed();
	
	@DefaultMessage("Internal Id: ")
	String internalURIIdLabelFixed();
	
	@DefaultMessage("MimeType: ")
	String internalURIMimeTypeLabelFixed();

	@DefaultMessage("Table Id: ")
	String tableIdLabelFixed();
	
	@DefaultMessage("SDMX Name Id: ")
	String sdmxNameIdLabelFixed();
	
	@DefaultMessage("SDMX URL: ")
	String sdmxURLLabelFixed();
	
	@DefaultMessage("SDMX Version: ")
	String sdmxVersionLabelFixed();
	
	@DefaultMessage("SDMX Agency: ")
	String sdmxAgencyLabelFixed();
	
	@DefaultMessage("SDMX Registry: ")
	String sdmxRegistryLabelFixed();
	
	@DefaultMessage("Name")
	String nameCol();

	@DefaultMessage("Type")
	String typeCol();

	@DefaultMessage("Empty")
	String gridEmptyText();

	@DefaultMessage("Error removing the resource: ")
	String errorRetrievingResourcesFixed();

	@DefaultMessage("Error retrieving current tabular resource id!")
	String errorRetrievingCurrentTabularResourceId();

	@DefaultMessage("Error get TR information!")
	String errorGetTRInformation();

	@DefaultMessage("Error setting Active TR!")
	String errorSettingActiveTR();

	@DefaultMessage("")
	@AlternateMessage({ "Name", "Name", "CreationDate", "Creation Date" })
	String resourceSortInfo(@Select ResourcesListViewPanel.ResourcesSortInfo sortItem);
	
	@DefaultMessage("Error invalid resource!")
	String errorInvalidResource();
	
	@DefaultMessage("Operation not allowed on this type of resource!")
	String operationNotAllowedOnThisTypeOfResource();


}
