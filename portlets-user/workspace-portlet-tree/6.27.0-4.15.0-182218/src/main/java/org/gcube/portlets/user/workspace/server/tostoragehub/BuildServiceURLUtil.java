package org.gcube.portlets.user.workspace.server.tostoragehub;

import java.util.UUID;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.util.ImageRequestType;

/**
 * The Class BuildServiceURLUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 17, 2019
 */
public class BuildServiceURLUtil {
	
	
	protected static final String IMAGE_SERVICE_URL = "ImageService";
	
	/**
	 * Builds the image url.
	 *
	 * @param id the id
	 * @param currentGroupId the current group id
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected static String buildImageUrl(String id, String currentGroupId, String currUserId)
	{
		return buildImageServiceUrl(id, ImageRequestType.IMAGE, currentGroupId, currUserId);
	}



	/**
	 * Builds the thumbnail url.
	 *
	 * @param id the id
	 * @param currentGroupId the current group id
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected static String buildThumbnailUrl(String id, String currentGroupId, String currUserId)
	{
		return buildImageServiceUrl(id, ImageRequestType.THUMBNAIL, currentGroupId, currUserId);
	}


	/**
	 * Builds the image service url.
	 *
	 * @param id the id
	 * @param requestType the request type
	 * @param currentGroupId the current group id read from PortalContext
	 * @param currUserId the curr user id
	 * @return the string
	 */
	protected static String buildImageServiceUrl(String id, ImageRequestType requestType, String currentGroupId, String currUserId){
		StringBuilder sb = new StringBuilder();
		sb.append(IMAGE_SERVICE_URL);
		sb.append("?id=");
		sb.append(id);
		sb.append("&type=");
		sb.append(requestType.toString());
		sb.append("&"+ConstantsExplorer.CURRENT_CONTEXT_ID+"=");
		sb.append(currentGroupId);
		//		sb.append("&"+ConstantsExplorer.CURRENT_USER_ID+"=");
		//		sb.append(currUserId);
		sb.append("&random=");
		sb.append(UUID.randomUUID().toString());
		return sb.toString();
	}

}
