package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface CurationToolBarMessages extends Messages {

	@DefaultMessage("Validation")
	String validationGroupHeadingText();
	
	@DefaultMessage("Show")
	String validationsShowButton();
	
	@DefaultMessage("Show Validations")
	String validationsShowButtonToolTip();

	@DefaultMessage("Delete")
	String validationsDeleteButton();
	
	@DefaultMessage("Delete Validations")
	String validationsDeleteButtonToolTip();
	
	@DefaultMessage("Duplicate Detection")
	String duplicateDetectionButton();

	@DefaultMessage("Detects duplicate lines in the table")
	String duplicateDetectionButtonToolTip();
	
	@DefaultMessage("Structure")
	String structureGroupHeadingText();

	@DefaultMessage("Table Type")
	String tableTypeButton();

	@DefaultMessage("Change table type")
	String tableTypeButtonToolTip();

	@DefaultMessage("Position Column")
	String changePositionColumnButton();

	@DefaultMessage("Change position column")
	String changePositionColumnButtonToolTip();
	
	@DefaultMessage("Labels")
	String changeColumnLabelButton();

	@DefaultMessage("Change column labels")
	String changeColumnLabelButtonToolTip();
	
	@DefaultMessage("Column Type")
	String columnTypeButton();

	@DefaultMessage("Change column type")
	String columnTypeButtonToolTip();
	
	@DefaultMessage("Add")
	String addColumnButton();

	@DefaultMessage("Add Column")
	String addColumnButtonToolTip();
	
	@DefaultMessage("Delete")
	String deleteColumnButton();

	@DefaultMessage("Delete column")
	String deleteColumnButtonToolTip();
	
	@DefaultMessage("Split")
	String splitColumnButton();

	@DefaultMessage("Split Column")
	String splitColumnButtonToolTip();
	
	@DefaultMessage("Merge")
	String mergeColumnButton();

	@DefaultMessage("Merge Column")
	String mergeColumnButtonToolTip();
	
	@DefaultMessage("Denormalize")
	String denormalizeButton();

	@DefaultMessage("Denormalize")
	String denormalizeButtonToolTip();
	
	@DefaultMessage("Normalize")
	String normalizeButton();

	@DefaultMessage("Normalize")
	String normalizeButtonToolTip();
	
	@DefaultMessage("Helper")
	String helperGroupHeadingText();

	@DefaultMessage("Extract Codelist")
	String extractCodelistButton();

	@DefaultMessage("Extract Codelist")
	String extractCodelistButtonToolTip();
	
	@DefaultMessage("Mapping Import")
	String codelistMappingButton();

	@DefaultMessage("Codelist Mapping Import")
	String codelistMappingButtonToolTip();

	
	
	
	
}