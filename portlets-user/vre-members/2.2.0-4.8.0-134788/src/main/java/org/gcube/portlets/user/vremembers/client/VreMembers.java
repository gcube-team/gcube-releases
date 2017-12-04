package org.gcube.portlets.user.vremembers.client;

import org.gcube.portlets.user.vremembers.client.panels.VREMembersPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VreMembers implements EntryPoint {

	public void onModuleLoad() {
		RootPanel.get("VRE-Members-Container").add(new VREMembersPanel());				
	}
}
