package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.Widget;

public interface RemovePinnedEventHandler extends EventHandler {

	public void onRemovePinnedResource(Widget toRemove,Storable theResource);	
	
}
