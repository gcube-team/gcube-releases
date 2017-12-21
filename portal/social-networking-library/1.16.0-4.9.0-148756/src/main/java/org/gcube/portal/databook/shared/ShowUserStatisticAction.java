package org.gcube.portal.databook.shared;

/**
 * Enum class that specify the possible actions to take when the GCubeSocialNetworking.SHOW_STATISTICS_ACTION_OID parameter
 * is found in the page url
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum ShowUserStatisticAction {

	POSTS_MADE_BY_USER("Your recent posts"),
	LIKES_MADE_BY_USER("Posts you liked"),
	COMMENTS_MADE_BY_USER("Posts you commented"),
	LIKES_GOT_BY_USER("Likes to your posts"),
	COMMENTS_GOT_BY_USER("Replies to your posts");
	
	private final String actionHumanFriendly;
	
	private ShowUserStatisticAction(String s) {
		actionHumanFriendly = s;
    }

	public String getHumanFriendlyAction() {
		return this.actionHumanFriendly;
	}
}
