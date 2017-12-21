/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class UploadCard extends WizardCard {

	protected HashMap<String, Component> sourcesPanels = new HashMap<String, Component>();
	protected CardLayout layout;
	protected CSVImportSession session;
	
	public UploadCard(CSVImportSession session, ArrayList<CSVSource> sources) {
		//FIXME step message calculated
		super("CSV Upload", "Step 2 of 4");

		this.session = session;
		layout = new CardLayout();
		ContentPanel uploadPanel = new ContentPanel(layout);
		uploadPanel.setHeaderVisible(false);
		
		for (CSVSource source:sources) {
			Component sourcePanel = source.getPanel(this, session);
			sourcesPanels.put(source.getId(), sourcePanel);
			uploadPanel.add(sourcePanel);
		}
		
		setContent(uploadPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {
		Log.trace("Selected source: "+session.getSource());
		
		Component sourcePanel = sourcesPanels.get(session.getSource().getId());
		layout.setActiveItem(sourcePanel);
		setEnableNextButton(false);
	}
		

}
