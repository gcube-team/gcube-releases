package org.gcube.portlets.user.td.columnwidget.client.dimension;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface CodelistSelectionListener {
	
	/**
	 * Called when selected codelist without errors
	 */
	public void selected(TabResource tabResource);
	
	/**
	 * Called when the select operation is aborted by the user.
	 */
	public void aborted();
	 
	/**
	 * Called when the something in the wizard is failed.
	 *  
	 * @param reason
	 * @param detail
	 */
	public void failed(String reason, String detail);
	
}