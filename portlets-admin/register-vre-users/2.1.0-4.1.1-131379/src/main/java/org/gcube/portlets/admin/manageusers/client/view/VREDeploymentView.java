package org.gcube.portlets.admin.manageusers.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.manageusers.client.ManageVreUsers;
import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
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
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VREDeploymentView extends Composite implements Display {
	private VerticalPanel mainPanel;
	private final ContentPanel tablePanel;

	private GroupingStore<PortalUserDTO> store;
	private StoreFilterField<PortalUserDTO> filterByName;
	private StoreFilterField<PortalUserDTO> filterByRole;
	private StoreFilterField<PortalUserDTO> filterByLab;
	private Grid<PortalUserDTO> grid;

	private Button approveButton;
	private Button refreshButton;
	

	private MenuItem approveMenu;
	private final CheckBoxSelectionModel<PortalUserDTO> sm = new CheckBoxSelectionModel<PortalUserDTO>();
	private GroupingView view = new GroupingView();  

	private Menu gridMenu;
	/**
	 * 
	 */
	public VREDeploymentView() {
		mainPanel = new VerticalPanel();

		filterByName = new StoreFilterField<PortalUserDTO>() {

			@Override
			protected boolean doSelect(Store<PortalUserDTO> store, PortalUserDTO parent, PortalUserDTO record,	String property, String filter) {
				String name = record.getLastName();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		filterByName.setEmptyText("Filter by Last Name");
		filterByName.setWidth("250px");

		filterByRole = new StoreFilterField<PortalUserDTO>() {

			@Override
			protected boolean doSelect(Store<PortalUserDTO> store, PortalUserDTO parent, PortalUserDTO record,	String property, String filter) {
				String name = record.getRole();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		filterByRole.setEmptyText("Filter by Role");
		filterByRole.setWidth("250px");

		filterByLab = new StoreFilterField<PortalUserDTO>() {

			@Override
			protected boolean doSelect(Store<PortalUserDTO> store, PortalUserDTO parent, PortalUserDTO record,	String property, String filter) {
				String name = record.getLaboratory();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		filterByLab.setEmptyText("Filter by Group");
		filterByLab.setWidth("250px");

		final ToggleButton expandButton = new ToggleButton("Expand/Collapse");
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {
				if (!expandButton.isPressed())
					view.collapseAllGroups();
				else
					view.expandAllGroups();
			}  

		});  

		tablePanel = new ContentPanel(new FitLayout());
		tablePanel.setHeaderVisible(false);
		tablePanel.setFrame(false);
		tablePanel.setStyleAttribute("Margin", "5px");
		mainPanel.add(tablePanel);		

		ToolBar toolBar = new ToolBar();  
		toolBar.getAriaSupport().setLabel("Filter Options");  
		toolBar.add(expandButton);
		toolBar.add(filterByName); 
		toolBar.add(filterByRole);
		toolBar.add(filterByLab);
		tablePanel.setTopComponent(toolBar);

		store = new GroupingStore<PortalUserDTO>();
		filterByName.bind(store);
		filterByRole.bind(store);
		filterByLab.bind(store);


		view.setShowGroupedColumn(true);  
		view.setStartCollapsed(false);
		view.setForceFit(true);  
		final ColumnModel cm = getVREListColumnModel();

		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) {  
				String f = cm.getColumnById(data.field).getHeader();  
				String l = data.models.size() == 1 ? "user" : "users";  
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		}); 
		grid = new Grid<PortalUserDTO>(store, cm); 
		grid.setView(view);

		approveMenu = new MenuItem("Add User");
		approveMenu.setIconStyle("approve-icon");


		gridMenu = new Menu();
		grid.setContextMenu(gridMenu);	 
		grid.setSelectionModel(sm);  
		grid.addPlugin(sm); 

		approveButton = new Button("Add Selected User(s)");
		refreshButton =  new Button("Refresh");

		approveButton.setEnabled(false);

	

		initWidget(mainPanel);	
		updateSize();
	}
	

	/**
	 * display the list of wfDocuments
	 */
	private void displayUsers() {
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
		gridPanel.addButton(approveButton);
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
	public void setData(List<PortalUserDTO> data) {
		store.removeAll();
		store.setDefaultSort("lastname", SortDir.DESC);
		store.sort("lastname", SortDir.DESC);
		store.groupBy("initial");  		
		store.add(data);  
		displayUsers();		
	}

	public Widget asWidget() {
		return this;
	}
	@Override
	public void updateSize() {
		RootPanel workspace = RootPanel.get(ManageVreUsers.CONTAINER_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - (topBorder + 150);
		int rootWidth = Window.getClientWidth() - 2*leftBorder - rightScrollBar;
		tablePanel.setPixelSize(rootWidth, rootHeight);
		mainPanel.setPixelSize(rootWidth, rootHeight);

	}




	/**
	 * 
	 * @return the Column Model for the table
	 */
	private ColumnModel getVREListColumnModel() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		configs.add(sm.getColumn());  
		sm.setSelectionMode(SelectionMode.MULTI);  

		ColumnConfig column = new ColumnConfig("lastname", "Last Name", 70);  
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("initial");  
		column.setHidden(true);
		column.setHeader("");
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Name");
		column.setWidth(70);		
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("id");  
		column.setHeader("Username");
		column.setWidth(50);  
		configs.add(column);  

		column = new ColumnConfig("email", "E-mail", 100);  
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 

		column = new ColumnConfig("role", "Role", 70);  
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("laboratory");  
		column.setHeader("Group");
		column.setWidth(70);  
		column.setRowHeader(true);  
		configs.add(column);  	

		return new ColumnModel(configs);
	}


	/**
	 * change the button to enable in the bottom bar depending on the status of the selected VRE
	 */
	@Override
	public void enableActionButtons(PortalUserDTO selectedItem) {
		getApproveButton().setEnabled(true);		
	}

	/**
	 * 
	 */
	@Override
	public void setGridContextMenu() {		
		gridMenu.removeAll();	
		gridMenu.add(approveMenu);
	}


	@Override
	public Button getApproveButton() {	return approveButton;
	}
	@Override
	public GridSelectionModel<PortalUserDTO> getGridSelectionModel() {	
		return grid.getSelectionModel();
	}
	@Override
	public Button getRefreshButton() {return refreshButton;
	}
	@Override
	public MenuItem getApproveMenu() {	return approveMenu;
	}


}
