package org.gcube.portlets.widgets.imagepreviewerwidget.client;

import org.gcube.portlets.widgets.imagepreviewerwidget.shared.Orientation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Image services (async version)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface ImageServiceAsync {

	/**
	 * Retrieve the image orientation (EXIF)
	 * @param imagePath
	 * @param callback
	 */
	void getImageOrientation(String imagePath,
			AsyncCallback<Orientation> callback);

}
