/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class SDMXDatasetSelectionCard extends WizardCard {
	
	private SDMXDatasetSelectionCard thisCard;
	@SuppressWarnings("unused")
	private SDMXExportSession exportSession;
	private DatasetSelectionPanel datasetSelectionPanel;

	public SDMXDatasetSelectionCard(final SDMXExportSession exportSession) {
		super("SDMX Dataset selection", "");
		
		this.exportSession = exportSession;
		
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
