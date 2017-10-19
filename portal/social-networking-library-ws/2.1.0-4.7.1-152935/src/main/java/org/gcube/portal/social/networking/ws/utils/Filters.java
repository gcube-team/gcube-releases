package org.gcube.portal.social.networking.ws.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.slf4j.LoggerFactory;

/**
 * Filters to apply to feeds/comments etc
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Filters {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Filters.class);

	private static List<String> getContexts(String context) throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault{
		
		// retrieve group information
		GroupManager gm = GroupManagerWSBuilder.getInstance().getGroupManager();
		GCubeGroup group = gm.getGroup(gm.getGroupIdFromInfrastructureScope(context));

		List<String> contexts = new ArrayList<String>();

		if(gm.isRootVO(group.getGroupId())){
			
		}
		else if(gm.isVO(group.getGroupId())){

			List<GCubeGroup> vres = group.getChildren();
			for (GCubeGroup gCubeGroup : vres) {
				contexts.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));
			}

		}else{
			contexts.add(gm.getInfrastructureScope(group.getGroupId()));
		}
		
		return contexts;
	}
	
	/**
	 * Given a list of not filtered feeds, the methods remove feeds unaccessible in this scope.
	 * If the initial context is the root: all feeds are returned;
	 * If the initial context is a VO: feeds for vres within the vo are returned;
	 * If the initial context is a vre: feeds of the vre are returned;
	 * @param feedsIds
	 * @param context
	 * @throws Exception 
	 */
	public static void filterFeedsPerContextById(
			List<String> feedsIds, String context) throws Exception {

		DatabookStore datastore = CassandraConnection.getInstance().getDatabookStore();
		List<Feed> feeds = new ArrayList<Feed>();

		for (String feedId : feedsIds) {
			try{
				feeds.add(datastore.readFeed(feedId));
			}catch(Exception e){
				logger.error("Unable to read feed with id " + feedId, e);	
			}
		}

		// filter
		filterFeedsPerContext(feeds, context);

		// clear and convert
		feedsIds.clear();
		for (Feed feed : feeds) {
			feedsIds.add(feed.getKey());
		}

	}

	/**
	 * Given a list of not filtered feeds, the methods remove feeds unaccessible in this scope.
	 * If the initial context is the root: all feeds are returned;
	 * If the initial context is a VO: feeds for vres within the vo are returned;
	 * If the initial context is a vre: feeds of the vre are returned;
	 * @param retrievedLikedFeeds
	 * @param context
	 * @throws Exception 
	 */
	public static void filterFeedsPerContext(List<Feed> feeds, String context) throws Exception {

		List<String> contexts = getContexts(context);
		
		// filter
		Iterator<Feed> iterator = feeds.iterator();
		while (iterator.hasNext()) {
			Feed feed = (Feed) iterator.next();
			if(!contexts.contains(feed.getVreid()))
				iterator.remove();
		}

	}

	/**
	 * Filter comments per context
	 * @param comments
	 * @param context
	 * @throws Exception 
	 */
	public static void filterCommentsPerContext(List<Comment> comments, String context) throws Exception {

		List<String> contexts = getContexts(context);

		// get cassandra store
		DatabookStore datastore = CassandraConnection.getInstance().getDatabookStore();

		// filter
		Iterator<Comment> iterator = comments.iterator();
		while (iterator.hasNext()) {
			try{
				Comment comment = (Comment) iterator.next();
				Feed parent = datastore.readFeed(comment.getFeedid());
				if(!contexts.contains(parent.getVreid()))
					iterator.remove();
			}catch(Exception e){
				logger.warn("Failed to analyze this comment", e);
				iterator.remove(); // remove it anyway
			}
		}

	}

	/**
	 * Depending on the type of object provided (e.g. Feed, Comment etc), some information are removed
	 * @param comments
	 * @throws Exception
	 */
	public static <T> void hideSensitiveInformation(List<T> toClear, String usernameCaller){

		if(toClear == null || toClear.isEmpty() || usernameCaller == null || usernameCaller.isEmpty())
			return;
		else{

			// for feeds
			if(toClear.get(0).getClass().equals(Feed.class)){

				for (T feed : toClear) {
					Feed feeded = ((Feed)feed);
					if(!usernameCaller.equals(feeded.getEntityId()))
						feeded.setEmail(""); // remove the email field
				}

			}else if(toClear.get(0).getClass().equals(EnhancedFeed.class)){
				for (T enhancedFeed : toClear) {
					Feed feeded = ((EnhancedFeed)enhancedFeed).getFeed();
					if(!usernameCaller.equals(feeded.getEntityId()))
						feeded.setEmail(""); // remove the email field
				}
			}

		}

	}

}
