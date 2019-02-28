package org.gcube.portlets.user.newsfeed.client.event;

/**
 * This class contains the events (in a package like format) to which this portlet listens at.
 * NOTE: these events are the same in the User-Statistics portlet and the share-updates one.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class PageBusEvents {
	
	// events in this portlet and user-statistics
	public static final String postIncrement = "org.gcube.portal.incrementPostCount";
	public static final String postDecrement = "org.gcube.portal.decrementPostCount";
	public static final String likesIncrement = "org.gcube.portal.incrementLikesGot";
	public static final String likesDecrement = "org.gcube.portal.decrementLikesGot";
	public static final String commentsIncrement = "org.gcube.portal.incrementCommentsGot";
	public static final String commentsDecrement = "org.gcube.portal.decrementCommentsGot";
	
	// events in this portlet and the share-updates one
	public static final String newPostCreated = "org.gcube.portal.databook.shared";
}
