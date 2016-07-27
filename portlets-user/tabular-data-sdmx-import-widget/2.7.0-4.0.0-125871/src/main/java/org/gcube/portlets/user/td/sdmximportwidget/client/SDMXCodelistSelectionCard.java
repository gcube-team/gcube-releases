/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SDMXCodelistSelectionCard extends WizardCard {

	protected SDMXCodelistSelectionCard thisCard;
	protected SDMXImportSession importSession;
	protected CodelistSelectionPanel codelistSelectionPanel;
	protected Codelist selectedCodelist = null;

	public SDMXCodelistSelectionCard(final SDMXImportSession importSession) {
		super("SDMX Codelist selection", "");

		this.importSession = importSession;
		thisCard = this;

		this.codelistSelectionPanel = new CodelistSelectionPanel(thisCard, res);

		codelistSelectionPanel
				.addSelectionHandler(new SelectionHandler<Codelist>() {

					public void onSelection(SelectionEvent<Codelist> event) {
						importSession
								.setSelectedCodelist(codelistSelectionPanel
										.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);
					}

				});

		setContent(codelistSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				try {
					SDMXTableDetailCard sdmxTableDetailCard = new SDMXTableDetailCard(
							importSession);
					getWizardWindow().addCard(sdmxTableDetailCard);
					Log.info("NextCard SDMXTableDetailCard");
					getWizardWindow().nextCard();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXCodelistSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		setBackButtonVisible(false);
		codelistSelectionPanel.deselectAll();
	}

}
