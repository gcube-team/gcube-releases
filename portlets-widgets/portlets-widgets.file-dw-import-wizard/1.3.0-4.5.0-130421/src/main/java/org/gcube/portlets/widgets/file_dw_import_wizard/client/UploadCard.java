/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;



import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.Source;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;



public class UploadCard extends WizardCard {
	protected HashMap<String, Component> sourcesPanels = new HashMap<String, Component>();

	protected CardLayout layout;
	protected ImportSession session;

	public UploadCard(ImportSession session, ArrayList<Source> sources) {
		// FIXME step message calculated
		super("Upload", "Step 3 of 4");

		this.session = session;
		layout = new CardLayout();
		ContentPanel uploadPanel = new ContentPanel(layout);
		uploadPanel.setHeaderVisible(false);

		for (Source source:sources) {
			Component sourcePanel = source.getPanel(this, session);
			sourcesPanels.put(source.getId(), sourcePanel);
			uploadPanel.add(sourcePanel);
		}
		
		
		
//		 sourcePanel = source.getPanel(this, session);
//		uploadPanel.add(sourcePanel);

		setContent(uploadPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {
//		Log.trace("Selected source: " + session.getSource());

		Component sourcePanel = sourcesPanels.get(session.getSource().getId());
		

		layout.setActiveItem(sourcePanel);
		setEnableNextButton(false);
	}

}
