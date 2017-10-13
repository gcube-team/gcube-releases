package org.gcube.portlets.user.workspace.client.view.panels;

import org.gcube.portlets.user.workspace.client.view.GxtListView;
import org.gcube.portlets.user.workspace.client.view.grids.GxtGridFilterGroupPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBottomToolBarItem;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtToolBarItemFunctionality;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Random;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtItemsPanel extends ContentPanel{
	private GxtToolBarItemFunctionality toolBarItemFunct;
	private GxtListView iconsViewContainer;
	private GxtGridFilterGroupPanel gridGroupViewContainer;
	private GxtBottomToolBarItem toolBarItemDetails;
	
	
	public GxtItemsPanel(GxtListView iconsViewContainer, GxtGridFilterGroupPanel gridGroupViewContainer, GxtToolBarItemFunctionality toolBarItem, GxtBottomToolBarItem toolBarItemDetails) {
		this.iconsViewContainer = iconsViewContainer;
		this.gridGroupViewContainer = gridGroupViewContainer;
		this.toolBarItemFunct = toolBarItem;
		this.toolBarItemDetails = toolBarItemDetails;
		this.setId("GxtItemsPanel "+Random.nextInt());
		setBorders(false);
		setBodyBorder(false);
		setHeaderVisible(false);
		
		this.setLayout(new FitLayout());
		setTopComponent(this.toolBarItemFunct.getToolBar());
		
		add(gridGroupViewContainer);
		
		setBottomComponent(this.toolBarItemDetails);

	}


	public GxtToolBarItemFunctionality getToolBarItemFunct() {
		return toolBarItemFunct;
	}


	public GxtListView getIconsViewContainer() {
		return iconsViewContainer;
	}


	public GxtGridFilterGroupPanel getGridGroupViewContainer() {
		return gridGroupViewContainer;
	}


	public GxtBottomToolBarItem getToolBarItemDetails() {
		return toolBarItemDetails;
	}
	

}
