package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.DataTypePropertiesCombo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.IOTypePropertiesCombo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.InputOutputVariablesProperties;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
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

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InputOutputVariablesPanel extends ContentPanel {

	@SuppressWarnings("unused")
	private EventBus eventBus;
	private ListStore<InputOutputVariables> storeInputOutputVariables;
	private Grid<InputOutputVariables> gridInputOutputVariables;
	private ListStore<DataType> storeComboDataType;
	private ComboBox<DataType> comboDataType;
	private ListStore<IOType> storeComboIOType;
	private ComboBox<IOType> comboIOType;
	private GridRowEditing<InputOutputVariables> gridInputOutputVariablesEditing;
	private TextButton btnAdd;
	private boolean addStatus;
	private int seq = 0;

	interface DataTypeTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}

	interface IOTypeTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}

	public InputOutputVariablesPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("InputOutputVariablesPanel");
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
		if (project != null && project.getInputData() != null
				&& project.getInputData().getListInputOutputVariables() != null) {
			seq = project.getInputData().getListInputOutputVariables().size();
		} else {
			seq = 0;
		}

		// Grid
		InputOutputVariablesProperties props = GWT
				.create(InputOutputVariablesProperties.class);

		ColumnConfig<InputOutputVariables, String> nameColumn = new ColumnConfig<InputOutputVariables, String>(
				props.name(), 100, "Name");
		// nameColumn.setMenuDisabled(true);

		ColumnConfig<InputOutputVariables, String> descriptionColumn = new ColumnConfig<InputOutputVariables, String>(
				props.description(), 100, "Description");
		// descriptionColumn.setMenuDisabled(true);

		ColumnConfig<InputOutputVariables, DataType> dataTypeColumn = new ColumnConfig<InputOutputVariables, DataType>(
				props.dataType(), 100, "Type");
		// inputTypeColumn.setMenuDisabled(true);
		dataTypeColumn.setCell(new AbstractCell<DataType>() {

			@Override
			public void render(Context context, DataType inputType,
					SafeHtmlBuilder sb) {
				DataTypeTemplates inputTypeTemplates = GWT
						.create(DataTypeTemplates.class);
				sb.append(inputTypeTemplates.format(inputType.getLabel()));
			}
		});

		ColumnConfig<InputOutputVariables, String> defaultValueColumn = new ColumnConfig<InputOutputVariables, String>(
				props.defaultValue(), 100, "Default");
		// defaColumn.setMenuDisabled(true);

		ColumnConfig<InputOutputVariables, IOType> ioTypeColumn = new ColumnConfig<InputOutputVariables, IOType>(
				props.ioType(), 100, "I/O");
		// inputTypeColumn.setMenuDisabled(true);
		ioTypeColumn.setCell(new AbstractCell<IOType>() {

			@Override
			public void render(Context context, IOType ioType,
					SafeHtmlBuilder sb) {
				IOTypeTemplates ioTypeTemplates = GWT
						.create(IOTypeTemplates.class);
				sb.append(ioTypeTemplates.format(ioType.getLabel()));
			}
		});

		ArrayList<ColumnConfig<InputOutputVariables, ?>> l = new ArrayList<ColumnConfig<InputOutputVariables, ?>>();
		l.add(nameColumn);
		l.add(descriptionColumn);
		l.add(dataTypeColumn);
		l.add(defaultValueColumn);
		l.add(ioTypeColumn);

		ColumnModel<InputOutputVariables> columns = new ColumnModel<InputOutputVariables>(
				l);

		storeInputOutputVariables = new ListStore<InputOutputVariables>(
				props.id());

		/*
		 * ArrayList<InputOutputVariablesVariables> list = new ArrayList<>();
		 * for (int i = 0; i < 10; i++) { list.add(new
		 * InputOutputVariablesVariables(i, "Test" + i, "Desc", "defaultValue",
		 * InputType.STRING)); }
		 * 
		 * storeEnvironmentVariable.addAll(list);
		 */

		if (project != null && project.getInputData() != null
				&& project.getInputData().getListInputOutputVariables() != null) {
			storeInputOutputVariables.addAll(project.getInputData()
					.getListInputOutputVariables());
		}

		final GridSelectionModel<InputOutputVariables> sm = new GridSelectionModel<InputOutputVariables>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		gridInputOutputVariables = new Grid<InputOutputVariables>(
				storeInputOutputVariables, columns);
		gridInputOutputVariables.setSelectionModel(sm);
		gridInputOutputVariables.getView().setStripeRows(true);
		gridInputOutputVariables.getView().setColumnLines(true);
		gridInputOutputVariables.getView().setAutoExpandColumn(nameColumn);
		gridInputOutputVariables.getView().setAutoFill(true);
		gridInputOutputVariables.setBorders(false);
		gridInputOutputVariables.setColumnReordering(false);

		// DND
		GridDragSource<InputOutputVariables> ds = new GridDragSource<InputOutputVariables>(
				gridInputOutputVariables);
		ds.addDragStartHandler(new DndDragStartEvent.DndDragStartHandler() {

			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<InputOutputVariables> draggingSelection = (ArrayList<InputOutputVariables>) event
						.getData();
				Log.debug("Start Drag: " + draggingSelection);

			}
		});

		GridDropTarget<InputOutputVariables> dt = new GridDropTarget<InputOutputVariables>(
				gridInputOutputVariables);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// EDITING //

		// DataType
		DataTypePropertiesCombo dataTypePropertiesCombo = GWT
				.create(DataTypePropertiesCombo.class);

		storeComboDataType = new ListStore<DataType>(
				dataTypePropertiesCombo.id());

		comboDataType = new ComboBox<DataType>(storeComboDataType,
				dataTypePropertiesCombo.label());
		comboDataType.setClearValueOnParseError(false);
		comboDataType.setEditable(false);

		comboDataType.setTriggerAction(TriggerAction.ALL);
		addHandlersForComboDataType(dataTypePropertiesCombo.label());

		storeComboDataType.clear();
		storeComboDataType.addAll(DataType.asList());
		storeComboDataType.commitChanges();

		// IOType
		IOTypePropertiesCombo ioTypePropertiesCombo = GWT
				.create(IOTypePropertiesCombo.class);

		storeComboIOType = new ListStore<IOType>(ioTypePropertiesCombo.id());

		comboIOType = new ComboBox<IOType>(storeComboIOType,
				ioTypePropertiesCombo.label());
		comboIOType.setClearValueOnParseError(false);
		comboIOType.setEditable(false);

		comboIOType.setTriggerAction(TriggerAction.ALL);
		addHandlersForComboIOType(ioTypePropertiesCombo.label());

		storeComboIOType.clear();
		storeComboIOType.addAll(IOType.asList());
		storeComboIOType.commitChanges();

		//
		TextField nameColumnEditing = new TextField();
		nameColumnEditing.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));
		TextField descriptionColumnEditing = new TextField();
		descriptionColumnEditing.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));
		TextField defaultValueColumnEditing = new TextField();
		defaultValueColumnEditing.addValidator(new RegExValidator("^[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*$",
				"Attention character \" is not allowed"));
		
		gridInputOutputVariablesEditing = new GridRowEditing<InputOutputVariables>(
				gridInputOutputVariables);
		gridInputOutputVariablesEditing
				.addEditor(nameColumn, nameColumnEditing);
		gridInputOutputVariablesEditing.addEditor(descriptionColumn,
				descriptionColumnEditing);
		gridInputOutputVariablesEditing
				.addEditor(dataTypeColumn, comboDataType);
		gridInputOutputVariablesEditing.addEditor(defaultValueColumn,
				defaultValueColumnEditing);
		gridInputOutputVariablesEditing.addEditor(ioTypeColumn, comboIOType);

		btnAdd = new TextButton();
		btnAdd.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		// btnAdd.setIconAlign(IconAlign.);
		btnAdd.setToolTip("Add Input/Output Variable");
		btnAdd.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addInputOutputVariable(event);
			}

		});

		TextButton btnDelete = new TextButton("Delete");
		btnDelete.addSelectHandler(new SelectEvent.SelectHandler() {
			public void onSelect(SelectEvent event) {
				GridCell cell = gridInputOutputVariablesEditing.getActiveCell();
				int rowIndex = cell.getRow();

				gridInputOutputVariablesEditing.cancelEditing();

				storeInputOutputVariables.remove(rowIndex);
				storeInputOutputVariables.commitChanges();

				gridInputOutputVariablesEditing.getCancelButton().setVisible(
						true);
				btnAdd.setEnabled(true);
				if (addStatus) {
					addStatus = false;
				}

				List<InputOutputVariables> listIOVariables = storeInputOutputVariables
						.getAll();
				List<InputOutputVariables> listNewIOVariables = new ArrayList<InputOutputVariables>();
				for (int i = 0; i < listIOVariables.size(); i++) {
					InputOutputVariables var = listIOVariables.get(i);
					var.setId(i);
					listNewIOVariables.add(var);
				}

				storeInputOutputVariables.clear();
				storeInputOutputVariables.addAll(listNewIOVariables);
				storeInputOutputVariables.commitChanges();

				seq = listNewIOVariables.size();
				Log.debug("Current Seq: " + seq);

			}
		});
		ButtonBar buttonBar = gridInputOutputVariablesEditing.getButtonBar();
		buttonBar.add(btnDelete);

		gridInputOutputVariablesEditing
				.addBeforeStartEditHandler(new BeforeStartEditHandler<InputOutputVariables>() {

					@Override
					public void onBeforeStartEdit(
							BeforeStartEditEvent<InputOutputVariables> event) {
						editingBeforeStart(event);

					}
				});

		gridInputOutputVariablesEditing
				.addCancelEditHandler(new CancelEditHandler<InputOutputVariables>() {

					@Override
					public void onCancelEdit(
							CancelEditEvent<InputOutputVariables> event) {
						storeInputOutputVariables.rejectChanges();
						btnAdd.setEnabled(true);

					}

				});

		gridInputOutputVariablesEditing
				.addCompleteEditHandler(new CompleteEditHandler<InputOutputVariables>() {

					@Override
					public void onCompleteEdit(
							CompleteEditEvent<InputOutputVariables> event) {
						try {
							if (addStatus) {
								addStatus = false;
							}
							storeInputOutputVariables.commitChanges();

							gridInputOutputVariablesEditing.getCancelButton()
									.setVisible(true);
							btnAdd.setEnabled(true);

						} catch (Throwable e) {
							Log.error("Error: " + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				});

		// ToolBar toolBar = new ToolBar();
		// toolBar.add(btnAdd, new BoxLayoutData(new Margins(0)));

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setAdjustForScroll(false);
		vlc.setScrollMode(ScrollMode.NONE);

		// vlc.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(gridInputOutputVariables, new VerticalLayoutData(1, 1,
				new Margins(0)));

		add(vlc, new MarginData(new Margins(0)));

	}

	private void editingBeforeStart(
			BeforeStartEditEvent<InputOutputVariables> event) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboDataType(LabelProvider<DataType> idI18N) {
		// TODO Auto-generated method stub

	}

	private void addHandlersForComboIOType(LabelProvider<IOType> idI18N) {
		// TODO Auto-generated method stub

	}

	public void addNewInputOutputVariables(
			InputOutputVariables inputOutputVariable) {
		try {
			Log.debug("Current Seq: " + seq);
			seq++;
			inputOutputVariable.setId(seq);
			Log.debug("New Input/Output Variable: " + inputOutputVariable);

			storeInputOutputVariables.add(inputOutputVariable);
			storeInputOutputVariables.commitChanges();

			if (gridInputOutputVariablesEditing.isEditing()) {
				gridInputOutputVariablesEditing.cancelEditing();
			}
			forceLayout();
		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void addInputOutputVariable(SelectEvent event) {
		try {
			Log.debug("Current Seq: " + seq);
			seq++;
			InputOutputVariables newInputOutputVariablesVariable = new InputOutputVariables(
					seq, "", "", "", DataType.STRING, IOType.INPUT, "");
			Log.debug("New Input/Output Variable: "
					+ newInputOutputVariablesVariable);
			gridInputOutputVariablesEditing.cancelEditing();
			addStatus = true;
			gridInputOutputVariablesEditing.getCancelButton().setVisible(false);
			storeInputOutputVariables.add(newInputOutputVariablesVariable);
			int row = storeInputOutputVariables
					.indexOf(newInputOutputVariablesVariable);

			storeComboDataType.clear();
			storeComboDataType.addAll(DataType.asList());
			storeComboDataType.commitChanges();

			storeComboIOType.clear();
			storeComboIOType.addAll(IOType.asList());
			storeComboIOType.commitChanges();

			gridInputOutputVariablesEditing.startEditing(new GridCell(row, 0));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void update(Project project) {
		Log.debug("Update Input/Output Variables: " + project);
		if (project != null && project.getInputData() != null
				&& project.getInputData().getListInputOutputVariables() != null) {
			storeInputOutputVariables.clear();
			storeInputOutputVariables.addAll(project.getInputData()
					.getListInputOutputVariables());
			storeInputOutputVariables.commitChanges();
			seq = project.getInputData().getListInputOutputVariables().size();

		} else {
			storeInputOutputVariables.clear();
			storeInputOutputVariables.commitChanges();
			seq = 0;
		}

	}

	public ArrayList<InputOutputVariables> getInputOutputVariables() {
		ArrayList<InputOutputVariables> listInputOutputVarialbles = new ArrayList<>(
				gridInputOutputVariables.getStore().getAll());
		return listInputOutputVarialbles;
	}

	public void clearVariables(Project project) {
		storeInputOutputVariables.clear();
		storeInputOutputVariables.commitChanges();
		seq = 0;
		forceLayout();

	}

}
