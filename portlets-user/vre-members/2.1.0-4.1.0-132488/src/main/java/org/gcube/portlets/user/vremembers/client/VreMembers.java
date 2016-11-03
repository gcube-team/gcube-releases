package org.gcube.portlets.user.vremembers.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.vremembers.client.panels.VREMembersPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VreMembers implements EntryPoint {

	public void onModuleLoad() {
		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				RootPanel.get("VRE-Members-Container").add(new VREMembersPanel());		
			}				
			@Override
			public void onFailure(Throwable caught) {					
			}
		});
		
	}
}
