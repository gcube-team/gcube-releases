/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.progress;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface OperationProgressSource {
	
	public void getProgress(AsyncCallback<OperationProgress> callback);
	
}
