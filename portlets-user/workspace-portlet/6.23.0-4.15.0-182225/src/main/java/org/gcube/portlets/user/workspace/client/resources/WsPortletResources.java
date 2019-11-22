/**
 * 
 */
package org.gcube.portlets.user.workspace.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 15, 2015
 *
 */
public interface WsPortletResources extends ClientBundle {

	public static final WsPortletResources INSTANCE = GWT.create(WsPortletResources.class);

	@Source("upload16.png")
	ImageResource upload16();
	
	@Source("shareablelinks-howto.txt")
	TextResource publicLinkHowTo();
	
//	@Source("sharelink-howto.txt")
//	TextResource shareLinkkHowTo();
}
