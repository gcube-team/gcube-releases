package org.gcube.portlets.user.td.columnwidget.client.dimension;

import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface DimensionRowSelectionListener {
	
	/**
	 * Called when selected one row of dimension or time dimension without errors
	 */
	public void selectedDimensionRow(DimensionRow dimensionRow);
	
	/**
	 * Called when the select operation is aborted by the user.
	 */
	public void abortedDimensionRowSelection();
	 
	/**
	 * Called when the something in the wizard is failed.
	 *  
	 * @param reason
	 * @param detail
	 */
	public void failedDimensionRowSelection(String reason, String detail);
	
}