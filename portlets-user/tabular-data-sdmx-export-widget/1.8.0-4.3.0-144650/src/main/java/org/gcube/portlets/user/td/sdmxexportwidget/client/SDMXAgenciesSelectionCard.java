/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SDMXAgenciesSelectionCard extends WizardCard {

	private SDMXAgenciesSelectionCard thisCard;
	private SDMXExportSession exportSession;
	private AgenciesSelectionPanel agenciesSelectionPanel;

	// private Agencies agency;

	public SDMXAgenciesSelectionCard(final SDMXExportSession exportSession) {
		super("SDMX Agencies selection", "");

		this.exportSession = exportSession;
		thisCard = this;

		this.agenciesSelectionPanel = new AgenciesSelectionPanel(thisCard, res);

		agenciesSelectionPanel
				.addSelectionHandler(new SelectionHandler<Agencies>() {

					public void onSelection(SelectionEvent<Agencies> event) {
						exportSession.setAgency(agenciesSelectionPanel
								.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);

					}

				});

		setContent(agenciesSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				switch (exportSession.getExportType()) {
				case CODELIST:
					SDMXTableDetailCard sdmxTableDetailCard = new SDMXTableDetailCard(
							exportSession);
					getWizardWindow().addCard(sdmxTableDetailCard);
					Log.info("NextCard SDMXTableDetailCard");
					getWizardWindow().nextCard();
					break;
				case DATASET:
					MeasureColumnSelectionCard measureColumnSelectionCard = new MeasureColumnSelectionCard(
							exportSession);
					getWizardWindow().addCard(measureColumnSelectionCard);
					Log.info("NextCard MeasureColumnSelectionCard");
					getWizardWindow().nextCard();
					break;
				case GENERIC:
				default:
					Log.error("Only the tabular resources with type Codelist and Dataset"
							+ " are exportable in SDMX!");
					break;

				}

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXAgenciesSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		// getWizardWindow().setEnableNextButton(false);

		setEnableNextButton(false);

		setBackButtonVisible(true);
		setEnableBackButton(true);

	}

}
