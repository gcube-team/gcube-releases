/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;


import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.form.Radio;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXAgencyTypeCard extends WizardCard {

	protected final SDMXExportSession exportSession;

	protected Agencies agency;

	public SDMXAgencyTypeCard(final SDMXExportSession exportSession) {
		super("SDMX agency type", "");

		this.exportSession = exportSession;

		agency = new Agencies();
		//agency.setNewAgency(false);
		exportSession.setAgency(agency);

		VerticalPanel selectionPanel = new VerticalPanel();
		selectionPanel.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());

		Radio radioSDMXAgencyPresent = new Radio();

		radioSDMXAgencyPresent
				.setBoxLabel("<p style='display:inline-table;'><b>Agencies Present</b><br>Select from the agencies already present in the registry</p>");
		radioSDMXAgencyPresent.setValue(true);
		radioSDMXAgencyPresent.setName("present");
		radioSDMXAgencyPresent.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());

		Radio radioSDMXAgencyNew = new Radio();
		radioSDMXAgencyNew
				.setBoxLabel("<p style='display:inline-table;'><b>New Agencies</b><br>Creates a new agency in the registry</p>");
		radioSDMXAgencyNew.setName("new");
		radioSDMXAgencyNew.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());

		selectionPanel.add(radioSDMXAgencyPresent);
		selectionPanel.add(radioSDMXAgencyNew);

		// we can set name on radios or use toggle group
		ToggleGroup toggle = new ToggleGroup();
		toggle.add(radioSDMXAgencyPresent);
		toggle.add(radioSDMXAgencyNew);

		toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

		
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Agency type: " + radio.getName());
					if (radio.getName().compareTo("present") == 0) {
						//exportSession.getAgency().setNewAgency(false);
					} else {
						if (radio.getName().compareTo("new") == 0) {
							//exportSession.getAgency().setNewAgency(true);
						} else {

						}

					}
				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange "
							+ e.getLocalizedMessage());
				}

			}
		});

		setContent(selectionPanel);

	}

	@Override
	public void setup() {
		/*Log.info("Agency New: "+exportSession.getAgency().isNewAgency());
		if (exportSession.getAgency().isNewAgency()) {
		} else {
			Command sayNextCard = new Command() {
				public void execute() {
					try {
						SDMXAgenciesSelectionCard sdmxAgenciesSelectionCard = new SDMXAgenciesSelectionCard(
								exportSession);
						getWizardWindow().addCard(sdmxAgenciesSelectionCard);
						Log.info("NextCard SDMXAgenciesSelectionCard");
						getWizardWindow().nextCard();
					} catch (Exception e) {
						Log.error("sayNextCard :" + e.getLocalizedMessage());
					}
				}
			};

			getWizardWindow().setNextButtonCommand(sayNextCard);

		}*/
	}

}
