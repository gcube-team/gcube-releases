/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;


import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXCodelistSelectionCard extends WizardCard {
	
	private SDMXCodelistSelectionCard thisCard;
	@SuppressWarnings("unused")
	private SDMXExportSession exportSession;
	private CodelistSelectionPanel codelistSelectionPanel;
	@SuppressWarnings("unused")
	private Codelist codelist;
	
	public SDMXCodelistSelectionCard(final SDMXExportSession exportSession) {
		super("SDMX Codelist selection", "");
		
		this.exportSession = exportSession;
		thisCard=this;
		
		this.codelistSelectionPanel=new CodelistSelectionPanel(thisCard,res);
		
		codelistSelectionPanel.addSelectionHandler(new SelectionHandler<Codelist>() {

			
			public void onSelection(SelectionEvent<Codelist> event) {
				//exportSession.setSelectedCodelist(codelistSelectionPanel.getSelectedItem());
				getWizardWindow().setEnableNextButton(true);
			}
		    	
			
		});
		
		setContent(codelistSelectionPanel);
		
	}
	
	
	@Override
	public void setup(){
		Command sayNextCard = new Command() {

			public void execute() {
				/*SDMXTableDetailCard sdmxTableDetailCard = new SDMXTableDetailCard(
						exportSession);
				getWizardWindow()
						.addCard(sdmxTableDetailCard);
				Log.info("NextCard SDMXTableDetailCard");
				getWizardWindow().nextCard();
				*/
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
	}

}
