package org.gcube.portlets.user.td.informationwidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.informationwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.informationwidget.client.licence.LicenceDataPropertiesCombo;
import org.gcube.portlets.user.td.informationwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.informationwidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.DataViewRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.DataViewRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabularResourceProperties extends FramedPanel {
	private static final String DESCRIPTION_FIELD_HEIGHT = "70px";
	private static final DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm");
	
	private TabularResourcePropertiesMessages msgs;
	private EventBus eventBus;
	private TRId trId;

	private TabResource tabResource;
	private TextField nameField;
	private TextArea descriptionField;
	private TextField agencyField;
	private TextField dateField;
	private TextArea rightField;
	private DateField validFromField;
	private DateField validUntilToField;
	private TextField ownerField;
	private CheckBox validField;
	private CheckBox finalizedField;

	private ComboBox<LicenceData> comboLicences;
	private ListStore<LicenceData> storeCombo;

	private TextField typeField;
	private TextField tableTypeNameField;

	private IconButton btnShare;
	private FieldLabel shareLabel;

	private VerticalLayoutContainer layoutTabularResource;

	private TextButton saveButton;
	// private TextButton validationsButton;
	// private TextButton resourcesButton;

	private ArrayList<LicenceData> licencesList;

	public TabularResourceProperties(String name, EventBus eventBus) {
		super();
		setId(name);
		this.eventBus = eventBus;
		this.msgs = GWT.create(TabularResourcePropertiesMessages.class);
		forceLayoutOnResize = true;

		retrieveLicencesList();

	}

	public void addTabularResource() {
		layoutTabularResource = new VerticalLayoutContainer();
		layoutTabularResource.setScrollMode(ScrollMode.AUTOY);
		layoutTabularResource.setAdjustForScroll(true);

		nameField = new TextField();
		nameField.setValue("");
		FieldLabel nameLabel = new FieldLabel(nameField, msgs.nameLabel());
		nameLabel.setToolTip(msgs.nameLabelToolTip());
		layoutTabularResource.add(nameLabel, new VerticalLayoutData(1, -1));

		descriptionField = new TextArea();
		descriptionField.setHeight(DESCRIPTION_FIELD_HEIGHT);
		descriptionField.setValue("");
		FieldLabel descriptionLabel = new FieldLabel(descriptionField,
				msgs.descriptionLabel());
		descriptionLabel.setToolTip(msgs.descriptionLabelToolTip());
		layoutTabularResource.add(descriptionLabel, new VerticalLayoutData(1,
				-1));

		typeField = new TextField();
		typeField.setReadOnly(true);
		typeField.setValue("");
		FieldLabel typeLabel = new FieldLabel(typeField, msgs.typeLabel());
		typeLabel.setToolTip(msgs.typeLabelToolTip());

		layoutTabularResource.add(typeLabel, new VerticalLayoutData(1, -1));

		agencyField = new TextField();
		agencyField.setValue("");
		FieldLabel agencyLabel = new FieldLabel(agencyField, msgs.agencyLabel());
		agencyLabel.setToolTip(msgs.agencyLabelToolTip());
		layoutTabularResource.add(agencyLabel, new VerticalLayoutData(1, -1));

		dateField = new TextField();
		dateField.setReadOnly(true);
		dateField.setValue("");
		FieldLabel dateLabel = new FieldLabel(dateField, msgs.dateLabel());
		dateLabel.setToolTip(msgs.dateLabelToolTip());
		layoutTabularResource.add(dateLabel, new VerticalLayoutData(1, -1));

		tableTypeNameField = new TextField();
		tableTypeNameField.setReadOnly(true);
		tableTypeNameField.setValue("");
		FieldLabel tableTypeNameLabel = new FieldLabel(tableTypeNameField,
				msgs.tableTypeNameLabel());
		tableTypeNameLabel.setToolTip(msgs.tableTypeNameLabelToolTip());
		layoutTabularResource.add(tableTypeNameLabel, new VerticalLayoutData(1,
				-1));

		rightField = new TextArea();
		rightField.setValue("");
		FieldLabel rightLabel = new FieldLabel(rightField, msgs.rightLabel());
		rightLabel.setToolTip(msgs.rightLabelToolTip());

		layoutTabularResource.add(rightLabel, new VerticalLayoutData(1, -1));

		validFromField = new DateField();
		FieldLabel validFromLabel = new FieldLabel(validFromField,
				msgs.validFromLabel());
		validFromLabel.setToolTip(msgs.validFromLabelToolTip());
		layoutTabularResource
				.add(validFromLabel, new VerticalLayoutData(1, -1));

		validUntilToField = new DateField();
		FieldLabel validUntilToLabel = new FieldLabel(validUntilToField,
				msgs.validUntilToLabel());
		validUntilToLabel.setToolTip(msgs.validUntilToLabelToolTip());
		layoutTabularResource.add(validUntilToLabel, new VerticalLayoutData(1,
				-1));

		// Combo Licences
		LicenceDataPropertiesCombo propsLicenceData = GWT
				.create(LicenceDataPropertiesCombo.class);
		storeCombo = new ListStore<LicenceData>(propsLicenceData.id());
		storeCombo.addAll(licencesList);

		comboLicences = new ComboBox<LicenceData>(storeCombo,
				propsLicenceData.licenceName());
		comboLicences.setMinListWidth(250);
		comboLicences.setEditable(false);
		comboLicences.setTypeAhead(false);
		comboLicences.setTriggerAction(TriggerAction.ALL);

		Log.trace("Combo Licence created");

		FieldLabel licencesLabel = new FieldLabel(comboLicences,
				msgs.licencesLabel());
		licencesLabel.setToolTip(msgs.licencesLabelToolTip());
		layoutTabularResource.add(licencesLabel, new VerticalLayoutData(1, -1));
		// /
		ownerField = new TextField();
		ownerField.setReadOnly(true);
		ownerField.setValue("");
		FieldLabel ownerLabel = new FieldLabel(ownerField, msgs.ownerLabel());
		ownerLabel.setToolTip(msgs.ownerLabelToolTip());
		layoutTabularResource.add(ownerLabel, new VerticalLayoutData(1, -1));

		btnShare = new IconButton();
		btnShare.setIcon(ResourceBundle.INSTANCE.share());
		btnShare.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnShare");
				eventBus.fireEvent(new RibbonEvent(RibbonType.SHARE));

			}
		});
		shareLabel = new FieldLabel(btnShare, msgs.shareLabel());
		shareLabel.setToolTip(msgs.shareLabelToolTip());
		shareLabel.setVisible(false);

		layoutTabularResource.add(shareLabel, new VerticalLayoutData(1, -1));

		validField = new CheckBox();
		validField.setValue(false);
		validField.setReadOnly(true);
		validField.setEnabled(false);
		FieldLabel validLabel = new FieldLabel(validField, msgs.validLabel());
		validLabel.setToolTip(msgs.validLabelToolTip());
		layoutTabularResource.add(validLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));

		finalizedField = new CheckBox();
		finalizedField.setValue(false);
		finalizedField.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					UtilsGXT3
							.info("Warning",
									"A TabularResource set to final can't be modified anymore!");
				}
			}
		});

		FieldLabel finalizedLabel = new FieldLabel(finalizedField,
				msgs.finalizedLabel());
		finalizedLabel.setToolTip(msgs.finalizedLabelToolTip());
		layoutTabularResource.add(finalizedLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));

		// Save Button
		saveButton = new TextButton(msgs.saveButton());
		saveButton.setIcon(ResourceBundle.INSTANCE.save());
		saveButton.setIconAlign(IconAlign.RIGHT);
		saveButton.setToolTip(msgs.saveButtonToolTip());
		SelectHandler saveHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onSave();

			}
		};
		saveButton.addSelectHandler(saveHandler);

		// Validation Button
		/*
		 * validationsButton = new TextButton(msgs.validationsButton());
		 * validationsButton.setIcon(ResourceBundle.INSTANCE.tableValidation());
		 * validationsButton.setIconAlign(IconAlign.RIGHT);
		 * validationsButton.setToolTip(msgs.validationsButtonToolTip());
		 * 
		 * SelectHandler validationsHandler = new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { openValidations();
		 * 
		 * } }; validationsButton.addSelectHandler(validationsHandler);
		 */

		// Resources Button
		/*
		 * resourcesButton = new TextButton(msgs.resourcesButton());
		 * resourcesButton.setIcon(ResourceBundle.INSTANCE.resources());
		 * resourcesButton.setIconAlign(IconAlign.RIGHT);
		 * resourcesButton.setToolTip(msgs.resourcesButtonToolTip());
		 * 
		 * SelectHandler resourcesHandler = new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { openResources();
		 * 
		 * } }; resourcesButton.addSelectHandler(resourcesHandler);
		 */

		//
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.setPack(BoxLayoutPack.START);
		hBox.add(saveButton, new BoxLayoutData(new Margins(2, 5, 2, 5)));
		// hBox.add(validationsButton, new BoxLayoutData(new Margins(2, 5, 2,
		// 5)));
		// hBox.add(resourcesButton, new BoxLayoutData(new Margins(2, 5, 2,
		// 5)));

		layoutTabularResource.add(hBox, new VerticalLayoutData(1, -1,
				new Margins(2)));

		add(layoutTabularResource);
	}

	protected void retrieveLicencesList() {
		TDGWTServiceAsync.INSTANCE
				.getLicences(new AsyncCallback<ArrayList<LicenceData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("load combo failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving licences.");
						}

					}

					public void onSuccess(ArrayList<LicenceData> result) {
						Log.trace("loaded " + result.size() + " LicenceData");
						licencesList = result;
						initInformation();
					}

				});

	}

	protected void updateTabularResource(TabResource tabResource) {
		this.tabResource = tabResource;
		Log.debug(tabResource.toString());
		nameField.setValue(tabResource.getName());
		descriptionField.setValue(tabResource.getDescription());
		typeField.setValue(tabResource.getTabResourceType());
		agencyField.setValue(tabResource.getAgency());
		try {
			dateField.setValue(sdf.format(tabResource.getDate()));
		} catch (Throwable e) {
			Log.error("Error parsing cration date: " + e.getLocalizedMessage());
		}

		tableTypeNameField.setValue(tabResource.getTableTypeName());
		rightField.setValue(tabResource.getRight());

		validFromField.clear();
		if (tabResource.getValidFrom() == null) {
			Log.debug("ValidFrom null or empty");
		} else {
			validFromField.setValue(tabResource.getValidFrom());
		}

		validUntilToField.clear();
		if (tabResource.getValidUntilTo() == null) {
			Log.debug("ValidUntilTo null or empty");
		} else {
			validUntilToField.setValue(tabResource.getValidUntilTo());

		}

		comboLicences.clear();
		if (tabResource.getLicence() != null
				&& !tabResource.getLicence().isEmpty()) {
			List<LicenceData> listLicence = storeCombo.getAll();
			boolean licenceFound = false;
			for (int i = 0; i < listLicence.size(); i++) {
				if (tabResource.getLicence().compareTo(
						listLicence.get(i).getLicenceId()) == 0) {
					comboLicences.setValue(listLicence.get(i));
					licenceFound = true;
					break;
				}
			}
			if (licenceFound) {
				Log.debug("Licence " + tabResource.getLicence() + " is found");
			} else {
				Log.debug("Licence " + tabResource.getLicence()
						+ " is not found");
			}
		} else {
			Log.debug("Licence is null");
		}

		ownerField.setValue(tabResource.getOwner().getLogin());
		validField.setValue(tabResource.isValid());
		if (tabResource.isFinalized()) {
			finalizedField.setReadOnly(true);
			finalizedField.setEnabled(false);
			finalizedField.setValue(true);
			saveButton.setEnabled(false);
		} else {
			finalizedField.setReadOnly(false);
			finalizedField.setEnabled(true);
			finalizedField.setValue(false);
			saveButton.setEnabled(true);
		}

		ArrayList<Contacts> contacts = tabResource.getContacts();
		if (contacts != null && contacts.size() > 0) {
			shareLabel.setVisible(true);
		} else {
			shareLabel.setVisible(false);
		}

	}

	/*
	 * protected void updateShareInfo(ShareInfo result) { ArrayList<Contacts>
	 * contacts = result.getContacts(); if (contacts != null && contacts.size()
	 * > 0) { shareLabel.setVisible(true); } else {
	 * shareLabel.setVisible(false); } }
	 */

	/*
	 * public void addTable() { tableFieldSet = new FieldSet();
	 * tableFieldSet.setHeadingText("Table");
	 * tableFieldSet.setCollapsible(true); tableFieldSet.setResize(true);
	 * 
	 * layoutTable = new VerticalLayoutContainer();
	 * 
	 * tableFieldSet.add(layoutTable);
	 * 
	 * tableTypeDefField = new TextField(); tableTypeDefField.setReadOnly(true);
	 * tableTypeDefField.setValue(""); layoutTable.add(new
	 * FieldLabel(tableTypeDefField, "Type"), new VerticalLayoutData(1, -1));
	 * 
	 * vl.add(tableFieldSet); }
	 */

	/*
	 * protected void openValidations() { Log.debug("Request Validations Tab");
	 * if (trId != null) { WidgetRequestEvent e = new WidgetRequestEvent(
	 * WidgetRequestType.VALIDATIONSTASKSPANEL); e.setTrId(trId);
	 * eventBus.fireEvent(e); } else { Log.error("TRId is null"); } }
	 */

	/*
	 * protected void openResources() { Log.debug("Request Resources Tab"); if
	 * (trId != null) { WidgetRequestEvent e = new WidgetRequestEvent(
	 * WidgetRequestType.RESOURCESPANEL); e.setTrId(trId);
	 * eventBus.fireEvent(e); } else { Log.error("TRId is null"); } }
	 */

	protected void updateTable(TableData tableData) {
		trId = tableData.getTrId();
		Log.debug("New :" + trId);
		forceLayout();
	}

	public void update() {
		TDGWTServiceAsync.INSTANCE
				.getTabResourceInformation(new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						updateTabularResource(result);
						getLastTable(result.getTrId());
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error retrienving properties: "
										+ caught.getLocalizedMessage());
							}

						}
					}

				});
	}

	/*
	 * protected void getShareInformation(TRId trId) {
	 * TDGWTServiceAsync.INSTANCE.getShareInfo(trId, new
	 * AsyncCallback<ShareInfo>() {
	 * 
	 * public void onSuccess(ShareInfo result) {
	 * Log.debug("Retrived share info:" + result); updateShareInfo(result);
	 * getLastTable(tabResource.getTrId());
	 * 
	 * }
	 * 
	 * public void onFailure(Throwable caught) { if (caught instanceof
	 * TDGWTSessionExpiredException) { eventBus.fireEvent(new
	 * SessionExpiredEvent( SessionExpiredType.EXPIREDONSERVER)); } else { if
	 * (caught instanceof TDGWTIsLockedException) {
	 * Log.error(caught.getLocalizedMessage()); UtilsGXT3.alert("Error Locked",
	 * caught.getLocalizedMessage()); } else {
	 * Log.error("Error retrienving Share Informations: " +
	 * caught.getLocalizedMessage()); UtilsGXT3.alert("Error",
	 * "Error retrienving Share Informations: " + caught.getLocalizedMessage());
	 * } } }
	 * 
	 * }); }
	 */

	protected void getLastTable(TRId trId) {
		TDGWTServiceAsync.INSTANCE.getLastTable(trId,
				new AsyncCallback<TableData>() {

					public void onSuccess(TableData result) {
						updateTable(result);

					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error retrienving Last Table: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Error retrienving Last Table: "
												+ caught.getLocalizedMessage());
							}
						}
					}

				});
	}

	public void initInformation() {
		addTabularResource();
		// addTable();
		update();
	}

	protected void onSave() {
		if (nameField.getValue() == null || nameField.getValue().isEmpty()) {
			Log.debug("Attention Fill name field");
			UtilsGXT3.info("Attention", "Fill name field");
			return;
		}

		tabResource.setName(nameField.getValue());
		tabResource.setDescription(descriptionField.getValue());
		tabResource.setAgency(agencyField.getValue());
		tabResource.setRight(rightField.getValue());

		Date vFrom = validFromField.getValue();
		if (vFrom == null) {
			Log.debug("No valid from set");
		} else {
			tabResource.setValidFrom(vFrom);
		}

		Date vUntilTo = validUntilToField.getValue();
		if (vUntilTo == null) {
			Log.debug("No valid until to set");
		} else {
			tabResource.setValidUntilTo(vUntilTo);
		}

		if (vFrom != null && vUntilTo != null && vFrom.compareTo(vUntilTo) > 0) {
			Log.debug("Attention Valid From field is higher than Valid Until To field");
			UtilsGXT3.info("Attention",
					"Valid From field is higher than Valid Until To field");
			return;
		}

		if (comboLicences.getCurrentValue() != null
				&& comboLicences.getCurrentValue().getLicenceId() != null
				&& !comboLicences.getCurrentValue().getLicenceId().isEmpty()) {
			tabResource.setLicence(comboLicences.getCurrentValue()
					.getLicenceId());
		}

		tabResource.setValid(validField.getValue());
		tabResource.setFinalized(finalizedField.getValue());

		TDGWTServiceAsync.INSTANCE.setTabResourceInformation(tabResource,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final",
											caught.getLocalizedMessage());
								} else {
									Log.error("Error Setting Tabular Resoruce Properties: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert("Error",
													"Error setting tabular resource properties: "
															+ caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(Void result) {
						Log.debug("Tabular Resource properties are set: "
								+ tabResource);
						UtilsGXT3.info("Proprerties",
								"Tabular Resource properties are set");
						if (finalizedField.getValue()) {
							finalizedField.setReadOnly(true);
							finalizedField.setValue(true);
							saveButton.setEnabled(false);
						} else {
							finalizedField.setReadOnly(false);
							finalizedField.setValue(false);
							saveButton.setEnabled(true);
						}
						DataViewRequestEvent dataViewRequestEvent = new DataViewRequestEvent();
						TabularResourceDataView tabularResouceDataView = new TabularResourceDataView(
								tabResource.getTrId(), tabResource.getName());
						dataViewRequestEvent
								.setDataView(tabularResouceDataView);
						dataViewRequestEvent
								.setDataViewRequestType(DataViewRequestType.UPDATE_TAB_NAME);
						eventBus.fireEvent(dataViewRequestEvent);
					}

				});

	}

}
