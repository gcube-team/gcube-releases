package org.gcube.portlets.user.td.unionwizardwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface UnionWizardMessages extends Messages {

	@DefaultMessage("Union")
	String unionWizardHead();

	@DefaultMessage("Select Tabular Resource for Union")
	String tabResourcesSelectionCardHead();

	@DefaultMessage("This tabular resource does not have a valid table!")
	String attentionThisTabularResourceDoesNotHaveAValidTable();

	@DefaultMessage("Delete")
	String delete();

	@DefaultMessage("Would you like to delete this tabular resource?")
	String woultYouLikeToDeleteThisTabularResource();

	@DefaultMessage("Would you like to delete this tabular resource without table?")
	String woultYouLikeToDeleteThisTabularResourceWithoutTable();

	@DefaultMessage("Error on delete Tabular Resource: ")
	String errorOnDeleteTabularResourceFixed();

	@DefaultMessage("Error retrienving information on current tabular resource: ")
	String errorRetrievingInfomationOnTRFixed();

	@DefaultMessage("Name")
	String nameColumn();

	@DefaultMessage("Type")
	String typeColumn();

	@DefaultMessage("Table Type")
	String tableTypeColumn();

	@DefaultMessage("Agency")
	String agencyColumn();

	@DefaultMessage("Owner")
	String ownerColumn();

	@DefaultMessage("Creation Date")
	String creationDateColumn();

	@DefaultMessage("Delete")
	String itemDelete();

	@DefaultMessage("Error retrieving tabular resources on server!")
	String errorRetrievingTabularResources();

	@DefaultMessage("Mapping beetween Tabular Resources")
	String columnMappingCardHead();

	@DefaultMessage("Creates a valid column map!")
	String attentionCreatesAValidColumnMap();

	@DefaultMessage("Error retrieving source columns on server!")
	String errorRetrievingSourceColumns();

	@DefaultMessage("Error retrieving union columns on server!")
	String errorRetrievingUnionColumns();

	@DefaultMessage("Select a column...")
	String comboSourceColumnEmptyText();

	@DefaultMessage("Select a column...")
	String comboUnionEmptyText();

	@DefaultMessage("Union with: ")
	String unionWithFixed();

	@DefaultMessage("Type: ")
	String typeFixed();

	@DefaultMessage("Owner: ")
	String ownerFixed();

	@DefaultMessage("Union Summary")
	String summaryUnion();

	@DefaultMessage("An error occured in union.")
	String errorInUnionFixed();

}