/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;

import com.allen_sauer.gwt.log.client.Log;
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
	
	protected CSVImportSession importSession;

	public SourceSelectionCard(final CSVImportSession importSession, ArrayList<CSVSource> sources) {
		super("CSV source selection", "Step 1 of 4");
		
		this.importSession = importSession;
		
		VerticalPanel sourceSelectionPanel = new VerticalPanel();
		sourceSelectionPanel.setSpacing(4);
		sourceSelectionPanel.setWidth("100%");
		sourceSelectionPanel.setHeight("100%");
		
		boolean first = true;
		
		for (CSVSource source:sources) {
			HorizontalPanel sourcePanel = getCSVSourcePanel(source, first);
			sourceSelectionPanel.add(sourcePanel);
			
			if (first) {
				importSession.setSource(source);
				first = false;
			}
		}
		
		setContent(sourceSelectionPanel);
		
	}
	
	protected HorizontalPanel getCSVSourcePanel(final CSVSource source, boolean select)
	{
		HorizontalPanel sourcePanel = new HorizontalPanel();
		sourcePanel.setSpacing(2);
		final SimpleRadioButton sourceRadioButton = new SimpleRadioButton("source");
		sourceRadioButton.setValue(select);
		sourceRadioButton.addClickHandler(new ClickHandler() {
			
			
			public void onClick(ClickEvent event) {
				Log.trace("Selection changed "+source.getId()+" "+sourceRadioButton.getValue());
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
