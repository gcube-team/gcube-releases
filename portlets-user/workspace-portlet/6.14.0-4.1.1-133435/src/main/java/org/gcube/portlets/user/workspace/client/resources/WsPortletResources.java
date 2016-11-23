/**
 * 
 */
package org.gcube.portlets.user.workspace.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 15, 2015
 *
 */
public interface WsPortletResources extends ClientBundle {

	public static final WsPortletResources ICONS = GWT.create(WsPortletResources.class);

	@Source("upload16.png")
	ImageResource upload16();
}
