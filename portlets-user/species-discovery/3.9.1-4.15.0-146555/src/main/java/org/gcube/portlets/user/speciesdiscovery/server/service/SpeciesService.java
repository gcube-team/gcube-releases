
package org.gcube.portlets.user.speciesdiscovery.server.service;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.classification;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.executor;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.occurrences;
import static org.gcube.data.streams.dsl.Streams.convert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.ClassificationClient;
import org.gcube.data.spd.client.proxies.ExecutorClient;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.gcube.data.spd.client.proxies.OccurrenceClient;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.util.StorageUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.Coordinate;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceRepositoryInfo;
import org.gcube.portlets.user.speciesdiscovery.shared.InvalidJobIdException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;


/**
 * The Class SpeciesService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
public class SpeciesService {

	protected Logger logger = Logger.getLogger(SpeciesService.class);
	protected String scope;
	protected ManagerClient call;
	protected OccurrenceClient occurrencesCall;
	protected ClassificationClient classificationCall;
	protected ExecutorClient executorCall;
	protected String lastQuery = "";


	/**
	 * Instantiates a new species service.
	 *
	 * @throws Exception the exception
	 */
	public SpeciesService() throws Exception {
		System.out.println("CALLING MANAGER ");
		this.call = manager().withTimeout(3, TimeUnit.MINUTES).build();
		this.executorCall = executor().withTimeout(3, TimeUnit.MINUTES).build();
		this.occurrencesCall = occurrences().withTimeout(3, TimeUnit.MINUTES).build();
		this.classificationCall = classification().withTimeout(3, TimeUnit.MINUTES).build();
	}

	/**
	 * Instantiates a new species service.
	 *
	 * @param scope
	 *            the scope
	 * @param instanceOnlyOccurrence
	 *            the instance only occurrence
	 * @throws Exception
	 *             the exception
	 */
	public SpeciesService(String scope, boolean instanceOnlyOccurrence)
		throws Exception {

		this.scope = scope;
		if (instanceOnlyOccurrence) {
			System.out.println("CALLING OCCURRENCE MANAGER ");
			this.occurrencesCall = occurrences().withTimeout(3, TimeUnit.MINUTES).build();
		}
	}

	/**
	 * Search by filters.
	 *
	 * @param searchTerm
	 *            the search term
	 * @param searchType
	 *            the search type
	 * @param searchFilters
	 *            the search filters
	 * @return the closeable iterator
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<ResultElement> searchByFilters(
		String searchTerm, SearchType searchType, SearchFilters searchFilters)
		throws SearchServiceException {

		logger.trace("searchByFilters searchTerm: " + searchTerm +
			" usearchFilters: " + searchFilters);
		try {
			logger.trace("query building...");
			String query =
				QueryBuilder.buildQuery(searchTerm, searchType, searchFilters);
			// System.out.println("query build - OK " + query);
			logger.trace("query build - OK " + query);
			// System.out.println("query: "+query);
			return searchByQuery(query);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(
				"Error calling the Species Service: " + e.getMessage());
		}
	}

	/**
	 * Retrieve taxonomy by id.
	 *
	 * @param ids
	 *            the ids
	 * @return the closeable iterator
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<TaxonomyItem> retrieveTaxonomyById(List<String> ids)
		throws SearchServiceException {

		logger.trace("retrieveTaxonomyById...");
		try {
			Stream<TaxonomyItem> stream = classificationCall.getTaxaByIds(ids);
			return new StreamIterator<TaxonomyItem>(stream);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(
				"Error calling the Species Service: " + e.getMessage());
		}
	}

	/**
	 * Retrieve synonyms by id.
	 *
	 * @param id
	 *            the id
	 * @return the closeable iterator
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<TaxonomyItem> retrieveSynonymsById(String id)
		throws SearchServiceException {

		logger.trace("retrieveSynonymsById...");
		try {
			Stream<TaxonomyItem> stream =
				classificationCall.getSynonymsById(id);
			return new StreamIterator<TaxonomyItem>(stream);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(
				"Error calling the Species Service: " + e.getMessage());
		}
	}

	/**
	 * Search by query.
	 *
	 * @param query
	 *            the query
	 * @return the closeable iterator
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<ResultElement> searchByQuery(String query)
		throws SearchServiceException {

		logger.trace("search by Query - query is: " + query);
		// System.out.println("searchByQuery query: "+query);
		try {
			lastQuery = query;
			logger.trace("call species service search...");
			// System.out.println("call species service search...");
			Stream<ResultElement> stream = call.search(query);
			return new StreamIterator<ResultElement>(stream);
		}
		catch (UnsupportedPluginException e2) {
			lastQuery = "Invalid query";
			String error =
				"Error calling the Species Service: plugin usupported";
			logger.error(
				"Error calling the Species Service: " + e2.getMessage(), e2);
			throw new SearchServiceException(error);
		}
		catch (InvalidQueryException e1) {
			lastQuery = "Invalid query";
			String error =
				"Error calling the Species Service: query syntax is not valid";
			logger.error(
				"Error calling the Species Service: " + e1.getMessage(), e1);
			throw new SearchServiceException(error);
		}
		catch (Exception e) {
			lastQuery = "Invalid query";
			String error =
				"Error calling the Species Service: an error occurred contacting the service";
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(error);
		}
	}

	/**
	 * Search by query2.
	 *
	 * @param query
	 *            the query
	 * @return the stream
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public Stream<ResultElement> searchByQuery2(String query)
		throws SearchServiceException {

		logger.trace("searchByQuery query: " + query);
		try {
			logger.trace("call species service search...");
			System.out.println("call species service search...");
			return call.search(query);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(
				"Error calling the Species Service: " + e.getMessage());
		}
	}

	/**
	 * Gets the plugins.
	 *
	 * @return the plugins
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public List<DataSourceModel> getPlugins()
		throws SearchServiceException {

		logger.trace("getPlugins...");
		try {
			List<DataSourceModel> listDsModel =
				new ArrayList<DataSourceModel>();
			System.out.println("setting scope " + scope);
			List<PluginDescription> plugin = call.getPluginsDescription();
			if (plugin != null) {
				logger.trace("*****PluginDescription is NOT null - length: "+plugin.size());
				for (int i = 0; i < plugin.size(); i++) {
					PluginDescription pluginDescription = plugin.get(i);
					ArrayList<DataSourceCapability> datasourceCapabilities =
						new ArrayList<DataSourceCapability>();
					Map<Capabilities, List<Conditions>> pluginCapabilities =
						pluginDescription.getSupportedCapabilities();
					logger.info("getCapabilities for plugin: " +pluginDescription.getName());
					for (Entry<Capabilities, List<Conditions>> pluginCapability : pluginCapabilities.entrySet()) {
						Capabilities capability = pluginCapability.getKey();
						logger.info("capability name: " +capability.name());
						ArrayList<SpeciesCapability> datasourceProperties = new ArrayList<SpeciesCapability>();
						logger.info("capability value: " +pluginCapability.getValue());
						for (Conditions condition : pluginCapability.getValue())
							datasourceProperties.addAll(getFilterCapabilityFromProperties(condition));

						datasourceCapabilities.add(new DataSourceCapability(getGxtCapabilityValueFromCapability(capability), datasourceProperties));
					}
					RepositoryInfo rep = pluginDescription.getInfo();
					// CREATE DataSourceRepositoryInfo
					DataSourceRepositoryInfo dsInfo =
						new DataSourceRepositoryInfo();
					if (rep != null) {
						// System.out.println("DESCRIPTION REPOSITORY: " +
						// rep.getDescription());
						dsInfo.setLogoUrl(rep.getLogoUrl());
						dsInfo.setPageUrl(rep.getPageReferenceUrl());
						dsInfo.setProperties(getPropertiesFromRepositoryInfoType(rep));
						dsInfo.setDescription(rep.getDescription());
						// dsInfo = new
						// DataSourceRepositoryInfo(rep.getLogoUrl(),
						// rep.getReferencePageUrl(),getPropertiesFromRepositoryInfoType(rep),
						// rep.getDescription());
						logger.trace("DataSourceRepositoryInfo :" + dsInfo);
						// logger.trace("Repository description size: " +
						// rep.getDescription().length());
					}
					listDsModel.add(new DataSourceModel(
						pluginDescription.getName(),
						pluginDescription.getName(),
						pluginDescription.getDescription(),
						datasourceCapabilities, dsInfo));
				}
			}
			else
				logger.trace("*****PluginDescription is null");
			return listDsModel;
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			// System.out.println("Error calling the Species Service: " + e);
			e.printStackTrace();
			throw new SearchServiceException("loading the data sources");
		}
	}

	/**
	 * Gets the properties from repository info type.
	 *
	 * @param rep
	 *            the rep
	 * @return the properties from repository info type
	 */
	private Map<String, String> getPropertiesFromRepositoryInfoType(
		RepositoryInfo rep) {

		Map<String, String> mapProperties = new HashMap<String, String>();
		if (rep.getProperties() == null) {
			logger.trace("*****Properties From RepositoryInfoType is null");
			return mapProperties;
		}
		for (Entry<String, String> property : rep.getProperties().entrySet())
			mapProperties.put(property.getKey(), property.getValue());
		return mapProperties;
	}

	/**
	 * Gets the filter capability from properties.
	 *
	 * @param property
	 *            the property
	 * @return the filter capability from properties
	 */
	private List<SpeciesCapability> getFilterCapabilityFromProperties(Conditions property) {

		if(property==null){
			logger.info("Conditions is null");
			return Collections.singletonList(SpeciesCapability.UNKNOWN);
		}

		switch (property) {
		case DATE:
			return Arrays.asList(SpeciesCapability.FROMDATE, SpeciesCapability.TODATE);
		case COORDINATE:
			return Arrays.asList(SpeciesCapability.UPPERBOUND, SpeciesCapability.LOWERBOUND);
		default:
			return Collections.singletonList(SpeciesCapability.UNKNOWN);
		}
	}

	/**
	 * Gets the gxt capability value from capability.
	 *
	 * @param capability
	 *            the capability
	 * @return the gxt capability value from capability
	 */
	private SpeciesCapability getGxtCapabilityValueFromCapability(
		Capabilities capability) {

		switch (capability) {
		case Classification:
			return SpeciesCapability.TAXONOMYITEM;
		case NamesMapping:
			return SpeciesCapability.NAMESMAPPING;
		case Occurrence:
			return SpeciesCapability.RESULTITEM;
		case Expansion:
			return SpeciesCapability.SYNONYMS;
		case Unfold:
			return SpeciesCapability.UNFOLD;
		default:
			return SpeciesCapability.UNKNOWN;
		}
	}

	/**
	 * Convert coordinate.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return the org.gcube.data.spd.model. coordinate
	 */
	protected org.gcube.data.spd.model.Coordinate convertCoordinate(
		Coordinate coordinate) {

		return new org.gcube.data.spd.model.Coordinate(
			coordinate.getLatitude(), coordinate.getLongitude());
	}

	/**
	 * Gets the occurrences by keys.
	 *
	 * @param keys
	 *            the keys
	 * @return the occurrences by keys
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<OccurrencePoint> getOccurrencesByKeys(
		List<String> keys)
		throws SearchServiceException {

		try {
			logger.trace("Calling occurrencesCall passing keys: "+keys);
			if(occurrencesCall==null){
				logger.warn("occurrencesCall is null, instancing again...");
				this.occurrencesCall = occurrences().withTimeout(3, TimeUnit.MINUTES).build();
			}
			Stream<OccurrencePoint> stream = occurrencesCall.getByKeys(keys);
			return new StreamIterator<OccurrencePoint>(stream);
		}
		catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: " + e.getMessage());
		}
	}

	/**
	 * Gets the occurrences by ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the occurrences by ids
	 * @throws SearchServiceException
	 *             the search service exception
	 */
	public CloseableIterator<OccurrencePoint> getOccurrencesByIds(
		List<String> ids)
		throws SearchServiceException {

		try {
			// Stream<String> idsStream = convert(ids);
			Stream<OccurrencePoint> stream = occurrencesCall.getByIds(ids);
			return new StreamIterator<OccurrencePoint>(stream);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(
				"Error calling the Species Service: " + e.getMessage());
		}
	}


	/**
	 * Generate gis layer from occurrence keys.
	 *
	 * @param occurrenceKeys the occurrence keys
	 * @param layerTitle the layer title
	 * @param layerDescr the layer descr
	 * @param author the author
	 * @param credits the credits
	 * @return the string
	 * @throws SearchServiceException the search service exception
	 */
	public String generateGisLayerFromOccurrenceKeys(List<String> occurrenceKeys, String layerTitle, String layerDescr, String author, String credits) throws SearchServiceException {

		try {
			ExecutorClient creator = AbstractPlugin.executor().build();
			Stream<String> keyStream = Streams.convert(occurrenceKeys);
			MetadataDetails details= new MetadataDetails(layerTitle, layerDescr, layerTitle, author, credits);
			return creator.createLayer(keyStream, details);
		}
		catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: " + e.getMessage());
		}
	}


	/**
	 * Gets the gis layer by job id.
	 *
	 * @param serverJobId the server job id
	 * @return the gis layer by job id
	 * @throws InvalidJobIdException the invalid job id exception
	 * @throws Exception the exception
	 */
	public CompleteJobStatus getGisLayerByJobId(String serverJobId) throws InvalidJobIdException, Exception{

		try {
			ExecutorClient creator = AbstractPlugin.executor().build();
			logger.debug("ExecutorClient is null: "+(creator==null));
			logger.debug("Get status for job Id: "+serverJobId);
			return creator.getStatus(serverJobId);
		}
		catch (InvalidIdentifierException e) {
			logger.error("Error calling the Species Service: " + e.getMessage());
			throw new InvalidJobIdException(
				"Invalid job id: " + serverJobId);
		}catch (Exception e) {
			throw new Exception("Service exception: ",e);
		}
	}


	/**
	 * Gets the gis layer result link by job id.
	 *
	 * @param serverJobId the server job id
	 * @return the gis layer result link by job id
	 * @throws InvalidJobIdException the invalid job id exception
	 */
	public String getGisLayerResultLinkByJobId(String serverJobId) throws InvalidJobIdException {

		try {
			ExecutorClient creator = AbstractPlugin.executor().build();
			return creator.getResultLink(serverJobId);
		}
		catch (InvalidIdentifierException e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new InvalidJobIdException(
				"Invalid job id: " + serverJobId);
		}
	}


	/**
	 * Cancel gis layer by job id.
	 *
	 * @param serverJobId the server job id
	 */
	public void cancelGisLayerByJobId(String serverJobId) {

		try {
			ExecutorClient creator = AbstractPlugin.executor().build();
			creator.removeJob(serverJobId);
		}
		catch (Exception e) {
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the taxon children by parent id.
	 *
	 * @param parentId
	 *            the parent id
	 * @return the taxon children by parent id
	 * @throws Exception
	 *             the exception
	 */
	public StreamIterator<TaxonomyItem> getTaxonChildrenByParentId(
		String parentId)
		throws Exception {

		try {
			Stream<TaxonomyItem> items =
				classificationCall.getTaxonChildrenById(parentId);
			return new StreamIterator<TaxonomyItem>(items);
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error(
				"Error calling the Species Service: " + e.getMessage(), e);
			throw new Exception("Error calling the Species Service: " +
				e.getMessage());
		}
	}

	/**
	 * Gets the taxonomy job by id.
	 *
	 * @param jobId
	 *            the job id
	 * @return the taxonomy job by id
	 */
	public CompleteJobStatus getTaxonomyJobById(String jobId) {

		CompleteJobStatus status = null;
		try {
			status = this.executorCall.getStatus(jobId);
		}
		catch (InvalidIdentifierException e) {
			logger.error("Error on service for get job by Id - InvalidIdentifierException");
			status = null;
		}
		catch (Exception e) {
			logger.error(
				"Error on service for get job by Id: " + e.getMessage(), e);
			status = null;
			// return new Status();
		}
		return status;
	}

	/**
	 * Gets the taxonomy job file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return the taxonomy job file by id
	 * @throws Exception
	 *             the exception
	 */
	public InputStream getTaxonomyJobFileById(String jobIdentifier)
		throws Exception {

		InputStream is = null;
		try {
			String url = this.executorCall.getResultLink(jobIdentifier);
			if (url == null || url.isEmpty()) {
				logger.error("URL returned by species service is: " + url);
				throw new StorageUrlIsEmpty();
			}
			logger.trace("URL returned by species service is: " + url);
			is = StorageUtil.getInputStreamByStorageClient(url);
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error saving file: " + e.getMessage(), e);
			throw new Exception("Error saving file: " + e.getMessage());
		}
		return is;
	}

	/**
	 * Gets the taxonomy job error file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return the taxonomy job error file by id
	 * @throws Exception
	 *             the exception
	 */
	public InputStream getTaxonomyJobErrorFileById(String jobIdentifier)
		throws Exception {

		InputStream is = null;
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			if (url == null || url.isEmpty()) {
				logger.error("URL returned by species service is: " + url);
				throw new StorageUrlIsEmpty();
			}
			logger.trace("URL returned by species service is: " + url);
			is = StorageUtil.getInputStreamByStorageClient(url);
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error saving error file: " + e.getMessage(), e);
			throw new Exception("Error saving file: " + e.getMessage());
		}
		return is;
	}

	/**
	 * Checks if is available taxonomy job error file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return true, if is available taxonomy job error file by id
	 * @throws Exception
	 *             the exception
	 */
	public boolean isAvailableTaxonomyJobErrorFileById(String jobIdentifier)
		throws Exception {

		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			if (url == null || url.isEmpty()) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			logger.error(
				"Error in is Available Taxonomy JobError File: " +
					e.getMessage(), e);
			throw new Exception(
				"Error in is Available Taxonomy JobError File: " +
					e.getMessage());
		}
	}

	/**
	 * Creates the taxonomy job for dwca by children.
	 *
	 * @param taxonomyId
	 *            the taxonomy id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String createTaxonomyJobForDWCAByChildren(String taxonomyId)
		throws Exception {

		try {
			return this.executorCall.createDwCAByChildren(taxonomyId);
		}
		catch (Exception e) {
			logger.error("Error in createTaxonomyJob: " + e.getMessage(), e);
			throw new Exception("Error in createTaxonomyJob: " + e.getMessage());
		}
	}

	/**
	 * Creates the taxonomy job for dwca by ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String createTaxonomyJobForDWCAByIds(List<String> ids)
		throws Exception {

		try {
			Stream<String> keysStream = convert(ids);
			return executorCall.createDwCAByIds(keysStream);
		}
		catch (Exception e) {
			logger.error(
				"Error in createTaxonomyJobForDWCA: " + e.getMessage(), e);
			throw new Exception("Error in createTaxonomyJobForDWCA: " +
				e.getMessage());
		}
	}

	/**
	 * Cancel taxonomy job by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 */
	public void cancelTaxonomyJobById(String jobIdentifier) {

		try {
			this.executorCall.removeJob(jobIdentifier);
		}
		catch (Exception e) {
			logger.error(
				"Error on service for remove job: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the occurrence job by id.
	 *
	 * @param jobId
	 *            the job id
	 * @return the occurrence job by id
	 */
	public CompleteJobStatus getOccurrenceJobById(String jobId) {

		try {
			return this.executorCall.getStatus(jobId);
		}catch (InvalidIdentifierException e) {
			logger.error("Error on service for get job by Id - InvalidIdentifierException");
			return null;
		}
		catch (Exception e) {
			logger.error("Error on service for get job by Id: " +
				e.getMessage());
			return null;
		}
	}

	/**
	 * Creates the occurrence csv job.
	 *
	 * @param streamKey
	 *            the stream key
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String createOccurrenceCSVJob(Stream<String> streamKey)
		throws Exception {

		try {
			return this.executorCall.createCSV(streamKey);
		}
		catch (Exception e) {
			logger.error(
				"Error in createOccurrenceCSVJob: " + e.getMessage(), e);
			throw new Exception("Error in createOccurrenceCSVJob: " +
				e.getMessage());
		}
	}

	/**
	 * Creates the occurrence darwincore job.
	 *
	 * @param streamKey
	 *            the stream key
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String createOccurrenceDARWINCOREJob(Stream<String> streamKey)
		throws Exception {

		try {
			return this.executorCall.createDarwincoreFromOccurrenceKeys(streamKey);
		}
		catch (Exception e) {
			logger.error(
				"Error in createOccurrenceDARWINCOREJob: " + e.getMessage(), e);
			throw new Exception("Error in createOccurrenceDARWINCOREJob: " +
				e.getMessage());
		}
	}

	/**
	 * Creates the occurrence csv open modeller job.
	 *
	 * @param streamKey
	 *            the stream key
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String createOccurrenceCSVOpenModellerJob(Stream<String> streamKey)
		throws Exception {

		try {
			return this.executorCall.createCSVforOM(streamKey);
		}
		catch (Exception e) {
			logger.error(
				"Error in createOccurrenceCSVOpenModellerJob: " +
					e.getMessage(), e);
			throw new Exception(
				"Error in createOccurrenceCSVOpenModellerJob: " +
					e.getMessage());
		}
	}

	/**
	 * Cancel occurrence job by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 */
	public void cancelOccurrenceJobById(String jobIdentifier) {

		try {
			this.executorCall.removeJob(jobIdentifier); // CHANGE INTO
														// OCCURRENCE JOB
														// **************************************************************************************
														// ** //TODO
		}
		catch (Exception e) {
			logger.error(
				"Error on service for remove job: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the occurrence job file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return the occurrence job file by id
	 * @throws Exception
	 *             the exception
	 */
	public InputStream getOccurrenceJobFileById(String jobIdentifier)
		throws Exception {

		InputStream is = null;
		try {
			String url = this.executorCall.getResultLink(jobIdentifier);
			logger.trace("URL returned by species service is: " + url);
			if (url == null || url.isEmpty()) {
				logger.error("URL returned by species service is: " + url);
				throw new StorageUrlIsEmpty();
			}
			is = StorageUtil.getInputStreamByStorageClient(url);
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error saving file: " + e.getMessage(), e);
			throw new Exception("Error saving file: " + e.getMessage());
		}
		return is;
	}

	/**
	 * Gets the occurrence job error file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return the occurrence job error file by id
	 * @throws Exception
	 *             the exception
	 */
	public InputStream getOccurrenceJobErrorFileById(String jobIdentifier)
		throws Exception {

		InputStream is = null;
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			logger.trace("URL returned by species service is: " + url);
			if (url == null || url.isEmpty()) {
				logger.error("URL returned by species service is: " + url);
				throw new StorageUrlIsEmpty();
			}
			is = StorageUtil.getInputStreamByStorageClient(url);
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error saving file: " + e.getMessage(), e);
			throw new Exception("Error saving file: " + e.getMessage());
		}
		return is;
	}

	/**
	 * Checks if is available occurrence job error file by id.
	 *
	 * @param jobIdentifier
	 *            the job identifier
	 * @return true, if is available occurrence job error file by id
	 * @throws Exception
	 *             the exception
	 */
	public boolean isAvailableOccurrenceJobErrorFileById(String jobIdentifier)
		throws Exception {

		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			if (url == null || url.isEmpty()) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			logger.error(
				"Error in is Available Occurrence JobError File: " +
					e.getMessage(), e);
			throw new Exception(
				"Error in is Available Occurrence JobError File: " +
					e.getMessage());
		}
	}

	/**
	 * Gets the last query.
	 *
	 * @return the last query
	 */
	public String getLastQuery() {

		return lastQuery;
	}
}
