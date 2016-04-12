package org.gcube.portlets.user.td.columnwidget.client.create;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;

public interface CreateDefColumnListener {
	/**
	 * Called when column definition is create without errors
	 */
	public void completedDefColumnCreation(ColumnMockUp defNewColumn);
	
	/**
	 * Called when column definition is aborted by the user.
	 */
	public void abortedDefColumnCreation();
	 
	/**
	 * 
	 * @param reason
	 * @param details
	 */
	public void failedDefColumnCreation(String reason, String details);
}
