/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 15, 2015
 *
 */
public interface Icons extends ClientBundle {

	public static final Icons ICONS = GWT.create(Icons.class);

	@Source("loading4.gif")
	ImageResource loading();

	@Source("top.png")
	ImageResource top();

	@Source("maven.png")
	ImageResource maven();

	@Source("wiki.png")
	ImageResource wiki();

	@Source("javadoc.png")
	ImageResource javadoc();

	@Source("download.png")
	ImageResource download();

	@Source("github.png")
	ImageResource github();
}
