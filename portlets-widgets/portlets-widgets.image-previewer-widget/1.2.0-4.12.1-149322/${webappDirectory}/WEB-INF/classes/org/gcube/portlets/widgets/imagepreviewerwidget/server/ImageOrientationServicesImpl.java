package org.gcube.portlets.widgets.imagepreviewerwidget.server;

import java.io.InputStream;
import java.net.URL;

import org.gcube.portlets.widgets.imagepreviewerwidget.client.ImageService;
import org.gcube.portlets.widgets.imagepreviewerwidget.shared.Orientation;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Image services.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class ImageOrientationServicesImpl extends RemoteServiceServlet implements ImageService {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageOrientationServicesImpl.class);

	@Override
	public Orientation getImageOrientation(String imagePath) {

		logger.debug("Image url is " + imagePath);

		if(imagePath == null || imagePath.isEmpty())
			return Orientation.DO_NOT_ROTATE;
		try{
			URL url = new URL(imagePath);
			InputStream is = url.openStream();
			Metadata metadata = ImageMetadataReader.readMetadata(is);
			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

			// exif is in the range [1, 8], we check only some of them ... we do not cover each case
			// That is, we cover just rotations case but not flip cases.
			int orientation = 1;
			orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

			logger.debug("Orientation is " + orientation);
			return mapIntToOrientation(orientation);
		} catch (Exception e) {
			logger.warn("Could not get orientation");
		}

		// as default
		return Orientation.DO_NOT_ROTATE;
	}

	/**
	 * Map from int to orientation parameter
	 * @param orientation
	 * @return
	 */
	private Orientation mapIntToOrientation(int orientation) {
		switch(orientation){
		case 3: return Orientation.ROTATE_180;
		case 6: return Orientation.ROTATE_90;
		case 8: return Orientation.ROTATE_270;
		default: return Orientation.DO_NOT_ROTATE;
		}
	}

}
