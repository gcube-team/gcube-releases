package org.gcube.portal.databook.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.NullArgumentException;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.Notification;
import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portal.databook.shared.RangeFeeds;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.CommentIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteIDNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteStatusNotFoundException;
import org.gcube.portal.databook.shared.ex.LikeIDNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationChannelTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationIDNotFoundException;
import org.gcube.portal.databook.shared.ex.NotificationTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.PrivacyLevelTypeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.PreparedIndexExpression;
import com.netflix.astyanax.serializers.StringSerializer;
/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * This class is used for querying and adding data to Cassandra via Astyanax High Level API 
 *
 */
public final class DBCassandraAstyanaxImpl implements DatabookStore {

	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(DBCassandraAstyanaxImpl.class);
	/**
	 * Column Family names
	 */
	public static final String CONNECTIONS = "Connections";
	public static final String PENDING_CONNECTIONS_CF_NAME = "PendingConnections";
	public static final String NOTIFICATIONS = "Notifications";
	public static final String FEEDS = "Feeds";
	public static final String COMMENTS = "Comments";
	public static final String LIKES = "Likes";
	public static final String INVITES = "Invites";
	public static final String VRE_TIMELINE_FEEDS = "VRETimeline";
	public static final String USER_TIMELINE_FEEDS = "USERTimeline";
	public static final String APP_TIMELINE_FEEDS = "AppTimeline";
	public static final String USER_LIKED_FEEDS = "USERLikes";
	public static final String USER_NOTIFICATIONS = "USERNotifications"; // regular user notifications timeline
	public static final String USER_MESSAGES_NOTIFICATIONS = "USERMessagesNotifications";  // user messages notifications timeline
	public static final String USER_NOTIFICATIONS_PREFERENCES = "USERNotificationsPreferences"; // preferences for notifications
	public static final String HASHTAGS_COUNTER = "HashtagsCounter"; // count the hashtags per group and type
	public static final String HASHTAGGED_FEEDS = "HashtaggedFeeds"; // contains hashtags per type associated with vre and feed
	public static final String VRE_INVITES = "VREInvites"; //contains the emails that were invited per VRE
	public static final String EMAIL_INVITES = "EMAILInvites"; //contains the list of invitation per email	
	public static final String ATTACHMENTS = "Attachments"; //contains the list of all the attachments in a feed
	public static final String FEED_ATTACHMENTS = "FeedAttachments"; //contains the list of all the attachments for a given feed (dynamic CF)


	private static ColumnFamily<String, String> cf_Connections = new ColumnFamily<String, String>(
			CONNECTIONS, // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_PendingConnections =	new ColumnFamily<String, String>(
			PENDING_CONNECTIONS_CF_NAME, // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_Feeds =	new ColumnFamily<String, String>(
			FEEDS,              // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_UserTline = new ColumnFamily<String, String>(
			USER_TIMELINE_FEEDS,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	private static ColumnFamily<String, String> cf_VRETline = new ColumnFamily<String, String>(
			VRE_TIMELINE_FEEDS,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	private static ColumnFamily<String, String> cf_AppTline = new ColumnFamily<String, String>(
			APP_TIMELINE_FEEDS,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	private static ColumnFamily<String, String> cf_Comments = new ColumnFamily<String, String>(
			COMMENTS,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_Likes = new ColumnFamily<String, String>(
			LIKES,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_Invites = new ColumnFamily<String, String>(
			INVITES, // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_UserLikedFeeds = new ColumnFamily<String, String>(
			USER_LIKED_FEEDS,     // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_Notifications =	new ColumnFamily<String, String>(
			NOTIFICATIONS,              // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_UserNotifications =	new ColumnFamily<String, String>(
			USER_NOTIFICATIONS,              // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_UserMessageNotifications =	new ColumnFamily<String, String>(
			USER_MESSAGES_NOTIFICATIONS,              // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	protected static ColumnFamily<String, String> cf_UserNotificationsPreferences =	new ColumnFamily<String, String>(
			USER_NOTIFICATIONS_PREFERENCES,  // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	private static ColumnFamily<String, String> cf_HashtagsCounter =	new ColumnFamily<String, String>(
			HASHTAGS_COUNTER,        // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	protected static ColumnFamily<String, String> cf_HashtagTimeline =	new ColumnFamily<String, String>(
			HASHTAGGED_FEEDS,  // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	private static ColumnFamily<String, String> cf_VREInvites =	new ColumnFamily<String, String>(
			VRE_INVITES,        // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer
	protected static ColumnFamily<String, String> cf_EmailInvites =	new ColumnFamily<String, String>(
			EMAIL_INVITES,  // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	protected static ColumnFamily<String, String> cf_Attachments =	new ColumnFamily<String, String>(
			ATTACHMENTS,  // Column Family Name
			StringSerializer.get(),   // Key Serializer
			StringSerializer.get());  // Column Serializer

	/**
	 * connection instance
	 */
	private CassandraClusterConnection conn;

	protected CassandraClusterConnection getConnection() {
		return conn;
	}
	/**
	 * use this constructor carefully from test classes
	 * @param dropSchema set true if you want do drop the current and set up new one
	 */
	protected DBCassandraAstyanaxImpl(boolean dropSchema) {
		conn = new CassandraClusterConnection(dropSchema);
	}
	/**
	 * public constructor, no dropping schema is allowed
	 */
	public DBCassandraAstyanaxImpl() {
		conn = new CassandraClusterConnection(false);
	}
	/**
	 * execute the mutation batch
	 * @param m
	 * @return true if everything went fine
	 */
	private boolean execute(MutationBatch m) {
		try {
			m.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	/*
	 * 
	 ********************** 	FRIENDSHIPS (CONNECTIONS)	***********************
	 *
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requestFriendship(String from, String to) {

		MutationBatch m = conn.getKeyspace().prepareMutationBatch();		
		m.withRow(cf_PendingConnections, to).putColumn(from, "", null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
			return false;
		}	
		_log.info(from + " has requested a connection to " + to);
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean approveFriendship(String from, String to) {

		MutationBatch m = conn.getKeyspace().prepareMutationBatch();		
		m.withRow(cf_Connections, to).putColumn(from, "", null);
		m.withRow(cf_Connections, from).putColumn(to, "", null);
		// Deleting a standard column
		m.withRow(cf_PendingConnections, from).deleteColumn(to);
		boolean result = execute(m);
		if (result)
			_log.info(from + " and " + to +  " are now connected");
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean denyFriendship(String from, String to) {
		// Deleting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();	
		m.withRow(cf_PendingConnections, from).deleteColumn(to);
		boolean result = execute(m);
		if (result)
			_log.info(from + " has denied connection to " + to);
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getFriends(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Connections)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
			return null;
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getName());
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPendingFriendRequests(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_PendingConnections)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
			return null;
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getName());
			}
		}
		return toReturn;
	}
	/*
	 * 
	 ********************** 	FEEDS	***********************
	 *
	 */
	/**
	 * common part to save a feed
	 * @param feed
	 * @return the partial mutation batch instance
	 */
	private MutationBatch initSaveFeed(Feed feed) {
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Feeds, feed.getKey().toString())
		.putColumn("Entityid", feed.getEntityId(), null)
		.putColumn("Time", feed.getTime().getTime()+"", null)
		.putColumn("Vreid", feed.getVreid(), null)
		.putColumn("Uri", feed.getUri(), null)
		.putColumn("UriThumbnail", feed.getUriThumbnail(), null)
		.putColumn("Description", feed.getDescription(), null)
		.putColumn("Privacy", feed.getPrivacy().toString(), null)
		.putColumn("FullName", feed.getFullName(), null)
		.putColumn("Type", feed.getType().toString(), null)
		.putColumn("Email", feed.getEmail(), null)
		.putColumn("ThumbnailURL", feed.getThumbnailURL(), null)
		.putColumn("CommentsNo", feed.getCommentsNo(), null)
		.putColumn("LikesNo", feed.getLikesNo(), null)
		.putColumn("LinkTitle", feed.getLinkTitle(), null)
		.putColumn("LinkDescription", feed.getLinkDescription(), null)
		.putColumn("LinkHost", feed.getLinkHost(), null)
		.putColumn("IsApplicationFeed", feed.isApplicationFeed(), null)
		.putColumn("multiFileUpload", feed.isMultiFileUpload(), null);
		return m;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveUserFeed(Feed feed) {
		MutationBatch m = initSaveFeed(feed);

		//an entry in the user Timeline 
		m.withRow(cf_UserTline, feed.getEntityId())
		.putColumn(feed.getTime().getTime()+"", feed.getKey().toString(), null);

		//an entry in the VRES Timeline iff vreid field is not empty
		if (feed.getVreid() != null && feed.getVreid().compareTo("") != 0) {
			//an entry in the VRES Timeline 
			m.withRow(cf_VRETline, feed.getVreid())
			.putColumn(feed.getTime().getTime()+"", feed.getKey().toString(), null);
		}
		return execute(m);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveUserFeed(Feed feed, List<Attachment> attachments) {
		if (attachments != null && !attachments.isEmpty())
			feed.setMultiFileUpload(true);
		boolean saveFeedResult = saveUserFeed(feed);
		if (saveFeedResult) {
			String feedKey = feed.getKey();
			for (Attachment attachment : attachments) {
				boolean attachSaveResult = saveAttachmentEntry(feedKey, attachment);
				if (!attachSaveResult)
					_log.warn("Some of the attachments failed to me saved: " + attachment.getName());
			}
			return true;
		}
		else return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveAppFeed(Feed feed) {
		MutationBatch m = initSaveFeed(feed);

		//an entry in the Applications Timeline 
		m.withRow(cf_AppTline, feed.getEntityId())
		.putColumn(feed.getTime().getTime()+"", feed.getKey().toString(), null);

		//an entry in the VRES Timeline iff vreid field is not empty
		if (feed.getVreid() != null && feed.getVreid().compareTo("") != 0) {
			//an entry in the VRES Timeline 
			m.withRow(cf_VRETline, feed.getVreid())
			.putColumn(feed.getTime().getTime()+"", feed.getKey().toString(), null);
		}
		boolean result = execute(m);
		if (result)
			_log.trace("saveAppFeed OK!");
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveAppFeed(Feed feed, List<Attachment> attachments) {
		if (attachments != null && !attachments.isEmpty())
			feed.setMultiFileUpload(true);
		boolean saveFeedResult = saveAppFeed(feed);
		if (saveFeedResult) {
			String feedKey = feed.getKey();
			for (Attachment attachment : attachments) {
				boolean attachSaveResult = saveAttachmentEntry(feedKey, attachment);
				if (!attachSaveResult)
					_log.warn("Some of the attachments failed to me saved: " + attachment.getName());
			}
			return true;
		}
		else return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveFeedToVRETimeline(String feedKey, String vreid) throws FeedIDNotFoundException {
		String feedId = feedKey;
		Feed toCheck = null;
		try {
			toCheck = readFeed(feedId);
			if (toCheck == null)
				throw new FeedIDNotFoundException("Could not find Feed with id " + feedId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();

		//an entry in the user Timeline 
		m.withRow(cf_VRETline, vreid)
		.putColumn(toCheck.getTime().getTime()+"", feedKey, null);
		return execute(m);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Feed readFeed(String feedid)
			throws PrivacyLevelTypeNotFoundException,
			FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException {
		Feed toReturn = new Feed();
		OperationResult<ColumnList<String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Feeds)
					.getKey(feedid)
					.execute();

			ColumnList<String> columns = result.getResult();
			if (columns.size() == 0) {
				throw new FeedIDNotFoundException("The requested feedid: " + feedid + " is not existing");
			}

			toReturn.setKey(feedid);
			toReturn.setDescription(columns.getColumnByName("Description").getStringValue());
			toReturn.setEmail(columns.getColumnByName("Email").getStringValue());
			toReturn.setFullName(columns.getColumnByName("FullName").getStringValue());
			toReturn.setPrivacy(getPrivacyLevel(columns.getColumnByName("Privacy").getStringValue()));
			toReturn.setThumbnailURL(columns.getColumnByName("ThumbnailURL").getStringValue());
			toReturn.setTime(getDateFromTimeInMillis(columns.getColumnByName("Time").getStringValue()));

			FeedType ft = getFeedType(columns.getColumnByName("Type").getStringValue());

			toReturn.setType(ft);
			toReturn.setUri(columns.getColumnByName("Uri").getStringValue());
			toReturn.setUriThumbnail(columns.getColumnByName("UriThumbnail").getStringValue());			
			toReturn.setVreid(columns.getColumnByName("Vreid").getStringValue());
			toReturn.setEntityId(columns.getColumnByName("Entityid").getStringValue());
			toReturn.setCommentsNo(columns.getColumnByName("CommentsNo").getStringValue());
			toReturn.setLikesNo(columns.getColumnByName("LikesNo").getStringValue());

			toReturn.setLinkTitle(columns.getColumnByName("LinkTitle").getStringValue());
			toReturn.setLinkDescription(columns.getColumnByName("LinkDescription").getStringValue());
			toReturn.setLinkHost(columns.getColumnByName("LinkHost").getStringValue());
			toReturn.setApplicationFeed(columns.getColumnByName("IsApplicationFeed").getBooleanValue());
			boolean isMultiFileUpload = false;
			try {
				isMultiFileUpload = columns.getColumnByName("multiFileUpload").getBooleanValue();
			}
			catch (NullPointerException e) { }
			toReturn.setMultiFileUpload(isMultiFileUpload);

		} catch (ConnectionException e) {
			e.printStackTrace();
			return null;
		}	
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getRecentFeedsByUserAndDate(String userid, long timeInMillis) throws IllegalArgumentException {
		Date now = new Date();
		if (timeInMillis > now.getTime())
			throw new IllegalArgumentException("the timeInMillis must be before today");

		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserTline)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		List<Feed> toReturn = new ArrayList<Feed>();
		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				long feedTime = Long.parseLong(column.getName());
				if (feedTime > timeInMillis) {
					try {
						Feed toCheck = readFeed(column.getStringValue());
						if (toCheck.getType() != FeedType.DISABLED)
							toReturn.add(toCheck);
					} catch (PrivacyLevelTypeNotFoundException
							| FeedTypeNotFoundException
							| FeedIDNotFoundException
							| ColumnNameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteFeed(String feedId) throws FeedIDNotFoundException, PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException {
		Feed toDelete = readFeed(feedId);
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//edit the entry in the feed CF
		m.withRow(cf_Feeds, toDelete.getKey().toString()).putColumn("Type", ""+FeedType.DISABLED, null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("Delete Feed ERROR for feedid " + feedId);
			return false;
		}
		_log.info("Delete Feed OK");
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getAllFeedsByUser(String userid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		return getFeedsByIds(getUserFeedIds(userid));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getAllFeedsByApp(String appid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		return getFeedsByIds(getAppFeedIds(appid));
	}
	/**
	 * helper method that retrieve all the feeds belongin to a list of Ids
	 * @param feedIds
	 * @return
	 * @throws ColumnNameNotFoundException 
	 * @throws FeedIDNotFoundException 
	 * @throws FeedTypeNotFoundException 
	 * @throws PrivacyLevelTypeNotFoundException 
	 */
	private List<Feed> getFeedsByIds(List<String> feedIds) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException {
		ArrayList<Feed> toReturn = new ArrayList<Feed>();
		for (String feedid : feedIds)  {
			Feed toAdd = readFeed(feedid);
			if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH)
				toReturn.add(toAdd);
		}
		return toReturn;
	}
	/**
	 * helper method that retrieve all the feed Ids belonging to a user
	 * @param userid  user identifier
	 * @return simply return a list of user feed UUID in chronological order from the oldest to the more recent
	 */
	private ArrayList<String> getUserFeedIds(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserTline)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}

	/**
	 * helper method that return whether the user 
	 * @param userid  user identifier
	 * @param feedid the feed identifier
	 * @return true if the feed id liked already
	 */
	private boolean isFeedLiked(String userid, String feedid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserLikedFeeds)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		// Iterate rows and their columns looking for the feeid id
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				if (column.getStringValue().compareTo(feedid)==0)	
					return true;
			}
		}
		return false;
	}


	/**
	 * helper method that retrieve all the feed Ids belonging to an application
	 * @param appid  application identifier
	 * @return simply return a list of app feed UUID in chronological order from the oldest to the more recent
	 */
	private ArrayList<String> getAppFeedIds(String appid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_AppTline)
					.getKeySlice(appid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	public List<Feed> getAllPortalPrivacyLevelFeeds() throws FeedTypeNotFoundException, ColumnNameNotFoundException, PrivacyLevelTypeNotFoundException {
		ArrayList<Feed> toReturn = new ArrayList<Feed>();
		OperationResult<Rows<String, String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Feeds)
					.searchWithIndex()
					.setLimit(20)   // Number of rows returned
					.addExpression()
					.whereColumn("Privacy").equals().value(PrivacyLevel.PORTAL.toString())
					.execute();
			// Iterate rows and their columns 
			for (Row<String, String> row : result.getResult()) {
				Feed toAdd = new Feed();
				toAdd.setKey(row.getKey());
				for (Column<String> col : row.getColumns()) {
					if (col.getName().compareTo("Description") == 0)
						toAdd.setDescription(col.getStringValue());
					else if (col.getName().compareTo("FullName") == 0)
						toAdd.setFullName(col.getStringValue());
					else if (col.getName().compareTo("Email") == 0) 
						toAdd.setEmail(col.getStringValue());
					else if (col.getName().compareTo("Privacy") == 0) 
						toAdd.setPrivacy(getPrivacyLevel(col.getStringValue()));
					else if (col.getName().compareTo("ThumbnailURL") == 0) 
						toAdd.setThumbnailURL(col.getStringValue());
					else if (col.getName().compareTo("Time") == 0) 
						toAdd.setTime(getDateFromTimeInMillis(col.getStringValue()));
					else if (col.getName().compareTo("Type") == 0) {
						FeedType ft = getFeedType(col.getStringValue());
						toAdd.setType(ft);
					}			
					else if (col.getName().compareTo("Uri") == 0) 
						toAdd.setUri(col.getStringValue());
					else if (col.getName().compareTo("UriThumbnail") == 0) 
						toAdd.setUriThumbnail(col.getStringValue());
					else if (col.getName().compareTo("Vreid") == 0) 
						toAdd.setVreid(col.getStringValue());
					else if (col.getName().compareTo("Entityid") == 0) 
						toAdd.setEntityId(col.getStringValue());
					else if (col.getName().compareTo("CommentsNo") == 0) 
						toAdd.setCommentsNo(col.getStringValue());
					else if (col.getName().compareTo("LikesNo") == 0) 
						toAdd.setLikesNo(col.getStringValue());
					else if (col.getName().compareTo("LinkDescription") == 0) 
						toAdd.setLinkDescription(col.getStringValue());
					else if (col.getName().compareTo("LinkHost") == 0) 
						toAdd.setLinkHost(col.getStringValue());
					else if (col.getName().compareTo("LinkTitle") == 0) 
						toAdd.setLinkTitle(col.getStringValue());
					else if (col.getName().compareTo("IsApplicationFeed") == 0) 
						toAdd.setApplicationFeed(col.getBooleanValue());
					else {
						_log.warn("getAllPortalPrivacyLevelFeeds(): Could not assign variable to this Feed for column name: " + col.getName());
					}
				}
				if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH)
					toReturn.add(toAdd);
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
			return toReturn;
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getRecentFeedsByUser(String userid, int quantity)	throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		ArrayList<Feed> toReturn = new ArrayList<Feed>();
		ArrayList<String> feedIDs = getUserFeedIds(userid);
		//check if quantity is greater than user feeds
		quantity = (quantity > feedIDs.size()) ? feedIDs.size() : quantity;

		//need them in reverse order		
		for (int i = feedIDs.size()-1; i >= (feedIDs.size()-quantity); i--) {
			Feed toAdd = readFeed(feedIDs.get(i));
			if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH) {
				toReturn.add(toAdd);
				_log.trace("Read recent feed: " + feedIDs.get(i));
			} else {
				_log.trace("Read and skipped feed: " + feedIDs.get(i) + " (Removed Feed)");
				quantity += 1; //increase the quantity in case of removed feed
				//check if quantity is greater than user feeds
				quantity = (quantity > feedIDs.size()) ? feedIDs.size() : quantity;
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getAllFeedsByVRE(String vreid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		return getFeedsByIds(getVREFeedIds(vreid));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getRecentFeedsByVRE(String vreid, int quantity) throws PrivacyLevelTypeNotFoundException,	FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		ArrayList<Feed> toReturn = new ArrayList<Feed>();
		ArrayList<String> feedIDs = getVREFeedIds(vreid);
		//check if quantity is greater than user feeds
		quantity = (quantity > feedIDs.size()) ? feedIDs.size() : quantity;

		//need them in reverse order		
		for (int i = feedIDs.size()-1; i >= (feedIDs.size()-quantity); i--) {
			Feed toAdd = readFeed(feedIDs.get(i));
			if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH) {
				toReturn.add(toAdd);
				_log.trace("Read recent feed: " + feedIDs.get(i));
			} else {
				_log.trace("Read and skipped feed: " + feedIDs.get(i) + " (Removed Feed) .");
				quantity += 1; //increase the quantity in case of removed feed
				//check if quantity is greater than user feeds
				quantity = (quantity > feedIDs.size()) ? feedIDs.size() : quantity;
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RangeFeeds getRecentFeedsByVREAndRange(String vreid, int from, int quantity) throws IllegalArgumentException, PrivacyLevelTypeNotFoundException,	FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		if (from < 1) {
			throw new IllegalArgumentException("From must be greather than 0");
		} 
		ArrayList<Feed> feedsToReturn = new ArrayList<Feed>();
		ArrayList<String> feedIDs = getVREFeedIds(vreid);

		//if from is greater than feeds size return empty
		if (from >=  feedIDs.size()) {
			_log.warn("The starting point of the range is greather than the total number of feeds for this timeline: " + from + " >= " + feedIDs.size());
			return new RangeFeeds();
		}

		int rangeStart = feedIDs.size()-from;
		int rangeEnd = rangeStart-quantity;

		//check that you reached the end
		if (rangeEnd<1)
			rangeEnd = 0;

		_log.debug("BEFORE starting Point=" + rangeStart + " rangeEnd= " + rangeEnd);
		//need them in reverse order		
		int howMany = from;
		for (int i = rangeStart; i > rangeEnd; i--) {
			Feed toAdd = readFeed(feedIDs.get(i));
			if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH) {
				feedsToReturn.add(toAdd);
				_log.trace("Read recent feed, i=" + i + " id= " + feedIDs.get(i));
			} else {
				_log.trace("Read and skipped feed, i=" + i + " id=: " + feedIDs.get(i) + " (Removed Feed) .");
				rangeEnd -= 1; //increase the upTo in case of removed feed
				//check if quantity is greater than user feeds
				rangeEnd = (rangeEnd > 0) ? rangeEnd : 0;
			}
			howMany++;
		}
		_log.debug("AFTER: starting Point==" + rangeStart + " rangeEnd= " + rangeEnd);
		return new RangeFeeds(howMany+1, feedsToReturn);
	}
	/**
	 * get a list of user vre feed UUIDs in chronological order from the oldest to the more recent
	 * @param vreid  vreid identifier (scope)
	 * @return simply return a list of user vre feed UUIDs in chronological order from the oldest to the more recent
	 */
	private ArrayList<String> getVREFeedIds(String vreid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_VRETline)
					.getKeySlice(vreid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}
	/*
	 * 
	 ********************** 	NOTIFICATIONS	***********************
	 *
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNotification(Notification n) {
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Notifications, n.getKey().toString())
		.putColumn("Type", n.getType().toString(), null)
		.putColumn("Userid", n.getUserid(), null)
		.putColumn("Subjectid", n.getSubjectid(), null)
		.putColumn("Time", n.getTime().getTime()+"", null)
		.putColumn("Uri", n.getUri(), null)
		.putColumn("Description", n.getDescription(), null)
		.putColumn("Read", n.isRead(), null)
		.putColumn("Senderid", n.getSenderid(), null)
		.putColumn("SenderFullName", n.getSenderFullName(), null)
		.putColumn("SenderThumbnail", n.getSenderThumbnail(), null);

		if (n.getType() != NotificationType.MESSAGE)
			//an entry in the user Notifications Timeline 
			m.withRow(cf_UserNotifications, n.getUserid()).putColumn(n.getTime().getTime()+"", n.getKey().toString(), null);
		else
			//an entry in the user Messages Notifications Timeline 
			m.withRow(cf_UserMessageNotifications, n.getUserid()).putColumn(n.getTime().getTime()+"", n.getKey().toString(), null);

		return execute(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Notification readNotification(String notificationid) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException {
		Notification toReturn = new Notification();
		OperationResult<ColumnList<String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Notifications)
					.getKey(notificationid)
					.execute();

			ColumnList<String> columns = result.getResult();
			if (columns.size() == 0) {
				throw new NotificationIDNotFoundException("The requested notificationid: " + notificationid + " is not existing");
			}

			toReturn.setKey(notificationid);
			NotificationType nt = getNotificationType(columns.getColumnByName("Type").getStringValue());
			toReturn.setType(nt);
			toReturn.setUserid(columns.getColumnByName("Userid").getStringValue());
			toReturn.setSubjectid(columns.getColumnByName("Subjectid").getStringValue());
			toReturn.setTime(getDateFromTimeInMillis(columns.getColumnByName("Time").getStringValue()));
			toReturn.setUri(columns.getColumnByName("Uri").getStringValue());
			toReturn.setDescription(columns.getColumnByName("Description").getStringValue());
			toReturn.setRead(columns.getColumnByName("Read").getBooleanValue());
			toReturn.setSenderid(columns.getColumnByName("Senderid").getStringValue());
			toReturn.setSenderFullName(columns.getColumnByName("SenderFullName").getStringValue());
			toReturn.setSenderThumbnail(columns.getColumnByName("SenderThumbnail").getStringValue());

		} catch (ConnectionException e) {
			e.printStackTrace();
			return null;
		}	
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setNotificationRead(String notificationidToSet) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException {
		Notification toSet = readNotification(notificationidToSet);
		if (toSet == null)
			throw new NotificationIDNotFoundException("The specified notification to set Read with id: " + notificationidToSet + " does not exist");

		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Notifications, notificationidToSet).putColumn("Read", true, null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("ERROR while setting Notification " + notificationidToSet + " to read.");
			return false;
		}
		_log.trace("Notification Set read OK to");
		return true;
	}
	/**
	 * 
	 * @param userid  user identifier
	 * @return simply return a list of user notifications UUID in chronological order from the oldest to the more recent
	 */
	private ArrayList<String> getUserNotificationsIds(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserNotifications)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}
	/**
	 * 
	 * @param userid  user identifier
	 * @return simply return a list of user messages notifications UUID in chronological order from the oldest to the more recent
	 */
	private ArrayList<String> getUserMessagesNotificationsIds(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserMessageNotifications)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Notification> getAllNotificationByUser(String userid, int limit) throws NotificationTypeNotFoundException,	ColumnNameNotFoundException {
		ArrayList<Notification> toReturn = new ArrayList<Notification>();
		ArrayList<String> notificationsIDs = getUserNotificationsIds(userid);
		//check if quantity is greater than user feeds
		limit = (limit > notificationsIDs.size()) ? notificationsIDs.size() : limit;

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= (notificationsIDs.size()-limit); i--) {
			Notification toAdd = null;
			try {
				toAdd = readNotification(notificationsIDs.get(i));
				toReturn.add(toAdd);
			} catch (NotificationIDNotFoundException e) {
				_log.error("Notification not found id=" + notificationsIDs.get(i));
			}
		}
		return toReturn;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Notification> getUnreadNotificationsByUser(String userid) throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException {
		ArrayList<Notification> toReturn = new ArrayList<Notification>();
		ArrayList<String> notificationsIDs = getUserNotificationsIds(userid);

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= 0; i--) {
			Notification toAdd = readNotification(notificationsIDs.get(i));
			if ((!toAdd.isRead()) && (toAdd.getType() != NotificationType.MESSAGE) ) { //i just set the first notification unread to read (much faster cuz i check only the newest)
				toReturn.add(toAdd);
				break;
			}
		}
		return toReturn;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Notification> getRangeNotificationsByUser(String userid,int from, int quantity) throws NotificationTypeNotFoundException, ColumnNameNotFoundException, NotificationIDNotFoundException {
		if (from < 1) {
			throw new IllegalArgumentException("From must be greather than 0");
		} 
		ArrayList<Notification> toReturn = new ArrayList<Notification>();
		ArrayList<String> notificationsIDs = getUserNotificationsIds(userid);

		//if from is greater than feeds size return empty
		if (from >=  notificationsIDs.size()) {
			_log.warn("The starting point of the range is greather than the total number of feeds for this timeline: " + from + " >= " + notificationsIDs.size());
			return new  ArrayList<Notification>();
		}

		int rangeStart = notificationsIDs.size()-from;
		int rangeEnd = rangeStart-quantity;

		//check that you reached the end
		if (rangeEnd<1)
			rangeEnd = 0;

		_log.debug("BEFORE starting Point=" + rangeStart + " rangeEnd= " + rangeEnd);
		//need them in reverse order		
		for (int i = rangeStart; i > rangeEnd; i--) {
			Notification toAdd = readNotification(notificationsIDs.get(i));
			toReturn.add(toAdd);
		}
		return toReturn;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setAllNotificationReadByUser(String userid) throws NotificationTypeNotFoundException, ColumnNameNotFoundException {
		ArrayList<String> notificationsIDs = getUserNotificationsIds(userid);

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= 0; i--) {
			Notification toAdd;
			try {
				toAdd = readNotification(notificationsIDs.get(i));
				if ((!toAdd.isRead()) && (toAdd.getType() != NotificationType.MESSAGE) ) {  //while I encounter unread notifications keep putting them to read, else exit
					setNotificationRead(toAdd.getKey());
				}
				else {
					break;
				}
			} catch (NotificationIDNotFoundException e) {
				_log.error("Could not set read notification with id =" + notificationsIDs.get(i));
			}			
		}
		return true;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Notification> getUnreadNotificationMessagesByUser(String userid) throws NotificationTypeNotFoundException,	ColumnNameNotFoundException, NotificationIDNotFoundException {
		ArrayList<Notification> toReturn = new ArrayList<Notification>();
		ArrayList<String> notificationsIDs = getUserMessagesNotificationsIds(userid);

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= 0; i--) {
			Notification toAdd = readNotification(notificationsIDs.get(i));
			if ((!toAdd.isRead()) && (toAdd.getType() == NotificationType.MESSAGE) ) {//i just set the first message notification unread to read (much faster)
				toReturn.add(toAdd);
				break;
			}
		}
		return toReturn;	
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkUnreadNotifications(String userid) throws NotificationTypeNotFoundException, ColumnNameNotFoundException {	
		ArrayList<String> notificationsIDs = getUserNotificationsIds(userid);
		//since #readNotification costs time and newer notifications are iterarate first (with the reverse for below)
		//i just see if the first non message notification (UserNotifications TimeLine) is read or not and return the value instead of iterating them one by one looking for unread()

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= 0; i--) {
			Notification toAdd;
			try {
				toAdd = readNotification(notificationsIDs.get(i));
				if (toAdd.getType() != NotificationType.MESSAGE)
					return ! toAdd.isRead();
			} catch (NotificationIDNotFoundException e) {
				_log.error("Notification not found with id = " + notificationsIDs.get(i));
				return false;
			}			
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkUnreadMessagesNotifications(String userid) throws NotificationIDNotFoundException, NotificationTypeNotFoundException, ColumnNameNotFoundException {
		ArrayList<String> notificationsIDs = getUserMessagesNotificationsIds(userid);
		//since #readNotification costs time and newer notifications are iterarate first (with the reverse for below)
		//i just see if the first message notification (UserMessagesNotifications TL) is read or not and return the value instead of iterating them one by one looking for unread()

		//need them in reverse order		
		for (int i = notificationsIDs.size()-1; i >= 0; i--) {
			Notification toAdd = readNotification(notificationsIDs.get(i));
			if (toAdd.getType() == NotificationType.MESSAGE)
				return ! toAdd.isRead();
		}
		return false;
	}
	/*
	 * 
	 ********************** 	NOTIFICATION SETTINGS	***********************
	 *
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NotificationChannelType> getUserNotificationChannels(String userid, NotificationType notificationType) throws NotificationChannelTypeNotFoundException, NotificationTypeNotFoundException {
		_log.trace("Asking for Single Notification preference of  " + userid +  " Type: " + notificationType);
		List<NotificationChannelType> toReturn = new ArrayList<NotificationChannelType>();
		NotificationChannelType[] toProcess = getUserNotificationPreferences(userid).get(notificationType);
		if (toProcess == null) {
			_log.warn("Single Notification preference of  " + userid +  " Type: " + notificationType + " not existing ... creating default");
			return createNewNotificationType(userid, notificationType);
		}
		else if (toProcess.length == 0) 
			return toReturn;
		else
			for (int i = 0; i < toProcess.length; i++) {
				toReturn.add(toProcess[i]);
			}
		return toReturn;
	}
	/**
	 * called when you add new notification types where the setting does not exist yet
	 * please note: by default we set all notifications
	 */
	private List<NotificationChannelType> createNewNotificationType(String userid, NotificationType notificationType) {
		List<NotificationChannelType> toReturn = new ArrayList<NotificationChannelType>();
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		String valueToInsert = "";
		NotificationChannelType[] wpTypes = NotificationChannelType.values();

		for (int i = 0; i < wpTypes.length; i++) {
			valueToInsert += wpTypes[i];
			if (i < wpTypes.length-1)
				valueToInsert += ",";
			toReturn.add(wpTypes[i]); //add the new added notification type
		}
		m.withRow(cf_UserNotificationsPreferences, userid).putColumn(notificationType.toString(), valueToInsert, null);
		boolean overAllresult = execute(m);
		if (overAllresult) {
			_log.trace("Set New Notification Setting for " + userid + " OK");
			return toReturn;
		}
		return new ArrayList<NotificationChannelType>(); //no notification if sth fails
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setUserNotificationPreferences(String userid, Map<NotificationType, NotificationChannelType[]> enabledChannels) {
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();

		for (NotificationType nType : enabledChannels.keySet()) {
			String valueToInsert = "";
			int channelsNo = (enabledChannels.get(nType) != null) ? enabledChannels.get(nType).length : 0;
			for (int i = 0; i < channelsNo; i++) {
				valueToInsert += enabledChannels.get(nType)[i];
				if (i < channelsNo-1)
					valueToInsert += ",";
			}
			if (channelsNo == 0) { //in case no channels were selected
				valueToInsert = "";
				_log.trace("No Channels selected for " + nType + " by " + userid);
			}
			m.withRow(cf_UserNotificationsPreferences, userid).putColumn(nType.toString(), valueToInsert, null);
		}

		boolean overAllresult = execute(m);
		if (overAllresult)
			_log.trace("Set Notification Map for " + userid + " OK");
		else
			_log.trace("Set Notification Map for " + userid + " FAILED");
		return overAllresult;		
	}
	/**
	 * {@inheritDoc}
	 * 
	 * by default Workspace and Calendar Notifications are set to Portal
	 */
	@Override
	public Map<NotificationType, NotificationChannelType[]> getUserNotificationPreferences(String userid) throws NotificationTypeNotFoundException, NotificationChannelTypeNotFoundException {
		_log.trace("Asking for Notification preferences of  " + userid);
		Map<NotificationType, NotificationChannelType[]> toReturn = new HashMap<NotificationType, NotificationChannelType[]>();
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserNotificationsPreferences)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		//if there are no settings for this user create an entry and put all of them at true
		if (result.getResult().getRowByIndex(0).getColumns().size() == 0) {
			_log.info("Userid " + userid + " settings not found, initiating its preferences...");
			HashMap<NotificationType, NotificationChannelType[]> toCreate = new HashMap<NotificationType, NotificationChannelType[]>();

			for (int i = 0; i < NotificationType.values().length; i++) {
				//TODO: Potential bug in NotificationType for workspace are refactored
				//create a map with all notification enabled except for workspace notifications (They start with WP_) it was the only quick way
				if (NotificationType.values()[i].toString().startsWith("WP_")) {
					NotificationChannelType[] wpTypes = { NotificationChannelType.PORTAL };
					toCreate.put(NotificationType.values()[i], wpTypes);
				}
				else
					toCreate.put(NotificationType.values()[i], NotificationChannelType.values());
			}
			setUserNotificationPreferences(userid, toCreate); //commit the map

			return toCreate;
		}
		else {
			_log.trace("Notification preferences Found for  " + userid);
			for (Row<String, String> row : result.getResult()) 
				for (Column<String> column : row.getColumns()) {
					String[] channels = column.getStringValue().split(",");
					if (channels != null && channels.length == 1 && channels[0].toString().equals("") ) { //it is empty, preference is set to no notification at all
						toReturn.put(getNotificationType(column.getName()), new NotificationChannelType[0]);
					} else {
						NotificationChannelType[] toAdd = new NotificationChannelType[channels.length];		
						for (int i = 0; i < channels.length; i++) {
							if (channels[i].compareTo("") != 0) {
								toAdd[i] = (getChannelType(channels[i]));
							}
						}
						toReturn.put(getNotificationType(column.getName()), toAdd);
					}
				}
		}
		return toReturn;
	}
	/*
	 * 
	 ********************** 	COMMENTS	***********************
	 *
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addComment(Comment comment) throws FeedIDNotFoundException {
		Feed toComment = null;
		if (comment == null)
			throw new NullArgumentException("Comment must be not null");
		if (comment.getFeedid() == null)
			throw new NullArgumentException("Comment feed id must be not null");

		String feedId = comment.getFeedid();
		try {
			toComment = readFeed(feedId);
			if (toComment == null)
				throw new FeedIDNotFoundException("Could not find Feed with id " + feedId + " to associate this comment");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the Comment CF
		m.withRow(cf_Comments, comment.getKey().toString())
		.putColumn("Text", comment.getText(), null)
		.putColumn("Timestamp", comment.getTime().getTime()+"", null)
		.putColumn("Userid", comment.getUserid(), null)
		.putColumn("Feedid",comment.getFeedid(), null)
		.putColumn("FullName",comment.getFullName(), null)
		.putColumn("ThumbnailURL", comment.getThumbnailURL(), null)
		.putColumn("IsEdited", comment.isEdit(), null);

		try {
			m.execute();			
		} catch (ConnectionException e) {
			e.printStackTrace();
			return false;
		}
		//update the comment count
		boolean updateCommentNoResult = updateFeedCommentsCount(toComment, true);
		return updateCommentNoResult;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Comment> getAllCommentByFeed(String feedid) {
		List<Comment> toReturn = new ArrayList<Comment>();

		PreparedIndexExpression<String, String> clause = cf_Comments.newIndexClause().whereColumn("Feedid").equals().value(feedid);
		OperationResult<Rows<String, String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Comments)
					.searchWithIndex()
					.setStartKey("")
					.addPreparedExpressions(Arrays.asList(clause))
					.execute();

			// Iterate rows and their columns 
			for (Row<String, String> row : result.getResult()) {
				Comment toAdd = new Comment();
				toAdd.setKey(row.getKey());
				for (Column<String> col : row.getColumns()) {
					if (col.getName().compareTo("Text") == 0)
						toAdd.setText(col.getStringValue());
					else if (col.getName().compareTo("FullName") == 0)
						toAdd.setFullName(col.getStringValue());
					else if (col.getName().compareTo("Timestamp") == 0) 
						toAdd.setTime(getDateFromTimeInMillis(col.getStringValue()));
					else if (col.getName().compareTo("Userid") == 0)
						toAdd.setUserid(col.getStringValue());
					else if (col.getName().compareTo("ThumbnailURL") == 0)
						toAdd.setThumbnailURL(col.getStringValue());
					else if (col.getName().compareTo("Feedid") == 0)
						toAdd.setFeedid(col.getStringValue());
					else if(col.getName().compareTo("IsEdited") == 0)
						toAdd.setEdit(col.getBooleanValue());
					else if(col.getName().compareTo("LastEditTime") == 0)
						toAdd.setLastEditTime(getDateFromTimeInMillis(col.getStringValue()));
					else {
						_log.error("getAllCommentByFeed(): Could not assign variable to this Comment for column name: " + col.getName());
					}
				}
				toReturn.add(toAdd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean editComment(Comment comment2Edit) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException,	CommentIDNotFoundException, FeedIDNotFoundException {
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Comments, comment2Edit.getKey().toString()).putColumn("Text", comment2Edit.getText(), null);
		m.withRow(cf_Comments, comment2Edit.getKey().toString()).putColumn("IsEdited", comment2Edit.isEdit(), null);
		m.withRow(cf_Comments, comment2Edit.getKey().toString()).putColumn("LastEditTime", comment2Edit.getLastEditTime().getTime() + "", null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("Comments update NOT OK ");
			return false;
		}
		_log.info("Comments update OK to: " + comment2Edit.getText());
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteComment(String commentid, String feedid) throws PrivacyLevelTypeNotFoundException,	FeedTypeNotFoundException, ColumnNameNotFoundException,	CommentIDNotFoundException, FeedIDNotFoundException {
		Feed toUpdate = readFeed(feedid);
		boolean updateCommentNoResult = false;

		updateCommentNoResult = updateFeedCommentsCount(toUpdate, false);
		if (updateCommentNoResult) {
			MutationBatch m = conn.getKeyspace().prepareMutationBatch();
			m.withRow(cf_Comments, commentid)
			.delete();

			try {
				m.execute();
			} catch (ConnectionException e) {
				_log.error("Comment Delete FAILED for " + commentid +  " from Feed " + feedid);
				e.printStackTrace();
			}
			_log.trace("Comment Deleted " + commentid +  " from Feed " + feedid);
		}


		return updateCommentNoResult;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean like(Like like) throws FeedIDNotFoundException {
		Feed toLike = null;
		if (like == null)
			throw new NullArgumentException("Like must be not null");
		if (like.getFeedid() == null)
			throw new NullArgumentException("Like feed id must be not null");

		String feedId = like.getFeedid();
		try {
			toLike = readFeed(feedId);
			if (toLike == null)
				throw new FeedIDNotFoundException("Could not find Feed with id " + feedId + " to associate this like");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		if (isFeedLiked(like.getUserid(), feedId)) {
			_log.info("User " + like.getUserid() +  " already liked Feed " + feedId);
			return true;
		}
		else {
			// Inserting data
			MutationBatch m = conn.getKeyspace().prepareMutationBatch();
			//an entry in the feed CF
			m.withRow(cf_Likes, like.getKey().toString())
			.putColumn("Timestamp", like.getTime().getTime()+"", null)
			.putColumn("Userid", like.getUserid(), null)
			.putColumn("Feedid",like.getFeedid(), null)
			.putColumn("FullName",like.getFullName(), null)
			.putColumn("ThumbnailURL", like.getThumbnailURL(), null);
			//and an entry in the UserLikesCF
			m.withRow(cf_UserLikedFeeds, like.getUserid()).putColumn(like.getKey(), like.getFeedid(), null);

			try {
				m.execute();
			} catch (ConnectionException e) {
				e.printStackTrace();
				return false;
			}	
			return updateFeedLikesCount(toLike, true);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean unlike(String userid, String likeid, String feedid) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, LikeIDNotFoundException, FeedIDNotFoundException {
		Feed toUpdate = readFeed(feedid);
		boolean updateLikeNoResult = false;

		updateLikeNoResult = updateFeedLikesCount(toUpdate, false); //this remove 1 from the Feed CF LikeNO
		if (updateLikeNoResult) {
			MutationBatch m = conn.getKeyspace().prepareMutationBatch();
			//delete the row from LikesCF
			m.withRow(cf_Likes, likeid).delete();
			//delete the column from UserLikes
			m.withRow(cf_UserLikedFeeds, userid).deleteColumn(likeid);



			try {
				m.execute();
			} catch (ConnectionException e) {
				_log.error("Like Delete FAILED for " + likeid +  " from Feed " + feedid);
				e.printStackTrace();
			}
			_log.trace("Unlike ok for " + likeid +  " from Feed " + feedid);
		}
		return updateLikeNoResult;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAllLikedFeedIdsByUser(String userid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_UserLikedFeeds)
					.getKeySlice(userid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		ArrayList<String> toReturn = new ArrayList<String>();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				toReturn.add(column.getStringValue());
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<Feed> getAllLikedFeedsByUser(String userid, int limit) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		ArrayList<Feed> toReturn = new ArrayList<Feed>();
		List<String> likedFeedIDs = getAllLikedFeedIdsByUser(userid);

		//check if quantity is greater than user feeds
		limit = (limit > likedFeedIDs.size()) ? likedFeedIDs.size() : limit;

		//need them in reverse order		
		for (int i = likedFeedIDs.size()-1; i >= (likedFeedIDs.size()-limit); i--) {
			Feed toAdd = readFeed(likedFeedIDs.get(i));
			if (toAdd.getType() == FeedType.TWEET || toAdd.getType() == FeedType.SHARE || toAdd.getType() == FeedType.PUBLISH) {
				toReturn.add(toAdd);
				_log.trace("Read recent feed: " + likedFeedIDs.get(i));
			} else {
				_log.trace("Read and skipped feed: " + likedFeedIDs.get(i) + " (Removed Feed)");
				limit += 1; //increase the quantity in case of removed feed
				//check if quantity is greater than user feeds
				limit = (limit > likedFeedIDs.size()) ? likedFeedIDs.size() : limit;
			}
		}
		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Like> getAllLikesByFeed(String feedid) {
		List<Like> toReturn = new ArrayList<Like>();
		OperationResult<Rows<String, String>> result;
		PreparedIndexExpression<String, String> clause = cf_Likes.newIndexClause().whereColumn("Feedid").equals().value(feedid);
		try {
			result = conn.getKeyspace().prepareQuery(cf_Likes)
					.searchWithIndex()
					.setStartKey("")
					.addPreparedExpressions(Arrays.asList(clause))
					.execute();
			// Iterate rows and their columns 
			for (Row<String, String> row : result.getResult()) {
				Like toAdd = new Like();
				toAdd.setKey(row.getKey());
				for (Column<String> col : row.getColumns()) {
					if (col.getName().compareTo("FullName") == 0)
						toAdd.setFullName(col.getStringValue());
					else if (col.getName().compareTo("Timestamp") == 0) 
						toAdd.setTime(getDateFromTimeInMillis(col.getStringValue()));
					else if (col.getName().compareTo("Userid") == 0)
						toAdd.setUserid(col.getStringValue());
					else if (col.getName().compareTo("ThumbnailURL") == 0)
						toAdd.setThumbnailURL(col.getStringValue());
					else if (col.getName().compareTo("Feedid") == 0)
						toAdd.setFeedid(col.getStringValue());
					else {
						_log.error("getAllLikesByFeed(): Could not assign variable to this Like for column name: " + col.getName());
					}
				}
				toReturn.add(toAdd);
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/*
	 * 
	 ********************** 	HASHTAGS	***********************
	 *
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveHashTags(String feedid, String vreid, List<String> hashtags)	throws FeedIDNotFoundException {
		Set<String> noduplicatesHashtags = null;
		if (hashtags != null && !hashtags.isEmpty()) {
			noduplicatesHashtags = new HashSet<String>(hashtags);
		}
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		for (String hashtag : noduplicatesHashtags) {
			String lowerCaseHashtag = hashtag.toLowerCase();
			m.withRow(cf_HashtagTimeline, lowerCaseHashtag).putColumn(feedid, vreid, null);
			boolean firstInsert = execute(m);
			boolean secondInsert = updateVREHashtagCount(vreid, lowerCaseHashtag, true);
			if (! (firstInsert && secondInsert)) {
				_log.error("saveHashTags: Could not save the hashtag(s)");
				return false;
			}
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteHashTags(String feedid, String vreid, List<String> hashtags) throws FeedIDNotFoundException {
		Set<String> noduplicatesHashtags = null;
		if (hashtags != null && !hashtags.isEmpty()) {
			noduplicatesHashtags = new HashSet<String>(hashtags);
		}
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		for (String hashtag : noduplicatesHashtags) {
			String lowerCaseHashtag = hashtag.toLowerCase();
			m.withRow(cf_HashtagTimeline, lowerCaseHashtag).deleteColumn(feedid);
			boolean firstDelete = execute(m);
			boolean secondInsert = updateVREHashtagCount(vreid, lowerCaseHashtag, false);
			if (! (firstDelete && secondInsert)) {
				_log.error("deleteHashTags: Could not delete the hashtag(s)");
				return false;
			}
		}
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Integer> getVREHashtagsWithOccurrence(String vreid) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_HashtagsCounter)
					.getKeySlice(vreid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		HashMap<String, Integer> toReturn = new HashMap<String, Integer> ();

		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				int curValue = Integer.parseInt(column.getStringValue());
				if (curValue > 0)
					toReturn.put(column.getName(), curValue);
			}
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Feed> getVREFeedsByHashtag(String vreid, String hashtag) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException {
		List<Feed> toReturn = new ArrayList<Feed>();
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_HashtagTimeline)
					.getKeySlice(hashtag)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}		
		ArrayList<String> feedIds = new ArrayList<String>();
		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				if (column.getStringValue().compareTo(vreid)==0)
					feedIds.add(column.getName());
			}
		}
		toReturn = getFeedsByIds(feedIds);
		return toReturn;
	}
	/*
	 * 
	 ********************** 	Invites	***********************
	 *
	 */
	/**
	 * common part to save a invite
	 * @param invite
	 * @return the partial mutation batch instance
	 */
	private MutationBatch initSaveInvite(Invite invite) {
		if (invite == null)
			throw new NullArgumentException("Invite instance is null");
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the invite CF
		m.withRow(cf_Invites, invite.getKey().toString())
		.putColumn("SenderUserId", invite.getSenderUserId(), null)
		.putColumn("Vreid", invite.getVreid(), null)
		.putColumn("InvitedEmail", invite.getInvitedEmail(), null)
		.putColumn("ControlCode", invite.getControlCode(), null)
		.putColumn("Status", invite.getStatus().toString(), null)
		.putColumn("Time", invite.getTime().getTime()+"", null)
		.putColumn("SenderFullName", invite.getSenderFullName(), null);
		return m;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isExistingInvite(String vreid, String email) {
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_EmailInvites)
					.getKeySlice(email)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}		
		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			for (Column<String> column : row.getColumns()) {
				if (column.getName().compareTo(vreid)==0)
					return column.getStringValue();
			}
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InviteOperationResult saveInvite(Invite invite) throws AddressException {
		if (invite == null)
			throw new NullArgumentException("Invite instance is null");
		String email = invite.getInvitedEmail();
		if (! verifyEmail(email))
			throw new AddressException("Email is not valid ->" + email);
		if (invite.getVreid() == null || invite.getVreid().equals(""))
			throw new NullArgumentException("VREId is null or empty");
		_log.debug("isExistingInvite? " + invite.getInvitedEmail() + " in " + invite.getVreid());
		if (isExistingInvite(invite.getVreid(), invite.getInvitedEmail()) != null)
			return InviteOperationResult.ALREADY_INVITED;
		_log.debug("Invite not found, proceed to save it ...");

		MutationBatch m = initSaveInvite(invite);
		//an entry in the VRE Invites 
		m.withRow(cf_VREInvites, invite.getVreid())
		.putColumn(invite.getKey().toString(), InviteStatus.PENDING.toString(), null);

		//an entry in the EMAIL Invites 
		m.withRow(cf_EmailInvites, email)
		.putColumn(invite.getVreid(), invite.getKey().toString(), null);
		boolean result = execute(m);
		return result ? InviteOperationResult.SUCCESS : InviteOperationResult.FAILED;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Invite readInvite(String inviteid) throws InviteIDNotFoundException, InviteStatusNotFoundException {
		Invite toReturn = new Invite();
		OperationResult<ColumnList<String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Invites)
					.getKey(inviteid)
					.execute();

			ColumnList<String> columns = result.getResult();
			if (columns.size() == 0) {
				throw new InviteStatusNotFoundException("The requested inviteid: " + inviteid + " is not existing");
			}

			toReturn.setKey(inviteid);
			toReturn.setSenderUserId(columns.getColumnByName("SenderUserId").getStringValue());
			toReturn.setVreid(columns.getColumnByName("Vreid").getStringValue());
			toReturn.setInvitedEmail(columns.getColumnByName("InvitedEmail").getStringValue());
			toReturn.setControlCode(columns.getColumnByName("ControlCode").getStringValue());
			InviteStatus status = getInviteStatusType(columns.getColumnByName("Status").getStringValue());
			toReturn.setStatus(status);
			toReturn.setTime(getDateFromTimeInMillis(columns.getColumnByName("Time").getStringValue()));
			toReturn.setSenderFullName(columns.getColumnByName("SenderFullName").getStringValue());

		} catch (ConnectionException e) {
			e.printStackTrace();
			return null;
		}	
		return toReturn;
	}
	/**
	 * helper method that retrieve all the Invites belonging to a list of Ids
	 * @param inviteIds the lisf of invites UUID
	 * @return all the invites belonging to a list of Ids
	 * @throws InviteIDNotFoundException 
	 * @throws InviteStatusNotFoundException 
	 */
	private List<Invite> getInvitesById(List<String> inviteIds) throws InviteIDNotFoundException, InviteStatusNotFoundException {
		ArrayList<Invite> toReturn = new ArrayList<Invite>();
		for (String inviteid : inviteIds)  
			toReturn.add(readInvite(inviteid));

		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 * @throws InviteStatusNotFoundException 
	 */
	@Override
	public boolean setInviteStatus(String vreid, String email, InviteStatus status) throws InviteIDNotFoundException, InviteStatusNotFoundException {
		String inviteid = isExistingInvite(vreid, email);
		Invite toSet = readInvite(inviteid);
		if (toSet == null)
			throw new InviteIDNotFoundException("The specified invite to set with id: " + inviteid + " does not exist");

		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//update in the Invites Static CF
		m.withRow(cf_Invites, inviteid).putColumn("Status", status.toString(), null);
		//updated in the VREInvites Dynamic CF
		m.withRow(cf_VREInvites, toSet.getVreid()).putColumn(inviteid, status.toString(), null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("ERROR while setting Invite " + inviteid + " to " + status.toString());
			return false;
		}
		_log.trace("Invite Status Set to " +  status.toString() + " OK");
		return true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Invite> getInvitedEmailsByVRE(String vreid, InviteStatus... status) throws InviteIDNotFoundException, InviteStatusNotFoundException{
		OperationResult<Rows<String, String>> result = null;
		try {
			result = conn.getKeyspace().prepareQuery(cf_VREInvites)
					.getKeySlice(vreid)
					.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}		
		ArrayList<String> invitesIds = new ArrayList<String>();
		// Iterate rows and their columns 
		for (Row<String, String> row : result.getResult()) {
			if (status != null) {
				for (Column<String> column : row.getColumns()) {
					for (int i = 0; i < status.length; i++) {
						if (column.getStringValue().compareTo(status[i].toString())==0)
							invitesIds.add(column.getName());
					}

				}
			}
			else {
				for (Column<String> column : row.getColumns()) 
					invitesIds.add(column.getName());
			}
		}
		return getInvitesById(invitesIds);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Attachment> getAttachmentsByFeedId(String feedId) throws FeedIDNotFoundException {
		Feed toCheck = null;
		try {
			toCheck = readFeed(feedId);
			if (toCheck == null)
				throw new FeedIDNotFoundException("Could not find Feed with id " + feedId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		List<Attachment> toReturn = new ArrayList<Attachment>();

		PreparedIndexExpression<String, String> clause = cf_Attachments.newIndexClause().whereColumn("feedId").equals().value(feedId);
		OperationResult<Rows<String, String>> result;
		try {
			result = conn.getKeyspace().prepareQuery(cf_Attachments)
					.searchWithIndex()
					.setStartKey("")
					.addPreparedExpressions(Arrays.asList(clause))
					.execute();

			// Iterate rows and their columns 
			for (Row<String, String> row : result.getResult()) {
				Attachment toAdd = new Attachment();
				toAdd.setId(row.getKey());
				for (Column<String> col : row.getColumns()) {
					if (col.getName().compareTo("feedId") == 0)
						_log.trace("Reading attachment if feed=" + col.getStringValue());
					else if (col.getName().compareTo("uri") == 0)
						toAdd.setUri(col.getStringValue());
					else if (col.getName().compareTo("name") == 0) 
						toAdd.setName(col.getStringValue());
					else if (col.getName().compareTo("description") == 0)
						toAdd.setDescription(col.getStringValue());
					else if (col.getName().compareTo("thumbnailURL") == 0)
						toAdd.setThumbnailURL(col.getStringValue());
					else if (col.getName().compareTo("mimeType") == 0)
						toAdd.setMimeType(col.getStringValue());
					else {
						_log.error("getAttachmentsByFeedId(): Could not assign variable to this Attachment for column name: " + col.getName());
					}
				}
				toReturn.add(toAdd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return toReturn;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeConnection() {
	}
	/*
	 * 
	 ********************** 	Helper methods	***********************
	 *
	 */
	/**
	 * @param feedId the feedId to which the attachment is attached
	 * @param toSave the instance to save
	 * @return true if the attachemnt entry is saved in the Attachments CF
	 */
	private boolean saveAttachmentEntry(String feedId, Attachment toSave) {
		// Inserting data
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the Attachment CF
		m.withRow(cf_Attachments, toSave.getId())
		.putColumn("feedId", feedId, null)
		.putColumn("uri", toSave.getUri(), null)
		.putColumn("name", toSave.getName(), null)
		.putColumn("description",toSave.getDescription(), null)
		.putColumn("thumbnailURL",toSave.getThumbnailURL(), null)
		.putColumn("mimeType",toSave.getMimeType(), null);
		try {
			m.execute();			
		} catch (ConnectionException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * simply return an enum representing the privacy level
	 * @param privacyLevel .
	 * @return correct enum representing the privacy level
	 * @throws NotificationChannelTypeNotFoundException 
	 * @throws FeedTypeNotFoundException 
	 */
	private NotificationChannelType getChannelType(String channelName) throws NotificationChannelTypeNotFoundException {
		if (channelName.compareTo("PORTAL") == 0)
			return NotificationChannelType.PORTAL;
		else if (channelName.compareTo("EMAIL") == 0)
			return NotificationChannelType.EMAIL;
		else if (channelName.compareTo("TWITTER") == 0)
			return NotificationChannelType.TWITTER;
		else
			throw new NotificationChannelTypeNotFoundException("The Notification Channel Type was not recognized should be one of " + NotificationChannelType.values() + " asked for: " + channelName);
	}

	/**
	 * simply return an enum representing the privacy level
	 * @param privacyLevel .
	 * @return correct enum representing the privacy level
	 * @throws FeedTypeNotFoundException 
	 */
	private PrivacyLevel getPrivacyLevel(String privacyLevel) throws PrivacyLevelTypeNotFoundException {
		if (privacyLevel.compareTo("CONNECTION") == 0)
			return PrivacyLevel.CONNECTION;
		else if (privacyLevel.compareTo("PRIVATE") == 0)
			return PrivacyLevel.PRIVATE;
		else if (privacyLevel.compareTo("PUBLIC") == 0)
			return PrivacyLevel.PUBLIC;
		else if (privacyLevel.compareTo("VRES") == 0)
			return PrivacyLevel.VRES;
		else if (privacyLevel.compareTo("SINGLE_VRE") == 0)
			return PrivacyLevel.SINGLE_VRE;
		else if (privacyLevel.compareTo("PORTAL") == 0)
			return PrivacyLevel.PORTAL;
		else
			throw new PrivacyLevelTypeNotFoundException("The Privacy Level was not recognized should be one of " + PrivacyLevel.values() + " asked for: " + privacyLevel);
	}
	/**
	 * simply return an enum representing the feed type
	 * @param type .
	 * @return correct enum representing the feed type
	 * @throws TypeNotFoundException .
	 */
	private FeedType getFeedType(String type) throws FeedTypeNotFoundException {
		if (type.compareTo("TWEET") == 0) {
			return FeedType.TWEET;
		}
		else if (type.compareTo("JOIN") == 0) {
			return FeedType.JOIN;
		}
		else if (type.compareTo("PUBLISH") == 0) {
			return FeedType.PUBLISH;
		}
		else if (type.compareTo("SHARE") == 0) {
			return FeedType.SHARE;
		}
		else if (type.compareTo("ACCOUNTING") == 0) {
			return FeedType.ACCOUNTING;
		}
		else if (type.compareTo("DISABLED") == 0) {
			return FeedType.DISABLED;
		}
		else
			throw new FeedTypeNotFoundException("The Feed Type was not recognized should be one of " + FeedType.values() + " asked for: " + type);
	}

	/**
	 * simply return an enum representing the invite status type
	 * @param type .
	 * @return correct enum representing the feed type
	 * @throws TypeNotFoundException .
	 */
	private InviteStatus getInviteStatusType(String type) throws InviteStatusNotFoundException {
		switch (type) {
		case "PENDING":
			return InviteStatus.PENDING;
		case "ACCEPTED":
			return InviteStatus.ACCEPTED;
		case "REJECTED":
			return InviteStatus.REJECTED;
		case "RETRACTED":
			return InviteStatus.RETRACTED;
		default:
			throw new InviteStatusNotFoundException("The Invite Status was not recognized should be one of " + InviteStatus.values() + " asked for: " + type);
		}

	}

	/**
	 * simply return an enum representing the feed type
	 * @param type .
	 * @return correct enum representing the feed type
	 * @throws TypeNotFoundException .
	 */
	private NotificationType getNotificationType(String type) throws NotificationTypeNotFoundException {
		if (type.compareTo("WP_FOLDER_SHARE") == 0) {
			return NotificationType.WP_FOLDER_SHARE;
		}
		else if (type.compareTo("WP_FOLDER_UNSHARE") == 0) {
			return NotificationType.WP_FOLDER_UNSHARE;
		}
		else if (type.compareTo("WP_ADMIN_UPGRADE") == 0) {
			return NotificationType.WP_ADMIN_UPGRADE;
		}
		else if (type.compareTo("WP_ADMIN_DOWNGRADE") == 0) {
			return NotificationType.WP_ADMIN_DOWNGRADE;
		}
		else if (type.compareTo("WP_FOLDER_RENAMED") == 0) {
			return NotificationType.WP_FOLDER_RENAMED;
		}
		else if (type.compareTo("WP_FOLDER_ADDEDUSER") == 0) {
			return NotificationType.WP_FOLDER_ADDEDUSER;
		}
		else if (type.compareTo("WP_FOLDER_REMOVEDUSER") == 0) {
			return NotificationType.WP_FOLDER_REMOVEDUSER;
		}
		else if (type.compareTo("WP_ITEM_DELETE") == 0) {
			return NotificationType.WP_ITEM_DELETE;
		}
		else if (type.compareTo("WP_ITEM_UPDATED") == 0) {
			return NotificationType.WP_ITEM_UPDATED;
		}
		else if (type.compareTo("WP_ITEM_NEW") == 0) {
			return NotificationType.WP_ITEM_NEW;
		}
		else if (type.compareTo("WP_ITEM_RENAMED") == 0) {
			return NotificationType.WP_ITEM_RENAMED;
		}
		else if (type.compareTo("OWN_COMMENT") == 0) {
			return NotificationType.OWN_COMMENT;
		}
		else if (type.compareTo("COMMENT") == 0) {
			return NotificationType.COMMENT;
		}
		else if (type.compareTo("MENTION") == 0) {
			return NotificationType.MENTION;
		}
		else if (type.compareTo("LIKE") == 0) {
			return NotificationType.LIKE;
		}
		else if (type.compareTo("CALENDAR_ADDED_EVENT") == 0) {
			return NotificationType.CALENDAR_ADDED_EVENT;
		}
		else if (type.compareTo("CALENDAR_UPDATED_EVENT") == 0) {
			return NotificationType.CALENDAR_UPDATED_EVENT;
		}
		else if (type.compareTo("CALENDAR_DELETED_EVENT") == 0) {
			return NotificationType.CALENDAR_DELETED_EVENT;
		}
		else if (type.compareTo("CALENDAR_ADDED_EVENT") == 0) {
			return NotificationType.CALENDAR_ADDED_EVENT;
		}
		else if (type.compareTo("CALENDAR_UPDATED_EVENT") == 0) {
			return NotificationType.CALENDAR_UPDATED_EVENT;
		}
		else if (type.compareTo("CALENDAR_DELETED_EVENT") == 0) {
			return NotificationType.CALENDAR_DELETED_EVENT;
		}
		else if (type.compareTo("MESSAGE") == 0) {
			return NotificationType.MESSAGE;
		}
		else if (type.compareTo("POST_ALERT") == 0) {
			return NotificationType.POST_ALERT;
		}
		else if (type.compareTo("REQUEST_CONNECTION") == 0) {
			return NotificationType.REQUEST_CONNECTION;
		}
		else if (type.compareTo("JOB_COMPLETED_NOK") == 0) {
			return NotificationType.JOB_COMPLETED_NOK;
		}
		else if (type.compareTo("JOB_COMPLETED_OK") == 0) {
			return NotificationType.JOB_COMPLETED_OK;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_EDIT") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_EDIT;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_VIEW") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_VIEW;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_FORWARD_STEP_COMPLETED_OWNER") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_FORWARD_STEP_COMPLETED_OWNER;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_STEP_FORWARD_PEER") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_STEP_FORWARD_PEER;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_STEP_REQUEST_TASK") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_STEP_REQUEST_TASK;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_USER_FORWARD_TO_OWNER") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_USER_FORWARD_TO_OWNER;
		}
		else if (type.compareTo("DOCUMENT_WORKFLOW_FIRST_STEP_REQUEST_INVOLVMENT") == 0) {
			return NotificationType.DOCUMENT_WORKFLOW_FIRST_STEP_REQUEST_INVOLVMENT;
		}	
		else if (type.compareTo("TDM_TAB_RESOURCE_SHARE") == 0) {
			return NotificationType.TDM_TAB_RESOURCE_SHARE;
		}	
		else if (type.compareTo("TDM_RULE_SHARE") == 0) {
			return NotificationType.TDM_RULE_SHARE;
		}	
		else if (type.compareTo("TDM_TEMPLATE_SHARE") == 0) {
			return NotificationType.TDM_TEMPLATE_SHARE;
		}	
		else if (type.compareTo("GENERIC") == 0) {
			return NotificationType.GENERIC;
		}

		else
			throw new NotificationTypeNotFoundException("The Notification Type was not recognized should be one of " + NotificationType.values() + " asked for: " + type);
	}
	/**
	 * 
	 * @param time in milliseconds
	 * @return a Date object
	 */
	private Date getDateFromTimeInMillis(String time) {
		Long timeInMillis = Long.parseLong(time);
		Calendar toSet = Calendar.getInstance();
		toSet.setTimeInMillis(timeInMillis);
		return toSet.getTime();
	}
	/**
	 * update the feed by incrementing or decrementing by (1) the CommentsNo 
	 * used when adding or removing a comment to a feed
	 * @param toUpdate the feedid
	 * @param increment set true if you want to add 1, false to subtract 1.
	 */
	private boolean updateFeedCommentsCount(Feed toUpdate, boolean increment) {
		int newCount = 0;
		try { 
			int current = Integer.parseInt(toUpdate.getCommentsNo());
			newCount = increment ? current+1 : current-1;
		}
		catch (NumberFormatException e) {
			_log.error("Comments Number found is not a number: " + toUpdate.getCommentsNo());
		}
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Feeds, toUpdate.getKey().toString()).putColumn("CommentsNo", ""+newCount, null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("CommentsNo update NOT OK ");
			return false;
		}
		_log.info("CommentsNo update OK to: " + newCount);
		return true;
	}

	/**
	 * update the feed by incrementing or decrementing by (1) the LikesNo 
	 * used when adding or removing a comment to a feed
	 * @param toUpdate the feedid
	 * @param increment set true if you want to add 1, false to subtract 1.
	 */
	private boolean updateFeedLikesCount(Feed toUpdate, boolean increment) {
		int newCount = 0;
		try { 
			int current = Integer.parseInt(toUpdate.getLikesNo());
			newCount = increment ? current+1 : current-1;
		}
		catch (NumberFormatException e) {
			_log.error("Likes Number found is not a number: " + toUpdate.getLikesNo());
		}
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		//an entry in the feed CF
		m.withRow(cf_Feeds, toUpdate.getKey().toString()).putColumn("LikesNo", ""+newCount, null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("LikesNo update NOT OK ");
			return false;
		}
		_log.info("LikesNo update OK to: " + newCount);
		return true;
	}

	/**
	 * update the hashtag count by incrementing or decrementing it by (1) 
	 * used when adding or removing a hashtag in a feed
	 * @param vreid the vreid
	 * @param hashtag the hashtag
	 * @param increment set true if you want to add 1, false to subtract 1.
	 */
	private boolean updateVREHashtagCount(String vreid, String hashtag, boolean increment) {
		Map<String, Integer> vreHashtags = getVREHashtagsWithOccurrence(vreid);
		//if the hashtag not yet exist 
		int newCount = 0;

		if (!vreHashtags.containsKey(hashtag)) {
			newCount = 1;
		}
		else {
			try { 
				int current = vreHashtags.get(hashtag);
				newCount = increment ? current+1 : current-1;
			}
			catch (NumberFormatException e) {
				_log.error("Hashtag Number found is not a number: " + newCount);
			}
		}
		_log.debug("Updating counter for " + hashtag + " to " + newCount);
		MutationBatch m = conn.getKeyspace().prepareMutationBatch();
		m.withRow(cf_HashtagsCounter, vreid).putColumn(hashtag, ""+newCount, null);
		try {
			m.execute();
		} catch (ConnectionException e) {
			_log.error("Hashtag Count update NOT OK ");
			return false;
		}
		_log.debug("Hashtag Count update OK to: " + newCount);
		return true;
	}
	/**
	 * verify an email address
	 * @param email
	 * @return true or false
	 */
	private boolean verifyEmail(String email) {
		boolean isValid = false;
		try {
			InternetAddress internetAddress = new InternetAddress(email);
			internetAddress.validate();
			isValid = true;
		} catch (AddressException e) {
			_log.error("Validation Exception Occurred for email: " + email);
		}
		return isValid;
	}







}
