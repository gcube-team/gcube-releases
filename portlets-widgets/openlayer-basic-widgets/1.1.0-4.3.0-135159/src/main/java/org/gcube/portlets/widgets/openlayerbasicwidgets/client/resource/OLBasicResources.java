package org.gcube.portlets.widgets.openlayerbasicwidgets.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface OLBasicResources extends ClientBundle {

	public static final OLBasicResources INSTANCE = GWT.create(OLBasicResources.class);

	@Source("OLBasic.css")
	OLBasicCSS olBasicCSS();

	@Source("tool-button-close_20.png")
	ImageResource toolButtonClose20();

	@Source("search_16.png")
	ImageResource search16();
}
