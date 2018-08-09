/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.progress;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface OperationProgressSource {
	
	public void getProgress(AsyncCallback<OperationProgress> callback);
	
}
