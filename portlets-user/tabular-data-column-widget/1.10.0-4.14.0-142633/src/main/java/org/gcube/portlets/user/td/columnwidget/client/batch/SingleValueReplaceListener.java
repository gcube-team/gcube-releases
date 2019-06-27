package org.gcube.portlets.user.td.columnwidget.client.batch;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface SingleValueReplaceListener {

	/**
	 * Called when selected replaceValue without errors
	 */
	public void selectedSingleValueReplace(String replaceValue);

	/**
	 * Called when the select operation is aborted by the user.
	 */
	public void abortedSingleValueReplace();

	/**
	 * Called when the something in the wizard is failed.
	 * 
	 * @param reason
	 * @param detail
	 */
	public void failedSingleValueReplace(String reason, String detail);

}
