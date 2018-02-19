package org.gcube.portlets.user.td.columnwidget.client.dimension;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ConnectCodelistListener {
	
	/**
	 * Called when selected codelist without errors
	 */
	public void selectedConnectCodelist(ColumnData connection);
	
	/**
	 * Called when the select operation is aborted by the user.
	 */
	public void abortedConnectCodelist();
	 
	/**
	 * Called when the something in the wizard is failed.
	 *  
	 * @param reason
	 * @param detail
	 */
	public void failedConnectCodelist(String reason, String detail);
	
}