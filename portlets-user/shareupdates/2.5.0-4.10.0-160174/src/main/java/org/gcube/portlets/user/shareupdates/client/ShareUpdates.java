package org.gcube.portlets.user.shareupdates.client;

import org.gcube.portlets.user.shareupdates.client.view.ShareUpdateForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Massimiliano Assante at ISTI CNR
 * @author Costantino Perciante at ISTI CNR
 */
public class ShareUpdates implements EntryPoint {

	public void onModuleLoad() {
		RootPanel.get("shareUpdateDiv").add(new ShareUpdateForm());
	}
}
