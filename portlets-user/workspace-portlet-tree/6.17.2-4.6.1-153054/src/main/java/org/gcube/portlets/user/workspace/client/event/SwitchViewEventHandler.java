package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SwitchViewEventHandler extends EventHandler {
	void onSwitchView(SwitchViewEvent switchViewEvent);
}