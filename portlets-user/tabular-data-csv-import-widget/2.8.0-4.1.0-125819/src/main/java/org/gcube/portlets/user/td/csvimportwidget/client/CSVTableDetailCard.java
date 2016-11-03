/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.csvimportwidget.client.licence.LicenceDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.container.MarginData;
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
public class CSVTableDetailCard extends WizardCard {
	
	private static final String TABLEDETAILPANELWIDTH = "100%";
	private static final String TABLEDETAILPANELHEIGHT = "100%";
	private static CSVImportWizardTDMessages msgs= GWT.create(CSVImportWizardTDMessages.class);
	private CommonMessages msgsCommon;
	
	private CSVImportSession importSession;
	private CSVTableDetailCard thisCard;

	private VerticalLayoutContainer p;
	private VerticalPanel tableDetailPanel;

	private TextField fieldName;
	private TextArea txtAreaDescription;
	private TextArea txtAreaRights;
	private DateField fieldValidFrom;
	private DateField fieldValidUntilTo;

	private TabResource detail;

	private ListLoader<ListLoadConfig, ListLoadResult<LicenceData>> loader;
	private ComboBox<LicenceData> comboLicences;
	

	public CSVTableDetailCard(final CSVImportSession importSession) {
		super(msgs.tabularResourceDetail(), "");
		this.importSession = importSession;
		thisCard = this;
		initMessages();
		
		tableDetailPanel = new VerticalPanel();

		tableDetailPanel.setSpacing(4);
		tableDetailPanel.setWidth(TABLEDETAILPANELWIDTH);
		tableDetailPanel.setHeight(TABLEDETAILPANELHEIGHT);

		FramedPanel form = new FramedPanel();
		form.setHeadingText(msgs.csvTableDetailCardFormHeader());
		

		FieldSet fieldSetInformation = new FieldSet();
		fieldSetInformation.setHeadingText(msgs.fieldSetInformationHead());
		fieldSetInformation.setCollapsible(false);

		form.add(fieldSetInformation, new MarginData(new Margins(0)));

		p = new VerticalLayoutContainer();
		fieldSetInformation.add(p, new MarginData(new Margins(0)));

		fieldName = new TextField();
		fieldName.setAllowBlank(false);
		fieldName.setEmptyText(msgs.fieldNameEmptyText());
		fieldName.setValue(importSession.getLocalFileName());
		p.add(new FieldLabel(fieldName, msgs.fieldNameLabel()), new VerticalLayoutData(1, -1));

		txtAreaDescription = new TextArea();
		txtAreaDescription.setAllowBlank(false);
		txtAreaDescription.setEmptyText(msgs.txtAreaDescriptionEmptyText());
		txtAreaDescription.setValue("CSV");
		p.add(new FieldLabel(txtAreaDescription, msgs.txtAreaDescriptionLabel()),
				new VerticalLayoutData(1, -1));

		txtAreaRights = new TextArea();
		txtAreaRights.setEmptyText(msgs.txtAreaRightsEmptyText());
		txtAreaRights.setAllowBlank(false);
		p.add(new FieldLabel(txtAreaRights, msgs.txtAreaRightsLabel()), new VerticalLayoutData(1, -1));

		fieldValidFrom = new DateField();
		fieldValidFrom.setValue(new Date());
		p.add(new FieldLabel(fieldValidFrom, msgs.fieldValidFromLabel()), new VerticalLayoutData(
				1, -1));

		fieldValidUntilTo = new DateField();
		p.add(new FieldLabel(fieldValidUntilTo, msgs.fieldValidUntilToLabel()),
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
		p.add(new FieldLabel(comboLicences, msgs.comboLicencesLabel()), new VerticalLayoutData(
				1, -1));

		tableDetailPanel.add(form);

		setCenterWidget(tableDetailPanel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
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
							showErrorAndHide(msgsCommon.error(),
									msgs.errorRetrievingLicences(),
									caught.getLocalizedMessage(), caught);
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
					Log.info("Remove CSVTableDetailCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(true);

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

		if (fieldName.getValue() == null || fieldName.getValue().isEmpty()
				|| !fieldName.isValid()) {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.fillInNameField());
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (txtAreaDescription.getValue() == null
					|| txtAreaDescription.getValue().isEmpty()
					|| !txtAreaDescription.isValid()) {
				d = new AlertMessageBox(msgsCommon.attention(),
						msgs.fillInDescriptionField());
				d.addHideHandler(hideHandler);
				d.show();
			} else {
				if (txtAreaRights.getValue() == null || txtAreaRights.getValue().isEmpty()
						|| !txtAreaRights.isValid()) {
					d = new AlertMessageBox(msgsCommon.attention(),
							msgs.fillInRightsField());
					d.addHideHandler(hideHandler);
					d.show();
				} else {
					fieldName.setReadOnly(true);
					txtAreaDescription.setReadOnly(true);
					txtAreaRights.setReadOnly(true);
					goNext();
				}
			}
		}
	}

	protected void goNext() {
		try {
			detail = new TabResource();
			detail.setName(fieldName.getCurrentValue());
			detail.setDescription(txtAreaDescription.getCurrentValue());
			detail.setRight(txtAreaRights.getCurrentValue());
			detail.setValidFrom(fieldValidFrom.getCurrentValue());
			detail.setValidUntilTo(fieldValidUntilTo.getCurrentValue());

			if (fieldValidFrom.getCurrentValue() != null
					&& fieldValidUntilTo.getCurrentValue() != null
					&& fieldValidFrom.getCurrentValue().compareTo(
							fieldValidUntilTo.getCurrentValue()) > 0) {
				Log.debug("Attention Valid From field is higher than Valid Until To field");
				AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
						msgs.validFromFieldIsHigherThanValidUntilToField());
				d.addHideHandler(new HideHandler() {

					public void onHide(HideEvent event) {
						getWizardWindow().setEnableNextButton(true);
						getWizardWindow().setEnableBackButton(false);

					}
				});
				d.show();
				return;
			}

			if (comboLicences.getCurrentValue() != null
					&& comboLicences.getCurrentValue().getLicenceId() != null
					&& !comboLicences.getCurrentValue().getLicenceId()
							.isEmpty()) {
				detail.setLicence(comboLicences.getCurrentValue()
						.getLicenceId());
			}

			importSession.setTabResource(detail);

			CSVOperationInProgressCard csvOperationInProgressCard = new CSVOperationInProgressCard(
					importSession);
			getWizardWindow().addCard(csvOperationInProgressCard);
			Log.info("NextCard CSVOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
