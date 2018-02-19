package org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface NetCDFBasicResources extends ClientBundle {

	public static final NetCDFBasicResources INSTANCE = GWT.create(NetCDFBasicResources.class);

	@Source("NetCDFBasic.css")
	NetCDFBasicCSS netCDFBasicCSS();

	@Source("tool-button-close_20.png")
	ImageResource toolButtonClose20();

	@Source("search_16.png")
	ImageResource search16();
}
