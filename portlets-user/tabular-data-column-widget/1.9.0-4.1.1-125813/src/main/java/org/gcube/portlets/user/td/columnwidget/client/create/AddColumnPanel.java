package org.gcube.portlets.user.td.columnwidget.client.create;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.properties.PeriodDataTypeProperties;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.ReplaceExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnTypeCodeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.LocaleTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.TabResourcePropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.LocaleTypeElement;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
//import com.google.gwt.regexp.shared.MatchResult;
//import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AddColumnPanel extends FramedPanel implements
		CodelistSelectionListener, MonitorDialogListener {
	// private static final String GEOMETRY_REGEXPR =
	// "(\\s*POINT\\s*\\(\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$)"
	// +
	// "|(\\s*LINESTRING\\s*\\((\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*,)+\\s*((-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*)\\)\\s*$)";

	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private EventBus eventBus;
	private AddColumnDialog parent;
	private TRId trId;

	private ComboBox<ColumnTypeCodeElement> comboColumnTypeCode = null;

	private ComboBox<ColumnDataTypeElement> comboMeasureType = null;
	private FieldLabel comboMeasureTypeLabel;

	private ComboBox<ColumnDataTypeElement> comboAttributeType = null;
	private FieldLabel comboAttributeTypeLabel;

	private ComboBox<TabResource> comboDimensionType = null;
	private FieldLabel comboDimensionTypeLabel;

	private ComboBox<ColumnData> comboColumnReferenceType = null;
	private FieldLabel comboColumnReferenceTypeLabel;
	private ListStore<ColumnData> storeComboColumnReferenceType;

	private ComboBox<PeriodDataType> comboTimeDimensionType = null;
	private FieldLabel comboTimeDimensionTypeLabel;
	private ListStore<PeriodDataType> storeComboTimeDimensionType;

	private ComboBox<LocaleTypeElement> comboLocaleType = null;
	private FieldLabel comboLocaleTypeLabel;
	private ListStore<LocaleTypeElement> storeComboLocaleType;

	private AddColumnSession addColumnSession;

	private TextButton btnAddColumn;

	private TextField columnLabelField;
	private TextField defaultValueString;
	private FieldLabel defaultStringLabel;

	private ExpressionWrapper exWrapper;
	private TextButton btnRemoveExpression;
	private TextButton btnAddExpression;
	private AddColumnMessages msgs;
	private CommonMessages msgsCommon;

	/**
	 * 
	 * @param parent
	 * @param eventBus
	 */
	public AddColumnPanel(AddColumnDialog parent, TRId trId, EventBus eventBus) {
		super();
		Log.debug("CreateDefColumnPanel[parent: " + parent + ", trId: " + trId);
		// this.thisPanel = this;
		this.parent = parent;
		this.eventBus = eventBus;
		this.trId = trId;
		initMessages();
		init();
		create();
	}

	/**
	 * 
	 * @param trId
	 * @param eventBus
	 */
	public AddColumnPanel(TRId trId, EventBus eventBus) {
		super();
		Log.debug("CreateDefColumnPanel[trId: " + trId);
		// this.thisPanel = this;
		this.eventBus = eventBus;
		this.trId = trId;
		initMessages();
		init();
		create();
	}

	protected void initMessages() {
		msgs = GWT.create(AddColumnMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		// Label
		columnLabelField = new TextField();
		FieldLabel columnLabelFieldLabel = new FieldLabel(columnLabelField,
				msgs.columnLabelFieldLabel());

		// Flow Expression
		HBoxLayoutContainer flowExpression = new HBoxLayoutContainer();
		flowExpression.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowExpression.setPack(BoxLayoutPack.START);
		flowExpression.setAdjustForFlexRemainder(true);

		// Default Value
		defaultValueString = new TextField();
		defaultValueString.setReadOnly(true);
		// defaultValueString.setWidth("166px");
		defaultValueString.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				openReplaceExpressionDialog();

			}
		}, ClickEvent.getType());

		btnAddExpression = new TextButton();
		btnAddExpression.setIcon(ExpressionResources.INSTANCE.add());
		btnAddExpression.setToolTip(msgs.btnAddExpressionToolTip());
		btnAddExpression.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Add Expression");
				openReplaceExpressionDialog();
			}
		});
		btnAddExpression.setVisible(true);

		btnRemoveExpression = new TextButton();
		btnRemoveExpression.setIcon(ExpressionResources.INSTANCE.delete());
		btnRemoveExpression.setToolTip(msgs.btnRemoveExpressionToolTip());
		btnRemoveExpression.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Remove Expression");
				removeExpression();
			}
		});
		btnRemoveExpression.setVisible(false);

		BoxLayoutData flex1 = new BoxLayoutData(new Margins(0));
		flex1.setFlex(0);

		BoxLayoutData flex2 = new BoxLayoutData(new Margins(0));
		flex2.setFlex(1);

		flowExpression.add(btnAddExpression, flex1);
		flowExpression.add(btnRemoveExpression, flex1);
		flowExpression.add(defaultValueString, flex2);

		defaultStringLabel = new FieldLabel(flowExpression,
				msgs.defaultStringLabel());

		// comboColumnTypeCode
		ColumnTypeCodeProperties propsColumnTypeCode = GWT
				.create(ColumnTypeCodeProperties.class);
		ListStore<ColumnTypeCodeElement> storeComboTypeCode = new ListStore<ColumnTypeCodeElement>(
				propsColumnTypeCode.id());
		storeComboTypeCode.addAll(ColumnTypeCodeStore
				.getColumnTypeCodesForAddColumn(trId));

		comboColumnTypeCode = new ComboBox<ColumnTypeCodeElement>(
				storeComboTypeCode, propsColumnTypeCode.label());
		Log.trace("ComboColumnTypeCode created");

		addHandlersForComboColumnTypeCode(propsColumnTypeCode.label());

		comboColumnTypeCode.setEmptyText(msgs.comboColumnTypeCodeEmptyText());
		comboColumnTypeCode.setWidth(191);
		comboColumnTypeCode.setTypeAhead(true);
		comboColumnTypeCode.setTriggerAction(TriggerAction.ALL);

		FieldLabel comboColumnTypeCodeLabel = new FieldLabel(
				comboColumnTypeCode, msgs.comboColumnTypeCodeLabel());

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

		comboMeasureTypeLabel = new FieldLabel(comboMeasureType,
				msgs.comboMeasureTypeLabel());

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
						retrieveLocales();
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

		comboLocaleTypeLabel = new FieldLabel(comboLocaleType,
				msgs.comboLocaleTypeLabel());

		// comboDimensionType
		TabResourcePropertiesCombo propsDimensionType = GWT
				.create(TabResourcePropertiesCombo.class);
		ListStore<TabResource> storeComboDimensionType = new ListStore<TabResource>(
				propsDimensionType.id());

		comboDimensionType = new ComboBox<TabResource>(storeComboDimensionType,
				propsDimensionType.label());
		Log.trace("ComboDimensionType created");

		addHandlersForComboDimensionType(propsDimensionType.label());

		comboDimensionType.setEmptyText(msgs.comboDimensionTypeEmptyText());
		comboDimensionType.setWidth(191);
		comboDimensionType.setEditable(false);
		comboDimensionType.setTriggerAction(TriggerAction.ALL);

		comboDimensionTypeLabel = new FieldLabel(comboDimensionType,
				msgs.comboDimensionTypeLabel());

		// ColumnReferenceType
		ColumnDataPropertiesCombo propsColumnReferenceType = GWT
				.create(ColumnDataPropertiesCombo.class);
		storeComboColumnReferenceType = new ListStore<ColumnData>(
				propsColumnReferenceType.id());

		comboColumnReferenceType = new ComboBox<ColumnData>(
				storeComboColumnReferenceType, propsColumnReferenceType.label());
		Log.trace("ComboColumnReferenceType created");

		addHandlersForComboColumnReferenceType(propsColumnReferenceType.label());

		comboColumnReferenceType.setEmptyText(msgs
				.comboColumnReferenceTypeEmptyText());
		comboColumnReferenceType.setWidth(191);
		comboColumnReferenceType.setEditable(false);
		comboColumnReferenceType.setTriggerAction(TriggerAction.ALL);

		comboColumnReferenceTypeLabel = new FieldLabel(
				comboColumnReferenceType, msgs.comboColumnReferenceTypeLabel());

		// comboTimeDimensionType
		PeriodDataTypeProperties propsTimeDimensionType = GWT
				.create(PeriodDataTypeProperties.class);
		storeComboTimeDimensionType = new ListStore<PeriodDataType>(
				propsTimeDimensionType.name());

		comboTimeDimensionType = new ComboBox<PeriodDataType>(
				storeComboTimeDimensionType, propsTimeDimensionType.label()) {
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						retrievePeriodDataType();
					}
				});
			}
		};
		Log.trace("ComboTimeDimensionType created");

		addHandlersForComboTimeDimensionType(propsTimeDimensionType.label());

		comboTimeDimensionType.setEmptyText(msgs
				.comboTimeDimensionTypeEmptyText());
		comboTimeDimensionType.setWidth(191);
		comboTimeDimensionType.setTypeAhead(true);
		comboTimeDimensionType.setTriggerAction(TriggerAction.ALL);

		comboTimeDimensionTypeLabel = new FieldLabel(comboTimeDimensionType,
				msgs.comboTimeDimensionTypeLabel());

		// Save
		btnAddColumn = new TextButton(msgs.btnAddColumnText());
		btnAddColumn.setIcon(ResourceBundle.INSTANCE.columnAdd());
		btnAddColumn.setIconAlign(IconAlign.RIGHT);
		btnAddColumn.setToolTip(msgs.btnAddColumnToolTip());

		SelectHandler changeHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				save();

			}
		};
		btnAddColumn.addSelectHandler(changeHandler);

		//
		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(columnLabelFieldLabel, new VerticalLayoutData(1, -1, new Margins(
				1)));
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
		v.add(comboTimeDimensionTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(1)));

		v.add(defaultStringLabel, new VerticalLayoutData(1, -1, new Margins(1)));
		v.add(btnAddColumn, new VerticalLayoutData(-1, -1, new Margins(10, 0,
				10, 0)));
		add(v, new VerticalLayoutData(-1, -1, new Margins(0)));

		comboMeasureTypeLabel.setVisible(false);
		comboAttributeTypeLabel.setVisible(false);
		comboDimensionTypeLabel.setVisible(false);
		comboColumnReferenceTypeLabel.setVisible(false);
		comboTimeDimensionTypeLabel.setVisible(false);
		comboLocaleTypeLabel.setVisible(false);

		defaultStringLabel.setVisible(false);

	}

	protected void removeExpression() {
		exWrapper = null;
		defaultValueString.reset();
		defaultValueString.removeToolTip();
		btnRemoveExpression.setVisible(false);
		btnAddExpression.setVisible(true);
		forceLayout();
	}

	protected void openReplaceExpressionDialog() {
		ColumnMockUp columnMockUp = retrieveColumnMockUp();
		if (columnMockUp != null) {
			ReplaceExpressionDialog replaceExpressionDialog = new ReplaceExpressionDialog(
					columnMockUp, trId, eventBus);
			replaceExpressionDialog
					.addExpressionWrapperNotificationListener(new ExpressionWrapperNotificationListener() {

						@Override
						public void onExpression(
								ExpressionWrapperNotification expressionWrapperNotification) {
							exWrapper = expressionWrapperNotification
									.getExpressionWrapper();
							if (exWrapper.isReplaceByValue()) {
								defaultValueString.setValue(exWrapper
										.getReplaceValue());
								defaultValueString.setToolTip(exWrapper
										.getReplaceValue());
							} else {
								defaultValueString.setValue(exWrapper
										.getReplaceExpressionContainer()
										.getExp().getReadableExpression());
								defaultValueString.setToolTip(exWrapper
										.getReplaceExpressionContainer()
										.getExp().getReadableExpression());
							}
							btnAddExpression.setVisible(false);
							btnRemoveExpression.setVisible(true);
							forceLayout();

						}

						@Override
						public void failed(Throwable throwable) {
							Log.error("Error in AddColumn during expression creation:"
									+ throwable.getLocalizedMessage());

						}

						@Override
						public void aborted() {
							Log.debug("Expression creation aborted");

						}
					});

			replaceExpressionDialog.show();

		}
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

	protected void addHandlersForComboTimeDimensionType(
			final LabelProvider<PeriodDataType> labelProvider) {
		comboTimeDimensionType
				.addSelectionHandler(new SelectionHandler<PeriodDataType>() {
					public void onSelection(SelectionEvent<PeriodDataType> event) {
						Log.debug("ComboTimeDimensionType selected: "
								+ event.getSelectedItem());
						PeriodDataType timeDimensionType = event
								.getSelectedItem();
						updateTimeDimensionType(timeDimensionType);
					}

				});
	}

	protected void updateColumnType(ColumnTypeCode type) {
		Log.debug("Update ColumnTypeCode " + type.toString());
		switch (type) {
		case CODENAME:
			comboLocaleTypeLabel.setVisible(true);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(false);
			exWrapper = null;
			defaultValueString.reset();
			btnRemoveExpression.setVisible(false);
			btnAddExpression.setVisible(true);
			defaultValueString.removeToolTip();
			defaultStringLabel.setVisible(true);
			break;
		case ATTRIBUTE:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(true);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(false);
			comboAttributeType.reset();
			defaultStringLabel.setVisible(false);
			break;
		case DIMENSION:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(true);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(false);
			defaultStringLabel.setVisible(false);
			break;
		case MEASURE:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(true);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(false);
			comboMeasureType.reset();
			defaultStringLabel.setVisible(false);
			break;
		case TIMEDIMENSION:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(true);
			defaultStringLabel.setVisible(false);
			break;
		default:
			comboLocaleTypeLabel.setVisible(false);
			comboMeasureTypeLabel.setVisible(false);
			comboAttributeTypeLabel.setVisible(false);
			comboDimensionTypeLabel.setVisible(false);
			comboColumnReferenceTypeLabel.setVisible(false);
			comboTimeDimensionTypeLabel.setVisible(false);
			exWrapper = null;
			defaultValueString.reset();
			btnRemoveExpression.setVisible(false);
			btnAddExpression.setVisible(true);
			defaultValueString.removeToolTip();
			defaultStringLabel.setVisible(true);
			break;
		}

		forceLayout();
	}

	protected void updateMeasureType(ColumnDataType type) {
		Log.debug("Update ColumnTypeCode " + type);
		exWrapper = null;
		defaultValueString.reset();
		btnRemoveExpression.setVisible(false);
		btnAddExpression.setVisible(true);
		defaultValueString.removeToolTip();
		defaultStringLabel.setVisible(true);
		forceLayout();
	}

	protected void updateAttributeType(ColumnDataType type) {
		Log.debug("Update ColumnTypeCode " + type);
		exWrapper = null;
		defaultValueString.reset();
		btnRemoveExpression.setVisible(false);
		btnAddExpression.setVisible(true);
		defaultValueString.removeToolTip();
		defaultStringLabel.setVisible(true);
		forceLayout();

	}

	protected void updateLocaleType(LocaleTypeElement type) {

	}

	protected void updateTimeDimensionType(PeriodDataType type) {

	}

	public void update(TRId trId) {
		this.trId = trId;
		this.clear();
		create();
	}

	protected void save() {
		ColumnMockUp columnMockUp;

		String defaultV = defaultValueString.getCurrentValue();
		String labelS = columnLabelField.getCurrentValue();

		if (labelS != null && !labelS.isEmpty()) {
			ColumnTypeCodeElement columnTypeCodeElement = comboColumnTypeCode
					.getCurrentValue();
			if (columnTypeCodeElement != null) {
				ColumnTypeCode type = columnTypeCodeElement.getCode();
				ColumnDataTypeElement columnDataTypeElement;
				switch (type) {
				case MEASURE:
					columnDataTypeElement = comboMeasureType.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							if (defaultV == null || defaultV.isEmpty()) {
								columnMockUp = new ColumnMockUp(null, null,
										labelS, type, dataType, defaultV);
							} else {
								if (exWrapper.isReplaceByValue()) {
									columnMockUp = new ColumnMockUp(null, null,
											labelS, type, dataType,
											exWrapper.getReplaceValue());
								} else {
									columnMockUp = new ColumnMockUp(
											null,
											null,
											labelS,
											type,
											dataType,
											exWrapper
													.getReplaceExpressionContainer()
													.getExp());

								}
							}
							addColumnSession = new AddColumnSession(trId,
									columnMockUp);
							callAddColumm();
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
					columnDataTypeElement = comboAttributeType
							.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							if (dataType == ColumnDataType.Geometry) {
								// RegExp regExp = RegExp
								// .compile(GEOMETRY_REGEXPR);
								// MatchResult matcher = regExp.exec(defaultV);
								// boolean matchFound = matcher != null;
								// if (matchFound) {
								if (defaultV == null || defaultV.isEmpty()) {
									columnMockUp = new ColumnMockUp(null, null,
											labelS, type, dataType, defaultV);
								} else {
									if (exWrapper.isReplaceByValue()) {
										columnMockUp = new ColumnMockUp(null,
												null, labelS, type, dataType,
												exWrapper.getReplaceValue());
									} else {
										columnMockUp = new ColumnMockUp(
												null,
												null,
												labelS,
												type,
												dataType,
												exWrapper
														.getReplaceExpressionContainer()
														.getExp());

									}
								}

								addColumnSession = new AddColumnSession(trId,
										columnMockUp);
								callAddColumm();
								// } else {
								// UtilsGXT3
								// .alert(msgsCommon.attention(),
								// "The default value is not a valid text representation for geometry type ( e.g. POINT(34 56) or LINESTRING(65 34, 56.43 78.65)!");
								// }
							} else {
								if (dataType == ColumnDataType.Date) {
									if (defaultV == null || defaultV.isEmpty()) {
										columnMockUp = new ColumnMockUp(null,
												null, labelS, type, dataType,
												defaultV);
									} else {
										if (exWrapper.isReplaceByValue()) {
											columnMockUp = new ColumnMockUp(
													null, null, labelS, type,
													dataType,
													exWrapper.getReplaceValue());
										} else {
											columnMockUp = new ColumnMockUp(
													null,
													null,
													labelS,
													type,
													dataType,
													exWrapper
															.getReplaceExpressionContainer()
															.getExp());

										}
									}
									addColumnSession = new AddColumnSession(
											trId, columnMockUp);

									callAddColumm();

								} else {
									if (defaultV == null || defaultV.isEmpty()) {
										columnMockUp = new ColumnMockUp(null,
												null, labelS, type, dataType,
												defaultV);
									} else {
										if (exWrapper.isReplaceByValue()) {
											columnMockUp = new ColumnMockUp(
													null, null, labelS, type,
													dataType,
													exWrapper.getReplaceValue());
										} else {
											columnMockUp = new ColumnMockUp(
													null,
													null,
													labelS,
													type,
													dataType,
													exWrapper
															.getReplaceExpressionContainer()
															.getExp());

										}
									}
									addColumnSession = new AddColumnSession(
											trId, columnMockUp);
									callAddColumm();
								}

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
				case CODEDESCRIPTION:
				case ANNOTATION:
					if (defaultV == null || defaultV.isEmpty()) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, defaultV);
					} else {
						if (exWrapper.isReplaceByValue()) {
							columnMockUp = new ColumnMockUp(null, null, labelS,
									type, exWrapper.getReplaceValue());
						} else {
							columnMockUp = new ColumnMockUp(null, null, labelS,
									type, exWrapper
											.getReplaceExpressionContainer()
											.getExp());

						}
					}
					addColumnSession = new AddColumnSession(trId, columnMockUp);
					callAddColumm();
					break;
				case CODENAME:
					LocaleTypeElement locale = comboLocaleType
							.getCurrentValue();
					if (locale != null) {
						if (defaultV == null || defaultV.isEmpty()) {
							columnMockUp = new ColumnMockUp(null, null, labelS,
									type, locale.getLocaleName(), defaultV);
						} else {
							if (exWrapper.isReplaceByValue()) {
								columnMockUp = new ColumnMockUp(null, null,
										labelS, type, locale.getLocaleName(),
										exWrapper.getReplaceValue());
							} else {
								columnMockUp = new ColumnMockUp(
										null,
										null,
										labelS,
										type,
										locale.getLocaleName(),
										exWrapper
												.getReplaceExpressionContainer()
												.getExp());

							}
						}
						addColumnSession = new AddColumnSession(trId,
								columnMockUp);
						callAddColumm();
					} else {
						UtilsGXT3.alert(msgsCommon.attention(), msgs.noLocaleSelected());
					}
					break;

				case DIMENSION:
					ColumnData columnReference = comboColumnReferenceType
							.getCurrentValue();
					if (columnReference != null) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, columnReference, defaultV);
						addColumnSession = new AddColumnSession(trId,
								columnMockUp);
						callAddColumm();
					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.noColumnReferenceSelected());
					}
					break;
				case TIMEDIMENSION:
					PeriodDataType periodDataType = comboTimeDimensionType
							.getCurrentValue();
					if (periodDataType != null) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, periodDataType, defaultV);
						addColumnSession = new AddColumnSession(trId,
								columnMockUp);
						callAddColumm();

					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.timeDimensionTypeNotSelected());
					}
					break;
				default:
					UtilsGXT3.alert(msgsCommon.attention(),
							msgs.thisColumnTypeIsNotSupported());
					break;
				}
			} else {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAColumnType());
			}
		} else {
			UtilsGXT3.alert("Attntion", msgs.insertAValidLabel());
		}

	}

	protected ColumnMockUp retrieveColumnMockUp() {
		ColumnMockUp columnMockUp = null;

		String labelS = columnLabelField.getCurrentValue();

		if (labelS != null && !labelS.isEmpty()) {
			ColumnTypeCodeElement columnTypeCodeElement = comboColumnTypeCode
					.getCurrentValue();
			if (columnTypeCodeElement != null) {
				ColumnTypeCode type = columnTypeCodeElement.getCode();
				ColumnDataTypeElement columnDataTypeElement;
				switch (type) {
				case MEASURE:
					columnDataTypeElement = comboMeasureType.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							columnMockUp = new ColumnMockUp(null, null, labelS,
									type, dataType, "");
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
					columnDataTypeElement = comboAttributeType
							.getCurrentValue();
					if (columnDataTypeElement != null) {
						ColumnDataType dataType = columnDataTypeElement
								.getType();
						if (dataType != null) {
							columnMockUp = new ColumnMockUp(null, null, labelS,
									type, dataType, "");

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
				case CODEDESCRIPTION:
				case ANNOTATION:
					columnMockUp = new ColumnMockUp(null, null, labelS, type,
							"");
					break;

				case CODENAME:
					LocaleTypeElement locale = comboLocaleType
							.getCurrentValue();
					if (locale != null) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, locale.getLocaleName(), "");

					} else {
						UtilsGXT3.alert(msgsCommon.attention(), msgs.noLocaleSelected());
					}
					break;
				case DIMENSION:
					ColumnData columnReference = comboColumnReferenceType
							.getCurrentValue();
					if (columnReference != null) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, columnReference, "");

					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.noColumnReferenceSelected());
					}
					break;
				case TIMEDIMENSION:
					PeriodDataType periodDataType = comboTimeDimensionType
							.getCurrentValue();
					if (periodDataType != null) {
						columnMockUp = new ColumnMockUp(null, null, labelS,
								type, periodDataType, "");

					} else {
						UtilsGXT3.alert(msgsCommon.attention(),
								msgs.timeDimensionTypeNotSelected());
					}
					break;
				default:
					UtilsGXT3.alert(msgsCommon.attention(),
							msgs.thisColumnTypeIsNotSupported());
					break;
				}
			} else {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAColumnType());
			}
		} else {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.insertAValidLabel());
		}
		return columnMockUp;

	}

	private void callAddColumm() {
		Log.debug(addColumnSession.toString());
		ExpressionServiceAsync.INSTANCE.startAddColumn(addColumnSession,
				new AsyncCallback<String>() {
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
									Log.debug("Add Column Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(
											msgsCommon.error(),
											msgs.errorInInvocationOfAddColumnOperation());
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
		comboDimensionType.setValue(tabResource);
		retrieveColumnData(tabResource);
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

	protected void retrieveColumnData(TabResource tabResource) {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(
				tabResource.getTrId(),
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
									UtilsGXT3.alert(
											msgs.errorRetrievingColumnsHead(),
											msgs.errorRetrievingColumns());
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

	protected void retrieveLocales() {
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
									UtilsGXT3.alert(
											msgs.errorRetrievingLocales(),
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
									UtilsGXT3.alert(msgs
											.errorRetrievingPeriodTypeHead(),
											caught.getLocalizedMessage());
								}
							}
						}

					}

					@Override
					public void onSuccess(ArrayList<PeriodDataType> result) {
						storeComboTimeDimensionType.clear();
						storeComboTimeDimensionType.addAll(result);
						storeComboTimeDimensionType.commitChanges();

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
				ChangeTableRequestType.ADDCOLUMN, operationResult.getTrId(),
				why);
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
				ChangeTableRequestType.ADDCOLUMN, operationResult.getTrId(),
				why);
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
