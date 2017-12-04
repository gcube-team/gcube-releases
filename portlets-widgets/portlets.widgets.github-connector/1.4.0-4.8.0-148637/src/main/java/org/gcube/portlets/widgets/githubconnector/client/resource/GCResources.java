package org.gcube.portlets.widgets.githubconnector.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface GCResources extends ClientBundle {

	public static final GCResources INSTANCE = GWT.create(GCResources.class);

	@Source("Wizard.css")
	WizardCSS wizardCSS();

	@Source("wizard-next_24.png")
	ImageResource wizardNext24();

	@Source("wizard-previous_24.png")
	ImageResource wizardPrevious24();

	@Source("wizard-go_24.png")
	ImageResource wizardGo24();

	@Source("tool-button-close_20.png")
	ImageResource toolButtonClose20();

	@Source("search_16.png")
	ImageResource search16();
}
