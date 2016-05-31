package org.gcube.portlets.admin.wfdocviewer.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.client.WfDocsLibrary;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.PermissionType;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserInfo;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wfdocviewer.client.WorkflowDocumentsViewer;
import org.gcube.portlets.admin.wfdocviewer.client.presenter.Display;
import org.gcube.portlets.admin.wfdocviewer.shared.RoleStep;
import org.gcube.portlets.admin.wfdocviewer.shared.UserBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfDocumentBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfTemplateBean;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.load.WorkspaceLightTreeLoadPopup;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 *
 */
public class WorkflowDocsView extends Composite implements Display {

	private GCubePanel mainPanel;

	private final BorderLayout layout = new BorderLayout();
	private final LayoutContainer layoutContainer = new LayoutContainer(layout);
	private final ContentPanel westView;
	private final ContentPanel centerView;
	HorizontalPanel hpnextAndRoles = new HorizontalPanel();


	ListStore<WfDocumentBean> store;
	Grid<WfDocumentBean> grid;
	final static String XTEMPLATE = "<p style=\"padding-left:10px;\"><b>Created:</b> {datecreated:date(\"yyyy-MM-dd\")}<br><b>Last Action:</b> {lastAction}<br><b>Time:</b> {lastChangeTime:date(\"yyyy-MM-dd 'at' HH:mm\")}</p>";
	private RowExpander expander;

	private Button deleteButton = new Button("Delete");
	private Button detailsButton = new Button("View details");
	private Button actionsLogButton = new Button("Show History");
	private Button addNewButton = new Button("Add new document");
	private Button nextButton = new Button("Next (assign roles to users) ");

	private Button createNewWfReportButtn = new Button("Create Worflow Report");

	private com.google.gwt.user.client.ui.Button addRoleButton = new com.google.gwt.user.client.ui.Button("Add new Role(s) for this step");

	Html permissionsLabel = new Html("Define file access permissions for each step:");  

	private ComboBox<WfTemplateBean> selectWfTemplateCombo = new ComboBox<WfTemplateBean>();  
	private WorkspaceLightTreeLoadPopup wpTreepopup = new WorkspaceLightTreeLoadPopup("Pick the Report for the workflow", false, true);

	//cellTable cols
	private Column<RoleStep, Boolean> viewCheckColumn;
	private Column<RoleStep, Boolean> updateCheckColumn;
	private Column<RoleStep, Boolean> deleteCheckColumn;
	private Column<RoleStep, Boolean> editPermissionsCheckColumn;
	private Column<RoleStep, Boolean> addCommentsCheckColumn;
	private Column<RoleStep, Boolean> updateCommentsCheckColumn;
	private Column<RoleStep, Boolean> deleteCommentsCheckColumn;

	private HorizontalPanel permissionPanel = new HorizontalPanel();
	private TabPanel stepsTabPanel = new TabPanel();  
	ArrayList<VerticalPanel> innerTabPanels = new ArrayList<VerticalPanel>();
	HashMap<String, ListView<UserBean>> rolesAndUsers = new HashMap<String, ListView<UserBean>>();

	//handler for the add Roles button
	private ClickHandler handler = null;

	public WorkflowDocsView() {
		mainPanel = new GCubePanel("Document Workflow Manager", "http://gcube.wiki.gcube-system.org/gcube/index.php/Workflow_Manager");
		westView = new ContentPanel();
		westView.setHeaderVisible(false);
		store = new ListStore<WfDocumentBean>();
		expander= new RowExpander();
		XTemplate tpl = XTemplate.create(XTEMPLATE);
		grid = new Grid<WfDocumentBean>(store, getWfDocsListColumnModel(expander, tpl)); 
		centerView = new ContentPanel(new FitLayout());
		centerView.setFrame(true);
		centerView.setHeaderVisible(false);
		centerView.setBodyStyle("backgroundColor: white;");
		centerView.setButtonAlign(HorizontalAlignment.CENTER);
		//centerView.mask("Initializing application, please wait ... ", "loading-indicator");

		initTableCols();
		renderLayoutContainer();

		mainPanel.add(layoutContainer);		

		initWidget(mainPanel);		

		updateSize();
	}

	/**
	 * 
	 */
	private void renderLayoutContainer() {  

		layoutContainer.setStyleAttribute("padding", "2px");  

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 370);  
		westData.setSplit(true);  
		westData.setMargins(new Margins(0, 5, 0, 0)); 
		westData.setCollapsible(false);  

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(false);  
		centerData.setCollapsible(false);  
		centerData.setMargins(new Margins(0));  

		//add(north, northData);  
		layoutContainer.add(westView, westData);  
		layoutContainer.add(centerView, centerData);  	

		selectWfTemplateCombo.setTemplate(getTemplate());

		grid.addPlugin(expander);
	}
	//need to instanciate the cols fot the CellTable
	private void initTableCols() {
		viewCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) { return null;} //the checkbox is initialized with no value
		};
		updateCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) {return null; }
		};
		deleteCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) {return null; }
		};
		addCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) {return null; }
		};
		updateCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) {return null;}
		};
		deleteCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) { return null;}
		};
		editPermissionsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep arg0) { return null;}
		};
	}
	/**
	 * display the list of wfDocuments
	 */
	private void displayDocuments() {
		westView.removeAll();
		grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("name"); 
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		//gridPanel.setHeading("Manage Workflow Documents");
		gridPanel.add(grid); 		
		gridPanel.addButton(detailsButton);

		gridPanel.addButton(addNewButton);
		gridPanel.addButton(actionsLogButton);
		gridPanel.addButton(deleteButton);
		gridPanel.setBorders(false);
		westView.add(gridPanel);

		westView.setLayout(new FitLayout()); 
		westView.layout();
	}



	@Override
	public void setData(List<WfDocumentBean> data) {
		store.removeAll();
		store.setDefaultSort("datecreated", SortDir.DESC);
		store.sort("datecreated", SortDir.DESC);
		store.add(data);  
		displayDocuments();		
		centerView.removeAll();
	}

	@Override
	public void updateSize() {
		RootPanel workspace = RootPanel.get(WorkflowDocumentsViewer.CONTAINER_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		mainPanel.setPixelSize(rootWidth-30, rootHeight-30);
		layoutContainer.setPixelSize(rootWidth-30, rootHeight-30);


	}


	/**
	 * Shows the roles defined for this report Workflow and the allows to assign them users
	 */
	public void showAssignRolesToUsersPanel(ArrayList<WfRole> roles, ArrayList<UserBean> users) {
		//instanciate the map containing roles and users to create
		rolesAndUsers = new HashMap<String, ListView<UserBean>>();

		// TODO Auto-generated method stub
		centerView.remove(stepsTabPanel);
		centerView.remove(hpnextAndRoles);
		selectWfTemplateCombo.setEnabled(false);
		permissionsLabel.setHtml("Define users for Roles defined in this Workflow:");
		ContentPanel cp = new ContentPanel();  
		cp.setHeaderVisible(false);
		cp.setFrame(false);  
		cp.setLayout(new RowLayout(Orientation.VERTICAL));  
		cp.setHeight(500);
		cp.setScrollMode(com.extjs.gxt.ui.client.Style.Scroll.AUTOY);
		for (WfRole role : roles) {
			cp.add(getRoleDragDropPanel(role.getRolename(), users));
		}	    
		centerView.add(cp, new RowData(1, 1, new Margins(0, 4, 0, 4)));  
		centerView.add(createNewWfReportButtn,  new RowData(1, -1, new Margins(4))); 
		centerView.layout();
	}
	/**
	 * 
	 * @param roleName
	 * @param users
	 * @return
	 */
	private ContentPanel getRoleDragDropPanel(String roleName, ArrayList<UserBean> users) {

		ListView<UserBean> list1 = new ListView<UserBean>();  
		list1.setDisplayProperty("displayName");  
		ListStore<UserBean> store = new ListStore<UserBean>();  
		store.setStoreSorter(new StoreSorter<UserBean>());
		store.sort("displayName", SortDir.ASC);
		store.add(users);  
		list1.setStore(store);  
		list1.setHeight(140);


		ListView<UserBean> list2 = new ListView<UserBean>();  
		list2.setDisplayProperty("displayName");  
		store = new ListStore<UserBean>();  
		store.setStoreSorter(new StoreSorter<UserBean>());  
		list2.setStore(store);  
		list2.setHeight(140);

		RowData data = new RowData(.5, 1);  
		data.setMargins(new Margins(5));  

		new ListViewDragSource(list1);  
		new ListViewDropTarget(list2);

		new ListViewDropTarget(list1);
		new ListViewDragSource(list2);  

		//saves the list and the role
		rolesAndUsers.put(roleName, list2);

		ContentPanel cp = new ContentPanel();  
		//  cp.setScrollMode(scroll)
		cp.setHeading("Drag users for Role: " +  roleName);  
		cp.setStyleAttribute("marginTop", "10px");  
		cp.setHeight(140);  
		cp.setFrame(true);  
		cp.setLayout(new RowLayout(Orientation.HORIZONTAL));  	    

		cp.add(list1, data);  
		cp.add(list2, data);  
		//makes it sisplay the scrollbar
		list1.setStyleAttribute("overflow-y", "scroll");
		list2.setStyleAttribute("overflow-y", "scroll");
		cp.layout();
		return cp;
	}


	/**
	 * 
	 */
	public void openReportTree() {
		wpTreepopup.setShowableTypes(ItemType.REPORT);
		wpTreepopup.setSelectableTypes(ItemType.REPORT);
		wpTreepopup.center();
		wpTreepopup.setWidth("550px");
		wpTreepopup.show();
	}
	/**
	 * show the panel to see a given wf report details (permissions and forwardActions)
	 */
	@Override
	public void showWfReportDetails(WfTemplate template, String status) {
		centerView.removeAll();
		stepsTabPanel.removeAll();

		Step[] steps = template.getGraph().getSteps();
		for (int i = 0; i < steps.length; i++) { //for each step
			ArrayList<RoleStep> roles = new ArrayList<RoleStep>();
			TabItem toAdd = new TabItem(steps[i].getLabel()); 
			if (steps[i].getPermissions() != null) {
				for (WfRole role: steps[i].getPermissions().keySet()) {
					roles.add(new RoleStep(role, steps[i]));
				}
				toAdd.add(getStepPermissionTableReadOnly(roles, steps[i]));
			}			
			toAdd.add(new Html("<hr width=\"100%\" height=\"1px\" />"));
			//get the data for each fwAction in this step
			ArrayList<ForwardActionTo> fwActionsTo = getForwardActionsWithDestination(steps[i], template.getGraph());  
			for (ForwardActionTo forwardActionTo : fwActionsTo) {
				toAdd.add(getForwardActionDetail(forwardActionTo, steps[i]));
			}

			stepsTabPanel.add(toAdd);  
		}		
		centerView.add(stepsTabPanel, new RowData(1, 1, new Margins(0, 4, 0, 4)));  

		centerView.layout();
	}

	private ContentPanel getForwardActionDetail(ForwardActionTo fwaction, Step step) {		
		ContentPanel toRet = new ContentPanel();  
		toRet.setCollapsible(true);
		toRet.setBodyStyle("backgroundColor: white;");
		toRet.setHeading("towards: " +  fwaction.toStepLabel);  
		toRet.setStyleAttribute("margin", "5px");  
		toRet.setStyleAttribute("padding", "5px");  
		toRet.setHeight(140);  
		toRet.setFrame(true);  
		toRet.setLayout(new RowLayout(Orientation.HORIZONTAL));  

		for (WfRole role : fwaction.getFwAction().getActions().keySet()) {
			ContentPanel cp = new ContentPanel();  
			cp.setStyleAttribute("backgroundColor", "white"); 
			cp.setStyleAttribute("margin", "10px");  
			cp.setFrame(true);
			cp.setHeading(role.getRolename());  
			for (UserInfo user: fwaction.getFwAction().getActions().get(role).keySet() ) {
				Html toAdd = new Html("<b>" + user.getDisplayName() + "</b>: " + fwaction.getFwAction().getActions().get(role).get(user));
				cp.add(toAdd);
			}
			cp.setWidth(200);
			//cp.setAutoWidth(true);
			toRet.add(cp);
			toRet.add(new Html("&nbsp;"));
		}

		return toRet;
	}

	/**
	 * show the panel to allow the association of a wf template to a report document
	 */
	@Override
	public void showInstanciateNewWorkflowPanel(String reportid, String reportName, ArrayList<WfTemplateBean> templates) {
		centerView.setLayout(new RowLayout(Orientation.VERTICAL));  

		Html reportSelectedLabel = new Html("<b>Select the template to associate to:</b> " + reportName + " &nbsp;&nbsp;&nbsp;");  
		reportSelectedLabel.addStyleName("pad-text");  
		reportSelectedLabel.setStyleAttribute("backgroundColor", "white");  
		reportSelectedLabel.setBorders(false);  
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(5);
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp.setHeight(30);
		hp.add(reportSelectedLabel);

		/**
		 * load the grid data
		 */
		final ListStore<WfTemplateBean> store = new ListStore<WfTemplateBean>();
		store.add(templates); 


		selectWfTemplateCombo.setEnabled(true);
		selectWfTemplateCombo.setEmptyText("Select a workflow template...");  
		selectWfTemplateCombo.setDisplayField("name");  
		selectWfTemplateCombo.setWidth(300);  
		selectWfTemplateCombo.setStore(store);  
		selectWfTemplateCombo.setTypeAhead(true);  
		selectWfTemplateCombo.setTriggerAction(TriggerAction.ALL);  

		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setSpacing(5);
		hp2.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp2.setHeight(50);
		hp.add(selectWfTemplateCombo);



		centerView.add(hp, new RowData(1, -1, new Margins(4)));  
		//	centerView.add(hp2, new RowData(1, -1, new Margins(4)));  
		stepsTabPanel.setVisible(false);


		reportSelectedLabel.addStyleName("pad-text");  
		reportSelectedLabel.setStyleAttribute("backgroundColor", "white");  
		reportSelectedLabel.setBorders(false);  
		permissionPanel.add(permissionsLabel);
		permissionPanel.setVisible(false);

		centerView.add(permissionPanel, new RowData(1, -1, new Margins(4)));  
		centerView.add(stepsTabPanel, new RowData(1, 1, new Margins(0, 4, 0, 4)));  

		centerView.layout();

	}
	/**
	 * show the tabpanel containing a tab fore each step
	 */
	public void showWfTemplateToInstanciate(WfTemplate template, ClickHandler clickHandler) {
		handler = clickHandler;
		centerView.remove(stepsTabPanel);
		stepsTabPanel.removeAll();
		permissionPanel.setVisible(true);
		stepsTabPanel.setVisible(true);
		stepsTabPanel.setWidth(centerView.getFrameWidth());  
		stepsTabPanel.setHeight(200);
		stepsTabPanel.setTabScroll(true); 
		//stepsTabPanel.setAutoHeight(true);

		Step[] steps = template.getGraph().getSteps();
		for (int i = 0; i < steps.length; i++) {
			String stepLabel = steps[i].getLabel();
			TabItem toAdd = new TabItem(stepLabel);  
			toAdd.add(getTabContentPermissions(steps[i], template.getGraph(), clickHandler));		
			stepsTabPanel.add(toAdd);  
		}		
		centerView.add(stepsTabPanel, new RowData(1, 1, new Margins(0, 4, 0, 4)));  


		hpnextAndRoles.setLayout(new FitLayout());
		hpnextAndRoles.setWidth("100%");
		hpnextAndRoles.setHorizontalAlign(HorizontalAlignment.RIGHT);
		hpnextAndRoles.add(nextButton);

		centerView.add(hpnextAndRoles,  new RowData(1, -1, new Margins(4)));  
		centerView.layout();


	}
	/**
	 * return the roles without duplicates and its belonging step
	 * @param step the source step
	 * @param graph
	 * @return
	 */
	private ArrayList<RoleStep> getEdgeRolesWithoutDuplicates(Step step, WfGraph graph) {
		ArrayList<RoleStep> toRet = new ArrayList<RoleStep>();
		
		//get the data, all the roles defined on the going out edges
		final ArrayList<ForwardAction> fwActions = graph.getForwardActions(step);  
		for (ForwardAction fa : fwActions) {
			for (WfRole role : fa.getRoles()) {
				boolean found = false;
				for (RoleStep rs : toRet) {
					if (rs.getRolename().compareTo(role.getRolename()) == 0)
						found = true;
				}
				if (! found)
					toRet.add(new RoleStep(role, step));
			}
		}	
		return toRet;
	}
	/**
	 * display the tab content for defining permissions. Assume roles defined on edges (going out) as roles defined for the current step
	 * @param step
	 * @param graph
	 * @param handler
	 * @return
	 */
	private InnerTabPanel getTabContentPermissions(Step step, WfGraph graph, ClickHandler handler) {	
		//contain the roles without duplicates and its belonging step
		ArrayList<RoleStep> roles = null;
		//handle the case of end step
		InnerTabPanel vp = null;
		if 	(step.getLabel().compareTo(WfDocsLibrary.END_STEP_LABEL)== 0) {
			roles = getEdgeRolesWithoutDuplicates(graph.getSteps()[0], graph); //passes the start step
			//need to change the source step into the end one
			for (RoleStep rs :  roles) {
				rs.setStep(step);
			}
			vp = new InnerTabPanel(step, roles);		
		}
		else { 
			roles = getEdgeRolesWithoutDuplicates(step, graph);
			vp = new InnerTabPanel(step, roles);		
		}
		innerTabPanels.add(vp);
		vp.add(getStepPermissionTable(roles, step));
		vp.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		vp.setSpacing(5);
		addRoleButton = new com.google.gwt.user.client.ui.Button(addRoleButton.getText());
		addRoleButton.addClickHandler(handler);
		vp.add(addRoleButton);		
		return vp;
	}
	/**
	 * construct the table doe definign permissions
	 * @return
	 */
	private CellTable<RoleStep> getStepPermissionTable(final ArrayList<RoleStep> roles, final Step step) {
		CellTable<RoleStep> table = new CellTable<RoleStep>();		

		TextColumn<RoleStep> roleColumn = new TextColumn<RoleStep>() {
			@Override
			public String getValue(RoleStep rs) {
				return rs.getRolename();
			}

		};
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.addColumn(roleColumn, "Role");
		table.setColumnWidth(roleColumn, 150, Unit.PX);

		table.addColumn(viewCheckColumn, "View");
		table.addColumn(updateCheckColumn, "Update");
		table.addColumn(deleteCheckColumn, "Delete");

		table.addColumn(addCommentsCheckColumn, "Add Comments");
		table.addColumn(updateCommentsCheckColumn, "Update Comments");
		table.addColumn(deleteCommentsCheckColumn, "Delete Comments");	

		table.setColumnWidth(viewCheckColumn, 55, Unit.PX);
		table.setColumnWidth(updateCheckColumn, 55, Unit.PX);
		table.setColumnWidth(deleteCheckColumn, 55, Unit.PX);
		table.setColumnWidth(addCommentsCheckColumn, 55, Unit.PX);
		table.setColumnWidth(updateCommentsCheckColumn, 55, Unit.PX);
		table.setColumnWidth(deleteCommentsCheckColumn, 55, Unit.PX);		
		table.setColumnWidth(editPermissionsCheckColumn, 55, Unit.PX);

		// Set the total row count. This isn't strictly necessary, but it affects
		// paging calculations, so its good habit to keep the row count up to date.
		table.setRowCount(roles.size());
		ArrayList<String> roleLabels = new ArrayList<String>();
		for (RoleStep role : roles) {
			roleLabels.add(role.getRolename());
		}
		// Push the data into the widget.
		table.setRowData(0, roles);
		table.setWidth("100%", true);


		return table;
	}

	private Boolean getPermissionValue(Step currStep, WfRole role, PermissionType type) {
		if (currStep.getPermissions() == null)
			return false;
		if (currStep.getPermissions().get(role) == null)
			return false;
		return currStep.getPermissions().get(role).contains(type);
	}
	/**
	 * construct the table for defining permissions
	 * @return
	 */
	private CellTable<RoleStep> getStepPermissionTableReadOnly(final ArrayList<RoleStep> roles, final Step step) {
		CellTable<RoleStep> table = new CellTable<RoleStep>();		

		TextColumn<RoleStep> roleColumn = new TextColumn<RoleStep>() {
			@Override
			public String getValue(RoleStep rs) {
				return rs.getRolename();
			}

		};
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.addColumn(roleColumn, "Role");
		table.setColumnWidth(roleColumn, 150, Unit.PX);

		Column<RoleStep, Boolean> viewCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) { return getPermissionValue(role.getStep(), role.getRole(), PermissionType.VIEW);
			} //the checkbox is initialized with no value
		};
		Column<RoleStep, Boolean> updateCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.UPDATE); }
		};
		Column<RoleStep, Boolean> deleteCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.DELETE); }
		};
		Column<RoleStep, Boolean> addCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.ADD_DISCUSSION); }
		};
		Column<RoleStep, Boolean> updateCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.UPDATE_DISCUSSION); }
		};
		Column<RoleStep, Boolean> 	deleteCommentsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.DELETE_DISCUSSION); }
		};
		Column<RoleStep, Boolean> 	editPermissionsCheckColumn = new Column<RoleStep, Boolean>(new CheckboxCell(true, false)) {
			public Boolean getValue(RoleStep role) {return getPermissionValue(role.getStep(), role.getRole(), PermissionType.EDIT_PERMISSIONS); }
		};

		FieldUpdater<RoleStep, Boolean> noUpdate = new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				Info.display("Read-only Mode activated", "Any changes will not have effect");
			}
		};
		viewCheckColumn.setFieldUpdater(noUpdate);
		updateCheckColumn.setFieldUpdater(noUpdate);
		deleteCheckColumn.setFieldUpdater(noUpdate);
		addCommentsCheckColumn.setFieldUpdater(noUpdate);
		updateCommentsCheckColumn.setFieldUpdater(noUpdate);
		editPermissionsCheckColumn.setFieldUpdater(noUpdate);


		table.addColumn(viewCheckColumn, "View");
		table.addColumn(updateCheckColumn, "Update");
		table.addColumn(deleteCheckColumn, "Delete");

		table.addColumn(addCommentsCheckColumn, "Add Comments");
		table.addColumn(updateCommentsCheckColumn, "Update Comments");
		table.addColumn(deleteCommentsCheckColumn, "Delete Comments");	

		table.setColumnWidth(viewCheckColumn, 55, Unit.PX);
		table.setColumnWidth(updateCheckColumn, 55, Unit.PX);
		table.setColumnWidth(deleteCheckColumn, 55, Unit.PX);
		table.setColumnWidth(addCommentsCheckColumn, 55, Unit.PX);
		table.setColumnWidth(updateCommentsCheckColumn, 55, Unit.PX);
		table.setColumnWidth(deleteCommentsCheckColumn, 55, Unit.PX);		
		table.setColumnWidth(editPermissionsCheckColumn, 55, Unit.PX);

		// Set the total row count. This isn't strictly necessary, but it affects
		// paging calculations, so its good habit to keep the row count up to date.
		table.setRowCount(roles.size());
		ArrayList<String> roleLabels = new ArrayList<String>();
		for (RoleStep role : roles) {
			roleLabels.add(role.getRolename());
		}
		// Push the data into the widget.
		table.setRowData(0, roles);
		table.setWidth("100%", true);


		return table;
	}

	/**
	 * metodo troiaio 
	 * @param vp
	 * @param roleToAdd
	 * @param roles
	 */
	public void addRoleToPermissionTable(ArrayList<WfRole> rolesToAdd) {

		InnerTabPanel vp = (InnerTabPanel) stepsTabPanel.getSelectedItem().getWidget(0);
		final ArrayList<RoleStep> presentyetRoles = vp.getRoles();
		for (WfRole toAdd : rolesToAdd) {
			presentyetRoles.add(new RoleStep(toAdd, vp.getMyStep()));
		}
		vp.clear();

		vp.add(getStepPermissionTable(presentyetRoles, vp.getMyStep()));
		vp.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		vp.setSpacing(5);
		addRoleButton = new com.google.gwt.user.client.ui.Button(addRoleButton.getText());
		addRoleButton.addClickHandler(handler);
		vp.add(addRoleButton);	
	}

	@Override
	public void maskCenterPanel(String message, boolean mask) {
		if (mask)
			centerView.mask(message, "loading-indicator");	
		else
			centerView.unmask();
	}

	/**
	 * the template for the combobox
	 * @return
	 */
	private native String getTemplate() /*-{ 
	    return [ 
	    '<tpl for="."><div class="x-combo-list-item">', 
	    '<h3><span>{name}</span></h3> Author: {author}', 
	    '</div></tpl>' 
	    ].join(""); 
	  }-*/; 

	/**
	 * 
	 * @return the Column Model for the table
	 */
	private ColumnModel getWfDocsListColumnModel(RowExpander expander, XTemplate tpl) {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		expander.setTemplate(tpl);
		configs.add(expander);

		ColumnConfig column = new ColumnConfig();  
		column = new ColumnConfig();  
		column.setId("displayName");  
		column.setHeader("Name");  
		column.setWidth(150);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("status");  
		column.setHeader("Status");
		column.setHidden(false);
		column.setWidth(50);  
		column.setRowHeader(true);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("lastAction");  
		column.setHeader("Last Action");
		column.setHidden(true);
		column.setWidth(50);  
		column.setRowHeader(true);  
		configs.add(column); 


		column = new ColumnConfig();  
		column.setId("lastChangeTime");  
		column.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd 'at' HH:mm"));  
		column.setHeader("Last Change");
		column.setHidden(true);
		column.setWidth(50);  
		column.setRowHeader(true);  
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId("datecreated");  
		column.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));  
		column.setHeader("Created");
		column.setHidden(false);
		column.setWidth(50);  
		column.setRowHeader(true);  
		configs.add(column); 

		return new ColumnModel(configs); 
	}


	public Button getDeleteButton() {return deleteButton;	}
	public Button getDetailsButton() {	return detailsButton; }
	public Button getAddnewButton() { return addNewButton; 	}
	public Button getActionsLogButton() { return actionsLogButton; 	}
	public HasClickHandlers getAddRoleButton() { return addRoleButton;}
	public Button getCreateNewWfReportButton() { return createNewWfReportButtn;}
	public Button getNextButton() {	return nextButton; }
	public ComboBox<WfTemplateBean>  getSelectWfTemplateCombo() { return selectWfTemplateCombo; }
	public WorkspaceLightTreeLoadPopup getWSTreepopup() {return wpTreepopup;}	

	public Column<RoleStep, Boolean> getViewCheckColumn() {return viewCheckColumn; }
	public Column<RoleStep, Boolean> getDeleteCheckColumn() { return deleteCheckColumn;	}
	public Column<RoleStep, Boolean> getDeleteCommentsCheckColumn() { return deleteCommentsCheckColumn; }
	public Column<RoleStep, Boolean> getEditPermissionCheckColumn() { return editPermissionsCheckColumn;	}
	public Column<RoleStep, Boolean> getUpdateCheckColumn() { return updateCheckColumn; }
	public Column<RoleStep, Boolean> getUpdateCommentsCheckColumn() { return updateCommentsCheckColumn; 	}
	public Column<RoleStep, Boolean> getAddCommentsCheckColumn() { 	return addCommentsCheckColumn; 	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return this;
	}
	/**
	 * 
	 * @return . 
	 */
	public ContentPanel getWestView() {
		return westView;
	}
	/**
	 * 
	 * @return -
	 */
	public ContentPanel getCenterView() {
		return centerView;
	}


	@Override
	public void showAddnewWfDocPanel() {
		centerView.clearState();
		openReportTree();
	}

	/**
	 * return a map containing for each key (rolename) the list of associated users listView
	 */
	public HashMap<String, ListView<UserBean>> getUsersAndRoles() {
		return rolesAndUsers;
	}

	@Override
	public void maskWestPanel(String message, boolean mask) {
		if (mask)
			centerView.mask(message, "loading-indicator");	
		else
			centerView.unmask();	
	}

	@Override
	public GridSelectionModel<WfDocumentBean> getGridSelectionModel() {
		return grid.getSelectionModel();
	}

	//helper class
	private class ForwardActionTo {
		final ForwardAction fwa;
		final String toStepLabel;

		public ForwardActionTo(ForwardAction fwa, String toStepLabel) {
			super();
			this.fwa = fwa;
			this.toStepLabel = toStepLabel;
		}

		public ForwardAction getFwAction() {
			return fwa;
		}

		public String getToStepLabel() {
			return toStepLabel;
		}		
	}

	/**
	 * return the forward actions associated to a given source step
	 * @param source
	 * @return
	 */
	private ArrayList<ForwardActionTo> getForwardActionsWithDestination(Step source, WfGraph graph) {
		ArrayList<ForwardActionTo> fwActions = new ArrayList<ForwardActionTo>();
		ForwardAction[][] matrix = graph.getMatrix();
		Step[] steps = graph.getSteps();
		int i = graph.indexOf(source);
		if (i < 0) {
			throw new AssertionError("The source step doesn not belong to this graph");
		}
		for (int j = 0; j < steps.length; j++) {
			if (matrix[i][j] != null) 
				fwActions.add(new ForwardActionTo(matrix[i][j], steps[j].getLabel()));
		}
		return fwActions;
	}


}
