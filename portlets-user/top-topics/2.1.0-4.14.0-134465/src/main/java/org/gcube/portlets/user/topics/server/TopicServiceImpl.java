package org.gcube.portlets.user.topics.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portlets.user.topics.client.TopicService;
import org.gcube.portlets.user.topics.shared.HashTagOccAndWeight;
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
 * TopicServiceImpl server side implementation for top-topics class
 * @author Massimiliano Assante, ISTI-CNR
 * @author Costantino Perciante, ISTI-CNR
 */
@SuppressWarnings("serial")
public class TopicServiceImpl extends RemoteServiceServlet implements TopicService {
	private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

	private static final int WINDOW_SIZE_IN_MONTHS = 6; // it must not exceed 12
	private static final double FRESHNESS_FACTOR = 0.4;
	private static final double NORMALIZED_SCORE_FACTOR = 0.6;

	private DatabookStore store;
	private GroupManager gm;
	private UserManager um;

	/**
	 * connect to cassandra at startup
	 */
	public void init() {
		store = new DBCassandraAstyanaxImpl();	
		gm = new LiferayGroupManager();
		um = new LiferayUserManager();
	}

	/**
	 * close connection to cassandra at shutdown
	 */
	public void destroy() {
		store.closeConnection();
	}


	/**
	 * return trending hashtags
	 */
	@Override
	public HashtagsWrapper getHashtags() {

		String userName = PortalContext.getConfiguration().getCurrentUser(getThreadLocalRequest()).getUsername();
		String currentScope = PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest());
		boolean isInfrastructure = isInfrastructureScope(currentScope);

		// get the reference time 
		Calendar referenceTime = Calendar.getInstance();
		int currentMonth = referenceTime.get(Calendar.MONTH); // jan = 0, ..... dec = 11
		referenceTime.set(Calendar.MONTH, currentMonth -  WINDOW_SIZE_IN_MONTHS); // the year is automatically decreased if needed
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		logger.debug("Reference time for trending topics is " + format.format(referenceTime.getTime()));

		ArrayList<HashTagOccAndWeight> toSort = new ArrayList<HashTagOccAndWeight>();
		ArrayList<String> hashtagsChart = new ArrayList<String>();
		Map<String, List<String>> hashtagsInVres = null;

		try {

			Map<String, Integer> hashtagsAndOccurrences = new HashMap<String, Integer>();

			if (isInfrastructure) {

				logger.debug("****** retrieving hashtags for user VREs and site");

				GCubeUser user = um.getUserByUsername(userName);
				Set<GCubeGroup> vresInPortal = gm.listGroupsByUserAndSite(user.getUserId(), getThreadLocalRequest().getServerName());
				logger.debug("Contexts in this site per user are " + vresInPortal);
				List<String> contexts = new ArrayList<String>();

				// get the scopes associated with such groups
				for (GCubeGroup gCubeGroup : vresInPortal) {
					contexts.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));
				}

				hashtagsInVres = new HashMap<String, List<String>>();

				for (String context : contexts) {		

					Map<String, Integer> hashtagsAndOccurrenceInScope = store.getVREHashtagsWithOccurrenceFilteredByTime(context, referenceTime.getTimeInMillis());

					// merge the values if needed
					for (String hashtag : hashtagsAndOccurrenceInScope.keySet()) {

						int newValue;
						List<String> vres = new ArrayList<String>();

						if(hashtagsAndOccurrences.containsKey(hashtag)){

							newValue = hashtagsAndOccurrences.get(hashtag) + hashtagsAndOccurrenceInScope.get(hashtag);
							vres = hashtagsInVres.get(hashtag);

						}else{

							newValue = hashtagsAndOccurrenceInScope.get(hashtag);

						}

						hashtagsAndOccurrences.put(hashtag, newValue);
						vres.add(context);
						hashtagsInVres.put(hashtag, vres);
					}
				}

			}
			else {

				logger.debug("****** retrieving hashtags for scope " + currentScope);
				hashtagsAndOccurrences = store.getVREHashtagsWithOccurrenceFilteredByTime(currentScope, referenceTime.getTimeInMillis());
			}

			// now we need to evaluate score for each element
			Map<String, Double> weights = evaluateWeights(hashtagsAndOccurrences, WINDOW_SIZE_IN_MONTHS, currentMonth, referenceTime, currentScope, hashtagsInVres);

			// at the end build the list
			for (String hashtag : hashtagsAndOccurrences.keySet()) {
				toSort.add(new HashTagOccAndWeight(hashtag, hashtagsAndOccurrences.get(hashtag), weights.get(hashtag)));
			}

			// sort for weights
			Collections.sort(toSort);

			// build the list of hashtags to display
			for (HashTagOccAndWeight wrapper : toSort) {
				logger.debug("Entry is " + wrapper.toString() + " with weight " + wrapper.getWeight());
				String hashtag = wrapper.getHashtag();
				String href="\"?"+
						new String(Base64.encodeBase64(GCubeSocialNetworking.HASHTAG_OID.getBytes()))+"="+
						new String(Base64.encodeBase64(hashtag.getBytes()))+"\"";
				String hashtagLink = "<a class=\"topiclink\" href=" + href + ">"+hashtag+"</a>";
				hashtagsChart.add(hashtagLink);
			}

			return new HashtagsWrapper(isInfrastructure, hashtagsChart);
		}
		catch (Exception e) {
			logger.error("Error while retrieving hashtags ", e);
			return null;
		}
	}


	/**
	 * Evaluate the weight for each element as w = 0.6 * s + 0.4 * f
	 * where s is the score: a normalized value given by counter_i / counter_max
	 * f is the freshness: evaluated taking into account the most recent feed containing that hashtag into the window w (that is, the period taken into account)
	 * @param hashtags
	 * @param hashtagsInVres (present if vreid is null)
	 * @param window size
	 * @param current month
	 * @param referenceTime
	 * @param vreid (present if hashtagsInVres is null)
	 * @return a Map of weight for each hashtag
	 */
	private Map<String, Double> evaluateWeights(
			Map<String, Integer> hashtags, 
			int windowSize, 
			int currentMonth, 
			Calendar referenceTime, 
			String vreId, 
			Map<String, List<String>> hashtagsInVres) {

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
		// w = NORMALIZED_SCORE_FACTOR * normalized_score + FRESHNESS_FACTOR * freshness
		// freshness is evaluated as (window_size - latest_feed_for_hashtag_in_window_month)/window_size
		for(Entry<String, Integer> entry : hashtags.entrySet()){

			// first part of the weight
			double weight = NORMALIZED_SCORE_FACTOR * normalized.get(entry.getKey());

			List<Feed> mostRecentFeedForHashtag = null;

			// we are in the simplest case.. the hashtag belongs (or the request comes) from a single vre
			if(hashtagsInVres == null){

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
			weight += FRESHNESS_FACTOR * freshness;

			// put it into the hashmap
			weights.put(entry.getKey(), weight);
		}

		return weights;
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope(String currentScope) {
		boolean toReturn = false;
		try {
			long groupId = gm.getGroupIdFromInfrastructureScope(currentScope);
			toReturn = !gm.isVRE(groupId);
			return toReturn;
		}
		catch (Exception e) {
			logger.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}			
	}

}
