/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import org.gcube.portlets.widgets.lighttree.client.resources.WorkspaceLightTreeResources;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ErrorPanel extends VerticalPanel {
	
	protected static final String ERROR_HEADER = "An error occurred loading the workspace";
	
	protected static final Image ERROR_IMAGE = new Image(WorkspaceLightTreeResources.INSTANCE.error());
	protected static final HorizontalPanel ERROR_PANEL = new HorizontalPanel();
	static{
		ERROR_PANEL.setSpacing(5);
		ERROR_PANEL.add(ERROR_IMAGE);
		ERROR_PANEL.add(new HTML("<b>"+ERROR_HEADER+"</b>"));
	}
	
	protected HTML errorMessage = new HTML();
	protected PushButton reloadButton = new PushButton(new Image(WorkspaceLightTreeResources.INSTANCE.refresh()));
	
	public ErrorPanel()
	{
		setSpacing(3);
	
		add(ERROR_PANEL);
		add(errorMessage);
		
		HorizontalPanel reloadPanel = new HorizontalPanel();
		reloadPanel.setSpacing(5);
		reloadPanel.add(new HTML("Try to reload it: "));
		reloadPanel.add(reloadButton);
		add(reloadPanel);
	}
	
	public void setMessage(String message)
	{
		errorMessage.setHTML(message);
	}
	
	public void setMessage(Throwable throwable)
	{
		setMessage(throwable.getMessage());
	}
	
	public void addClickHandler(ClickHandler handler)
	{
		reloadButton.addClickHandler(handler);
	}

}
