package org.gcube.portlets.user.td.columnwidget.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.mapping.ColumnMappingDialog;
import org.gcube.portlets.user.td.columnwidget.client.mapping.ColumnMappingListProperties;
import org.gcube.portlets.user.td.columnwidget.client.mapping.ColumnMappingListener;
import org.gcube.portlets.user.td.columnwidget.client.properties.PeriodDataTypeProperties;
import org.gcube.portlets.user.td.columnwidget.client.properties.ValueDataFormatProperties;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnTypeCodeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.LocaleTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.TabResourcePropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.LocaleTypeElement;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.RefColumn;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ValueDataFormat;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadConfigBean;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * 
 * ChangeColumnTypePanel is the panel for change column type
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ChangeColumnTypePanel extends FramedPanel implements
		CodelistSelectionListener, ColumnMappingListener, MonitorDialogListener {

	interface ComboBoxTemplates extends XTemplates {
		@XTemplate("<div qtip=\"{example}\" qtitle=\"Format {id}\">{id}</div>")
		SafeHtml format(String id, String example);

	}

	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";
	private EventBus eventBus;
	private ChangeColumnTypeDialog parent;
	private TRId trId;
	private HashMap<ColumnDataType, ArrayList<ValueDataFormat>> valueDataFormatMap;

	private String columnName;
	private ColumnData sourceColumnChangeType;
	private ColumnData columnRequested;
	private ColumnData connectionColumn;
	private ComboBox<ColumnData> comboColumn = null;

	private ComboBox<ColumnTypeCodeElement> comboColumnTypeCode = null;
	private FieldLabel comboColumnTypeCodeLabel;

	private ComboBox<ColumnDataTypeElement> comboMeasureType = null;
	private FieldLabel comboMeasureTypeLabel;

	private ComboBox<ColumnDataTypeElement> comboAttributeType = null;
	private FieldLabel comboAttributeTypeLabel;

	private ComboBox<TabResource> comboDimensionType = null;
	private FieldLabel comboDimensionTypeLabel;
	private ListStore<TabResource> storeComboDimensionType;

	private ComboBox<ColumnData> comboColumnReferenceType = null;
	private FieldLabel comboColumnReferenceTypeLabel;
	private ListStore<ColumnData> storeComboColumnReferenceType;

	private ComboBox<ColumnMappingList> comboColumnMapping = null;
	private FieldLabel comboColumnMappingLabel;
	private ListStore<ColumnMappingList> storeComboColumnMapping;

	private ComboBox<PeriodDataType> comboPeriodType = null;
	private FieldLabel comboPeriodTypeLabel;
	private ListStore<PeriodDataType> storeComboPeriodType;

	private ListStore<ValueDataFormat> storeComboValueDataFormat;
	private ComboBox<ValueDataFormat> comboValueDataFormat;
	private FieldLabel comboValueDataFormatLabel;

	private ComboBox<LocaleTypeElement> comboLocaleType = null;
	private FieldLabel comboLocaleTypeLabel;
	private ListStore<LocaleTypeElement> storeComboLocaleType;

	private ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader;

	private TextButton applyBtn;

	private ChangeColumnTypeSession changeColumnTypeSession;
	private boolean panelCreated;
	private ChangeColumnTypeMessages msgs;
	private CommonMessages msgsCommon;
	
	public ChangeColumnTypePanel(TRId trId, String columnName, EventBus eventBus) {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus = eventBus;
		panelCreated = false;
		initMessages();
		retrieveValueDataFormatMap();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ChangeColumnTypeMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void retrieveValueDataFormatMap() {

		TDGWTServiceAsync.INSTANCE
				.getValueDataFormatsMap(new AsyncCallback<HashMap<ColumnDataType, ArrayList<ValueDataFormat>>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {

							Log.debug("Error retrieving value data formats map: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert(msgsCommon.error(),
									msgs.errorRetrievingValueDataFormatsMap());

						}

					}

					@Override
					public void onSuccess(
							HashMap<ColumnDataType, ArrayList<ValueDataFormat>> result) {
						valueDataFormatMap = result;
						if (columnName != null && !columnName.isEmpty()) {
							retrieveColumnRequested();
						} else {
							panelCreated = true;
							create();
						}
					}
				});

	}

	protected void retrieveColumnRequested() {

		TDGWTServiceAsync.INSTANCE.getColumn(trId, columnName,
				new AsyncCallback<ColumnData>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving column: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingColumnOnServerHead(),
													msgs.errorRetrievingColumnOnServer());
								}
							}
						}

					}

					@Override
					public void onSuccess(ColumnData result) {
						Log.debug("Retrieved Column: " + result);
						if (result == null) {
							UtilsGXT3.alert(msgsCommon.error(),
									msgs.requestColumnIsNull());
						}
						columnRequested = result;
						if (panelCreated) {
							loader.load();
						} else {
							panelCreated = true;
							create();
						}
					}
				});

	}

	protected void create() {

		Log.debug("Create ChangeColumnTypePanel(): [" + trId.toString()
				+ " columnName: " + columnName + "]");

		// Column Data
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);
		ListStore<ColumnData> storeCombo = new ListStore<ColumnData>(
				propsColumnData.id());

		Log.trace("StoreCombo created");

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy) {
			@Override
			protected ListLoadConfig newLoadConfig() {
				return (ListLoadConfig) new ListLoadConfigBean();
			}

		};

		// loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(
				storeCombo));
		Log.trace("LoaderCombo created");

		comboColumn = new ComboBox<ColumnData>(storeCombo,
				propsColumnData.label()) {

			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load();
					}
				});
			}
		};
		Log.trace("Combo ColumnData created");

		// addHandlersForEventObservation(comboColumn, propsColumnData.label());
		addHandlersForComboColumn(propsColumnData.label());

		comboColumn.setLoader(loader);
		comboColumn.setEmptyText(msgs.comboColumnEmptyText());
		comboColumn.setWidth(191);
		comboColumn.setTypeAhead(false);
		comboColumn.setEditable(false);
		comboColumn.setTriggerAction(TriggerAction.ALL);
		
		FieldLabel comboColumnLabel=new FieldLabel(comboColumn, msgs.comboColumnLabel());
		
		// comboColumnTypeCode
		ColumnTypeCodeProperties propsColumnTypeCode = GWT
				.create(ColumnTypeCodeProperties.class);
		ListStore<ColumnTypeCodeElement> storeComboTypeCode = new ListStore<ColumnTypeCodeElement>(
				propsColumnTypeCode.id());
		storeComboTypeCode.addAll(ColumnTypeCodeStore.getColumnTypeCodes(trId));

		comboColumnTypeCode = new ComboBox<ColumnTypeCodeElement>(
				storeComboTypeCode, propsColumnTypeCode.label());
		Log.trace("ComboColumnTypeCode created");

		addHandlersForComboColumnTypeCode(propsColumnTypeCode.label());

		comboColumnTypeCode.setEmptyText(msgs.comboColumnTypeCodeEmptyText());
		comboColumnTypeCode.setWidth(191);
		comboColumnTypeCode.setTypeAhead(true);
		comboColumnTypeCode.setTriggerAction(TriggerAction.ALL);

		comboColumnTypeCodeLabel = new FieldLabel(comboColumnTypeCode,
				msgs.comboColumnTypeCodeLabel());
		
		// comboMeasureType
		ColumnDataTypeProperties propsMeasureType = GWT
				.create(ColumnDataTypeProperties.class);
		ListStore<ColumnDataTypeElement> storeComboMeasureType = new ListStore<ColumnDataTypeElement>(
				propsMeasureType.id());
		storeComboMeasureType.addAll(ColumnDataTypeStore.getMeasureType());

		comboMeasureType = new ComboBox<ColumnDataTypeElement>(
				storeComboMeasureType, propsMeasureType.label());
		Log.trace("ComboMeasureType created");

		addHandlersForComboMeasureType(propsMeasureType.label());

		comboMeasureType.setEmptyText(msgs.comboMeasureTypeEmptyText());
		comboMeasureType.setWidth(191);
		comboMeasureType.setTypeAhead(true);
		comboMeasureType.setTriggerAction(TriggerAction.ALL);

		comboMeasureTypeLabel = new FieldLabel(comboMeasureType, msgs.comboMeasureTypeLabel());

		// comboAttributeType
		ColumnDataTypeProperties propsAttributeType = GWT
				.create(ColumnDataTypeProperties.class);
		ListStore<ColumnDataTypeElement> storeComboAttributeType = new ListStore<ColumnDataTypeElement>(
				propsAttributeType.id());
		storeComboAttributeType.addAll(ColumnDataTypeStore.getAttributeType());

		comboAttributeType = new ComboBox<ColumnDataTypeElement>(
				storeComboAttributeType, propsAttributeType.label());
		Log.trace("ComboAttributeType created");

		addHandlersForComboAttributeType(propsAttributeType.label());

		comboAttributeType.setEmptyText(msgs.comboAttributeTypeEmptyText());
		comboAttributeType.setWidth(191);
		comboAttributeType.setTypeAhead(true);
		comboAttributeType.setTriggerAction(TriggerAction.ALL);

		comboAttributeTypeLabel = new FieldLabel(comboAttributeType,
				msgs.comboAttributeTypeLabel());

		// comboLocaleType
		LocaleTypeProperties propsLocaleType = GWT
				.create(LocaleTypeProperties.class);
		storeComboLocaleType = new ListStore<LocaleTypeElement>(
				propsLocaleType.id());

		comboLocaleType = new ComboBox<LocaleTypeElement>(storeComboLocaleType,
				propsLocaleType.label()) {

			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						retriveLocales();
					}
				});
			}
		};
		Log.trace("ComboLocaleType created");

		addHandlersForComboLocaleType(propsLocaleType.label());

		comboLocaleType.setEmptyText(msgs.comboLocaleTypeEmptyText());
		comboLocaleType.setWidth(191);
		comboLocaleType.setTypeAhead(true);
		comboLocaleType.setTriggerAction(TriggerAction.ALL);

		comboLocaleTypeLabel = new FieldLabel(comboLocaleType, msgs.comboLocaleTypeLabel());

		// comboDimensionType
		TabResourcePropertiesCombo propsDimensionType = GWT
				.create(TabResourcePropertiesCombo.class);
		storeComboDimensionType = new ListStore<TabResource>(
				propsDimensionType.id());

		comboDimensionType = new ComboBox<TabResource>(storeComboDimensionType,
				propsDimensionType.label());
		Log.trace("ComboDimensionType created");

		addHandlersForComboDimensionType(propsDimensionType.label());

		comboDimensionType.setEmptyText(msgs.comboDimensionTypeEmptyText());
		comboDimensionType.setWidth(191);
		comboDimensionType.setEditable(false);
		comboDimensionType.setTriggerAction(TriggerAction.ALL);

		comboDimensionTypeLabel = new FieldLabel(comboDimensionType, msgs.comboDimensionTypeLabel());

		// ColumnReferenceType
		ColumnDataPropertiesCombo propsColumnReferenceType = GWT
				.create(ColumnDataPropertiesCombo.class);
		storeComboColumnReferenceType = new ListStore<ColumnData>(
				propsColumnReferenceType.id());

		comboColumnReferenceType = new ComboBox<ColumnData>(
				storeComboColumnReferenceType, propsColumnReferenceType.label());
		Log.trace("ComboColumnReferenceType created");

		addHandlersForComboColumnReferenceType(propsColumnReferenceType.label());

		comboColumnReferenceType.setEmptyText(msgs.comboColumnReferenceTypeEmptyText());
		comboColumnReferenceType.setWidth(191);
		comboColumnReferenceType.setEditable(false);
		comboColumnReferenceType.setTriggerAction(TriggerAction.ALL);

		comboColumnReferenceTypeLabel = new FieldLabel(
				comboColumnReferenceType, msgs.comboColumnReferenceTypeLabel());

		// ColumnMapping
		ColumnMappingListProperties propsColumnMapping = GWT
				.create(ColumnMappingListProperties.class);
		storeComboColumnMapping = new ListStore<ColumnMappingList>(
				propsColumnMapping.id());

		comboColumnMapping = new ComboBox<ColumnMappingList>(
				storeComboColumnMapping, propsColumnMapping.name());
		Log.trace("ComboColumnMapping created");

		addHandlersForComboColumnMapping(propsColumnMapping.name());

		comboColumnMapping.setEmptyText(msgs.comboColumnMappingEmptyText());
		comboColumnMapping.setWidth(191);
		comboColumnMapping.setEditable(false);
		comboColumnMapping.setTriggerAction(TriggerAction.ALL);

		comboColumnMappingLabel = new FieldLabel(comboColumnMapping, msgs.comboColumnMappingLabel());

		// comboPeriodType
		PeriodDataTypeProperties propsPeriodDataType = GWT
				.create(PeriodDataTypeProperties.class);
		storeComboPeriodType = new ListStore<PeriodDataType>(
				propsPeriodDataType.name());

		comboPeriodType = new ComboBox<PeriodDataType>(storeComboPeriodType,
				propsPeriodDataType.label()) {
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						retrievePeriodDataType();
					}
				});
			}
		};

		Log.trace("ComboPeriodType created");

		addHandlersForComboPeriodType(propsPeriodDataType.label());

		comboPeriodType.setEmptyText(msgs.comboPeriodTypeEmptyText());
		comboPeriodType.setWidth(191);
		comboPeriodType.setTypeAhead(true);
		comboPeriodType.setTriggerAction(TriggerAction.ALL);
		comboPeriodTypeLabel = new FieldLabel(comboPeriodType, msgs.comboPeriodTypeLabel());

		// comboValueDataFormat
		ValueDataFormatProperties propsValueDataFormat = GWT
				.create(ValueDataFormatProperties.class);
		storeComboValueDataFormat = new ListStore<ValueDataFormat>(
				propsValueDataFormat.id());

		comboValueDataFormat = new ComboBox<ValueDataFormat>(
				storeComboValueDataFormat, propsValueDataFormat.label(),
				new AbstractSafeHtmlRenderer<ValueDataFormat>() {
					public SafeHtml render(ValueDataFormat item) {
						final ComboBoxTemplates comboBoxTemplates = GWT
								.create(ComboBoxTemplates.class);
						return comboBoxTemplates.format(item.getId(),
								item.getExample());
					}
				}

		);

		Log.trace("ComboTimeDataFormat created");

		addHandlersForComboValueDataFormat(propsValueDataFormat.label());

		comboValueDataFormat.setEmptyText(msgs.comboValueDataFormatEmptyText());
		comboValueDataFormat.setWidth(191);
		comboValueDataFormat.setTypeAhead(true);
		comboValueDataFormat.setTriggerAction(TriggerAction.ALL);
		comboValueDataFormatLabel = new FieldLabel(comboValueDataFormat,
				msgs.comboValueDataFormatLabel());

		// Apply
		applyBtn = new TextButton(msgs.applyBtnText());
		applyBtn.setIcon(ResourceBundle.INSTANCE.columnType());
		applyBtn.setIconAlign(IconAlign.RIGHT);
		applyBtn.setToolTip(msgs.applyBtnToolTip());

		SelectHandler changeHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onChangeTypeColumn();

			}
		};
		applyBtn.addSelectHandler(changeHandler);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(comboColumnLabel, new VerticalLayoutData(1,
				-1, new Margins(1)));
		v.add(comboColumnTypeCodeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(comboLocaleTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(comboMeasureTypeLabel, new VerticalLayoutData(1, -1, new Margins(
				1)));
		v.add(comboAttributeTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(comboDimensionTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(comboColumnReferenceTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		/*
		 * v.add(comboColumnMappingLabel, new VerticalLayoutData(1, -1, new
		 * Margins(1)));
		 */
		v.add(comboPeriodTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(comboValueDataFormatLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));
		v.add(applyBtn, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(1, 1, new Margins(0)));

		resetToInitialState();

		// addButton();
	}

	protected void resetToInitialState() {
		comboMeasureTypeLabel.setVisible(false);
		comboAttributeTypeLabel.setVisible(false);
		comboDimensionTypeLabel.setVisible(false);
		comboColumnReferenceTypeLabel.setVisible(false);
		comboColumnMappingLabel.setVisible(false);
		comboPeriodTypeLabel.setVisible(false);
		comboValueDataFormat.setValidateOnBlur(false);
		comboLocaleTypeLabel.setVisible(false);
	}

	protected void addHandlersForComboColumn(
			final LabelProvider<ColumnData> labelProvider) {
		comboColumn.addSelectionHandler(new SelectionHandler<ColumnData>() {
			public void onSelection(SelectionEvent<ColumnData> event) {
				Log.debug("ComboColumn selected: " + event.getSelectedItem());
				ColumnData columnData = event.getSelectedItem();
				updateComboStatus(columnData);
			}

		});
	}

	protected void addHandlersForComboColumnTypeCode(
			final LabelProvider<ColumnTypeCodeElement> labelProvider) {
		comboColumnTypeCode
				.addSelectionHandler(new SelectionHandler<ColumnTypeCodeElement>() {
					public void onSelection(
							SelectionEvent<ColumnTypeCodeElement> event) {
						Log.debug("ComboColumnTypeCode selected: "
								+ event.getSelectedItem());
						ColumnTypeCodeElement columnType = event
								.getSelectedItem();
						updateColumnType(columnType.getCode());
						updateConfBtnChange(columnType.getCode());
					}

				});
	}

	protected void addHandlersForComboMeasureType(
			final LabelProvider<ColumnDataTypeElement> labelProvider) {
		comboMeasureType
				.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {
						Log.debug("ComboMeasureType selected: "
								+ event.getSelectedItem());
						ColumnDataTypeElement measureType = event
								.getSelectedItem();
						updateMeasureType(measureType.getType());
					}

				});
	}

	protected void addHandlersForComboAttributeType(
			final LabelProvider<ColumnDataTypeElement> labelProvider) {
		comboAttributeType
				.addSelectionHandler(new SelectionHandler<ColumnDataTypeElement>() {
					public void onSelection(
							SelectionEvent<ColumnDataTypeElement> event) {
						Log.debug("ComboAttributeType selected: "
								+ event.getSelectedItem());
						ColumnDataTypeElement attributeType = event
								.getSelectedItem();
						updateAttributeType(attributeType.getType());
					}

				});
	}

	protected void addHandlersForComboLocaleType(
			final LabelProvider<LocaleTypeElement> labelProvider) {
		comboLocaleType
				.addSelectionHandler(new SelectionHandler<LocaleTypeElement>() {
					public void onSelection(
							SelectionEvent<LocaleTypeElement> event) {
						Log.debug("ComboLocaleType selected: "
								+ event.getSelectedItem());
						LocaleTypeElement localeType = event.getSelectedItem();
						updateLocaleType(localeType);
					}

				});
	}

	protected void addHandlersForComboDimensionType(
			final LabelProvider<TabResource> labelProvider) {

		comboDimensionType.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboDimension TriggerClickEvent");
				callDialogCodelistSelection();
				comboDimensionType.collapse();

			}

		});

	}

	protected void addHandlersForComboColumnReferenceType(
			final LabelProvider<ColumnData> labelProvider) {

		comboColumnReferenceType
				.addSelectionHandler(new SelectionHandler<ColumnData>() {
					public void onSelection(SelectionEvent<ColumnData> event) {
						Log.debug("ComboColumnReferenceType selected: "
								+ event.getSelectedItem());

					}

				});

	}

	protected void addHandlersForComboColumnMapping(
			final LabelProvider<ColumnMappingList> labelProvider) {

		comboColumnMapping.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboColumnMapping TriggerClickEvent");
				callColumnMappingDialog();
				comboColumnMapping.collapse();

			}

		});

	}

	protected void addHandlersForComboPeriodType(
			final LabelProvider<PeriodDataType> labelProvider) {
		comboPeriodType
				.addSelectionHandler(new SelectionHandler<PeriodDataType>() {
					public void onSelection(SelectionEvent<PeriodDataType> event) {
						Log.debug("ComboPeriodType selected: "
								+ event.getSelectedItem());
						PeriodDataType periodDataType = event.getSelectedItem();
						updatePeriodType(periodDataType);
					}

				});
	}

	protected void addHandlersForComboValueDataFormat(
			final LabelProvider<ValueDataFormat> labelProvider) {
		comboValueDataFormat
				.addSelectionHandler(new SelectionHandler<ValueDataFormat>() {
					public void onSelection(
							SelectionEvent<ValueDataFormat> event) {
						Log.debug("ComboTimeDataFormat selected: "
								+ event.getSelectedItem());
						ValueDataFormat timeDataFormat = event
								.getSelectedItem();
						updateTimeDataFormat(timeDataFormat);
					}

				});
	}

	protected void updateColumnType(ColumnTypeCode type) {
		Log.debug("Update ColumnTypeCode: " + type);
		switch (type) {
		case CODENAME:
			comboLocaleTypeLabel.setVisible(true);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(false);
			comboValueDataFormatLabel.setVisible(false);
			break;
		case ATTRIBUTE:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(true);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(false);
			comboValueDataFormatLabel.setVisible(false);
			comboAttributeType.reset();
			break;
		case DIMENSION:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(true);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(false);
			comboValueDataFormatLabel.setVisible(false);
			break;
		case MEASURE:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(true);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(false);
			comboValueDataFormatLabel.setVisible(false);
			comboMeasureType.reset();
			break;
		case TIMEDIMENSION:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(true);
			comboValueDataFormatLabel.setVisible(true);
			break;
		default:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboColumnMappingLabel.setVisible(false);
			comboPeriodTypeLabel.setVisible(false);
			comboValueDataFormatLabel.setVisible(false);
			break;
		}

		// Reset comboDimensionType
		comboDimensionType.reset();
		comboDimensionType.clear();
		storeComboDimensionType.commitChanges();
		forceLayout();
	}

	protected void updateMeasureType(ColumnDataType type) {
		comboValueDataFormatLabel.setVisible(true);
		ArrayList<ValueDataFormat> valueDataFormats = valueDataFormatMap
				.get(type);
		comboValueDataFormat.clear();
		comboValueDataFormat.reset();
		comboValueDataFormat.getStore().clear();
		comboValueDataFormat.getStore().addAll(valueDataFormats);
		comboValueDataFormat.redraw();
	}

	protected void updateAttributeType(ColumnDataType type) {
		if (type.compareTo(ColumnDataType.Text) == 0) {
			comboValueDataFormatLabel.setVisible(false);
		} else {
			comboValueDataFormatLabel.setVisible(true);
			ArrayList<ValueDataFormat> valueDataFormats = valueDataFormatMap
					.get(type);
			comboValueDataFormat.clear();
			comboValueDataFormat.reset();
			comboValueDataFormat.getStore().clear();
			comboValueDataFormat.getStore().addAll(valueDataFormats);
			comboValueDataFormat.redraw();
		}
	}

	protected void updateLocaleType(LocaleTypeElement type) {

	}

	protected void updatePeriodType(PeriodDataType periodDataType) {

		ArrayList<ValueDataFormat> valueDataFormats = periodDataType
				.getTimeDataFormats();
		comboValueDataFormat.clear();
		comboValueDataFormat.reset();
		comboValueDataFormat.getStore().clear();
		comboValueDataFormat.getStore().addAll(valueDataFormats);
		comboValueDataFormat.redraw();

	}

	protected void updateTimeDataFormat(ValueDataFormat type) {

	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load combo failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.error(),
											msgs.errorRetrievingColumnsOfTabularResource()
													+ trId.getId());
								}
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						setComboStatus(result);
						callback.onSuccess(new ListLoadResultBean<ColumnData>(
								result));

					}

				});

	}

	protected void setComboStatus(ArrayList<ColumnData> result) {
		Log.debug("ColumnRequested: " + columnRequested);
		if (columnRequested != null) {
			String columnId;
			if (columnRequested.isViewColumn()) {
				columnId = columnRequested.getColumnViewData()
						.getSourceTableDimensionColumnId();
			} else {
				columnId = columnRequested.getColumnId();
			}

			for (ColumnData cd : result) {
				Log.debug("Column:" + cd.getColumnId());
				if (cd.getColumnId().compareTo(columnId) == 0) {
					updateComboStatus(cd);
					return;
				}
			}
		} else {
			applyBtn.disable();
			comboColumnTypeCode.reset();
			comboColumnTypeCodeLabel.setVisible(false);
		}
	}

	protected void updateComboStatus(ColumnData cd) {
		sourceColumnChangeType = cd;
		Log.debug("Update Combos ColumnData: " + cd);
		changeColumnTypeSession = new ChangeColumnTypeSession();
		changeColumnTypeSession.setColumnData(cd);
		comboColumn.setValue(cd);
		comboColumnTypeCodeLabel.setVisible(true);
		comboColumnTypeCode.setValue(ColumnTypeCodeStore.selectedElement(cd
				.getTypeCode()));
		ColumnTypeCode type = ColumnTypeCodeStore.selected(cd.getTypeCode());
		changeColumnTypeSession.setColumnTypeCode(type);
		updateColumnType(type);
		if (type == ColumnTypeCode.MEASURE) {
			changeColumnTypeSession.setColumnDataType(ColumnDataTypeStore
					.selectedMeasure(cd.getDataTypeName()));
			comboMeasureType.setValue(ColumnDataTypeStore
					.selectedMeasureElement(cd.getDataTypeName()));
			comboValueDataFormatLabel.setVisible(true);
			ColumnDataType columnDataType = ColumnDataType
					.getColumnDataTypeFromId(cd.getDataTypeName());
			ArrayList<ValueDataFormat> valueDataFormats = valueDataFormatMap
					.get(columnDataType);
			comboValueDataFormat.clear();
			comboValueDataFormat.reset();
			comboValueDataFormat.getStore().clear();
			comboValueDataFormat.getStore().addAll(valueDataFormats);
			comboValueDataFormat.redraw();

		} else {
			if (type == ColumnTypeCode.ATTRIBUTE) {
				changeColumnTypeSession.setColumnDataType(ColumnDataTypeStore
						.selectedAttribute(cd.getDataTypeName()));
				comboAttributeType.setValue(ColumnDataTypeStore
						.selectedAttributeElement(cd.getDataTypeName()));
				ColumnDataType columnDataType = ColumnDataType
						.getColumnDataTypeFromId(cd.getDataTypeName());
				if (columnDataType.compareTo(ColumnDataType.Text) == 0) {
					comboValueDataFormatLabel.setVisible(false);
				} else {
					comboValueDataFormatLabel.setVisible(true);
					ArrayList<ValueDataFormat> valueDataFormats = valueDataFormatMap
							.get(columnDataType);
					comboValueDataFormat.clear();
					comboValueDataFormat.reset();
					comboValueDataFormat.getStore().clear();
					comboValueDataFormat.getStore().addAll(valueDataFormats);
					comboValueDataFormat.redraw();
				}
			} else {
				if (type == ColumnTypeCode.CODENAME) {
					setLocale(cd.getLocale());
				} else {

					if (type == ColumnTypeCode.TIMEDIMENSION) {
						changeColumnTypeSession.setPeriodDataType(cd
								.getPeriodDataType());
						comboPeriodType.setValue(cd.getPeriodDataType());
						ArrayList<ValueDataFormat> valueDataFormats = cd
								.getPeriodDataType().getTimeDataFormats();
						comboValueDataFormat.clear();
						comboValueDataFormat.reset();
						comboValueDataFormat.getStore().clear();
						comboValueDataFormat.getStore()
								.addAll(valueDataFormats);
						comboValueDataFormat.redraw();
					} else {
						// TODO
						if (type == ColumnTypeCode.DIMENSION) {
							RefColumn refColumn = new RefColumn(
									String.valueOf(cd.getRelationship()
											.getTargetTableId()), cd
											.getRelationship()
											.getTargetColumnId());
							retrieveConnectionForViewColumn(refColumn);

						} else {

						}
					}

				}
			}

		}
		updateConfBtnChange(type);

	}

	public void update(TRId trId, String columnName) {
		this.trId = trId;
		this.columnName = columnName;

		if (columnName != null && !columnName.isEmpty()) {
			retrieveColumnRequested();
		} else {

			columnRequested = null;
			comboColumn.reset();
			comboDimensionType.clear();
			resetToInitialState();
			// Reset comboDimensionType
			comboDimensionType.reset();
			comboDimensionType.clear();
			storeComboDimensionType.commitChanges();

			loader.load();
		}

	}

	protected void retrieveConnectionForViewColumn(RefColumn refCol) {
		Log.debug("Retrieve Connection For View Column: " + refCol);
		TDGWTServiceAsync.INSTANCE.getConnection(refCol,
				new AsyncCallback<ColumnData>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load column failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingConnectionHead(),
													msgs.errorRetrievingConnection());
								}
							}
						}
					}

					public void onSuccess(ColumnData result) {
						Log.debug("Column: " + result);
						connectionColumn = result;
						retrieveTabularResource(result.getTrId());
					}

				});

	}

	protected void updateConfBtnChange(ColumnData columnReference) {
		if (connectionColumn == null) {
			if (columnReference != null) {
				applyBtn.enable();
			} else {
				applyBtn.disable();
			}
		} else {
			if (columnReference == null) {
				applyBtn.disable();
			} else {
				if (columnReference.getColumnId().compareTo(
						connectionColumn.getColumnId()) == 0) {
					applyBtn.disable();
				} else {
					applyBtn.enable();
				}
			}

		}
	}

	protected void updateConfBtnChange(ColumnTypeCode columnTypeCode) {
		if (columnTypeCode == null) {
			ColumnTypeCodeElement codeElement = comboColumnTypeCode.getValue();
			if (codeElement == null) {
				applyBtn.disable();
			} else {
				configureBtnChange(codeElement.getCode());
			}
		} else {
			configureBtnChange(columnTypeCode);
		}
	}

	//
	protected void configureBtnChange(ColumnTypeCode columnTypeCode) {
		ColumnTypeCode sourceTypeCode = ColumnTypeCode
				.getColumnTypeCodeFromId(sourceColumnChangeType.getTypeCode());

		switch (sourceTypeCode) {
		case ANNOTATION:
			applyBtn.enable();
			break;
		case ATTRIBUTE:
			applyBtn.enable();
			break;
		case CODE:
			applyBtn.enable();
			break;
		case CODEDESCRIPTION:
			applyBtn.enable();
			break;
		case CODENAME:
			applyBtn.enable();
			break;
		case DIMENSION:
			if (columnTypeCode.compareTo(ColumnTypeCode.DIMENSION) == 0
					|| columnTypeCode.compareTo(ColumnTypeCode.TIMEDIMENSION) == 0) {
				applyBtn.disable();
			} else {
				applyBtn.enable();
			}
			break;
		case MEASURE:
			applyBtn.enable();
			break;
		case TIMEDIMENSION:
			if (columnTypeCode.compareTo(ColumnTypeCode.DIMENSION) == 0
					|| columnTypeCode.compareTo(ColumnTypeCode.TIMEDIMENSION) == 0) {
				applyBtn.disable();
			} else {
				applyBtn.enable();
			}
			break;
		default:
			applyBtn.enable();
			break;
		}

	}

	protected void onChangeTypeColumn() {

		ColumnData columnData = comboColumn.getCurrentValue();
		if (columnData != null) {
			ColumnTypeCodeElement columnTypeCodeElement = comboColumnTypeCode
					.getCurrentValue();
			if (columnTypeCodeElement != null) {
				ColumnTypeCode type = columnTypeCodeElement.getCode();
				ColumnDataTypeElement columnDataTypeElement;
				switch (type) {
				case MEASURE:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					columnDataTypeElement = comboMeasureType.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							changeColumnTypeSession
									.setColumnDataTypeTarget(dataType);
							ValueDataFormat valueDataFormat = comboValueDataFormat
									.getCurrentValue();
							if (valueDataFormat != null) {
								changeColumnTypeSession
										.setValueDataFormat(valueDataFormat);
								callChangeColumnType();
							} else {
								UtilsGXT3.alert(msgsCommon.attention(),
										msgs.typeFormatNotSelected());
							}

						} else {
							UtilsGXT3.alert(msgsCommon.attention(),
									msgs.columnDataTypeNotSelected());
						}

					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.columnDataTypeNotSelected());
					}
					break;
				case ATTRIBUTE:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					columnDataTypeElement = comboAttributeType
							.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							changeColumnTypeSession
									.setColumnDataTypeTarget(dataType);
							ValueDataFormat valueDataFormat = comboValueDataFormat
									.getCurrentValue();
							if (valueDataFormat != null
									|| (valueDataFormat == null && dataType
											.compareTo(ColumnDataType.Text) == 0)) {
								changeColumnTypeSession
										.setValueDataFormat(valueDataFormat);
								callChangeColumnType();
							} else {
								UtilsGXT3.alert(msgsCommon.attention(),
										msgs.typeFormatNotSelected());
							}
						} else {
							UtilsGXT3.alert(msgsCommon.attention(),
									msgs.columnDataTypeNotSelected());
						}
					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.columnDataTypeNotSelected());
					}
					break;
				case CODE:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					callChangeColumnType();
					break;
				case CODENAME:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					LocaleTypeElement locale = comboLocaleType
							.getCurrentValue();
					if (locale != null) {
						changeColumnTypeSession.setLocale(locale
								.getLocaleName());
						callChangeColumnType();
					} else {
						UtilsGXT3.alert(msgsCommon.attention(), msgs.noLocaleSelected());
					}
					break;
				case CODEDESCRIPTION:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					callChangeColumnType();
					break;
				case ANNOTATION:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					callChangeColumnType();
					break;
				case DIMENSION:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					ColumnData columnReference = comboColumnReferenceType
							.getCurrentValue();
					if (columnReference != null) {
						ColumnMappingList mapping = comboColumnMapping
								.getValue();
						if (mapping != null) {
							changeColumnTypeSession
									.setColumnMappingList(mapping);
						}
						changeColumnTypeSession
								.setCodelistColumnReference(columnReference);
						callChangeColumnType();
					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.referenceColumnNotSelected());
					}
					break;
				case TIMEDIMENSION:
					changeColumnTypeSession.setColumnTypeCodeTarget(type);
					PeriodDataType periodDataType = comboPeriodType
							.getCurrentValue();
					if (periodDataType != null) {
						changeColumnTypeSession
								.setPeriodDataType(periodDataType);
						ValueDataFormat timeDataFormat = comboValueDataFormat
								.getCurrentValue();
						if (timeDataFormat != null) {
							changeColumnTypeSession
									.setValueDataFormat(timeDataFormat);
							callChangeColumnType();
						} else {
							UtilsGXT3.alert(msgsCommon.attention(),
									msgs.timeFormatNotSelected());
						}

					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.periodTypeNotSelected());
					}
					break;
				default:
					UtilsGXT3.alert(msgsCommon.attention(),
							msgs.thisColumnTypeIsNotSupportedForNow());
					break;
				}
			} else {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAColumnType());
			}
		} else {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAColumn());
		}
	}

	private void callChangeColumnType() {
		TDGWTServiceAsync.INSTANCE.startChangeColumnType(
				changeColumnTypeSession, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Change Column Type Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.changeColumnTypeErrorHead(),
													msgs.changeColumnTypeError());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);

					}

				});

	}

	protected void callDialogCodelistSelection() {
		CodelistSelectionDialog dialogCodelistSelection = new CodelistSelectionDialog(
				eventBus);
		dialogCodelistSelection.addListener(this);
		dialogCodelistSelection.show();
	}

	@Override
	public void selected(TabResource tabResource) {
		Log.debug("Selected Codelist: " + tabResource);
		comboDimensionType.setValue(tabResource, true);

		retrieveColumnsForDimension(tabResource.getTrId());
	}

	@Override
	public void aborted() {
		Log.debug("Select Codelist Aborted");

	}

	@Override
	public void failed(String reason, String detail) {
		Log.error("Select Codelist Failed[reason: " + reason + " , detail:"
				+ detail + "]");

	}

	protected void retrieveColumnsForDimension(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving columns: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingColumnOnServerHead(),
													msgs.errorRetrievingColumnOnServer());
								}
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						comboColumnReferenceType.reset();
						storeComboColumnReferenceType.clear();
						storeComboColumnReferenceType.addAll(result);
						storeComboColumnReferenceType.commitChanges();
						comboColumnReferenceTypeLabel.setVisible(true);
						forceLayout();
					}
				});

	}

	protected void retrieveColumnsForSelectedDimension(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving columns: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingColumnOnServerHead(),
													msgs.errorRetrievingColumnOnServer());
								}
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						comboColumnReferenceType.reset();
						storeComboColumnReferenceType.clear();
						storeComboColumnReferenceType.addAll(result);
						storeComboColumnReferenceType.commitChanges();
						comboColumnReferenceTypeLabel.setVisible(true);
						comboColumnReferenceType.setValue(connectionColumn,
								true);
						forceLayout();
					}
				});

	}

	protected void retrieveTabularResource(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId,
				new AsyncCallback<TabResource>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving tabular resource: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingTabularResource(),
													caught.getLocalizedMessage());
								}
							}
						}

					}

					@Override
					public void onSuccess(TabResource result) {
						comboDimensionType.setValue(result, true);
						retrieveColumnsForSelectedDimension(result.getTrId());

					}

				});

	}

	protected void retriveLocales() {
		TDGWTServiceAsync.INSTANCE
				.getLocales(new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingLocales(),
											caught.getLocalizedMessage());
								}
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						storeComboLocaleType.clear();
						ArrayList<LocaleTypeElement> locales = new ArrayList<LocaleTypeElement>();
						LocaleTypeElement locale;
						for (String local : result) {
							locale = new LocaleTypeElement(local);
							locales.add(locale);
						}
						storeComboLocaleType.addAll(locales);
						storeComboLocaleType.commitChanges();
						// comboColumnReferenceTypeLabel.setVisible(true);

					}
				});

	}

	protected void retrievePeriodDataType() {
		TDGWTServiceAsync.INSTANCE
				.getPeriodDataTypes(new AsyncCallback<ArrayList<PeriodDataType>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug(caught.getLocalizedMessage());
									UtilsGXT3.alert(
											msgs.errorRetrievingPeriodType(),
											caught.getLocalizedMessage());
								}
							}
						}

					}

					@Override
					public void onSuccess(ArrayList<PeriodDataType> result) {
						storeComboPeriodType.clear();
						storeComboPeriodType.addAll(result);
						storeComboPeriodType.commitChanges();

					}
				});

	}

	protected void setLocale(String locale) {
		for (LocaleTypeElement loc : storeComboLocaleType.getAll()) {
			if (loc.getLocaleName().compareTo(locale) == 0) {
				comboLocaleType.setValue(loc);
				break;
			}
		}
	}

	// TODO
	protected void callColumnMappingDialog() {
		ColumnData selectedColumn = comboColumn.getCurrentValue();
		if (selectedColumn == null) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.columnNotSelected());
			return;
		}

		TabResource dimensionTR = comboDimensionType.getValue();
		if (dimensionTR == null) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.codelistNotSelected());
			return;
		}

		ColumnData referenceColumn = comboColumnReferenceType.getValue();
		if (referenceColumn == null) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.referenceColumnNotSelected());
			return;
		}

		ColumnMappingDialog columnMappingDialog = new ColumnMappingDialog(trId,
				selectedColumn, dimensionTR, referenceColumn, eventBus);
		columnMappingDialog.addColumnMappingListener(this);
		columnMappingDialog.show();
	}

	@Override
	public void selectedColumnMapping(ColumnMappingList columnMappingList) {
		Log.debug("Selected ColumnMapping: " + columnMappingList);
		comboColumnMapping.setValue(columnMappingList, true);

	}

	@Override
	public void abortedColumnMapping() {
		Log.debug("Column Mapping selection Aborted");

	}

	@Override
	public void failedColumnMapping(String reason, String detail) {
		Log.error("Error selecting Column Mapping:" + reason + " " + detail);

	}

	public void close() {
		if (parent != null) {
			parent.close();
		}
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.CHANGECOLUMNTYPE,
				operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.CHANGECOLUMNTYPE,
				operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
