/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXDatasetSelectionCard extends WizardCard {
	
	protected SDMXDatasetSelectionCard thisCard;
	protected SDMXImportSession importSession;
	protected DatasetSelectionPanel datasetSelectionPanel;

	public SDMXDatasetSelectionCard(final SDMXImportSession importSession) {
		super("SDMX Dataset selection", "");
		
		this.importSession = importSession;
		
		thisCard=this;
		
		this.datasetSelectionPanel=new DatasetSelectionPanel(thisCard,res);
		
		setContent(datasetSelectionPanel);
		
	}
	
	
	@Override
	public void setup(){
		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXDatasetSelectionCard");
					
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
	    };
	   
	    getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
	}

}
