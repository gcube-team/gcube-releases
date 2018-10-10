/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


// TODO: Auto-generated Javadoc
/**
 * The Interface Icons.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 7, 2018
 */
public interface Icons extends ClientBundle {

	/** The Constant ICONS. */
	public static final Icons ICONS = GWT.create(Icons.class);

	/**
	 * Loading.
	 *
	 * @return the image resource
	 */
	@Source("loading.gif")
	ImageResource loading();
	
}
