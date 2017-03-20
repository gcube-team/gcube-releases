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
public class MeasureColumnSelectionCard extends WizardCard {

	private MeasureColumnSelectionCard thisCard;
	private SDMXExportSession exportSession;
	private MeasureColumnSelectionPanel measureColumnSelectionPanel;
	//private Agencies agency;

	public MeasureColumnSelectionCard(final SDMXExportSession exportSession) {
		super("Measure column selection", "");

		this.exportSession = exportSession;
		thisCard = this;

		this.measureColumnSelectionPanel = new MeasureColumnSelectionPanel(thisCard,res, exportSession);

		measureColumnSelectionPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						exportSession.setObsValueColumn(measureColumnSelectionPanel
								.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);

					}

				});

		setContent(measureColumnSelectionPanel);

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
					Log.info("Remove measureColumnSelectionCard");
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
