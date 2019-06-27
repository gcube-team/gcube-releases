package org.gcube.portlets.user.workspace.client.view.panels;

import org.gcube.portlets.user.workspace.client.ConstantsPortlet.ViewSwitchTypeInResult;
import org.gcube.portlets.user.workspace.client.view.GxtListView;
import org.gcube.portlets.user.workspace.client.view.grids.GxtGridFilterGroupPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBottomToolBarItem;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBreadcrumbPathPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtToolBarItemFunctionality;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class GxtCardLayoutResultPanel extends LayoutContainer{

	private CardLayout cardLayout = new CardLayout();
	private GxtItemsPanel itemPanel;
	private GxtListView iconsViewContainer;
	private GxtGridFilterGroupPanel gridGroupViewContainer;

	private ContentPanel activePanel = null;
	private GxtToolBarItemFunctionality toolBarItemFunct;
	private GxtBottomToolBarItem toolBarItemDetails;
	private GxtBreadcrumbPathPanel breadcrumbPanel;

	public GxtCardLayoutResultPanel(
			GxtGridFilterGroupPanel gridFilterGroupContainer,
			GxtListView listViewContainer,
			GxtBottomToolBarItem toolBarItemDetails, GxtBreadcrumbPathPanel toolBarPathPanel) {
		this.iconsViewContainer = listViewContainer;
		this.gridGroupViewContainer = gridFilterGroupContainer;
		this.toolBarItemDetails = toolBarItemDetails;
		this.breadcrumbPanel = toolBarPathPanel;

		instanceItemsPanel();

	}

	private void instanceItemsPanel(){
		this.toolBarItemFunct = new GxtToolBarItemFunctionality(gridGroupViewContainer, breadcrumbPanel); //instance toolbar
		this.itemPanel = new GxtItemsPanel(iconsViewContainer, gridGroupViewContainer, toolBarItemFunct, toolBarItemDetails);
	}


	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		setLayout(new FitLayout());
		
//		setId("GxtCardLayoutResultPanel");
		setHeight("auto");
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(cardLayout);
		cp.setHeight("auto");
//		cp.setId("GxtCardLayoutResultPanel Content Panel "+Random.nextInt());
		cp.add(itemPanel);
		cp.setHeight("auto");

		cardLayout.setActiveItem(itemPanel);
		activePanel = itemPanel;

		add(cp);

	};

	public void setActivePanel(ViewSwitchTypeInResult type){
		
		if(type.equals(ViewSwitchTypeInResult.Group) ){
			cardLayout.setActiveItem(itemPanel);
			activePanel = itemPanel;
		}
	}

	//	  public GxtGridMessagesFilterPanel getMessagesPanelContainer() {
	//			return messagesPanelContainer;
	//		}

	public GxtItemsPanel getItemPanel() {
		return itemPanel;
	}

	public void setItemPanel(GxtItemsPanel itemPanel) {
		this.itemPanel = itemPanel;
	}


	public GxtToolBarItemFunctionality getToolBarItemFunctionalities() {
		return toolBarItemFunct;
	}
	
	public ContentPanel getActivePanel(){
		return activePanel;
	}

	public GxtBottomToolBarItem getToolBarItemDetails() {
		return toolBarItemDetails;
	}
	
	/**
	 * @return the gridGroupViewContainer
	 */
	public GxtGridFilterGroupPanel getGridGroupViewContainer() {
		return gridGroupViewContainer;
	}

}
