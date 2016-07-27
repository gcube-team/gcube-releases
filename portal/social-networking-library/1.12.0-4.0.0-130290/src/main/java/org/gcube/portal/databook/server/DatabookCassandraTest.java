package org.gcube.portal.databook.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.PrivacyLevelTypeNotFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabookCassandraTest {
	private static DBCassandraAstyanaxImpl store;

	@BeforeClass
	public static void setup() throws Exception {
		store = new DBCassandraAstyanaxImpl(); //set to true if you want to drop the KeySpace and recreate it
	}

	@AfterClass
	public static void close(){
		store.closeConnection();
		System.out.println("End");

	}

	//	@Test
	//	public void getHashTagsFilteredByTime() throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException{
	//
	//		int windowSize = 6; // go back windowSize months
	//
	//		String vreUnderTest = "/gcube/devsec/devVRE";
	//
	//		// reference time 
	//		Calendar calendar = Calendar.getInstance();
	//		int currentMonth = calendar.get(Calendar.MONTH); // jan = 0, ..... dec = 11
	//		calendar.set(Calendar.MONTH, currentMonth -  windowSize); // the year is automatically decreased if needed
	//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	//		System.out.println("Reference time for trending topics is " + format.format(calendar.getTime()));
	//
	//		Map<String, Integer> res = 
	//				store.getVREHashtagsWithOccurrenceFilteredByTime(
	//						vreUnderTest, calendar.getTimeInMillis());
	//
	//		// find max score inside the list (counter)
	//		int max = 0;
	//		for(Entry<String, Integer> entry : res.entrySet()){
	//
	//			max = max < entry.getValue() ? entry.getValue() : max;
	//
	//		}
	//
	//		// normalize
	//		Map<String, Double> normalized = new HashMap<String, Double>();
	//		for(Entry<String, Integer> entry : res.entrySet()){
	//
	//			normalized.put(entry.getKey(), (double)entry.getValue() / (double)max);
	//
	//		}
	//
	//		// create the weight for each entry as:
	//		// w = 0.6 * normalized_score + 0.4 * freshness
	//		// freshness is evaluated as (window_size - latest_feed_for_hashtag_in_window_month)/window_size
	//		Map<String, Double> scoredList = new HashMap<String, Double>();
	//		for(Entry<String, Integer> entry : res.entrySet()){
	//
	//			double weight = 0.6 * normalized.get(entry.getKey());
	//
	//			// retrieve the last feed for this hashtag and locate it into the window
	//			List<Feed> mostRecentFeedForHashtag = store.getVREFeedsByHashtag(vreUnderTest, entry.getKey());
	//
	//			// retrieve the most recent one among these feeds
	//			Collections.sort(mostRecentFeedForHashtag, Collections.reverseOrder());
	//
	//			// locate into the window
	//			Calendar locateInWindow = Calendar.getInstance();
	//			locateInWindow.setTimeInMillis(mostRecentFeedForHashtag.get(0).getTime().getTime());
	//
	//			// get the month
	//			int sub = currentMonth - locateInWindow.get(Calendar.MONTH);
	//			int value = sub >= 0? sub : 12 - Math.abs(sub);
	//			double freshness = (double)(windowSize - value) / (double)(windowSize);
	//			System.out.println("freshness is " + freshness + " because the last feed has month " + locateInWindow.get(Calendar.MONTH));
	//
	//			weight += 0.4 * freshness;
	//
	//			scoredList.put(entry.getKey(), weight);
	//		}
	//
	//		// print sorted
	//		Map<String, Double> scoredListSorted = sortByValue(scoredList);
	//		for(Entry<String, Double> entry : scoredListSorted.entrySet()){
	//
	//			System.out.println("[hashtag=" + entry.getKey() + " , weight="  + entry.getValue() + "]");
	//		}
	//	}
	//
	//	public static <K, V extends Comparable<? super V>> Map<K, V> 
	//	sortByValue( Map<K, V> map )
	//	{
	//		List<Map.Entry<K, V>> list =
	//				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	//		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	//				{
	//			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	//			{
	//				return (o2.getValue()).compareTo( o1.getValue() );
	//			}
	//				});
	//
	//		Map<K, V> result = new LinkedHashMap<K, V>();
	//		for (Map.Entry<K, V> entry : list)
	//		{
	//			result.put( entry.getKey(), entry.getValue() );
	//		}
	//		return result;
	//	}


	//	@Test
	//	public void getHashTags() throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, FeedIDNotFoundException, ColumnNameNotFoundException{
	//		List<Feed> resList = store.getVREFeedsByHashtag("/gcube/devsec/devVRE", "#test");
	//
	//		for (Feed feed : resList) {
	//			System.out.println(feed.getTime());
	//		}
	//
	//	}

	//	@Test
	//	public void getComment(){
	//
	//		String uuid = "820969b2-4632-4197-9fd6-5aafab781faa";
	//
	//		Comment c;
	//		try {
	//			c = store.readCommentById(uuid);
	//			System.err.println(c);
	//		} catch (CommentIDNotFoundException e) {
	//			// TODO Auto-generated catch block
	//			System.err.println(e.toString());
	//		}
	//	}

	//	@Test
	//	public void vreIds(){
	//		
	//		try {
	//			List<String> ids = store.getAllVREIds();
	//			System.out.println(ids);
	//		} catch (ConnectionException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//	}

	//	@Test
	//	public void testFeedNumberPerUser() {
	//		String userid = "massimiliano.assante";
	//		
	//		List<Feed> feeds = null;
	//		int numComment = 0;
	//		long init = System.currentTimeMillis();
	//		try {
	//			feeds = store.getAllFeedsByUser(userid);
	//			
	//			for (Feed feed : feeds) {
	//				List<Comment> comments = store.getAllCommentByFeed(feed.getKey());
	//				
	//				
	//				for (Comment comment : comments) {
	//					 numComment ++;
	//				}
	//			}
	//			
	//		} catch (PrivacyLevelTypeNotFoundException | FeedTypeNotFoundException
	//				| ColumnNameNotFoundException | FeedIDNotFoundException e) {
	//			// TODO Auto-generated catch block
	//			System.err.println(e.toString());
	//		}
	//		long end = System.currentTimeMillis();
	//		System.err.println("retrieved " + feeds.size() + " and " + numComment + " in " + (end - init) + "ms");
	//	}

	//		@Test
	//		public void testAttachments() {
	//			Attachment a1 = new Attachment(UUID.randomUUID().toString(), "www1", "gattino1", "description1", "http://cdn.tuttozampe.com/wp-content/uploads/2010/09/ipoglicemia-gatto.jpg", "image/jpg");
	//			Attachment a2 = new Attachment(UUID.randomUUID().toString(), "www2", "name2", "description2", "http://www.gcomegatto.it/wp-content/uploads/2015/01/09gatto.jpg","image/jpg");
	//			Attachment a3 = new Attachment(UUID.randomUUID().toString(), "www3", "name3", "description3", "http://cdn.tuttozampe.com/wp-content/uploads/2010/09/ipoglicemia-gatto.jpg","image/jpg");
	//			List<Attachment> toPass = new ArrayList<Attachment>();
	//			toPass.add(a1);
	//			toPass.add(a2);
	//			toPass.add(a3);
	//		
	//			String feedId = UUID.randomUUID().toString();
	//			Feed feed = new Feed(feedId, FeedType.TWEET, "massimiliano.assante", new Date(), "/gcube/devsec/devVRE", 
	//					"http://www.dailybest.it/wp-content/uploads/2015/10/gattini-nele-ciotole-e1344352237289.jpg", 
	//					"http://www.dailybest.it/wp-content/uploads/2015/10/gattini-nele-ciotole-e1344352237289.jpg", 
	//					"This post has attachments (gattini) ", PrivacyLevel.SINGLE_VRE, 
	//					"Massimiliano Assante", 
	//					"massimiliano.assante@isti.cnr.it", 
	//					"http://www.dailybest.it/wp-content/uploads/2015/10/gattini-nele-ciotole-e1344352237289.jpg", 
	//					"Gattino", 
	//					"linkDesc", 
	//					"image/jpeg", false);
	//			feed.setMultiFileUpload(true);
	//			assertTrue(store.saveUserFeed(feed, toPass));
	//			System.out.println("Wrote post? ");
	//			System.out.println("Feed has the following attachments: ");
	//			try {
	//				for (Attachment at : store.getAttachmentsByFeedId(feedId)) {
	//					System.out.println(at);
	//				}
	//			} catch (FeedIDNotFoundException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			
	//		}

	//	@Test
	//	public void testHashTag() {
	//		try {	
	//			final String VREID = "/gcube/devsec/devVRE";
	//			final String HASHTAG1 = "#testHashTag";
	//			final String HASHTAG2 = "#testHashTag3";
	//			List<String> hashtags = new LinkedList<String>();
	//			hashtags.add(HASHTAG1);
	//			hashtags.add(HASHTAG2);
	//			
	////			Feed feed = new Feed(UUID.randomUUID().toString(), FeedType.TWEET, "massimiliano.assante", new Date(), VREID, 
	////					"www.d4science.org/monitor", "thumbUri", "This is a feed with " + HASHTAG1 + " and " + HASHTAG2, PrivacyLevel.VRES, "Massimiliano Assante", "massimiliano.assante@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host");
	////			assertTrue(store.saveUserFeed(feed));
	////			assertTrue(store.saveHashTags(feed.getKey(), VREID, hashtags));
	////			assertTrue(store.deleteHashTags("d0c64e42-9616-4e24-a65a-7a63a280d676", VREID, hashtags));
	////			System.out.println(feed);
	////			
	//			System.out.println("\ngetting getVREHashtagsWithOccurrence for " + VREID);
	//			Map<String, Integer> hashtagsWithOcc = store.getVREHashtagsWithOccurrence(VREID);
	//			for (String hashtag : hashtagsWithOcc.keySet()) {
	//				System.out.println(hashtag + ":" + hashtagsWithOcc.get(hashtag));
	//			}
	//			
	//			System.out.println("\ngetting getVREFeedsByHashtag for " + VREID + " and " + HASHTAG1);
	//			for (Feed theFeed : store.getVREFeedsByHashtag(VREID, HASHTAG1)) {
	//				System.out.println(theFeed);
	//			}
	//			
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		
	//		
	//	}


	//	/**
	//	 * use exclusively to add a new (Static) CF to a keyspace with a secondary index
	//	 */
	//	@Test
	//	public void addAttachmentStaticColumnFamilies() {
	//		ColumnFamily<String, String> CF_ATTACHMENTS = ColumnFamily.newColumnFamily(DBCassandraAstyanaxImpl.ATTACHMENTS, StringSerializer.get(), StringSerializer.get());
	//
	//		try {
	//			String colNameToIndex = "feedId";
	//			new CassandraClusterConnection(false).getKeyspace().createColumnFamily(CF_ATTACHMENTS, ImmutableMap.<String, Object>builder()
	//							.put("column_metadata", ImmutableMap.<String, Object>builder()
	//								.put(colNameToIndex, ImmutableMap.<String, Object>builder()
	//									.put("validation_class", "UTF8Type")
	//									.put("index_name",       "FeedIndex_"+UUID.randomUUID().toString().substring(0,5))
	//									.put("index_type",       "KEYS")
	//									.build())
	//								.build())
	//							.build());
	//
	//
	//		} catch (ConnectionException e) {
	//			e.printStackTrace();
	//		}
	//		System.out.println("addStaticColumnFamily END");
	//	}


	//		/**
	//		 * use exclusively to add a new (Dynamic) CF to a keyspace
	//		 */
	//		@Test
	//		public void addInvitesDynamicColumnFamilies() {
	//			System.out.println("UserNotificationsUnread");
	//			ColumnFamily<String, String> cf_UserNotificationsUnreadTimeline =	new ColumnFamily<String, String>(
	//					DBCassandraAstyanaxImpl.USER_NOTIFICATIONS_UNREAD,  // Column Family Name
	//					StringSerializer.get(),   // Key Serializer
	//					StringSerializer.get());  // Column Serializer
	//	
	//			try {
	//			
	//				new CassandraClusterConnection(false).getKeyspace().createColumnFamily(cf_UserNotificationsUnreadTimeline, ImmutableMap.<String, Object>builder()
	//						.put("default_validation_class", "UTF8Type")
	//						.put("key_validation_class",     "UTF8Type")
	//						.put("comparator_type",          "UTF8Type")
	//						.build());
	//				
	//			} catch (ConnectionException e) {
	//				e.printStackTrace();
	//			}
	//			System.out.println("UserNotificationsUnread END");
	//		}


	//	private List<String> getKeys() {
	//		List<String> toReturn = new ArrayList<String>();
	//		try {
	//			
	//		    OperationResult<Rows<String, String>> rows = store.getConnection().getKeyspace().prepareQuery(DBCassandraAstyanaxImpl.cf_UserNotificationsPreferences)
	//			.getAllRows()
	//			.setRowLimit(1000)  // This is the page size
	//			.execute();
	//		    int i = 1;
	//		    for (Row<String, String> row : rows.getResult()) {
	//		    	System.out.println(i+" ROW: " + row.getKey() + " " + row.getColumns().size());
	//		    	toReturn.add(row.getKey());
	//		    	i++;
	//		    }
	//		} catch (ConnectionException e) {
	//		    e.printStackTrace();
	//		}
	//		return toReturn;
	//	}
	//	
	//	@Test
	//	public void testUserNotificationPreferences() {	
	//		System.out.println("Notification type" + NotificationType.POST_ALERT.toString() +" OFF for:");
	//		try {
	//			for (String user : getKeys()) {
	//				List<NotificationChannelType> channels = store.getUserNotificationChannels(user, NotificationType.POST_ALERT);
	//				if (channels.isEmpty()) {
	//					System.out.println(user);
	//				}
	//				else if (! channels.contains(NotificationChannelType.EMAIL)) {
	//					System.out.println(user + "->" + channels.toString());
	//				}
	//			}


	//			for (NotificationChannelType channel : store.getUserNotificationChannels("roberto.trasarti", NotificationType.POST_ALERT)) {
	//				System.out.println(channel);
	//			}
	//		} catch (NotificationChannelTypeNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (NotificationTypeNotFoundException e) {
	//			e.printStackTrace();
	//		};
	//		
	//	}

	//	@Test
	//	public void testLikes() {
	//		int count = 10;
	//		Feed feed = new Feed(UUID.randomUUID().toString(), FeedType.SHARE, "massimiliano.assante", new Date(), "/gcube/devsec/devVRE", 
	//				"http://www.d4science.org/monitor", "thumbUri", "This feed is Liked ", PrivacyLevel.PUBLIC, 
	//				"Massimiliano Assante", "massimiliano.assante@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host", false);
	//		assertTrue(store.saveUserFeed(feed));
	//		Like toUnlike = new Like(UUID.randomUUID().toString(),"massimiliano.assante", 
	//				new Date(), feed.getKey().toString(), "Massi Pallino", "thumbUrl");
	//
	//		try {
	//			assertTrue(store.like(toUnlike));
	//			for (int i = 0; i < count; i++) 
	//				assertTrue(store.like(new Like(UUID.randomUUID().toString(),"massimiliano.assante", 
	//						new Date(), feed.getKey().toString(), "Rino Pallino", "thumbUrl")));
	//
	//			System.out.println("massimiliano.assante liked the following feeds: ");
	//			for (String feedid : store.getAllLikedFeedIdsByUser("massimiliano.assante")) {
	//				System.out.println(feedid);
	//			}
	//
	//			for (Like like : store.getAllLikesByFeed(feed.getKey().toString())) {
	//				System.out.println(like);
	//			}	
	//			System.out.println("massimiliano.assante trying unlike the following feed: " + toUnlike);
	//			store.unlike("massimiliano.assante", toUnlike.getKey(), toUnlike.getFeedid());
	//
	//		} catch (Exception e) {
	//			System.out.println("Exception feed id not found");
	//		}
	//	}
	//	/**
	//	 * use exclusively to add a new CF to a keyspace
	//	 */
	//	@Test
	//	public void addNotifPreferencesColumnFamily() {
	//		//		ColumnFamily<String, String> cf_UserNotificationsPreferences =	new ColumnFamily<String, String>(
	//		//				DBCassandraAstyanaxImpl.USER_NOTIFICATIONS_PREFERENCES,  // Column Family Name
	//		//				StringSerializer.get(),   // Key Serializer
	//		//				StringSerializer.get());  // Column Serializer
	//		//			
	//		//		try {
	//		//			new CassandraClusterConnection(false).getKeyspace().createColumnFamily(cf_UserNotificationsPreferences, ImmutableMap.<String, Object>builder()
	//		//			        .put("default_validation_class", "UTF8Type")
	//		//			        .put("key_validation_class",     "UTF8Type")
	//		//			        .put("comparator_type",          "UTF8Type")
	//		//			        .build());
	//		//		} catch (ConnectionException e) {
	//		//			e.printStackTrace();
	//		//		}
	//	}
	//
	//	@Test
	//	public void testFriendships() {
	//		assertTrue(store.requestFriendship("massimiliano.assante", "leonardo.candela"));
	//		assertTrue(store.requestFriendship("massimiliano.assante", "ermit"));
	//		assertTrue(store.requestFriendship("massimiliano.assante", "giorgino"));
	//		assertTrue(store.requestFriendship("barabba", "massimiliano.assante"));
	//
	//		assertTrue(store.approveFriendship("leonardo.candela", "massimiliano.assante"));
	//		assertTrue(store.approveFriendship("ermit", "massimiliano.assante"));
	//
	//		assertTrue(store.denyFriendship("giorgino", "massimiliano.assante"));
	//		System.out.println("Pending Connections for massimiliano.assante:");
	//		for (String userid: store.getPendingFriendRequests("massimiliano.assante")) {
	//			System.out.println(userid);
	//		}
	//
	//		System.out.println("Connections for massimiliano.assante:");
	//		for (String userid: store.getFriends("massimiliano.assante")) {
	//			System.out.println(userid);
	//		}
	//
	//	}
	//	@Test
	//	public void testLikedFeedsRetrieval() {
	//		try {
	//			for (Feed feed : store.getAllLikedFeedsByUser("luca.frosini", 10)) {
	//				System.out.println(feed);
	//			}
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} 
	//	}
	//
	//
	//	
	//
	//	@Test
	//	public void testSingleNotification() {
	//		Notification not = new Notification(
	//				UUID.randomUUID().toString(), 
	//				NotificationType.LIKE, 
	//				"leonardo.candela", 
	//				"MESSAGEID", 
	//				new Date(), 
	//				"uri", 
	//				"This is notification about a like", 
	//				false, 
	//				"leonardo.candela", "Leonardo Candela",
	//				"thumburl");
	//		assertTrue(store.saveNotification(not));
	//
	//		not = new Notification(
	//				UUID.randomUUID().toString(), 
	//				NotificationType.MESSAGE, 
	//				"massimiliano.assante", 
	//				"MESSAGEID", 
	//				new Date(), 
	//				"uri", 
	//				"This is notification about a like", 
	//				false, 
	//				"antonio.gioia", "Antonio Gioia",
	//				"thumburl");
	//		assertTrue(store.saveNotification(not));
	//		System.out.println("Writing one Notification " +  not);
	//	}
	//
	//	@Test
	//	public void testNotifications() {
	//		Notification not = null;
	//		System.out.println("Writing 18 Notifications");
	//		int count = 18;
	//		for (int i = 0; i < count; i++) {
	//			if (i % 2 != 0) {
	//				not = new Notification(UUID.randomUUID().toString(), NotificationType.JOB_COMPLETED_OK, 
	//						"leonardo.candela",  "TWEETID", new Date(), "uri", "This is notification about job completed OK #"+i, false, "pasquale.pagano", "Pasquale Pagano", "thumburl");
	//			} else {
	//				not = new Notification(UUID.randomUUID().toString(), NotificationType.JOB_COMPLETED_NOK, 
	//						"massimiliano.assante", "MESSAGEID", new Date(), "uri", "This is notification about completed NOK #"+i, false, "leonardo.candela", "Leonardo Candela", "thumburl");
	//			}
	//			assertTrue(store.saveNotification(not));
	//			try {
	//				Thread.sleep(150);
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		Notification rNot= null;
	//		try {
	//
	//			//read
	//			rNot = store.readNotification(not.getKey().toString());
	//			assertNotNull(rNot);
	//			System.out.println("Reading one Notification " +  rNot.getKey() + ": " + rNot.getDescription() + " Type: " + rNot.getType());
	//
	//			//set Read
	//			assertTrue(store.setNotificationRead(rNot.getKey().toString()));
	//
	//			System.out.println("Notification " +  rNot.getKey() + " of Type: " + rNot.getType() + " was set to READ");
	//
	//			not = new Notification(UUID.randomUUID().toString(), NotificationType.LIKE, 
	//					"leonardo.candela", "FEEDID", new Date(), "uri", "This is notification of a Liked Leo feed by Massi", false, "massimiliano.assante", "Massimiliano Assante", "thumburl");
	//			assertTrue(store.saveNotification(not));
	//			try {
	//				Thread.sleep(150);
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			rNot = store.readNotification(not.getKey().toString());
	//			System.out.println("Reading another Notification " +  rNot.getKey() + " of Type: " + rNot.getType() + " Read:? " + rNot.isRead());
	//			//			//set Read
	//			//			assertTrue(store.setNotificationRead(rNot.getKey().toString()));
	//			//			System.out.println("Notification " +  rNot.getKey() + " of Type: " + rNot.getType() + " was set to READ subject was this: " + rNot.getSubjectid());
	//			//			
	//			Random randomGenerator = new Random();
	//
	//			System.out.println("leonardo.candela Notifications: ");
	//			List<Notification> recentNots =  store.getAllNotificationByUser("leonardo.candela",  randomGenerator.nextInt(50));
	//			assertNotNull(recentNots);
	//			for (Notification notif :recentNots)
	//				System.out.println(notif);
	//
	//
	//			System.out.println("massimiliano.assante Notifications: ");
	//			recentNots =  store.getUnreadNotificationsByUser("massimiliano.assante");
	//			assertNotNull(recentNots);
	//			for (Notification notif :recentNots)
	//				System.out.println(notif);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} 
	//
	//		System.out.println("getRangeNotificationsByUser massimiliano.assante: ");
	//		try {
	//			int from = 0;
	//			for (int i = 0; i < 5; i++) {
	//				System.out.println("\nFROM="+from);
	//				List<Notification> range = store.getRangeNotificationsByUser("massimiliano.assante", from, 50);
	//				for (Notification notification : range) {
	//					System.out.println(notification.getDescription());
	//					from = 1+i * 50;
	//				}
	//			}
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} 
	//	}
	//
	//
	//	@Test
	//	public void testFeeds() {
	//		int count = 18;
	//		Feed feed = null;
	//		for (int i = 0; i < count; i++) {
	//			if (i % 2 != 0) {
	//				feed = new Feed(UUID.randomUUID().toString(), FeedType.JOIN, "massimiliano.assante", new Date(), "/gcube/devsec/devVRE", 
	//						"www.d4science.org/monitor", "thumbUri", "This is feed# "+ i, PrivacyLevel.VRES, "Massimiliano Assante", "massimiliano.assante@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host");
	//			} else {
	//				feed = new Feed(UUID.randomUUID().toString(), FeedType.TWEET, "leonardo.candela", new Date(), "", 
	//						"www.d4science.org/web/guest", "thumbUri", "This is feed# "+ i, PrivacyLevel.PORTAL, "Leonardo Candela", "leonardo.candela@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host");
	//			}
	//			assertTrue(store.saveUserFeed(feed));
	//			try {
	//				Thread.sleep(150);
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		Feed rFeed = null;
	//		try {
	//			rFeed = store.readFeed(feed.getKey().toString());
	//			rFeed = store.readFeed(feed.getKey().toString());
	//			rFeed = store.readFeed(feed.getKey().toString());
	//			rFeed = store.readFeed(feed.getKey().toString());
	//			assertNotNull(rFeed);
	//
	//			String feedIdToDelete = UUID.randomUUID().toString();
	//			feed = new Feed(feedIdToDelete, FeedType.PUBLISH, "massimiliano.assante", new Date(), "/gcube/devsec/devVRE", 
	//					"www.d4science.org/monitor", "thumbUri", "This is feed to be deleted", PrivacyLevel.VRES, "Massimiliano Assante", "massimiliano.assante@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host", false);
	//			assertTrue(store.saveUserFeed(feed));
	//			try {
	//				Thread.sleep(250);
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			System.out.println("Test Delete Feed ");
	//			assertTrue(store.deleteFeed(feedIdToDelete));
	//
	//			System.out.println("massimiliano.assante ALL FEEDS: ");
	//			for (Feed recFeed : store.getAllFeedsByUser("massimiliano.assante"))
	//				System.out.println(recFeed);
	//		}		
	//		catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//
	//
	//		@Test
	//		public void testComments() {
	//			int count = 10;
	//			Feed feed = new Feed(UUID.randomUUID().toString(), FeedType.SHARE, "massimiliano.assante", new Date(), "/gcube/devsec/devVRE", 
	//					"http://www.d4science.org/monitor", "thumbUri", "This is feed that is going to be commented ", PrivacyLevel.PUBLIC, "Massimiliano Assante", 
	//					"massimiliano.assante@isti.cnr.it", "thumburl", "linkTitle", "linkDesc", "host", false);
	//			assertTrue(store.saveUserFeed(feed));
	//	
	//			Comment toDelete = null;
	//			for (int i = 0; i < count; i++) {
	//				try {
	//					toDelete = new Comment(UUID.randomUUID().toString(),"leonardo.candela", 
	//							new Date(), feed.getKey().toString(), "This comment #"+i, "Leonardo Candela", "thumbUrl");
	//					assertTrue(store.addComment(toDelete));
	//	
	//				} catch (FeedIDNotFoundException e) {
	//					System.out.println("Exception feed id not found");
	//				}			
	//			}
	//			System.out.println("GetAllCOmmentsByFeed ");
	//			for (Comment cm : store.getAllCommentByFeed(feed.getKey().toString())) {
	//				try {
	//					System.out.println(store.readCommentById(cm.getKey()));
	//				} catch (CommentIDNotFoundException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			};
	//	
	//			try {
	//				assertTrue(store.deleteComment(toDelete.getKey(), toDelete.getFeedid()));
	//			} catch (Exception e) {
	//				System.out.println("Exception feed id not found");
	//			}
	//		}


}
