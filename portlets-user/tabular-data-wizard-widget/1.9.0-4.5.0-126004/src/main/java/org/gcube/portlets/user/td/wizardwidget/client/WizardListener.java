/**
 * 
 */
package org.gcube.portlets.user.td.wizardwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface WizardListener {
	
	/**
	 * Called when the wizard is completed without errors
	 */
	public void completed(TRId id);

	
	/**
	 * Called when the operation is put in background
	 */
	public void putInBackground();
	
	/**
	 * Called when the wizard has been aborted by the user.
	 */
	public void aborted();
	
	/**
	 * Called when the something in the wizard is failed.
	 * @param title 
	 * @param message the failure reason.
	 * @param details the failure details.
	 * @param throwable the exception.
	 */
	public void failed(String title, String message, String details, Throwable throwable);
	
}
