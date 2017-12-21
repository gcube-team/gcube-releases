/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;
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
public class SDMXTemplateExportMeasureColumnSelectionCard extends WizardCard {

	private SDMXTemplateExportMeasureColumnSelectionCard thisCard;
	private SDMXTemplateExportSession sdmxTemplateExportSession;
	private SDMXTemplateExportMeasureColumnSelectionPanel measureColumnSelectionPanel;

	public SDMXTemplateExportMeasureColumnSelectionCard(
			final SDMXTemplateExportSession sdmxTemplateExportSession) {
		super("Measure column selection", "");

		this.sdmxTemplateExportSession = sdmxTemplateExportSession;
		thisCard = this;

		this.measureColumnSelectionPanel = new SDMXTemplateExportMeasureColumnSelectionPanel(
				thisCard, res, sdmxTemplateExportSession);

		measureColumnSelectionPanel
				.addSelectionHandler(new SelectionHandler<TemplateColumnData>() {

					public void onSelection(
							SelectionEvent<TemplateColumnData> event) {
						if (measureColumnSelectionPanel.getSelectedItem() == null) {
							sdmxTemplateExportSession.setObsValueColumn(null);
							getWizardWindow().setEnableNextButton(false);

						} else {
							sdmxTemplateExportSession
									.setObsValueColumn(measureColumnSelectionPanel
											.getSelectedItem());
							getWizardWindow().setEnableNextButton(true);
						}
					}

				});

		setContent(measureColumnSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				SDMXTemplateExportTableDetailCard sdmxTableDetailCard = new SDMXTemplateExportTableDetailCard(
						sdmxTemplateExportSession);
				getWizardWindow().addCard(sdmxTableDetailCard);
				Log.info("NextCard SDMXTemplateExportTableDetailCard");
				getWizardWindow().nextCard();

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove measureColumnSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		// getWizardWindow().setEnableNextButton(false);

		setBackButtonVisible(true);
		setEnableBackButton(true);

		if (measureColumnSelectionPanel == null
				|| measureColumnSelectionPanel.getSelectedItem() == null) {
			setEnableNextButton(false);
		} else {
			setEnableNextButton(true);
		}

	}

}
