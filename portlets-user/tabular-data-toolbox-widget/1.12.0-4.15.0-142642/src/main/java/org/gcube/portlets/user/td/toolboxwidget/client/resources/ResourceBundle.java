package org.gcube.portlets.user.td.toolboxwidget.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;


/**
 * Resource Bundle
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	
	@Source("wiki_link.txt")
	ExternalTextResource linksProperties();
	
	@Source("tabular-data-wiki.png")
	ImageResource wiki();
	
	@Source("tabular-data-wiki_32.png")
	ImageResource wiki32();
	

}