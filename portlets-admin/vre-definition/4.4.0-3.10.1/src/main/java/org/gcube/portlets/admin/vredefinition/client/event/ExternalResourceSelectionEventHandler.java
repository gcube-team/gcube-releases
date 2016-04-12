package org.gcube.portlets.admin.vredefinition.client.event;

import com.google.gwt.event.shared.EventHandler;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @version 0.2 Sep 2012
 * 
 */
public interface ExternalResourceSelectionEventHandler extends EventHandler {

	void onSelectedExternalResources(ExternalResourceSelectionEvent event);
}
