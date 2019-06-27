package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.DataTypePropertiesCombo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.GlobalVariablesProperties;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.GlobalVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GlobalVariablesPanel extends ContentPanel {

	@SuppressWarnings("unused")
	private EventBus eventBus;
	private ListStore<GlobalVariables> storeGlobalVariable;
	private Grid<GlobalVariables> gridGlobalVariable;
	private GridRowEditing<GlobalVariables> gridGlobalVariableEditing;
	private TextButton btnAdd;
	private boolean addStatus;
	private int seqGlobalVariables = 0;
	private ListStore<DataType> storeComboInputType;
	private ComboBox<DataType> comboInputType;

	interface InputTypeTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}

	public GlobalVariablesPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("GlobalVariablesPanel");
		this.eventBus = eventBus;

		// msgs = GWT.create(ServiceCategoryMessages.class);
		try {
			init();
			create(project);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void init() {
		setHeaderVisible(false);
		setResize(true);
		setBodyBorder(false);
		setBorders(false);
		forceLayoutOnResize = true;
	}

	private void create(Project project) {
		// Grid
		GlobalVariablesProperties props = GWT.create(GlobalVariablesProperties.class);

		ColumnConfig<GlobalVariables, String> nameColumn = new ColumnConfig<GlobalVariables, String>(props.name(), 100,
				"Name");
		// nameColumn.setMenuDisabled(true);

		ColumnConfig<GlobalVariables, String> descriptionColumn = new ColumnConfig<GlobalVariables, String>(
				props.description(), 100, "Description");
		// descriptionColumn.setMenuDisabled(true);

		ColumnConfig<GlobalVariables, DataType> inputTypeColumn = new ColumnConfig<GlobalVariables, DataType>(
				props.dataType(), 100, "Type");
		// inputTypeColumn.setMenuDisabled(true);
		inputTypeColumn.setCell(new AbstractCell<DataType>() {

			@Override
			public void render(Context context, DataType inputType, SafeHtmlBuilder sb) {
				InputTypeTemplates inputTypeTemplates = GWT.create(InputTypeTemplates.class);
				sb.append(inputTypeTemplates.format(inputType.getLabel()));
			}
		});

		ColumnConfig<GlobalVariables, String> defaultValueColumn = new ColumnConfig<GlobalVariables, String>(
				props.defaultValue(), 100, "Default");
		// defaColumn.setMenuDisabled(true);

		ArrayList<ColumnConfig<GlobalVariables, ?>> l = new ArrayList<ColumnConfig<GlobalVariables, ?>>();
		l.add(nameColumn);
		l.add(descriptionColumn);
		l.add(inputTypeColumn);
		l.add(defaultValueColumn);

		ColumnModel<GlobalVariables> columns = new ColumnModel<GlobalVariables>(l);

		storeGlobalVariable = new ListStore<GlobalVariables>(props.id());

		if (project != null && project.getInputData() != null
				&& project.getInputData().getListGlobalVariables() != null) {
			storeGlobalVariable.addAll(project.getInputData().getListGlobalVariables());
			seqGlobalVariables = project.getInputData().getListGlobalVariables().size();
		} else {
			seqGlobalVariables = 0;
		}

		final GridSelectionModel<GlobalVariables> sm = new GridSelectionModel<GlobalVariables>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		gridGlobalVariable = new Grid<GlobalVariables>(storeGlobalVariable, columns);
		gridGlobalVariable.setSelectionModel(sm);
		gridGlobalVariable.getView().setStripeRows(true);
		gridGlobalVariable.getView().setColumnLines(true);
		gridGlobalVariable.getView().setAutoExpandColumn(nameColumn);
		gridGlobalVariable.getView().setAutoFill(true);
		gridGlobalVariable.setBorders(false);
		gridGlobalVariable.setColumnReordering(false);

		// DND
		GridDragSource<GlobalVariables> ds = new GridDragSource<GlobalVariables>(gridGlobalVariable);
		ds.addDragStartHandler(new DndDragStartEvent.DndDragStartHandler() {

			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<GlobalVariables> draggingSelection = (ArrayList<GlobalVariables>) event.getData();
				Log.debug("Start Drag: " + draggingSelection);

			}
		});

		GridDropTarget<GlobalVariables> dt = new GridDropTarget<GlobalVariables>(gridGlobalVariable);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// EDITING //
		// Key

		DataTypePropertiesCombo inputTypePropertiesCombo = GWT.create(DataTypePropertiesCombo.class);

		storeComboInputType = new ListStore<DataType>(inputTypePropertiesCombo.id());

		comboInputType = new ComboBox<DataType>(storeComboInputType, inputTypePropertiesCombo.label());
		comboInputType.setClearValueOnParseError(false);
		comboInputType.setEditable(false);

		comboInputType.setTriggerAction(TriggerAction.ALL);
		addHandlersForComboInputType(inputTypePropertiesCombo.label());

		//
		TextField nameColumnEditing = new TextField();
		nameColumnEditing.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));
		TextField descriptionColumnEditing = new TextField();
		descriptionColumnEditing.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));
		TextField defaultValueColumnEditing = new TextField();
		defaultValueColumnEditing.addValidator(
				new RegExValidator("^[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*$", "Attention character \" is not allowed"));

		gridGlobalVariableEditing = new GridRowEditing<GlobalVariables>(gridGlobalVariable);
		gridGlobalVariableEditing.addEditor(nameColumn, nameColumnEditing);
		gridGlobalVariableEditing.addEditor(descriptionColumn, descriptionColumnEditing);
		gridGlobalVariableEditing.addEditor(inputTypeColumn, comboInputType);
		gridGlobalVariableEditing.addEditor(defaultValueColumn, defaultValueColumnEditing);

		btnAdd = new TextButton("Add");
		btnAdd.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		btnAdd.setScale(ButtonScale.SMALL);
		btnAdd.setIconAlign(IconAlign.LEFT);
		btnAdd.setToolTip("Add Global Variable");
		btnAdd.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addGlobalVariable(event);
			}

		});

		TextButton btnTest = new TextButton("Test");
		btnTest.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		btnTest.setScale(ButtonScale.SMALL);
		btnTest.setIconAlign(IconAlign.LEFT);
		btnTest.setToolTip("Test");
		btnTest.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				testGrid(event);
			}

		});

		TextButton btnDelete = new TextButton("Delete");
		btnDelete.addSelectHandler(new SelectEvent.SelectHandler() {
			public void onSelect(SelectEvent event) {
				GridCell cell = gridGlobalVariableEditing.getActiveCell();
				int rowIndex = cell.getRow();

				gridGlobalVariableEditing.cancelEditing();

				storeGlobalVariable.remove(rowIndex);
				storeGlobalVariable.commitChanges();

				gridGlobalVariableEditing.getCancelButton().setVisible(true);
				btnAdd.setEnabled(true);
				if (addStatus) {
					addStatus = false;
				}

				List<GlobalVariables> listSelected = storeGlobalVariable.getAll();
				List<GlobalVariables> listNewSelected = new ArrayList<GlobalVariables>();
				for (int i = 0; i < listSelected.size(); i++) {
					GlobalVariables var = listSelected.get(i);
					var.setId(i);
					listNewSelected.add(var);
				}

				storeGlobalVariable.clear();
				storeGlobalVariable.addAll(listNewSelected);
				storeGlobalVariable.commitChanges();

				seqGlobalVariables = listNewSelected.size();
				Log.debug("Current Seq: " + seqGlobalVariables);

			}
		});
		ButtonBar buttonBar = gridGlobalVariableEditing.getButtonBar();
		buttonBar.add(btnDelete);

		gridGlobalVariableEditing.addBeforeStartEditHandler(new BeforeStartEditHandler<GlobalVariables>() {

			@Override
			public void onBeforeStartEdit(BeforeStartEditEvent<GlobalVariables> event) {
				editingBeforeStart(event);

			}
		});

		gridGlobalVariableEditing.addCancelEditHandler(new CancelEditHandler<GlobalVariables>() {

			@Override
			public void onCancelEdit(CancelEditEvent<GlobalVariables> event) {
				storeGlobalVariable.rejectChanges();
				btnAdd.setEnabled(true);

			}

		});

		gridGlobalVariableEditing.addCompleteEditHandler(new CompleteEditHandler<GlobalVariables>() {

			@Override
			public void onCompleteEdit(CompleteEditEvent<GlobalVariables> event) {
				try {
					if (addStatus) {
						addStatus = false;
					}
					storeGlobalVariable.commitChanges();

					gridGlobalVariableEditing.getCancelButton().setVisible(true);
					btnAdd.setEnabled(true);

				} catch (Throwable e) {
					Log.error("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		});

		ToolBar toolBar = new ToolBar();
		toolBar.add(btnAdd, new BoxLayoutData(new Margins(0)));

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setAdjustForScroll(false);
		vlc.setScrollMode(ScrollMode.NONE);

		vlc.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(gridGlobalVariable, new VerticalLayoutData(1, 1, new Margins(0)));

		add(vlc, new MarginData(new Margins(0)));

	}

	private void testGrid(SelectEvent event) {
		final ConfirmMessageBox mb = new ConfirmMessageBox("Attention", "" + getGlobalVariables());
		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					break;
				case YES:
					break;
				default:
					break;
				}

			}
		});
		mb.setWidth(300);
		mb.show();

	}

	private void editingBeforeStart(BeforeStartEditEvent<GlobalVariables> event) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboInputType(LabelProvider<DataType> idI18N) {
		// TODO Auto-generated method stub

	}

	private void addGlobalVariable(SelectEvent event) {
		try {
			seqGlobalVariables++;
			GlobalVariables newGlobalVariable = new GlobalVariables(seqGlobalVariables, "", "", "", DataType.STRING);
			Log.debug("New Global Variable: " + newGlobalVariable);
			gridGlobalVariableEditing.cancelEditing();
			addStatus = true;
			gridGlobalVariableEditing.getCancelButton().setVisible(false);
			storeGlobalVariable.add(newGlobalVariable);
			int row = storeGlobalVariable.indexOf(newGlobalVariable);

			storeComboInputType.clear();

			storeComboInputType.addAll(DataType.asListForGlobalVariables());
			storeComboInputType.commitChanges();

			gridGlobalVariableEditing.startEditing(new GridCell(row, 0));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void update(Project project) {
		if (project != null && project.getInputData() != null
				&& project.getInputData().getListGlobalVariables() != null) {
			storeGlobalVariable.clear();
			storeGlobalVariable.addAll(project.getInputData().getListGlobalVariables());
			storeGlobalVariable.commitChanges();
			seqGlobalVariables = project.getInputData().getListGlobalVariables().size();

		} else {
			storeGlobalVariable.clear();
			storeGlobalVariable.commitChanges();
			seqGlobalVariables = 0;
		}
	}

	public ArrayList<GlobalVariables> getGlobalVariables() {
		ArrayList<GlobalVariables> listGlobalVarialbles = new ArrayList<>(gridGlobalVariable.getStore().getAll());
		return listGlobalVarialbles;
	}

	public void clearVariables(Project project) {
		storeGlobalVariable.clear();
		storeGlobalVariable.commitChanges();
		seqGlobalVariables = 0;
		forceLayout();

	}

}
