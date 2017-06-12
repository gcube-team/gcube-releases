package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.ProjectInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectInfoPanel extends ContentPanel {

	private static final int LABAEL_WIDTH = 120;
	@SuppressWarnings("unused")
	private EventBus eventBus;
	private TextField algorithmName;
	private TextField algorithmDescription;
	private TextField algorithmCategory;
	//private ListStore<RequestedVRE> storeRequestedVRE;
	//private Grid<RequestedVRE> gridRequestedVRE;
	//private GridRowEditing<RequestedVRE> gridRequestedVREEditing;
	//private TextButton btnAdd;
	//private boolean addStatus;
	//private int seq = 0;

	public ProjectInfoPanel(Project project, EventBus eventBus) {
		super();
		Log.debug("ProjectInfoPanel");
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

		//
		algorithmName = new TextField();
		algorithmName.setAllowBlank(false);
		algorithmName.addValidator(new RegExValidator("^[a-zA-Z0-9_]*$",
				"Attention only characters a-z,A-Z,0-9 are allowed"));
		algorithmName.setEmptyText("Enter name...");
		algorithmName.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				algorithmName.validate();

			}
		});

		FieldLabel nameLabel = new FieldLabel(algorithmName, "Name");
		nameLabel.setLabelWidth(LABAEL_WIDTH);

		//
		algorithmDescription = new TextField();
		algorithmDescription.setAllowBlank(false);
		algorithmDescription.setEmptyText("Enter description...");
		algorithmDescription.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));
		FieldLabel descriptionLabel = new FieldLabel(algorithmDescription,
				"Description");
		descriptionLabel.setLabelWidth(LABAEL_WIDTH);

		//
		algorithmCategory = new TextField();
		algorithmCategory.setAllowBlank(false);
		algorithmCategory.setEmptyText("Enter description...");
		algorithmCategory.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));
		FieldLabel categoryLabel = new FieldLabel(algorithmCategory, "Category");
		categoryLabel.setLabelWidth(LABAEL_WIDTH);

		// Grid
		/*
		RequestedVREProperties props = GWT.create(RequestedVREProperties.class);

		ColumnConfig<RequestedVRE, String> nameColumn = new ColumnConfig<RequestedVRE, String>(
				props.name(), 100, "Name");
		// nameColumn.setMenuDisabled(true);

		ColumnConfig<RequestedVRE, String> descriptionColumn = new ColumnConfig<RequestedVRE, String>(
				props.description(), 100, "Description");
		// descriptionColumn.setMenuDisabled(true);

		ArrayList<ColumnConfig<RequestedVRE, ?>> l = new ArrayList<ColumnConfig<RequestedVRE, ?>>();
		l.add(nameColumn);
		l.add(descriptionColumn);

		ColumnModel<RequestedVRE> columns = new ColumnModel<RequestedVRE>(l);

		storeRequestedVRE = new ListStore<RequestedVRE>(props.id());*/

		if (project != null && project.getInputData() != null
				&& project.getInputData().getProjectInfo() != null) {

			if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
				algorithmName.setValue(project.getInputData().getProjectInfo()
						.getAlgorithmName());
			}
			if (project.getInputData().getProjectInfo()
					.getAlgorithmDescription() != null) {
				algorithmDescription.setValue(project.getInputData()
						.getProjectInfo().getAlgorithmDescription());
			}

			if (project.getInputData().getProjectInfo()
					.getAlgorithmCategory() != null) {
				algorithmCategory.setValue(project.getInputData()
						.getProjectInfo().getAlgorithmCategory());
			}

			
			/*if (project.getInputData().getProjectInfo().getListRequestedVRE() != null) {
				storeRequestedVRE.addAll(project.getInputData()
						.getProjectInfo().getListRequestedVRE());
				seq = project.getInputData().getProjectInfo()
						.getListRequestedVRE().size();
			}*/

		}
		
		/*
		final GridSelectionModel<RequestedVRE> sm = new GridSelectionModel<RequestedVRE>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		gridRequestedVRE = new Grid<RequestedVRE>(storeRequestedVRE, columns);
		gridRequestedVRE.setSelectionModel(sm);
		gridRequestedVRE.getView().setStripeRows(true);
		gridRequestedVRE.getView().setColumnLines(true);
		gridRequestedVRE.getView().setAutoExpandColumn(nameColumn);
		gridRequestedVRE.getView().setAutoFill(true);
		gridRequestedVRE.setBorders(false);
		gridRequestedVRE.setColumnReordering(false);

		// DND
		GridDragSource<RequestedVRE> ds = new GridDragSource<RequestedVRE>(
				gridRequestedVRE);
		ds.addDragStartHandler(new DndDragStartEvent.DndDragStartHandler() {

			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<RequestedVRE> draggingSelection = (ArrayList<RequestedVRE>) event
						.getData();
				Log.debug("Start Drag: " + draggingSelection);

			}
		});

		GridDropTarget<RequestedVRE> dt = new GridDropTarget<RequestedVRE>(
				gridRequestedVRE);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// EDITING //
		TextField nameColumnEditing = new TextField();
		nameColumnEditing.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));
		TextField descriptionColumnEditing = new TextField();
		descriptionColumnEditing.addValidator(new RegExValidator("^[^\"]*$",
				"Attention character \" is not allowed"));

		gridRequestedVREEditing = new GridRowEditing<RequestedVRE>(
				gridRequestedVRE);
		gridRequestedVREEditing.addEditor(nameColumn, nameColumnEditing);
		gridRequestedVREEditing.addEditor(descriptionColumn,
				descriptionColumnEditing);

		btnAdd = new TextButton("Add");
		btnAdd.setIcon(StatAlgoImporterResources.INSTANCE.add16());
		btnAdd.setScale(ButtonScale.SMALL);
		btnAdd.setIconAlign(IconAlign.LEFT);
		btnAdd.setToolTip("Add VRE");
		btnAdd.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addRequestedVRE(event);
			}

		});

		TextButton btnDelete = new TextButton("Delete");
		btnDelete.addSelectHandler(new SelectEvent.SelectHandler() {
			public void onSelect(SelectEvent event) {
				GridCell cell = gridRequestedVREEditing.getActiveCell();
				int rowIndex = cell.getRow();

				gridRequestedVREEditing.cancelEditing();

				storeRequestedVRE.remove(rowIndex);
				storeRequestedVRE.commitChanges();

				gridRequestedVREEditing.getCancelButton().setVisible(true);
				btnAdd.setEnabled(true);
				if (addStatus) {
					addStatus = false;
				}

				List<RequestedVRE> listSelected = storeRequestedVRE.getAll();
				List<RequestedVRE> listNewSelected = new ArrayList<RequestedVRE>();
				for (int i = 0; i < listSelected.size(); i++) {
					RequestedVRE var = listSelected.get(i);
					var.setId(i);
					listNewSelected.add(var);
				}

				storeRequestedVRE.clear();
				storeRequestedVRE.addAll(listNewSelected);
				storeRequestedVRE.commitChanges();

				seq = listNewSelected.size();
				Log.debug("Current Seq: " + seq);

			}
		});
		ButtonBar buttonBar = gridRequestedVREEditing.getButtonBar();
		buttonBar.add(btnDelete);

		gridRequestedVREEditing
				.addBeforeStartEditHandler(new BeforeStartEditHandler<RequestedVRE>() {

					@Override
					public void onBeforeStartEdit(
							BeforeStartEditEvent<RequestedVRE> event) {
						editingBeforeStart(event);

					}
				});

		gridRequestedVREEditing
				.addCancelEditHandler(new CancelEditHandler<RequestedVRE>() {

					@Override
					public void onCancelEdit(CancelEditEvent<RequestedVRE> event) {
						storeRequestedVRE.rejectChanges();
						btnAdd.setEnabled(true);

					}

				});

		gridRequestedVREEditing
				.addCompleteEditHandler(new CompleteEditHandler<RequestedVRE>() {

					@Override
					public void onCompleteEdit(
							CompleteEditEvent<RequestedVRE> event) {
						try {
							if (addStatus) {
								addStatus = false;
							}
							storeRequestedVRE.commitChanges();

							gridRequestedVREEditing.getCancelButton()
									.setVisible(true);
							btnAdd.setEnabled(true);

						} catch (Throwable e) {
							Log.error("Error: " + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				});
		
		*/
		// /
		

		//ToolBar toolBar = new ToolBar();
		//toolBar.add(btnAdd, new BoxLayoutData(new Margins(0)));

		//FieldLabel requestedVRELabel = new FieldLabel(toolBar, "Requested VREs");
		//requestedVRELabel.setLabelWidth(LABAEL_WIDTH);
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setAdjustForScroll(false);
		vlc.setScrollMode(ScrollMode.NONE);

		vlc.add(nameLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(descriptionLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(categoryLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		//vlc.add(requestedVRELabel,
		//		new VerticalLayoutData(1, -1, new Margins(0)));
		//vlc.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		//vlc.add(gridRequestedVRE, new VerticalLayoutData(1, 1, new Margins(0)));

		add(vlc, new MarginData(new Margins(0)));
	}

	public void update(Project project) {
		Log.debug("Update Project Info: " + project);
		if (project != null && project.getInputData() != null
				&& project.getInputData().getProjectInfo() != null) {

			if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
				algorithmName.setValue(project.getInputData().getProjectInfo()
						.getAlgorithmName());
			} else {
				algorithmName.clear();
			}
			if (project.getInputData().getProjectInfo()
					.getAlgorithmDescription() != null) {
				algorithmDescription.setValue(project.getInputData()
						.getProjectInfo().getAlgorithmDescription());
			} else {
				algorithmDescription.clear();
			}
			if (project.getInputData().getProjectInfo().getAlgorithmCategory() != null) {
				algorithmCategory.setValue(project.getInputData()
						.getProjectInfo().getAlgorithmCategory());
			} else {
				algorithmCategory.clear();
			}

			/*
			if (project.getInputData().getProjectInfo().getListRequestedVRE() != null) {
				storeRequestedVRE.clear();
				storeRequestedVRE.addAll(project.getInputData()
						.getProjectInfo().getListRequestedVRE());
				storeRequestedVRE.commitChanges();
				seq = project.getInputData().getProjectInfo()
						.getListRequestedVRE().size();

			} else {
				storeRequestedVRE.clear();
				storeRequestedVRE.commitChanges();
				seq = 0;
			}*/

		} else {
			algorithmName.clear();
			algorithmDescription.clear();
			algorithmCategory.clear();
			//storeRequestedVRE.clear();
			//storeRequestedVRE.commitChanges();
			//seq = 0;
		}

	}

	/*

	private void editingBeforeStart(BeforeStartEditEvent<RequestedVRE> event) {
		// TODO Auto-generated method stub

	}

	private void addRequestedVRE(SelectEvent event) {
		try {
			seq++;
			RequestedVRE newRequestedVRE = new RequestedVRE(seq, "", "");
			Log.debug("New RequestedVRE: " + newRequestedVRE);
			gridRequestedVREEditing.cancelEditing();
			addStatus = true;
			gridRequestedVREEditing.getCancelButton().setVisible(false);
			storeRequestedVRE.add(newRequestedVRE);
			int row = storeRequestedVRE.indexOf(newRequestedVRE);

			gridRequestedVREEditing.startEditing(new GridCell(row, 0));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}*/

	public ProjectInfo getProjectInfo() {
		String name = algorithmName.getCurrentValue();
		String description = algorithmDescription.getCurrentValue();
		String category = algorithmCategory.getCurrentValue();
		//ArrayList<RequestedVRE> listRequestedVRE = new ArrayList<>(
		//		gridRequestedVRE.getStore().getAll());
		return new ProjectInfo(name, description, category);

	}
}
