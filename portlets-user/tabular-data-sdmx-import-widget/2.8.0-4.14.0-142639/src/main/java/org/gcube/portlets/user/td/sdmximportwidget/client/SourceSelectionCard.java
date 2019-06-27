/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;


import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.FileSource;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.source.WorkspaceSource;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
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
public class SourceSelectionCard extends WizardCard {
	
	protected final SDMXImportSession importSession;
	
	final SDMXRegistrySource sdmxRegistrySource= SDMXRegistrySource.INSTANCE;
	final FileSource fileSource= FileSource.INSTANCE;
    final WorkspaceSource workspaceSource=WorkspaceSource.INSTANCE;
	
   
	public SourceSelectionCard(final SDMXImportSession importSession) {
		super("SDMX source selection", "");
		
		this.importSession = importSession;
		//Default
		importSession.setSource(sdmxRegistrySource);	
		
		VerticalPanel sourceSelectionPanel = new VerticalPanel();
		sourceSelectionPanel.setStylePrimaryName(res.wizardCSS().getImportSelectionSources());
		
		Radio radioSDMXRegistrySource = new Radio();
	    
		radioSDMXRegistrySource.setBoxLabel("<p style='display:inline-table;'><b>"+sdmxRegistrySource.getName()+"</b><br>"+sdmxRegistrySource.getDescription()+"</p>");
	    radioSDMXRegistrySource.setValue(true);
	    radioSDMXRegistrySource.setName(sdmxRegistrySource.getName());
	    radioSDMXRegistrySource.setStylePrimaryName(res.wizardCSS().getImportSelectionSource());
		 
		Radio radioWorkspaceSource = new Radio();
		radioWorkspaceSource.setBoxLabel("<p style='display:inline-table;'><b>"+workspaceSource.getName()+"</b><br>"+workspaceSource.getDescription()+"</p>");
		radioWorkspaceSource.setName(workspaceSource.getName());
		radioWorkspaceSource.setStylePrimaryName(res.wizardCSS().getImportSelectionSource());
		radioWorkspaceSource.disable();
		
	    Radio radioFileSource = new Radio();
		radioFileSource.setBoxLabel("<p style='display:inline-table;'><b>"+fileSource.getName()+"</b><br>"+fileSource.getDescription()+"</p>");
		radioFileSource.setName(fileSource.getName());
		radioFileSource.setStylePrimaryName(res.wizardCSS().getImportSelectionSource());
		radioFileSource.disable();
		
	    sourceSelectionPanel.add(radioSDMXRegistrySource);
	    sourceSelectionPanel.add(radioWorkspaceSource);
	    sourceSelectionPanel.add(radioFileSource);
	    
	    
	    // we can set name on radios or use toggle group
	    ToggleGroup toggle = new ToggleGroup();
	    toggle.add(radioSDMXRegistrySource);
	    toggle.add(radioWorkspaceSource);
	    toggle.add(radioFileSource);
	    	    
	    toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {
			
		
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Source Selected:" + radio.getName());
					if (radio.getName().compareTo(sdmxRegistrySource.getName()) == 0) {
						importSession.setSource(sdmxRegistrySource);
					} else {
						if (radio.getName().compareTo(workspaceSource.getName()) == 0) {
							importSession.setSource(workspaceSource);
						} else {
							if (radio.getName().compareTo(fileSource.getName()) == 0) {
								importSession.setSource(fileSource);
							} else {

							}

						}

					}
				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange " + e.getLocalizedMessage());
				}
				
			}
		});
				
		
		setContent(sourceSelectionPanel);
		
	}
	
	@Override
	public void setup(){
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					String sourceId = importSession.getSource().getId();
					if (sourceId == null || sourceId.isEmpty()) {
						Log.error("SDMX Import Source Id: " + sourceId);
					} else {
						if (sourceId.compareTo("SDMXRegistry") == 0) {
							SDMXRegistrySelectionCard sdmxRegistrySelectionCard = new SDMXRegistrySelectionCard(
									importSession);
							getWizardWindow()
									.addCard(sdmxRegistrySelectionCard);
							Log.info("NextCard SDMXRegistrySelectionCard");
							getWizardWindow().nextCard();
						} else {
							if (sourceId.compareTo("File") == 0) {

							} else {
								if (sourceId.compareTo("Workspace") == 0) {

								} else {

								}
							}
						}

					}
				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
				}
			}
	    };
	
	    getWizardWindow().setNextButtonCommand(sayNextCard);
	   
	}	
	
}
