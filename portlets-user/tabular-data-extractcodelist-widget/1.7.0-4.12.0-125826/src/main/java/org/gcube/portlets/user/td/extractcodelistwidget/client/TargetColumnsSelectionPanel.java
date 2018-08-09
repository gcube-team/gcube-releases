package org.gcube.portlets.user.td.extractcodelistwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistTargetColumn;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.columnwidget.client.create.CreateDefColumnDialog;
import org.gcube.portlets.user.td.columnwidget.client.create.CreateDefColumnListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.properties.ExtractCodelistDefColumnPropertiesCombo;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TargetColumnsSelectionPanel extends FramedPanel implements
		CodelistSelectionListener {

	private static final String COMBO_COLUMN = "comboColumn";
	private static final String COMBO_DEF_COLUMN = "comboDefColumn";
	private static final String CHECK_NEW = "checkNew";
	private static final String WIDTH = "526px";
	private static final String HEIGHT = "378px";
	private static final String FIELDWIDTH = "526px";
	private static final String FIELDSHEIGHT = "336px";
	private static final int LABELSIZE = 120;
	private static final int LABEL_SIZE_IN_CHAR = 17;
	private static final int COMBOWIDTH = 270;

	private CommonMessages msgsCommon;
	private ExtractCodelistMessages msgs;

	private TargetColumnsSelectionCard parent;
	private TabResource connection;
	private ArrayList<ColumnData> connectionColumns;

	private ArrayList<ExtractCodelistTargetColumn> targetColumns;

	private ToolBar toolBarHead;

	private TextButton btnConnect;
	private TextButton btnDisconnect;
	private TextField connectionField;

	private SimpleContainer form;
	private VerticalLayoutContainer formLayout;

	public TargetColumnsSelectionPanel(TargetColumnsSelectionCard parent) {

		this.parent = parent;
		connection = null;
		Log.debug("Create TargetColumnsSelectionPanel()");
		initMessages();
		init();
		create();

	}

	protected void initMessages() {
		msgs = GWT.create(ExtractCodelistMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		toolBarHead = new ToolBar();

		// Connect Codelist
		btnConnect = new TextButton();
		btnConnect.setIcon(ResourceBundle.INSTANCE.codelistLink24());
		btnConnect.setIconAlign(IconAlign.TOP);
		btnConnect.setTitle(msgs.btnConnectTitle());
		btnConnect.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Connect");
				btnConnect.disable();
				connectCodelist();

			}

		});

		toolBarHead.add(btnConnect);

		// Disconnect Codelist
		btnDisconnect = new TextButton();
		btnDisconnect.setIcon(ResourceBundle.INSTANCE.codelistLinkBreak24());
		btnDisconnect.setIconAlign(IconAlign.TOP);
		btnDisconnect.setTitle(msgs.btnDisconnectTitle());
		btnDisconnect.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Disconnect");
				btnDisconnect.disable();
				disconnectCodelist();

			}

		});
		toolBarHead.add(btnDisconnect);

		connectionField = new TextField();
		toolBarHead.add(connectionField);

		form = new SimpleContainer();
		form.setWidth(FIELDWIDTH);
		form.setHeight(FIELDSHEIGHT);

		formLayout = new VerticalLayoutContainer();
		// formLayout.setScrollMode(ScrollMode.AUTO);
		formLayout.setAdjustForScroll(true);

		ArrayList<FieldLabel> fields = generateFields();
		for (FieldLabel fl : fields) {
			formLayout.add(fl, new VerticalLayoutData(1, -1, new Margins(1)));

		}

		form.add(formLayout);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		// v.setHeight(HEIGHT);
		// v.setWidth(WIDTH);
		v.add(toolBarHead, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(form, new VerticalLayoutData(1, -1, new Margins(0)));

		add(v);

		connectionField.setValue("");
		connectionField.setVisible(false);
		btnDisconnect.setVisible(false);
		btnConnect.setVisible(true);
		btnConnect.enable();

	}

	protected ArrayList<FieldLabel> generateFields() {
		ArrayList<FieldLabel> fields = new ArrayList<FieldLabel>();
		int i = 0;
		for (ColumnData sourceCol : parent.getExtractCodelistSession()
				.getSourceColumns()) {
			String label = new String();
			if (sourceCol != null && sourceCol.getLabel() != null) {
				label = SafeHtmlUtils.htmlEscape(sourceCol.getLabel());
				if (label.length() > LABEL_SIZE_IN_CHAR + 2) {
					label = label.substring(0, LABEL_SIZE_IN_CHAR);
					label += "...";
				}
			}

			if (connection == null) {
				FieldLabel fieldLabel = retrieveNewColumnLabel(i, sourceCol,
						label);
				fieldLabel.setLabelWidth(LABELSIZE);
				fieldLabel.setId(sourceCol.getColumnId());
				fields.add(fieldLabel);
			} else {
				FieldLabel fieldLabel = retrieveReferenceColumnLabel(i,
						sourceCol, label);
				fieldLabel.setLabelWidth(LABELSIZE);
				fieldLabel.setId(sourceCol.getColumnId());
				fields.add(fieldLabel);
			}
			i++;
		}
		return fields;
	}

	protected FieldLabel retrieveNewColumnLabel(final int index,
			final ColumnData col, String label) {

		// comboDefColumn
		ExtractCodelistDefColumnPropertiesCombo props = GWT
				.create(ExtractCodelistDefColumnPropertiesCombo.class);
		ListStore<ColumnMockUp> storeComboDefNewColumn = new ListStore<ColumnMockUp>(
				props.id());

		final ComboBox<ColumnMockUp> comboDefNewColumn = new ComboBox<ColumnMockUp>(
				storeComboDefNewColumn, props.label());
		Log.debug("ComboDefColumn created");

		final CreateDefColumnListener createDefNewColumnListener = new CreateDefColumnListener() {

			@Override
			public void failedDefColumnCreation(String reason, String details) {
				Log.error("Change Value Failed:" + reason + " " + details);

			}

			@Override
			public void completedDefColumnCreation(ColumnMockUp columnMockUp) {
				comboDefNewColumn.setValue(columnMockUp, true);

			}

			@Override
			public void abortedDefColumnCreation() {
				Log.debug("Change Value Aborted");

			}
		};

		comboDefNewColumn.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboDefColumn TriggerClickEvent");
				comboDefNewColumn.collapse();

				CreateDefColumnDialog createDefNewColumnDialog = new CreateDefColumnDialog(
						TableType.CODELIST, parent.getEventBus());
				createDefNewColumnDialog
						.addListener(createDefNewColumnListener);
				createDefNewColumnDialog.show();

			}

		});

		comboDefNewColumn.setEmptyText(msgs.comboDefNewColumnEmptyText());
		comboDefNewColumn.setWidth(COMBOWIDTH);
		comboDefNewColumn.setEditable(false);
		comboDefNewColumn.setTriggerAction(TriggerAction.ALL);

		FieldLabel comboDefNewColumnLabel = new FieldLabel(comboDefNewColumn,
				label);
		comboDefNewColumnLabel.setId(col.getColumnId());
		return comboDefNewColumnLabel;
	}

	protected FieldLabel retrieveReferenceColumnLabel(int index,
			final ColumnData col, String label) {
		// comboReferenceDefColumn
		ExtractCodelistDefColumnPropertiesCombo props = GWT
				.create(ExtractCodelistDefColumnPropertiesCombo.class);
		ListStore<ColumnMockUp> storeComboReferenceDefColumn = new ListStore<ColumnMockUp>(
				props.id());

		final ComboBox<ColumnMockUp> comboReferenceDefColumn = new ComboBox<ColumnMockUp>(
				storeComboReferenceDefColumn, props.label());
		comboReferenceDefColumn.setItemId(COMBO_DEF_COLUMN);

		Log.debug("ComboDefColumn created");

		final CreateDefColumnListener createReferenceDefColumnListener = new CreateDefColumnListener() {

			@Override
			public void failedDefColumnCreation(String reason, String details) {
				Log.error("Change Value Failed:" + reason + " " + details);

			}

			@Override
			public void completedDefColumnCreation(ColumnMockUp columnMockUp) {
				comboReferenceDefColumn.setValue(columnMockUp, true);

			}

			@Override
			public void abortedDefColumnCreation() {
				Log.debug("Change Value Aborted");

			}
		};

		comboReferenceDefColumn
				.addTriggerClickHandler(new TriggerClickHandler() {

					@Override
					public void onTriggerClick(TriggerClickEvent event) {
						Log.debug("ComboDefColumn TriggerClickEvent");
						comboReferenceDefColumn.collapse();

						CreateDefColumnDialog createReferenceDefColumnDialog = new CreateDefColumnDialog(
								TableType.CODELIST, parent.getEventBus());
						createReferenceDefColumnDialog
								.addListener(createReferenceDefColumnListener);
						createReferenceDefColumnDialog.show();

					}

				});

		comboReferenceDefColumn.setEmptyText(msgs
				.comboReferenceDefColumnEmptyText());
		comboReferenceDefColumn.setWidth(COMBOWIDTH);
		comboReferenceDefColumn.setEditable(false);
		comboReferenceDefColumn.setTriggerAction(TriggerAction.ALL);

		// comboColumn
		ColumnDataPropertiesCombo propsCod = GWT
				.create(ColumnDataPropertiesCombo.class);
		ListStore<ColumnData> storeComboColumn = new ListStore<ColumnData>(
				propsCod.id());
		storeComboColumn.addAll(connectionColumns);

		final ComboBox<ColumnData> comboColumn = new ComboBox<ColumnData>(
				storeComboColumn, propsCod.label());
		comboColumn.setItemId(COMBO_COLUMN);

		Log.debug("ComboColumn created");

		comboColumn.setEmptyText(msgs.comboColumnEmptyText());
		comboColumn.setWidth(COMBOWIDTH);
		comboColumn.setEditable(false);
		comboColumn.setTriggerAction(TriggerAction.ALL);

		CheckBox checkNew = new CheckBox();
		checkNew.setItemId(CHECK_NEW);
		checkNew.setEnabled(true);
		checkNew.setBoxLabel(msgs.checkNewLabel());
		checkNew.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					comboColumn.setVisible(false);
					comboReferenceDefColumn.setVisible(true);
					forceLayout();
				} else {
					comboColumn.setVisible(true);
					comboReferenceDefColumn.setVisible(false);
					forceLayout();
				}

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.START);

		BoxLayoutData boxLayoutData = new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(checkNew, boxLayoutData);
		flowButton.add(comboColumn, boxLayoutData);
		flowButton.add(comboReferenceDefColumn, boxLayoutData);

		FieldLabel columnLabel = new FieldLabel(flowButton, label);
		columnLabel.setId(col.getColumnId());
		comboReferenceDefColumn.setVisible(false);

		return columnLabel;
	}

	protected void disconnectCodelist() {
		Log.debug("Disconnect codelist");
		this.connection = null;
		connectionField.setValue("");
		connectionField.setVisible(false);
		btnDisconnect.setVisible(false);
		btnConnect.setVisible(true);
		btnConnect.enable();
		toolBarHead.forceLayout();
		updatedForm();

	}

	protected void connectCodelist() {
		CodelistSelectionDialog codelistSelectionDialog = new CodelistSelectionDialog(
				parent.getEventBus());
		codelistSelectionDialog.addListener(this);
		codelistSelectionDialog.show();
	}

	@Override
	public void selected(TabResource tabResource) {
		Log.debug("Selected connection: " + tabResource);
		this.connection = tabResource;
		retrieveColumnData();

	}

	@Override
	public void aborted() {
		Log.debug("Connection Aborted");
		btnConnect.enable();

	}

	@Override
	public void failed(String reason, String detail) {
		Log.debug("Connection Failed: " + reason + " " + detail);
		UtilsGXT3.alert(msgsCommon.error(), reason);
		btnConnect.enable();

	}

	protected void retrieveColumnData() {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(connection.getTrId(),
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								parent.showErrorAndHide(
										msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								Log.debug("Error retrieving columns: "
										+ caught.getLocalizedMessage());
								parent.showErrorAndHide(msgsCommon.error(),
										msgs.errorRetrievingColumnsFixed(),
										caught.getLocalizedMessage(), caught);
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						connectionColumns = result;
						enableConnection();

					}
				});

	}

	protected void enableConnection() {
		connectionField.setValue(connection.getName());
		connectionField.setVisible(true);
		btnDisconnect.setVisible(true);
		btnConnect.setVisible(false);
		btnConnect.enable();
		toolBarHead.forceLayout();
		updatedForm();

	}

	protected void updatedForm() {
		form.clear();
		formLayout = new VerticalLayoutContainer();
		// formLayout.setScrollMode(ScrollMode.AUTO);
		formLayout.setAdjustForScroll(true);

		ArrayList<FieldLabel> fields = generateFields();
		for (FieldLabel fl : fields) {
			formLayout.add(fl, new VerticalLayoutData(1, -1, new Margins(1)));

		}

		form.add(formLayout);
		forceLayout();
	}

	public boolean updateExtractCodelistSession() {
		targetColumns = new ArrayList<ExtractCodelistTargetColumn>();

		int i = 0;
		int lenght = formLayout.getWidgetCount();

		for (; i < lenght; i++) {
			FieldLabel fieldLabel = (FieldLabel) formLayout.getWidget(i);
			String columnId = fieldLabel.getId();
			Log.debug("Field id:" + columnId);
			ColumnData colCurrent = null;
			for (ColumnData col : parent.getExtractCodelistSession()
					.getSourceColumns()) {
				if (col.getColumnId().compareTo(columnId) == 0) {
					colCurrent = col;
					Log.debug("Column Match:" + colCurrent);
					break;
				}
			}
			if (colCurrent == null) {
				UtilsGXT3.alert(msgsCommon.error(), msgs.errorCreatingForm());
				return false;
			}

			if (connection == null) {
				@SuppressWarnings("unchecked")
				ComboBox<ColumnMockUp> comboDefColumn = ((ComboBox<ColumnMockUp>) fieldLabel
						.getWidget());
				ColumnMockUp defNewColumn = comboDefColumn.getValue();
				Log.debug("Retrieved: " + defNewColumn.toString());
				ExtractCodelistTargetColumn extractCodelistTargetColumn = new ExtractCodelistTargetColumn(
						colCurrent, defNewColumn);
				Log.debug("New TargetColumn: " + extractCodelistTargetColumn);
				targetColumns.add(extractCodelistTargetColumn);
			} else {

				HBoxLayoutContainer flowButton = (HBoxLayoutContainer) fieldLabel
						.getWidget();
				CheckBox checkNew = (CheckBox) flowButton
						.getItemByItemId(CHECK_NEW);
				if (checkNew == null) {
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.errorCreatingFormForCheckRadio());
					return false;
				}

				if (checkNew.getValue()) {
					@SuppressWarnings("unchecked")
					ComboBox<ColumnMockUp> comboDefColumn = (ComboBox<ColumnMockUp>) flowButton
							.getItemByItemId(COMBO_DEF_COLUMN);
					ColumnMockUp defNewColumn = comboDefColumn.getValue();
					Log.debug("Retrieved: " + defNewColumn.toString());
					ExtractCodelistTargetColumn extractCodelistTargetCol = new ExtractCodelistTargetColumn(
							colCurrent, defNewColumn);
					Log.debug("New TargetColumn:" + extractCodelistTargetCol);
					targetColumns.add(extractCodelistTargetCol);
				} else {
					@SuppressWarnings("unchecked")
					ComboBox<ColumnData> comboColumn = (ComboBox<ColumnData>) flowButton
							.getItemByItemId(COMBO_COLUMN);
					ColumnData columnData = comboColumn.getCurrentValue();
					if (columnData == null) {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.attentionFillAllColumn());
						return false;
					} else {
						ExtractCodelistTargetColumn extractCodelistTargetC = new ExtractCodelistTargetColumn(
								colCurrent, columnData, connection.getTrId());
						Log.debug("New TargetColumn:" + extractCodelistTargetC);
						targetColumns.add(extractCodelistTargetC);
					}
				}

			}
		}

		Log.debug("UpdateExtractCodelistSession:" + targetColumns);
		parent.getExtractCodelistSession().setTargetColumns(targetColumns);
		return true;
	}

}
