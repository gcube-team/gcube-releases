package org.gcube.portlets.user.td.wizardwidget.client.dataresource;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	@Source("resources/Wizard.css")
	WizardCSS wizardCSS();
	
	@Source("resources/arrow-refresh.png")
	ImageResource refresh();
	
	@Source("resources/arrow-refresh_16.png")
	ImageResource refresh16();
	
	
	@Source("resources/arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("resources/wizard-next.png")
	ImageResource wizardNext();
	
	@Source("resources/wizard-next_32.png")
	ImageResource wizardNext32();
	
	@Source("resources/wizard-previous.png")
	ImageResource wizardPrevious();
	
	@Source("resources/wizard-previous_32.png")
	ImageResource wizardPrevious32();
	
	@Source("resources/wizard-go.png")
	ImageResource wizardGo();
	
	@Source("resources/wizard-go_32.png")
	ImageResource wizardGo32();
	
	
}
 