package org.gcube.portlets.user.templates.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.richtext.RichTextToolbar;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.user.templates.client.HeaderBar;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.Templates;
import org.gcube.portlets.user.templates.client.TitleBar;
import org.gcube.portlets.user.templates.client.WorkspacePanel;
import org.gcube.portlets.user.templates.client.components.AttributeArea;
import org.gcube.portlets.user.templates.client.components.AttributeDialog;
import org.gcube.portlets.user.templates.client.components.BasicTextArea;
import org.gcube.portlets.user.templates.client.components.CommentArea;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.components.D4sRichTextarea;
import org.gcube.portlets.user.templates.client.components.DefaultArea;
import org.gcube.portlets.user.templates.client.components.DoubleColumnPanel;
import org.gcube.portlets.user.templates.client.components.DroppingArea;
import org.gcube.portlets.user.templates.client.components.FakeTextArea;
import org.gcube.portlets.user.templates.client.components.GenericTable;
import org.gcube.portlets.user.templates.client.components.GroupingDelimiterArea;
import org.gcube.portlets.user.templates.client.components.GroupingInnerArea;
import org.gcube.portlets.user.templates.client.components.ImageArea;
import org.gcube.portlets.user.templates.client.components.InstructionArea;
import org.gcube.portlets.user.templates.client.components.ClientRepeatableSequence;
import org.gcube.portlets.user.templates.client.dialogs.RepeatSequenceDialog;
import org.gcube.portlets.user.templates.client.dialogs.TablePropertyDialog;
import org.gcube.portlets.user.templates.client.dialogs.ToolboxDialog;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.model.TemplateSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> Presenter </code> class acts as the Controller in the MVP pattern
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */

public class Presenter {	

	/**
	 *  View part
	 */
	private WorkspacePanel wp;

	private HeaderBar header;

	private TitleBar titleBar;

	//private	TemplateGenerator tg;

	private String currentUser;
	private String currentScope;
	int counter = 1;

	private CommonCommands commonCommands;
	/**
	 *  Model
	 */
	private TemplateModel model;


	private int currFocus;

	RichTextToolbar currentSelectedToolbar;
	RichTextArea currentSelectedTextArea;

	private ToolboxDialog tbPanel;

	private ReportUIComponent selectedComponent;

	/**
	 * 
	 * @param singleton . the singleton
	 */
	public Presenter(final TemplateModel model) {
		this.model = model;
		this.wp = new WorkspacePanel(this.model, this);

		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {}

			public void onSuccess(String[] result) {
				currentUser = result[0];
				currentScope = result[1];

				AsyncCallback<Model> readCallback = new AsyncCallback<Model>() {
					public void onFailure(Throwable caught) { }
					public void onSuccess(Model result) {

						if (result != null)
							loadModel(result);
						else
							wp.addFirstTextArea();
					}
				};				
				model.getModelService().readTemplateFromSession(readCallback);
			}
		};
		model.getModelService().getUserAndScope(callback);
		commonCommands = new CommonCommands(this);
		showToolbox();
	}	

	public void showToolbox() {
		tbPanel = new ToolboxDialog(this, 800, wp.getAbsoluteTop()+50);
		tbPanel.show();
	}
	/**
	 * put the commands in the hashmap
	 */
	private HashMap<String, Command> getCommands() {
		/**
		 * commands to pass to the toolbar
		 */
		HashMap<String, Command> toReturn = new HashMap<String, Command>();

		Command  newTemplate= new Command() {

			public void execute() {
				getModel().resetModelInSession();
			}
		};

		toReturn.put("save", commonCommands.saveTemplate);
		toReturn.put("newdoc", newTemplate);
		toReturn.put("open_template", commonCommands.openTemplate);
		toReturn.put("importing", commonCommands.importTemplateCommand);
		toReturn.put("insertImage", commonCommands.insertImage);
		toReturn.put("pickColor", commonCommands.pickColor);

		return toReturn;

	}

	/**
	 * called when nextPage Button is Clicked
	 */
	public void addNewPage() {
		seekLastPage();
		cleanWorkspace();
		model.insertNewPage();
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
		titleBar.hideNextButton();
		titleBar.showPrevButton();
		setCurrCursorPos(-1);
		addFakeTextArea(0, false);
	}



	/**
	 * in case someone inser a new section when not in the last page
	 */
	public void seekLastPage() {
		while (! (model.getCurrentPage() == model.getTotalPages()) )
			nextPageButtonClicked();
	}
	/**
	 * in case someone imported a new section 
	 * @param sect2Seek .
	 */
	public void seekSection(int sect2Seek) {
		loadFirstSection();
		while (! ( model.getCurrentPage() == sect2Seek) )
			nextPageButtonClicked();
	}



	/**
	 * add the text format toolbar on the top
	 */
	public void addTextToolBar() {
		RichTextToolbar rtbar = new RichTextToolbar(new RichTextArea(), false, getCommands(), false);
		SimplePanel deco = new SimplePanel();
		rtbar.setEnabled(false);
		deco.add(rtbar);
		deco.setSize("100%", "25");
		rtbar.setWidth("100%");	
		Templates.get().getToolbarPanel().add(deco);
	}


	/**
	 * changes the template name
	 * @param newName .
	 */
	public void changeTemplateName(String newName) {
		//		---> TO MODEL
		model.setTemplateName(newName);
		//		---> TO VIEW		
		Templates.get().getTitleHeader().setTemplateName(newName);
	}

	public void setEditedOnBy(Date date, String username) {
		Templates.get().getTitleHeader().setEditedOnBy(date, username);
	}


	public TitleBar getTitleBar() {
		return titleBar;
	}

	public void setTitleBar(TitleBar titleBar) {
		this.titleBar = titleBar;
	}

	/**
	 * remove the user-added components from the workspace, and from the model
	 *
	 */
	public void cleanAll() {
		//		reset the model
		model = new TemplateModel();
		//reset the UI

		//give the new model instance 
		header.setModel(model);
		wp.setModel(model);

		cleanWorkspace();
		titleBar.hideNextButton();
		titleBar.hidePrevButton();
		titleBar.setTemplateName(model.getTemplateName());
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
		titleBar.clearEditedOnBy();
		//resizeWorkingArea(model.getPageWidth(), model.getPageHeight());


	}
	/**
	 * remove the user-added components from the workspace (in the current page) but not from the model 
	 *
	 */
	public void cleanWorkspace() {
		wp.getMainLayout().clear();
		Templates.get().getScrollerPanel().setScrollPosition(0);
	}


	/**
	 * to remove the current displayed section
	 */
	public void discardCurrentSection() {
		if (model.getTotalPages() == 1)
			Window.alert("Cannot discard section, need ad least 2");
		else {
			boolean result = Window.confirm("Are you sure you want to discard section number " + model.getCurrentPage() + "?");
			if (result) {
				TemplateSection removed = model.discardSection(model.getCurrentPage());
				if (removed == null)
					GWT.log("REMOVED NOTHING", null);
				else
					GWT.log("REMOVED " + removed.getAllComponents().size(), null);
				loadFirstSection();
			}
		}
	}
	/**
	 * enable the format text toolbar for the given Rich Textarea passed as argument
	 * @param d4sArea the enabled text area
	 */
	public void enableTextToolBar(RichTextArea d4sArea) {
		RichTextToolbar rtbar = new RichTextToolbar(d4sArea, false,  getCommands(), false);
		currentSelectedToolbar = rtbar;
		currentSelectedTextArea = d4sArea;

		rtbar.setEnabled(true);
		Templates.get().getToolbarPanel().clear();
		SimplePanel deco = new SimplePanel();
		deco.add(rtbar);
		deco.setSize("100%", "25");
		rtbar.setWidth("100%");		
		Templates.get().getToolbarPanel().add(deco);
	}



	/**
	 * Import a Section in the View and in the Model
	 * @param toLoad the SerializableModel instance where toget the section
	 * @param sectionNoToimport section to import 0 -> n-1
	 * @param beforeSection say where to import this section (before)
	 * @param asLastSection say to import this section as last section in the curren template / report 
	 */
	public void importSection(Model toLoad, int sectionNoToimport, int beforeSection, boolean asLastSection) {
		model.importSectionInModel(this, toLoad, sectionNoToimport, beforeSection, asLastSection);
		if (asLastSection) 		
			seekLastPage();
		else 
			seekSection(beforeSection);
		Window.alert("Importing Complete");
	}
	/**
	 * generate a random number between 0 and 10^4
	 * @return
	 */
	private int generateRandom() {
		return (int) (Math.random() * 10000) + 1;
	}

	/**
	 * @return the header UI component
	 */
	public HeaderBar getHeader() {
		return header;
	}

	/**
	 * 
	 * @return the model
	 */
	public TemplateModel getModel() {
		return model;
	}

	/**
	 * 
	 * @return .
	 */
	public WorkspacePanel getWp() {
		return wp;
	}
	/**
	 * allow to repeat a sequence of compoenents in the report
	 */
	public void openGroupingDialog() {
		RepeatSequenceDialog dlg = new RepeatSequenceDialog(this);
		dlg.show();
		dlg.center();
	}

	/**
	 * insert a repeatable sequence in the view and in the model
	 * @param types the sequence that was chosen
	 */
	public void insertGroup(ComponentType ... types) {
		Coords start = getInsertionPoint();

		//insert the repeat seq.
		ClientRepeatableSequence repSeq = new ClientRepeatableSequence(this, model, types);

		//insert the sequence
		start = getInsertionPoint();
		int index = wp.addComponentToLayout(repSeq, false);
		//construct the component to store in the model
		TemplateComponent repSequence = new TemplateComponent(this.model, start.getX(), start.getY(), TemplateModel.TEMPLATE_WIDTH - 50, repSeq.getHeight(), 
				model.getCurrentPage(), repSeq.getType(), "", repSeq);
		model.addComponentToModel(repSequence, index);
		
		start = getInsertionPoint();
		int height = 30;
		addFakeTextArea(start.getY()+height, false);
		model.storeInSession();

	}
	
	public TemplateComponent getGroupInnerArea() {
		Coords start = getInsertionPoint();
		GroupingInnerArea spacer = new GroupingInnerArea();
		return new TemplateComponent(model, 0, start.getY(), TemplateModel.TEMPLATE_WIDTH, 25, 
				model.getCurrentPage(), ComponentType.REPEAT_SEQUENCE_INNER, "", spacer);
	}
	
	
	/**
	 * Create the DroppingImage object
	 */
	public TemplateComponent createDroppingArea(ComponentType type, int width, int height, boolean hideControls) {
		Coords start = getInsertionPoint();
		DroppingArea imageDropping = new DroppingArea(this, start.getX(), start.getY(), width, height, false);
		if (hideControls)
			imageDropping.hideCloseButton();
		String paramName = "image-" + generateRandom();
		//		construct the component
		TemplateComponent toAdd = 
				new TemplateComponent(this.model, start.getX(), start.getY(),
						width, height, model.getCurrentPage(), ComponentType.DYNA_IMAGE, paramName, imageDropping);
		return toAdd;
	}
	/**
	 * 
	 * @param type
	 * @param width
	 * @param height
	 */
	public void insertDroppingArea(ComponentType type, int width, int height) {
		TemplateComponent toAdd = createDroppingArea(type, width, height, false);
		ReportUIComponent droppingImage = (ReportUIComponent) toAdd.getContent();
			
		//add the component to the layout ---> TO VIEW
		int toInsert = wp.addComponentToLayout(droppingImage, false);

		//add the component to the model  ---> TO MODEL
		model.addComponentToModel(toAdd, toInsert);
		Coords start = getInsertionPoint();
		addFakeTextArea(start.getY()+height, false);
	}
	/**
	 * called when add page break command is Clicked
	 */
	public TemplateComponent addPageBreak() {
		Coords start = getInsertionPoint();
		int width = TemplateModel.TEMPLATE_WIDTH - 50;
		int height = 30;

		DefaultArea imgToPlace = 
				new DefaultArea(ComponentType.PAGEBREAK, this, start.getX(), start.getY(), width, height); 	

		imgToPlace.addStyleName("pagebreak");

		//		construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height,
				model.getCurrentPage(), ComponentType.PAGEBREAK, "", imgToPlace, false);



		//add the component to the layout ---> TO VIEW
		int toInsert = wp.addComponentToLayout(imgToPlace, false);
		//add the component to the model
		model.addComponentToModel(toAdd, toInsert);

		addFakeTextArea(start.getY()+height, false);
		return toAdd;
	}


	/**
	 * insert a new static area in the UI and in the model
	 * 
	 * @param start .
	 * @param x .
	 * @param y .
	 */
	public TemplateComponent createStaticTextArea(ComponentType type, int width, int height, boolean hideControls) {
		Coords start = getInsertionPoint();
		ReportUIComponent textArea = null;
		if (type != ComponentType.BODY) {
			textArea = new BasicTextArea(type, this,  start.getX(), start.getY(), width, height);
		}
		else {
			textArea = new D4sRichTextarea(type, this, start.getX(), start.getY(), width, height);
		}	
		if (hideControls)
			textArea.hideCloseButton();

		//construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height, 
				model.getCurrentPage(), type, "", textArea);
		return toAdd;
	}

	public void insertStaticTextArea(ComponentType type, int width, int height) {
		TemplateComponent toAdd = createStaticTextArea(type, width, height, false);
		ReportUIComponent textArea = (ReportUIComponent) toAdd.getContent();

		//-add the component to the page --> to VIEW 
		int index = wp.addComponentToLayout(textArea, false);
		//add the component to the model --> To MODEL
		model.addComponentToModel(toAdd, index);

		Coords start = getInsertionPoint();
		addFakeTextArea(start.getY()+height, false);
	}
	/**
	 * 
	 * @param type
	 * @param rows
	 * @param cols
	 * @return
	 */
	public TemplateComponent createTable(ComponentType type, int rows, int cols, boolean hideControls){
		Coords start = getInsertionPoint();			
		//		construct the component
		GenericTable table = new GenericTable(rows, cols, type, this, start.getX(), start.getY(), TemplateModel.TEMPLATE_WIDTH - 50, GenericTable.DEFAULT_HEIGHT, hideControls);
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), TemplateModel.TEMPLATE_WIDTH - 50, 50,
				model.getCurrentPage(), type, "", table);
		return toAdd;
	}
	
	/**
	 * 
	 * @param start the coordinates x,y where to place the widget
	 * @param width .
	 * @param height .
	 * @param imgToPlace . 
	 */
	public void insertTable(ComponentType type, int rows, int cols) {
		Coords start = getInsertionPoint();				
		TemplateComponent toAdd = createTable(type, rows, cols, false);
	
		ReportUIComponent table = (ReportUIComponent) toAdd.getContent();

		//add the component to the layout ---> TO VIEW
		int index = wp.addComponentToLayout(table, false);
		
		model.addComponentToModel(toAdd, index);

		addFakeTextArea(start.getY()+table.getOffsetHeight(), false);
	}
	/**
	 * insert a new comment area in the UI and in the UI and model
	 * @param width .
	 * @param height .
	 */
	public TemplateComponent addCommentArea(int width, int height, boolean isGrouped){
		Coords start = getInsertionPoint();
		CommentArea ca = new CommentArea(this,  start.getX(), start.getY(), width, height);

		//construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height, 
				model.getCurrentPage(), ca.getType(), "", ca);


		//-add the component to the page --> to VIEW 
		int index = wp.addComponentToLayout(ca, false);
		ca.selectText();
		//add the component to the model --> To MODEL
		model.addComponentToModel(toAdd, index);


		addFakeTextArea(start.getY()+height, false);
		return toAdd;
	}
	/**
	 * insert a new instruction area in the UI and in the UI and model
	 * @param width .
	 * @param height .
	 */
	public TemplateComponent addInstructionArea(int width, int height, boolean isGrouped) {
		Coords start = getInsertionPoint();
		InstructionArea ca = new InstructionArea(this,  start.getX(), start.getY(), width, height);

		//construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height, 
				model.getCurrentPage(), ca.getType(), "", ca);


		//-add the component to the page --> to VIEW 
		int index = wp.addComponentToLayout(ca, false);
		ca.selectText();
		//add the component to the model --> To MODEL
		model.addComponentToModel(toAdd, index);


		addFakeTextArea(start.getY()+height, false);
		return toAdd;
	}
	/**
	 * insert a new attribute area in the UI and in the UI and model
	 * @param width .
	 * @param height .
	 * @param useCheckBox 
	 */
	public TemplateComponent createAttributArea(int width, int height, String name, String[] values, boolean useCheckBox, boolean displayBlock, boolean hideControls) {
		Coords start = getInsertionPoint();
		AttributeArea ca = null;
		if (useCheckBox)
			ca = new AttributeArea(this,  start.getX(), start.getY(), width, height, name, values, ComponentType.ATTRIBUTE_MULTI);
		else
			ca = new AttributeArea(this,  start.getX(), start.getY(), width, height, name, values, ComponentType.ATTRIBUTE_UNIQUE);
		
		//pass the display info to the Report
		Metadata mdDisplay = new Metadata();
		mdDisplay.setAttribute("display");
		mdDisplay.setValue(displayBlock ? "block" : "inline");
		ArrayList<Metadata> toPass = new ArrayList<Metadata>();
		toPass.add(mdDisplay);
		
		if (hideControls)
			ca.hideCloseButton();
		
		//construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height, 
				model.getCurrentPage(), ca.getType(), "", ca, toPass);	
		
		//this is important for edit
		ca.setComponent(toAdd);
		
		return toAdd;
	}
	/**
	 * 
	 * @param width
	 * @param height
	 * @param name the attr name
	 * @param values the values
	 * @param useCheckBox if true it will be a multi selection (checkbox), false a single selection (radiobox)
	 * @param displayBlock if true the items will be displayed as block, if false inline
	 */
	public void addAttributArea(int width, int height, String name, String[] values, boolean useCheckBox, boolean displayBlock) {
		TemplateComponent toAdd = createAttributArea(width, height, name, values, useCheckBox, displayBlock, false);
		AttributeArea ca = (AttributeArea) toAdd.getContent();

		//-add the component to the page --> to VIEW 
		int index = wp.addComponentToLayout(ca, false);

		//add the component to the model --> To MODEL
		model.addComponentToModel(toAdd, index);
		
		Coords start = getInsertionPoint();
		addFakeTextArea(start.getY()+height, false);
	}

	/**
	 * @param useCheckBox 
	 * 
	 */
	public void openAttributeDialog(int width, int height, boolean useCheckBox) {
		AttributeDialog dialog = new AttributeDialog(this,width, height, useCheckBox);
		dialog.show();
	}

	/**
	 * 
	 * @param widgetContainer .
	 */
	public void updateWidgetIndicesInModel(FlowPanel widgetContainer) {

		int widgetsNo = widgetContainer.getWidgetCount();
		for (int i = 0; i < widgetsNo; i++) {
			Widget toUpdate = widgetContainer.getWidget(i);
			model.updateModelComponentIndex(toUpdate, i);
		}
	}

	/**
	 * 
	 * @param start the coordinates x,y where to place the widget
	 * @param width .
	 * @param height .
	 * @param imgToPlace . 
	 */
	public void insertStaticImage(Coords start, int width, int height, ImageArea imgToPlace, boolean isDoubleColLayout) {
		GWT.log("Adding IMAGE " + start.getX() + " y=" + start.getY(), null);

		//		construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height,
				model.getCurrentPage(), ComponentType.STATIC_IMAGE, "", imgToPlace, isDoubleColLayout);



		//add the component to the layout ---> TO VIEW
		int index = wp.addComponentToLayout(imgToPlace, false);

		//add the component to the model
		model.addComponentToModel(toAdd, index);

		addFakeTextArea(start.getY()+height, false);

	}

	/**
	 * 
	 * @param start the coordinates x,y where to place the widget
	 * @param width .
	 * @param height .
	 * @param imgToPlace . 
	 */
	public void insertPlaceHolder(ComponentType type, int width, int height) {
		Coords start = getInsertionPoint();				
		DefaultArea imgToPlace = 
				new DefaultArea(type, this, start.getX(), start.getY(), width, height); 	

		if (type == ComponentType.TOC)
			imgToPlace.addStyleName("tocArea");
		else if (type == ComponentType.TIME_SERIES)
			imgToPlace.addStyleName("timeseriesArea");
		else if (type == ComponentType.BODY_TABLE_IMAGE) 
			imgToPlace.addStyleName("text-table-image");
		else
			imgToPlace.addStyleName("biblioArea");

		//		construct the component
		TemplateComponent toAdd = new TemplateComponent(this.model, start.getX(), start.getY(), width, height,
				model.getCurrentPage(), type, "", imgToPlace, false);

		//add the component to the layout ---> TO VIEW
		int index = wp.addComponentToLayout(imgToPlace, false);
		//add the component to the model
		model.addComponentToModel(toAdd, index);


		addFakeTextArea(start.getY()+height, false);

	}

	/**
	 * 
	 * @param type
	 * @param width
	 * @param height
	 */
	public void showTablePopup(ComponentType type, int width, int height) {
		TablePropertyDialog dlg = new TablePropertyDialog(this);
		dlg.show();
	}

	public int getSelectedIndex() {
		return currFocus;
	}

	public void setCurrCursorPos(int index) {
		this.currFocus = index;
	}

	public Coords getInsertionPoint() {
		int y = getSelectedIndex();
		return new Coords(25, y);
	}

	public Widget addFakeTextArea(int y, boolean doubleInsterted) {
		GWT.log("Setting on" + y, null);

		FakeTextArea toAdd = new FakeTextArea(counter++, this);
		TemplateComponent tc = new TemplateComponent(model, 0, y, TemplateModel.TEMPLATE_WIDTH, 25, 
				model.getCurrentPage(), ComponentType.FAKE_TEXTAREA, "", toAdd);


		int index = wp.addComponentToLayout(toAdd, true);
		if (doubleInsterted)
			model.addComponentToModel(tc, index+1);
		else
			model.addComponentToModel(tc, index);

		model.storeInSession();

		return toAdd;
	}

	public Widget addGroupingStart(int y) {
		GWT.log("Setting on" + y, null);

		FakeTextArea toAdd = new FakeTextArea(counter++, this);
		TemplateComponent tc = new TemplateComponent(model, 0, y, TemplateModel.TEMPLATE_WIDTH, 25, 
				model.getCurrentPage(), ComponentType.REPEAT_SEQUENCE_DELIMITER, "", toAdd);


		int index = wp.addComponentToLayout(toAdd, true);
		model.addComponentToModel(tc, index);

		model.storeInSession();

		return toAdd;
	}



	/**
	 * 
	 * @param leftItem
	 * @param rightItem
	 */
	public void insertDoubleColumnItems(ReportUIComponent leftItem, ReportUIComponent rightItem) {		
		Coords start = getInsertionPoint();

		int width = TGenConstants.DEFAULT_IMAGE_WIDTH;
		int height = TGenConstants.DEFAULT_IMAGE_HEIGHT;

		//		//construct the component
		TemplateComponent toAddLeft = new TemplateComponent(this.model, start.getX(), start.getY(), width, height, 
				model.getCurrentPage(), leftItem.getType(), "", leftItem, true);

		TemplateComponent toAddRight = new TemplateComponent(this.model, start.getX()+500, start.getY(), width, height, 
				model.getCurrentPage(), rightItem.getType(), "", rightItem, true);

		DoubleColumnPanel toAdd = new DoubleColumnPanel(leftItem, rightItem);

		int index = wp.addComponentToLayout(toAdd, false);

		//		//add the component to the model --> To MODEL
		model.addComponentToModel(toAddLeft, index);

		index = wp.addComponentToLayout(new HTML(), false);
		model.addComponentToModel(toAddRight, index);

		addFakeTextArea(start.getY()+height, false);
	}

	/**
	 * 
	 * @param toMove .
	 * @param left .
	 * @param top .
	 */
	public void moveWidget(Widget toMove, int left, int top) {
		//	wp.getMainLayout().add(toMove);
		wp.moveWidget(toMove, left, top);
	}


	/**
	 * called when nextPage Button is Clicked
	 */
	public void nextPageButtonClicked() {
		cleanWorkspace();
		//refresh the current page in the model
		model.setCurrentPage(model.getCurrentPage() + 1);

		//refresh the current page in the UI
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());

		//read the previous user added elements to the template page from the model and place them back in the UI
		placeTemplatePageElements(model.getCurrentPage());

		if (model.getCurrentPage() == model.getTotalPages()) 
			titleBar.hideNextButton();
		else
			titleBar.showNextButton();

		if (model.getCurrentPage() == 1)
			titleBar.hidePrevButton();
		else
			titleBar.showPrevButton();
	}

	/**
	 * load the template to edit in the MODEL and in the VIEW
	 * @param templateToOpen the name of the template to open without extension nor path
	 */
	public void openTemplate(String templateToOpen, String TemplateObjectID) {
		//will asyncrously return a SerializableModel instance read from disk
		model.getModelService().readModel(templateToOpen, TemplateObjectID, false, new AsyncCallback<Model>() {
			public void onFailure(Throwable caught) {
				Window.alert("OPS! we can't open this template: " + caught.getMessage());
			}

			public void onSuccess(Model toLoad) {
				if (toLoad == null)
					Window.alert("OPS! it seems you are trying to open an old version, only Templates 3.0+ are supported");
				if (toLoad.getPageWidth() == TemplateModel.OLD_TEMPLATE_WIDTH)
					Window.alert("OPS! we think you are trying to open a previuos version template, only gCube Templates 1.5+ are supported");
				else
					loadModel(toLoad);
			}
		});
	}

	private void loadModel(Model toLoad) {
		//    	reset the UI	
		cleanAll();

		//load the serializable model in my Model 
		model.loadModel(toLoad, this);

		wp.setModel(model);

		titleBar.setTemplateName(model.getTemplateName());
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
		setEditedOnBy(model.getLastEdit(), model.getLastEditBy());

		int currPage = model.getCurrentPage();
		//load the UI components of the current page
		GWT.log("READ CURR PAGE"+currPage, null);
		placeTemplatePageElements(currPage);

		//if there is more than one page place in the UI the next page button 
		if (currPage < model.getTotalPages()) {
			titleBar.showNextButton();
		}
		if (currPage > 1)
			titleBar.showPrevButton();

	}

	private void loadFirstSection() {
		//reset the UI
		cleanWorkspace();
		titleBar.hideNextButton();
		titleBar.hidePrevButton();
		model.setCurrentPage(1);
		wp.getMainLayout().setStyleName("templateFrame");
		wp.getMainLayout().addStyleName("position-relative");

		titleBar.setTemplateName(model.getTemplateName());
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());

		int currPage = model.getCurrentPage();
		//load the UI components of the current page
		GWT.log("READ CURR PAGE"+currPage, null);
		placeTemplatePageElements(currPage);

		//if there is more than one page place in the UI the next page button 
		if (currPage < model.getTotalPages()) {
			titleBar.showNextButton();
		}
		if (currPage > 1)
			titleBar.showPrevButton();

	}

	/**
	 * It places back the user added widgets (TemplateComponents) in the page
	 * 
	 * @param pageNo . the page number of the wanted TemplateComponent(s)
	 */

	public void placeTemplatePageElements(int pageNo) {
		setCurrCursorPos(-1);
		if (! (model.getSectionComponent(pageNo) == null)) {
			List<TemplateComponent> pageElems = model.getSectionComponent(pageNo);

			for (TemplateComponent component : pageElems) {

				GWT.log("Reading component.. " + component.getType() + " locked?" + component.isLocked(), null);

				int uiX = component.getX();
				int uiY= component.getY();		
				switch (component.getType()) {	
				case HEADING_1:				
				case HEADING_2:					
				case HEADING_3:	
				case HEADING_4:
				case HEADING_5:
				case BODY_NOT_FORMATTED:
				case TITLE:					
					BasicTextArea textArea = (BasicTextArea) component.getContent();
					textArea.get().setTop(uiY);
					textArea.get().setLeft(uiX);
					wp.addComponentToLayoutSystem(textArea,  component.isDoubleColLayout());
					break;
				case BODY:					
					D4sRichTextarea textArea2 = (D4sRichTextarea) component.getContent();
					textArea2.get().setTop(uiY);
					textArea2.get().setLeft(uiX);
					wp.addComponentToLayoutSystem(textArea2,  component.isDoubleColLayout());
					break;
				case STATIC_IMAGE: 
					ImageArea imgToPlace = (ImageArea) component.getContent();
					wp.addComponentToLayoutSystem(imgToPlace,  component.isDoubleColLayout());
					break;
				case DYNA_IMAGE: 
					DroppingArea imageDropping = (DroppingArea) component.getContent();
					wp.addComponentToLayoutSystem(imageDropping,  component.isDoubleColLayout());
					break;
				case FAKE_TEXTAREA:
					FakeTextArea toAdd = (FakeTextArea) component.getContent();
					wp.addComponentToLayoutSystem(toAdd,  component.isDoubleColLayout());
					break;
				case TOC:
					DefaultArea dp = (DefaultArea) component.getContent();
					wp.addComponentToLayoutSystem(dp,  component.isDoubleColLayout());
					break;
				case BODY_TABLE_IMAGE:
					DefaultArea bo = (DefaultArea) component.getContent();
					wp.addComponentToLayoutSystem(bo,  component.isDoubleColLayout());
					break;
				case BIBLIO:
					DefaultArea dp2 = (DefaultArea) component.getContent();
					wp.addComponentToLayoutSystem(dp2,  component.isDoubleColLayout());
				case PAGEBREAK:
					DefaultArea dp3 = (DefaultArea) component.getContent();
					wp.addComponentToLayoutSystem(dp3,  component.isDoubleColLayout());
					break;
				case TIME_SERIES:
					DefaultArea dp5 = (DefaultArea) component.getContent();
					wp.addComponentToLayoutSystem(dp5,  component.isDoubleColLayout());
					break;
				case FLEX_TABLE:
					GenericTable gt = (GenericTable) component.getContent();
					GWT.log("Reading TABLE rows: " + gt.getRowsNo()  + " cols: " + gt.getCols());
					wp.addComponentToLayoutSystem(gt,  component.isDoubleColLayout());
					break;
				case COMMENT:
					CommentArea ca = (CommentArea) component.getContent();
					wp.addComponentToLayoutSystem(ca,  false);
					break;
				case ATTRIBUTE:
				case ATTRIBUTE_MULTI:
				case ATTRIBUTE_UNIQUE:
					AttributeArea at = (AttributeArea) component.getContent();
					at.setComponent(component); //important for editing it
					wp.addComponentToLayoutSystem(at,  false);
					break;
				case INSTRUCTION:
					InstructionArea in = (InstructionArea) component.getContent();
					wp.addComponentToLayoutSystem(in,  false);
					break;
				case REPEAT_SEQUENCE_DELIMITER:
					GroupingDelimiterArea gpa = (GroupingDelimiterArea) component.getContent();
					wp.addComponentToLayoutSystem(gpa,  component.isDoubleColLayout());
					break;
				case REPEAT_SEQUENCE:
					ClientRepeatableSequence rps = (ClientRepeatableSequence) component.getContent();
					wp.addComponentToLayoutSystem(rps,  component.isDoubleColLayout());
					break;

				}
			}
		}

	}

	/**
	 * called when prevPage Button is Clicked
	 */

	public void prevPageButtonClicked() {
		cleanWorkspace();
		//refresh the current page in the model
		model.setCurrentPage(model.getCurrentPage() - 1);

		//refresh the current page in the UI
		titleBar.setPageDisplayer(model.getCurrentPage(), model.getTotalPages());

		//read the previous user added elements to the template page from the model and place them back in the UI
		placeTemplatePageElements(model.getCurrentPage());

		if (model.getCurrentPage() == model.getTotalPages()) 
			titleBar.hideNextButton();
		else
			titleBar.showNextButton();

		if (model.getCurrentPage() == 1)
			titleBar.hidePrevButton();
		else
			titleBar.showPrevButton();
	}

	/**
	 * remove the user-added components both from UI and Model
	 * @param toRemove the widget to remove
	 * @return true if the user-added component has been successfully removed
	 */
	public boolean removeTemplateComponent(Widget toRemove) {
		GWT.log("removeTemplateComponent");
		TemplateComponent toCheck = checkIfDoubleColLayout(toRemove);
		if (toCheck.isDoubleColLayout()) {
			if (Window.confirm("This will remove both area, do you want to proceed?")) {

				//first remove the one you clicked X on
				//remove from the MODEL
				TemplateComponent comp = model.removeComponentFromModel(toRemove);

				DoubleColumnPanel dpanel =  (DoubleColumnPanel) wp.getMainLayout().getWidget( wp.getMainLayout().getWidgetIndex(toRemove.getParent()) );
				Widget otherOne = dpanel.getTheOtherOne(toRemove);
				model.removeComponentFromModel(otherOne);


				//remove from the VIEW	
				//				int indexHTMLspacer = wp.getMainLayout().getWidgetIndex(toRemove.getParent()) + 1;
				//				wp.getMainLayout().remove(indexHTMLspacer);
				boolean w = wp.removeComponentFromLayout(toRemove.getParent());	

				return w;
			} 
			else 
				return false;
		}
		else {
			//remove from the MODEL
			TemplateComponent comp = model.removeComponentFromModel(toRemove);
			//remove from the VIEW
			boolean w = wp.removeComponentFromLayout(toRemove);	

			return w;
		}
	}




	/**
	 * 
	 * @param tc
	 * @return
	 */
	public TemplateComponent checkIfDoubleColLayout(Widget toRemove) {

		int tcPage = model.getCurrentPage();
		List<TemplateComponent> templateElements = model.getSectionComponent(tcPage);
		TemplateComponent toReturn = null;
		for (int i = 0; i < templateElements.size(); i++) {
			TemplateComponent tc = templateElements.get(i);
			if (tc.getContent().equals(toRemove))
				return tc;			
		}
		return toReturn;
	}

	/**
	 * 
	 * @param toChange .
	 */
	public void storeChangeInSession(Widget toChange) {
		model.storeInSession();
	}

	/**
	 * 
	 * @param toRepos .
	 * @param newX .
	 * @param newY .
	 */
	public void repositionModelComponents(Widget toRepos, int newX, int newY) {
		model.repositionModelComponent(toRepos, newX, newY);
	}


	/**
	 * Resize the template componet in the model and in the UI
	 * 
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeTemplateComponent(Widget toResize, int newWidth, int newHeight) {
		wp.resizeWidget(toResize, newWidth, newHeight);
		model.resizeModelComponent(toResize, newWidth, newHeight);
	}

	/**
	 * Resize the template componet in the model and in the UI
	 * 
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeTemplateComponentInView(Widget toResize, int newWidth, int newHeight) {
		wp.resizeWidget(toResize, newWidth, newHeight);
	}

	/**
	 * 
	 * @param toLock .
	 * @param locked .
	 */
	public void lockComponent(Widget toLock, boolean locked) {
		model.lockComponent(toLock, locked);
	}

	/**
	 * Resize the template componet just the model 
	 * 
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeTemplateComponentInModel(Widget toResize, int newWidth, int newHeight) {
		model.resizeModelComponent(toResize, newWidth, newHeight);
	}
	/**
	 * 
	 * Save the current Template
	 */
	public void saveTemplate(String basketidToSaveIn) {
		model.serializeModel(basketidToSaveIn);
	}

	//	*** GETTERS and SETTERS for UI Panels

	/**
	 * 
	 * @param header .
	 */
	public void setHeader(HeaderBar header) {
		this.header = header;
	}

	/**
	 * 
	 * @param templateModel .
	 */
	public void setModel(TemplateModel templateModel) {
		this.model = templateModel;
	}

	/**
	 * 
	 * @param wp .
	 */
	public void setWp(WorkspacePanel wp) {
		this.wp = wp;
	}

	/**
	 * 
	 * @return the scope in which the application is running on
	 */
	public String getCurrentScope() {
		return currentScope;
	}

	/**
	 * 
	 * @return the user username who is using the application 
	 */
	public String getCurrentUser() {
		return currentUser;
	}

	/**
	 * 
	 * @return .
	 */
	public WorkspacePanel getWorkSpacePanel() {
		return wp;
	}


	public RichTextToolbar getCurrentSelected() {
		return currentSelectedToolbar;
	}
	public RichTextArea getCurrentSelectedTextArea() {
		return currentSelectedTextArea;
	}

	/**
	 * 
	 * @return the component selected
	 */
	public ReportUIComponent getSelectedComponent() {
		return selectedComponent;
	}
	/**
	 * set the selected compoenent
	 * @param selectedComponent .
	 */
	public void setSelectedComponent(ReportUIComponent selectedComponent) {
		this.selectedComponent = selectedComponent;
	}



}
