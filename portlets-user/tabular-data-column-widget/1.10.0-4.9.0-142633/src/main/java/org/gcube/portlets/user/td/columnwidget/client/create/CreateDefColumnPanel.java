package org.gcube.portlets.user.td.columnwidget.client.create;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnTypeCodeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.properties.LocaleTypeProperties;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeStore;
import org.gcube.portlets.user.td.expressionwidget.client.store.LocaleTypeElement;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
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
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * This Panel is used only with Codelist,
 * so only with CODE,CODENAME,CODEDESCRIPTION,ANNOTATION columns
 * 
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class CreateDefColumnPanel extends FramedPanel {
	private static final String WIDTH = "448px";
	private static final String HEIGHT = "180px";
	private static final String FIELDWIDTH = "436px";
	private static final String FIELDSHEIGHT = "130px";

	private ArrayList<CreateDefColumnListener> listeners;

	private EventBus eventBus;
	private CreateDefColumnDialog parent;
	private TableType tableType;

	private ComboBox<ColumnTypeCodeElement> comboColumnTypeCode = null;
	private FieldLabel comboColumnTypeCodeLabel;

	private ComboBox<LocaleTypeElement> comboLocaleType = null;
	private FieldLabel comboLocaleTypeLabel;
	private ListStore<LocaleTypeElement> storeComboLocaleType;

	private TextButton btnSave;
	private TextButton btnClose;
	private SimpleContainer form;
	private VerticalLayoutContainer formLayout;
	private TextField columnLabel;
	private TextField defaultValue;
	private CreateDefColumnMessages msgs;
	private CommonMessages msgsCommon;

	/**
	 * 
	 * @param parent
	 * @param eventBus
	 */
	public CreateDefColumnPanel(CreateDefColumnDialog parent,
			TableType tableType, EventBus eventBus) {
		super();
		Log.debug("CreateDefColumnPanel[parent: " + parent + ", tableType: "
				+ tableType);
		this.parent = parent;
		this.eventBus = eventBus;
		this.tableType = tableType;
		initMessages();
		initListeners();
		init();
		create();
	}
	
	protected void initMessages(){
		msgs = GWT.create(CreateDefColumnMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void initListeners() {
		listeners = new ArrayList<CreateDefColumnListener>();
	}

	protected void create() {
		// Label
		columnLabel = new TextField();
		FieldLabel columnLabelLabel = new FieldLabel(columnLabel, msgs.columnLabel());

		// Default Value
		defaultValue = new TextField();
		FieldLabel defaultValueLabel = new FieldLabel(defaultValue,
				msgs.defaultValueLabel());

		// comboColumnTypeCode
		ColumnTypeCodeProperties propsColumnTypeCode = GWT
				.create(ColumnTypeCodeProperties.class);
		ListStore<ColumnTypeCodeElement> storeComboTypeCode = new ListStore<ColumnTypeCodeElement>(
				propsColumnTypeCode.id());

		switch (tableType) {
		case CODELIST:
			storeComboTypeCode.addAll(ColumnTypeCodeStore
					.getColumnTypeCodesForCodelist());
			break;
		case DATASET:
			storeComboTypeCode.addAll(ColumnTypeCodeStore
					.getColumnTypeCodesForDataset());
			break;
		case GENERIC:
			storeComboTypeCode.addAll(ColumnTypeCodeStore
					.getColumnTypeCodesForGeneric());
			break;
		default:
			break;

		}

		comboColumnTypeCode = new ComboBox<ColumnTypeCodeElement>(
				storeComboTypeCode, propsColumnTypeCode.label());
		Log.trace("ComboColumnTypeCode created");

		addHandlersForComboColumnTypeCode(propsColumnTypeCode.label());

		comboColumnTypeCode.setEmptyText(msgs.comboColumnTypeCodeEmptyText());
		comboColumnTypeCode.setWidth(191);
		comboColumnTypeCode.setEditable(false);
		comboColumnTypeCode.setTypeAhead(false);
		comboColumnTypeCode.setTriggerAction(TriggerAction.ALL);

		comboColumnTypeCodeLabel = new FieldLabel(comboColumnTypeCode,
				msgs.comboColumnTypeCodeLabel());

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

		//
		form = new SimpleContainer();
		form.setWidth(FIELDWIDTH);
		form.setHeight(FIELDSHEIGHT);

		formLayout = new VerticalLayoutContainer();
		formLayout.setScrollMode(ScrollMode.AUTO);

		formLayout
				.add(columnLabelLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		formLayout.add(comboColumnTypeCodeLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		formLayout.add(comboLocaleTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		formLayout.add(defaultValueLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));

		form.add(formLayout);

		btnSave = new TextButton(msgs.btnSaveText());
		btnSave.setIcon(ResourceBundle.INSTANCE.save());
		btnSave.setIconAlign(IconAlign.RIGHT);
		btnSave.setToolTip(msgs.btnSaveToolTip());
		btnSave.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Save");
				btnSave.disable();
				save();

			}
		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		BoxLayoutData boxLayoutData = new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnSave, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer vPanel = new VerticalLayoutContainer();
		vPanel.add(form, new VerticalLayoutData(1, -1));
		vPanel.add(flowButton, new VerticalLayoutData(1, -1, new Margins(1)));
		add(vPanel);

		comboLocaleTypeLabel.setVisible(false);

	}

	protected void save() {
		ColumnMockUp defNewColumn;
		ColumnTypeCode currentType = null;
		String localeName = null;

		String lab = columnLabel.getCurrentValue();
		ColumnTypeCodeElement typeElement = comboColumnTypeCode
				.getCurrentValue();
		LocaleTypeElement localeElement = comboLocaleType.getCurrentValue();
		String valueDefault = defaultValue.getCurrentValue();

		Log.debug("CheckValue: label: " + lab + ", type: " + typeElement
				+ ", locale: " + localeElement + ", default: " + valueDefault);
		if (lab == null || lab.isEmpty()) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.addALabel());
			btnSave.enable();
			return;
		}

		if (typeElement == null) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.noTypeSelected());
			btnSave.enable();
			return;
		} else {
			if (typeElement.getCode() == null) {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.noTypeSelected());
				btnSave.enable();
				return;
			} else {
				currentType = typeElement.getCode();
			}
		}

		switch (currentType) {
		case ATTRIBUTE:
			return;
		case CODE:
		case CODEDESCRIPTION:
		case ANNOTATION:
			defNewColumn = new ColumnMockUp(null, null, lab, currentType,
					valueDefault);

			break;
		case CODENAME:
			if (localeElement == null) {
				UtilsGXT3.alert(msgsCommon.attention(), msgs.noLocaleSelected());
				btnSave.enable();
				return;
			} else {
				if (localeElement.getLocaleName() == null
						|| localeElement.getLocaleName().isEmpty()) {
					UtilsGXT3.alert(msgsCommon.attention(), msgs.noLocaleSelected());
					btnSave.enable();
					return;
				} else {
					localeName = localeElement.getLocaleName();
					defNewColumn = new ColumnMockUp(null, null, lab,
							currentType, localeName, valueDefault);
				}
			}
			break;
		case DIMENSION:
			return;
		case MEASURE:
			return;
		case TIMEDIMENSION:
			return;
		default:
			return;

		}
		Log.debug("DefNewColumn:" + defNewColumn);
		fireCompleted(defNewColumn);

	}

	public void close() {
		if (parent != null) {
			parent.close();
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

	protected void updateColumnType(ColumnTypeCode type) {
		Log.debug("Update ColumnTypeCode " + type.toString());
		switch (type) {
		case CODENAME:
			comboLocaleTypeLabel.setVisible(true);
			forceLayout();
			break;
		default:
			comboLocaleTypeLabel.setVisible(false);
			forceLayout();
			break;
		}
	}

	protected void updateLocaleType(LocaleTypeElement type) {

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
									UtilsGXT3.alert(msgs.errorRetrievingLocaleHead(),
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

					}
				});

	}

	public void addListener(CreateDefColumnListener listener) {
		Log.debug("Add Listener:" + listener);
		listeners.add(listener);
	}

	public void removeListener(CreateDefColumnListener listener) {
		Log.debug("Remove Listener:" + listener);
		listeners.remove(listener);
	}

	public void fireCompleted(ColumnMockUp defNewColumn) {
		for (CreateDefColumnListener listener : listeners)
			listener.completedDefColumnCreation(defNewColumn);
		close();
	}

	public void fireAborted() {
		for (CreateDefColumnListener listener : listeners)
			listener.abortedDefColumnCreation();
		close();
	}

	public void fireFailed(String reason, String details) {
		for (CreateDefColumnListener listener : listeners)
			listener.failedDefColumnCreation(reason, details);
		close();
	}

}
