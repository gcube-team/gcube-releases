package org.gcube.portlets.user.workflowdocuments.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;
import org.gcube.portlets.user.workflowdocuments.client.WorkflowDocuments;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class WfDocLibraryView extends Composite implements Display {
	private GCubePanel mainPanel;
	private final ContentPanel tablePanel;

	private ListStore<WorkflowDocument> store;
	private Grid<WorkflowDocument> grid;
	
	private Button viewButton;
	private Button editButton;
	private Button addCommentsButton;
	private Button viewCommentsButton;
	private Button forwardButton;
	private Button refreshButton;
	
	/**
	 * 
	 */
	public WfDocLibraryView() {
		mainPanel = new GCubePanel("Workflow Documents Library", "http://gcube.wiki.gcube-system.org/gcube/index.php/My_Document_Workflows");
		tablePanel = new ContentPanel(new FitLayout());
		tablePanel.setHeaderVisible(false);
		tablePanel.setFrame(false);
		tablePanel.setStyleAttribute("Margin", "5px");
		mainPanel.add(tablePanel);		
		
		store = new ListStore<WorkflowDocument>();
		grid = new Grid<WorkflowDocument>(store, getWfDocsListColumnModel()); 
		
		viewButton = new Button("View Document");
		editButton = new Button("Edit Document");
		addCommentsButton = new Button("Add Comment");
		viewCommentsButton = new Button("View Comments");
		forwardButton = new Button("Forward to another step");
		refreshButton =  new Button("Refresh");
		
		viewButton.setEnabled(false);
		editButton.setEnabled(false);
		addCommentsButton.setEnabled(false);
		viewCommentsButton.setEnabled(false);
		forwardButton.setEnabled(false);
		refreshButton.setEnabled(true);
		
		initWidget(mainPanel);	
		updateSize();
	}
	
	/**
	 * display the list of wfDocuments
	 */
	private void displayDocuments() {
		tablePanel.removeAll();
		//grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("name"); 		
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);	

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 		
		gridPanel.setButtonAlign(HorizontalAlignment.CENTER);  
		gridPanel.addButton(viewButton);
		gridPanel.addButton(editButton);
		gridPanel.addButton(addCommentsButton);
		gridPanel.addButton(viewCommentsButton);
		gridPanel.addButton(forwardButton);
		gridPanel.addButton(refreshButton);
		gridPanel.setBorders(false);
		tablePanel.add(gridPanel);
		tablePanel.setLayout(new FitLayout()); 
		tablePanel.layout();
		//check if there is a ?oid= in the url and tries to highlight the row 
		checkRowSelect();
	}
	
	private void checkRowSelect() {
		final String workflowId = Window.Location.getParameter(WorkflowDocuments.GET_OID_PARAMETER);
		
		if (workflowId != null) {
			GWT.log("checkRowSelect wfid="+workflowId);
			doSelectRow(workflowId);
		}
	}
	
	public void doSelectRow(String workflowid) {
		int index = 0;
		for (WorkflowDocument doc : store.getModels()) {
			if (doc.getId().compareTo(workflowid) == 0) {
				index = store.indexOf(doc);
			}
		}
		grid.getView().focusRow(index);
		grid.getSelectionModel().select(index, false);
	}

	/**
	 * 
	 * @return the Column Model for the table
	 */
	private ColumnModel getWfDocsListColumnModel() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  
		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Workflow document name");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("status");  
		column.setHeader("Status");
		column.setHidden(false);
		column.setWidth(75);  
		column.setRowHeader(true);  
		configs.add(column);  
		
		column = new ColumnConfig();  
		column.setId("statusDesc");  
		column.setHeader("Description");
		column.setHidden(false);
		column.setWidth(225);  
		column.setRowHeader(true);
		configs.add(column);  
		
		column = new ColumnConfig("curRole", "Your Role", 55);  
		column.setHidden(false);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRowHeader(true);  
		configs.add(column); 
		
		column = new ColumnConfig("dateCreated", "Date Created", 70);  
		column.setHidden(false);
		column.setAlignment(HorizontalAlignment.CENTER);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("dd/MM/yyyy"));  
		column.setRowHeader(true);  
		configs.add(column); 
				
		column = new ColumnConfig("lastAction", "Last Action", 65);  
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 
		
		column = new ColumnConfig("lastDate", "Last Action time", 70);  
		column.setHidden(false);
		column.setAlignment(HorizontalAlignment.CENTER);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("dd/MM/yyyy 'at' HH:mm"));  
		column.setRowHeader(true);  
		configs.add(column);  
		
		CheckColumnConfig checkColumn = new CheckColumnConfig("update", "Editing perm.", 50);
		checkColumn.setAlignment(HorizontalAlignment.CENTER);
		checkColumn.setHidden(false);
		checkColumn.setRowHeader(true);  
		configs.add(checkColumn);  

		CheckColumnConfig checkComments = new CheckColumnConfig("hasComments", "Has Comments", 50);
		checkComments.setAlignment(HorizontalAlignment.CENTER);
		checkComments.setHidden(false);
		checkComments.setRowHeader(true);  
		configs.add(checkComments);  
		
		CheckColumnConfig checkLock = new CheckColumnConfig("isLocked", "Locked", 50);
		checkLock.setAlignment(HorizontalAlignment.CENTER);
		checkLock.setHidden(true);
		checkLock.setRowHeader(true);  
		configs.add(checkLock);  
		
		column = new ColumnConfig("lockedBy", "Locked by", 65);  
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 
		
		column = new ColumnConfig("lockExpiration", "Lock expiration", 65);  
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHidden(false);
		column.setRowHeader(true);  
		configs.add(column); 

		return new ColumnModel(configs); 
	}
	@Override
	public void updateSize() {
		RootPanel workspace = RootPanel.get(WorkflowDocuments.CONTAINER_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		tablePanel.setPixelSize(rootWidth-30, rootHeight-30);
		mainPanel.setPixelSize(rootWidth-30, rootHeight-30);
		
	}
	@Override
	public void maskCenterPanel(String message, boolean mask) {
		if (mask)
			tablePanel.mask(message, "loading-indicator");	
		else
			tablePanel.unmask();	
	}

	@Override
	public void setData(List<WorkflowDocument> data) {
		store.removeAll();
		store.setDefaultSort("lastDate", SortDir.DESC);
		store.sort("lastDate", SortDir.DESC);
		store.add(data);  
		displayDocuments();		
	}
	
	public Widget asWidget() {
		return this;
	}

	@Override
	public Button getEditButton() {	return editButton;
	}
	@Override
	public Button getForwardButton() {return forwardButton;
	}
	@Override
	public Button getViewButton() {	return viewButton;
	}
	@Override
	public Button getAddCommentsButton() {	return addCommentsButton;
	}
	@Override
	public Button getViewCommentsButton() {	return viewCommentsButton;
	}
	@Override
	public Button getRefreshButton() { return refreshButton;		
	}
	@Override
	public GridSelectionModel<WorkflowDocument> getGridSelectionModel() {
		return grid.getSelectionModel();
	}


	

}
