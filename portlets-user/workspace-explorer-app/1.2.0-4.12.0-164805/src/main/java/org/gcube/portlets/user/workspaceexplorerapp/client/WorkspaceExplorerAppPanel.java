/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class WorkspaceExplorerAppPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 19, 2016
 */
public class WorkspaceExplorerAppPanel extends SimplePanel{


	/**
	 * Instantiates a new workspace explorer app panel.
	 *
	 * @param aPanel the a panel
	 */
	public WorkspaceExplorerAppPanel(Widget aPanel) {
	    ensureDebugId("WorkspaceExplorerAppPanel");
	    this.getElement().setId("WorkspaceExplorerAppPanel");
	    this.getElement().setAttribute("id","WorkspaceExplorerAppPanel");
	    setWidth("100%");
	    add(aPanel);
	}
}