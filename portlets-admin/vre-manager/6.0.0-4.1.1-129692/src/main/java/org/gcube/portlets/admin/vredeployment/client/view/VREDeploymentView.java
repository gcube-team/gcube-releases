package org.gcube.portlets.admin.vredeployment.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.vredeployment.client.VREDeploymentApp;
import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class VREDeploymentView extends Composite implements Display {
	private GCubePanel mainPanel;
	private final ContentPanel tablePanel;

	private GroupingStore<VREDefinitionBean> store;
	private Grid<VREDefinitionBean> grid;

	private Button viewButton;
	private Button editButton;
	private Button removeButton;
	private Button undeployButton;	
	private Button approveButton;
	private Button viewReportButton;
	private Button refreshButton;
	private Button viewTextualReportButton;
	private Button postPoneExpiration;

	private MenuItem viewMenu;
	private MenuItem editMenu;
	private MenuItem removeMenu;
	private MenuItem approveMenu;
	private MenuItem viewReportMenu;


	private Menu gridMenu;
	/**
	 * 
	 */
	public VREDeploymentView() {
		mainPanel = new GCubePanel("VRE Manager", "https://gcube.wiki.gcube-system.org/gcube/index.php/VRE_Administration#VRE_Approval");

		tablePanel = new ContentPanel(new FitLayout());
		tablePanel.setHeaderVisible(false);
		tablePanel.setFrame(false);
		tablePanel.setStyleAttribute("Margin", "5px");
		mainPanel.add(tablePanel);		

		store = new GroupingStore<VREDefinitionBean>();

		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);  
		final ColumnModel cm = getVREListColumnModel();
		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) {  
				String f = cm.getColumnById(data.field).getHeader();  
				String l = data.models.size() == 1 ? "VRE" : "VREs";  
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		}); 
		grid = new Grid<VREDefinitionBean>(store, cm); 
		grid.setView(view);

		viewMenu = new MenuItem("View details");
		viewMenu.setIconStyle("view-icon");
		editMenu = new MenuItem("Edit");
		editMenu.setIconStyle("edit-icon");
		approveMenu = new MenuItem("Approve");
		approveMenu.setIconStyle("approve-icon");
		viewReportMenu = new MenuItem("View Report");
		viewReportMenu.setIconStyle("viewreport-icon");
		removeMenu = new MenuItem("Remove from infrastructure");
		removeMenu.setIconStyle("remove-icon");

		gridMenu = new Menu();
		grid.setContextMenu(gridMenu);	 

		viewButton = new Button("View details");
		editButton = new Button("Edit");
		approveButton = new Button("Approve");
		viewReportButton = new Button("View Report");
		removeButton = new Button("Remove from list");
		undeployButton = new Button("Undeploy VRE");
		refreshButton =  new Button("Refresh");
		viewTextualReportButton = new Button("View Text-Only Report");
		
		postPoneExpiration = new Button("Postpone Expiration ");

		viewButton.setEnabled(false);
		editButton.setEnabled(false);
		approveButton.setEnabled(false);
		viewReportButton.setEnabled(false);
		removeButton.setEnabled(false);
		refreshButton.setEnabled(true);
		viewTextualReportButton.setEnabled(false);
		postPoneExpiration.setEnabled(false);
		undeployButton.setEnabled(false);


		initWidget(mainPanel);	
		updateSize();
	}

	/**
	 * display the list of wfDocuments
	 */
	private void displayDocuments() {
		tablePanel.removeAll();
		grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("name"); 		
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);	

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 		
		gridPanel.setButtonAlign(HorizontalAlignment.CENTER);  
		gridPanel.setButtonAlign(HorizontalAlignment.CENTER);  
		gridPanel.addButton(viewButton);
		gridPanel.addButton(editButton);
		
		gridPanel.addButton(approveButton);
		gridPanel.addButton(viewReportButton);
		gridPanel.addButton(viewTextualReportButton);
		gridPanel.addButton(removeButton);
		gridPanel.addButton(undeployButton);		
		gridPanel.addButton(postPoneExpiration);
		gridPanel.addButton(refreshButton);
		
		gridPanel.setBorders(false);
		tablePanel.add(gridPanel);
		tablePanel.setLayout(new FitLayout()); 
		tablePanel.layout();
	}

	@Override
	public void maskCenterPanel(String message, boolean mask) {
		if (mask)
			tablePanel.mask(message, "loading-indicator");	
		else
			tablePanel.unmask();	
	}

	@Override
	public void setData(List<VREDefinitionBean> data) {
		store.removeAll();
		//		store.setDefaultSort("lastDate", SortDir.DESC);
		//		store.sort("lastDate", SortDir.DESC);
		store.groupBy("status");  
		store.add(data);  
		displayDocuments();		
	}

	public Widget asWidget() {
		return this;
	}
	@Override
	public void updateSize() {
		RootPanel workspace = RootPanel.get(VREDeploymentApp.CONTAINER_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		tablePanel.setPixelSize(rootWidth-30, rootHeight-30);
		mainPanel.setPixelSize(rootWidth-30, rootHeight-30);

	}




	/**
	 * 
	 * @return the Column Model for the table
	 */
	private ColumnModel getVREListColumnModel() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  
		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("VRE name");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("status");  
		column.setHeader("Status");
		column.setHidden(false);
		column.setWidth(75);  
		column.setRowHeader(true);  
		configs.add(column);  

		column = new ColumnConfig("description", "Description", 300);  
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 
		
		column = new ColumnConfig("endingDate", "Expires", 100);  
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 

		return new ColumnModel(configs);
	}


	/**
	 * change the button to enable in the bottom bar depending on the status of the selected VRE
	 */
	@Override
	public void enableActionButtons(VREDefinitionBean selectedItem) {
		if (selectedItem != null) {
			if ((selectedItem.getStatus().compareTo("Failed") == 0) || (selectedItem.getStatus().compareTo("Finished") == 0) || (selectedItem.getStatus().compareTo("Deployed") == 0)) {
				getViewButton().setEnabled(true);
				getEditButton().setEnabled(false);
				getApproveButton().setEnabled(false);
				getRemoveButton().setEnabled(true);
				getViewReportButton().setEnabled(true);
				getViewTextualReportButton().setEnabled(true);
			} 
			else if ((selectedItem.getStatus().compareTo("Running") == 0) || (selectedItem.getStatus().compareTo("Deploying") == 0)) {
				getViewButton().setEnabled(true);
				getEditButton().setEnabled(false);
				getApproveButton().setEnabled(false);
				getRemoveButton().setEnabled(false);
				getViewReportButton().setEnabled(true);
				getViewTextualReportButton().setEnabled(true);
			}
			else if (selectedItem.getStatus().compareTo("Pending") == 0 || selectedItem.getStatus().compareTo("Incomplete") == 0 || selectedItem.getStatus().compareTo("Disposed") == 0) {
				getViewButton().setEnabled(true);
				getEditButton().setEnabled(true);
				getApproveButton().setEnabled(true);
				getRemoveButton().setEnabled(true);
				getViewReportButton().setEnabled(false);
			}
			
			//also enable postpone just for deployed VREs
			if (selectedItem.getStatus().compareTo("Deployed") == 0) {
				getPostPoneButton().setEnabled(true);
				getUndeployButton().setEnabled(true);
			}
		} 
		else {
			getViewButton().setEnabled(false);
			getEditButton().setEnabled(false);
			getApproveButton().setEnabled(false);
			getRemoveButton().setEnabled(false);
			getViewReportButton().setEnabled(false);
			getViewTextualReportButton().setEnabled(false);
			getPostPoneButton().setEnabled(false);
		}
	}

	@Override
	public void showDetailsDialog(String html2Show) {
		final Dialog simple = new Dialog();			
		simple.setHeading("VRE Details");  
		simple.setButtons(Dialog.CLOSE);  
		simple.setBodyStyleName("pad-text");  
		Html toDisplay = new Html(html2Show);
		simple.add(toDisplay); 
		simple.getItem(0).getFocusSupport().setIgnore(true);  
		simple.setScrollMode(Scroll.AUTO);  
		simple.setHideOnButtonClick(true);  	
		simple.setModal(true);
		simple.setSize(600, 450);
		simple.setMaximizable(true);
		simple.show();
		simple.el().fadeIn(FxConfig.NONE);

	}

	/**
	 * change the items to show in the menu depending on the status of the selected VRE
	 */
	@Override
	public void setGridContextMenu(String vreStatus) {		
		gridMenu.removeAll();		
		if (vreStatus.compareTo("Failed") == 0 ||  (vreStatus.compareTo("Finished") == 0) ||  (vreStatus.compareTo("Deployed") == 0)) {
			gridMenu.add(viewMenu);
			gridMenu.add(removeMenu);
			gridMenu.add(viewReportMenu);
		} 
		else if ((vreStatus.compareTo("Running") == 0) ||  (vreStatus.compareTo("Deploying") == 0)) {
			gridMenu.add(viewReportMenu);
			gridMenu.add(viewMenu);
		}
		else if (vreStatus.compareTo("Pending") == 0) {
			gridMenu.add(approveMenu);
			gridMenu.add(viewMenu);
			gridMenu.add(editMenu);
			gridMenu.add(removeMenu);
		} 
		else if (vreStatus.compareTo("Incomplete") == 0 || vreStatus.compareTo("Disposed") == 0) {
			gridMenu.add(approveMenu);
			gridMenu.add(viewMenu);
			gridMenu.add(editMenu);
			gridMenu.add(removeMenu);
		} 
	}

	@Override
	public Button getUndeployButton() {	return undeployButton;
	}
	@Override
	public Button getApproveButton() {	return approveButton;
	}
	@Override
	public Button getEditButton() {return editButton;
	}
	@Override
	public Button getPostPoneButton() {return postPoneExpiration;
	}
	@Override
	public GridSelectionModel<VREDefinitionBean> getGridSelectionModel() {	return grid.getSelectionModel();
	}
	@Override
	public Button getRefreshButton() {return refreshButton;
	}
	@Override
	public Button getRemoveButton() {return removeButton;
	}
	@Override
	public Button getViewButton() {return viewButton;
	}
	@Override
	public Button getViewReportButton() {return viewReportButton;
	}
	@Override
	public Button getViewTextualReportButton() {return viewTextualReportButton;
	}
	@Override
	public MenuItem getApproveMenu() {	return approveMenu;
	}
	@Override
	public MenuItem getEditMenu() {		return editMenu;
	}
	@Override
	public MenuItem getRemoveMenu() {	return removeMenu;
	}
	@Override
	public MenuItem getViewMenu() {	 return viewMenu;
	}
	@Override
	public MenuItem getViewReportMenu() {return viewReportMenu;
	}

}
