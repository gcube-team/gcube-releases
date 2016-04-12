package org.gcube.portlets.user.td.mainboxwidget.client.grid;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface GridHeaderColumnMenuMessages extends Messages {

	//
	@DefaultMessage("Position Column")
	String changePositionItem();
	
	@DefaultMessage("Change the position of columns")
	String changePositionItemToolTip();
	
	@DefaultMessage("Labels")
	String changeLabelItem();
	
	@DefaultMessage("Change the labels of columns")
	String changeLabelItemToolTip();
	
	@DefaultMessage("Add Column")
	String addColumnItem();
	
	@DefaultMessage("Add a column to tabular resource")
	String addColumnItemToolTip();
	
	@DefaultMessage("Delete Column")
	String deleteColumnItem();
	
	@DefaultMessage("Delete the columns of tabular resource")
	String deleteColumnItemToolTip();
	
	@DefaultMessage("Split Column")
	String splitColumnItem();
	
	@DefaultMessage("Split a column of tabular resource")
	String splitColumnItemToolTip();
	
	@DefaultMessage("Merge Column")
	String mergeColumnItem();
	
	@DefaultMessage("Merge the columns of tabular resource")
	String mergeColumnItemToolTip();
	
	@DefaultMessage("Column Type")
	String changeColumnTypeItem();
	
	@DefaultMessage("Change the column type")
	String changeColumnTypeItemToolTip();
	
	@DefaultMessage("Filter")
	String filterItem();
	
	@DefaultMessage("Filter rows")
	String filterItemToolTip();
	
	@DefaultMessage("Replace Batch")
	String replaceBatchItem();
	
	@DefaultMessage("Replace values in batch")
	String replaceBatchItemToolTip();
	
	@DefaultMessage("Replace By Expression")
	String replaceByExpressionItem();
	
	@DefaultMessage("Replace values by expression")
	String replaceByExpressionItemToolTip();

	@DefaultMessage("Replace By External")
	String replaceByExternalItem();
	
	@DefaultMessage("Replace values by external tabular resource")
	String replaceByExternalItemToolTip();
	
	
}