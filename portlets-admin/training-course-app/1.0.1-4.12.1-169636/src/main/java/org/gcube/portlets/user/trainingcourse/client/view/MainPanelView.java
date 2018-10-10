package org.gcube.portlets.user.trainingcourse.client.view;

import org.gcube.portlets.user.trainingcourse.client.view.binder.ItemActionAndInfoView;
import org.gcube.portlets.user.trainingcourse.client.view.binder.NavigationBarView;
import org.gcube.portlets.user.trainingcourse.client.view.binder.ProjectInfoView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


// TODO: Auto-generated Javadoc
/**
 * The Class MainPanelView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2018
 */
public class MainPanelView extends DockPanel{
	
	/** The Constant MIN_HEIGHT_150. */
	public static final int MIN_HEIGHT_150 = 150;

	/** The Constant WEST_SIZE. */
	public static final int WEST_SIZE = 450;
	
	/** The Constant SOUTH_SIZE. */
	public static final int SOUTH_SIZE = 300;
	
	/** The base center panel. */
	private SplitLayoutPanel baseCenterPanel = new SplitLayoutPanel();
	
	/** The navigation bar view. */
	private NavigationBarView navigationBarView;
	
	/** The item action and info view. */
	private ItemActionAndInfoView itemActionAndInfoView;
	
	/** The project info view. */
	private ProjectInfoView projectInfoView;
	
	/** The project actions. */
	//private ProjectAction projectActions;
	
	/** The center panel. */
	private DockPanel centerPanel = new DockPanel();
	
	/** The w explorer. */
	private WorkspaceResourceUploadExplorerView wsExplorer;
	
	/** The center panel width. */
	private int centerPanelWidth;
	
	/** The center panel height. */
	private int centerPanelHeight;
	
	/** The west panel. */
	private ScrollPanel westPanel;
	
	
	/** The south panel. */
	private ScrollPanel southPanel;
	
	/**
	 * Instantiates a new main panel view.
	 */
	public MainPanelView() {
		initView();
		initLayout();
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		navigationBarView = new NavigationBarView();
		itemActionAndInfoView = new ItemActionAndInfoView();
		projectInfoView = new ProjectInfoView();
		//projectActions = new ProjectAction();
		//centerPanel.getElement().setId("center-panel-fra");
		wsExplorer = new WorkspaceResourceUploadExplorerView();
	}

	/**
	 * Inits the layout.
	 */
	private void initLayout() {
		//setSize("100%", "100%");
		//topPanel.add(navigationBarView);
		westPanel = new ScrollPanel();
//		VerticalPanel vp = new VerticalPanel();
//		vp.getElement().getStyle().setOverflowY(Overflow.AUTO);
//		vp.add(projectInfoView);
		//vp.add(projectActions);
		westPanel.add(projectInfoView);
		baseCenterPanel.addWest(westPanel, 1); //JUST FOR NOT DISPLAY EMPTY FIELDSS
		//baseCenterPanel.setSize("200px", "200px");
		centerPanel.add(wsExplorer, DockPanel.CENTER);
		baseCenterPanel.add(centerPanel);

		//baseCenterPanel.setSize("100%","100px");
		this.add(navigationBarView, DockPanel.NORTH);
		//this.setCellHorizontalAlignment(topPanel, DockPanel.ALIGN_CENTER);
		this.add(baseCenterPanel, DockPanel.CENTER);
		
	}
	
	/**
	 * Update size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void updateSize(int width, int height){
		this.setSize(width+"px", height+"px");
		navigationBarView.setWidth(width+"px");
		GWT.log("centerPanel updated width: "+width + " heig: "+height);
		baseCenterPanel.setSize(width+"px", height+"px");
		centerPanelWidth = width-WEST_SIZE-30;
		centerPanelHeight = height;
		//wExplorer.getBasePanel().setSize(centerPanelWidth+"px", centerPanelHeight"px");
		centerPanel.setSize(centerPanelWidth+"px", height+"px");
		GWT.log("centerPanelWidth: "+centerPanelWidth + " centerPanelHeight: "+height);
//		centerPanel.getElement().getStyle().setBorderColor("red");
//		centerPanel.getElement().getStyle().setBorderWidth(2.0, Unit.PX);
//		centerPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		setWidgetCenterPanelSize(centerPanelWidth, centerPanelHeight);
	}
	
	/**
	 * Gets the navigation bar view.
	 *
	 * @return the navigation bar view
	 */
	public NavigationBarView getNavigationBarView() {
		return navigationBarView;
	}
	
	/**
	 * Show west panel.
	 *
	 * @param show the show
	 */
	public void showWestPanel(boolean show) {
		if(show)
			baseCenterPanel.setWidgetSize(westPanel, WEST_SIZE);
		else
			baseCenterPanel.setWidgetSize(westPanel, 1);
	}

	/**
	 * Gets the project info view.
	 *
	 * @return the project info view
	 */
	public ProjectInfoView getProjectInfoView() {
		return projectInfoView;
	}
	
	/**
	 * Gets the workspace explorer view.
	 *
	 * @return the workspace explorer view
	 */
	public WorkspaceResourceUploadExplorerView getWorkspaceExplorerView(){
		return wsExplorer;
	}
	
	/**
	 * Gets the base center panel.
	 *
	 * @return the base center panel
	 */
	public SplitLayoutPanel getBaseCenterPanel() {
		return baseCenterPanel;
	}
	
	/**
	 * Gets the center panel height.
	 *
	 * @return the center panel height
	 */
	public int getCenterPanelHeight() {
		return centerPanelHeight;
	}
	
	/**
	 * Gets the center panel width.
	 *
	 * @return the center panel width
	 */
	public int getCenterPanelWidth() {
		return centerPanelWidth;
	}
	
	/**
	 * Gets the item action and info view.
	 *
	 * @return the item action and info view
	 */
	public ItemActionAndInfoView getItemActionAndInfoView() {
		return itemActionAndInfoView;
	}
	
	
	
	/**
	 * Gets the ws explorer.
	 *
	 * @return the ws explorer
	 */
	public WorkspaceResourceUploadExplorerView getWsExplorer() {
		return wsExplorer;
	}
	
	
	/**
	 * Sets the visible more info.
	 *
	 * @param bool the new visible more info
	 */
	public void setVisibleMoreInfo(boolean bool){
		if(bool) {
			itemActionAndInfoView.setHeight(SOUTH_SIZE-10+"px");
			itemActionAndInfoView.getElement().getStyle().setOverflowY(Overflow.AUTO);
			southPanel = new ScrollPanel(itemActionAndInfoView);
			centerPanel.add(southPanel, DockPanel.SOUTH);
		}
		else
			try {
				centerPanel.remove(itemActionAndInfoView);
			}catch (Exception e) {
				// TODO: handle exception
			}
		itemActionAndInfoView.setVisible(bool);
	}
	
	
	/**
	 * Sets the widget center panel size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setWidgetCenterPanelSize(int width, int height) {
		if((height-SOUTH_SIZE)<MIN_HEIGHT_150)
			height = MIN_HEIGHT_150;
		else
			height = height-SOUTH_SIZE;
//		
		wsExplorer.setWidgetSize(width, height);
//		wExplorer.getElement().getStyle().setBorderColor("blue");
//		wExplorer.getElement().getStyle().setBorderWidth(2.0, Unit.PX);
//		wExplorer.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		wExplorer.getElement().getStyle().setOverflowY(Overflow.AUTO);
	}


	/**
	 * Reset componenets.
	 */
	public void resetComponents() {
		projectInfoView.resetView();
		wsExplorer.resetView();
		
	}
	
	public HTMLPanel getMoreInfoPanel() {
		return itemActionAndInfoView.getMoreInfo();
	}



}
