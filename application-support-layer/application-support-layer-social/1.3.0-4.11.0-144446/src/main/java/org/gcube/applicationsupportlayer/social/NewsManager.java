package org.gcube.applicationsupportlayer.social;

import java.io.InputStream;

import org.gcube.portal.databook.shared.ImageType;



/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Dec 2012
 *
 */
public interface NewsManager {
	/**
	 * use to share an update from your application
	 * 
	 * @param feedtext add a description for the update you are sharing
	 * @return true if the update is correctly delivered, false otherwise
	 */
	boolean shareApplicationUpdate(String feedtext);
	/**
	 * use to share an update from your application with a reference to the news object
	 * 
	 * @param feedtext description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 * @return true if the update is correctly delivered, false otherwise
	 */
	boolean shareApplicationUpdate(String feedtext, String uriGETparams);
	/**
	 * use to share an update from your application with a reference to the news object and with a link preview 
	 * 
	 * @param feedtext add a description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 * @param previewTitle the title to show in the preview
	 * @param previewDescription the description to show in the preview
	 * @param previewThumbnailUrl the image url to show in the preview
	 * @return true if the update is correctly delivered, false otherwise
	 */
	boolean shareApplicationUpdate(String feedtext, String uriGETparams, String previewTitle, String previewDescription, String previewThumbnailUrl);
	/**
	 * use to share an update from your application with a reference to the news object and with a link preview passing its input stream
	 * 
	 * @param feedtext add a description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 * @param previewTitle the title to show in the preview
	 * @param previewDescription the description to show in the preview
	 * @param previewThumbnailInputStream the image url Input Stream to show in the preview
	 * @param imageExtension the image Extension
	 * @return true if the update is correctly delivered, false otherwise
	 */
	boolean shareApplicationUpdate(String feedtext, String uriGETparams, String previewTitle, String previewDescription, InputStream previewThumbnailInputStream, ImageType imageExtension);
}
