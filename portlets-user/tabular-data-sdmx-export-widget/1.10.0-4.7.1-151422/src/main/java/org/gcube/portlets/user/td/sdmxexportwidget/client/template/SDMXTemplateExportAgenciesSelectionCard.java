/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SDMXTemplateExportAgenciesSelectionCard extends WizardCard {

	private SDMXTemplateExportAgenciesSelectionCard thisCard;
	private SDMXTemplateExportSession sdmxTemplateExportSession;
	private SDMXTemplateExportAgenciesSelectionPanel agenciesSelectionPanel;

	// private Agencies agency;

	public SDMXTemplateExportAgenciesSelectionCard(
			final SDMXTemplateExportSession sdmxTemplateExportSession) {
		super("SDMX Agencies selection", "");

		this.sdmxTemplateExportSession = sdmxTemplateExportSession;
		thisCard = this;

		this.agenciesSelectionPanel = new SDMXTemplateExportAgenciesSelectionPanel(
				thisCard, res);

		agenciesSelectionPanel
				.addSelectionHandler(new SelectionHandler<Agencies>() {

					public void onSelection(SelectionEvent<Agencies> event) {
						if (agenciesSelectionPanel.getSelectedItem() == null) {
							sdmxTemplateExportSession.setAgency(null);
							getWizardWindow().setEnableNextButton(false);
						} else {
							sdmxTemplateExportSession
									.setAgency(agenciesSelectionPanel
											.getSelectedItem());
							getWizardWindow().setEnableNextButton(true);
						}
					}

				});

		setContent(agenciesSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				SDMXTemplateSelectionCard sdmxTemplateSelectionCard = new SDMXTemplateSelectionCard(
						sdmxTemplateExportSession);
				getWizardWindow().addCard(sdmxTemplateSelectionCard);
				Log.info("NextCard SDMXTemplateExportTemplateSelectionCard");
				getWizardWindow().nextCard();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXTemplateExportAgenciesSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		// getWizardWindow().setEnableNextButton(false);

		

		setBackButtonVisible(true);
		setEnableBackButton(true);

		if (agenciesSelectionPanel == null
				|| agenciesSelectionPanel.getSelectedItem() == null) {
			setEnableNextButton(false);
		} else {
			setEnableNextButton(true);
		}

	}

}
