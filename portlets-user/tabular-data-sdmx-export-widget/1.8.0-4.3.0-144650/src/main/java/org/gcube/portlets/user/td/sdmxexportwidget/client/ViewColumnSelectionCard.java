/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
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
public class ViewColumnSelectionCard extends WizardCard {

	private ViewColumnSelectionCard thisCard;
	private SDMXExportSession exportSession;
	private ViewColumnSelectionPanel viewColumnSelectionPanel;
	//private Agencies agency;

	public ViewColumnSelectionCard(final SDMXExportSession exportSession) {
		super("Codelist column selection", "");

		this.exportSession = exportSession;
		thisCard = this;

		this.viewColumnSelectionPanel = new ViewColumnSelectionPanel(thisCard,res, exportSession);

		viewColumnSelectionPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						exportSession.setObsValueColumn(viewColumnSelectionPanel
								.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);

					}

				});

		setContent(viewColumnSelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				SDMXTableDetailCard sdmxTableDetailCard = new SDMXTableDetailCard(
						exportSession);
				getWizardWindow().addCard(sdmxTableDetailCard);
				Log.info("NextCard SDMXTableDetailCard");
				getWizardWindow().nextCard();

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove viewColumnSelectionCard");
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
