package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.RuleColumnPlaceHolderDescriptorProperties;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleTableType;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
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
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RuleOnTableNewDefinitionCard extends WizardCard {

	
	public interface DataTypePropertiesCombo extends
			PropertyAccess<ColumnDataType> {

		@Path("id")
		ModelKeyProvider<ColumnDataType> id();

		LabelProvider<ColumnDataType> label();

	}

	private static int seq;
	private static RuleOnTableNewMessages msgs = GWT
			.create(RuleOnTableNewMessages.class);
	private CommonMessages msgsCommon;
	
	private RuleOnTableNewDefinitionCard thisCard;
	private TDRuleTableType tdRuleTableType;
	private Grid<RuleColumnPlaceHolderDescriptor> grid;
	private ListStore<RuleColumnPlaceHolderDescriptor> store;
	private boolean addStatus;
	

	public RuleOnTableNewDefinitionCard() {
		super(msgs.ruleOnTableNewDefinitionCardHead(),
				msgs.ruleOnTableNewDefinitionCardFoot());
		this.thisCard = this;
		initMessages();
		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		panel.add(v);

		// Grid
		RuleColumnPlaceHolderDescriptorProperties props = GWT
				.create(RuleColumnPlaceHolderDescriptorProperties.class);

		ColumnConfig<RuleColumnPlaceHolderDescriptor, String> labelCol = new ColumnConfig<RuleColumnPlaceHolderDescriptor, String>(
				props.label(), 220, msgs.labelCol());
		ColumnConfig<RuleColumnPlaceHolderDescriptor, ColumnDataType> columnDataTypeCol = new ColumnConfig<RuleColumnPlaceHolderDescriptor, ColumnDataType>(
				props.columnDataType(), 130, msgs.columnDataTypeCol());

		ArrayList<ColumnConfig<RuleColumnPlaceHolderDescriptor, ?>> l = new ArrayList<ColumnConfig<RuleColumnPlaceHolderDescriptor, ?>>();
		l.add(labelCol);
		l.add(columnDataTypeCol);

		ColumnModel<RuleColumnPlaceHolderDescriptor> columns = new ColumnModel<RuleColumnPlaceHolderDescriptor>(
				l);

		store = new ListStore<RuleColumnPlaceHolderDescriptor>(props.id());

		final GridSelectionModel<RuleColumnPlaceHolderDescriptor> sm = new GridSelectionModel<RuleColumnPlaceHolderDescriptor>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		grid = new Grid<RuleColumnPlaceHolderDescriptor>(store, columns);
		grid.setSelectionModel(sm);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);
		grid.getView().setAutoExpandColumn(labelCol);
		grid.getView().setSortingEnabled(false);

		GridDragSource<RuleColumnPlaceHolderDescriptor> ds = new GridDragSource<RuleColumnPlaceHolderDescriptor>(
				grid);
		ds.addDragStartHandler(new DndDragStartHandler() {

			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<RuleColumnPlaceHolderDescriptor> draggingSelection = (ArrayList<RuleColumnPlaceHolderDescriptor>) event
						.getData();
				Log.debug("Start Drag: " + draggingSelection);

			}
		});
		GridDropTarget<RuleColumnPlaceHolderDescriptor> dt = new GridDropTarget<RuleColumnPlaceHolderDescriptor>(
				grid);
		dt.setFeedback(Feedback.BOTH);
		dt.setAllowSelfAsSource(true);

		// EDITING //
		DataTypePropertiesCombo dataTypePropertiesCombo = GWT
				.create(DataTypePropertiesCombo.class);

		ListStore<ColumnDataType> storeComboColumnDataType = new ListStore<ColumnDataType>(
				dataTypePropertiesCombo.id());
		storeComboColumnDataType.addAll(ColumnDataType.asList());

		ComboBox<ColumnDataType> comboColumnDataType = new ComboBox<ColumnDataType>(
				storeComboColumnDataType, dataTypePropertiesCombo.label());
		comboColumnDataType.setClearValueOnParseError(false);

		comboColumnDataType.setTriggerAction(TriggerAction.ALL);

		final TextField labelField = new TextField();
		labelField.addValidator(new EmptyValidator<String>());

		final GridRowEditing<RuleColumnPlaceHolderDescriptor> editing = new GridRowEditing<RuleColumnPlaceHolderDescriptor>(
				grid);
		
		TextButton btnSave=editing.getSaveButton();
		btnSave.setText(msgsCommon.btnSaveText());
		btnSave.setToolTip(msgsCommon.btnSaveToolTip());
		
		TextButton btnCancel=editing.getCancelButton();
		btnCancel.setText(msgsCommon.btnCancelText());
		btnCancel.setToolTip(msgsCommon.btnCancelToolTip());
	
		editing.addEditor(labelCol, labelField);
		editing.addEditor(columnDataTypeCol, comboColumnDataType);

		final TextButton btnAddColumn = new TextButton(msgs.btnAddColumnText());
		btnAddColumn.setIcon(ResourceBundle.INSTANCE.columnAdd24());
		btnAddColumn.setIconAlign(IconAlign.TOP);
		btnAddColumn.setToolTip(msgs.btnAddColumnToolTip());
		btnAddColumn.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				store.size();
				RuleColumnPlaceHolderDescriptor descriptor = new RuleColumnPlaceHolderDescriptor(
						String.valueOf(seq++), "", ColumnDataType.Text);

				editing.cancelEditing();
				addStatus = true;
				editing.getCancelButton().setVisible(false);
				store.add(descriptor);

				int row = store.indexOf(descriptor);
				editing.startEditing(new GridCell(row, 0));

			}
		});

		TextButton btnDelete = new TextButton(msgs.btnDeleteText());
		btnDelete.addSelectHandler(new SelectEvent.SelectHandler() {
			public void onSelect(SelectEvent event) {
				GridCell cell = editing.getActiveCell();
				int rowIndex = cell.getRow();

				editing.cancelEditing();

				store.remove(rowIndex);
				store.commitChanges();

				editing.getCancelButton().setVisible(true);
				btnAddColumn.setEnabled(true);
				setEnableNextButton(true);
				if (addStatus) {
					addStatus = false;
				}
			}
		});
		ButtonBar buttonBar = editing.getButtonBar();
		buttonBar.add(btnDelete);

		editing.addBeforeStartEditHandler(new BeforeStartEditHandler<RuleColumnPlaceHolderDescriptor>() {

			@Override
			public void onBeforeStartEdit(
					BeforeStartEditEvent<RuleColumnPlaceHolderDescriptor> event) {
				btnAddColumn.setEnabled(false);
				setEnableNextButton(false);
			}
		});

		editing.addCancelEditHandler(new CancelEditHandler<RuleColumnPlaceHolderDescriptor>() {

			@Override
			public void onCancelEdit(
					CancelEditEvent<RuleColumnPlaceHolderDescriptor> event) {
				store.rejectChanges();
				btnAddColumn.setEnabled(true);
				setEnableNextButton(true);

			}

		});

		editing.addCompleteEditHandler(new CompleteEditHandler<RuleColumnPlaceHolderDescriptor>() {

			@Override
			public void onCompleteEdit(
					CompleteEditEvent<RuleColumnPlaceHolderDescriptor> event) {
				try {

					String label = labelField.getCurrentValue();
					Log.debug("Current Label: " + label);

					boolean exist = false;
					GridCell cell = event.getEditCell();
					int rowIndex = cell.getRow();
					RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor = store
							.get(rowIndex);

					Log.debug("Store: " + store.getAll().toString());
					for (RuleColumnPlaceHolderDescriptor descriptor : store
							.getAll()) {
						if (ruleColumnPlaceHolderDescriptor.getId().compareTo(
								descriptor.getId()) != 0
								&& label.compareToIgnoreCase(descriptor
										.getLabel()) == 0) {
							exist = true;
							break;
						}

					}

					Log.debug("Exist: " + exist);
					if (exist) {
						Log.debug("AddStatus: " + addStatus);
						if (addStatus) {
							addStatus = false;
							store.remove(rowIndex);
							store.commitChanges();
						} else {
							store.rejectChanges();
						}
						UtilsGXT3
								.alert(msgsCommon.attention(),
										msgs.labelAlreadyPresent());
					} else {
						store.commitChanges();
					}

					editing.getCancelButton().setVisible(true);
					btnAddColumn.setEnabled(true);
					setEnableNextButton(true);

				} catch (Throwable e) {
					Log.error("Error in RuleOnTableNewDefinitionCard: "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		});

		//
		ToolBar toolBar = new ToolBar();
		toolBar.add(btnAddColumn);

		v.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(grid, new VerticalLayoutData(1, 1, new Margins(0)));

		return panel;
	}

	@Override
	public void setup() {
		Log.debug("RuleOnTableNewDefinitionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("RuleOnTableNewDefinitionCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove RuleOnTableNewDefinitionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		if (store == null || store.size() <= 0) {
			AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
					msgs.addAtLeastOneColumn());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		}

		tdRuleTableType = new TDRuleTableType(
				new ArrayList<RuleColumnPlaceHolderDescriptor>(store.getAll()));
		
		goNext();
	}

	protected void goNext(){
		try {
			RuleOnTableNewExpressionCard createRuleOnTableExpressionCard = new RuleOnTableNewExpressionCard(
					tdRuleTableType);
			getWizardWindow().addCard(createRuleOnTableExpressionCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	

	@Override
	public void dispose() {

	}

}
