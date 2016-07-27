package org.gcube.portlets.user.td.columnwidget.client.mapping;

import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;




/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ColumnMappingListener {
	
	/**
	 * Called when created column mapping without errors
	 */ 
	public void selectedColumnMapping(ColumnMappingList columnMappingList);
	
	/**
	 * Called when the column mapping operation is aborted by the user.
	 */
	public void abortedColumnMapping();
	 
	/**
	 * Called when the something in the wizard is failed.
	 *  
	 * @param reason
	 * @param detail
	 */
	public void failedColumnMapping(String reason, String detail);
	
}