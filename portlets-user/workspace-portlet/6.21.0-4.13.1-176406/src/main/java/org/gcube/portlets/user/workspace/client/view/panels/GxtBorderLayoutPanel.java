package org.gcube.portlets.user.workspace.client.view.panels;

import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.view.ExplorerPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBottomToolBarItem;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Class GxtBorderLayoutPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 10, 2015
 */
public class GxtBorderLayoutPanel extends ContentPanel {
	/**
	 *
	 */
	private ContentPanel north = new ContentPanel();
	private ContentPanel west = new ContentPanel();
	private ContentPanel center = new ContentPanel();
	private ContentPanel east = new ContentPanel();
	private ContentPanel south = new ContentPanel();
	private GxtBottomToolBarItem toolBarItemDetails = null;
	private ExplorerPanel expPanel = null; //TODO change position
	private GxtSeachAndFilterPanel searchAndFilterContainer;
	private GxtCardLayoutResultPanel gxtCardLayoutResultPanel;
	private MultipleDNDUpload dnd;


	/**
	 * Instantiates a new gxt border layout panel.
	 *
	 * @param searchAndFilterContainer2 the search and filter container2
	 * @param explorerPanel the explorer panel
	 * @param gxtCardLayoutResultPanel the gxt card layout result panel
	 * @param detailsContainer2 the details container2
	 * @param dnd the dnd
	 */
	public GxtBorderLayoutPanel(
			GxtSeachAndFilterPanel searchAndFilterContainer2,
			ExplorerPanel explorerPanel,
			GxtCardLayoutResultPanel gxtCardLayoutResultPanel,
			GxtBottomToolBarItem detailsContainer2, MultipleDNDUpload dnd) {

		this.searchAndFilterContainer = searchAndFilterContainer2;
		this.expPanel = explorerPanel;
		this.gxtCardLayoutResultPanel = gxtCardLayoutResultPanel;
		this.toolBarItemDetails = detailsContainer2;
		this.dnd = dnd;

		this.initLayout();
		this.createLayouts();
	}

	/**
	 * Inits the layout.
	 */
	private void initLayout(){

		north.setId("NorthPanel");

		north.setLayout(new FitLayout());
		north.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
		west.setId("WestPanel");
		west.setLayout(new FitLayout());
		center.setId("CenterPanel");
		center.setLayout(new FitLayout());
	    center.setHeaderVisible(false);
		east.setId("EastPanel");
		center.setScrollMode(Scroll.AUTOX);
		center.setBorders(false);

	    north.setHeaderVisible(false);
	    west.setHeaderVisible(false);

	    south.setHeading(ConstantsPortlet.DETAILS);
	    south.setHeaderVisible(false);
	    south.setLayout(new FitLayout());

	    east.setVisible(false);
//	    south.setVisible(false);
	}

	/**
	 * Creates the layouts.
	 */
	private void createLayouts(){

		final BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		//setStyleAttribute("padding", "10px");
		setHeaderVisible(false);
//		center.add(this.gridFilter);

	    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, ConstantsPortlet.NORTH_HEIGHT, ConstantsPortlet.NORTH_HEIGHT, ConstantsPortlet.NORTH_HEIGHT);

	    int treePanelWidth = 330;

	    //it makes appear the collapse header for mobiles
	    int leftBorder = RootPanel.get(ConstantsPortlet.WORKSPACEDIV).getAbsoluteLeft();
		int rootWidth = Window.getClientWidth() - 2* leftBorder; 
		if (rootWidth < ConstantsPortlet.HIDE_TREE_PANEL_WHEN_WIDTH_LESS_THAN) {
			west.setHeaderVisible(true);
		}

	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, treePanelWidth,treePanelWidth,treePanelWidth+70);
	    westData.setSplit(true);
	    westData.setCollapsible(true);
	    westData.setMargins(new Margins(0,1,0,0));

	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
	    centerData.setMargins(new Margins(0));

	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 150,50,150);
        eastData.setSplit(true);
	    eastData.setCollapsible(true);
	    eastData.setMargins(new Margins(0,0,0,1));
	    
	        

	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 30,30,30);
	    southData.setSplit(true);
	    southData.setCollapsible(false);
	    southData.setMargins(new Margins(1, 0, 0, 0));

	    north.add(this.searchAndFilterContainer);
	    north.setScrollMode(Scroll.AUTOY);
	    west.add(this.expPanel);
	    
  
	    north.addListener(Events.Resize, new Listener<BoxComponentEvent>(){

			@Override
			public void handleEvent(BoxComponentEvent be) {

				searchAndFilterContainer.getToolbarPathPanel().refreshSize();
			}

		});

	    center.addListener(Events.Resize,new Listener<BoxComponentEvent>(){

			@Override
			public void handleEvent(BoxComponentEvent be) {
				toolBarItemDetails.setItemsNumberToCenter();
				updateSizeCard();
				updateSizeGrid();
			}
		});

	    gxtCardLayoutResultPanel.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				updateSizeCard();
			}
		});

	    gxtCardLayoutResultPanel.getGridGroupViewContainer().addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				updateSizeGrid();
			}
		});
//		center.add(this.toolbarContainer);
	    center.setId("Center Panel "+Random.nextInt());
	    dnd.addUniqueContainer(this.gxtCardLayoutResultPanel);
	    center.add(dnd);
//	    south.add(this.toolBarItemDetails);
	    west.addListener(Events.Resize, new Listener<BoxComponentEvent>(){

			@Override
			public void handleEvent(BoxComponentEvent be) {
			    expPanel.getAsycTreePanel().setSizeTreePanel(expPanel.getWidth()-13, expPanel.getHeight()-10);
//			    if(expPanel.getSmartFolderPanel()!=null)
//			    	expPanel.getSmartFolderPanel().setSizeSmartPanel(expPanel.getWidth()-2, expPanel.getHeight()-29);
			}
		});
	    

	    add(north, northData);
	    add(west, westData);
	    add(center, centerData);
	    add(east, eastData);
	}

	/**
	 * Update size card.
	 */
	private void updateSizeCard(){
		gxtCardLayoutResultPanel.setSize(center.getWidth()-2,  center.getHeight());
	}

	/**
	 * Update size grid.
	 */
	private void updateSizeGrid(){
		gxtCardLayoutResultPanel.getGridGroupViewContainer().refreshSize(center.getWidth()-5+"px", center.getHeight()-75+"px");
	}

	/**
	 * Update parent id.
	 *
	 * @param parentId the parent id
	 */
	public void updateDnDParentId(String parentId){
		dnd.setParameters(parentId, UPLOAD_TYPE.File);
	}
	
	public void collapseTreePanel() {
			west.collapse();
	}
}