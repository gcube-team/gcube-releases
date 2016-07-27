package org.gcube.portlet.user.my_vres.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author massi
 *
 */
public class MyVREs implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("myVREsDIV").add(new VresPanel());
		
	}
}
