/**
 * 
 */
package org.gcube.portlets.user.td.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TabularDataServiceAsync {

	/**
	 * 
	 * @see org.gcube.portlets.user.td.client.rpc.TabularDataService#greetServer(java.lang.String)
	 */
	void greetServer(String name, AsyncCallback<String> callback);

}
