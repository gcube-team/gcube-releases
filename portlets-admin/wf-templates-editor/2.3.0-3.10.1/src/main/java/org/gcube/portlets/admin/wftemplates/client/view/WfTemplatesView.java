package org.gcube.portlets.admin.wftemplates.client.view;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.shared.EdgePoint;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wftemplates.client.WfTemplatesConstants;
import org.gcube.portlets.admin.wftemplates.client.WorkflowTemplates;
import org.gcube.portlets.admin.wftemplates.client.presenter.MyDiagramController;
import org.gcube.portlets.admin.wftemplates.client.presenter.MyDropController;
import org.gcube.portlets.admin.wftemplates.client.presenter.WfTemplatesPresenter;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.orange.links.client.DiagramController;
import com.orange.links.client.canvas.DiagramCanvas;
import com.orange.links.client.canvas.MultiBrowserDiagramCanvas;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.event.TieLinkHandler;

/**
 * <code> WfTemplatesView </code> class is the view component of this webapp in the MVP Pattern
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class WfTemplatesView extends Composite implements WfTemplatesPresenter.Display  {

	public final static int TEMPLATES_PER_PAGE = 15;

	private GCubePanel mainPanel;
	//private  DockPanel layoutPanel = new DockPanel(); 
	private  DockLayoutPanel layoutPanel = new DockLayoutPanel(Unit.PCT); 
	private VerticalPanel westPanel;
	private VerticalPanel centerPanel;
	private HorizontalPanel southPanel;
	AbsolutePanel dropPanel = new AbsolutePanel();
	
	private Button createNewButton = new Button("Create New");
	private Button addNewStepButton = new Button("Add New Step");
	private Button deleteButton = new Button("Delete selected");
	private Button saveButton = new Button("Save template");
	private Button resetButton = new Button("Reset current");

	private MyDiagramController dc;
	PickupDragController dragController;
	private CellTable<WfTemplate> table;
	private SingleSelectionModel<WfTemplate> selectionModel;
	private ArrayList<Step> steps;
	private ArrayList<WfStep> wfsteps;



	/**
	 * constructor
	 */
	public WfTemplatesView() {
		mainPanel = new GCubePanel("Workflow Template Editor", "https://gcube.wiki.gcube-system.org/gcube/index.php/Workflow_Templates");

		westPanel = getWestPanel();
		southPanel = getSouthPanel();
		centerPanel = getCenterPanel();

		centerPanel.setWidth("100%");
		southPanel.setWidth("100%");
		southPanel.setStyleName("southPanel");

		layoutPanel.addWest(westPanel, 20);
		layoutPanel.addSouth(southPanel, 7);
		layoutPanel.add(centerPanel);

		mainPanel.add(layoutPanel);
		initWidget(mainPanel);

		Window.addResizeHandler(new ResizeHandler() {			
			public void onResize(ResizeEvent event) {
				updateSize();				
			}
		});

		initTable();

		updateSize();
		enableGraphControlPanel(false);
	}
	

	@Override
	public void resetView() {
		resetInitGraphWidget(null, null);	
		enableGraphControlPanel(false);
		enableSaveButton(false);
		enableDropPanel(false);
	}

	/**
	 * initializes the table to show the templates list
	 */
	private void initTable() {
		/*
		 * Define a key provider for a Template. Use the unique ID as the key which allows to maintain selection even if the name changes.
		 */
		ProvidesKey<WfTemplate> keyProvider = new ProvidesKey<WfTemplate>() {
			public Object getKey(WfTemplate item) {
				// Always do a null check.
				return (item == null) ? null : item.getTemplateid();
			}
		};
		// Create a cell to render each value.
		// Create a CellList using the keyProvider.
		table = new CellTable<WfTemplate>(TEMPLATES_PER_PAGE, keyProvider);
		selectionModel = new SingleSelectionModel<WfTemplate>();
		table.setSelectionModel(selectionModel);
	}

	/**
	 * 
	 * @return
	 */
	private VerticalPanel getCenterPanel() {
		VerticalPanel toRet = new VerticalPanel();		
		HTML displayInit = new HTML("Click on a template or create a new one", true);
		displayInit.setStyleName("splashDisplay");
		toRet.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		toRet.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		toRet.add(displayInit);

		return toRet;
	}

	/**
	 * @return
	 */
	private VerticalPanel getWestPanel() {
		VerticalPanel toReturn = new VerticalPanel();
		toReturn.setWidth("100%");
		toReturn.setStyleName("westPanel");		
		return toReturn;
	}

	/**
	 * 
	 * @param table
	 */
	public void setData(List<WfTemplate> list) {
		this.table = getTemplatesTable(list);
		getWestPanel().clear();
		// Create a Pager to control the table.
		SimplePager pager;
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(table);
		westPanel.add(table);
		westPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		westPanel.add(pager);

		/**
		 * the buttons menu
		 */
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.add(deleteButton);
		bottomPanel.add(createNewButton);
		westPanel.add(bottomPanel);
	}


	/**
	 * construct the celltable to hold the templates
	 * @return
	 */
	private CellTable<WfTemplate> getTemplatesTable(final List<WfTemplate> templates) {
		// Add a text column to show the nam and the author of not existent yet
		if (table.getColumnCount() == 0) {
			TextColumn<WfTemplate> nameColumn = new TextColumn<WfTemplate>() {
				@Override
				public String getValue(WfTemplate object) {
					return object.getTemplatename();
				}
			};
			table.addColumn(nameColumn, "Name");

			TextColumn<WfTemplate> authorColumn = new TextColumn<WfTemplate>() {
				@Override
				public String getValue(WfTemplate object) {
					return object.getAuthor();
				}
			};
			table.addColumn(authorColumn, "Author");
			table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		}


		table.addRangeChangeHandler(new Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent arg0) {
				Range range = table.getVisibleRange();
				int start = range.getStart();
				int length = range.getLength();
				List<WfTemplate> toSet = new ArrayList<WfTemplate>(templates.size());
				for (int i = start; i < start + length && i < templates.size(); i++)
					toSet.add((WfTemplate) templates.get(i));
				table.setRowData(start, toSet);

			}
		});


		// Set the total row count. This isn't strictly necessary, but it affects
		// paging calculations, so its good habit to keep the row count up to date.
		table.setRowCount(templates.size());
		// Push the data into the widget.
		table.setRowData(0, templates);
		table.setWidth("100%", true);

		return table;
	}


	private HorizontalPanel getSouthPanel() {
		HorizontalPanel toRet = new HorizontalPanel();
		HorizontalPanel buttonsP = new HorizontalPanel();
		toRet.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		toRet.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		buttonsP.add(addNewStepButton);
		buttonsP.add(resetButton);
		buttonsP.add(saveButton);
		buttonsP.setSpacing(5);
		toRet.setStyleName("southPanel");
		saveButton.setWidth("120px");
		toRet.add(buttonsP);
		return toRet;
	}

	/**
	 * BUTTONS / Table GETTERS
	 */
	public HasClickHandlers getCreateNewButton() {	return createNewButton;	}
	public HasClickHandlers getDeleteButton() {	return deleteButton;	}
	public HasClickHandlers getSaveButton() {	return saveButton;	}
	public HasClickHandlers getResetButton() {	return resetButton;	}
	public HasClickHandlers getAddNewStepButton() {	return addNewStepButton; }
	public MyDiagramController getDiagramController() { return dc; }
	public SingleSelectionModel<WfTemplate> getTableSelectionModel()  {	return selectionModel;	}


	/**
	 *  display the selected template in the view
	 */
	@Override
	public void displaySelectedWfTemplate(WfGraph graph, TieLinkHandler linkHandler, HandlerManager eventBus) {
		resetInitGraphWidget(linkHandler, eventBus);
		Step[] steps = graph.getSteps();
		for (int i = 0; i < steps.length; i++) {
			addNewStep(steps[i].getLabel(), steps[i].getDescription(), steps[i].getLeft(), steps[i].getTop());
		}
		ForwardAction[][] matrix = graph.getMatrix();
		for (int i = 0; i < steps.length; i++) {
			for (int j = 0; j < steps.length; j++) {
				if (matrix[i][j] != null) {
					Connection c1 = dc.drawStraightArrowConnection(wfsteps.get(i), wfsteps.get(j));
					ForwardAction fwA = matrix[i][j];
					addNewRoles(c1, fwA.getRolesToString());
					for (EdgePoint p : fwA.getPoints()) {
						dc.addPointOnConnection(c1, p.getLeft(), p.getTop());
					}
				}
			}
		}
	}
	/**
	 * shows the create new template view
	 * @param width
	 * @param height
	 */
	@Override
	public void showCreateNewTemplate(TieLinkHandler linkHandler, HandlerManager eventBus) {
		DiagramCanvas canvas = resetInitGraphWidget(linkHandler, eventBus);
		int midY = canvas.getHeight() / 2;
		addNewStep("Start", "This is the entry point step of the workflow and is always present", 25, midY);
		addNewStep("End", "This is the ending point step of the workflow and is always present", canvas.getWidth()-100,midY);		
	}

	private DiagramCanvas resetInitGraphWidget(TieLinkHandler linkHandler, HandlerManager eventBus) {
		centerPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		int width = centerPanel.getOffsetWidth();
		int height = centerPanel.getOffsetHeight();
		DiagramCanvas canvas = new MultiBrowserDiagramCanvas(width, height-50);
		//instantiate the listof current steps (model)
		steps = new ArrayList<Step>();
		//instantiate the listof current wfsteps (viww)
		wfsteps = new ArrayList<WfStep>();

		if (dc == null) {
			centerPanel.clear();
			dc = new MyDiagramController(canvas, eventBus);
			dc.showGrid(true);
			dc.addTieLinkHandler(linkHandler);
			centerPanel.setVerticalAlignment(HasAlignment.ALIGN_TOP);
			centerPanel.add(dc.getView());
			//instantiate the drag controller
			dragController = new PickupDragController(dc.getView(), true);		

			dragController.setBehaviorConstrainedToBoundaryPanel(false);
			dc.addDeleteOptionInContextualMenu("Delete this edge");
			dc.addSetStraightOptionInContextualMenu("Set Straight");
			
			DiagramCanvas canvas2 = new MultiBrowserDiagramCanvas(width, height-(height-50));
			DiagramController dc2 = new DiagramController(canvas2);
			dc2.showGrid(true);
			dropPanel.add(dc2.getView());
			HTML remove = new HTML("Remove Step");
			Image trashbin = new Image(WfTemplatesConstants.TRASH_IMAGE);
			trashbin.addStyleName("removePanel");
			remove.addStyleName("removePanel");
			dc2.addWidget(remove, width/2-60, 10);
			dc2.addWidget(trashbin,  width/2-97, 10);
			dropPanel.setPixelSize(width, 50);
			dropPanel.getElement().getStyle().setBackgroundColor("#333");
			dropPanel.getElement().getStyle().setBackgroundColor("rgba(51,51,51,0.30");
			dropPanel.getElement().getStyle().setOpacity(0.5);

			//	dropPanel.add(targetPanel);
		
			centerPanel.add(dropPanel);
			dropPanel.setVisible(false);
			DropController dropController = new MyDropController(dropPanel, dc);
			dragController.registerDropController(dropController);
		}
		else {
			GWT.log("CLEARING DIAGRAM");
			dc.clearDiagram();
		}
		return canvas;

	}
	private void makeDraggable(Widget toDrag) {
		dragController.makeDraggable(toDrag);
	}

	
	public void enableDropPanel(boolean enabled) {
			dropPanel.setVisible(enabled);
	}
	/**
	 * add a new step to the canvas
	 */

	public void addNewStep(String stepName, String description, int...leftTop) {
		if (! (leftTop.length == 2 || leftTop.length == 0) )
			throw new IllegalArgumentException("Only int left int top permitted or none of them");
		WfStep toAdd = new WfStep(stepName, description);
		int left = 50;
		int top = 50;
		if (leftTop.length == 2) {
			left = leftTop[0];
			top = leftTop[1];
		}
		dc.addWidget(toAdd, left, top);
		makeDraggable(toAdd);

		//save the step
		steps.add(new Step(left, top, stepName, description, null));
		wfsteps.add(toAdd);
	}



	@Override
	public void addNewRoles(Connection selectedEdge, String toDisplay) {		
		Label decorationLabel = new Label(toDisplay);
		decorationLabel.getElement().getStyle().setPadding(2, Unit.PX);
		decorationLabel.getElement().getStyle().setBackgroundColor("#FFF");
		decorationLabel.getElement().getStyle().setProperty("border", "1px dashed black");
		dc.addDecoration(decorationLabel, selectedEdge);
		//		
	}



	public void enableSaveButton(boolean enabled) {
		this.saveButton.setEnabled(enabled);

	}
	/**
	 * updateSize of the app depending on the window size
	 */
	private void updateSize() {
		RootPanel workspace = RootPanel.get(WorkflowTemplates.PORTLET_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = Window.getClientWidth() - leftBorder;
		mainPanel.setPixelSize(rootWidth-10, rootHeight-30);
		layoutPanel.setPixelSize(rootWidth-10, rootHeight-30);
		westPanel.setSize("100%", "100%");
		centerPanel.setSize("100%", "100%");
//		mainPanel.setSize(""+(rootWidth-30), ""+(rootHeight-30));
//		layoutPanel.setSize(""+(rootWidth-30), ""+(rootHeight-30));
	}
	@Override
	public Widget asWidget() {
		return this;
	}
	@Override
	public void showLoading(boolean show) {
		VerticalPanel load = new VerticalPanel();
		if (show) {			
			load.setWidth("100%");
			RootPanel workspace = RootPanel.get();
			int topBorder = workspace.getAbsoluteTop();
			int rootHeight = Window.getClientHeight() - topBorder - 100;
			load.setHeight(rootHeight+"px");
			load.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
			load.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			load.add(new Image(WfTemplatesConstants.LOADING));
			westPanel.add(load);
		}
		else {
			westPanel.clear();
		}
	}
	@Override
	public void enableGraphControlPanel(boolean enabled) {
		southPanel.setVisible(true);
		saveButton.setEnabled(enabled);
		addNewStepButton.setEnabled(enabled);
		resetButton.setEnabled(enabled);

	}

	@Override
	public void enableDeleteButton(boolean enabled) {
		deleteButton.setEnabled(enabled);

	}







}
