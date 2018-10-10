/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client.resource;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * The Interface WorkspaceUploaderIcons.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 24, 2015
 */
public interface WorkspaceUploaderIcons extends ClientBundle {

	/**
	 * Loading.
	 *
	 * @return the image resource
	 */
	@Source("spinner.gif")
	ImageResource loading();

	/**
	 * Failed.
	 *
	 * @return the image resource
	 */
	@Source("failed.png")
	ImageResource failed();

	/**
	 * Completed.
	 *
	 * @return the image resource
	 */
	@Source("ok.png")
	ImageResource completed();

	/**
	 * Cancel.
	 *
	 * @return the image resource
	 */
	@Source("cancel.png")
	ImageResource cancel();

	/**
	 * Cancel red.
	 *
	 * @return the image resource
	 */
	@Source("cancel_red.png")
	ImageResource cancelRed();

	/**
	 * Close win.
	 *
	 * @return the image resource
	 */
	@Source("closewindow.png")
	ImageResource closeWin();
	
	/**
	 * Wait icon.
	 *
	 * @return the image resource
	 */
	@Source("wait.png")
	ImageResource waitIcon();

	/**
	 * @return
	 */
	@Source("upload.gif")
	ImageResource uploadIcon();
}
