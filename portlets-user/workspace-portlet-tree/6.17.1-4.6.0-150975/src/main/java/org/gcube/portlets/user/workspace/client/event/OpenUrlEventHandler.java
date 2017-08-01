package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface OpenUrlEventHandler extends EventHandler {
	void onClickUrl(OpenUrlEvent openUrlEvent);
}