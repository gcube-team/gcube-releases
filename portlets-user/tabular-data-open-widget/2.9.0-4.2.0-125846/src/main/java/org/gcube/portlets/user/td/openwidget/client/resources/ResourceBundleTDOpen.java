package org.gcube.portlets.user.td.openwidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundleTDOpen extends ClientBundle {
	
	public static final ResourceBundleTDOpen INSTANCE=GWT.create(ResourceBundleTDOpen.class);

	
	@Source("delete.png")
	ImageResource delete();
	
	@Source("delete_32.png")
	ImageResource delete32();
	
	@Source("lock-red.png")
	ImageResource lock();
	
	@Source("lock-red_32.png")
	ImageResource lock32();
	
	@Source("lock-open-green.png")
	ImageResource lockOpen();
	
	@Source("lock-open-green_32.png")
	ImageResource lockOpen32();
	
	@Source("page-white-share.png")
	ImageResource share();
	
	@Source("page-white-share_32.png")
	ImageResource share32();
	
	@Source("information.png")
	ImageResource information();
	
	@Source("information_32.png")
	ImageResource information32();
	
	
}