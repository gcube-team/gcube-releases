package org.gcube.socialnetworking.social_data_indexing_common.utils;

/**
 * The fields used to build up the index.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class IndexFields {
	
	// name of the index
	public static final String INDEX_NAME = "social";
	
	// table for enhanced feeds
	public static final String EF_FEEDS_TABLE = "enhanced_feeds";
	
	// enhanced feeds' fields of interest
	public static final String EF_ATTACHMENT_NAME = "attachments.name";
	public static final String EF_FEED_TEXT = "feed.description";
	public static final String EF_COMMENT_TEXT = "comments.text";
	public static final String EF_PREVIEW_DESCRIPTION = "feed.linkTitle";
	public static final String EF_FEED_AUTHOR = "feed.fullName";
	public static final String EF_FEED_VRE_ID = "feed.vreid";
	public static final String EF_COMMENT_FULL_NAME = "comments.fullName";
}
