package org.gcube.application.framework.search.library.model;

import static org.gcube.data.streams.dsl.Streams.*;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;





//import org.apache.axis.message.addressing.EndpointReference;
//import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.xerces.util.URI.MalformedURIException;
import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.accesslogger.model.AdvancedSearchAccessLogEntry;
import org.gcube.application.framework.accesslogger.model.SemanticEnrichmentAccessLogEntry;
import org.gcube.application.framework.accesslogger.model.SimpleSearchAccessLogEntry;
//import org.gcube.application.framework.core.cache.RIsManager;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.NoSearchMasterEPRFoundException;
import org.gcube.application.framework.search.library.exception.QuerySyntaxException;
import org.gcube.application.framework.search.library.exception.ResultsStreamRetrievalException;
import org.gcube.application.framework.search.library.exception.ReadingUserProfileException;
import org.gcube.application.framework.search.library.exception.gRS2CreationException;
import org.gcube.application.framework.search.library.impl.ResultSetConsumer;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.util.ArraysComparison;
import org.gcube.application.framework.search.library.util.FindFieldsInfo;
import org.gcube.application.framework.search.library.util.Modifiers;
import org.gcube.application.framework.search.library.util.Operator;
import org.gcube.application.framework.search.library.util.Order;
import org.gcube.application.framework.search.library.util.Point;
import org.gcube.application.framework.search.library.util.QuerySanitizer;
import org.gcube.application.framework.search.library.util.SearchConstants;
import org.gcube.application.framework.search.library.util.SearchType;
import org.gcube.application.framework.search.library.util.SessionConstants;
//import org.gcube.application.framework.userprofiles.library.impl.UserProfile;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamException;
import org.gcube.data.streams.exceptions.StreamOpenException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLRelation;
import search.library.util.cql.query.tree.GCQLSortNode;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.Modifier;
import search.library.util.cql.query.tree.ModifierSet;
import static org.gcube.resources.discovery.icclient.ICFactory.*;

/**
 * 
 * @author Rena - NKUA
 * 
 */

public class Query implements Cloneable {

	// ACCESS LOGGER
	private AccessLogger accessLogger = AccessLogger.getAccessLogger();

	// the browsable fields
	protected List<Field> browsableFields;

	// the criteria set by the user
	protected List<Criterion> criteria;

	// the previous criteria set by the user
	protected List<Criterion> previousCriteria;

	// whether the user wants distinct values from browsing
	protected boolean distinct;

	// the geospatial information set by the user
	protected GeospatialInfo geospatialInfo;

	// AND/OR operator for conditions
	protected Operator operator;

	// Ascending or Descending order per collection
	protected Order order;

	protected String queryDescription;

	protected List<String> languages;

	protected List<Field> searchableFields;

	protected String searchType; // Used for Google Search distinction

	protected List<String> selectedCollections;

	// protected ArrayList<String> sortBy = new ArrayList<String>();
	private String sortBy;

	protected ResultSetConsumerI searchRSC;

	// the query as a string
	protected String queryString;

	protected String genericSearchType;

	/** Object logger. **/
	private static Logger logger = LoggerFactory.getLogger(Query.class);
	// protected final GCUBELog logger = new GCUBELog(this);

	protected boolean setRelation; // used for Geospatial Search

	protected String relation; // probably needed

	ArrayList<String> relationModifiers; // probably needed

	ArrayList<String> indexModifiers; // needed?

	int selectedLanguage;

	// flag to define the semantic enrichment
	private boolean semanticEnrichment;

	protected String browseBy;

	private String searchTerm = new String();

	private boolean ftsAvailable;

	List<Field> sortableFields;

	private GCQLNode previousQuery = null;

	private String previousQueryDescription = new String();

//	protected static AtomicInteger SMid = new AtomicInteger();

//	private String searchEPR;
	
	public static final String ENDPOINT_KEY = "resteasy-servlet";

	private String ftsId = new String();

	Field geoField;

	boolean hasResults = true;

	ArrayList<String> selectedPresentationFields;

	ArrayList<String> searchQueryTerms;

	private long searchStartTime;

	/* Flag to indicate if the data fusion is supported. */
	private boolean rankingSupport = false;

	public void setRanking(boolean rankSupport) {
		this.rankingSupport = rankSupport;
	}

	public void setHasResults(boolean existResults) {
		hasResults = existResults;
	}

	public boolean hasResults() {
		return hasResults;
	}

	public boolean isFtsAvailable() {
		logger.debug("Supports fts? -> " + ftsAvailable);
		return ftsAvailable;
	}

	public void setFtsAvailable(boolean ftsAvailable) {
		this.ftsAvailable = ftsAvailable;
	}

	public void setSemanticEnrichment(boolean semanticEnrichment) {
		this.semanticEnrichment = semanticEnrichment;
	}

	public boolean getSemanticEnrichment() {
		return this.semanticEnrichment;
	}

	public boolean isGeoAvailable() {
		return geoAvailable;
	}

	public void setGeoAvailable(boolean geoAvailable) {
		this.geoAvailable = geoAvailable;
	}

//	private Cache cachedSearchEPRs;
	
	private boolean geoAvailable;

	public String getSearchTerm() {
		return searchTerm;
	}
	

	public void setSearchTerm(String searchTerm) {
		// Check if there is a wildcard
		// if (searchTerm.contains("*")) {
		// if (!searchTerm.startsWith("\"") || !searchTerm.endsWith("\"")) {
		// String newSearchTerm = "\"" + searchTerm + "\"";
		// searchTerm = newSearchTerm;
		// }
		// }
		
		if(searchTerm.startsWith("[") && searchTerm.endsWith("]"))
			this.searchTerm = "\""+StringEscapeUtils.unescapeHtml(QuerySanitizer.sanitizeQuery(searchTerm))+"\"";
		else
			this.searchTerm = searchTerm;
		
		logger.debug("this.searchTerm = " + this.searchTerm);
		
		this.searchQueryTerms.clear();
	}

	public Query() {
		super();
		criteria = new ArrayList<Criterion>();
		previousCriteria = new ArrayList<Criterion>();
		operator = Operator.OR;
		selectedCollections = new ArrayList<String>();
		searchableFields = new ArrayList<Field>();
		browsableFields = new ArrayList<Field>();
		languages = new ArrayList<String>();
		order = Order.ASC;
		distinct = false;
		searchRSC = null;
		searchType = SearchType.NoSearch;
		genericSearchType = SearchType.NoSearch;
		sortableFields = new ArrayList<Field>();
		sortBy = new String();
		browseBy = new String();
		selectedPresentationFields = new ArrayList<String>();
		searchQueryTerms = new ArrayList<String>();
		semanticEnrichment = false;
	}
	
	
	private void debugQuery(){
		logger.debug("==== Debug Query ===");
		logger.debug("Previous Criteria: ");
		for(Criterion c : previousCriteria)
			logger.debug("id: "+c.getSearchFieldId()+" name: "+c.getSearchFieldName()+" value: "+c.getSearchFieldValue());
		logger.debug("Current Criteria: ");
		for(Criterion c : criteria)
			logger.debug("id: "+c.getSearchFieldId()+" name: "+c.getSearchFieldName()+" value: "+c.getSearchFieldValue());
		logger.debug("Selected collections: ");
		for(String s : selectedCollections)
			logger.debug(s);
		logger.debug("Searchable Fields: ");
		for(Field f : searchableFields)
			logger.debug("id: "+f.id+" label: "+f.label+" name: "+f.name+" value: "+f.value+" description: "+f.description+" datatype: "+f.dataType);
		logger.debug("Browsable Fields: ");
		for(Field f : browsableFields)
			logger.debug("id: "+f.id+" label: "+f.label+" name: "+f.name+" value: "+f.value+" description: "+f.description+" datatype: "+f.dataType);
		logger.debug("Languages: ");
		for(String s : languages)
			logger.debug(s);
		logger.debug("Sortby: "+sortBy);
		logger.debug("Browseby: "+browseBy);
		logger.debug("Selected Presentation Fields: ");
		for(String s : selectedPresentationFields)
			logger.debug(s);
		logger.debug("Search Query Terms: ");
		for(String s : searchQueryTerms)
			logger.debug(s);
		logger.debug("Semantic enrichment: "+semanticEnrichment);
	}
	
	
	public void enableEPRCache(){
		if(CacheManager.getInstance().getCache("CachedSearchEPRs") == null){
			logger.debug("Creating a cache for the EPRs");
			CacheManager.getInstance().addCache(new Cache("CachedSearchEPRs", 100, false, true, 60, 60));			
		}
	}
	

	/**
	 * Adds a new criterion to the list of search criteria
	 * 
	 * @param criterion
	 *            the criterion to be added
	 * 
	 */
	public void addCriterion(Criterion criterion) {
		logger.debug("adding criterion id: " + criterion.getSearchFieldId()
				+ " and value: " + criterion.getSearchFieldValue());

		if((criterion.getSearchFieldValue().startsWith("[") && criterion.getSearchFieldValue().endsWith("]"))){
			criteria.add(criterion);
			criterion.setSearchFieldValue("\""+StringEscapeUtils.unescapeHtml(QuerySanitizer.sanitizeQuery(criterion.getSearchFieldValue()))+"\"");
			logger.debug("Sanitized criterion added: " + criterion.getSearchFieldValue());
			return;
		}
		
		
		if (criterion.getSearchFieldValue().contains(" ")
				&& (!criterion.getSearchFieldValue().startsWith("\"") || !criterion
						.getSearchFieldValue().endsWith("\""))) {
			String[] splitted = criterion.getSearchFieldValue().split(" ");
			for (int i = 0; i < splitted.length; i++) {
				Criterion crit = new Criterion();
				crit.setSearchFieldId(criterion.getSearchFieldId());
				crit.setSearchFieldName(criterion.getSearchFieldName());
				crit.setSearchFieldValue(splitted[i]);

				criteria.add(crit);
				logger.debug("Criterion added: " + crit.getSearchFieldValue());

			}
			return;
		}
		logger.debug("Criterion added: " + criterion.getSearchFieldValue());
		criteria.add(criterion);
	}

	/**
	 * Removes the i-th search criterion
	 * 
	 * @param i
	 *            the position in the list where the desired search criterion
	 *            rests
	 */
	public void removeCriterion(int i) {
		logger.debug("Removing Criterion: {name=" + criteria.get(i).getSearchFieldName()+", value="+criteria.get(i).getSearchFieldValue()+"}");
		criteria.remove(i);
	}

	/**
	 * 
	 * @param previous
	 *            whether it should be cloned for previous or not
	 * 
	 * @return cloned query object
	 */
	public Query clone(boolean previous) {
		Query q = new Query();
		logger.debug("cloning - previous: " + previous);
		
		if (!previous) {
			for (Criterion x : this.criteria) {
				q.criteria.add(x.clone());
			}

			for (String s : this.searchQueryTerms) {
				q.searchQueryTerms.add(new String(s));
			}

			for (String x : this.selectedCollections) {
				String y = new String(x);
				q.selectedCollections.add(y);
			}

			for (Field x : this.searchableFields) {
				q.searchableFields.add(x.clone());
			}

			for (Field x : this.sortableFields) {
				q.sortableFields.add(x.clone());
			}

			for (Field x : this.browsableFields) {
				q.browsableFields.add(x.clone());
			}

			for (String x : this.selectedPresentationFields) {
				String y = new String(x);
				q.selectedPresentationFields.add(y);
			}

			q.searchType = SearchType.NoSearch;
			q.genericSearchType = SearchType.NoSearch;

			if (ftsAvailable)
				q.ftsAvailable = true;
			else
				q.ftsAvailable = false;
			if (geoAvailable)
				q.geoAvailable = true;
			else
				q.geoAvailable = false;
			if (semanticEnrichment)
				q.semanticEnrichment = true;
			else
				q.semanticEnrichment = false;

			for (int i = 0; i < languages.size(); i++) {
				q.languages.add(new String(this.languages.get(i)));
			}
			q.selectedLanguage = this.selectedLanguage;
			previousQuery = null;
			previousQueryDescription = new String();
			q.ftsId = this.ftsId;

			q.rankingSupport = this.rankingSupport;

			if (this.geoField != null)
				q.geoField = this.geoField.clone();
		} else {

			logger.debug("do i have criteria? : " + criteria.size() + " "
					+ previousCriteria.size());
			for (Criterion x : this.criteria) {
				logger.debug(x.getSearchFieldName() + " "
						+ x.getSearchFieldValue());
				if (!x.getSearchFieldValue().equals(""))
					q.previousCriteria.add(x.clone());
			}

			for (String x : this.selectedCollections) {
				String y = new String(x);
				q.selectedCollections.add(y);
			}

			for (String x : this.selectedPresentationFields) {
				String y = new String(x);
				q.selectedPresentationFields.add(y);
			}

			q.searchType = SearchType.PreviousSearch;
			q.genericSearchType = this.genericSearchType;

			if (searchType.equals(SearchType.SimpleSearch)) {
				Criterion newCrit = new Criterion();
				newCrit.setSearchFieldId(ftsId);
				newCrit.setSearchFieldName("Any");
				newCrit.setSearchFieldValue(searchTerm);
				q.previousCriteria.add(newCrit);
			}
			for (Field x : this.searchableFields) {
				q.searchableFields.add(x.clone());
			}

			for (Field x : this.sortableFields) {
				q.sortableFields.add(x.clone());
			}

			for (Field x : this.browsableFields) {
				q.browsableFields.add(x.clone());
			}

			if (ftsAvailable)
				q.ftsAvailable = true;
			else
				q.ftsAvailable = false;
			if (semanticEnrichment)
				q.semanticEnrichment = true;
			else
				q.semanticEnrichment = false;

			for (int i = 0; i < languages.size(); i++) {
				q.languages.add(new String(this.languages.get(i)));
			}
			q.selectedLanguage = this.selectedLanguage;
			// TODO: Correct?
			q.previousQuery = this.previousQuery;
			q.previousQueryDescription = new String(
					this.previousQueryDescription);

			q.ftsId = this.ftsId;
			if (this.geoField != null)
				q.geoField = this.geoField.clone();
		}

		q.distinct = this.distinct;
		if (this.geospatialInfo != null
				&& this.geospatialInfo.getBounds() != null) {
			q.geospatialInfo = this.geospatialInfo.clone();
		}

		q.operator = this.operator;
		q.order = this.order;
		q.queryDescription = this.queryDescription;
		q.queryString = this.queryString;
		q.sortBy = new String(this.sortBy);
		q.browseBy = new String(this.browseBy);

		if (this.searchTerm != null)
			q.searchTerm = new String(this.searchTerm);
		
		return q;
	}

	/**
	 * Sets the selected language
	 * 
	 * @param language
	 */
	public void setLanguage(int language) {
		selectedLanguage = language;
	}

	public void setPresentationFields(ArrayList<String> presentationFields) {
		selectedPresentationFields = presentationFields;
	}

	private void findAvailableLanguages(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		SearchHelper sh = new SearchHelper(session);
		CollectionInfo firstCol = null;
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		if (selectedRealCollections != null
				&& selectedRealCollections.size() != 0) {
			ArrayList<String> allLanguages = new ArrayList<String>();

			/* Find common languages supported by the collections */

			// Get the languages of the first collection
			logger.debug("Finding Available Languages");
			logger.debug("The number of selected collections is: "
					+ selectedCollections.size());

			List<String> realCollections = getSelectedRealCollections(session);
			firstCol = sh.findCollectionInfo(realCollections.get(0));
			allLanguages.addAll(firstCol.getLanguages());
			logger.debug("initially languages are: " + allLanguages.size());
			logger.debug("Real selected Collections: " + realCollections.size());
			CollectionInfo col = null;
			// Remove the languages that are not in common with the other
			// selected collections
			for (int i = 1; i < realCollections.size(); i++) {

				col = sh.findCollectionInfo(realCollections.get(i));
				// get the languages for this collection
				ArrayList<String> colLanguages = col.getLanguages();
				for (int j = 0; j < firstCol.getLanguages().size(); j++) {
					if (!colLanguages.contains(firstCol.getLanguages().get(j))) {

						int ind = allLanguages.indexOf(firstCol.getLanguages()
								.get(j));

						// TODO
						if (ind >= 0)
							allLanguages.remove(ind);
						else
							logger.debug("Element: "
									+ firstCol.getLanguages().get(j)
									+ "wasn't found in allLanguages collection!");
					}
				}

			}

			languages.clear();
			languages = allLanguages;
		} else
			languages.clear();

		logger.debug("Number of languages: " + languages.size());
	}

	private void findAvailableSearchFields(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		/*
		 * Find the available Search Fields based on the selected collections
		 * and the selected language.
		 */

		logger.debug("Find available search fields");
		if (selectedCollections.size() == 0 || selectedLanguage == -1
				|| languages.size() == 0) {
			searchableFields = new ArrayList<Field>();
			browsableFields = new ArrayList<Field>();
			logger.debug("No selected collections or no languages - returning");
			return;
		}

		SearchHelper sh = new SearchHelper(session);
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = sh
				.getAvailableCollections();

		// find the common fields for those collections
		ArrayList<Field> searchableFields = new ArrayList<Field>();
		List<String> realCollections = getSelectedRealCollections(session);
		logger.debug("Number of selected real collections: "
				+ realCollections.size());
		CollectionInfo firstCol = sh.findCollectionInfo(realCollections.get(0));
		logger.debug("Found collection - printing info: ");
		logger.debug(firstCol.getName());
		searchableFields.addAll(firstCol.getIndices());
		logger.debug("For collection: " + firstCol.getId()
				+ " adding indices: " + searchableFields.size());
		// ArrayList<Field> clone = searchableFields;
		ArrayList<Field> clone = new ArrayList<Field>();
		for (int i = 0; i < searchableFields.size(); i++) {
			clone.add(searchableFields.get(i).clone());
		}
		logger.debug("clone has: " + clone.size());
		for (int i = 1; i < realCollections.size(); i++) {
			for (int j = 0; j < searchableFields.size(); j++) {
				logger.debug("Checking or field: "
						+ searchableFields.get(j).getLabel()
						+ " inside collection: " + realCollections.get(i));
				CollectionInfo colInfo = sh.findCollectionInfo(realCollections
						.get(i));
				boolean found = false;
				for (int k = 0; k < colInfo.getIndices().size(); k++) {
					logger.debug("Comparing fields: "
							+ colInfo.getIndices().get(k).getId() + " "
							+ colInfo.getIndices().get(k).getLabel() + " with "
							+ searchableFields.get(j).getId() + " "
							+ searchableFields.get(j).getLabel());
					if (colInfo.getIndices().get(k).getId()
							.equals(searchableFields.get(j).getId())) {
						found = true;
						logger.debug("FOUND!");
						break;
					}
				}
				if (!found) {
					for (int k = 0; k < clone.size(); k++) {
						if (clone.get(k).getId()
								.equals(searchableFields.get(j).getId())) {
							logger.debug("REMOVING");
							clone.remove(k);
							break;
						}
					}
				}
			}
		}
		logger.debug("clone has now: " + clone.size());
		searchableFields.clear();

		// find the common fields for that language
		for (int i = 0; i < clone.size(); i++) {
			ArrayList<String> langs = clone.get(i).getLanguages();
			logger.debug("The number of field languages is: " + langs.size());
			logger.debug("Number of languages: " + languages.size());
			logger.debug("Selected language: " + selectedLanguage);
			if (!langs.contains(languages.get(selectedLanguage))) {
				logger.debug("Adding Field");
				searchableFields.add(clone.get(i));
			} else
				logger.debug("Not adding field");
		}

		// searchableFields = clone;
		this.searchableFields = searchableFields;
		// browsableFields.clear();
		// for (int i = 0; i < searchableFields.size(); i++) {
		// if (!searchableFields.get(i).getName().equals("Any"))
		// this.browsableFields.add(searchableFields.get(i));
		// }
		logger.debug("Finally the number is: " + searchableFields.size());
	}

	private void findAvailableBrowseFields(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		/*
		 * Find the available Search Fields based on the selected collections
		 * and the selected language.
		 */

		logger.debug("Find available browse fields");
		if (selectedCollections.size() == 0 || selectedLanguage == -1) {
			searchableFields = new ArrayList<Field>();
			browsableFields = new ArrayList<Field>();
			return;
		}

		SearchHelper sh = new SearchHelper(session);
		// HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections =
		// sh.getAvailableCollections();

		// find the common fields for those collections
		ArrayList<Field> browsableFields = new ArrayList<Field>();
		List<String> realCollections = getSelectedRealCollections(session);
		logger.debug("Number of selected real collections: "
				+ realCollections.size());
		CollectionInfo firstCol = sh.findCollectionInfo(realCollections.get(0));
		browsableFields.addAll(firstCol.getBrowsableFields());
		logger.debug("For collection: " + firstCol.getId()
				+ " adding browse fields: " + browsableFields.size());
		// ArrayList<Field> clone = searchableFields;
		ArrayList<Field> clone = new ArrayList<Field>();
		for (int i = 0; i < browsableFields.size(); i++) {
			clone.add(browsableFields.get(i).clone());
		}
		logger.debug("clone has: " + clone.size());
		for (int i = 1; i < realCollections.size(); i++) {
			for (int j = 0; j < browsableFields.size(); j++) {
				logger.debug("Checking or field: "
						+ browsableFields.get(j).getLabel()
						+ " inside collection: " + realCollections.get(i));
				CollectionInfo colInfo = sh.findCollectionInfo(realCollections
						.get(i));
				boolean found = false;
				for (int k = 0; k < colInfo.getBrowsableFields().size(); k++) {
					logger.debug("Comparing fields: "
							+ colInfo.getBrowsableFields().get(k).getId() + " "
							+ colInfo.getBrowsableFields().get(k).getLabel()
							+ " with " + browsableFields.get(j).getId() + " "
							+ browsableFields.get(j).getLabel());
					if (colInfo.getBrowsableFields().get(k).getId()
							.equals(browsableFields.get(j).getId())) {
						found = true;
						logger.debug("FOUND!");
						break;
					}
				}
				if (!found) {
					for (int k = 0; k < clone.size(); k++) {
						if (clone.get(k).getId()
								.equals(browsableFields.get(j).getId())) {
							logger.debug("REMOVING");
							clone.remove(k);
							break;
						}
					}
				}
			}
		}
		logger.debug("clone has now: " + clone.size());
		browsableFields.clear();

		browsableFields = clone;

		this.browsableFields = browsableFields;
		// browsableFields.clear();

		logger.debug("Finally the number is: " + this.browsableFields.size());
	}

	// Gathers the sortable fields: the fields that are sortable - searchable
	// and presentable
	private void findAvailableSortFields(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		if (selectedCollections == null || selectedCollections.size() == 0) {
			searchableFields = new ArrayList<Field>();
			return;
		}

		session.removeAttribute(SessionConstants.SESSION_SNIPPET_ATTR);
		session.removeAttribute(SessionConstants.SESSION_TITLE_ATTR);

		List<String> presentables = null;
		try {
			presentables = findPresentableFields(session);
			logger.debug("NIK--presentablesNEWONE:");
			printPresentables(presentables);

			for (Field x : searchableFields) {
				if (x.isSortable) {
					// Check if it is presentable also
					if (presentables.contains(x.id))
						sortableFields.add(x);
				}
			}

		} catch (Exception e) {
			logger.error(
					"******************************************************findpresentableFields method thrown an exception.",
					e);
			// try {
			// presentablesOLDONE = findAllPresentableFields_OLD(session);
			// logger.debug("NIK--presentablesOLD:");
			// printPresentables(presentablesOLDONE);
			//
			// presentables = findPresentableFields(session);
			// logger.debug("NIK--presentablesNEWONE:");
			// printPresentables(presentables);
			//
			// for (Field x : searchableFields) {
			// if (x.isSortable) {
			// // Check if it is presentable also
			// if (presentables.contains(x.id))
			// sortableFields.add(x);
			// }
			// }
			// } catch (ResourceRegistryException e1) {
			// logger.error("Resource registry exception", e1);
			// }
		}
	}

	private void printPresentables(List<String> presentables) {
		logger.debug("Printing " + presentables.size() + " presentables");
		for (String field : presentables)
			logger.debug(field);
	}

	
	
	public List<String> getSelectedRealCollections(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		return getSelectedRealCollectionsAlt(session);
	}
	
	public List<String> getSelectedRealCollectionsAlt(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		logger.debug("Inside get selected real collections.");
		List<String> cols = new ArrayList<String>();
		SearchHelper sh = new SearchHelper(session);
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> allCollections = sh
				.getAvailableCollections();
		Set<CollectionInfo> groups = allCollections.keySet();
		logger.debug("number of selected collections: "
				+ selectedCollections.size());
		for (String a : selectedCollections) {
			CollectionInfo colInfo = sh.findCollectionInfo(a);
			logger.debug("Collection: " + a);
			if (a.equals("all_collections")
					|| selectedCollections.contains("all_collections")) {
				logger.debug("all_collections");
				for (CollectionInfo group : groups) {
					logger.debug("Iterrating group!: " + group.getName());
					for (int i = 0; i < allCollections.get(group).size(); i++) {
						cols.add(allCollections.get(group).get(i).getId());
					}
				}
				logger.debug("number of returned collections: " + cols.size());
				return cols;
			} else if (colInfo.isCollectionGroup) {
				logger.debug("collection group");
				for (CollectionInfo group : groups) {
					if (group.getId().equals(colInfo.getId())) {
						// add all the collections of the group
						ArrayList<CollectionInfo> realCols = allCollections
								.get(group);
						for (int i = 0; i < realCols.size(); i++) {
							cols.add(realCols.get(i).getId());
						}
					}
				}
			} else {
				logger.debug("real collection");
				for (CollectionInfo group : groups) {
					// if (allCollections.get(group).contains(a))
					// cols.add(a);
					for (int i = 0; i < allCollections.get(group).size(); i++) {
						logger.debug("Comparing: "
								+ allCollections.get(group).get(i).getId()
								+ " with " + a);
						if (allCollections.get(group).get(i).getId().equals(a)) {
							if (!cols.contains(a))
								cols.add(a);
						}
					}
				}
			}
		}

		logger.debug("number of returned collections: " + cols.size());
		return cols;
	}

	public List<Field> getAvailableBrowseFields() {
		return browsableFields;
	}

	public List<Field> getAvailableSearchFields() {
		return searchableFields;
	}

	public List<Field> getAvailableSortFields() {
		return sortableFields;
	}

	public List<String> getAvailableLanguages() {
		return languages;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public GeospatialInfo getGeosatial() {
		return geospatialInfo;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getQueryDescription() {
		return queryDescription;
	}

	public String getQueryString() {
		return queryString;
	}

	public List<String> getSelectedCollections() {
		return selectedCollections;
	}

	public String getSelectedLanguage() {
		return languages.get(selectedLanguage);
	}

	public void reset() {
		logger.debug("Resetting query values");
		criteria.clear();
		operator = Operator.OR;
		order = Order.ASC;
		distinct = false;
		searchQueryTerms.clear();
	}

	public void selectCollections(List<String> newCollections,
			boolean selected, ASLSession session, boolean keepCriteriaAndQuery) {
		selectCollections(newCollections, selected, session, false, keepCriteriaAndQuery);
	}
	
	public void selectCollections(List<String> newCollections,
			boolean selected, ASLSession session) {
		selectCollections(newCollections, selected, session, false, false);
	}

	public void selectCollections(List<String> newCollections,
			boolean selected, ASLSession session,
			boolean replaceExistingSelectedCollections,
			boolean keepCriteriaQueries) {
		try {
			if (replaceExistingSelectedCollections)
				selectedCollections = newCollections;
			else {
				for (int i = 0; i < newCollections.size(); i++) {
					logger.debug("new collections:**" + newCollections.get(i)
							+ "**");
					if (selected
							&& !selectedCollections.contains(newCollections
									.get(i))) {
						logger.debug("adding:**" + newCollections.get(i) + "**");
						selectedCollections.add(newCollections.get(i));
					} else if (!selected
							&& selectedCollections.contains(newCollections
									.get(i))) {
						logger.debug("removing:**" + newCollections.get(i)
								+ "**");
						selectedCollections.remove(newCollections.get(i));
					}
				}
			}
			
			if(!keepCriteriaQueries){
				criteria.clear();
				searchQueryTerms.clear();
			}

			logger.debug("Going to update the information for the selected collections");
			findAvailableFts(session);
			findAvailableGeospatial(session);
			findAvailableLanguages(session);
			findAvailableSearchFields(session);
			findAvailableBrowseFields(session);
			findAvailableSortFields(session);
			logger.debug("Number of searchable fields found -> "
					+ searchableFields.size());
		} catch (InitialBridgingNotCompleteException be) {
			logger.debug("Initial bridging has not completed yet", be);
		} catch (InternalErrorException e) {
			logger.error("An internal error has occured: ", e);
		}
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public void setGeospatial(GeospatialInfo geospatial) {
		logger.debug("Setting geospatial info.");
		this.geospatialInfo = geospatial;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean setSelectedLanguage(String lang, ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		reset();
		if (!languages.contains(lang))
			return false;
		selectedLanguage = languages.indexOf(lang);

		findAvailableSearchFields(session);
		return true;
	}

	public void updateCriterionId(int i, String id) {
		logger.debug("Updating criterion: " + i + ", previous id was: "
				+ criteria.get(i).getSearchFieldId() + ", new is: " + id);
		criteria.get(i).setSearchFieldId(id);
	}

	public void updateCriterionName(int i, String name) {
		logger.debug("Updating criterion: " + i + ", previous name was: "
				+ criteria.get(i).getSearchFieldName() + ", new is: " + name);
		criteria.get(i).setSearchFieldName(name);
	}

	public void updateCriterionValue(int i, String value) {
		logger.debug("Updating criterion: " + i + ", previous value was: "
				+ criteria.get(i).getSearchFieldName() + ", new is: " + value);
		// if (value.contains("*")) {
		// if (!value.startsWith("\"") || !value.endsWith("\"")) {
		// String newValue = "\"" + value + "\"";
		// value = newValue;
		// }
		// }


		if(value.startsWith("[")&&value.endsWith("]")){
			criteria.get(i).setSearchFieldValue("\""+(QuerySanitizer.sanitizeQuery(value))+"\"");
			logger.debug("Sanitized criterion updated: " + criteria.get(i).getSearchFieldValue());
			return;
		}
			
		
		if (value.contains(" ")
				&& (!value.startsWith("\"") || !value.endsWith("\""))) {
			String[] splitted = value.split(" ");
			criteria.get(i).setSearchFieldValue(splitted[0]);
			for (int j = 1; j < splitted.length; j++) {
				Criterion crit = new Criterion();
				crit.setSearchFieldId(criteria.get(i).getSearchFieldId());
				crit.setSearchFieldName(criteria.get(i).getSearchFieldName());
				crit.setSearchFieldValue(splitted[j]);
				criteria.add(crit);
			}
			return;
		}
		criteria.get(i).setSearchFieldValue(value);
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getGenericSearchType() {
		return genericSearchType;
	}

	public List<String> getSelectedCollectionNames(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		// HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections =
		// (HashMap<CollectionInfo, ArrayList<CollectionInfo>>) session
		// .getAttribute(SessionConstants.Collections);

		List<String> realCollections = getSelectedRealCollections(session);
		List<String> collectionNames = new ArrayList<String>();
		SearchHelper sh = new SearchHelper(session);
		for (String colId : realCollections) {
			CollectionInfo colInf = sh.findCollectionInfo(colId);
			if (colInf != null) {
				collectionNames.add(colInf.getName());
			}
		}
		return collectionNames;
	}

	public void setGenericSearchType(String genType) {
		genericSearchType = genType;
	}

	public void setSetRelation(boolean set) {
		setRelation = set;
	}

	public void setBrowseBy(String browseByField) {

		browseBy = browseByField;
	}

	public String getBrowseByField() {
		return browseBy;
	}

	public String getBrowseByFieldName() {
		String fieldName;
		try {
			fieldName = QueryHelper.GetFieldNameById(browseBy);
		} catch (Exception e) {
			logger.debug("Couldn't get the name of the last browse by field ID");
			fieldName = "";
		}
		return fieldName;
	}

	public ResultSetConsumerI search(ASLSession session, boolean simple, ISearchClient searchClient)
			throws QuerySyntaxException, NoSearchMasterEPRFoundException, InitialBridgingNotCompleteException, InternalErrorException, SearchASLException	{
		logger.debug("About to create Query");

		logger.info("Time_Counter -- Called search, start counting time.");
		searchStartTime = System.currentTimeMillis();

		if (simple) {
			searchType = SearchType.SimpleSearch;
		} else
			searchType = SearchType.AdvancedSearch;
		
		ResultSetConsumer.removeSessionVariables(session);
		
		logger.info("Time_Counter -- Started creating Search Query " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		String[] query = createSearchQuery(session, simple);
		logger.info("Time_Counter -- Finished creating Search Query "
				+ (System.currentTimeMillis() - searchStartTime) / 1000.0
				+ " sec(s)");

		queryDescription = query[1];

		// filter out "," from query - prevents query parser exceptions
		query[0] = query[0].replace(",", " ");

		logger.debug("The Search Query is: ///////////////////////////////////////////////////////");
		logger.debug(query[0]);
		logger.debug("/////////////////////////////////////////////////////////////////////////");

		enableEPRCache();
		
		// check if there is any EPR cached
		logger.debug("Checking for cached Search Master EPRs in this scope");
		ArrayList<String> cachedEPRs = null;
		
		logger.info("Time_Counter -- Started Send Search to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		if( (CacheManager.getInstance().getCache("CachedSearchEPRs") != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()) != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue() != null))
			cachedEPRs = (ArrayList<String>) CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue();
		if ((cachedEPRs != null) && (cachedEPRs.size() > 0)) {
			for (int i = 0; i < cachedEPRs.size(); i++) {
				try{
					logger.debug("Acquiring result stream from "
							+ cachedEPRs.get(i));
					logger.info("Time_Counter -- Start submitting search to EPR " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					Stream<GenericRecord> recordsStream = submitSearch(searchClient ,query[0], cachedEPRs.get(i), session);
					logger.info("Time_Counter -- Finished submitting search to EPR, got the results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					if ((recordsStream != null) && (!recordsStream.isClosed())) {
						logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC = new ResultSetConsumer(recordsStream,
								searchType, false);
						logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC.setGenericSearchType(genericSearchType);
						searchRSC.setSearchStartTime(searchStartTime); 
						logger.info("Time_Counter -- Finished Send Search to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						return searchRSC;
					} else
						logger.debug("Search service: "
								+ cachedEPRs.get(i)
								+ " returned either a null or a closed records stream.");
				}catch(SearchASLException ex){
					logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
				}
				catch(MalformedURLException ex){
					logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
				}
				catch(gRS2CreationException ex){
					logger.debug("ResultSetConsumer could not be created");
				}
			}
		}
		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.info("Time_Counter -- Stopped Send Search to cached Search EPRs. No cached EPRs  " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		logger.debug("Cached Search Master EPRs in this scope are either not available or empty. Contacting IS to get new ones");
		String[] foundEPRs = null;
		
		logger.info("Time_Counter -- Start Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		foundEPRs = findSearchMasterEPRFeather(session);
		logger.info("Time_Counter -- Finished Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		ArrayList<String> discoveredEPRs = new ArrayList<String>();
		for (String epr : foundEPRs)
			discoveredEPRs.add(epr);
		
		if (foundEPRs == null || foundEPRs.length == 0) {
			logger.debug("No Search Masters Found");
			throw new NoSearchMasterEPRFoundException();
		} else {
			logger.debug("Number of Search Master EPRs: " + foundEPRs.length);
			CacheManager.getInstance().getCache("CachedSearchEPRs").put(new Element(session.getScope(), discoveredEPRs));
		}
		for (int i = 0; i < foundEPRs.length; i++) {
			try{
				logger.debug("////////////////////////Parsing query again!!!///////////////////////////////");
				logger.info("Time_Counter -- Start submitting search to EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
						foundEPRs[i], session);
				logger.info("Time_Counter -- Finished submitting search to EPRs , got results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				if ((recordsStream != null) && (!recordsStream.isClosed())) {
					logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC = new ResultSetConsumer(recordsStream, searchType,
							false);
					logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC.setGenericSearchType(genericSearchType);
					searchRSC.setSearchStartTime(searchStartTime); 
					return searchRSC;
				} else
					logger.debug("New discovered Search service returned either a null or a closed records stream.");
			}catch(SearchASLException ex){
				logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
			}
			catch(MalformedURLException ex){
				logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
			}
			catch(gRS2CreationException ex){
				logger.debug("ResultSetConsumer could not be created");
			}
		}
//		//if execution has reached this point, means that the cached instances are not working, so remove them and let it renew them on the next run.
//		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("NONE OF THE CACHED EPRs DID WORK, NEITHER CONTACTING I.S. HAS REVEALED ANY NEW ONES! WILL THROW A SEARCH EXCEPTION");
		throw new SearchASLException(new Exception("Either no available instance of search service exists OR all search instances failed to perform the search."));
//		return null;
	}

	
	public ResultSetConsumerI genericSearch(ASLSession session,
			List<String> terms, ISearchClient searchClient) throws QuerySyntaxException, 
			InitialBridgingNotCompleteException, InternalErrorException, SearchASLException	{
		return genericSearch(session, terms, false, searchClient);
	}

	
	public ResultSetConsumerI genericSearch(ASLSession session,
			List<String> terms, boolean onlyTitleSnippet, ISearchClient searchClient)
			throws QuerySyntaxException, InitialBridgingNotCompleteException, InternalErrorException, SearchASLException {
		logger.debug("About to create Query");
		
		logger.debug("Generic Search terms: ");
		for(String t:terms)
			logger.debug(t);
		
		logger.info("Time_Counter -- Called generic search, start counting time.");
		searchStartTime = System.currentTimeMillis();
		searchType = SearchType.GenericSearch;
		ResultSetConsumer.removeSessionVariables(session);
		
		if(onlyTitleSnippet)
			semanticEnrichment = false;
		
		logger.info("Time_Counter -- Started creating Generic Search Query " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		String[] query = createGenericSearchQuery(session, terms);
		logger.info("Time_Counter -- Finished creating Generic Search Query "
				+ (System.currentTimeMillis() - searchStartTime) / 1000.0
				+ " sec(s)");
		
		queryDescription = query[1];

		// filter out "," from query - prevents query parser exceptions
		query[0] = query[0].replace(",", " ");

		logger.debug("The Generic Search Query is: ///////////////////////////////////////////////////////");
		logger.debug(query[0]);
		logger.debug("/////////////////////////////////////////////////////////////////////////");

		enableEPRCache();
		
		// check if there is any EPR cached
		logger.debug("Checking for cached Search Master EPRs in this scope");
		ArrayList<String> cachedEPRs = null;
		
		if( (CacheManager.getInstance().getCache("CachedSearchEPRs") != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()) != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue() != null))
			cachedEPRs = (ArrayList<String>) CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue();
		if ((cachedEPRs != null) && (cachedEPRs.size() > 0)) {
			logger.info("Time_Counter -- Started Sending Generic Search to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
			for (int i = 0; i < cachedEPRs.size(); i++) {
				try{
					logger.debug("Acquiring result stream from "
							+ cachedEPRs.get(i));
					logger.info("Time_Counter -- Start submitting generic search to an EPR " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
							cachedEPRs.get(i), session);
					logger.info("Time_Counter -- Finished submitting generic search to an EPR, got the Result Stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					if ((recordsStream != null) && (!recordsStream.isClosed())) {
						logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC = new ResultSetConsumer(recordsStream,
								searchType, onlyTitleSnippet);
						logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC.setGenericSearchType(genericSearchType);
						searchRSC.setSearchStartTime(searchStartTime); 
						logger.info("Time_Counter -- Finished Sending Generic Search to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						return searchRSC;
					} else
						logger.debug("Search service: "
								+ cachedEPRs.get(i)
								+ " returned either a null or a closed records stream.");
				}catch(SearchASLException ex){
					logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
				}
				catch(MalformedURLException ex){
					logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
				}
				catch(gRS2CreationException ex){
					logger.debug("ResultSetConsumer could not be created");
				}
			}
		}
		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.info("Time_Counter -- Stopped Sending Generic Search to cached Search EPRs. No cached EPRs  " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		logger.debug("Cached Search Master EPRs in this scope are either not available or empty. Contacting IS to get new ones");
		String[] foundEPRs = null;
		logger.info("Time_Counter -- Start Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		foundEPRs = findSearchMasterEPRFeather(session);
		logger.info("Time_Counter -- Finished Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");

		ArrayList<String> discoveredEPRs = new ArrayList<String>();
		for (String epr : foundEPRs)
			discoveredEPRs.add(epr);

		if (foundEPRs == null || foundEPRs.length == 0) {
			logger.debug("No Search Masters Found");
			return null;
//			throw new NoSearchMasterEPRFoundException();
		} else {
			logger.debug("Number of Search Master EPRs: " + foundEPRs.length);
			CacheManager.getInstance().getCache("CachedSearchEPRs").put(new Element(session.getScope(), discoveredEPRs));
		}
		for (int i = 0; i < foundEPRs.length; i++) {
			try{
				logger.debug("////////////////////////Parsing query again!!!///////////////////////////////");
				logger.info("Time_Counter -- Start submitting generic search to EPR " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
						foundEPRs[i], session);
				logger.info("Time_Counter -- Finished submitting generic search to EPR, got results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				if ((recordsStream != null) && (!recordsStream.isClosed())) {
					logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC = new ResultSetConsumer(recordsStream, searchType,
							onlyTitleSnippet);
					logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC.setGenericSearchType(genericSearchType);
					searchRSC.setSearchStartTime(searchStartTime); 
					return searchRSC;
				} else
					logger.debug("New discovered Search service returned either a null or a closed records stream.");
			}catch(SearchASLException ex){
				logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
			}
			catch(MalformedURLException ex){
				logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
			}
			catch(gRS2CreationException ex){
				logger.debug("ResultSetConsumer could not be created");
			}
		}
//		//if execution has reached this point, means that the cached instances are not working, so remove them and let it renew them on the next run.
//		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("NONE OF THE CACHED EPRs DID WORK, NEITHER CONTACTING I.S. HAS REVEALED ANY NEW ONES! WILL THROW A SEARCH EXCEPTION");
		throw new SearchASLException(new Exception("Either no available instance of search service exists OR all search instances failed to perform the search."));

	}
	

	public ResultSetConsumerI browse(ASLSession session, ISearchClient searchClient)
			throws InitialBridgingNotCompleteException, InternalErrorException, SearchASLException {

		logger.info("Time_Counter -- Called browse, start counting time");
		searchStartTime = System.currentTimeMillis();

		if (!distinct)
			searchType = SearchType.Browse;
		else
			searchType = SearchType.BrowseFields;
		ResultSetConsumer.removeSessionVariables(session);

		logger.info("Time_Counter -- Started creating browse query "
				+ (System.currentTimeMillis() - searchStartTime) / 1000.0
				+ " sec(s)");
		String[] query = createBrowseQuery(session);
		logger.info("Time_Counter -- Finished creating browse query "
				+ (System.currentTimeMillis() - searchStartTime) / 1000.0
				+ " sec(s)");

		queryDescription = query[1];

		logger.debug("The browse Query is: ///////////////////////////////////////////////////////");
		logger.debug(query[0]);
		logger.debug("/////////////////////////////////////////////////////////////////////////");

		enableEPRCache();
		
		// check if there is any EPR cached
		logger.debug("Checking for cached Search Master EPRs in this scope");
		ArrayList<String> cachedEPRs = null;
		
		logger.info("Time_Counter -- Started Send browse query to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		if( (CacheManager.getInstance().getCache("CachedSearchEPRs") != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()) != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue() != null))
			cachedEPRs = (ArrayList<String>) CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue();
		if ((cachedEPRs != null) && (cachedEPRs.size() > 0)) {
			for (int i = 0; i < cachedEPRs.size(); i++) {
				try{
					logger.debug("Acquiring result stream from " + cachedEPRs.get(i));
					logger.info("Time_Counter -- Start submitting browse query to EPR " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0], cachedEPRs.get(i), session);
					logger.info("Time_Counter -- Finished submitting browse query to EPR, got the Result Stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					if ((recordsStream != null) && (!recordsStream.isClosed())) {
						logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC = new ResultSetConsumer(recordsStream, searchType, false);
						logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						searchRSC.setGenericSearchType(genericSearchType);
						searchRSC.setSearchStartTime(searchStartTime); 
						logger.info("Time_Counter -- Finished Send browse query to cached Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
						return searchRSC;
					} else
						logger.debug("Search service: "
								+ cachedEPRs.get(i)
								+ " returned either a null or a closed records stream.");
				}
				catch(SearchASLException ex){
					logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
				}
				catch(MalformedURLException ex){
					logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
				}
				catch(gRS2CreationException ex){
					logger.debug("ResultSetConsumer could not be created");
				}
			}
		}
		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.info("Time_Counter -- Stopped Send browse query to cached Search EPRs. No cached EPRs  " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		logger.debug("Cached Search Master EPRs in this scope are either not available or empty. Contacting IS to get new ones");
		String[] foundEPRs = null;
		logger.info("Time_Counter -- Start Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		foundEPRs = findSearchMasterEPRFeather(session);
		logger.info("Time_Counter -- Finished Searching IS for Search EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		ArrayList<String> discoveredEPRs = new ArrayList<String>();
		for (String epr : foundEPRs){
			logger.debug("Adding epr: " + epr);
			discoveredEPRs.add(epr);
		}
		if (foundEPRs == null || foundEPRs.length == 0) {
			logger.debug("No Search Masters Found");
			return null;
//			throw new NoSearchMasterEPRFoundException();
		} else {
			logger.debug("Number of Search Master EPRs: " + foundEPRs.length);
			CacheManager.getInstance().getCache("CachedSearchEPRs").put(new Element(session.getScope(), discoveredEPRs));
		}
		for (int i = 0; i < foundEPRs.length; i++) {
			try{
				logger.debug("////////////////////////Parsing query again!!!///////////////////////////////");
				logger.info("Time_Counter -- Start submitting browse to EPRs " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
						foundEPRs[i], session);
				logger.info("Time_Counter -- Finished submitting browse to EPRs , got results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
				if ((recordsStream != null) && (!recordsStream.isClosed())) {
					logger.info("Time_Counter -- Start creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC = new ResultSetConsumer(recordsStream, searchType, false);
					logger.info("Time_Counter -- Finished creating the Result set consumer " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
					searchRSC.setGenericSearchType(genericSearchType);
					searchRSC.setSearchStartTime(searchStartTime); 
					return searchRSC;
				} else
					logger.debug("New discovered Search service returned either a null or a closed records stream.");
			}
			catch(SearchASLException ex){
				logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
			}
			catch(MalformedURLException ex){
				logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
			}
			catch(gRS2CreationException ex){
				logger.debug("ResultSetConsumer could not be created");
			}
		}
		//if execution has reached this point, means that the cached instances are not working, so remove them and let it renew them on the next run.
//		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("NONE OF THE CACHED EPRs DID WORK, NEITHER CONTACTING I.S. HAS REVEALED ANY NEW ONES! WILL THROW A SEARCH EXCEPTION");
		throw new SearchASLException(new Exception("Either no available instance of search service exists OR all search instances failed to perform the search."));
	}

	public ResultSetConsumerI quickSearch(ASLSession session, String keyword, ISearchClient searchClient) throws SearchASLException {
		
		searchType = SearchType.QuickSearch;

		searchStartTime = System.currentTimeMillis();
		
		logger.debug("Inside quick search!");
		ResultSetConsumer.removeSessionVariables(session);
		// Check if there is a wildcard
		// if (keyword.contains("*")) {
		// if (!keyword.startsWith("\"") || !keyword.endsWith("\"")) {
		// String newKeyword = "\"" + keyword + "\"";
		// keyword = newKeyword;
		// }
		// }

		logger.debug("Trying to create quick query");
		String[] query = createQuickQuery(session, keyword);
		queryDescription = query[1];

		// filter out "," from query - prevents query parser exceptions
		query[0] = query[0].replace(",", " ");

		logger.debug("/////////////////////////////////////////////////////////////////////////");
		logger.debug("Quick Query: " + query[0]);
		logger.debug("/////////////////////////////////////////////////////////////////////////");

		enableEPRCache();
		
		// check if there is any EPR cached
		logger.debug("Checking for cached Search Master EPRs in this scope");
		ArrayList<String> cachedEPRs = null;
		if( (CacheManager.getInstance().getCache("CachedSearchEPRs") != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()) != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue() != null))
			cachedEPRs = (ArrayList<String>) CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue();
		if ((cachedEPRs != null) && (cachedEPRs.size() > 0)) {
			for (int i = 0; i < cachedEPRs.size(); i++) {
				try{
					logger.debug("Acquiring result stream from "
							+ cachedEPRs.get(i));
					Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
							cachedEPRs.get(i), session);
					if ((recordsStream != null) && (!recordsStream.isClosed())) {
						searchRSC = new ResultSetConsumer(recordsStream,
								searchType, false);
						searchRSC.setGenericSearchType(genericSearchType);
						searchRSC.setSearchStartTime(searchStartTime); 
						return searchRSC;
					} else
						logger.debug("Search service: "
								+ cachedEPRs.get(i)
								+ " returned either a null or a closed records stream.");
				}catch(SearchASLException ex){
					logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
				}
				catch(MalformedURLException ex){
					logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
				}
				catch(gRS2CreationException ex){
					logger.debug("ResultSetConsumer could not be created");
				}
			}
		}
		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("Cached Search Master EPRs in this scope are either not available or empty. Contacting IS to get new ones");
		String[] foundEPRs = null;
//		try {
			foundEPRs = findSearchMasterEPRFeather(session);
			ArrayList<String> discoveredEPRs = new ArrayList<String>();
			for (String epr : foundEPRs)
				discoveredEPRs.add(epr);
			
//		} catch (Exception e1) {
//			logger.debug("Failed to discover any new endpoints, wrong URI" + e1);
//		}
		if (foundEPRs == null || foundEPRs.length == 0) {
			logger.debug("No Search Masters Found, returning null");
			return null;
//			throw new NoSearchMasterEPRFoundException();
		} else {
			logger.debug("Number of Search Master EPRs: " + foundEPRs.length);
			CacheManager.getInstance().getCache("CachedSearchEPRs").put(new Element(session.getScope(), discoveredEPRs));
		}
		for (int i = 0; i < foundEPRs.length; i++) {
			try{
				logger.debug("////////////////////////Parsing query again!!!///////////////////////////////");
				Stream<GenericRecord> recordsStream = submitSearch(searchClient, query[0],
						foundEPRs[i], session);
				if ((recordsStream != null) && (!recordsStream.isClosed())) {
					searchRSC = new ResultSetConsumer(recordsStream, searchType, false);
					searchRSC.setGenericSearchType(genericSearchType);
					searchRSC.setSearchStartTime(searchStartTime); 
					return searchRSC;
				} else
					logger.debug("New discovered Search service returned either a null or a closed records stream.");
			}catch(SearchASLException ex){
				logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
			}
			catch(MalformedURLException ex){
				logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
			}
			catch(gRS2CreationException ex){
				logger.debug("ResultSetConsumer could not be created");
			}
		}
		//if execution has reached this point, means that the cached instances are not working, so remove them and let it renew them on the next run.
		logger.debug("NONE OF THE CACHED EPRs DID WORK, NEITHER CONTACTING I.S. HAS REVEALED ANY NEW ONES! WILL THROW A SEARCH EXCEPTION");
		throw new SearchASLException(new Exception("Either no available instance of search service exists OR all search instances failed to perform the search."));
	}

	public String[] testSearchQuery(ASLSession session, boolean simple,
			boolean browse, String quick, ISearchClient searchClient)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		logger.debug("About to create Query for testing: ");
		String[] query = new String[2];

		if (!quick.equals("")) {
			query = createQuickQuery(session, quick);
		}

		else if (!browse) {
			try {
				query = createSearchQuery(session, simple);
			} catch (QuerySyntaxException e) {
				logger.error("Exception:", e);
			}
		} else {
			query = createBrowseQuery(session);
		}

		return query;

	}

	/**
	 * Submits the CQL query to SearchMaster
	 * 
	 * @param session
	 *            the D4Science session to be used
	 * @param query
	 *            the query described in gCQL query language
	 * @return a consumer to retrieve the results as pages
	 * @throws gRS2CreationException
	 * @throws NoSearchMasterEPRFoundException
	 * @throws SearchASLException 
	 * @throws ResultsStreamRetrievalException
	 * @throws MalformedURLException
	 */
	public ResultSetConsumerI submitCQLQuery(ASLSession session, String query, ISearchClient searchClient)
			throws NoSearchMasterEPRFoundException, SearchASLException {

		searchStartTime = System.currentTimeMillis();
		
		logger.debug("inside submitCQLQuery() ");

		ResultSetConsumer.removeSessionVariables(session);
		
		enableEPRCache();
		
		// filter out "," from query - prevents query parser exceptions
		query = query.replace(",", " ");
		
		// check if there is any EPR cached
		logger.debug("Checking for cached Search Master EPRs in this scope");
		ArrayList<String> cachedEPRs = null;
		if( (CacheManager.getInstance().getCache("CachedSearchEPRs") != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()) != null) && (CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue() != null))
			cachedEPRs = (ArrayList<String>) CacheManager.getInstance().getCache("CachedSearchEPRs").get(session.getScope()).getObjectValue();
		if ((cachedEPRs != null) && (cachedEPRs.size() > 0)) {
			for (int i = 0; i < cachedEPRs.size(); i++) {
				try{
					logger.debug("Acquiring result stream from "
							+ cachedEPRs.get(i));
					Stream<GenericRecord> recordsStream = submitSearch(searchClient, query,
							cachedEPRs.get(i), session);
					if ((recordsStream != null) && (!recordsStream.isClosed())) {
						searchRSC = new ResultSetConsumer(recordsStream,
								searchType, false);
						searchRSC.setGenericSearchType(genericSearchType);
						searchRSC.setSearchStartTime(searchStartTime); 
						return searchRSC;
					} else
						logger.debug("Search service: "
								+ cachedEPRs.get(i)
								+ " returned either a null or a closed records stream.");
				}catch(SearchASLException ex){
					logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
				}
				catch(MalformedURLException ex){
					logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
				}
				catch(gRS2CreationException ex){
					logger.debug("ResultSetConsumer could not be created");
				}
			}
		}
		
		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("Cached Search Master EPRs in this scope are either not available or empty. Contacting IS to get new ones");
		String[] foundEPRs = null;

		foundEPRs = findSearchMasterEPRFeather(session);
		// add them also on cache
		ArrayList<String> discoveredEPRs = new ArrayList<String>();
		for (String epr : foundEPRs)
			discoveredEPRs.add(epr);

		if (foundEPRs == null || foundEPRs.length == 0) {
			logger.debug("No Search Masters Found");
			throw new NoSearchMasterEPRFoundException();
		} else {
			logger.debug("Number of Search Master EPRs: " + foundEPRs.length);
			CacheManager.getInstance().getCache("CachedSearchEPRs").put(new Element(session.getScope(), discoveredEPRs));
		}
		for (int i = 0; i < foundEPRs.length; i++) {
			try{
				logger.debug("////////////////////////Parsing query again!!!///////////////////////////////");
				Stream<GenericRecord> recordsStream = submitSearch(searchClient, query,
						foundEPRs[i], session);
				if ((recordsStream != null) && (!recordsStream.isClosed())) {
					searchRSC = new ResultSetConsumer(recordsStream, searchType,
							false);
					searchRSC.setGenericSearchType(genericSearchType);
					searchRSC.setSearchStartTime(searchStartTime); 
					return searchRSC;
				} else
					logger.debug("New discovered Search service returned either a null or a closed records stream.");
			}catch(SearchASLException ex){
				logger.debug("Using epr: "+cachedEPRs.get(i) +" has failed to execute the search");
			}
			catch(MalformedURLException ex){
				logger.debug("Endpoint "+cachedEPRs.get(i) +" for search is not a valid url");
			}
			catch(gRS2CreationException ex){
				logger.debug("ResultSetConsumer could not be created");
			}
		}
//		//if execution has reached this point, means that the cached instances are not working, so remove them and let it renew them on the next run.
//		CacheManager.getInstance().getCache("CachedSearchEPRs").remove(session.getScope());
		logger.debug("NONE OF THE CACHED EPRs DID WORK, NEITHER CONTACTING I.S. HAS REVEALED ANY NEW ONES! WILL THROW A SEARCH EXCEPTION");
		throw new SearchASLException(new Exception("Either no available instance of search service exists OR all search instances failed to perform the search."));
	}

	private void findAvailableFts(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		ftsAvailable = false;
		if (selectedRealCollections != null
				&& selectedRealCollections.size() != 0) {
			SearchHelper searchH = new SearchHelper(session);
			HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = searchH
					.getAvailableCollections();

			logger.debug("Inside findAvailableFTS");

			for (int i = 0; i < selectedRealCollections.size(); i++) {
				logger.debug("Trying to find collection info");
				CollectionInfo colInfo = FindFieldsInfo.findCollectionInfo(
						selectedRealCollections.get(i), collections);
				if (colInfo != null) {
					if (!colInfo.isFts()) {
						ftsAvailable = false;
						logger.debug("no available fts");
						break;
					} else {
						ftsId = colInfo.getFtsId();
						logger.debug("Col Info fts!: " + ftsId);
						ftsAvailable = true;
					}
				}
			}
		} else {
			ftsAvailable = false;
		}
	}

	private void findAvailableGeospatial(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		if (selectedRealCollections != null
				&& selectedRealCollections.size() != 0) {
			SearchHelper searchH = new SearchHelper(session);
			HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = searchH
					.getAvailableCollections();

			logger.debug("Inside findAvailableGeospatial");
			// boolean found = false;
			for (int i = 0; i < selectedRealCollections.size(); i++) {
				logger.debug("Trying to find collection info");
				CollectionInfo colInfo = FindFieldsInfo.findCollectionInfo(
						selectedRealCollections.get(i), collections);
				if (colInfo != null) {
					if (!colInfo.isGeospatial()) {
						geoAvailable = false;
						break;
					} else {
						geoAvailable = true;
						geoField = colInfo.getGeospatialField();
					}

				}
			}
			// geoAvailable = true;
		} else
			geoAvailable = false;
	}

	protected String[] createSearchQuery(ASLSession session, boolean simple)
			throws QuerySyntaxException, InitialBridgingNotCompleteException,
			InternalErrorException {
		
		// logger.info("inside createSearchQuery");

		// ACCESS LOGGER preparation
		SearchHelper sh = new SearchHelper(session);
		ArrayList<String> externalCollections = sh.getExternalCollections();
		List<String> realCollections = getSelectedRealCollections(session);
		String[][] collectionsTable = new String[realCollections.size()][2];
		for (int c = 0; c < realCollections.size(); c++) {
			CollectionInfo colInfo = sh.findCollectionInfo(realCollections
					.get(c));
			if (externalCollections.contains(colInfo.getId())) {
				collectionsTable[c][0] = colInfo.getName()
						+ "_externalCollection";
				collectionsTable[c][1] = colInfo.getId();
			} else {
				collectionsTable[c][0] = colInfo.getName();
				collectionsTable[c][1] = colInfo.getId();
			}
		}
		
		String[] q = new String[2];
		GCQLNode projectionNode = null;
		GCQLNode sortNode = null;

		if (!searchType.equals(SearchType.PreviousSearch)) {
			/* Get the projection and sort by part */
			projectionNode = getPresentationPart(session);
			if (sortBy != null && !sortBy.equals("")) {
				logger.debug("Sortby node");
				sortNode = getSortByPart(session);
			}
		}
		
		GCQLNode termsNode = getCriteriaPart(session, simple);

		if (termsNode == null
				&& (geospatialInfo == null || geospatialInfo.getBounds() == null)) {
			throw new QuerySyntaxException("No criteria specified");
		}
		GCQLNode collectionsPart = getCollectionsQueryPart(session);
		if (searchType.equals(SearchType.PreviousSearch)) {
			// We have previous search: we need to remove the project and sort
			// nodes from the initial query - and add the new criteria
			GCQLProjectNode projectNode = (GCQLProjectNode) previousQuery;
			GCQLNode subtree = projectNode.subtree;
			GCQLSortNode sortInitialNode = null;
			GCQLNode tree = null;
			if (subtree instanceof GCQLSortNode) {
				sortInitialNode = (GCQLSortNode) subtree;
				tree = sortInitialNode.subtree;
			} else
				tree = subtree;

			// Create a boolean Node to connect the previous with the new
			// criteria
			GCQLAndNode andNode = new GCQLAndNode();
			andNode.left = tree;
			andNode.right = termsNode;
			if (sortInitialNode != null) {
				sortInitialNode.subtree = andNode;
				projectNode.subtree = sortInitialNode;
			} else {
				projectNode.subtree = andNode;
			}

			queryString = projectNode.toCQL();
			queryDescription = getQueryDescriptionForPreviousQuery(session);
			q[0] = queryString;
			q[1] = queryDescription;
			return q;
		}
		if (simple) {
			GCQLAndNode andNode = new GCQLAndNode();
			andNode.left = termsNode;
			andNode.right = collectionsPart;

			GCQLProjectNode projNode = null;
			if (projectionNode != null) {
				projNode = (GCQLProjectNode) projectionNode;
				if (sortNode != null) {
					logger.debug("Simple sortby");
					GCQLSortNode sort_node = (GCQLSortNode) sortNode;
					sort_node.subtree = andNode;
					projNode.subtree = sort_node;
				} else {
					projNode.subtree = andNode;
				}
			} else {
				if (sortNode != null) {
					logger.debug("Simple sortby");
					GCQLSortNode sort_node = (GCQLSortNode) sortNode;
					sort_node.subtree = andNode;
				}
			}

			if (projNode != null)
				queryString = projNode.toCQL();
			else {
				if (sortNode != null)
					queryString = sortNode.toCQL();
				else
					queryString = andNode.toCQL();
			}

			// TODO - Check!
			if (this.rankingSupport) {

				/*
				 * Create a string with all search terms, separated by empty
				 * space.
				 */
				String searchTerms = new String();

				int totalSearchTerms = searchQueryTerms.size();

				/*
				 * In case of multiple search terms, start fusion with the "
				 * character.
				 */
				if (totalSearchTerms > 1)
					searchTerms += "\"";

				for (int i = 0; i < (totalSearchTerms - 1); ++i)
					searchTerms += (searchQueryTerms.get(i) + " ");

				/* Add last search term, without a trailing space. */
				searchTerms += searchQueryTerms.get(totalSearchTerms - 1);

				/*
				 * In case of multiple search terms, end fusion with the "
				 * character.
				 */
				if (totalSearchTerms > 1)
					searchTerms += "\"";

				/* Add support for data fusion. */
				queryString += " fuse " + searchTerms;
			}

			queryDescription = getQueryDescriptionForSimple(session);
			q[0] = queryString;
			q[1] = queryDescription;

			// ACCESS LOGGER
			if(semanticEnrichment){
				SemanticEnrichmentAccessLogEntry enrichedEntry = new SemanticEnrichmentAccessLogEntry(collectionsTable, this.searchTerm);
				accessLogger.logEntry(session.getUsername(), session.getScopeName(), enrichedEntry);
			}
			else{
				SimpleSearchAccessLogEntry simpleEntry = new SimpleSearchAccessLogEntry(collectionsTable, this.searchTerm);
				accessLogger.logEntry(session.getUsername(), session.getScopeName(), simpleEntry);
			}
			
			return q;
		} else { //advanced search
			if (geospatialInfo == null || geospatialInfo.getBounds() == null) {
				GCQLNode languageNode = getLanguageQueryPart(session);
				GCQLAndNode andNode1 = new GCQLAndNode();
				andNode1.left = collectionsPart;
				andNode1.right = languageNode;
				GCQLAndNode andNode2 = new GCQLAndNode();
				andNode2.left = termsNode;
				andNode2.right = andNode1;
				GCQLProjectNode projNode = null;
				if (projectionNode != null) {
					projNode = (GCQLProjectNode) projectionNode;
					if (sortNode != null) {
						logger.debug("Advanced sortBy");
						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
						sort_node.subtree = andNode2;
						projNode.subtree = sort_node;
					} else {
						projNode.subtree = andNode2;
					}
				} else {
					if (sortNode != null) {
						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
						sort_node.subtree = andNode2;
					}
				}

				if (projNode != null)
					queryString = projNode.toCQL();
				else if (sortNode != null)
					queryString = sortNode.toCQL();
				else
					queryString = andNode2.toCQL();

				queryDescription = getQueryDescriptionForAdvanced(session);
				q[0] = queryString;
				q[1] = queryDescription;

				// ACCESS LOGGER
				String[][] criteriaTable = new String[criteria.size()][2];
				for (int m = 0; m < criteria.size(); m++) {
					criteriaTable[m][0] = criteria.get(m).getSearchFieldName();
					// logger.info(criteriaTable[m][0]);
					criteriaTable[m][1] = criteria.get(m).getSearchFieldValue();
					// logger.info(criteriaTable[m][1]);
				}
				String oper;
				if (operator == Operator.AND) {
					oper = "AND";
				} else
					oper = "OR";
				
				if(semanticEnrichment){
					SemanticEnrichmentAccessLogEntry enrichedEntry = new SemanticEnrichmentAccessLogEntry(collectionsTable, this.searchTerm);
					accessLogger.logEntry(session.getUsername(), session.getScopeName(), enrichedEntry);
				}
				else{
					AdvancedSearchAccessLogEntry advancedEntry = new AdvancedSearchAccessLogEntry(collectionsTable, criteriaTable, oper);
					accessLogger.logEntry(session.getUsername(),session.getScopeName(), advancedEntry);
				}
				
				return q;
			} else {
				// geospatial search

				// create first the geospatial part
				// GCQLNode languageNode = getLanguageQueryPart(session);
				// GCQLNode geoCollectionsPart =
				// getGeoCollectionsQueryPart(session);
				// GCQLAndNode andNode1 = new GCQLAndNode();
				// andNode1.left = geoCollectionsPart;
				// andNode1.right = languageNode;
				// GCQLAndNode andNode2 = new GCQLAndNode();

				// List<String> realCollections =
				// getSelectedRealCollections(session);
				SearchHelper s_h = new SearchHelper(session);
				CollectionInfo colInfo = null;
				ArrayList<GCQLNode> geoNodes = new ArrayList<GCQLNode>();
				for (int i = 0; i < realCollections.size(); i++) {
					colInfo = s_h.findCollectionInfo(realCollections.get(i));
					if (colInfo.isGeospatial()) {
						GCQLNode geoNd = getGeoQueryPart(session,
								colInfo.getId());
						geoNodes.add(geoNd);
					}
				}
				// GCQLNode geoQueryPart = getGeoQueryPart(session);
				// andNode2.left = geoQueryPart;
				// andNode2.right = andNode1;

				GCQLNode geoQueryPart = null;
				if (geoNodes.size() > 1) {
					GCQLOrNode previousNode = null;
					for (int i = 0; i < geoNodes.size() - 1; i++) {
						if (previousNode == null) {
							previousNode = new GCQLOrNode();
							previousNode.left = geoNodes.get(i);
							previousNode.right = geoNodes.get(i + 1);
						} else {
							GCQLOrNode orNd = new GCQLOrNode();
							orNd.left = previousNode;
							orNd.right = geoNodes.get(i + 1);
							previousNode = orNd;
						}
					}

					geoQueryPart = previousNode;
				} else {
					geoQueryPart = geoNodes.get(0);
				}

				// create the criteria part node
				GCQLAndNode andNode3 = null;
				if (criteria != null && criteria.size() != 0) {
					andNode3 = new GCQLAndNode();
					// andNode3.left = andNode2;
					andNode3.left = geoQueryPart;
					andNode3.right = termsNode;
				}

				GCQLProjectNode projNode = null;
				if (projectionNode != null) {
					projNode = (GCQLProjectNode) projectionNode;
					if (sortNode != null) {
						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
						if (andNode3 != null)
							sort_node.subtree = andNode3;
						else {
							// sort_node.subtree = andNode2;
							sort_node.subtree = geoQueryPart;
						}

						projNode.subtree = sort_node;
					} else {
						if (andNode3 != null) {
							projNode.subtree = andNode3;
						} else {
							// projNode.subtree = andNode2;
							projNode.subtree = geoQueryPart;
						}
					}
				} else {
					if (sortNode != null) {
						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
						if (andNode3 != null)
							sort_node.subtree = andNode3;
						else {
							// sort_node.subtree = andNode2;
							sort_node.subtree = geoQueryPart;
						}
					}
				}

				if (projNode != null)
					queryString = projNode.toCQL();
				else if (sortNode != null)
					queryString = sortNode.toCQL();
				else if (andNode3 != null)
					queryString = andNode3.toCQL();
				// else if (andNode2 != null)
				// queryString = andNode2.toCQL();
				else if (geoQueryPart != null)
					queryString = geoQueryPart.toCQL();
				queryDescription = getQueryDescriptionForGeospatial(session);
				q[0] = queryString;
				q[1] = queryDescription;

				return q;
			}
		}
	}

	

	/**
	 * Used only from tagcloud-visualisation-widget
	 */
	public String createIndexVisQuery(ASLSession session, ArrayList<String> searchTerms) throws InitialBridgingNotCompleteException, InternalErrorException, QuerySyntaxException {
		
		if((session.getScope()==null) || session.getScope().isEmpty()){
			logger.error("Session has stored an empty scope !");
		}
		
		searchType = SearchType.AdvancedSearch;
		boolean simple = false;
	
		logger.debug("inside createIndexVisQuery");

		// ACCESS LOGGER preparation
		SearchHelper sh = new SearchHelper(session);
		ArrayList<String> externalCollections = sh.getExternalCollections();
		List<String> realCollections = getSelectedRealCollections(session);
		String[][] collectionsTable = new String[realCollections.size()][2];
		for (int c = 0; c < realCollections.size(); c++) {
			CollectionInfo colInfo = sh.findCollectionInfo(realCollections.get(c));
			if (externalCollections.contains(colInfo.getId())) {
				collectionsTable[c][0] = colInfo.getName() + "_externalCollection";
				collectionsTable[c][1] = colInfo.getId();
			} else {
				collectionsTable[c][0] = colInfo.getName();
				collectionsTable[c][1] = colInfo.getId();
			}
		}

		String[] q = new String[2];
		GCQLNode projectionNode = null;
		GCQLNode sortNode = null;

		
		
		GCQLNode termsNode = getCriteriaPart(session, simple);

		if (termsNode == null && (geospatialInfo == null || geospatialInfo.getBounds() == null)) {
			throw new QuerySyntaxException("No criteria specified");
		}
		GCQLNode collectionsPart; //= getCollectionsQueryPart(session);

		///////////////////
		logger.debug("num_collections_before_filtering: "+realCollections.size());
		Iterator <String> iter = realCollections.iterator();
		while(iter.hasNext()){
			CollectionInfo colInfo = sh.findCollectionInfo(iter.next());
			if(("opensearch").equalsIgnoreCase(colInfo.getCollectionType())){
				logger.debug("Removed opensearch collection: "+colInfo.getName());
				iter.remove();
			}
			else{
				logger.debug("Keeping non-opensearch collection: "+colInfo.getName());
			}
		}
		logger.debug("num_collections_after_filtering: "+realCollections.size());
		

		GCQLRelation colRelation = new GCQLRelation();
		colRelation.setBase(SearchConstants.CollectionsRelation);

		// Create a GCQLTermNode for the First Collection
		GCQLTermNode collectionNode = new GCQLTermNode();
		collectionNode.setIndex(SearchConstants.CollectionField);
		collectionNode.setRelation(colRelation);
		collectionNode.setTerm(realCollections.get(0));
		if (realCollections.size() == 1) {
			collectionsPart = collectionNode;
		}

		// we have more than one collections
		GCQLOrNode previousOrNode = null;

		for (int i = 1; i < realCollections.size(); i++) {
			GCQLTermNode newColNode = new GCQLTermNode();
			newColNode.setIndex(SearchConstants.CollectionField);
			newColNode.setRelation(colRelation);
			newColNode.setTerm(realCollections.get(i));
			if (previousOrNode == null) {
				previousOrNode = new GCQLOrNode();
				previousOrNode.left = collectionNode;
				previousOrNode.right = newColNode;
			} else {
				GCQLOrNode newOrNode = new GCQLOrNode();
				newOrNode.right = previousOrNode;
				newOrNode.left = newColNode;
				previousOrNode = newOrNode;
			}
		}

		collectionsPart = previousOrNode;
		
		///////////////

//		else { //advanced search
//			if (geospatialInfo == null || geospatialInfo.getBounds() == null) {
				GCQLNode languageNode = getLanguageQueryPart(session);
				GCQLAndNode andNode1 = new GCQLAndNode();
				andNode1.left = collectionsPart;
				andNode1.right = languageNode;
				GCQLAndNode andNode2 = new GCQLAndNode();
				andNode2.left = termsNode;
				andNode2.right = andNode1;
//				GCQLProjectNode projNode = null;
//				if (projectionNode != null) {
//					projNode = (GCQLProjectNode) projectionNode;
//					if (sortNode != null) {
//						logger.debug("Advanced sortBy");
//						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
//						sort_node.subtree = andNode2;
//						projNode.subtree = sort_node;
//					} else {
//						projNode.subtree = andNode2;
//					}
//				} else {
//					if (sortNode != null) {
//						GCQLSortNode sort_node = (GCQLSortNode) sortNode;
//						sort_node.subtree = andNode2;
//					}
//				}
//
//				if (projNode != null)
//					queryString = projNode.toCQL();
//				else if (sortNode != null)
//					queryString = sortNode.toCQL();
//				else
					queryString = andNode2.toCQL();

				queryDescription = getQueryDescriptionForAdvanced(session);
				q[0] = queryString;
				q[1] = queryDescription;

				// ACCESS LOGGER
				String[][] criteriaTable = new String[criteria.size()][2];
				for (int m = 0; m < criteria.size(); m++) {
					criteriaTable[m][0] = criteria.get(m).getSearchFieldName();
					// logger.info(criteriaTable[m][0]);
					criteriaTable[m][1] = criteria.get(m).getSearchFieldValue();
					// logger.info(criteriaTable[m][1]);
				}
				String oper;
				if (operator == Operator.AND) {
					oper = "AND";
				} else
					oper = "OR";
				
				if(semanticEnrichment){
					SemanticEnrichmentAccessLogEntry enrichedEntry = new SemanticEnrichmentAccessLogEntry(collectionsTable, this.searchTerm);
					accessLogger.logEntry(session.getUsername(), session.getScopeName(), enrichedEntry);
				}
				else{
					AdvancedSearchAccessLogEntry advancedEntry = new AdvancedSearchAccessLogEntry(collectionsTable, criteriaTable, oper);
					accessLogger.logEntry(session.getUsername(),session.getScopeName(), advancedEntry);
				}
				
				return q[0];
//			}
		
		
	}
	
	
	// check this later to add there also the snippet
	private String[] createGenericSearchQuery(ASLSession session,
			List<String> terms) throws QuerySyntaxException,
			InitialBridgingNotCompleteException, InternalErrorException {
		// ACCESS LOGGER preparation
		SearchHelper sh = new SearchHelper(session);
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> availableCollections = sh.getAvailableCollections();
		logger.debug("genericSearch -> available collections #: " + availableCollections.keySet().size());
		// String[][] collectionsTable = new String[realCollections.size()][2];
		Iterator<Entry<CollectionInfo, ArrayList<CollectionInfo>>> iterator = availableCollections.entrySet().iterator();
		List<String> names = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		while (iterator.hasNext()) {
			Entry<CollectionInfo, ArrayList<CollectionInfo>> entry = iterator.next();
			for (CollectionInfo collection : entry.getValue()) {
				if (collection.isFts()) {
					names.add(collection.getName());
					ids.add(collection.getId());
					ftsId = collection.getFtsId();
				}
			}
		}
		String[][] collectionsTable = new String[names.size()][2];
		for (int i = 0; i < names.size(); i++) {
			collectionsTable[i][0] = names.get(i);
			collectionsTable[i][1] = ids.get(i);
		}

		String[] q = new String[2];
		GCQLNode projectionNode = null;
		GCQLNode sortNode = null;

		if (!searchType.equals(SearchType.PreviousSearch)) {
			/* Get the projection and sort by part */
			logger.debug("getting presentation part");
			projectionNode = getGenericSearchPresentationPart(availableCollections, session);
			if (sortBy != null && !sortBy.equals("")) {
				logger.debug("Sortby node");
				sortNode = getSortByPart(session);
			}
		}
		
		List<String> sanitizedTerms = new ArrayList<String>();
		for(String st: terms)
			sanitizedTerms.add(StringEscapeUtils.unescapeHtml(QuerySanitizer.sanitizeQuery(st)));
		
		GCQLNode termsNode = getGenericCriterialPart(session, sanitizedTerms);

		if (termsNode == null
				&& (geospatialInfo == null || geospatialInfo.getBounds() == null)) {
			throw new QuerySyntaxException("No criteria specified");
		}
		// logger.info("Printing criterial part " + termsNode.toCQL());

		GCQLNode collectionsPart = getCollectionsGenericQueryPart(session);
		
		
		
		if (searchType.equals(SearchType.PreviousSearch)) {
			logger.info("There was a previous Generic Search Query ");
			// We have previous search: we need to remove the project and sort
			// nodes from the initial query - and add the new criteria
			GCQLProjectNode projectNode = (GCQLProjectNode) previousQuery;
			GCQLNode subtree = projectNode.subtree;
			GCQLSortNode sortInitialNode = null;
			GCQLNode tree = null;
			if (subtree instanceof GCQLSortNode) {
				sortInitialNode = (GCQLSortNode) subtree;
				tree = sortInitialNode.subtree;
			} else
				tree = subtree;

			// Create a boolean Node to connect the previous with the new
			// criteria
			GCQLAndNode andNode = new GCQLAndNode();
			andNode.left = tree;
			andNode.right = termsNode;
			if (sortInitialNode != null) {
				sortInitialNode.subtree = andNode;
				projectNode.subtree = sortInitialNode;
			} else {
				projectNode.subtree = andNode;
			}

			queryString = projectNode.toCQL();
			queryDescription = getQueryDescriptionForPreviousQuery(session);
			q[0] = queryString;
			q[1] = queryDescription;
			return q;
		}
		GCQLAndNode andNode = new GCQLAndNode();
		andNode.left = termsNode;
		andNode.right = collectionsPart;

		GCQLProjectNode projNode = null;
		if (projectionNode != null) {
			projNode = (GCQLProjectNode) projectionNode;
			if (sortNode != null) {
				logger.debug("Simple sortby");
				GCQLSortNode sort_node = (GCQLSortNode) sortNode;
				sort_node.subtree = andNode;
				projNode.subtree = sort_node;
			} else {
				projNode.subtree = andNode;
			}
		} else {
			if (sortNode != null) {
				logger.debug("Simple sortby");
				GCQLSortNode sort_node = (GCQLSortNode) sortNode;
				sort_node.subtree = andNode;
			}
		}

		if (projNode != null)
			queryString = projNode.toCQL();
		else {
			if (sortNode != null)
				queryString = sortNode.toCQL();
			else
				queryString = andNode.toCQL();
		}
		queryDescription = getQueryDescriptionForSimple(session);
		q[0] = queryString;
		q[1] = queryDescription;

		// ACCESS LOGGER
		SimpleSearchAccessLogEntry simpleEntry = new SimpleSearchAccessLogEntry(
				collectionsTable, this.searchTerm);
		accessLogger.logEntry(session.getUsername(), session.getScopeName(),
				simpleEntry);

		return q;
	}
	
	

	protected String[] createBrowseQuery(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		String[] q = new String[2];
		GCQLNode projectionNode = null;
		if (distinct) {
			logger.debug("getting distinct");
			projectionNode = getPresentationPartDistinct(session);
			logger.debug("Got presentation part browse distinct");
		} else {
			projectionNode = getPresentationPart(session);
			logger.debug("Got presentation part browse");
		}
		GCQLNode browseNode = getBrowseCriteriaPart(session);
		GCQLNode collectionsPart = getCollectionsQueryPart(session);

		GCQLAndNode andNode = new GCQLAndNode();
		andNode.left = browseNode;
		andNode.right = collectionsPart;

		if (projectionNode != null) {
			GCQLProjectNode projNode = (GCQLProjectNode) projectionNode;
			projNode.subtree = andNode;

			q[0] = projNode.toCQL();
		} else {
			q[0] = andNode.toCQL();
		}
		
		q[1] = getQueryDescriptionForBrowse(session);
		queryString = q[0];
		queryDescription = q[1];
		
		return q;
	}

	protected String[] createQuickQuery(ASLSession session, String keyword) {
		String[] q = new String[2];

		logger.debug("quick trying to get presentation part keyword is: "
				+ keyword);
		GCQLNode projectionNode = getPresentationPart(session);
		logger.debug("Quick got presentation part");
		GCQLNode quickNode = getQuickCriterionPart(session, keyword);
		logger.debug("Quick got criteria part");
		if (projectionNode != null) {
			GCQLProjectNode projNode = (GCQLProjectNode) projectionNode;
			projNode.subtree = quickNode;

			q[0] = projNode.toCQL();
		} else {
			q[0] = quickNode.toCQL();
		}
		q[1] = getQueryDescriptionForQuick(session, keyword);
		return q;
	}

	protected String getQueryDescriptionForSimple(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		String queryDescr = /* "Search for: */"\"" + searchTerm
				+ "\" in collection(s): ";
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		SearchHelper sh = new SearchHelper(session);
		for (int i = 0; i < selectedRealCollections.size(); i++) {
			CollectionInfo colInfo = sh
					.findCollectionInfo(selectedRealCollections.get(i));
			queryDescr += colInfo.getName() + ", ";
		}

		queryDescr = queryDescr.substring(0, queryDescr.length() - 2);
		if (sortBy != null && !sortBy.equals("")) {
			logger.debug("The sort by is simple: " + sortBy);
			queryDescr += ". Sort the results by: " + findSortFieldName(sortBy);
		}
		return queryDescr;
	}

	protected String getQueryDescriptionForQuick(ASLSession session,
			String keyword) {
		String queryDescr = "Search for: \"" + keyword
				+ "\"  in all collections";
		return queryDescr;
	}

	private String findSortFieldName(String sortById) {
		for (Field x : sortableFields) {
			logger.debug("comparing sortby: " + x.getId() + " with " + sortById);
			if (x.getId().trim().equals(sortById.trim()))
				return x.getName();
		}
		return "";
	}

	protected String getQueryDescriptionForBrowse(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		String queryDescr = "Browse by: "
				+ findSearchFieldName(browseBy.trim()) + " in collection(s): ";
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		SearchHelper sh = new SearchHelper(session);
		for (int i = 0; i < selectedRealCollections.size(); i++) {
			CollectionInfo colInfo = sh
					.findCollectionInfo(selectedRealCollections.get(i));
			queryDescr += colInfo.getName() + ", ";
		}

		queryDescr = queryDescr.substring(0, queryDescr.length() - 2);
		return queryDescr;
	}

	protected String getQueryDescriptionForAdvanced(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		// String queryDescr = "Search for: ";
		String queryDescr = new String();
		for (int i = 0; i < criteria.size(); i++) {
			logger.debug("Criterion name: "
					+ (criteria.get(i).getSearchFieldId()));
			queryDescr += findSearchFieldName(criteria.get(i)
					.getSearchFieldId());
			queryDescr += " = \"" + criteria.get(i).getSearchFieldValue();
			// queryDescr += "\", ";
			if (operator == Operator.OR) {
				queryDescr += "\" or ";
			} else {
				queryDescr += "\" and ";
			}
		}
		logger.debug(queryDescr);
		if (operator == Operator.OR)
			queryDescr = queryDescr.substring(0, queryDescr.length() - 4);
		else
			queryDescr = queryDescr.substring(0, queryDescr.length() - 5);
		queryDescr += " in collection(s): ";
		SearchHelper sh = new SearchHelper(session);
		List<String> selectedRealCollections = getSelectedRealCollections(session);
		for (int i = 0; i < selectedRealCollections.size(); i++) {
			CollectionInfo colInfo = sh
					.findCollectionInfo(selectedRealCollections.get(i));
			queryDescr += colInfo.getName() + ", ";
		}
		queryDescr += "in " + languages.get(selectedLanguage) + " language.";
		if (sortBy != null && !sortBy.equals("")) {
			logger.debug("The sortby field is: " + sortBy);
			queryDescr += " Sort the results by: " + findSortFieldName(sortBy);
		}
		return queryDescr;
	}

	protected String getQueryDescriptionForGeospatial(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		if (criteria != null && criteria.size() != 0)
			return getQueryDescriptionForAdvanced(session);
		else
			return "GeospatialSearch";
	}

	protected String getQueryDescriptionForPreviousQuery(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		String queryDescr = previousQueryDescription + ", and"
				+ getQueryDescriptionForAdvanced(session);
		return queryDescr;
	}

	private String findSearchFieldName(String sfId) {
		for (Field x : searchableFields) {
			logger.debug("Compare criterion: " + x.getId().trim() + " with  "
					+ sfId.trim());
			if (x.getId().trim().equals(sfId.trim())) {
				logger.debug("The name is: " + x.getName() + " and the label: "
						+ x.getLabel());
				return x.getName();
			} else {
				logger.debug("Not equal!");
			}
		}
		return "";
	}

	protected GCQLNode getCollectionsQueryPart(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		List<String> realCollections = getSelectedRealCollections(session);

		GCQLRelation colRelation = new GCQLRelation();
		colRelation.setBase(SearchConstants.CollectionsRelation);

		// Create a GCQLTermNode for the First Collection
		GCQLTermNode collectionNode = new GCQLTermNode();
		collectionNode.setIndex(SearchConstants.CollectionField);
		collectionNode.setRelation(colRelation);
		collectionNode.setTerm(realCollections.get(0));
		if (realCollections.size() == 1) {
			return collectionNode;
		}

		// we have more than one collections
		GCQLOrNode previousOrNode = null;

		for (int i = 1; i < realCollections.size(); i++) {
			GCQLTermNode newColNode = new GCQLTermNode();
			newColNode.setIndex(SearchConstants.CollectionField);
			newColNode.setRelation(colRelation);
			newColNode.setTerm(realCollections.get(i));
			if (previousOrNode == null) {
				previousOrNode = new GCQLOrNode();
				previousOrNode.left = collectionNode;
				previousOrNode.right = newColNode;
			} else {
				GCQLOrNode newOrNode = new GCQLOrNode();
				newOrNode.right = previousOrNode;
				newOrNode.left = newColNode;
				previousOrNode = newOrNode;
			}
		}

		return previousOrNode;
	}

	protected GCQLNode getCollectionsGenericQueryPart(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		// List<String> realCollections = getSelectedRealCollections(session);
		SearchHelper sh = new SearchHelper(session);
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> availableCollections = sh
				.getAvailableCollections();

		List<String> allCollections = new ArrayList<String>();
		Iterator<CollectionInfo> iter = availableCollections.keySet()
				.iterator();
		while (iter.hasNext()) {
			ArrayList<CollectionInfo> group = availableCollections.get(iter
					.next());
			for (int i = 0; i < group.size(); i++) {
				if (group.get(i).isFts())
					allCollections.add(group.get(i).getId());
			}
		}

		GCQLRelation colRelation = new GCQLRelation();
		colRelation.setBase(SearchConstants.CollectionsRelation);


		
		
		// Create a GCQLTermNode for the First Collection
		GCQLTermNode collectionNode = new GCQLTermNode();
		
		if(allCollections.isEmpty()) {
//			logger.error("No collections Returning an );
			return collectionNode;
		}
		
		collectionNode.setIndex(SearchConstants.CollectionField);
		collectionNode.setRelation(colRelation);
		collectionNode.setTerm(allCollections.get(0));
		if (allCollections.size() == 1) {
			return collectionNode;
		}

		// we have more than one collections
		GCQLOrNode previousOrNode = null;

		for (int i = 1; i < allCollections.size(); i++) {
			GCQLTermNode newColNode = new GCQLTermNode();
			newColNode.setIndex(SearchConstants.CollectionField);
			newColNode.setRelation(colRelation);
			newColNode.setTerm(allCollections.get(i));
			if (previousOrNode == null) {
				previousOrNode = new GCQLOrNode();
				previousOrNode.left = collectionNode;
				previousOrNode.right = newColNode;
			} else {
				GCQLOrNode newOrNode = new GCQLOrNode();
				newOrNode.right = previousOrNode;
				newOrNode.left = newColNode;
				previousOrNode = newOrNode;
			}
		}

		return previousOrNode;
	}
	


	protected GCQLNode getGeoQueryPart(ASLSession session, String collectionId) {
		GCQLTermNode geoNode = new GCQLTermNode();
		// geoNode.setIndex(SearchConstants.GEO_Field);
		geoNode.setIndex(geoField.getId());
		GCQLRelation geoRel = new GCQLRelation();
		geoRel.setBase(SearchConstants.GEO_Relation);
		Modifier inclusionMod = new Modifier(SearchConstants.geoInclusionMod,
				"=", SearchConstants.contains);

		Modifier collectionMod = new Modifier(SearchConstants.geoCollectionMod,
				"=", collectionId);
		Modifier languageMod = new Modifier(SearchConstants.geoLanguageMod,
				"=", languages.get(selectedLanguage));
		Modifier rankerMod = new Modifier(SearchConstants.geoRankerMod, "=",
				"\"" + SearchConstants.genericRanker + " false" + "\"");
		Modifier refinerMod = new Modifier(SearchConstants.geoRefinerMod, "=",
				"\"" + SearchConstants.timeSpanRefiner + " false "
						+ geospatialInfo.getStartingDateString() + " "
						+ geospatialInfo.getEndingDateString() + "\"");
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		// modifiers.add(inclusionMod);
		modifiers.add(collectionMod);
		modifiers.add(languageMod);
		modifiers.add(inclusionMod);
		modifiers.add(rankerMod);
		modifiers.add(refinerMod);
		geoRel.setModifiers(modifiers);
		geoNode.setRelation(geoRel);
		String coordinates = new String();
		Point[] points = geospatialInfo.getBounds();
		for (int i = 0; i < points.length; i++) {
			double x = points[i].getLongitude();
			double y = points[i].getLatitude();
			coordinates += String.valueOf(x) + " " + String.valueOf(y) + " ";
		}
		// cut the last space
		coordinates = "\"" + coordinates.substring(0, coordinates.length() - 1)
				+ "\"";
		geoNode.setTerm(coordinates);

		return geoNode;
	}

	protected GCQLNode getGeoCollectionsQueryPart(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {
		List<String> realCollections = getSelectedRealCollections(session);

		GCQLRelation colRelation = new GCQLRelation();
		colRelation.setBase(SearchConstants.CollectionsRelation);
		SearchHelper s_h = new SearchHelper(session);

		// Create a GCQLTermNode for the first collection that has a geo index
		GCQLTermNode collectionNode = null;
		int i;
		for (i = 0; i < realCollections.size(); i++) {
			CollectionInfo colInfo = s_h.findCollectionInfo(realCollections
					.get(i));
			if (colInfo.isGeospatial()) {
				collectionNode = new GCQLTermNode();
				collectionNode.setIndex(SearchConstants.CollectionField);
				collectionNode.setRelation(colRelation);
				collectionNode.setTerm(realCollections.get(i));
				break;
			}
		}

		if (collectionNode == null)
			return null;
		else {
			GCQLOrNode previousOrNode = null;
			for (; i < realCollections.size(); i++) {
				CollectionInfo colInfo = s_h.findCollectionInfo(realCollections
						.get(i));
				if (colInfo.isGeospatial()) {
					GCQLTermNode newColNode = new GCQLTermNode();
					newColNode.setIndex(SearchConstants.CollectionField);
					newColNode.setRelation(colRelation);
					newColNode.setTerm(realCollections.get(i));
					if (previousOrNode == null) {
						previousOrNode = new GCQLOrNode();
						previousOrNode.left = collectionNode;
						previousOrNode.right = newColNode;
					} else {
						GCQLOrNode newOrNode = new GCQLOrNode();
						newOrNode.right = previousOrNode;
						newOrNode.left = newColNode;
						previousOrNode = newOrNode;
					}
				}
			}

			if (previousOrNode == null)
				return collectionNode;
			else
				return previousOrNode;
		}
	}

	protected GCQLNode getGenericCriterialPart(ASLSession session,
			List<String> terms) throws QuerySyntaxException {
		logger.debug("Inside getGenericCriterialPart - the search type is: "
				+ searchType);
		if (terms.size() < 1) {
			throw new QuerySyntaxException("Empty term list given");
		}
		
		if(terms.size()==1){ //probably from portal...
			String term = terms.get(0); 
			if(!term.contains("\"") && term.contains(" ")){
				terms.clear();
				String[] trms = term.split(" ");
				for(String t : trms)
					terms.add(t);
			}
		}
		
		
		GCQLRelation fieldRelation = new GCQLRelation();
		fieldRelation.setBase(SearchConstants.CommonFieldRelation);
		GCQLTermNode criterionNode = new GCQLTermNode();
		criterionNode.setIndex(ftsId);
		criterionNode.setRelation(fieldRelation);
		criterionNode.setTerm(terms.get(0));
		
		if (terms.size() == 1) {
			return criterionNode;
		}
		GCQLOrNode previousOrNode = null;
		for (int i = 1; i < terms.size(); i++) {
			GCQLTermNode newCriterionNode = new GCQLTermNode();
			newCriterionNode.setIndex(ftsId);
			newCriterionNode.setRelation(fieldRelation);
			newCriterionNode.setTerm(terms.get(i));

			if (previousOrNode == null) {
				previousOrNode = new GCQLOrNode();
				previousOrNode.left = criterionNode;
				previousOrNode.right = newCriterionNode;
			} else {
				GCQLOrNode newOrNode = new GCQLOrNode();
				newOrNode.right = previousOrNode;
				newOrNode.left = newCriterionNode;
				previousOrNode = newOrNode;
			}

			searchQueryTerms.add(terms.get(i));
		}
		return previousOrNode;
	}

	protected GCQLNode getCriteriaPart(ASLSession session, boolean simple) {
		logger.debug("Inside getCriteriaPart - the search type is: "
				+ searchType);
		
		searchQueryTerms.clear();
		if (!simple) {
			if (previousCriteria != null && previousCriteria.size() != 0) {
				for (int i = 0; i < previousCriteria.size(); i++) {
					criteria.add(previousCriteria.get(i).clone());
					searchQueryTerms.add(previousCriteria.get(i)
							.getSearchFieldValue());
				}
				// criteria.addAll(previousCriteria);
				previousCriteria.clear();
				this.operator = Operator.AND;
			} else
				logger.debug("no previous criteria set");
			if (criteria != null && criteria.size() != 0) {
				GCQLRelation fieldRelation = new GCQLRelation();
				fieldRelation.setBase(SearchConstants.CommonFieldRelation);

				// Create a GCQLTermNode for the first criterion
				GCQLTermNode criterionNode = new GCQLTermNode();
				// if (criteria.get(0).getSearchFieldId().equals("Any")) {
				// criterionNode.setIndex(SearchConstants.FTS_Field);
				// } else {
				
				
				criterionNode.setIndex(criteria.get(0).getSearchFieldId());
				// }
				criterionNode.setRelation(fieldRelation);
				
				String sanitized = QuerySanitizer.sanitizeQuery(criteria.get(0).getSearchFieldValue());
				sanitized = StringEscapeUtils.unescapeHtml(sanitized);
				
				criterionNode.setTerm("\""+sanitized+"\"");

				logger.debug("criterionNode.setTerm:"+"\""+sanitized+"\"");
				
				searchQueryTerms.add(sanitized);
				if (criteria.size() == 1) {
					if (sanitized == null || sanitized.equals(""))
						return null;
					return criterionNode;
				}

				// we have more than one criteria
				if (operator.equals(Operator.AND)) {
					GCQLAndNode previousAndNode = null;
					for (int i = 1; i < criteria.size(); i++) {
						sanitized = QuerySanitizer.sanitizeQuery(criteria.get(i).getSearchFieldValue());
						sanitized = StringEscapeUtils.unescapeHtml(sanitized);
						if (sanitized == null
								|| sanitized.equals(""))
							continue;

						GCQLTermNode newCriterionNode = new GCQLTermNode();
						// if (criteria.get(i).getSearchFieldId().equals("Any"))
						// {
						// newCriterionNode.setIndex(SearchConstants.FTS_Field);
						// } else {
						newCriterionNode.setIndex(criteria.get(i)
								.getSearchFieldId());
						// }
						newCriterionNode.setRelation(fieldRelation);
						
						sanitized = StringEscapeUtils.unescapeHtml(sanitized);
						
						newCriterionNode.setTerm(sanitized);
						
						searchQueryTerms.add(sanitized);

						if (previousAndNode == null) {
							previousAndNode = new GCQLAndNode();
							previousAndNode.left = criterionNode;
							previousAndNode.right = newCriterionNode;
						} else {
							GCQLAndNode newAndNode = new GCQLAndNode();
							newAndNode.right = previousAndNode;
							newAndNode.left = newCriterionNode;
							previousAndNode = newAndNode;
						}
					}
					return previousAndNode;
				} else {
					GCQLOrNode previousOrNode = null;
					for (int i = 1; i < criteria.size(); i++) {
						sanitized = QuerySanitizer.sanitizeQuery(criteria.get(i).getSearchFieldValue());
						sanitized = StringEscapeUtils.unescapeHtml(sanitized);
						
						if (sanitized == null || sanitized.equals(""))
							continue;
						GCQLTermNode newCriterionNode = new GCQLTermNode();
						if (criteria.get(i).getSearchFieldName().equals("Any")) {
							// newCriterionNode.setIndex(SearchConstants.FTS_Field);
							newCriterionNode.setIndex(ftsId);
						} else {
							newCriterionNode.setIndex(criteria.get(i).getSearchFieldId());
						}
						newCriterionNode.setRelation(fieldRelation);
						newCriterionNode.setTerm(sanitized);

						searchQueryTerms.add(sanitized);

						if (previousOrNode == null) {
							previousOrNode = new GCQLOrNode();
							previousOrNode.left = criterionNode;
							previousOrNode.right = newCriterionNode;
						} else {
							GCQLOrNode newOrNode = new GCQLOrNode();
							newOrNode.right = previousOrNode;
							newOrNode.left = newCriterionNode;
							previousOrNode = newOrNode;
						}
					}
					return previousOrNode;
				}
			} else{
				logger.debug("No current criteria set, will return null");
				return null;
			}
		} else {

			logger.debug("searchterm__="+searchTerm);
			
			if (!searchTerm.contains(" ")
					|| (searchTerm.startsWith("\"") && searchTerm
							.endsWith("\""))) {
				GCQLRelation fieldRelation = new GCQLRelation();
				fieldRelation.setBase(SearchConstants.CommonFieldRelation);

				// Create a GCQLTermNode for the first criterion
				GCQLTermNode criterionNode = new GCQLTermNode();
				// criterionNode.setIndex(SearchConstants.FTS_Field);
				logger.debug("The fts id is: " + ftsId);
				criterionNode.setIndex(ftsId);
				criterionNode.setRelation(fieldRelation);
				
				searchTerm = QuerySanitizer.sanitizeQuery(searchTerm);
				searchTerm = StringEscapeUtils.unescapeHtml(searchTerm);
				
				logger.debug("criterionNode.setTerm():"+searchTerm);
				
				criterionNode.setTerm(searchTerm);

				searchQueryTerms.add(searchTerm);

				return criterionNode;
			} else {
				String[] searchTerms = searchTerm.split("\\s+");
				GCQLRelation fieldRelation = new GCQLRelation();
				fieldRelation.setBase(SearchConstants.CommonFieldRelation);
				GCQLTermNode criterionNode = new GCQLTermNode();
				criterionNode.setIndex(ftsId);
				criterionNode.setRelation(fieldRelation);
				
				logger.debug("criterionNode.setTerm():"+searchTerm);
				criterionNode.setTerm(searchTerms[0]);

				// TODO - Check
				logger.debug("searchQueryTerms.add()"+searchTerms[0]);
				
				searchQueryTerms.add(StringEscapeUtils.unescapeHtml(QuerySanitizer.sanitizeQuery(searchTerms[0])));

				GCQLOrNode previousOrNode = null;
				for (int i = 1; i < searchTerms.length; i++) {
					GCQLTermNode newCriterionNode = new GCQLTermNode();
					newCriterionNode.setIndex(ftsId);
					newCriterionNode.setRelation(fieldRelation);
					newCriterionNode.setTerm(searchTerms[i]);

					if (previousOrNode == null) {
						previousOrNode = new GCQLOrNode();
						previousOrNode.left = criterionNode;
						previousOrNode.right = newCriterionNode;
					} else {
						GCQLOrNode newOrNode = new GCQLOrNode();
						newOrNode.right = previousOrNode;
						newOrNode.left = newCriterionNode;
						previousOrNode = newOrNode;
					}

					searchQueryTerms.add(searchTerms[i]);
				}
				return previousOrNode;
			}
		}
	}

	protected GCQLNode getQuickCriterionPart(ASLSession session, String keyword) {
		GCQLRelation fieldRelation = new GCQLRelation();
		fieldRelation.setBase(SearchConstants.CommonFieldRelation);

		logger.debug("Quick getting criterion");
		// Create a GCQLTermNode for the first criterion
		GCQLTermNode criterionNode = new GCQLTermNode();
		// criterionNode.setIndex(SearchConstants.FTS_Field);
		String quickField = "";
		try {

			quickField = (String) session.getAttribute("quickFieldId");
			if (quickField == null) {
				logger.debug("Quick got presentation part from registry");
				List<gr.uoa.di.madgik.rr.element.search.Field> alFields = gr.uoa.di.madgik.rr.element.search.Field
						.getFieldsWithName(false, "allIndexes");
				if (alFields != null && alFields.size() != 0) {
					criterionNode.setIndex(alFields.get(0).getID());
					session.setAttribute("quickFieldId",
							criterionNode.getIndex());
				}

				logger.debug("Quick got it");
			} else
				criterionNode.setIndex(quickField);

			// TODO: throw better exception
		} catch (ResourceRegistryException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		criterionNode.setRelation(fieldRelation);
		if (!keyword.contains(" ")
				|| (keyword.startsWith("\"") && (keyword.endsWith("\"")))) {
			criterionNode.setTerm(keyword);
			return criterionNode;
		} else if (keyword.contains(" ")) {
			String[] searchTerms = keyword.split(" ");
			criterionNode.setTerm(searchTerms[0]);
			GCQLOrNode previousOrNode = null;
			for (int i = 1; i < searchTerms.length; i++) {
				GCQLTermNode newCriterionNode = new GCQLTermNode();
				newCriterionNode.setIndex(criterionNode.getIndex());
				newCriterionNode.setRelation(criterionNode.getRelation());
				newCriterionNode.setTerm(searchTerms[i]);

				if (previousOrNode == null) {
					previousOrNode = new GCQLOrNode();
					previousOrNode.left = criterionNode;
					previousOrNode.right = newCriterionNode;
				} else {
					GCQLOrNode newOrNode = new GCQLOrNode();
					newOrNode.right = previousOrNode;
					newOrNode.left = newCriterionNode;
					previousOrNode = newOrNode;
				}
			}
			return previousOrNode;

		} else {
			criterionNode.setTerm(keyword);
			return criterionNode;
		}

	}

	protected GCQLNode getBrowseCriteriaPart(ASLSession session) {
		GCQLRelation browseFieldRelation = new GCQLRelation();
		browseFieldRelation.setBase(SearchConstants.BrowseFieldRelation);
		GCQLTermNode newCriterionNode = new GCQLTermNode();
		newCriterionNode.setIndex(browseBy);
		newCriterionNode.setRelation(browseFieldRelation);
		newCriterionNode.setTerm("\"*\"");
		return newCriterionNode;
	}

	protected GCQLNode getLanguageQueryPart(ASLSession session) {
		// Create a GCQLTerm node for the language
		GCQLTermNode languageNode = new GCQLTermNode();
		languageNode.setIndex(SearchConstants.LanguageField);
		GCQLRelation languageRelation = new GCQLRelation();
		languageRelation.setBase(SearchConstants.LanguagesRelation);
		languageNode.setRelation(languageRelation);
		languageNode.setTerm(this.languages.get(this.selectedLanguage));

		return languageNode;
	}



	/**
	 * 
	 * 
	 * @author nikolas laskaris (blame him)
	 * @param session
	 * @return
	 * @throws Exception
	 */
	protected List<String> findPresentableFields(ASLSession session)
			throws Exception {
		logger.debug("Inside method getPresentableFields()");
		
		SearchHelper sh = new SearchHelper(session);

		// Get the user selected collections from the session
		List<String> selectedCollections = getSelectedRealCollections(session);
		logger.debug("SelectedCollections.size(): "
				+ selectedCollections.size());
		
//		System.out.println("SELECTED COLLECTIONS FROM SESSION: "+ selectedCollections);

		ArrayList<String> common = new ArrayList<String>();
		HashMap<String, ArrayList<String>> collectionsPresentableFields = null;

		// Get from session all presentable fields for the collections
		// collectionsPresentableFields = (HashMap<String, ArrayList<String>>)
		// session.getAttribute(SessionConstants.allPresentableFields);

		try {
			collectionsPresentableFields = readUserProfile(session);
		} catch (ReadingUserProfileException e) {
			logger.debug(e.getMessage());
		}

		if ((collectionsPresentableFields != null)
				&& (collectionsPresentableFields.size() > 0)) {
			logger.debug("Going for the comparison to find the common presentables");
			common = ArraysComparison.getCommonFields(
					collectionsPresentableFields, selectedCollections);
		} else { // if still null, then there's no hope that this will work, so
					// get all and recompute common
			logger.debug("Presentables from user profile is empty. Going for askForAllPresentables()");
			common = askForAllCommonPresentables(selectedCollections, sh,
					session);
		}
		logger.debug("Number of common presentables: " + common.size());

		session.setAttribute(SessionConstants.presentableFields, common);

		logger.debug("SessionConstants.sessionDetailedResult was: " + session.getAttribute(SessionConstants.sessionDetailedResult));
		Boolean isDetailedResult = true;
		
		
		for (int n = 0; n < common.size(); n++) {
			String id = common.get(n);
			String name = QueryHelper.GetFieldNameById(id);
		
			logger.debug("Checking common field name: " + name);
		
			if (name.trim().equals(SessionConstants.SNIPPET_FIELD)) {
				session.setAttribute(SessionConstants.SESSION_SNIPPET_ATTR, id);
				isDetailedResult = false;
			} else if (name.trim().equals(SessionConstants.TITLE_FIELD)) {
				session.setAttribute(SessionConstants.SESSION_TITLE_ATTR, id);
				isDetailedResult = false;
			}
		}
		
		
		session.setAttribute(SessionConstants.sessionDetailedResult, isDetailedResult);

		logger.debug("SessionConstants.sessionDetailedResult now is: " + session.getAttribute(SessionConstants.sessionDetailedResult));
		
		return common;
	}

	/**
	 * 
	 * 
	 * @author nikolas laskaris (blame him)
	 * @param selectedCollections
	 * @param sh
	 * @param session
	 * @return
	 * @throws InitialBridgingNotCompleteException
	 * @throws InternalErrorException
	 */
	protected ArrayList<String> askForAllCommonPresentables(
			List<String> selectedCollections, SearchHelper sh,
			ASLSession session) throws InitialBridgingNotCompleteException,
			InternalErrorException {

		HashMap<String, ArrayList<String>> collectionsPresentableFields = new HashMap<String, ArrayList<String>>();
		ArrayList<String> common = new ArrayList<String>();
		ArrayList<Field> fields = new ArrayList<Field>();
		// filling the array with the presentation fields
		for (int i = 0; i < selectedCollections.size(); i++) {
			fields = sh.findCollectionInfo(selectedCollections.get(i))
					.getPresentationFields();
			ArrayList<String> fieldsID = new ArrayList<String>();
			for (int f = 0; f < fields.size(); f++)
				fieldsID.add(fields.get(f).getId());
			collectionsPresentableFields.put(selectedCollections.get(i),
					fieldsID);
		}
		
//		System.out.println("Selected collections---initial: "+selectedCollections);
//		System.out.println("collectionsPresentableFields---(initial): "+ collectionsPresentableFields);
		
		// session.setAttribute(SessionConstants.allPresentableFields,
		// collectionsPresentableFields);
		
		
		common = ArraysComparison.getCommonFields(collectionsPresentableFields,
				selectedCollections); // previous, with some extra checks
		
//		System.out.println("COMMON FIELDS AFTER: "+ common);

		return common;
	}

	protected GCQLNode getSortByPart(ASLSession session) {
		GCQLSortNode sortNode = new GCQLSortNode();
		ModifierSet modSet = new ModifierSet(sortBy);
		if (order == Order.ASC) {
			modSet.addModifier(Modifiers.SortAscending);
		} else
			modSet.addModifier(Modifiers.SortDescending);
		sortNode.addSortIndex(modSet);
		return sortNode;
	}


	
	protected GCQLNode getGenericSearchPresentationPart(HashMap<CollectionInfo, ArrayList<CollectionInfo>> availableCollections, ASLSession session) {
		
		logger.debug("Getting presentation part for Generic Search");
		GCQLProjectNode projectNode = new GCQLProjectNode();
		
		//it should be called only by generic search
		
		if(semanticEnrichment){ //return all presentables
			session.setAttribute(SessionConstants.sessionDetailedResult, true);
			logger.debug("'Return all fields' flag is set for this Generic Search");
			ModifierSet modfSet = new ModifierSet("*");
			projectNode.addProjectIndex(modfSet);
			return projectNode;				
		}
		else{ //return only Snippet and Title
			ArrayList<String> fields = new ArrayList<String>();
			String snippetID = null, titleID = null;
			//find snippet and title id
			Set <CollectionInfo> collec = availableCollections.keySet();
			for(CollectionInfo col : collec){
				ArrayList <CollectionInfo> colinfos = availableCollections.get(col);
				for(CollectionInfo colInfo : colinfos){
					for(Field field : colInfo.getPresentationFields()){
						if(field.getName().trim().equalsIgnoreCase(SessionConstants.SNIPPET_FIELD)){
							session.setAttribute(SessionConstants.SESSION_SNIPPET_ATTR, field.getId());
							snippetID = field.getId();
						}
						if(field.getName().trim().equalsIgnoreCase(SessionConstants.TITLE_FIELD)){
							session.setAttribute(SessionConstants.SESSION_TITLE_ATTR, field.getId());
							titleID = field.getId();
						}
						if((snippetID != null) && (titleID != null))
							break;
					}
						
				}
			}
			if(snippetID!=null) 
				fields.add(snippetID);
			if(titleID!=null) 
				fields.add(titleID);
			
			session.setAttribute(SessionConstants.sessionDetailedResult, false);
			session.setAttribute(SessionConstants.presentableFields, fields);
			
			projectNode.addProjectIndex(new ModifierSet(snippetID));
			projectNode.addProjectIndex(new ModifierSet(titleID));
			
			return projectNode;

			
		}

		
	}
	

	protected GCQLNode getPresentationPart(ASLSession session) {

		GCQLProjectNode projectNode = new GCQLProjectNode();

		if (searchType.equals(SearchType.QuickSearch)) {
			logger.debug("It's a QuickSearch");
			ModifierSet modfSet = new ModifierSet("*");
			projectNode.addProjectIndex(modfSet);
			return projectNode;
		}


		// Retrieve Presentation Fields
		ArrayList<String> fields = new ArrayList<String>();

		if (searchType.equals(SearchType.Browse)) {
			logger.debug("SearchType: Browse  -- get all available presentation fields");
			fields = (ArrayList<String>) session.getAttribute(SessionConstants.presentableFields);

			// printLogAllFields(fields);

		}// case of simple search and generic search
		else if (!semanticEnrichment) { // no semantic enrichment (normal
										// search functionality) in case
										// that there is a "S" field
										// available, load the fields
										// ArrayList just with project only
										// this "S" field
			
			String snippet = (String) session.getAttribute(SessionConstants.SESSION_SNIPPET_ATTR);
			String title = (String) session.getAttribute(SessionConstants.SESSION_TITLE_ATTR);
			
			if (snippet != null) {
				logger.debug("snippet field '" + snippet + "' is available, project this");
				fields.add(snippet);
			}
			if (title != null) {
				logger.debug("title field '" + title + "' is available, project this");
				fields.add(title);
			}
			
//			//this is for the generic search case, where the above two values are definitely not in session, if no simple search is done previously.
//			if((snippet == null) || (title == null)){
//				fields.clear();
//				
//			}
			
			
		}
		// fields will be empty if it's not a "browse" or if semantic enrichment
		// is not enabled or if there are no snippet fields
		if (fields.isEmpty()) {
			logger.debug("use all the available presentable fields");
			if (selectedPresentationFields.size() == 0) {
				logger.debug("profile presentation fields are empty, getting all the available");
				fields = (ArrayList<String>) session
						.getAttribute(SessionConstants.presentableFields);
			} else {
				logger.debug("using profile presentation fields");
				fields = selectedPresentationFields;
				selectedPresentationFields.clear();
			}
		}

		ArrayList<String> fieldStrings = new ArrayList<String>();
		logger.debug("Presentation num: " + fields.size());
		for (String field : fields) {
			ModifierSet modfSet = new ModifierSet(field);
			projectNode.addProjectIndex(modfSet);
			fieldStrings.add(field);
		}

		if (searchType.equals(SearchType.Browse)) {
			if (!fieldStrings.contains(browseBy)) {
				ModifierSet modfSet = new ModifierSet(browseBy);
				projectNode.addProjectIndex(modfSet);
				fields.add(browseBy);
				session.setAttribute(SessionConstants.presentableFields, fields);
			}
		}
		// else if (searchType.equals(SearchType.BrowseFields)) {
		// ModifierSet modfSet = new ModifierSet(browseBy);
		// projectNode.addProjectIndex(modfSet);
		// fields.clear();
		// fields.add(browseBy);
		// session.setAttribute(SessionConstants.presentableFields, fields);
		// }
		else if (searchType.equals(SearchType.AdvancedSearch)) {
			for (int i = 0; i < criteria.size(); i++) {
				if (!fieldStrings.contains(criteria.get(i).getSearchFieldId())) {
					ModifierSet modfSet = new ModifierSet(criteria.get(i)
							.getSearchFieldId());
					projectNode.addProjectIndex(modfSet);
					fields.add(criteria.get(i).getSearchFieldId());
					session.setAttribute(SessionConstants.presentableFields,
							fields);
				}
			}
		}
		/*************************/
		// session.removeAttribute(SessionConstants.presentableFields);
		// session.setAttribute(SessionConstants.presentableFields,
		// presentationIdsNames);
		if (fields.size() != 0)
			return projectNode;
		else
			return null;
	}

	protected GCQLNode getPresentationPartDistinct(ASLSession session) {

		GCQLProjectNode projectNode = new GCQLProjectNode();
		logger.debug("PresentationPart - browse distinct");

		// if (searchType.equals(SearchType.QuickSearch)) {
		// // TODO: change it and put * in projection
		// // get the id of the standard presentation field for quick search
		// // List<gr.uoa.di.madgik.rr.element.search.Field> titleField = null;
		// // try {
		// // titleField =
		// // gr.uoa.di.madgik.rr.element.search.Field.getFieldsWithName(false,
		// // "title");
		// // } catch (ResourceRegistryException e) {
		// // // TODO Auto-generated catch block
		// // logger.error("Exception:", e);
		// // }
		// // if (titleField != null && titleField.size() != 0) {
		// // ModifierSet modSet = new ModifierSet(titleField.get(0).getID());
		// // projectNode.addProjectIndex(modSet);
		// // return projectNode;
		// // } else {
		// // logger.debug("quick: title field is null");
		// // return null;
		// // }
		//
		// ModifierSet modfSet = new ModifierSet("*");
		// projectNode.addProjectIndex(modfSet);
		// session.removeAttribute(SessionConstants.presentableFields);
		// return projectNode;
		// }
		// /************
		// Retrieve Presentation Fields
		ArrayList<String> fields = new ArrayList<String>();
		// in case that there is a "S" field available, load the fields
		// ArrayList just with project only this "S" field
		// if(session.getAttribute(SessionConstants.SESSION_SNIPPET_ATTR) !=
		// null){
		// fields = new ArrayList<String>();
		// fields.add((String)session.getAttribute(SessionConstants.SESSION_SNIPPET_ATTR));
		// }
		// else
		// fields = (ArrayList<String>)
		// session.getAttribute(SessionConstants.presentableFields);
		// /************
		// ArrayList<String> fieldStrings = new ArrayList<String>();
		// logger.debug("Presentation num: " + fields.size());
		// for (String field : fields) {
		// ModifierSet modfSet = new ModifierSet(field);
		// if (distinct) {
		// modfSet.addModifier("distinct");
		// }
		// projectNode.addProjectIndex(modfSet);
		// fieldStrings.add(field);
		// }
		// /************
		// if (searchType.equals(SearchType.Browse) ||
		// searchType.equals(SearchType.BrowseFields)) {
		logger.debug("PresentationPart browse");
		// if (!fieldStrings.contains(browseBy)) {
		// ModifierSet modfSet = new ModifierSet(browseBy);
		// // if (distinct) {
		// modfSet.addModifier("distinct");
		// fields.clear();
		// logger.debug("clearing fields - distinct");
		// // }
		// logger.debug("Not clearing fields - distinct");
		// projectNode.addProjectIndex(modfSet);
		// fields.add(browseBy);
		// session.setAttribute(SessionConstants.presentableFields, fields);
		// } else {
		// if (distinct) {
		ModifierSet modfSet = new ModifierSet(browseBy);
		modfSet.addModifier("distinct");
//		fields.clear();
		fields.add(browseBy);
		projectNode.addProjectIndex(modfSet);
//		session.removeAttribute(SessionConstants.presentableFields);
//		session.setAttribute(SessionConstants.presentableFields, fields);
		// }
		// }
		// } else if (searchType.equals(SearchType.AdvancedSearch)) {
		// for (int i = 0; i < criteria.size(); i++) {
		// if (!fieldStrings.contains(criteria.get(i).getSearchFieldId())) {
		// ModifierSet modfSet = new
		// ModifierSet(criteria.get(i).getSearchFieldId());
		// projectNode.addProjectIndex(modfSet);
		// fields.add(criteria.get(i).getSearchFieldId());
		// session.setAttribute(SessionConstants.presentableFields, fields);
		// }
		// }
		// }
		/*************************/
		// session.removeAttribute(SessionConstants.presentableFields);
		// session.setAttribute(SessionConstants.presentableFields,
		// presentationIdsNames);
		if (fields.size() != 0)
			return projectNode;
		else
			return null;
	}

	private HashMap<String, ArrayList<String>> readUserProfile(
			ASLSession session) throws ReadingUserProfileException {
		// Reading the profile...
		HashMap<String, ArrayList<String>> userPresentationFields = null;
		logger.debug("ASL UserProfile is deactivated.");
		try {
//			logger.debug("Inside readUserProfile -- ");
//			UserProfile userProf = new UserProfile(session);
//			userPresentationFields = userProf.getPresentationFields(session.getUsername());
			if (userPresentationFields != null) {
				session.setAttribute(SessionConstants.allPresentableFields, userPresentationFields);
				logger.debug("size map: " + userPresentationFields.size());
			}
			return userPresentationFields;
		} catch (Exception e) {
			// logger.error("Exception:", e);
			// return null;
			throw new ReadingUserProfileException(e);
		}
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		logger.debug("Setting sort by");
		this.sortBy = sortBy;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @param session
	 *            the D4Science session to be used
	 * @return a consumer to retrieve the search results as pages
	 */
	public ResultSetConsumerI getSearchResults(ASLSession session) {
		return searchRSC;
	}

	public List<String> getSelectedCollectionsNames(ASLSession session)
			throws InitialBridgingNotCompleteException, InternalErrorException {

		List<String> realCollections = getSelectedRealCollections(session);
		List<String> collectionNames = new ArrayList<String>();
		SearchHelper sh = new SearchHelper(session);

		for (String colId : realCollections) {
			CollectionInfo colInf = sh.findCollectionInfo(colId);
			if (colInf != null) {
				collectionNames.add(colInf.getName());
			}
		}

		return collectionNames;
	}



	protected String[] findSearchMasterEPRFeather(ASLSession session) {
		logger.debug("Looking for a Search Master epr (using featherweight stack)");
		ScopeProvider.instance.set(session.getScope());
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		XQuery query = queryFor(GCoreEndpoint.class);

		long starttime = System.currentTimeMillis();
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Search'")
				.addCondition("$resource/Profile/ServiceName/text() eq 'SearchSystemService'")
				.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
		
		List<GCoreEndpoint> searchInstances = client.submit(query);
		logger.debug("Found " + searchInstances.size()
				+ " search service instances.");
		ArrayList<String> searchMasters = new ArrayList<String>();
		for (GCoreEndpoint searchInstance : searchInstances){
			if (searchInstance != null && searchInstance.profile() != null && searchInstance.profile().endpointMap() != null){
				Endpoint endpoint = searchInstance.profile().endpointMap().get(ENDPOINT_KEY);
				if (endpoint != null && endpoint.uri() != null){
					searchMasters.add(new String(endpoint.uri().toString()));
				}
			}

			
		}
			// normally it's only one endpoint per instance
			//for (Endpoint endpoint : searchInstance.profile().endpoints())
			//	searchMasters.add(new EndpointReference(endpoint.uri()
			//			.toString()));
		String[] epr = new String[searchMasters.size()];
		for (int i = 0; i < searchMasters.size(); i++)
			epr[i] = searchMasters.get(i);
		return epr;
	}

	protected Stream<GenericRecord> submitSearch(ISearchClient searchClient, String query,
			String searchMasterURI, ASLSession session)
			throws MalformedURLException, SearchASLException {
//		ScopeProvider.instance.set(session.getScopeName());
		logger.debug("parsing params of URI: " + searchMasterURI);
		logger.info("searchMasterURI: " + searchMasterURI);
		searchClient.setScope(session.getScopeName());
		searchClient.initializeClient(searchMasterURI);
		try {
			logger.debug("USER "+session.getUsername()+" IS CALLING SEARCH WITH QUERY : \t"+query);
			String rsLocator = searchClient.query(query, new HashSet<String>(Arrays.asList(session.getUsername())), false);
			
			
			URI rsURI = null;			
			try{
				rsURI = new URI(rsLocator);
			}
			catch(Exception e){
				throw new SearchASLException("Search returned an empty RS locator, cannot parse it", e);
			}
				
			Stream<GenericRecord> records = convert(rsURI).of(GenericRecord.class).withTimeout(60,TimeUnit.SECONDS);
			
			
			if (records == null) {
				logger.debug("Search service returned a null result stream!");
				throw new StreamException(
						"Returned search results stream is null!", null);
			}
			if (records.isClosed()) {
				logger.debug("Search service returned a closed result stream!");
				throw new StreamOpenException(
						"Search results stream returned, is closed!", null);
			}
			return records;
		} catch (Exception e) {
			throw new SearchASLException("error while searching", e);
		}
			
	}


	public ArrayList<String> getSearchQueryTerms() {
		return this.searchQueryTerms;
	}

}
