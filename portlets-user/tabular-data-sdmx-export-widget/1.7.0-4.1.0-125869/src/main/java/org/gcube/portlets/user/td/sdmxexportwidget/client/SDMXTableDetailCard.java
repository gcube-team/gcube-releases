/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXTableDetailCard extends WizardCard {

	protected final String TABLEDETAILPANELWIDTH = "100%";
	protected final String TABLEDETAILPANELHEIGHT = "100%";
	protected final String FORMWIDTH = "538px";

	protected SDMXExportSession exportSession;
	protected SDMXTableDetailCard thisCard;

	protected static final AgenciesProperties agenciesProperties = GWT
			.create(AgenciesProperties.class);
	protected VerticalLayoutContainer p = new VerticalLayoutContainer();
	protected VerticalPanel tableDetailPanel;

	protected TextField id;
	protected TextField agencyName;
	protected TextField registryBaseUrl;
	protected TextField version;

	protected TextButton checkButton;

	public SDMXTableDetailCard(final SDMXExportSession exportSession) {
		super("SDMX Table Detail", "");

		this.exportSession = exportSession;
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

		fieldSet.add(p);

		id = new TextField();
		id.setAllowBlank(false);
		id.setEmptyText("Enter Id...");
		id.setValue(exportSession.getTabResource().getName());
		p.add(new FieldLabel(id, "Id"), new VerticalLayoutData(1, -1));

		agencyName = new TextField();
		agencyName.setVisible(true);
		agencyName.setEmptyText("Enter Agency...");
		agencyName.setValue(exportSession.getAgency().getName());
		FieldLabel agencyNameLabel = new FieldLabel(agencyName, "Agency");
		p.add(agencyNameLabel, new VerticalLayoutData(1, -1));

		registryBaseUrl = new TextField();
		registryBaseUrl.setVisible(false);
		registryBaseUrl.setEmptyText("Enter Registry URL...");
		String urlRegistry = ((SDMXRegistrySource) exportSession.getSource())
				.getUrl();
		if (urlRegistry == null || urlRegistry.isEmpty()) {
			registryBaseUrl.setValue(null);
		} else {
			registryBaseUrl.setValue(urlRegistry);
		}
		FieldLabel registryBaseUrlLabel = new FieldLabel(registryBaseUrl,
				"Registry URL");
		registryBaseUrlLabel.setVisible(false);
		p.add(registryBaseUrlLabel, new VerticalLayoutData(1, -1));

		version = new TextField();
		version.setAllowBlank(false);
		version.setEmptyText("Enter Version...");
		version.setValue("");
		p.add(new FieldLabel(version, "Version"), new VerticalLayoutData(1, -1));

		tableDetailPanel.add(form);
		setContent(tableDetailPanel);

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

		if (id.getValue() == null || id.getValue().isEmpty() || !id.isValid()
				|| version.getValue() == null || version.getValue().isEmpty()
				|| !version.isValid() || agencyName.getValue() == null
				|| agencyName.getValue().isEmpty() || !agencyName.isValid()) {

			d = new AlertMessageBox("Attention!", "Fill in all fields");
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (!version.getValue().matches("[0-9]+\\.[0-9]+")) {
				d = new AlertMessageBox("Attention!", "Version must match the regular expression [0-9]+\\.[0-9]+");
				d.addHideHandler(hideHandler);
				d.show();
			} else {
				id.setReadOnly(true);
				registryBaseUrl.setReadOnly(true);
				version.setReadOnly(true);
				agencyName.setReadOnly(true);
				goNext();
			}
		}

	}

	protected void goNext() {
		try {
			exportSession.setId(id.getCurrentValue());
			exportSession.setAgencyName(agencyName.getCurrentValue());
			exportSession.setVersion(version.getCurrentValue());
			exportSession.setRegistryBaseUrl(registryBaseUrl.getCurrentValue());
			SDMXOperationInProgressCard sdmxOperationInProgressCard = new SDMXOperationInProgressCard(
					exportSession);
			getWizardWindow().addCard(sdmxOperationInProgressCard);
			Log.info("NextCard SDMXOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

}
