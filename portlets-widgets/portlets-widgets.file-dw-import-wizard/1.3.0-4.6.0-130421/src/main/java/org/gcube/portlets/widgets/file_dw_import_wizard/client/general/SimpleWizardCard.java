/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.general;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * A simple wizard card.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class SimpleWizardCard extends WizardCard {

	/**
	 * Create a new simple wizard card.
	 * @param title the card title.
	 * @param footer the card footer.
	 * @param content the card content.
	 */
	public SimpleWizardCard(String title, String footer, String content) {
		super(title, footer);
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		HTML htmlContent = new HTML(content);
		htmlContent.setStyleName("wizard-simple-content");
		contentPanel.add(htmlContent);
		
		setContent(contentPanel);
	}

}
