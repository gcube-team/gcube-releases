/**
 * 
 */
package org.gcube.portlets.user.td.mainboxwidget.client.rpc;



import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface TabularDataServiceAsync {
	
	public static TabularDataServiceAsync INSTANCE = (TabularDataServiceAsync) GWT
			.create(TabularDataService.class);
	
	
	void hello(AsyncCallback<String> callback);

}
