package org.gcube.portlets.user.joinvre.client;

import org.gcube.portlets.user.joinvre.client.responsive.ResponsivePanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class JoinVRE implements EntryPoint {

	public void onModuleLoad() {
		GWT.log("onModuleLoad");
		RootPanel.get("JoinVRE-Container").add(new ResponsivePanel());
	}
	
}
