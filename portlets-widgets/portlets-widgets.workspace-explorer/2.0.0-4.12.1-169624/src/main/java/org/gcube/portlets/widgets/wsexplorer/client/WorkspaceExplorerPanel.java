/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * The Class SplitPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2015
 */
public class WorkspaceExplorerPanel extends DockPanel{

	public int width = 550;
//	public int height = 400;
	public int west_width = 150;

	private ScrollPanel westPanel = new ScrollPanel();
	private ScrollPanel centerScrollable = new ScrollPanel();
	private ScrollPanel southPanel = new ScrollPanel();

	/**
	 * Instantiates a new workspace explorer panel.
	 *
	 * @param splitterSize the splitter size
	 * @param wsExplorer the ws explorer
	 * @param breadcrumbs the breadcrumbs
	 * @param navigation the navigation
	 */
	public WorkspaceExplorerPanel(int splitterSize, ScrollPanel wsExplorer, Composite breadcrumbs, Composite navigation, String height) {
	    ensureDebugId("WorkspaceNavigatorPanel");

	    add(breadcrumbs, DockPanel.NORTH);

//	    southPanel.ensureDebugId("SouthPanelWEP");
	    add(southPanel, DockPanel.SOUTH);

	    westPanel.setWidth(west_width+"px");
	    westPanel.add(navigation);
	    add(westPanel, DockPanel.WEST);

	    centerScrollable.setSize(width+"px", height);
	    centerScrollable.add(wsExplorer);
	    add(centerScrollable, DockPanel.CENTER);
	}

	/**
	 * @return the southPanel
	 */
	public ScrollPanel getSouthPanel() {
		return southPanel;
	}

	/**
	 * Gets the west panel.
	 *
	 * @return the westPanel
	 */
	public ScrollPanel getWestPanel() {
		return westPanel;
	}

	/**
	 * Gets the center scrollable.
	 *
	 * @return the centerScrollable
	 */
	public ScrollPanel getCenterScrollable() {
		return centerScrollable;
	}
}