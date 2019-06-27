package org.gcube.socialnetworking.social_data_indexing_common.utils;

/**
 * The fields of the documents which are searchable. 
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum SearchableFields {
	POST_TEXT("feed.description"),
	COMMENT_TEXT("comments.text"),
	PREVIEW_DESCRIPTION("feed.linkTitle"),
	POST_AUTHOR("feed.fullName"),
	POST_VRE_ID("feed.vreid"),
	COMMENT_AUTHOR("comments.fullName"),
	ATTACHMENT_NAME("attachments.name");

	String name;

	SearchableFields(String name){
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
