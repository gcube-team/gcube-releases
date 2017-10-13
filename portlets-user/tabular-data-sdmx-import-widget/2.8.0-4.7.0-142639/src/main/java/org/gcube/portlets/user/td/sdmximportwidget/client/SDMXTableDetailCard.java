/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.sdmximportwidget.client.licence.LicenceDataPropertiesCombo;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadConfigBean;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXTableDetailCard extends WizardCard {
	protected DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	protected final String TABLEDETAILPANELWIDTH = "100%";
	protected final String TABLEDETAILPANELHEIGHT = "100%";
	protected final String FORMWIDTH = "538px";

	protected SDMXImportSession importSession;
	protected SDMXTableDetailCard thisCard;

	protected static final AgenciesProperties agenciesProperties = GWT
			.create(AgenciesProperties.class);
	protected VerticalLayoutContainer p = new VerticalLayoutContainer();
	protected VerticalPanel tableDetailPanel;

	protected TextField name;
	protected TextArea description;
	protected TextArea rights;
	protected TextField agencyName;
	protected DateField validFrom;
	protected DateField validUntilTo;

	protected TextButton checkButton;

	protected ListLoader<ListLoadConfig, ListLoadResult<LicenceData>> loader;
	protected ComboBox<LicenceData> comboLicences;

	TabResource detail;

	public SDMXTableDetailCard(final SDMXImportSession importSession) {
		super("SDMX Table Detail", "");

		this.importSession = importSession;
		thisCard = this;

		tableDetailPanel = new VerticalPanel();

		tableDetailPanel.setSpacing(4);
		tableDetailPanel.setWidth(TABLEDETAILPANELWIDTH);
		tableDetailPanel.setHeight(TABLEDETAILPANELHEIGHT);

		FramedPanel form = new FramedPanel();
		form.setHeadingText("Details");
		form.setWidth(FORMWIDTH);

		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingText("Information");
		fieldSet.setCollapsible(false);
		form.add(fieldSet);

		// VerticalLayoutContainer p = new VerticalLayoutContainer();
		fieldSet.add(p);

		name = new TextField();
		name.setAllowBlank(false);
		name.setEmptyText("Enter a name...");
		name.setValue(importSession.getSelectedCodelist().getName());
		p.add(new FieldLabel(name, "Name"), new VerticalLayoutData(1, -1));

		description = new TextArea();
		description.setAllowBlank(false);
		description.setEmptyText("Enter a description...");
		description.setValue(importSession.getSelectedCodelist()
				.getDescription());
		p.add(new FieldLabel(description, "Description"),
				new VerticalLayoutData(1, -1));

		rights = new TextArea();
		rights.setEmptyText("Enter rights...");
		rights.setAllowBlank(false);
		p.add(new FieldLabel(rights, "Rights"), new VerticalLayoutData(1, -1));

		agencyName = new TextField();
		agencyName.setVisible(true);
		agencyName.setEmptyText("Enter Agency...");
		agencyName.setValue(importSession.getSelectedCodelist().getAgencyId());
		FieldLabel agencyNameLabel = new FieldLabel(agencyName, "Agency");
		agencyNameLabel.setLabelSeparator("");
		p.add(agencyNameLabel, new VerticalLayoutData(1, -1));

		validFrom = new DateField();
		validFrom.setValue(new Date());
		p.add(new FieldLabel(validFrom, "Valid From"), new VerticalLayoutData(
				1, -1));

		validUntilTo = new DateField();
		p.add(new FieldLabel(validUntilTo, "Valid Until To"),
				new VerticalLayoutData(1, -1));

		// Combo Licences
		LicenceDataPropertiesCombo propsLicenceData = GWT
				.create(LicenceDataPropertiesCombo.class);
		ListStore<LicenceData> storeCombo = new ListStore<LicenceData>(
				propsLicenceData.id());

		Log.debug("StoreCombo created");

		RpcProxy<ListLoadConfig, ListLoadResult<LicenceData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<LicenceData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<LicenceData>> callback) {
				loadData(loadConfig, callback);
			}
		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<LicenceData>>(
				proxy) {
			@Override
			protected ListLoadConfig newLoadConfig() {
				return (ListLoadConfig) new ListLoadConfigBean();
			}

		};

		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, LicenceData, ListLoadResult<LicenceData>>(
				storeCombo));
		Log.trace("LoaderCombo created");

		comboLicences = new ComboBox<LicenceData>(storeCombo,
				propsLicenceData.licenceName()) {

			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load();
					}
				});
			}
		};
		comboLicences.setEditable(false);
		comboLicences.setTypeAhead(false);
		comboLicences.setTriggerAction(TriggerAction.ALL);
		Log.trace("Combo Licence created");

		// /
		p.add(new FieldLabel(comboLicences, "Licence"), new VerticalLayoutData(
				1, -1));

		tableDetailPanel.add(form);

		setContent(tableDetailPanel);

	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<LicenceData>> callback) {
		TDGWTServiceAsync.INSTANCE
				.getLicences(new AsyncCallback<ArrayList<LicenceData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("load combo failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving licences.");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<LicenceData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						callback.onSuccess(new ListLoadResultBean<LicenceData>(
								result));

					}

				});

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXTableDetailCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		setBackButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		getWizardWindow().setEnableBackButton(true);

	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		if (name.getValue() == null || name.getValue().isEmpty()
				|| !name.isValid()) {
			d = new AlertMessageBox("Attention!", "Fill in name field");
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (description.getValue() == null
					|| description.getValue().isEmpty()
					|| !description.isValid()) {
				d = new AlertMessageBox("Attention!",
						"Fill in description field");
				d.addHideHandler(hideHandler);
				d.show();
			} else {
				if (rights.getValue() == null || rights.getValue().isEmpty()
						|| !rights.isValid()) {
					d = new AlertMessageBox("Attention!",
							"Fill in rights field");
					d.addHideHandler(hideHandler);
					d.show();
				} else {
					if (agencyName.getValue() == null
							|| agencyName.getValue().isEmpty()
							|| !agencyName.isValid()) {

						d = new AlertMessageBox("Attention!",
								"Fill in agency name field");
						d.addHideHandler(hideHandler);
						d.show();

					} else {
						name.setReadOnly(true);
						description.setReadOnly(true);
						rights.setReadOnly(true);
						agencyName.setReadOnly(true);
						goNext();
					}
				}
			}
		}

	}

	protected void goNext() {
		try {
			detail = new TabResource();
			detail.setName(name.getCurrentValue());
			detail.setAgency(agencyName.getCurrentValue());
			detail.setDescription(description.getCurrentValue());
			detail.setRight(rights.getCurrentValue());

			detail.setValidFrom(validFrom.getCurrentValue());
			detail.setValidUntilTo(validUntilTo.getCurrentValue());

			if (validFrom.getCurrentValue() != null
					&& validUntilTo.getCurrentValue() != null
					&& validFrom.getCurrentValue().compareTo(
							validUntilTo.getCurrentValue()) > 0) {
				Log.debug("Attention Valid From field is higher than Valid Until To field");
				AlertMessageBox d = new AlertMessageBox("Attention!",
						"Valid From field is higher than Valid Until To field");
				d.addHideHandler(new HideHandler() {

					public void onHide(HideEvent event) {
						getWizardWindow().setEnableNextButton(true);
						getWizardWindow().setEnableBackButton(false);

					}
				});
				d.show();
			}

			if (comboLicences.getCurrentValue() != null
					&& comboLicences.getCurrentValue().getLicenceId() != null
					&& !comboLicences.getCurrentValue().getLicenceId()
							.isEmpty()) {
				detail.setLicence(comboLicences.getCurrentValue()
						.getLicenceId());
			}

			importSession.setTabResource(detail);
			SDMXOperationInProgressCard sdmxOperationInProgressCard = new SDMXOperationInProgressCard(
					importSession);
			getWizardWindow().addCard(sdmxOperationInProgressCard);
			Log.info("NextCard SDMXOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

}
