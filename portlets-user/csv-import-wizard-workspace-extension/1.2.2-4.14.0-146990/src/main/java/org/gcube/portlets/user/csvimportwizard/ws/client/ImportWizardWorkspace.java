/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.ws.client;

import org.gcube.portlets.user.csvimportwizard.ws.client.rpc.ImportWizardWSService;
import org.gcube.portlets.user.csvimportwizard.ws.client.rpc.ImportWizardWSServiceAsync;

import com.google.gwt.core.client.GWT;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ImportWizardWorkspace {
	
	public static final ImportWizardWSServiceAsync SERVICE = GWT.create(ImportWizardWSService.class);

}
