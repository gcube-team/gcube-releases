package org.gcube.portlets.widgets.imagepreviewerwidget.client;

import org.gcube.portlets.widgets.imagepreviewerwidget.shared.Orientation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Image services
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@RemoteServiceRelativePath("imageservices")
public interface ImageService extends RemoteService{

	/**
	 * Retrieve the image orientation (EXIF)
	 * @param imagePath
	 * @return
	 */
	public Orientation getImageOrientation(String imagePath);
	
}
