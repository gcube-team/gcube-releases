/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.general;


public interface WizardListener {
	
	/**
	 * Called when the wizard is completed without errors
	 */
	public void completed();
	
	/**
	 * Called when the wizard has been aborted by the user.
	 */
	public void aborted();
	
	/**
	 * Called when the something in the wizard is failed.
	 * @param throwable the exception or <code>null</code>.
	 * @param reason the failure reason or <code>null</code>.
	 * @param details the failure details or <code>null</code>.
	 */
	public void failed(Throwable throwable, String reason, String details);
	
}
