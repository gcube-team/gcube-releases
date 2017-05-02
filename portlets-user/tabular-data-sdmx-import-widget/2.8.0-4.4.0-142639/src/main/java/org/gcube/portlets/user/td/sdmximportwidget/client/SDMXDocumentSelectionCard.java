/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.document.CodelistDocument;
import org.gcube.portlets.user.td.gwtservice.shared.document.DatasetDocument;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
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
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXDocumentSelectionCard extends WizardCard {
	
	protected final SDMXImportSession importSession;
	
	final CodelistDocument codelist= CodelistDocument.INSTANCE;
    final DatasetDocument dataset= DatasetDocument.INSTANCE;
	
	public SDMXDocumentSelectionCard(final SDMXImportSession importSession) {
		super("SDMX document selection", "");
		
		this.importSession = importSession;
		//Default
		importSession.setSDMXDocument(codelist);
		
		VerticalPanel documentSelectionPanel = new VerticalPanel();
		documentSelectionPanel.setStylePrimaryName(res.wizardCSS().getImportSelectionSources());
		
		Radio radioCodelist = new Radio();
	    
		radioCodelist.setBoxLabel("<p style='display:inline-table;'><b>"+codelist.getName()+"</b><br>"+codelist.getDescription()+"</p>");
	    radioCodelist.setValue(true);
	    radioCodelist.setName(codelist.getName());
	    radioCodelist.setStylePrimaryName(res.wizardCSS().getImportSelectionSource());
		 
	    
	    Radio radioDataSet = new Radio();
		radioDataSet.setBoxLabel("<p style='display:inline-table;'><b>"+dataset.getName()+"</b><br>"+dataset.getDescription()+"</p>");
		radioDataSet.setName(dataset.getName());
		radioDataSet.setStylePrimaryName(res.wizardCSS().getImportSelectionSource());
		radioDataSet.disable();
		
	    documentSelectionPanel.add(radioCodelist);
	    documentSelectionPanel.add(radioDataSet);
	 
	    
	    // we can set name on radios or use toggle group
	    ToggleGroup toggle = new ToggleGroup();
	    toggle.add(radioCodelist);
	    toggle.add(radioDataSet);
	    
	    toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {
			
			
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Document Selected:" + radio.getName());
					if (radio.getName().compareTo(codelist.getName()) == 0) {
						importSession.setSDMXDocument(codelist);
					} else {
						if (radio.getName().compareTo(dataset.getName()) == 0) {
							importSession.setSDMXDocument(dataset);
						} else {

						}

					}
				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange " + e.getLocalizedMessage());
				}
				
			}
		});
				
	
		
		setContent(documentSelectionPanel);
		
	}
	
	
	

}
