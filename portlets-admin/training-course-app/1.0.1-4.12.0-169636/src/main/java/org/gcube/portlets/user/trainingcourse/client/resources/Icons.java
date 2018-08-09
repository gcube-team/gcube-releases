/**
 *
 */
package org.gcube.portlets.user.trainingcourse.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

// TODO: Auto-generated Javadoc
/**
 * The Interface Icons.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 11, 2018
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
	
	/**
	 * Loading.
	 *
	 * @return the image resource
	 */
	@Source("suitcase.png")
	ImageResource suitcase();
}
