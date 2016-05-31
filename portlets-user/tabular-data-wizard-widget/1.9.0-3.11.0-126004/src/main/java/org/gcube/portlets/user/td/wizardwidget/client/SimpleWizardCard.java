/**
 * 
 */
package org.gcube.portlets.user.td.wizardwidget.client;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
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
