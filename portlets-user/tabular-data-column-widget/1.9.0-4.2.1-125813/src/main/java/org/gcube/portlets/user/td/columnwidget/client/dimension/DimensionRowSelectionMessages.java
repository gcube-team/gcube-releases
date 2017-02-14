package org.gcube.portlets.user.td.columnwidget.client.dimension;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface DimensionRowSelectionMessages extends Messages {
	
	@DefaultMessage("Dimension")
	String dialogHead();
	
	@DefaultMessage("No valid View Column associated with this column!")
	String noValidViewColumnAssociatedWithThisColumn();
	
	@DefaultMessage("Value")
	String valueLabel();
	
	@DefaultMessage("Select")
	String btnSelectText();
	
	@DefaultMessage("Select")
	String btnSelectToolTip();
	
	@DefaultMessage("Select a row!")
	String selectARow();
	
	@DefaultMessage("No valid Relationship associated with this column!")
	String noValidRelationshipAssociatedWithThisColumn();
	
	
	
}
