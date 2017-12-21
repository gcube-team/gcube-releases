package org.gcube.portlets.user.workspace.client.view;

import org.gcube.portlets.user.workspace.client.view.grids.GxtGridFilterGroupPanel;
import org.gcube.portlets.user.workspace.client.view.panels.GxtBasicTabPanel;
import org.gcube.portlets.user.workspace.client.view.panels.GxtBorderLayoutPanel;
import org.gcube.portlets.user.workspace.client.view.panels.GxtCardLayoutResultPanel;
import org.gcube.portlets.user.workspace.client.view.panels.GxtSeachAndFilterPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBottomToolBarItem;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBreadcrumbPathPanel;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class WorskpacePortlet {

	private GxtBorderLayoutPanel borderLayoutContainer = null;
	private GxtBasicTabPanel basicTabContainer = null;
//	private GxtToolBarFunctionsPanel toolbarPanelContainer = null;
	private ExplorerPanel explorerPanel = null;
	private GxtBottomToolBarItem toolBarItemDetails;
	private GxtListView listViewContainer;
	private GxtBreadcrumbPathPanel toolBarPathPanel;
	private GxtGridFilterGroupPanel gridFilterGroupContainer;
	private GxtSeachAndFilterPanel searchAndFilterContainer;
	private GxtCardLayoutResultPanel gxtCardLayoutResultPanel;
	private MultipleDNDUpload dnd;

	public WorskpacePortlet(boolean activeGroup) {

		this.basicTabContainer = new GxtBasicTabPanel();
		this.toolBarPathPanel = new GxtBreadcrumbPathPanel();
		this.searchAndFilterContainer = new GxtSeachAndFilterPanel(this.toolBarPathPanel);
			
		this.gridFilterGroupContainer = new GxtGridFilterGroupPanel(activeGroup);
		this.listViewContainer = new GxtListView();
		this.toolBarItemDetails = new GxtBottomToolBarItem();

		this.gxtCardLayoutResultPanel = new GxtCardLayoutResultPanel(gridFilterGroupContainer, listViewContainer, toolBarItemDetails);
	}
	
	public WorskpacePortlet(ExplorerPanel expPanel, boolean activeGroup) {
		this(activeGroup);
		this.explorerPanel = expPanel;
		this.dnd = new MultipleDNDUpload();
		this.borderLayoutContainer = new GxtBorderLayoutPanel(this.searchAndFilterContainer, this.explorerPanel, this.gxtCardLayoutResultPanel, this.toolBarItemDetails, this.dnd);
	}

	public GxtBorderLayoutPanel getBorderLayoutContainer() {
		return borderLayoutContainer;
	}
	
	public GxtBasicTabPanel getBasicTabContainer() {
		return basicTabContainer;
	}
	
	public GxtSeachAndFilterPanel getSearchAndFilterContainer() {
		return searchAndFilterContainer;
	}

	/**
	 * Gets the tool bar path.
	 *
	 * @return the tool bar path
	 */
	public GxtBreadcrumbPathPanel getToolBarPath() {
		return toolBarPathPanel;
	}

	public GxtGridFilterGroupPanel getGridGroupContainer() {
		return gridFilterGroupContainer;
	}

	public ExplorerPanel getExplorerPanel() {
		return explorerPanel;
	}
	
	public GxtCardLayoutResultPanel getGxtCardLayoutResultPanel() {
		return gxtCardLayoutResultPanel;
	}

	public GxtBottomToolBarItem getToolBarItemDetails() {
		return toolBarItemDetails;
	}
	
	/**
	 * @return the dnd
	 */
	public MultipleDNDUpload getDND() {
		return dnd;
	}
}
