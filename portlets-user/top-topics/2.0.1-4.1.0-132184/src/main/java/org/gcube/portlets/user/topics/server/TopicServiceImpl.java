package org.gcube.portlets.user.topics.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portlets.user.topics.client.TopicService;
import org.gcube.portlets.user.topics.shared.HashTagAndOccurrence;
import org.gcube.portlets.user.topics.shared.HashtagsWrapper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Massimiliano Assante, ISTI-CNR
 */
@SuppressWarnings("serial")
public class TopicServiceImpl extends RemoteServiceServlet implements TopicService {
	private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

	public static final String TEST_USER = "test.user";
	private static final String TEST_SCOPE = "/gcube/devsec/devVRE";
	private static final int WINDOW_SIZE_IN_MONTHS = 6; // it must not exceed 12

	/**
	 * The Cassandra store interface
	 */
	private DatabookStore store;

	/**
	 * connect to cassandra at startup
	 */
	public void init() {
		store = new DBCassandraAstyanaxImpl();	
	}

	/**
	 * close connection to cassandra at shutdown
	 */
	public void destroy() {
		store.closeConnection();
	}
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			logger.warn("USER IS NULL setting test.user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = TEST_USER;
		//		user = "massimiliano.assante";
		return user;
	}

	/**
	 * return trending hashtags
	 */
	@Override
	public HashtagsWrapper getHashtags() {
		ArrayList<String> hashtagsChart = new ArrayList<String>();
		ASLSession session = getASLSession();
		String userName = session.getUsername();
		String currentScope = session.getScope();
		boolean isInfrastructure = isInfrastructureScope();

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(TEST_USER) == 0) {
			logger.debug("Found " + userName + " returning nothing");
			return null;
		}

		long timestampStart = System.currentTimeMillis();

		// get the reference time 
		Calendar referenceTime = Calendar.getInstance();
		int currentMonth = referenceTime.get(Calendar.MONTH); // jan = 0, ..... dec = 11
		referenceTime.set(Calendar.MONTH, currentMonth -  WINDOW_SIZE_IN_MONTHS); // the year is automatically decreased if needed

		// print it
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		logger.debug("Reference time for trending topics is " + format.format(referenceTime.getTime()));

		try {

			ArrayList<HashTagAndOccurrence> toSort = new ArrayList<HashTagAndOccurrence>();

			if (isInfrastructure) {
				logger.debug("****** retrieving hashtags for user VREs and site");

				// different vres could have a same hashtag, we need to merge them
				Map<String, Integer> hashtags = new HashMap<String, Integer>();

				// we need a map for the couple <hashtag, vre in which it is present>
				// it is needed because later we need to retrieve the most recent feed among the ones
				// containing the hashtag itself
				Map<String, List<String>> hashtagsInVres = new HashMap<String, List<String>>();

				GroupManager gm = new LiferayGroupManager();
				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(userName);

				Set<GCubeGroup> vresInPortal = gm.listGroupsByUserAndSite(user.getUserId(), getThreadLocalRequest().getServerName());
				logger.debug("Contexts in this site are per user " + vresInPortal);

				List<String> contexts = new ArrayList<String>();

				// get the scopes associated with such groups
				for (GCubeGroup gCubeGroup : vresInPortal) {
					contexts.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));
				}

				for (String context : contexts) {					
					Map<String, Integer> map = store.getVREHashtagsWithOccurrenceFilteredByTime(context, referenceTime.getTimeInMillis());

					// merge the values if needed
					for (String hashtag : map.keySet()) {

						if(hashtags.containsKey(hashtag)){

							int currentValue = hashtags.get(hashtag);
							int newValue = currentValue + map.get(hashtag);

							// remove and re-add
							hashtags.remove(hashtag);
							hashtags.put(hashtag, newValue);

							// get the current list of vres in which the hashtag is present and add this new one
							List<String> vres = hashtagsInVres.get(hashtag);
							vres.add(context);
							hashtagsInVres.put(hashtag, vres);

						}else{

							hashtags.put(hashtag, map.get(hashtag));

							// put in the hashmap hashtagsInVres too
							List<String> vres = new ArrayList<String>();
							vres.add(context);
							hashtagsInVres.put(hashtag, vres);
						}
					}
				}

				// now we need to evaluate score for each element
				Map<String, Double> weights = evaluateWeight(hashtags, WINDOW_SIZE_IN_MONTHS, currentMonth, referenceTime, null, hashtagsInVres);

				// at the end build the list
				for (String hashtag : hashtags.keySet()) {
					toSort.add(new HashTagAndOccurrence(hashtag, hashtags.get(hashtag), weights.get(hashtag)));
				}

			}
			else {
				logger.debug("****** retrieving hashtags for scope " + currentScope);
				Map<String, Integer> hashtags = store.getVREHashtagsWithOccurrenceFilteredByTime(currentScope, referenceTime.getTimeInMillis());
				// now we need to evaluate the weight for each element
				Map<String, Double> weights = evaluateWeight(hashtags, WINDOW_SIZE_IN_MONTHS, currentMonth, referenceTime, currentScope, null);
				for (String hashtag : hashtags.keySet()) {
					toSort.add(new HashTagAndOccurrence(hashtag, hashtags.get(hashtag), weights.get(hashtag)));
				}
			}

			logger.debug("Number of topics retrieved is " + toSort.size());

			Collections.sort(toSort); // sort for weight

			for (HashTagAndOccurrence wrapper : toSort) {

				logger.debug("Entry is " + wrapper.toString() + " with weight " + wrapper.getWeight());

				String hashtag = wrapper.getHashtag();

				String href="\"?"+
						new String(Base64.encodeBase64(GCubeSocialNetworking.HASHTAG_OID.getBytes()))+"="+
						new String(Base64.encodeBase64(hashtag.getBytes()))+"\"";
				String hashtagLink = "<a class=\"topiclink\" href=" + href + ">"+hashtag+"</a>";
				hashtagsChart.add(hashtagLink);
			}
		}
		catch (Exception e) {
			logger.error("Error while retrieving hashtags ", e);
			return null;
		}

		long timestampEnd = System.currentTimeMillis() - timestampStart;
		logger.debug("Overall time to retrieve hastags is " + timestampEnd + "ms");

		return new HashtagsWrapper(isInfrastructure, hashtagsChart);
	}


	/**
	 * Evaluate the weight for each element as w = 0.6 * s + 0.4 * f
	 * where s is the score: a normalized value given by counter_i / counter_max
	 * f is the freshness: evaluated taking into account the most recent feed containing that hashtag into the window w (that is, 
	 * the period taken into account)
	 * @param hashtags
	 * @param hashtagsInVres (present if vreid is null)
	 * @param window size
	 * @param current month
	 * @param referenceTime
	 * @param vreid (present if hashtagsInVres is null)
	 * @return a Map of weight for each hashtag
	 */
	private Map<String, Double> evaluateWeight(Map<String, Integer> hashtags, int windowSize, int currentMonth, Calendar referenceTime, String vreId, Map<String, List<String>> hashtagsInVres) {

		Map<String, Double> weights = new HashMap<String, Double>();

		// find max score inside the list (counter)
		int max = 0;
		for(Entry<String, Integer> entry : hashtags.entrySet()){

			max = max < entry.getValue() ? entry.getValue() : max;

		}

		// normalize
		Map<String, Double> normalized = new HashMap<String, Double>();
		for(Entry<String, Integer> entry : hashtags.entrySet()){

			normalized.put(entry.getKey(), (double)entry.getValue() / (double)max);

		}

		// create the weight for each entry as:
		// w = 0.6 * normalized_score + 0.4 * freshness
		// freshness is evaluated as (window_size - latest_feed_for_hashtag_in_window_month)/window_size
		for(Entry<String, Integer> entry : hashtags.entrySet()){

			// first part of the weight
			double weight = 0.6 * normalized.get(entry.getKey());

			List<Feed> mostRecentFeedForHashtag = null;

			// we are in the simplest case.. the hashtag belongs (or the request comes) from a single vre
			if(vreId != null){

				try{

					mostRecentFeedForHashtag = store.getVREFeedsByHashtag(vreId, entry.getKey());

				}catch(Exception e){

					logger.error("Unable to retrieve the most recent feeds for hashtag " + entry.getKey() + " in " + vreId);

					// put a weight of zero for this hashtag
					weights.put(entry.getKey(), 0.0);
					continue;
				}

			}else{ // we are not so lucky

				// get the list of vres for this hashtag
				List<String> vres = hashtagsInVres.get(entry.getKey());

				// init list
				mostRecentFeedForHashtag = new ArrayList<Feed>();

				List<Feed> feedsForVre;
				for (String vre : vres) {
					try{
						feedsForVre = store.getVREFeedsByHashtag(vre, entry.getKey());
					}catch(Exception e){
						logger.error("Unable to retrieve the most recent feeds for hashtag " + entry.getKey() + " in " + vreId);
						continue; 
					}

					// add to the list
					mostRecentFeedForHashtag.addAll(feedsForVre);
				}

				// check if there is at least a feed or it is empty
				if(mostRecentFeedForHashtag.isEmpty()){
					// put a weight of zero for this hashtag
					weights.put(entry.getKey(), 0.0);
					continue;
				}
			}

			// retrieve the most recent one among these feeds
			Collections.sort(mostRecentFeedForHashtag, Collections.reverseOrder());

			// get month of the last recent feed for this hashtag
			Calendar monstRecentFeedForHashTagTime = Calendar.getInstance();
			monstRecentFeedForHashTagTime.setTimeInMillis(mostRecentFeedForHashtag.get(0).getTime().getTime());

			int sub = currentMonth - monstRecentFeedForHashTagTime.get(Calendar.MONTH);
			int value = sub >= 0? sub : 12 - Math.abs(sub);
			double freshness = 1.0 - (double)(value) / (double)(windowSize);
			logger.debug("freshness is " + freshness + " for hashtag " + entry.getKey() + 
					" because the last feed has month " + monstRecentFeedForHashTagTime.get(Calendar.MONTH));

			// update the weight
			weight += 0.4 * freshness;

			// put it into the hashmap
			weights.put(entry.getKey(), weight);
		}

		// print sorted
		Map<String, Double> scoredListSorted = sortByWeight(weights);
		for(Entry<String, Double> entry : scoredListSorted.entrySet()){

			logger.debug("[hashtag=" + entry.getKey() + " , weight="  + entry.getValue() + "]");
		}

		return weights;
	}

	/**
	 * Sort a map by its values
	 * @param map
	 * @return
	 */
	private static <K, V extends Comparable<? super V>> Map<K, V> 
	sortByWeight( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
				});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		boolean toReturn = false;
		try {
			GroupManager manager = new LiferayGroupManager();
			long groupId = manager.getGroupIdFromInfrastructureScope(getASLSession().getScope());
			toReturn = !manager.isVRE(groupId);
			return toReturn;
		}
		catch (Exception e) {
			logger.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}			
	}

}
