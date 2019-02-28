/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.client.wizard;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SimpleWizardCard extends WizardCard {

	/**
	 * Create a new simple wizard card.
	 * 
	 * @param title
	 *            the card title.
	 * @param footer
	 *            the card footer.
	 * @param content
	 *            the card content.
	 */
	public SimpleWizardCard(String title, String footer, String content) {
		super(title, footer);

		SimplePanel contentPanel = new SimplePanel();
		HTML htmlContent = new HTML(content);
		htmlContent.setStyleName("wizard-simple-content");
		contentPanel.add(htmlContent);

		setContent(contentPanel);
	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				try {
					GWT.log("NextCard");
					getWizardWindow().nextCard();
				} catch (Exception e) {
					GWT.log("sayNextCard :" + e.getLocalizedMessage());
				}
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					GWT.log("PreviousCard");
					getWizardWindow().previousCard();
				} catch (Exception e) {
					GWT.log("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(true);
		setBackButtonVisible(true);
		setEnableNextButton(true);
		setNextButtonVisible(true);
	}

}
