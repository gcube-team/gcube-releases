package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.InterpreterPackageInfoProperties;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
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
import com.sencha.gxt.widget.core.client.form.FieldLabel;
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
public class InterpreterInfoPanel extends ContentPanel {

	@SuppressWarnings("unused")
	private EventBus eventBus;
	private ListStore<InterpreterPackageInfo> storeInterpreterPackageInfo;
	private Grid<InterpreterPackageInfo> gridInterpreterPackageInfo;
	private GridRowEditing<InterpreterPackageInfo> gridInterpreterPackageInfoEditing;
	private TextButton btnAdd;
	private boolean addStatus;
	private int seqInterpreterPackages = 0;
	private TextField interpreterVersion;

	public InterpreterInfoPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("InterpreterPanel");
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
		InterpreterPackageInfoProperties props = GWT.create(InterpreterPackageInfoProperties.class);

		ColumnConfig<InterpreterPackageInfo, String> nameColumn = new ColumnConfig<InterpreterPackageInfo, String>(
				props.name(), 100, "Name");
		// nameColumn.setMenuDisabled(true);

		ColumnConfig<InterpreterPackageInfo, String> detailsColumn = new ColumnConfig<InterpreterPackageInfo, String>(
				props.details(), 100, "Details");
		// descriptionColumn.setMenuDisabled(true);

		ArrayList<ColumnConfig<InterpreterPackageInfo, ?>> l = new ArrayList<ColumnConfig<InterpreterPackageInfo, ?>>();
		l.add(nameColumn);
		l.add(detailsColumn);

		ColumnModel<InterpreterPackageInfo> columns = new ColumnModel<InterpreterPackageInfo>(l);

		storeInterpreterPackageInfo = new ListStore<InterpreterPackageInfo>(props.id());

		/*
		 * ArrayList<InterpreterPackageInfo> list = new ArrayList<>(); for (int
		 * i = 0; i < 10; i++) { list.add(new InterpreterPackageInfo(i, "Test" +
		 * i, "Desc", "defaultValue", InputType.STRING)); }
		 * 
		 * storeEnvironmentVariable.addAll(list);
		 */

		if (project != null && project.getInputData() != null && project.getInputData().getInterpreterInfo() != null
				&& project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo() != null) {
			storeInterpreterPackageInfo
					.addAll(project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo());
			seqInterpreterPackages = project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo().size();
		} else {
			seqInterpreterPackages = 0;
		}

		final GridSelectionModel<InterpreterPackageInfo> sm = new GridSelectionModel<InterpreterPackageInfo>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		gridInterpreterPackageInfo = new Grid<InterpreterPackageInfo>(storeInterpreterPackageInfo, columns);
		gridInterpreterPackageInfo.setSelectionModel(sm);
		gridInterpreterPackageInfo.getView().setStripeRows(true);
		gridInterpreterPackageInfo.getView().setColumnLines(true);
		gridInterpreterPackageInfo.getView().setAutoExpandColumn(nameColumn);
		gridInterpreterPackageInfo.getView().setAutoFill(true);
		gridInterpreterPackageInfo.setBorders(false);
		gridInterpreterPackageInfo.setColumnReordering(false);

		// DND
		GridDragSource<InterpreterPackageInfo> ds = new GridDragSource<InterpreterPackageInfo>(
				gridInterpreterPackageInfo);
		ds.addDragStartHandler(new DndDragStartEvent.DndDragStartHandler() {

			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<InterpreterPackageInfo> draggingSelection = (ArrayList<InterpreterPackageInfo>) event
						.getData();
				Log.debug("Start Drag: " + draggingSelection);

			}
		});

		GridDropTarget<InterpreterPackageInfo> dt = new GridDropTarget<InterpreterPackageInfo>(
				gridInterpreterPackageInfo);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// EDITING //
		TextField nameColumnEditing = new TextField();
		nameColumnEditing.setAllowBlank(false);
		nameColumnEditing.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));
		TextField versionColumnEditing = new TextField();
		versionColumnEditing.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));

		gridInterpreterPackageInfoEditing = new GridRowEditing<InterpreterPackageInfo>(gridInterpreterPackageInfo);
		gridInterpreterPackageInfoEditing.addEditor(nameColumn, nameColumnEditing);
		gridInterpreterPackageInfoEditing.addEditor(detailsColumn, versionColumnEditing);

		btnAdd = new TextButton("Add");
		btnAdd.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		// btnAdd.setIconAlign(IconAlign.);
		btnAdd.setToolTip("Add Package Info");
		btnAdd.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addInterpreterPackageInfo(event);
			}

		});

		TextButton btnDelete = new TextButton("Delete");
		btnDelete.addSelectHandler(new SelectEvent.SelectHandler() {
			public void onSelect(SelectEvent event) {
				GridCell cell = gridInterpreterPackageInfoEditing.getActiveCell();
				int rowIndex = cell.getRow();

				gridInterpreterPackageInfoEditing.cancelEditing();

				storeInterpreterPackageInfo.remove(rowIndex);
				storeInterpreterPackageInfo.commitChanges();

				gridInterpreterPackageInfoEditing.getCancelButton().setVisible(true);
				btnAdd.setEnabled(true);
				if (addStatus) {
					addStatus = false;
				}

				List<InterpreterPackageInfo> listPackages = storeInterpreterPackageInfo.getAll();
				List<InterpreterPackageInfo> listNewPackages = new ArrayList<InterpreterPackageInfo>();
				for (int i = 0; i < listPackages.size(); i++) {
					InterpreterPackageInfo var = listPackages.get(i);
					var.setId(i);
					listNewPackages.add(var);
				}

				storeInterpreterPackageInfo.clear();
				storeInterpreterPackageInfo.addAll(listNewPackages);
				storeInterpreterPackageInfo.commitChanges();

				seqInterpreterPackages = listNewPackages.size();
				Log.debug("Current Seq: " + seqInterpreterPackages);

			}
		});
		ButtonBar buttonBar = gridInterpreterPackageInfoEditing.getButtonBar();
		buttonBar.add(btnDelete);

		gridInterpreterPackageInfoEditing
				.addBeforeStartEditHandler(new BeforeStartEditHandler<InterpreterPackageInfo>() {

					@Override
					public void onBeforeStartEdit(BeforeStartEditEvent<InterpreterPackageInfo> event) {
						editingBeforeStart(event);

					}
				});

		gridInterpreterPackageInfoEditing.addCancelEditHandler(new CancelEditHandler<InterpreterPackageInfo>() {

			@Override
			public void onCancelEdit(CancelEditEvent<InterpreterPackageInfo> event) {
				storeInterpreterPackageInfo.rejectChanges();
				btnAdd.setEnabled(true);

			}

		});

		gridInterpreterPackageInfoEditing.addCompleteEditHandler(new CompleteEditHandler<InterpreterPackageInfo>() {

			@Override
			public void onCompleteEdit(CompleteEditEvent<InterpreterPackageInfo> event) {
				try {
					if (addStatus) {
						addStatus = false;
					}
					storeInterpreterPackageInfo.commitChanges();

					gridInterpreterPackageInfoEditing.getCancelButton().setVisible(true);
					btnAdd.setEnabled(true);

				} catch (Throwable e) {
					Log.error("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		});

		interpreterVersion = new TextField();
		interpreterVersion.setAllowBlank(false);
		interpreterVersion.setEmptyText("Enter version...");
		interpreterVersion.addValidator(new RegExValidator("^[^\"]*$", "Attention character \" is not allowed"));
		FieldLabel interpreterVersionLabel = new FieldLabel(interpreterVersion, "Version");

		if (project != null && project.getInputData() != null && project.getInputData().getInterpreterInfo() != null
				&& project.getInputData().getInterpreterInfo().getVersion() != null) {
			interpreterVersion.setValue(project.getInputData().getInterpreterInfo().getVersion());

		}

		ToolBar toolBar = new ToolBar();
		toolBar.add(btnAdd, new BoxLayoutData(new Margins(0)));

		FieldLabel interpreterPackagesLabel = new FieldLabel(toolBar, "Packages");

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setAdjustForScroll(false);
		vlc.setScrollMode(ScrollMode.NONE);

		vlc.add(interpreterVersionLabel, new VerticalLayoutData(1, -1, new Margins(5, 0, 5, 0)));

		vlc.add(interpreterPackagesLabel, new VerticalLayoutData(1, -1, new Margins(0)));

		// vlc.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(gridInterpreterPackageInfo, new VerticalLayoutData(1, 1, new Margins(0)));

		add(vlc, new MarginData(new Margins(0)));

	}

	private void editingBeforeStart(BeforeStartEditEvent<InterpreterPackageInfo> event) {
		// TODO Auto-generated method stub

	}

	public void addNewInterpreterPackageInfo(InterpreterPackageInfo interpreterPackageInfo) {
		try {
			Log.debug("Current Seq: " + seqInterpreterPackages);
			seqInterpreterPackages++;
			interpreterPackageInfo.setId(seqInterpreterPackages);
			Log.debug("New Interpreter Package Info: " + interpreterPackageInfo);
			storeInterpreterPackageInfo.add(interpreterPackageInfo);
			storeInterpreterPackageInfo.commitChanges();

			if (gridInterpreterPackageInfoEditing.isEditing()) {
				gridInterpreterPackageInfoEditing.cancelEditing();
			}
			forceLayout();
		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void addInterpreterPackageInfo(SelectEvent event) {
		try {
			Log.debug("Current Seq: " + seqInterpreterPackages);
			seqInterpreterPackages++;
			InterpreterPackageInfo newInterpreterPackageInfo = new InterpreterPackageInfo(seqInterpreterPackages, "",
					"", "");
			Log.debug("New Interpreter Package Info: " + newInterpreterPackageInfo);
			gridInterpreterPackageInfoEditing.cancelEditing();
			addStatus = true;
			gridInterpreterPackageInfoEditing.getCancelButton().setVisible(false);
			storeInterpreterPackageInfo.add(newInterpreterPackageInfo);
			int row = storeInterpreterPackageInfo.indexOf(newInterpreterPackageInfo);

			gridInterpreterPackageInfoEditing.startEditing(new GridCell(row, 0));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void update(Project project) {
		Log.debug("Update Interpreter Package Info: " + project);
		if (project != null && project.getInputData() != null && project.getInputData().getInterpreterInfo() != null) {

			if (project.getInputData().getInterpreterInfo().getVersion() != null) {
				interpreterVersion.setValue(project.getInputData().getInterpreterInfo().getVersion());

			} else {
				interpreterVersion.clear();
			}

			if (project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo() != null) {

				storeInterpreterPackageInfo.clear();
				storeInterpreterPackageInfo
						.addAll(project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo());
				storeInterpreterPackageInfo.commitChanges();
				seqInterpreterPackages = project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo()
						.size();
			} else {
				storeInterpreterPackageInfo.clear();
				storeInterpreterPackageInfo.commitChanges();
				seqInterpreterPackages = 0;
			}
		} else {
			interpreterVersion.clear();
			storeInterpreterPackageInfo.clear();
			storeInterpreterPackageInfo.commitChanges();
			seqInterpreterPackages = 0;
		}

	}

	public InterpreterInfo getInterpreterInfo() {

		ArrayList<InterpreterPackageInfo> interpreterPackagesInfo = new ArrayList<>(
				gridInterpreterPackageInfo.getStore().getAll());

		String version = interpreterVersion.getCurrentValue();
		return new InterpreterInfo(version, interpreterPackagesInfo);

	}
}
