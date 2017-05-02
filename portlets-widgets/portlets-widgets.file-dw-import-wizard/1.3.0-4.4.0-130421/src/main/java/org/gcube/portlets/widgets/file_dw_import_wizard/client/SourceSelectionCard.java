/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.Source;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimpleRadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class SourceSelectionCard extends WizardCard {
	
	protected ImportSession importSession;

	public SourceSelectionCard(final ImportSession importSession, ArrayList<Source> sources) {
		super("Source selection", "Step 2 of 4");
		
		this.importSession = importSession;
		
		VerticalPanel sourceSelectionPanel = new VerticalPanel();
		sourceSelectionPanel.setSpacing(4);
		sourceSelectionPanel.setWidth("100%");
		sourceSelectionPanel.setHeight("100%");
		
		boolean first = true;
		
		for (Source source:sources) {
			HorizontalPanel sourcePanel = getSourcePanel(source, first);
			sourceSelectionPanel.add(sourcePanel);
			
			if (first) {
				importSession.setSource(source);
				first = false;
			}
		}
		
		setContent(sourceSelectionPanel);
		
	}
	
	protected HorizontalPanel getSourcePanel(final Source source, boolean select)
	{
		HorizontalPanel sourcePanel = new HorizontalPanel();
		sourcePanel.setSpacing(2);
		final SimpleRadioButton sourceRadioButton = new SimpleRadioButton("source");
		sourceRadioButton.setValue(select);
		sourceRadioButton.addClickHandler(new ClickHandler() {
			
			
			public void onClick(ClickEvent event) {
				if (sourceRadioButton.getValue()) importSession.setSource(source);
			}
			
		});
		
		sourcePanel.add(sourceRadioButton);
		HTML sourceDescription = new HTML();
		StringBuilder description = new StringBuilder();
		description.append("<b>");
		description.append(source.getName());
		description.append("</b><br><p>");
		description.append(source.getDescription());
		description.append("</p>");
		sourceDescription.setHTML(description.toString());
		sourcePanel.add(sourceDescription);
		
		return sourcePanel;
	}

}
