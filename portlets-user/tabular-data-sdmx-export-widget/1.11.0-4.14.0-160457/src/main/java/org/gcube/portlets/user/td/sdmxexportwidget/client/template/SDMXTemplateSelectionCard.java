/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
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
public class SDMXTemplateSelectionCard extends WizardCard {

	private SDMXTemplateSelectionCard thisCard;
	private SDMXTemplateExportSession sdmxTemplateExportSession;
	private SDMXTmplateExportTemplateSelectionPanel sdmxTemplateExportTemplateSelectionPanel;
	
	public SDMXTemplateSelectionCard(
			final SDMXTemplateExportSession sdmxTemplateExportSession) {
		super("SDMX Template selection", "");

		this.sdmxTemplateExportSession = sdmxTemplateExportSession;
		thisCard = this;

		this.sdmxTemplateExportTemplateSelectionPanel = new SDMXTmplateExportTemplateSelectionPanel(thisCard, res);

		sdmxTemplateExportTemplateSelectionPanel
				.addSelectionHandler(new SelectionHandler<TemplateData>() {

					public void onSelection(SelectionEvent<TemplateData> event) {
						if (sdmxTemplateExportTemplateSelectionPanel.getSelectedItem() != null) {
							sdmxTemplateExportSession
									.setTemplateData(sdmxTemplateExportTemplateSelectionPanel
											.getSelectedItem());
							getWizardWindow().setEnableNextButton(true);
						} else {
							sdmxTemplateExportSession
									.setTemplateData(null);
							getWizardWindow().setEnableNextButton(false);
						}
					}

				});

		setContent(sdmxTemplateExportTemplateSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				SDMXTemplateExportMeasureColumnSelectionCard sdmxTemplateExportMeasureColumnSelectionCard = new SDMXTemplateExportMeasureColumnSelectionCard(
						sdmxTemplateExportSession);
				getWizardWindow().addCard(sdmxTemplateExportMeasureColumnSelectionCard);
				Log.info("NextCard SDMXTemplateExportMeasureColumnSelectionCard");
				getWizardWindow().nextCard();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXTemplateExportTemplateSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		
		
		if (sdmxTemplateExportTemplateSelectionPanel == null
				|| sdmxTemplateExportTemplateSelectionPanel.getSelectedItem() == null) {
			setEnableNextButton(false);
		} else {
			setEnableNextButton(true);
		}
		
		
	}

}
