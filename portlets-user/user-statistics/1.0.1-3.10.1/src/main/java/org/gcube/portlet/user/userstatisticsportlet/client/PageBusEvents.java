package org.gcube.portlet.user.userstatisticsportlet.client;

/**
 * This class contains the events (in a package like format) to which this portlet listens at.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class PageBusEvents {
	
	public static final String postIncrement = "org.gcube.portal.incrementPostCount";
	public static final String postDecrement = "org.gcube.portal.decrementPostCount";
	public static final String likesIncrement = "org.gcube.portal.incrementLikesGot";
	public static final String likesDecrement = "org.gcube.portal.decrementLikesGot";
	public static final String commentsIncrement = "org.gcube.portal.incrementCommentsGot";
	public static final String commentsDecrement = "org.gcube.portal.decrementCommentsGot";
}
