/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;


/**
 * The Interface PerformFishResources.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 8, 2019
 */
public interface PerformFishResources extends ClientBundle {

	public static final PerformFishResources INSTANCE = GWT.create(PerformFishResources.class);

	/**
	 * Loading.
	 *
	 * @return the image resource
	 */
	@Source("loading4.gif")
	ImageResource loading();

	/**
	 * Top.
	 *
	 * @return the image resource
	 */
	@Source("top.png")
	ImageResource top();

	/**
	 * Maven.
	 *
	 * @return the image resource
	 */
	@Source("maven.png")
	ImageResource maven();

	/**
	 * Wiki.
	 *
	 * @return the image resource
	 */
	@Source("wiki.png")
	ImageResource wiki();

	/**
	 * Javadoc.
	 *
	 * @return the image resource
	 */
	@Source("javadoc.png")
	ImageResource javadoc();

	/**
	 * Download.
	 *
	 * @return the image resource
	 */
	@Source("download.png")
	ImageResource download();

	/**
	 * Github.
	 *
	 * @return the image resource
	 */
	@Source("github.png")
	ImageResource github();
	
	@Source("Error_Page.html")
	public TextResource errorPage();
}
