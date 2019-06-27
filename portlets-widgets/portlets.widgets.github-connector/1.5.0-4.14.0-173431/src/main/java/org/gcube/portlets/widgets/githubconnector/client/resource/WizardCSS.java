/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.client.resource;

import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface WizardCSS extends CssResource {

	@ClassName("wizard-card-title")
	public String getWizardCardTitle();

	@ClassName("wizard-card-footer")
	public String getWizardCardFooter();

	@ClassName("wizard-previous-button-text")
	public String getWizardPreviousButtonText();

	@ClassName("wizard-previous-button-icon")
	public String getWizardPreviousButtonIcon();

	@ClassName("wizard-next-button-text")
	public String getWizardNextButtonText();

	@ClassName("wizard-next-button-icon")
	public String getWizardNextButtonIcon();

	@ClassName("wizard-tool-button-text")
	public String getWizardToolButtonText();

	@ClassName("wizard-tool-button-icon")
	public String getWizardToolButtonIcon();

	@ClassName("card-panel")
	public String getCardPanel();

	@ClassName("card-panel-content")
	public String getCardPanelContent();

	@ClassName("progress-bar-container")
	public String getProgressBarContainer();

	@ClassName("progress-bar")
	public String getProgressBar();

	@ClassName("progress-bar-text")
	public String getProgressBarText();

}
