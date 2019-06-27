/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class WorkspaceUploaderResources.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 24, 2015
 */
public class WorkspaceUploaderResources {

	public static final WorkspaceUploaderIcons ICONS = GWT.create(WorkspaceUploaderIcons.class);

	/**
	 * Gets the image loading.
	 *
	 * @return the image loading
	 */
	public static Image getImageLoading() {
		return new Image(ICONS.loading());
	}

	/**
	 * Gets the image failed.
	 *
	 * @return the image failed
	 */
	public static Image getImageFailed() {
		return new Image(ICONS.failed());
	}

	/**
	 * Gets the image completed.
	 *
	 * @return the image completed
	 */
	public static Image getImageCompleted() {
		return new Image(ICONS.completed());
	}

	/**
	 * Gets the image cancel.
	 *
	 * @return the image cancel
	 */
	public static Image getImageCancel() {
		return new Image(ICONS.cancel());
	}

	/**
	 * Gets the image cancel.
	 *
	 * @return the image cancel
	 */
	public static Image getImageAbort() {
		return new Image(ICONS.abort());
	}

	/**
	 * Gets the image cancel red.
	 *
	 * @return the image cancel red
	 */
	public static Image getImageCancelRed() {
		return new Image(ICONS.cancelRed());
	}

	/**
	 * Gets the image close win.
	 *
	 * @return the image close win
	 */
	public static Image getImageCloseWin() {
		return new Image(ICONS.closeWin());
	}

	/**
	 * Gets the image wait.
	 *
	 * @return the image wait
	 */
	public static Image getImageWait() {
		return new Image(ICONS.waitIcon());
	}

	/**
	 * Gets the image upload.
	 *
	 * @return the image upload
	 */
	public static Image getImageUpload() {
		return new Image(ICONS.uploadIcon());
	}
}
