package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 15, 2015
 *
 */
public interface CkanPortletResources extends ClientBundle {

	public static final CkanPortletResources ICONS = GWT.create(CkanPortletResources.class);

	@Source("loader.gif")
	ImageResource loading();
}
