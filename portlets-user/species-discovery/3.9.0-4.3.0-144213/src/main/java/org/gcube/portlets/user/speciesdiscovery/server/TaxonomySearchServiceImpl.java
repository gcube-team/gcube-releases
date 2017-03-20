package org.gcube.portlets.user.speciesdiscovery.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import net.sf.csv4j.CSVWriter;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.model.ClassificationModel;
import org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService;
import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.job.GisLayerJobUtil;
import org.gcube.portlets.user.speciesdiscovery.server.job.OccurrenceJobUtil;
import org.gcube.portlets.user.speciesdiscovery.server.job.OccurrenceKeys;
import org.gcube.portlets.user.speciesdiscovery.server.job.TaxonomyJobUtil;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.DaoSession;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.GisLayerJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.ResultRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.service.IteratorChainBuilder;
import org.gcube.portlets.user.speciesdiscovery.server.service.SpeciesService;
import org.gcube.portlets.user.speciesdiscovery.server.service.TaxonomyItemConverter;
import org.gcube.portlets.user.speciesdiscovery.server.session.FetchingSession;
import org.gcube.portlets.user.speciesdiscovery.server.session.FetchingSessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.session.FilterableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.session.SelectableFetchingBuffer;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CSVGenerator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.OccurenceCSVConverter;
import org.gcube.portlets.user.speciesdiscovery.server.stream.OccurenceCSVConverterOpenModeller;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.FieldAggregator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation.TaxonomyClassificationAggregator;
import org.gcube.portlets.user.speciesdiscovery.server.util.DateUtil;
import org.gcube.portlets.user.speciesdiscovery.server.util.GetWorkspaceUtil;
import org.gcube.portlets.user.speciesdiscovery.server.util.QueryUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DatabaseServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.GisLayerJob;
import org.gcube.portlets.user.speciesdiscovery.shared.InvalidJobIdException;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrenceBatch;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesJob;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchByQueryParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResult;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyJob;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ManagerClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ManagerClusterCommonNameDataSourceForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * @author "Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it"
 * @author "Federico De Faveri defaveri@isti.cnr.it" -
 */
public class TaxonomySearchServiceImpl extends RemoteServiceServlet implements TaxonomySearchService {

	protected static final String SAVE_CHILDREN_OF = "Save children of ";
	protected static final String RESUBMIT = "Resubmit";

	private static final long serialVersionUID = -287193068445844326L;

	protected static final long MAX_BUFFERING_ELEMENTS = 1000;
	protected static final long BUFFER_LIMIT = 10;

	protected Logger logger = Logger.getLogger(TaxonomySearchService.class);

	public static final String TAXONOMYUNKNOWN = "Unknown";
	public static final String BASETAXONOMY = "Kingdom";
	public static final String UNK = "Unk";

//	static {
//		Logger root = Logger.getLogger("org.gcube.portlets.user.speciesdiscovery");
//		root.setLevel(Level.ALL);
//	}

	/**
 * Gets the ASL session.
 *
 * @return the ASL session
 */
protected ASLSession getASLSession()
	{
		return SessionUtil.getAslSession(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the species service.
	 *
	 * @return the species service
	 * @throws SearchServiceException the search service exception
	 */
	protected SpeciesService getSpeciesService() throws SearchServiceException
	{
		try {
			ASLSession session = getASLSession();
			return SessionUtil.getService(session);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurred when contacting the species service", e);
			//			System.out.println("An error occurred retrieving the service" +e);
			throw new SearchServiceException("contacting the species service.");
		}
	}

	/**
	 * Gets the search session.
	 *
	 * @return the search session
	 * @throws SearchServiceException the search service exception
	 */
	protected FetchingSession<? extends FetchingElement> getSearchSession() throws SearchServiceException
	{
		ASLSession session = getASLSession();
		FetchingSession<? extends FetchingElement> searchSession = SessionUtil.getCurrentSearchSession(session);

		if (searchSession == null) {
			logger.error("No search session found for user "+session.getUsername());
			throw new SearchServiceException("No search session found for user "+session.getUsername());
		}

		return searchSession;
	}



	/**
	 * Gets the occurrence session.
	 *
	 * @return the occurrence session
	 * @throws SearchServiceException the search service exception
	 */
	protected FetchingSession<Occurrence> getOccurrenceSession() throws SearchServiceException
	{
		ASLSession session = getASLSession();
		FetchingSession<Occurrence> occurrenceSession = SessionUtil.getCurrentOccurrenceSession(session);

		if (occurrenceSession == null) {
			logger.error("No occurrence session found for user "+session.getUsername());
			throw new SearchServiceException("No occurrence session found for user "+session.getUsername());
		}

		return occurrenceSession;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchByScientificName(String searchTerm, SearchFilters searchFilters) throws SearchServiceException {
		logger.info("searchByScientificName searchTerm: "+searchTerm+" searchFilters: "+searchFilters);

		stopSearch();
		search(searchTerm, SearchType.BY_SCIENTIFIC_NAME, searchFilters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void searchByCommonName(String searchTerm, SearchFilters searchFilters) throws SearchServiceException {
		logger.info("searchByCommonName searchTerm: "+searchTerm+" searchFilters: "+searchFilters);

		stopSearch();
		search(searchTerm, SearchType.BY_COMMON_NAME, searchFilters);
	}

	/**
	 * Search.
	 *
	 * @param searchTerm the search term
	 * @param searchType the search type
	 * @param searchFilters the search filters
	 * @return the search result type
	 * @throws SearchServiceException the search service exception
	 */
	protected SearchResultType search(String searchTerm, SearchType searchType, SearchFilters searchFilters) throws SearchServiceException
	{

		ASLSession aslSession = getASLSession();
		try {
			deleteAllRowIntoDaoTable(); //RESET TABLE
			SpeciesService taxonomyService = getSpeciesService();
			CloseableIterator<ResultElement> input = taxonomyService.searchByFilters(searchTerm, searchType, searchFilters);

			SessionUtil.setCurrentQuery(aslSession, taxonomyService.getLastQuery());
//			System.out.println("returned input stream by service...");
			logger.info("returned input stream by service...");

			SearchResultType resultType = QueryUtil.getResultType(searchFilters);
			CloseableIterator<FetchingElement> output = IteratorChainBuilder.buildChain(input, resultType, aslSession);
			FetchingSessionUtil.createFetchingSession(output, resultType, aslSession);
			return resultType;
		} catch (Exception e) {
			logger.error("Error starting search "+searchType+" for term \""+searchTerm+"\" with filters "+searchFilters, e);
			SessionUtil.setCurrentQuery(aslSession, "invalid query");
			throw new SearchServiceException(e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#searchByQuery(java.lang.String)
	 */
	@Override
	public SearchByQueryParameter searchByQuery(String query) throws SearchServiceException {
		logger.info("searchByQuery - query: "+query);

		stopSearch();
		ASLSession aslSession = getASLSession();

		try {
			deleteAllRowIntoDaoTable(); //RESET TABLE
			SpeciesService taxonomyService = getSpeciesService();
			CloseableIterator<ResultElement> input = taxonomyService.searchByQuery(query);

			SessionUtil.setCurrentQuery(aslSession, query);
			logger.info("service return iterator searched...");

			SearchByQueryParameter queryParameters = QueryUtil.getQueryResultType(query);
			CloseableIterator<FetchingElement> output = IteratorChainBuilder.buildChain(input, queryParameters.getSearchResultType(), aslSession);
			FetchingSessionUtil.createFetchingSession(output, queryParameters.getSearchResultType(), aslSession);
			logger.info("creating fetching session completed!");
			return queryParameters;

		} catch (Exception e) {
			logger.error("Error starting search by query", e);
			SessionUtil.setCurrentQuery(aslSession, "invalid query");
			throw new SearchServiceException(e.getMessage());
		}
	}


	/**
	 * Delete all row into dao table.
	 *
	 * @throws Exception the exception
	 */
	private void deleteAllRowIntoDaoTable() throws Exception{
		logger.info("deleting all row into dao's");
		ResultRowPersistence daoResultRow = null;
		TaxonRowPersistence daoTaxon = null;
		TaxonomyRowPersistence daoTaxonomyRow = null;
		ASLSession session = getASLSession();

		try {
			daoResultRow = DaoSession.getResultRowDAO(session);
			daoTaxon = DaoSession.getTaxonDAO(session);
			daoTaxonomyRow = DaoSession.getTaxonomyDAO(session);

		} catch (Exception e) {
			logger.error("Error in delete all row -  getDao's " +e.getMessage(), e);
			throw new Exception("Error in delete all row-  getDao's " + e.getMessage(), e);
		}

		try {
			if(daoResultRow!=null)
				daoResultRow.removeAll();

			if(daoTaxon!=null)
				daoTaxon.removeAll();

			if(daoTaxonomyRow!=null)
				daoTaxonomyRow.removeAll();

			logger.info("delete all row into Dao's - completed");
		} catch (Exception e) {
			logger.error("Error in delete all row");
			throw new Exception("Error in delete all row" + e.getCause(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<ResultRow> getSearchResultRows(int start, int limit, ResultFilter activeFiltersObject, boolean onlySelected) throws SearchServiceException {
		logger.info("getSearchResultRows start: "+start+" limit: "+limit+" onlySelected: "+onlySelected);
		Long startTime =  System.currentTimeMillis();

		FetchingSession<ResultRow> searchSession = (FetchingSession<ResultRow>) getSearchSession();
		ArrayList<ResultRow> chunk = new ArrayList<ResultRow>();

		try {
			List<ResultRow> data = new ArrayList<ResultRow>();
			if (onlySelected) {
				SelectableFetchingBuffer<ResultRow> buffer = (SelectableFetchingBuffer<ResultRow>) searchSession.getBuffer();
				data = buffer.getSelected();

				int end = Math.min(start+limit, data.size());
				start = Math.min(start, end);

				logger.info("chunk selected data bounds [start: "+start+" end: " + end+"]");
				data = data.subList(start, end);

			} else if (activeFiltersObject == null || !activeFiltersObject.isActiveFilters()) {
				if(limit>0){
					data = searchSession.getBuffer().getList(start,limit);
				}
			} else {
				FilterableFetchingBuffer<ResultRow> buffer = (FilterableFetchingBuffer<ResultRow>) searchSession.getBuffer();
				data = buffer.getFilteredList(activeFiltersObject);

				int end = Math.min(start+limit, data.size());
				start = Math.min(start, end);

				logger.info("chunk filtered data bounds [start: "+start+" end: " + end+"]");
				data = data.subList(start, end);
			}

			logger.info("Fetching data from search session buffer, size: "+data.size());

			for (ResultRow resultRow : data) {

				//return common names?
				if(activeFiltersObject == null || !activeFiltersObject.isLoadCommonName() || !resultRow.existsCommonName()){
					resultRow.setCommonNames(null);
				}

				//return properties?
				if(activeFiltersObject == null || !resultRow.existsProperties() || !activeFiltersObject.isLoadAllProperties()){
					resultRow.setProperties(null);
				}
				chunk.add(resultRow);
				logger.debug("getSearchResultRows returning on client result item with id: " +resultRow.getId() + " service id: "+resultRow.getServiceId());
			}

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			logger.info("returning "+chunk.size()+" elements in " + time);
		} catch (Exception e) {
			logger.error("Error in getSearchResultRows ", e);
			throw new SearchServiceException(e.getMessage());
		}

		return new SearchResult<ResultRow>(chunk);
	}

	/**
	 * Prints the properties.
	 *
	 * @param properties the properties
	 */
	private void printProperties(List<ItemParameter> properties){

		for (ItemParameter itemParameter : properties) {
			System.out.println("Property "+itemParameter);
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getSearchTaxonomyRow(int, int, org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<TaxonomyRow> getSearchTaxonomyRow(int start, int limit, ResultFilter activeFiltersObject, boolean onlySelected) throws SearchServiceException {

		logger.info("getSearchResultRows start: "+start+" limit: "+limit+" onlySelected: "+onlySelected);
		Long startTime =  System.currentTimeMillis();
		FetchingSession<TaxonomyRow> searchSession = (FetchingSession<TaxonomyRow>) getSearchSession();
		ArrayList<TaxonomyRow> chunk = new ArrayList<TaxonomyRow>();

		try {

			logger.info("current buffer size "+searchSession.getBuffer().size());
			List<TaxonomyRow> data = new ArrayList<TaxonomyRow>();

			if (onlySelected) {
				logger.info("getting only selected data");
				SelectableFetchingBuffer<TaxonomyRow> buffer = (SelectableFetchingBuffer<TaxonomyRow>) searchSession.getBuffer();
				data = buffer.getSelected();

				int end = Math.min(start+limit, data.size());
				start = Math.min(start, end);

				logger.info("chunk selected data bounds [start: "+start+" end: " + end+"]");

				data = data.subList(start, end);

			} else if (activeFiltersObject == null || !activeFiltersObject.isActiveFilters()) {
				logger.info("getting all available data");

				if(limit>0){
					Map<String, String> filterAndMap = new HashMap<String, String>();
					filterAndMap.put(TaxonomyRow.IS_PARENT, "false");
					data = searchSession.getBuffer().getList(filterAndMap, start,limit);
				}
			} else {
				logger.info("getting filtered data");
				FilterableFetchingBuffer<TaxonomyRow> buffer = (FilterableFetchingBuffer<TaxonomyRow>) searchSession.getBuffer();
				data = buffer.getFilteredList(activeFiltersObject);

				int end = Math.min(start+limit, data.size());
				start = Math.min(start, end);

				logger.info("chunk filtered data bounds [start: "+start+" end: " + end+"]");

				data = data.subList(start, end);
			}

			logger.info("Fetching data from search session buffer, size: "+data.size());

			for (TaxonomyRow taxonomyRow : data) {
				//ADD common names

				//return common names?
				if(activeFiltersObject == null || !activeFiltersObject.isLoadCommonName() || !taxonomyRow.existsCommonName()){
					taxonomyRow.setCommonNames(null);
				}

				//return properties?
				if(activeFiltersObject == null || !taxonomyRow.existsProperties() || !activeFiltersObject.isLoadAllProperties()){
					taxonomyRow.setProperties(null);
				}
				logger.info("getSearchTaxonomyRow return on client taxonomy item with id: " +taxonomyRow.getId() + " service id: "+taxonomyRow.getServiceId());
				chunk.add(taxonomyRow);

			}

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			logger.info("returning "+chunk.size()+" elements in " + time);

		} catch (Exception e) {
			logger.error("Error in getSearchTaxonomyRow " + e.getMessage(), e);
			throw new SearchServiceException(e.getMessage());
		}

		return new SearchResult<TaxonomyRow>(chunk);
	}

	/**
	 * Load taxonomy parent by parent id.
	 *
	 * @param parentID the parent id
	 * @return the taxonomy row
	 * @throws Exception the exception
	 */
	public TaxonomyRow loadTaxonomyParentByParentId(String parentID) throws Exception {

		TaxonomyRow taxonomyRow = null;
		logger.info("loadTaxonomyParentByParentId: "+ parentID);

		try {

			TaxonomyRowPersistence dao = DaoSession.getTaxonomyDAO(getASLSession());
			CriteriaBuilder queryBuilder = dao.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Predicate pr1 =  queryBuilder.equal(dao.rootFrom(cq).get(TaxonomyRow.PARENT_ID), parentID);
			cq.where(pr1);

			Iterator<TaxonomyRow> iterator = dao.executeCriteriaQuery(cq).iterator();

			if(iterator!=null && iterator.hasNext()){
				taxonomyRow = iterator.next();
			}

		} catch (Exception e) {
			logger.error("Error in loadTaxonomyParentsByRowId", e);
			throw new Exception(e);
		}

		return taxonomyRow;
	}

//	public void loadParentsListOfTaxonomy(TaxonomyRow taxonomy) throws Exception {
//
//		taxonomy.setParent(setParentListOfTaxonomy(taxonomy.getParent()));
//	}
//
//	private TaxonomyRow setParentListOfTaxonomy(TaxonomyRow taxonomy) throws Exception{
//
//		if (taxonomy == null) return null;
//		//		DaoSession.getTaxonomyDAO(getASLSession()).refresh(taxonomy.getParent());
//		taxonomy.setParent(setParentListOfTaxonomy(taxonomy.getParent()));
//		return taxonomy;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<String, Integer> getFilterCounterById(GridField field) throws Exception {
		logger.info("Filter Counter for: "+ field);
		try {
			FetchingSession<? extends FetchingElement> searchSession = getSearchSession();
			FieldAggregator<?,?> aggregator = (FieldAggregator<?,?>) searchSession.getAggregator(FieldAggregator.getFieldAggregatorName(field));
			if (aggregator!=null) return aggregator.getAggregation();
			else return new HashMap<String, Integer>();
		} catch(Exception e)
		{
			logger.error("Error in getFilterCounterById "+ field.getId()+" "+field.getName(), e);
			throw new SearchServiceException(e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getFilterCounterForClassification(java.lang.String)
	 */
	@Override
	public HashMap<String, ClassificationModel> getFilterCounterForClassification(String rankLabel) throws Exception {
		logger.info("Counter for classification: "+ rankLabel);
		try {
			MainTaxonomicRankEnum rank = MainTaxonomicRankEnum.valueOfLabel(rankLabel);

			if (rank!=null) {
				FetchingSession<? extends FetchingElement> searchSession = getSearchSession();
				TaxonomyClassificationAggregator<?> classificationAggregator = (TaxonomyClassificationAggregator<?>) searchSession.getAggregator(TaxonomyClassificationAggregator.NAME);
				return classificationAggregator.getAggregation().get(rank);
			} else return new HashMap<String, ClassificationModel>();
		} catch(Exception e)
		{
			logger.error("Error in getFilterCounterForClassification "+ rankLabel, e);
			throw new SearchServiceException(e.getMessage());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public SearchStatus getSearchStatus(boolean onlySelected, boolean isActiveFilterOnResult) throws SearchServiceException {
		logger.info("getSearchStatus onlySelected: "+onlySelected);

		FetchingSession<? extends FetchingElement> searchSession = getSearchSession();

		SearchStatus status = new SearchStatus();
		int bufferSize = 0;

		try {
			bufferSize = isActiveFilterOnResult?((FilterableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer()).getFilteredListSize():searchSession.getBufferSize();
		} catch (Exception e) {
			logger.info("getSearchStatus bufferSize error : "+e.getMessage(), e);
			throw new SearchServiceException(e.getMessage());
		}

		logger.info("getSearchStatus bufferSize " + bufferSize);
		//if buffer size is >= the MAXIMUM ELEMENTS. Maximun is reached and the search is stopped
		if(bufferSize>=MAX_BUFFERING_ELEMENTS){
			logger.info("getSearchStatus MAX_BUFFERING_ELEMENTS is reached -  stop search");
			stopSearchWithoutRemove();

			//CALCULATE NEW BUFFER SIZE AFTER FETCHING IS CLOSED
			try {
//				int sleepingTime = 500;
//				logger.info("sleeping "+sleepingTime+" ms for translate last objets arrived into buffer");
//				Thread.sleep(sleepingTime); //SLEEPING 0,5 sec, for translating objects that are inserting in buffer and calculate new size of buffer
//				logger.info("sleep termined - search status alive");
				bufferSize = isActiveFilterOnResult?((FilterableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer()).getFilteredListSize():searchSession.getBufferSize();

			} catch (SQLException e) {
				logger.info("getSearchStatus bufferSize error : "+e.getMessage(), e);
				throw new SearchServiceException("An error occured on server in searching status, please retry");
			} catch (Exception e) {
				logger.info("getSearchStatus bufferSize error : "+e.getMessage(), e);
				throw new SearchServiceException("An error occured on server in searching status, please retry");
			}

			status.setResultEOF(true);
			status.setSize(bufferSize);
			status.setIsMaxSize(true);
			return status;
		}

		if (!onlySelected && !isActiveFilterOnResult) {
			status.setResultEOF(searchSession.isComplete());
			status.setSize(bufferSize);
		} else if(isActiveFilterOnResult){
			status.setResultEOF(true);

			try {
				status.setSize(((FilterableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer()).getFilteredListSize());
			} catch (Exception e) {
				logger.error("isActiveFilterOnResult - An error occured in getSearchStatus " +e.getMessage(), e);
				throw new SearchServiceException(e.getMessage());
			}
		}
		else{

			status.setResultEOF(true);

			try {
				status.setSize(((SelectableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer()).getSelected().size());
			} catch (Exception e) {
				logger.error("An error occured in getSearchStatus " +e.getMessage(), e);
				throw new SearchServiceException(e.getMessage());
			}
		}

		logger.info("getSearchStatus return status size: "+status.getSize()  +" EOF: " + status.isResultEOF());
		return status;
	}


	/**
	 * {@inheritDoc}
	 */
	public void stopSearchWithoutRemove() throws SearchServiceException {
		logger.info("stopSearch without Remove");

		ASLSession session = getASLSession();
		//we safely get the session if exists
		FetchingSession<? extends FetchingElement> searchSession = SessionUtil.getCurrentSearchSession(session);

		if (searchSession != null) {
			try {
				searchSession.close();
			} catch (IOException e) {
				throw new SearchServiceException(e.getMessage());
			}
		} else logger.warn("Search session not found");
	}



	/**
	 * Stop search.
	 *
	 * @throws SearchServiceException the search service exception
	 */
	private void stopSearch() throws SearchServiceException {
		logger.info("stopSearch");

		ASLSession session = getASLSession();
		FetchingSession<? extends FetchingElement> searchSession = SessionUtil.getCurrentSearchSession(session);

		if (searchSession != null) {
			try {
				searchSession.close();
			} catch (IOException e) {
				throw new SearchServiceException(e.getMessage());
			}
			SessionUtil.setCurrentSearchSession(session, null);
		} else logger.warn("Search session not found");
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#userStopSearch()
	 */
	public void userStopSearch() throws SearchServiceException {
		ASLSession session = getASLSession();
		FetchingSession<? extends FetchingElement> searchSession = SessionUtil.getCurrentSearchSession(session);

		if (searchSession != null) {
			try {
				searchSession.close();
			} catch (IOException e) {
				throw new SearchServiceException(e.getMessage());
			}
		} else logger.warn("Search session not found");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateRowSelection(int rowId, boolean selection) throws SearchServiceException {
		logger.info("updateRowSelection rowId: "+rowId+" selection: "+selection);

		try {
			ASLSession session = getASLSession();
			FetchingSession<? extends FetchingElement> searchSession = SessionUtil.getCurrentSearchSession(session);
			SelectableFetchingBuffer<? extends FetchingElement> buffer = (SelectableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer();
			buffer.updateSelection(rowId, selection);

		} catch(Exception e){
			logger.error("Error in updateRowSelection rowId: "+rowId+" selection: "+selection, e);
			throw new SearchServiceException(e.getMessage());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer updateRowSelections(boolean selection, ResultFilter activeFiltersObject) throws SearchServiceException {
		logger.info("updateRowSelections selection: "+selection);
		List<? extends FetchingElement> data;
		FetchingSession<? extends FetchingElement> searchSession = getSearchSession();
		int size = 0;
		try {

			if (activeFiltersObject == null || !activeFiltersObject.isActiveFilters()) {
				SelectableFetchingBuffer<? extends FetchingElement> buffer = (SelectableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer();
				buffer.updateAllSelection(selection);
				size = buffer.size();
			} else {
				@SuppressWarnings("unchecked")
				FilterableFetchingBuffer<ResultRow> buffer = (FilterableFetchingBuffer<ResultRow>) searchSession.getBuffer();
				data = buffer.getFilteredList(activeFiltersObject);

				if(data!=null){

					List<String> ids = new ArrayList<String>();
					for (FetchingElement fetchingElement : data){
						ids.add(fetchingElement.getId()+"");
					}
					SelectableFetchingBuffer<? extends FetchingElement> bufferCompleted = (SelectableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer();
					bufferCompleted.updateAllSelectionByIds(selection, ids);

					size = data.size();
				}
			}

		} catch (Exception e) {
			logger.error("An error occurred in updateRowSelections", e);
			throw new SearchServiceException(e.getMessage());
		}

		return Integer.valueOf(size);
	}


	/**
	 * {@inheritDoc}
	 * @throws SearchServiceException
	 */
	@Override
	public int countOfSelectedRow() throws SearchServiceException{
		logger.info("countOfSelectedRow()");

		FetchingSession<? extends FetchingElement> searchSession = getSearchSession();

		try {

			SelectableFetchingBuffer<? extends FetchingElement> buffer = (SelectableFetchingBuffer<? extends FetchingElement>) searchSession.getBuffer();
			return buffer.sizeSelected();

		} catch (Exception e) {
			logger.error("An error occurred in updateRowSelections", e);
			throw new SearchServiceException(e.getMessage());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int retrieveOccurencesFromSelection() throws SearchServiceException {
		logger.info("retrieveOccurencesFromSelection()");

		int count = 0;
		FetchingSession<ResultRow> searchSession = (FetchingSession<ResultRow>) getSearchSession();

		try {
			Collection<ResultRow> selectedRows = ((SelectableFetchingBuffer<ResultRow>) searchSession.getBuffer()).getSelected();
			logger.info("found "+selectedRows.size()+" selected rows");

			List<String> keys = new ArrayList<String>(selectedRows.size());

			for (ResultRow row:selectedRows) {
				//ADD KEY ONLY IF IS NOT EQUAL NULL AND SIZE IS > 0
				if(row.getOccurencesKey()!=null && row.getOccurencesKey().length()>0){
					keys.add(row.getOccurencesKey());
					count += row.getOccurencesCount();
				}
			}

			logger.info("found "+count+" occurrence points");

			//TODO remove
			if (logger.isInfoEnabled()) logger.info("selected keys: "+keys);

			SpeciesService taxonomyService = getSpeciesService();

			CloseableIterator<OccurrencePoint> source = taxonomyService.getOccurrencesByKeys(keys);
			CloseableIterator<Occurrence> input = IteratorChainBuilder.buildOccurrenceConverter(source);

			//DELETE ALL ROW INTO DAO OCCURENCES
			OccurrenceRowPersistence occurrencesDao = DaoSession.getOccurrenceDAO(getASLSession());
			occurrencesDao.removeAll();
			FetchingSessionUtil.createOccurrenceFetchingSession(input, getASLSession());

		} catch (Exception e) {
			logger.error("An error occurred getting the number of occurrence points", e);
			throw new SearchServiceException(e.getMessage());
		}

		return count;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getOccurrencesBatch(int, int)
	 */
	@Override
	public OccurrenceBatch getOccurrencesBatch(int start, int limit) throws SearchServiceException {
		logger.info("getOccurrencesBatch: start: "+start+" limit: "+limit);

		FetchingSession<Occurrence> occurrenceSession = getOccurrenceSession();
		List<Occurrence> buffer;
		OccurrenceBatch result = null;

		try {
			buffer = occurrenceSession.getBuffer().getList();
			logger.info("Fetching data from occurrence session buffer, size: "+buffer.size());
			int end = Math.min(start+limit, buffer.size());
			logger.info("chunk bounds[start: "+start+" end: " + end+"]");
			ArrayList<Occurrence> data = new ArrayList<Occurrence>(buffer.subList(start, end));
			logger.info("returning "+data.size()+" elements");

			//DEBUG
//			for (Occurrence occurrence : data) {
//				logger.info("citation :" + occurrence.getCitation()+
//						" catalog number: "+occurrence.getCatalogueNumber()+
//						" country: " +occurrence.getCountry()+
//						" family: "+occurrence.getFamily()+
//						" id: "+ occurrence.getId() +
//						" institute code: " +occurrence.getInstitutionCode() +
//						" kingdom: " + occurrence.getKingdom()+
//						" scientific name: "+ occurrence.getScientificName()+
//						" basis of record: "+occurrence.getBasisOfRecord());
//
//			}
			result = new OccurrenceBatch(data);
			result.setResultEOF(occurrenceSession.isComplete());

		} catch (Exception e) {
			logger.error("An error occurred getting the occurrence points", e);
			throw new SearchServiceException(e.getMessage());
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getCountOfOccurrencesBatch()
	 */
	@Override
	public OccurrencesStatus getCountOfOccurrencesBatch() throws SearchServiceException {
		logger.info("get CountOf Occurrences Batch");

		FetchingSession<Occurrence> occurrenceSession = getOccurrenceSession();
		List<Occurrence> buffer;

		try {
			buffer = occurrenceSession.getBuffer().getList();
			logger.info("Fetching data from occurrence session buffer, size: "+buffer.size());
			return new OccurrencesStatus(occurrenceSession.isComplete(), buffer.size());

		} catch (Exception e) {
			logger.error("An error occurred getting the occurrence points", e);
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopRetrievingOccurrences() throws SearchServiceException {
		logger.info("stop Retrieving Occurrences ");

		ASLSession session = getASLSession();
		FetchingSession<Occurrence> occurrenceSearchSession = SessionUtil.getCurrentOccurrenceSession(session);

		if (occurrenceSearchSession != null) {
			try {
				occurrenceSearchSession.close();
				logger.info("Occurrence session removed");
			} catch (IOException e) {
				throw new SearchServiceException(e.getMessage());
			}
			SessionUtil.setCurrentOccurrenceSession(session, null);
		} else logger.warn("Occurrence session not found");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobGisLayerModel createGisLayerJobFromSelectedOccurrenceKeys(String layerTitle, String layerDescr, long totalPoints) throws Exception {
		try {

			List<String> occurrenceKeys = getSelectedOccurrenceKeys();
			SpeciesService taxonomyService = getSpeciesService();
			String author = getASLSession().getUsername();
			String credits = "";
			GisLayerJobPersistence gisLayerJob = DaoSession.getGisLayersJobDAO(getASLSession());
			logger.trace("GisLayerJobPersistence found");
			return GisLayerJobUtil.createGisLayerJobByOccurrenceKeys(occurrenceKeys, taxonomyService, layerTitle, layerDescr, author, credits, totalPoints, gisLayerJob);
		} catch (Exception e) {
			logger.error("An error occurred creating the map", e);
			throw new Exception(e.getMessage());
	}


		/*Iterator<Occurrence> iteratorOccurrences = getIteratorSelectedOccurrenceIds();
		IteratorPointInfo streamKey = new IteratorPointInfo(iteratorOccurrences);

		try {
			SpeciesService taxonomyService = getSpeciesService();
			String groupName = taxonomyService.generateMapFromOccurrencePoints(streamKey);
			logger.info("generated groupName: "+groupName);
			return groupName;
		} catch (Exception e) {
			logger.error("An error occurred creating the map", e);
			throw new SearchServiceException(e.getMessage());
		}*/
	}

	//USED FOR DEBUG
	/**
	 * Prints the id.
	 *
	 * @param listId the list id
	 */
	protected void printId(List<String> listId){

		for (String id : listId) {
			System.out.println("Found id : " +id);
		}
	}

	/**
	 * Gets the selected occurrence ids.
	 *
	 * @return the selected occurrence ids
	 * @throws SearchServiceException the search service exception
	 */
	protected List<String> getSelectedOccurrenceIds() throws SearchServiceException{

		FetchingSession<Occurrence> occurrenceSession = getOccurrenceSession();
		List<Occurrence> buffer;
		List<String> listId = new ArrayList<String>();

		try {

			buffer = occurrenceSession.getBuffer().getList();

			for (Occurrence occurrence : buffer) {
				listId.add(occurrence.getServiceId());
			}

			return  listId;
		} catch (Exception e) {

			logger.error("An error occurred on getSelectedOccurrenceIds", e);
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Gets the iterator selected occurrence ids.
	 *
	 * @return the iterator selected occurrence ids
	 * @throws SearchServiceException the search service exception
	 */
	protected Iterator<Occurrence> getIteratorSelectedOccurrenceIds() throws SearchServiceException{

		FetchingSession<Occurrence> occurrenceSession = getOccurrenceSession();
		try {
			return occurrenceSession.getBuffer().getList().iterator();
		} catch (Exception e) {
			logger.error("An error occurred on getIteratorSelectedOccurrenceIds", e);
			throw new SearchServiceException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveSelectedOccurrencePoints(java.lang.String, java.lang.String, org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat, org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum)
	 */
	@Override
	@Deprecated
	public void saveSelectedOccurrencePoints(String destinationFolderId, String fileName, SaveFileFormat fileFormat, OccurrencesSaveEnum typeCSV) throws SearchServiceException {
		logger.info("saveSelectedOccurrencePoints destinationFolderId: "+destinationFolderId+" fileName: "+fileName+" fileFormat: "+fileFormat+" typeCSV: "+typeCSV);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveSelectedTaxonomyPoints(java.lang.String, java.lang.String, org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat)
	 */
	@Override
	public void saveSelectedTaxonomyPoints(String destinationFolderId, String fileName, SaveFileFormat fileFormat) throws SearchServiceException {
		//TODO OLD CALL
	}

	/**
	 * Gets the selected occurrence keys.
	 *
	 * @return the selected occurrence keys
	 * @throws SearchServiceException the search service exception
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getSelectedOccurrenceKeys() throws SearchServiceException
	{
		FetchingSession<ResultRow> searchSession = (FetchingSession<ResultRow>) getSearchSession();
		Collection<ResultRow> selectedRows;
		List<String> keys = null;

		try {

			selectedRows = ((SelectableFetchingBuffer<ResultRow>) searchSession.getBuffer()).getSelected();
			logger.info("found "+selectedRows.size()+" selected rows");
			keys = new ArrayList<String>(selectedRows.size());
			int count = 0;

			for (ResultRow row:selectedRows) {
				keys.add(row.getOccurencesKey());
				count += row.getOccurencesCount();
			}
			logger.info("found "+count+" occurrence points and "+keys.size()+" keys");

			//TODO remove
			if (logger.isInfoEnabled()) logger.info("selected keys: "+keys);

		} catch (Exception e) {
			logger.error("An error occured in getSelectedOccurrenceKeys" + e.getMessage());
			throw new SearchServiceException(e.getMessage());
		}

		return keys;
	}


	/**
	 * Gets the selected result row id.
	 *
	 * @return the selected result row id
	 * @throws SearchServiceException the search service exception
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getSelectedResultRowId() throws SearchServiceException
	{
		FetchingSession<ResultRow> searchSession = (FetchingSession<ResultRow>) getSearchSession();
		Collection<ResultRow> selectedRows;
		List<String> listId = null;

		try {
			selectedRows = ((SelectableFetchingBuffer<ResultRow>) searchSession.getBuffer()).getSelected();

			logger.info("found "+selectedRows.size()+" selected rows");

			listId = new ArrayList<String>(selectedRows.size());

			for (ResultRow row:selectedRows)
				listId.add(row.getServiceId());

			logger.info("found "+listId.size()+" ids");

			//TODO remove
			if (logger.isTraceEnabled()) logger.info("selected ids: "+listId);

		} catch (Exception e) {
			logger.error("An error occured in getSelectedOccurrenceKeys" + e.getMessage());
			throw new SearchServiceException(e.getMessage());
		}

		return listId;
	}

	/**
	 * Gets the selected taxonomy id and data source.
	 *
	 * @return the selected taxonomy id and data source
	 * @throws SearchServiceException the search service exception
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, String> getSelectedTaxonomyIdAndDataSource() throws SearchServiceException
	{
		FetchingSession<TaxonomyRow> searchSession = (FetchingSession<TaxonomyRow>) getSearchSession();
		HashMap<String, String> hashIdTaxonDataSource = null;
		Collection<TaxonomyRow> selectedRows;

		try {
			selectedRows = ((SelectableFetchingBuffer<TaxonomyRow>) searchSession.getBuffer()).getSelected();

			logger.info("found "+selectedRows.size()+" selected rows");
			hashIdTaxonDataSource = new HashMap<String, String>(selectedRows.size());

			for (TaxonomyRow row:selectedRows){
				hashIdTaxonDataSource.put(row.getServiceId(), row.getDataProviderName());
				logger.info("add this id: "+row.getServiceId()+" to list");
			}

			logger.info("found "+hashIdTaxonDataSource.size()+" id");

			//TODO remove
			if (logger.isInfoEnabled()) logger.info("selected ids: "+hashIdTaxonDataSource);

		} catch (Exception e) {
			logger.error("An error occured in getSelectedOccurrenceKeys" + e.getMessage());
			throw new SearchServiceException(e.getMessage());
		}

		return hashIdTaxonDataSource;
	}

	/**
	 * Generate csv file.
	 *
	 * @param ids the ids
	 * @param csvType the csv type
	 * @return the file
	 * @throws Exception the exception
	 */
	protected File generateCSVFile(List<String> ids, OccurrencesSaveEnum csvType) throws Exception
	{
		File csvFile = File.createTempFile("test", ".csv");
		logger.info("outputfile "+csvFile.getAbsolutePath());

		FileWriter fileWriter = new FileWriter(csvFile);
		CSVWriter writer = new CSVWriter(fileWriter);

		SpeciesService taxonomyService = getSpeciesService();

		CloseableIterator<OccurrencePoint> source = taxonomyService.getOccurrencesByIds(ids);
		CloseableIterator<Occurrence> result = IteratorChainBuilder.buildOccurrenceConverter(source);

		CSVGenerator<Occurrence> csvGenerator = null;

		switch (csvType) {

			case OPENMODELLER: {

				OccurenceCSVConverterOpenModeller converterOpenModeller = new OccurenceCSVConverterOpenModeller();
				csvGenerator = new CSVGenerator<Occurrence>(result, converterOpenModeller, OccurenceCSVConverterOpenModeller.HEADER);

			}break;

			case STANDARD:{

				OccurenceCSVConverter converter = new OccurenceCSVConverter();
				csvGenerator = new CSVGenerator<Occurrence>(result, converter, OccurenceCSVConverter.HEADER);

			}break;
		}

		while(csvGenerator.hasNext()) writer.writeLine(csvGenerator.next());

		fileWriter.close();
		return csvFile;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadDataSourceList()
	 */
	@Override
	public List<DataSourceModel> loadDataSourceList() throws SearchServiceException {
		logger.info("loadDataSourceList... ");
		List<DataSourceModel> listDS = null;

		try {
			DaoSession.initSessionDaoObjects(getASLSession()); //FIXME temporary?
			logger.info("DAOs OK... ");
			System.out.println("DAOs OK");
			SpeciesService taxonomyService = getSpeciesService();
			System.out.println("Species Services OK");
			logger.info("Species Services OK... ");
			listDS = taxonomyService.getPlugins();
			System.out.println("Plugins OK");
			logger.info("Plugins OK");
			logger.info("Return list plugin - size: "  +listDS.size());

		} catch (DatabaseServiceException e) {
			throw new SearchServiceException("Sorry, an error has occurred on the server while "+e.getMessage());

		} catch (Exception e) {
			throw new SearchServiceException("Sorry, an error has occurred on the server while "+e.getMessage());
		}

		return listDS;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadListCommonNameByRowId(java.lang.String)
	 */
	@Override
	@Deprecated
	public ArrayList<CommonName> loadListCommonNameByRowId(String resultRowId) throws Exception {

		ArrayList<CommonName> listCommonName = new ArrayList<CommonName>();

		return listCommonName;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getParentsList(org.gcube.portlets.user.speciesdiscovery.shared.Taxon)
	 */
	@Override
	@Deprecated
	public List<Taxon> getParentsList(Taxon taxon) throws Exception {

		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadListChildrenByParentId(java.lang.String)
	 */
	@Override
	public ArrayList<LightTaxonomyRow> loadListChildrenByParentId(String parentId) throws Exception {
		logger.info("Load List Children By ParentId: " + parentId);

		ArrayList<LightTaxonomyRow> listLightTaxonomyRow = new ArrayList<LightTaxonomyRow>();

		if(parentId==null || parentId.isEmpty()){
			logger.warn("parentId is null or empty ");
			return listLightTaxonomyRow;
		}

		try {

			SpeciesService taxonomyService = getSpeciesService();
			CloseableIterator<TaxonomyItem> streamIterator = taxonomyService.getTaxonChildrenByParentId(parentId);
			ASLSession session = getASLSession();
			TaxonomyItemConverter converter = new TaxonomyItemConverter(getASLSession());

			Map<String, TaxonomyRow> mapChildren = SessionUtil.getHashMapChildrenTaxonomyCache(session);

			if(mapChildren==null){
				logger.info("Cache taxa children doesn't exists into session, creating..");
				mapChildren = new HashMap<String, TaxonomyRow>();
			}

			while (streamIterator.hasNext()) {
				TaxonomyItem tax = streamIterator.next();

				TaxonomyRow taxonomy = converter.convert(tax);

				if(mapChildren.get(taxonomy.getServiceId())==null){
					logger.info("Taxonomy with service id: "+taxonomy.getServiceId()+" doesn't exists into Map Children, adding..");
					mapChildren.put(taxonomy.getServiceId(),taxonomy);
				}

				LightTaxonomyRow lightTaxRow  = ClusterStructuresForTaxonomyRow.convetTaxonomyRowToLigthTaxonomyRow(taxonomy);
				listLightTaxonomyRow.add(lightTaxRow);
			}

			SessionUtil.setHashMapChildrenTaxonomyCache(session, mapChildren);
			streamIterator.close();

		} catch (Exception e) {
			logger.error("Error on loadListChildByParentId ", e);
			throw new Exception("Error on loadListChildByParentId", e);
		}

		logger.info("Return list children By ParentId "+parentId+"- with size: "+ listLightTaxonomyRow.size());

		return listLightTaxonomyRow;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getListTaxonomyJobs()
	 */
	@Override
	public List<JobTaxonomyModel> getListTaxonomyJobs() throws Exception {
		logger.info("getListTaxonomyJobs... ");
		List<JobTaxonomyModel> listJobs = new ArrayList<JobTaxonomyModel>();

		try{

			TaxonomyJobPersistence taxonomyJobDao = DaoSession.getTaxonomyJobDAO(getASLSession());
			Iterator<TaxonomyJob> iterator = taxonomyJobDao.getList().iterator();

		    SpeciesService taxonomyService = getSpeciesService();

		    while(iterator!=null && iterator.hasNext()){
		    	TaxonomyJob job = iterator.next();
		     	logger.info("get taxonomy job "+job.getId()+ " from service");

		    	try{
		    		CompleteJobStatus statusResponse = taxonomyService.getTaxonomyJobById(job.getId());

			    	if(statusResponse!=null){
			    		logger.info("statusResponse is not null..." + job.getId());
				    	JobTaxonomyModel jobSpeciesModel = TaxonomyJobUtil.convertJob(job, statusResponse, taxonomyJobDao);
				    	logger.info("added list jobTaxonomyId: "+job.getTaxonomyId() + " status "+job.getState());

				    	if(jobSpeciesModel!=null)
				    		listJobs.add(jobSpeciesModel);
			    	}
			    	else{
			    		logger.info("TaxonomyJob statusResponse is null..." + job.getId());
			    		TaxonomyJobUtil.deleteTaxonomyJobById(job.getId(),taxonomyJobDao);
			    	}

		    	}catch (Exception e) {
					e.printStackTrace();
					logger.error("Error on getListSpeciesJobs ", e);
					throw new Exception("Error on getListSpeciesJobs", e);
				}
		    }

		}catch (Exception e) {
			logger.error("Error on getListSpeciesJobs ", e);
			throw new Exception("Error on getListSpeciesJobs", e);
		}

	    return listJobs;

	}


	/**
	 * Creates the taxonomy job by children.
	 *
	 * @param taxonomyServiceId the taxonomy service id
	 * @param taxonomyName the taxonomy name
	 * @param taxonomyRank the taxonomy rank
	 * @param dataSourceName the data source name
	 * @return the job taxonomy model
	 * @throws Exception the exception
	 */
	@Override
	public JobTaxonomyModel createTaxonomyJobByChildren(String taxonomyServiceId, String taxonomyName, String taxonomyRank, String dataSourceName) throws Exception {
		//FIXED 20/05/2013
		logger.info("Create job for taxonomy id: " + taxonomyServiceId);
//		System.out.println("Create job for taxonomy id: " + taxonomy.getServiceId());

		JobTaxonomyModel jobSpeciesModel = null;

		TaxonomyJobPersistence taxonomyJobDao = DaoSession.getTaxonomyJobDAO(getASLSession());

		SpeciesService taxonomyService = getSpeciesService();

		String speciesJobId = taxonomyService.createTaxonomyJobForDWCAByChildren(taxonomyServiceId);

		long submitTime = Calendar.getInstance().getTimeInMillis();

		String name = NormalizeString.lowerCaseUpFirstChar(taxonomyName) + " group";

		//STORE INTO DAO
		TaxonomyJob speciesJob = new TaxonomyJob(speciesJobId, DownloadState.PENDING.toString(), name, taxonomyName, dataSourceName, taxonomyRank, 0, submitTime, 0, taxonomyServiceId);
		taxonomyJobDao.insert(speciesJob);

		jobSpeciesModel = new JobTaxonomyModel(speciesJob.getId(), speciesJob.getDescriptiveName(), DownloadState.PENDING, null, taxonomyName, dataSourceName, taxonomyRank);

		Date submit = DateUtil.millisecondsToDate(speciesJob.getSubmitTime());
//		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
		jobSpeciesModel.setSubmitTime(submit);
		jobSpeciesModel.setEndTime(null);

		return jobSpeciesModel;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#createTaxonomyJobByIds(java.lang.String, java.util.List)
	 */
	@Override
	public JobTaxonomyModel createTaxonomyJobByIds(String search, List<DataSourceModel> dataSources) throws Exception {

		logger.info("Create job ForDWCAByIds for: " + search);

		Map<String, String> hashIdDs = getSelectedTaxonomyIdAndDataSource();
		JobTaxonomyModel jobSpeciesModel = null;
		TaxonomyJobPersistence taxonomyJobDao = DaoSession.getTaxonomyJobDAO(getASLSession());
		SpeciesService taxonomyService = getSpeciesService();
		List<String> listId = new ArrayList<String>(hashIdDs.keySet());
		String speciesJobId = taxonomyService.createTaxonomyJobForDWCAByIds(listId);
		long submitTime = Calendar.getInstance().getTimeInMillis();

		String name = NormalizeString.lowerCaseUpFirstChar(search) + " - ";
		name += listId.size() + " ";
		name += listId.size()>1?"taxa":"taxon";

		String dataSourceName = "";

		for (String taxonId : listId) {
			if(!dataSourceName.contains(hashIdDs.get(taxonId))) //remove duplicate
				dataSourceName+=hashIdDs.get(taxonId) + ", ";
		}

		if(dataSourceName.endsWith(", "))
			dataSourceName = dataSourceName.substring(0, dataSourceName.length()-2);

		//STORE INTO DAO
		TaxonomyJob speciesJob = new TaxonomyJob(speciesJobId, DownloadState.PENDING.toString(), name, name, dataSourceName, "", 0, submitTime, 0, speciesJobId);
		taxonomyJobDao.insert(speciesJob);

		jobSpeciesModel = new JobTaxonomyModel(speciesJob.getId(), speciesJob.getDescriptiveName(), DownloadState.PENDING, null, name, dataSourceName, "");

		Date submit = DateUtil.millisecondsToDate(speciesJob.getSubmitTime());
//		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
		jobSpeciesModel.setSubmitTime(submit);
		jobSpeciesModel.setEndTime(null);

		return jobSpeciesModel;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#resubmitTaxonomyJob(java.lang.String)
	 */
	@Override
	public JobTaxonomyModel resubmitTaxonomyJob(String jobIdentifier) throws Exception {

		logger.info("Resubmit taxonomy job for id: " + jobIdentifier);

		JobTaxonomyModel jobSpeciesModel = null;

		//Get Dao with list taxonomy jobs
		TaxonomyJobPersistence taxonomyJobDao = DaoSession.getTaxonomyJobDAO(getASLSession());

		CriteriaBuilder queryBuilder = taxonomyJobDao.getCriteriaBuilder();
		CriteriaQuery<Object> cq = queryBuilder.createQuery();
		Predicate pr1 =  queryBuilder.equal(taxonomyJobDao.rootFrom(cq).get(TaxonomyJob.ID_FIELD), jobIdentifier);
		cq.where(pr1);

		Iterator<TaxonomyJob> iterator = taxonomyJobDao.executeCriteriaQuery(cq).iterator();

		TaxonomyJob taxonomy;

		if(iterator.hasNext())
			 taxonomy = iterator.next();
		else
			return jobSpeciesModel;

		SpeciesService taxonomyService = getSpeciesService();

		//recover taxomyId
		String speciesJobId = taxonomyService.createTaxonomyJobForDWCAByChildren(taxonomy.getTaxonomyId());

		long submitTime = Calendar.getInstance().getTimeInMillis();

		String name = RESUBMIT + ": " +NormalizeString.lowerCaseUpFirstChar(taxonomy.getDescriptiveName());

		//STORE INTO DAO
		TaxonomyJob speciesJob = new TaxonomyJob(speciesJobId, DownloadState.PENDING.toString(), name, taxonomy.getDescriptiveName(), taxonomy.getDataSourceName(), taxonomy.getRank(), 0, submitTime, 0, taxonomy.getTaxonomyId());
		taxonomyJobDao.insert(speciesJob);

		jobSpeciesModel = new JobTaxonomyModel(speciesJob.getId(), speciesJob.getDescriptiveName(), DownloadState.PENDING, null, taxonomy.getDescriptiveName(), taxonomy.getDataSourceName(), taxonomy.getRank());

		Date submit = DateUtil.millisecondsToDate(speciesJob.getSubmitTime());
//		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
		jobSpeciesModel.setSubmitTime(submit);
		jobSpeciesModel.setEndTime(null);

		return jobSpeciesModel;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#cancelTaxonomyJob(java.lang.String)
	 */
	@Override
	public boolean cancelTaxonomyJob(String jobIdentifier) throws Exception {

		try{

			SpeciesService taxonomyService = getSpeciesService();

			//REMOVE JOB ON THE SERVICE
			taxonomyService.cancelTaxonomyJobById(jobIdentifier);

			TaxonomyJobPersistence speciesJobDao = DaoSession.getTaxonomyJobDAO(getASLSession());

			int count = TaxonomyJobUtil.deleteTaxonomyJobById(jobIdentifier, speciesJobDao);

			if(count==1)
				return true;

		}catch (Exception e) {
			logger.error("Error on cancel taxonomy job ", e);
			throw new Exception("Error on cancel taxonomy job", e);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveTaxonomyJob(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveTaxonomyJob(String jobIdentifier, String destinationFolderId, String fileName, String scientificName, String dataSourceName) throws Exception {

		logger.info("saveSpeciesJob jobId: "+jobIdentifier+" destinationFolderId: "+destinationFolderId+" fileName: "+fileName);

		try {

			SpeciesService taxonomyService = getSpeciesService();
			String mimeType = "application/zip";
			InputStream inputStream = taxonomyService.getTaxonomyJobFileById(jobIdentifier);

			if(inputStream!=null){
				Workspace workspace = GetWorkspaceUtil.getWorskspace(getASLSession());
				logger.info("input stream is not null");

				WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(destinationFolderId);
				fileName = WorkspaceUtil.getUniqueName(fileName, folder);
				folder.createExternalFileItem(fileName,"Taxonomy job generated files", mimeType, inputStream);
				logger.info("Save file with taxonomy was completed");
			}
			else{

				logger.info("input stream is null");
				return false;
			}

			return true;

		} catch (Exception e) {

			logger.error("An error occurred saving the generated file into the workspace",e);
			throw new SearchServiceException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveTaxonomyJobError(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveTaxonomyJobError(String jobIdentifier, String destinationFolderId, String fileName, String scientificName, String dataSourceName) throws Exception {
		logger.info("saveSpeciesJob error jobId: "+jobIdentifier+" destinationFolderId: "+destinationFolderId+" fileName: "+fileName);

		try {

			SpeciesService taxonomyService = getSpeciesService();
			String mimeType = "text/plain";

			InputStream inputStream = taxonomyService.getTaxonomyJobErrorFileById(jobIdentifier);

			if(inputStream!=null){
				Workspace workspace = GetWorkspaceUtil.getWorskspace(getASLSession());
				logger.info("input stream is not null");
				WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(destinationFolderId);
				fileName = WorkspaceUtil.getUniqueName(fileName, folder);
				folder.createExternalFileItem(fileName,"Report errors on taxonomy job", mimeType, inputStream);
				logger.info("Save report file with errors occurred was completed");
			}
			else{

				logger.info("input stream is null");
				return false;
			}

			return true;

		} catch (Exception e) {

			logger.error("An error occurred saving the generated file into the workspace",e);
			throw new SearchServiceException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#retrieveTaxonomyByIds(java.util.List)
	 */
	@Override
	public List<LightTaxonomyRow> retrieveTaxonomyByIds(List<String> ids) throws Exception{
		logger.info("retrieveTaxonomyByIds ids size: " + ids.size());
		List<LightTaxonomyRow> listLightTaxonomyRow = new ArrayList<LightTaxonomyRow>();

		try {

			SpeciesService taxonomyService = getSpeciesService();
			//StreamExtend<String> convert = new StreamExtend<String>(ids.iterator());

			CloseableIterator<TaxonomyItem> streamIterator = taxonomyService.retrieveTaxonomyById(ids);

			ASLSession session = getASLSession();
			TaxonomyItemConverter taxonomyItemConverter = new TaxonomyItemConverter(session);

			Map<String, TaxonomyRow> mapTaxonomyIds = SessionUtil.getHashMapTaxonomyByIdsCache(session);

			if(mapTaxonomyIds==null){
				logger.info("Cache taxa ByIds doesn't exists into session, creating..");
				mapTaxonomyIds = new HashMap<String, TaxonomyRow>();
			}

			while (streamIterator.hasNext()) {
				TaxonomyItem tax = streamIterator.next();

				TaxonomyRow taxonomy = taxonomyItemConverter.convert(tax);

				if(mapTaxonomyIds.get(taxonomy.getServiceId())==null){
					logger.info("Taxonomy with service id: "+taxonomy.getServiceId()+" doesn't exists into Map Taxonomy Ids, adding..");
					mapTaxonomyIds.put(taxonomy.getServiceId(),taxonomy);
				}

				LightTaxonomyRow lightTaxRow  = ClusterStructuresForTaxonomyRow.convetTaxonomyRowToLigthTaxonomyRow(taxonomy);
				listLightTaxonomyRow.add(lightTaxRow);
			}

			SessionUtil.setHashMapTaxonomyByIdsCache(session, mapTaxonomyIds);

			streamIterator.close();

		} catch (Exception e) {

			e.printStackTrace();
			logger.error("An error retrieve taxonomy by Id",e);
//			throw new Exception(e.getMessage());
		}

		return listLightTaxonomyRow;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#retrieveSynonymsByRefId(java.lang.String)
	 */
	@Override
	public List<LightTaxonomyRow> retrieveSynonymsByRefId(String refId) throws Exception{
		logger.info("retrieveSynonymsById id: " + refId);
		List<LightTaxonomyRow> listLightTaxonomyRow = new ArrayList<LightTaxonomyRow>();

		try {

			SpeciesService taxonomyService = getSpeciesService();

			CloseableIterator<TaxonomyItem> streamIterator = taxonomyService.retrieveSynonymsById(refId);

			ASLSession session = getASLSession();
			TaxonomyItemConverter taxonomyItemConverter = new TaxonomyItemConverter(getASLSession());

			Map<String, TaxonomyRow> mapSysnosyms = SessionUtil.getHashMapSynonymsTaxonomyCache(getASLSession());

			if(mapSysnosyms==null){
				logger.info("Cache synonyms doesn't exists into session, creating..");
				mapSysnosyms = new HashMap<String, TaxonomyRow>();
			}

//			int i = 1;
			while (streamIterator.hasNext()) {
				TaxonomyItem tax = streamIterator.next();

				TaxonomyRow taxonomy = taxonomyItemConverter.convert(tax);

				if(mapSysnosyms.get(taxonomy.getServiceId())==null){
					logger.info("Taxonomy with service id: "+taxonomy.getServiceId()+" doesn't exists into Map Synonyms, adding..");
					mapSysnosyms.put(taxonomy.getServiceId(),taxonomy);
				}

				LightTaxonomyRow lightTaxRow  = ClusterStructuresForTaxonomyRow.convetTaxonomyRowToLigthTaxonomyRow(taxonomy);
				listLightTaxonomyRow.add(lightTaxRow);
			}

			SessionUtil.setHashMapSynonymsTaxonomyCache(session, mapSysnosyms);

			streamIterator.close();

		} catch (Exception e) {

			e.printStackTrace();
			logger.error("An error retrieve synonyms by Id",e);
		}

		return listLightTaxonomyRow;

	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JobOccurrencesModel> createOccurrencesJob(List<JobOccurrencesModel> listJobOccurrenceModel, SaveFileFormat saveFileFormat, OccurrencesSaveEnum csvType, boolean isByDataSource, int expectedOccurrence) throws Exception {
		logger.info("createOccurencesJobFromSelection...");

		List<JobOccurrencesModel> listResultJobModel = new ArrayList<JobOccurrencesModel>();
		@SuppressWarnings("unchecked")
		FetchingSession<ResultRow> searchSession = (FetchingSession<ResultRow>) getSearchSession();
		SpeciesService taxonomyService = getSpeciesService();
		ASLSession aslSession = getASLSession();
		String dataSourceAsXml = "";

		try {

			List<String> keys = null;
			//Get Dao with list occurrences jobs
			OccurrenceJobPersistence occurrencesJobDao = DaoSession.getOccurrencesJobDAO(aslSession);

			if(!isByDataSource){  //NOT IS BY DATASOURCE - CREATE ONE JOB

				keys = OccurrenceJobUtil.getListOfSelectedKey(searchSession);

				if(listJobOccurrenceModel!=null && listJobOccurrenceModel.get(0)!=null){ //IN THIS CASE - THERE IS ONE JOBMODEL

					List<DataSource> dataSourceList = listJobOccurrenceModel.get(0).getDataSources();

					//ADDED DATA SOURCE LIST
					listResultJobModel.add(OccurrenceJobUtil.createOccurrenceJobOnServiceByKeys(listJobOccurrenceModel.get(0), taxonomyService, occurrencesJobDao, keys, dataSourceList, saveFileFormat, csvType, expectedOccurrence));
				}
			}else{ //IS BY DATASOURCE - CREATE MORE JOB, ONE FOR EACH DATASOURCE

				for (JobOccurrencesModel jobModel : listJobOccurrenceModel) { //IN THIS CASE - FOR EACH JOBMODEL THERE IS ONE DATASOURCE

					dataSourceAsXml = "";

					if(jobModel.getDataSources()!=null && jobModel.getDataSources().get(0)!=null){

						//recover keys
						DataSource dataSource = jobModel.getDataSources().get(0);

						OccurrenceKeys occKey = OccurrenceJobUtil.getListOfSelectedKeyByDataSource(dataSource.getName(), aslSession);

						List<DataSource> dataSourceList = jobModel.getDataSources();

						//ADDED DATA SOURCE LIST
						listResultJobModel.add(OccurrenceJobUtil.createOccurrenceJobOnServiceByKeys(jobModel, taxonomyService, occurrencesJobDao, occKey.getListKey(), dataSourceList, saveFileFormat, csvType, occKey.getTotalOccurrence()));
					}
				}

			}

		return listResultJobModel;

		} catch (Exception e) {
			logger.error("An error occurred in createOccurencesJobFromSelection", e);
			throw new Exception(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#resubmitOccurrencesJob(java.lang.String)
	 */
	@Override
	public List<JobOccurrencesModel> resubmitOccurrencesJob(String jobIdentifier) throws Exception {
		logger.info("createOccurencesJobFromSelection...");

		List<JobOccurrencesModel> listResultJobModel = new ArrayList<JobOccurrencesModel>();
		SpeciesService taxonomyService = getSpeciesService();
		ASLSession aslSession = getASLSession();
		List<String> keys = null;

		try {

			//Get Dao with list occurrences jobs
			OccurrenceJobPersistence occurrencesJobDao = DaoSession.getOccurrencesJobDAO(aslSession);

			CriteriaBuilder queryBuilder = occurrencesJobDao.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Predicate pr1 =  queryBuilder.equal(occurrencesJobDao.rootFrom(cq).get(OccurrencesJob.ID_FIELD), jobIdentifier);
			cq.where(pr1);

			Iterator<OccurrencesJob> iterator = occurrencesJobDao.executeCriteriaQuery(cq).iterator();

			OccurrencesJob job;

			if(iterator.hasNext())
				 job = iterator.next();
			else
				return listResultJobModel;

			//recover keys
			keys = OccurrenceJobUtil.revertListKeyFromStoredXMLString(job.getResultRowKeysAsXml());

			//recover file format
			SaveFileFormat fileFormat = OccurrenceJobUtil.converFileFormat(job.getFileFormat());

			//recover csv type
			OccurrencesSaveEnum csvType = OccurrenceJobUtil.convertCsvType(job.getCsvType());

			String name = RESUBMIT + ": "+job.getName();

			JobOccurrencesModel jobModel = new JobOccurrencesModel("",name, job.getScientificName(), job.getDataSources(), fileFormat, csvType, job.isByDataSource());
			jobModel.setTotalOccurrences(job.getExpectedOccurrence());

			listResultJobModel.add(OccurrenceJobUtil.createOccurrenceJobOnServiceByKeys(jobModel, taxonomyService, occurrencesJobDao, keys, job.getDataSources(), fileFormat, csvType, jobModel.getTotalOccurrences()));

		} catch (Exception e) {
			logger.error("An error occurred in createOccurencesJobFromSelection", e);
			throw new Exception(e.getMessage());
		}

		return listResultJobModel;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getListOccurrencesJob()
	 */
	@Override
	public List<JobOccurrencesModel> getListOccurrencesJob() throws Exception{
		logger.info("getListOccurencesJob... ");
		List<JobOccurrencesModel> listJobs = new ArrayList<JobOccurrencesModel>();

		try {

			OccurrenceJobPersistence occurrencesJobDao = DaoSession.getOccurrencesJobDAO(getASLSession());
			Iterator<OccurrencesJob> iterator = occurrencesJobDao.getList().iterator();
			SpeciesService taxonomyService = getSpeciesService();

			while (iterator!=null && iterator.hasNext()) {
				OccurrencesJob job = iterator.next();
				CompleteJobStatus statusResponse = taxonomyService.getOccurrenceJobById(job.getId());
			 	logger.info("get occurrence job "+job.getId()+ " from service");

			 	try{

					if(statusResponse!=null){
			    		logger.info("statusResponse of occurrence job is not null..." + job.getId());
				    	JobOccurrencesModel jobOccurrenceModel = OccurrenceJobUtil.convertJob(job, statusResponse, occurrencesJobDao);

				    	if(jobOccurrenceModel!=null){
					    	logger.info("added list jobOccurrenceId: "+jobOccurrenceModel.getJobIdentifier() + " status "+jobOccurrenceModel.getDownloadState());
				    		listJobs.add(jobOccurrenceModel);
				    	}
					}
			    	else{
			    		logger.info("statusResponse of occurrence job is null..." + job.getId());
			     		logger.info("deleting job ..." + job.getId());
			    		OccurrenceJobUtil.deleteOccurrenceJobById(job.getId(),occurrencesJobDao);
			    	}

		    	}catch (Exception e) {
					e.printStackTrace();
					logger.error("Error on getListOccurencesJob ", e);
					throw new Exception("Error on getListOccurencesJob", e);
				}
			}

		} catch (Exception e) {
			logger.error("Error on get iterator "+e, e);
		}

		return listJobs;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getListGisLayerJob()
	 */
	@Override
	public List<JobGisLayerModel> getListGisLayerJob() throws Exception{
		logger.info("getListGisLayerJob... ");
		List<JobGisLayerModel> listJobs = new ArrayList<JobGisLayerModel>();

		try {

			GisLayerJobPersistence gisLayerJobDao = DaoSession.getGisLayersJobDAO(getASLSession());

			Iterator<GisLayerJob> iterator = gisLayerJobDao.getList().iterator();
			SpeciesService taxonomyService = getSpeciesService();

			while (iterator!=null && iterator.hasNext()) {
				GisLayerJob job = iterator.next();

				if(job.getId()==null || job.getId().isEmpty()){
					logger.warn("Gis job has an id null or empty, skipping");
				}else{

				 	try{

						CompleteJobStatus statusResponse = taxonomyService.getGisLayerByJobId(job.getId());
					 	logger.info("get occurrence job "+job.getId()+ " from service");

						if(statusResponse!=null){
				    		logger.info("statusResponse of gis layer job is not null..." + job.getId());
					    	JobGisLayerModel convertJob = GisLayerJobUtil.convertJob(job, statusResponse, gisLayerJobDao, taxonomyService, getASLSession());

					    	if(convertJob!=null){
						    	logger.info("added list jobOccurrenceId: "+convertJob.getJobIdentifier() + " status "+convertJob.getDownloadState());
					    		listJobs.add(convertJob);
					    	}
						}
				    	else{
				    		logger.info("statusResponse of occurrence job is null..." + job.getId());
				     		logger.info("deleting job ..." + job.getId());
				     		GisLayerJobUtil.deleteGisLayerJobById(job.getId(),gisLayerJobDao);
				    	}

			    	}catch (Exception e) {

			    		if (e instanceof InvalidJobIdException){
			    			logger.info("The spd service unkwnowns GIS job id: "+job.getId() +" deleting it from db...");
			    			GisLayerJobUtil.deleteGisLayerJobById(job.getId(),gisLayerJobDao);
			    		}else{

							logger.error("Error on getListGisLayerJob ", e);
							throw new Exception("Error on getListGisLayerJob", e);
			    		}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error on get iterator "+e, e);
		}

		return listJobs;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveOccurrenceJob(org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveOccurrenceJob(JobOccurrencesModel jobModel, String destinationFolderId, String fileName, String scientificName, String dataSourceName) throws Exception {
		logger.info("saveOccurrenceJob jobId: "+jobModel.getJobIdentifier()+" destinationFolderId: "+destinationFolderId+" fileName: "+fileName + " file format: "+jobModel.getFileFormat());

		try {

			SpeciesService taxonomyService = getSpeciesService();
			String mimeType = null;

			switch (jobModel.getFileFormat()) {
				case CSV: {
					mimeType = "text/csv";
				} break;
				case DARWIN_CORE:{
					mimeType = "application/xhtml+xml";
				} break;
			}

			InputStream inputStream = taxonomyService.getOccurrenceJobFileById(jobModel.getJobIdentifier());

			if(inputStream!=null){
				Workspace workspace = GetWorkspaceUtil.getWorskspace(getASLSession());
				logger.info("input stream is not null");
//				System.out.println("input stream is not null");

				WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(destinationFolderId);
				fileName = WorkspaceUtil.getUniqueName(fileName, folder);
				folder.createExternalFileItem(fileName,"Occurrence job generated files", mimeType, inputStream);
				logger.info("Save file with occurrences was completed");
			}
			else{
				logger.info("input stream is null");
				return false;
			}

			return true;

		} catch (Exception e) {
			logger.error("An error occurred saving the generated file into the workspace",e);
			throw new SearchServiceException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveOccurrenceJobError(org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveOccurrenceJobError(JobOccurrencesModel jobModel, String destinationFolderId, String fileName, String scientificName, String dataSourceName) throws Exception {

		logger.info("saveOccurrenceJobError jobId: "+jobModel.getJobIdentifier()+" destinationFolderId: "+destinationFolderId+" fileName: "+fileName + " file format: "+jobModel.getFileFormat());

		try {

			SpeciesService taxonomyService = getSpeciesService();
			String mimeType = "text/plain";

			InputStream inputStream = taxonomyService.getOccurrenceJobErrorFileById(jobModel.getJobIdentifier());

			if(inputStream!=null){
				Workspace workspace = GetWorkspaceUtil.getWorskspace(getASLSession());
				logger.info("input stream is not null");
//				System.out.println("input stream is not null");

				WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(destinationFolderId);
				fileName = WorkspaceUtil.getUniqueName(fileName, folder);
				folder.createExternalFileItem(fileName,"Report errors occurred on occurrence job", mimeType, inputStream);
				logger.info("Save report file with errors occurred was completed");
			}
			else{

				logger.info("input stream is null");
				return false;
			}

			return true;

		} catch (Exception e) {

			logger.error("An error occurred saving the generated file into the workspace",e);
			throw new SearchServiceException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#cancelOccurrenceJob(java.lang.String)
	 */
	@Override
	public boolean cancelOccurrenceJob(String jobIdentifier) throws Exception {
		logger.info("cancelOccurrenceJob jobIdentifier: "+jobIdentifier);
		try{

			SpeciesService taxonomyService = getSpeciesService();
			//REMOVE JOB ON THE SERVICE
			taxonomyService.cancelTaxonomyJobById(jobIdentifier);
			OccurrenceJobPersistence occurrenceJobDAO = DaoSession.getOccurrencesJobDAO(getASLSession());
			int count = OccurrenceJobUtil.deleteOccurrenceJobById(jobIdentifier, occurrenceJobDAO);

			if(count==1)
				return true;

		}catch (Exception e) {
			logger.error("Error on cancel occurrence job ", e);
			throw new Exception("Error on cancel occurrence job", e);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadStructuresForResultRowClustering()
	 */
	@Override
	public ClusterStructuresForResultRow loadStructuresForResultRowClustering() throws Exception {

		int countSelectedRow = countOfSelectedRow();
		boolean isReduced = false;
		int totalRow = countSelectedRow;

		ASLSession session = getASLSession();
		HashMap<String, ClusterCommonNameDataSourceForResultRow> hashCluster = SessionUtil.getCurrentClusterCommonNameForResultRow(session);

		//Reset cluster for common name
		if(hashCluster!=null)
			SessionUtil.setCurrentClusterCommonNameForResultRow(session, null);


		//LIMIT NUMBER OF ITEMS TO ConstantsSpeciesDiscovery.LIMIT_ITEM_DETAILS
		if(countSelectedRow>ConstantsSpeciesDiscovery.RESULT_ROW_LIMIT_ITEM_DETAILS)
			countSelectedRow = ConstantsSpeciesDiscovery.RESULT_ROW_LIMIT_ITEM_DETAILS;

		ResultFilter filter = new ResultFilter(false, true, true);

		SearchResult<ResultRow> searchResults = getSearchResultRows(0, countSelectedRow, filter, true);

		ClusterStructuresForResultRow cluster = new ClusterStructuresForResultRow(searchResults,isReduced, totalRow);

		//TODO USE THREAD?
		ManagerClusterCommonNameDataSourceForResultRow manager = new ManagerClusterCommonNameDataSourceForResultRow(cluster.getHashClusterScientificNameResultRowServiceID(), cluster.getHashResult());

		SessionUtil.setCurrentClusterCommonNameForResultRow(getASLSession(), manager.getHashClusterCommonNameDataSource());

		//THIS OBJECT IS NOT USED ON CLIENT
		cluster.setHashResult(null);

		return cluster;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadClusterCommonNameForResultRowByScientificName(java.lang.String)
	 */
	@Override
	public ClusterCommonNameDataSourceForResultRow loadClusterCommonNameForResultRowByScientificName(String scientificName) throws Exception {

		logger.info("loadClusterCommonNameForResultRowByScientificName for scientific name: "+scientificName);
		HashMap<String, ClusterCommonNameDataSourceForResultRow> hashCluster = SessionUtil.getCurrentClusterCommonNameForResultRow(getASLSession());

		if(hashCluster==null){
			logger.warn("Error in loadClusterCommonNameForResultRowByScientificName, hashCluster was not found in session");
			return null;
		}

		ClusterCommonNameDataSourceForResultRow cluster = hashCluster.get(scientificName);
		if(cluster==null){
			logger.warn("Error in loadClusterCommonNameForResultRowByScientificName, cluster was not found in session");
			return null;
		}
		return cluster;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadDataSourceForResultRow(boolean, boolean)
	 */
	@Override
	public List<DataSource> loadDataSourceForResultRow(boolean selected, boolean distinct) throws Exception {

		ResultRowPersistence daoResultRow = null;
		ASLSession session = getASLSession();
		List<DataSource> listDataSource = new ArrayList<DataSource>();

		try {
			daoResultRow = DaoSession.getResultRowDAO(session);

			EntityManager em = daoResultRow.createNewManager();
			List<String> listDN = new ArrayList<String>();
			try {
				String selectString = "select ";
				if(distinct)
					selectString+= "distinct ";

				Query query = em.createQuery(selectString + "t."+ResultRow.DATASOURCE_NAME+" from ResultRow t where t.selected = "+selected );
				listDN = query.getResultList();

			} finally {
				em.close();
			}

			for (String dataSourceName : listDN) {
				listDataSource.add(new DataSource(dataSourceName, dataSourceName));
			}

		} catch (Exception e) {
			logger.error("Error in loadDataSourceForResultRow " +e.getMessage(), e);
			throw new Exception("Error in loadDataSourceForResultRow " + e.getMessage(), e);
		}

		return listDataSource;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadStructuresForTaxonomyClustering()
	 */
	@Override
	public ClusterStructuresForTaxonomyRow loadStructuresForTaxonomyClustering() throws Exception {

		int countSelectedRow = countOfSelectedRow();
		boolean isReduced = false;
		int totalRow = countSelectedRow;


		ASLSession session = getASLSession();
		HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> hashCluster = SessionUtil.getCurrentClusterCommonNameForTaxonomyRow(session);

		//Reset cluster for common name
		if(hashCluster!=null)
			SessionUtil.setCurrentClusterCommonNameForTaxonomyRow(session, null);


		HashMap<String, TaxonomyRow> mapOldChildren = SessionUtil.getHashMapChildrenTaxonomyCache(session);
		//Reset list children to last clustering
		if(mapOldChildren!=null)
			SessionUtil.setHashMapChildrenTaxonomyCache(session, null);

		HashMap<String, TaxonomyRow> mapSynonyms = SessionUtil.getHashMapSynonymsTaxonomyCache(session);
		//Reset list synonyms to last clustering
		if(mapSynonyms!=null)
			SessionUtil.setHashMapSynonymsTaxonomyCache(session, null);

		HashMap<String, TaxonomyRow> mapTaxonomyIds = SessionUtil.getHashMapTaxonomyByIdsCache(session);
		//Reset list synonyms to last clustering
		if(mapTaxonomyIds!=null)
			SessionUtil.setHashMapTaxonomyByIdsCache(session, null);

		//LIMIT NUMBER OF ITEMS TO ConstantsSpeciesDiscovery.LIMIT_ITEM_DETAILS
		if(countSelectedRow>ConstantsSpeciesDiscovery.TAXONOMY_LIMIT_ITEMS_DETAILS){
			countSelectedRow = ConstantsSpeciesDiscovery.TAXONOMY_LIMIT_ITEMS_DETAILS;
			isReduced = true;
		}

		ResultFilter filter = new ResultFilter(false, true, true);
		SearchResult<TaxonomyRow> searchResults = getSearchTaxonomyRow(0, countSelectedRow, filter, true);
		ClusterStructuresForTaxonomyRow cluster = new ClusterStructuresForTaxonomyRow(searchResults,isReduced, totalRow);

		//TODO USE THREAD?
		ManagerClusterCommonNameDataSourceForTaxonomyRow manager = new ManagerClusterCommonNameDataSourceForTaxonomyRow(cluster.getHashClusterScientificNameTaxonomyRowServiceID(), cluster.getHashResult());
		SessionUtil.setCurrentClusterCommonNameForTaxonomyRow(session, manager.getHashClusterCommonNameDataSource());

		//THIS OBJECT IS NOT USED ON CLIENT
		cluster.setHashResult(null);

		return cluster;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#changeStatusOccurrenceJob(java.lang.String, org.gcube.portlets.user.speciesdiscovery.shared.DownloadState)
	 */
	@Override
	public boolean changeStatusOccurrenceJob(String jobIdentifier, DownloadState state) throws Exception {

		OccurrenceJobPersistence occurrenceJobDAO = DaoSession.getOccurrencesJobDAO(getASLSession());

		int count = OccurrenceJobUtil.changeStatusOccurrenceJobById(jobIdentifier, state, occurrenceJobDAO);

		if(count==1)
			return true;

		return false;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#changeStatusTaxonomyJob(java.lang.String, org.gcube.portlets.user.speciesdiscovery.shared.DownloadState)
	 */
	@Override
	public boolean changeStatusTaxonomyJob(String jobIdentifier, DownloadState state) throws Exception {

		TaxonomyJobPersistence taxonomyJobDAO = DaoSession.getTaxonomyJobDAO(getASLSession());

		int count = TaxonomyJobUtil.changeStatusTaxonomyJobById(jobIdentifier, state, taxonomyJobDAO);

		if(count==1)
			return true;

		return false;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#isAvailableTaxonomyJobReportError(java.lang.String)
	 */
	@Override
	public boolean isAvailableTaxonomyJobReportError(String jobIdentifier) throws Exception {

		logger.info("isAvailableTaxonomyJobReportError jobId: "+jobIdentifier);
		try {

			SpeciesService taxonomyService = getSpeciesService();
			return taxonomyService.isAvailableTaxonomyJobErrorFileById(jobIdentifier);

		} catch (Exception e) {
			logger.error("An error occurred getting error (taxonomy) file for jobid "+jobIdentifier,e);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#isAvailableOccurrenceJobReportError(java.lang.String)
	 */
	@Override
	public boolean isAvailableOccurrenceJobReportError(String jobIdentifier) throws Exception {

		logger.info("isAvailableOccurrenceJobReportError jobId: "+jobIdentifier);
		try {

			SpeciesService taxonomyService = getSpeciesService();
			return taxonomyService.isAvailableOccurrenceJobErrorFileById(jobIdentifier);

		} catch (Exception e) {
			logger.error("An error occurred getting error (occurrence) file for jobid "+jobIdentifier,e);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#getLastQuery()
	 */
	@Override
	public String getLastQuery(){
		logger.info("getLastQuery...");
		ASLSession session = getASLSession();
		return SessionUtil.getCurrentQuery(session);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#loadClusterCommonNameForTaxonomyRowByScientificName(java.lang.String)
	 */
	@Override
	public ClusterCommonNameDataSourceForTaxonomyRow loadClusterCommonNameForTaxonomyRowByScientificName(String scientificName) {

		logger.info("loadClusterCommonNameForTaxonomyRowByScientificName for scientific name: "+scientificName);

		HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> hashCluster = SessionUtil.getCurrentClusterCommonNameForTaxonomyRow(getASLSession());

		if(hashCluster==null){
			logger.warn("Error in loadClusterCommonNameForTaxonomyRowByScientificName, hashCluster was not found in session");
			return null;
		}

		ClusterCommonNameDataSourceForTaxonomyRow cluster = hashCluster.get(scientificName);

		if(cluster==null){
			logger.warn("Error in loadClusterCommonNameForTaxonomyRowByScientificName, cluster was not found in session");
			return null;
		}

		return cluster;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#saveGisLayerAsWsLink(org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean saveGisLayerAsWsLink(JobGisLayerModel jobGisLayer, String destinationFolderId, String fileName) throws Exception{

		try {

			Workspace workspace = GetWorkspaceUtil.getWorskspace(getASLSession());
			logger.info("input stream is not null");
			WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(destinationFolderId);
			fileName = WorkspaceUtil.getUniqueName(fileName, folder);

			if(jobGisLayer.getGisViewerAppLink()==null){
		 		SpeciesService speciesService = getSpeciesService();
		 		CompleteJobStatus statusResponse = speciesService.getGisLayerByJobId(jobGisLayer.getJobIdentifier());
		 		GisLayerJobPersistence gisLayerJobDao = DaoSession.getGisLayersJobDAO(getASLSession());

		 		try{
		 			GisLayerJob gLJ = gisLayerJobDao.getItemByIdField(jobGisLayer.getJobIdentifier());
		 			if(gLJ!=null){
		 				jobGisLayer = GisLayerJobUtil.convertJob(gLJ, statusResponse, gisLayerJobDao, speciesService, getASLSession());
		 			}
		 		}catch(Exception e){
		 			logger.error("Error on retrieving gis link from DB for job id: "+jobGisLayer.getJobIdentifier(), e);
		 			throw new Exception(e.getMessage());
		 		}
			}

			workspace.createExternalUrl(fileName, jobGisLayer.getLayerDescription() + "- Layer UUID: "+jobGisLayer.getLayerUUID(), jobGisLayer.getGisViewerAppLink(), destinationFolderId);
			logger.info("Saving External link "+fileName +" completed");
			return true;

		} catch (Exception e) {
			logger.error("Sorry, an error occurred saving the file '"+fileName+"' in your Workspace, try again",e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#changeStatusGisLayerJob(java.lang.String, org.gcube.portlets.user.speciesdiscovery.shared.DownloadState)
	 */
	@Override
	public boolean changeStatusGisLayerJob(String jobId, DownloadState state) {

		GisLayerJobPersistence gisLayerDAO;
		try {

			gisLayerDAO = DaoSession.getGisLayersJobDAO(getASLSession());
			int count = GisLayerJobUtil.changetStatusGisLayerJob(jobId, state, gisLayerDAO);
			if(count==1)
				return true;

			return false;
		}
		catch (Exception e) {
			logger.error("An error occured in changeStatusGisLayerJob for jobId: "+jobId);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#cancelGisLayerJob(java.lang.String)
	 */
	@Override
	public boolean cancelGisLayerJob(String jobIdentifier) throws Exception {

		try{
			SpeciesService speciesService = getSpeciesService();
			//REMOVE JOB ON THE SERVICE
			speciesService.cancelGisLayerByJobId(jobIdentifier);
			GisLayerJobPersistence gisLayerDao = DaoSession.getGisLayersJobDAO(getASLSession());
			int count = GisLayerJobUtil.deleteGisLayerJobById(jobIdentifier, gisLayerDao);
			if(count==1)
				return true;

		}catch (Exception e) {
			logger.error("Erroron deleting gis layer job ", e);
			throw new Exception("Sorry, an error occurred deleting gis layer job", e);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.rpc.TaxonomySearchService#resubmitGisLayerJob(java.lang.String)
	 */
	@Override
	public JobGisLayerModel resubmitGisLayerJob(String jobIdentifier) throws Exception {

		//TODO
		return null;

//		logger.info("Resubmit gis layer job by id: " + jobIdentifier);
//		JobGisLayerModel jobGisLayerModel = null;
//		GisLayerJobPersistence gisLayerJobDAO = DaoSession.getGisLayersJobDAO(getASLSession());
//
//		CriteriaBuilder queryBuilder = gisLayerJobDAO.getCriteriaBuilder();
//		CriteriaQuery<Object> cq = queryBuilder.createQuery();
//		Predicate pr1 =  queryBuilder.equal(gisLayerJobDAO.rootFrom(cq).get(GisLayerJob.ID_FIELD), jobIdentifier);
//		cq.where(pr1);
//
//		Iterator<GisLayerJob> iterator = gisLayerJobDAO.executeCriteriaQuery(cq).iterator();
//
//		GisLayerJob gisLayerJob;
//
//		if(iterator.hasNext())
//			 gisLayerJob = iterator.next();
//		else
//			return jobGisLayerModel;
//
//		SpeciesService speciesService = getSpeciesService();
//
//		//recover taxomyId
//		String speciesJobId = speciesService.generateGisLayerFromOccurrenceKeys(occurrenceKeys, layerTitle, layerDescr, author, credits)
//
//		long submitTime = Calendar.getInstance().getTimeInMillis();
//
//		String name = RESUBMIT + ": " +NormalizeString.lowerCaseUpFirstChar(gisLayerJob.getDescriptiveName());
//
//		//STORE INTO DAO
//		TaxonomyJob speciesJob = new TaxonomyJob(speciesJobId, DownloadState.PENDING.toString(), name, gisLayerJob.getDescriptiveName(), gisLayerJob.getDataSourceName(), gisLayerJob.getRank(), 0, submitTime, 0, gisLayerJob.getTaxonomyId());
//		gisLayerJobDAO.insert(speciesJob);
//
//		jobGisLayerModel = new JobTaxonomyModel(speciesJob.getId(), speciesJob.getDescriptiveName(), DownloadState.PENDING, null, gisLayerJob.getDescriptiveName(), gisLayerJob.getDataSourceName(), gisLayerJob.getRank());
//
//		Date submit = DateUtil.millisecondsToDate(speciesJob.getSubmitTime());
////		jobSpeciesModel.setStartTime(DateUtil.dateToDateFormatString(start));
//		jobGisLayerModel.setSubmitTime(submit);
//		jobGisLayerModel.setEndTime(null);
//
//		return jobGisLayerModel;
	}


}
