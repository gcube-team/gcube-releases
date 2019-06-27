package org.gcube.portlets.user.td.informationwidget.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
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
	
	
	@Source("disk.png")
	ImageResource save();
	
	@Source("disk_32.png")
	ImageResource save32();
	
	@Source("table-validation.png")
	ImageResource tableValidation();
	
	@Source("table-validation_32.png")
	ImageResource tableValidation32();
	
	@Source("page-white-share.png")
	ImageResource share();
	
	@Source("page-white-share_32.png")
	ImageResource share32();
	
	@Source("resources.png")
	ImageResource resources();
	
	@Source("resources_32.png")
	ImageResource resources32();
	
	@Source("information.png")
	ImageResource information();
	
	@Source("information_32.png")
	ImageResource information32();
	

}