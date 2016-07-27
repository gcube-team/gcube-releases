package org.gcube.portlets.user.speciesdiscovery.server.service;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.classification;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.executor;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.occurrence;
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
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.client.proxies.Occurrence;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.data.streams.Stream;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.IteratorPointInfo;
import org.gcube.portlets.user.speciesdiscovery.server.util.StorageUtil;
import org.gcube.portlets.user.speciesdiscovery.shared.Coordinate;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceRepositoryInfo;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchFilters;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

//import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SpeciesService {

	protected Logger logger = Logger.getLogger(SpeciesService.class);

	protected String scope;
	protected ASLSession session;

	protected Manager call;
	protected Occurrence occurrencesCall;
	protected Classification classificationCall;
	protected Executor executorCall;
	
	protected String lastQuery = "";


	public SpeciesService(String scope, ASLSession session) throws Exception
	{
		this(scope);
		this.session = session;
	}
	
	
	public SpeciesService(String scope) throws Exception
	{
		this.scope = scope;
		ScopeProvider.instance.set(scope);
//		this.call = manager().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
//		this.occurrencesCall =  occurrences().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
//	    this.classificationCall = classification().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
		System.out.println("CALLING MANAGER ");
		this.call = manager().withTimeout(3, TimeUnit.MINUTES).build();
		this.executorCall = executor().withTimeout(3, TimeUnit.MINUTES).build();
		this.occurrencesCall =  occurrence().withTimeout(3, TimeUnit.MINUTES).build();
	    this.classificationCall = classification().withTimeout(3, TimeUnit.MINUTES).build();
	}
	
	
	public SpeciesService(String scope, boolean instanceOnlyOccurrence) throws Exception
	{
		this.scope = scope;
		
		if(instanceOnlyOccurrence){
			ScopeProvider.instance.set(scope);
			System.out.println("CALLING OCCURRENCE MANAGER ");
			this.occurrencesCall =  occurrence().withTimeout(3, TimeUnit.MINUTES).build();
		}
	}


	public CloseableIterator<ResultElement> searchByFilters(String searchTerm, SearchType searchType, SearchFilters searchFilters) throws SearchServiceException {
		logger.trace("searchByFilters searchTerm: "+searchTerm+ " usearchFilters: "+searchFilters);

		try {
			logger.trace("query building...");
			String query = QueryBuilder.buildQuery(searchTerm, searchType, searchFilters);
//			System.out.println("query build - OK " + query);
			logger.trace("query build - OK " + query);
//			System.out.println("query: "+query);
			return searchByQuery(query);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}
	
	
	public CloseableIterator<TaxonomyItem> retrieveTaxonomyById(Stream<String> streamIds) throws SearchServiceException {
		logger.trace("retrieveTaxonomyById...");

		try {
			ScopeProvider.instance.set(scope);
			Stream<TaxonomyItem> stream = classificationCall.getTaxaByIds(streamIds);
			return new StreamIterator<TaxonomyItem>(stream);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}
	
	
	public CloseableIterator<TaxonomyItem> retrieveSynonymsById(String id) throws SearchServiceException {
		logger.trace("retrieveSynonymsById...");

		try {
			ScopeProvider.instance.set(scope);
			Stream<TaxonomyItem> stream = classificationCall.getSynonymsById(id);
			return new StreamIterator<TaxonomyItem>(stream);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}


	public CloseableIterator<ResultElement> searchByQuery(String query) throws SearchServiceException {
		logger.trace("search by Query - query is: "+query);
//		System.out.println("searchByQuery query: "+query);

		try {
			ScopeProvider.instance.set(scope);
			lastQuery = query;
//			System.err.println("ScopeProvider SCOPE "+ScopeProvider.instance.get());
			logger.trace("call species service search...");
//			System.out.println("call species service search...");
			Stream<ResultElement> stream = call.search(query);
			return new StreamIterator<ResultElement>(stream);
			
		}catch (UnsupportedPluginException e2) {
			lastQuery = "Invalid query";
			String error = "Error calling the Species Service: plugin usupported";
			logger.error("Error calling the Species Service: " + e2.getMessage(), e2);
			throw new SearchServiceException(error);
		}catch (InvalidQueryException e1) {
			lastQuery = "Invalid query";
			String error = "Error calling the Species Service: query syntax is not valid";
			logger.error("Error calling the Species Service: " + e1.getMessage(), e1);
			throw new SearchServiceException(error);
		} catch (Exception e) {
			lastQuery = "Invalid query";
			String error = "Error calling the Species Service: an error occurred contacting the service";
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException(error);
		}
	}
	
	
	public Stream<ResultElement> searchByQuery2(String query) throws SearchServiceException {
		logger.trace("searchByQuery query: "+query);
//		System.out.println("searchByQuery query: "+query);

		try {
			ScopeProvider.instance.set(scope);
//			System.err.println("ScopeProvider SCOPE "+ScopeProvider.instance.get());
			logger.trace("call species service search...");
			System.out.println("call species service search...");
			return call.search(query);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}
	

	public List<DataSourceModel> getPlugins() throws SearchServiceException {
		logger.trace("getPlugins...");
		try {

			List<DataSourceModel> listDsModel = new ArrayList<DataSourceModel>();

			ScopeProvider.instance.set(scope);
			System.out.println("setting scope "+scope);
			List<PluginDescription> plugin = call.getPluginsDescription();
			
			if(plugin!=null){
				
				logger.trace("*****PluginDescription is NOT null - length: " + plugin.size());
				
				for (int i = 0; i < plugin.size(); i++) {
	
					PluginDescription pluginDescription = plugin.get(i);
					
					ArrayList<DataSourceCapability> datasourceCapabilities = new ArrayList<DataSourceCapability>();
					
					Map<Capabilities, List<Conditions>> pluginCapabilities = pluginDescription.getSupportedCapabilities();
					
					logger.trace("getCapabilities for..." + pluginDescription.getName());
					for (Entry<Capabilities, List<Conditions>> pluginCapability:pluginCapabilities.entrySet()) {
						
						Capabilities capability = pluginCapability.getKey();
						
						ArrayList<SpeciesCapability> datasourceProperties = new ArrayList<SpeciesCapability>();
						for (Conditions condition:pluginCapability.getValue()) datasourceProperties.addAll(getFilterCapabilityFromProperties(condition));
						
						datasourceCapabilities.add(new DataSourceCapability(getGxtCapabilityValueFromCapability(capability), datasourceProperties));
					}
					
					RepositoryInfo rep = pluginDescription.getInfo();
					
					//CREATE DataSourceRepositoryInfo
					DataSourceRepositoryInfo dsInfo = new DataSourceRepositoryInfo();
					
					if(rep!=null){
//						System.out.println("DESCRIPTION REPOSITORY: " + rep.getDescription());
						dsInfo.setLogoUrl(rep.getLogoUrl());
						dsInfo.setPageUrl(rep.getPageReferenceUrl());
						dsInfo.setProperties(getPropertiesFromRepositoryInfoType(rep));
						dsInfo.setDescription(rep.getDescription());
//						dsInfo = new DataSourceRepositoryInfo(rep.getLogoUrl(), rep.getReferencePageUrl(),getPropertiesFromRepositoryInfoType(rep), rep.getDescription());	
						logger.trace("DataSourceRepositoryInfo :"+dsInfo);	
//						logger.trace("Repository description size: " + rep.getDescription().length());
					}
					
					listDsModel.add(new DataSourceModel(pluginDescription.getName(), pluginDescription.getName(), pluginDescription.getDescription(), datasourceCapabilities, dsInfo));	

				}
			}
			else
				logger.trace("*****PluginDescription is null");

			return listDsModel;

		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
//			System.out.println("Error calling the Species Service: " + e);
			e.printStackTrace();
			throw new SearchServiceException("loading the data sources");
		}
	}
	
	private Map<String, String> getPropertiesFromRepositoryInfoType(RepositoryInfo rep){
		
		Map<String, String> mapProperties = new HashMap<String, String>();
		
		if(rep.getProperties()==null){
			logger.trace("*****Properties From RepositoryInfoType is null");
			return mapProperties;
		}
		
		
		for (Entry<String, String> property : rep.getProperties().entrySet()) mapProperties.put(property.getKey(), property.getValue());
		
		return mapProperties;
	}

	private List<SpeciesCapability> getFilterCapabilityFromProperties(Conditions property){

		switch (property) {
			case DATE: return Arrays.asList(SpeciesCapability.FROMDATE, SpeciesCapability.TODATE);
			case COORDINATE: return Arrays.asList(SpeciesCapability.UPPERBOUND, SpeciesCapability.LOWERBOUND);
			default: return Collections.singletonList(SpeciesCapability.UNKNOWN);

		}
	}

	private SpeciesCapability getGxtCapabilityValueFromCapability(Capabilities capability){

		switch (capability) {
			case Classification: return SpeciesCapability.TAXONOMYITEM;
			case NamesMapping: return SpeciesCapability.NAMESMAPPING;
			case Occurrence: return SpeciesCapability.RESULTITEM;
			case Expansion: return SpeciesCapability.SYNONYMS;
			case Unfold: return SpeciesCapability.UNFOLD;
			default: return SpeciesCapability.UNKNOWN;
		}
	}

	/*protected List<Property> createFilterProperties(SearchFilters searchFilters)
	{
		List<Property> properties = new ArrayList<Property>();
		if (searchFilters.getUpperBound()!=null) properties.add(new Property(Properties.CoordinateTo, convertCoordinate(searchFilters.getUpperBound())));
		if (searchFilters.getLowerBound()!=null) properties.add(new Property(Properties.CoordinateFrom, convertCoordinate(searchFilters.getLowerBound())));

		if (searchFilters.getFromDate()!=null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.setTime(searchFilters.getFromDate());
			properties.add(new Property(Properties.DateFrom, fromDate));
		}
		if (searchFilters.getToDate()!=null) {
			Calendar toDate = Calendar.getInstance();
			toDate.setTime(searchFilters.getToDate());
			properties.add(new Property(Properties.DateTo, toDate));
		}

		return properties;
	}*/

	protected org.gcube.data.spd.model.Coordinate convertCoordinate(Coordinate coordinate)
	{
		return new org.gcube.data.spd.model.Coordinate(coordinate.getLatitude(), coordinate.getLongitude());
	}

	public CloseableIterator<OccurrencePoint> getOccurrencesByKeys(List<String> keys) throws SearchServiceException {
		try {
			Stream<String> keysStream = convert(keys);
			ScopeProvider.instance.set(scope);
			Stream<OccurrencePoint> stream =  occurrencesCall.getByKeys(keysStream);
			return new StreamIterator<OccurrencePoint>(stream);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}
	
	public CloseableIterator<OccurrencePoint> getOccurrencesByIds(List<String> ids) throws SearchServiceException {
		try {
			Stream<String> idsStream = convert(ids);
			ScopeProvider.instance.set(scope);
			Stream<OccurrencePoint> stream =  occurrencesCall.getByIds(idsStream);
			return new StreamIterator<OccurrencePoint>(stream);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}

//	public String generateMapFromOccurrencePoints(List<String> keys) throws SearchServiceException {
//		try {
//			Stream<String> keysStream = convert(keys);
//			ScopeProvider.instance.set(scope);
//			return occurrencesCall.getLayerByIds(keysStream);
//		} catch (Exception e) {
//			logger.error("Error calling the Species Service: " + e.getMessage(), e);
//			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
//		}
//	}
	
	public String generateMapFromOccurrencePoints(IteratorPointInfo streamKey) throws SearchServiceException {
		try {
//			Stream<String> keysStream = convert(keys);
			ScopeProvider.instance.set(scope);
			
			return occurrencesCall.createLayer(streamKey);
		} catch (Exception e) {
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
		}
	}
	

//	public File getOccurrencesAsDarwinCoreByIds(List<String> ids) throws SearchServiceException {
//		try {
//			Stream<String> keysStream = convert(ids);
//			ScopeProvider.instance.set(scope);
//			File occurrenceFile = occurrencesCall.getDarwinCoreByIds(keysStream);
//			return occurrenceFile;
//		} catch (Exception e) {
//			logger.error("Error calling the Species Service: " + e.getMessage(), e);
//			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
//		}
//	}
//	
//	public File getOccurrencesAsDarwinCoreArchive(List<String> ids) throws SearchServiceException {
//		try {
//			Stream<String> keysStream = convert(ids);
//			ScopeProvider.instance.set(scope);
//			File occurrenceFile = classificationCall.getDarwinCoreArchive(keysStream);
//			return occurrenceFile;
//		} catch (Exception e) {
//			logger.error("Error calling the Species Service: " + e.getMessage(), e);
//			throw new SearchServiceException("Error calling the Species Service: "+e.getMessage());
//		}
//	}
	
	public  StreamIterator<TaxonomyItem> getTaxonChildrenByParentId(String parentId) throws Exception{
		
		try {
			ScopeProvider.instance.set(scope);
			Stream<TaxonomyItem> items = classificationCall.getTaxonChildrenById(parentId);
			return new StreamIterator<TaxonomyItem>(items);
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Error calling the Species Service: " + e.getMessage(), e);
			throw new Exception("Error calling the Species Service: "+e.getMessage());
		}
	}

	public Status getTaxonomyJobById(String jobId) {

		ScopeProvider.instance.set(scope);
		Status status = null;
		
		try{
		
			status = this.executorCall.getStatus(jobId);
				
		}catch (InvalidIdentifierException e) {
			logger.error("Error on service for get job by Id - InvalidIdentifierException");
			status = null;
			
		}catch (Exception e) {
			logger.error("Error on service for get job by Id: " + e.getMessage(), e);
			status = null;
//			return new Status();
		}
		
		return status;
	}

	public InputStream getTaxonomyJobFileById(String jobIdentifier) throws Exception {

		InputStream is = null;
		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getResultLink(jobIdentifier);
			
			if(url==null || url.isEmpty()){
				logger.error("URL returned by species service is: "+url);
				throw new StorageUrlIsEmpty();
			}
			
			logger.trace("URL returned by species service is: "+url);
			is = StorageUtil.getInputStreamByStorageClient(url);
		
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Error saving file: "+e.getMessage(), e);
			throw new Exception("Error saving file: "+e.getMessage());
		}

		return is;
	}
	
	
	public InputStream getTaxonomyJobErrorFileById(String jobIdentifier) throws Exception {

		InputStream is = null;
		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			
			if(url==null || url.isEmpty()){
				logger.error("URL returned by species service is: "+url);
				throw new StorageUrlIsEmpty();
			}
			
			logger.trace("URL returned by species service is: "+url);
			is = StorageUtil.getInputStreamByStorageClient(url);
		
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Error saving error file: "+e.getMessage(), e);
			throw new Exception("Error saving file: "+e.getMessage());
		}

		return is;
	}
	
	public boolean isAvailableTaxonomyJobErrorFileById(String jobIdentifier) throws Exception {

		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			
			if(url==null || url.isEmpty()){
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			logger.error("Error in is Available Taxonomy JobError File: "+e.getMessage(), e);
			throw new Exception("Error in is Available Taxonomy JobError File: "+e.getMessage());
		}
	}

	public String createTaxonomyJobForDWCAByChildren(String taxonomyId) throws Exception {
		
		ScopeProvider.instance.set(scope);
		
		try {
			return this.executorCall.createDwCAByChildren(taxonomyId);
		
		} catch (Exception e) {
			logger.error("Error in createTaxonomyJob: "+e.getMessage(), e);
			throw new Exception("Error in createTaxonomyJob: "+e.getMessage());
		}
	}
	
	public String createTaxonomyJobForDWCAByIds(List<String> ids) throws Exception {
		
		ScopeProvider.instance.set(scope);
		
		try {
			Stream<String> keysStream = convert(ids);
			return executorCall.createDwCAByIds(keysStream);
		} catch (Exception e) {
			logger.error("Error in createTaxonomyJobForDWCA: "+e.getMessage(), e);
			throw new Exception("Error in createTaxonomyJobForDWCA: "+e.getMessage());
		}
	}

	public void cancelTaxonomyJobById(String jobIdentifier){
		
		try{
			ScopeProvider.instance.set(scope);		
			this.executorCall.removeJob(jobIdentifier);
		}catch (Exception e) {
			logger.error("Error on service for remove job: " + e.getMessage(), e);
		}
	}


	public Status getOccurrenceJobById(String jobId) {
		try{
			ScopeProvider.instance.set(scope);
			return this.executorCall.getStatus(jobId); //CHANGE INTO OCCURRENCE JOB ************************************************************************************** //TODO
				
		}catch (InvalidIdentifierException e) {
			logger.error("Error on service for get job by Id - InvalidIdentifierException");
			return null;
			
		}catch (Exception e) {
			logger.error("Error on service for get job by Id: " + e.getMessage());
			return null;
		}
	}
	
	
	public String createOccurrenceCSVJob(Stream<String> streamKey) throws Exception{
		
		try {
			ScopeProvider.instance.set(scope);
			return this.executorCall.createCSV(streamKey);
		
		} catch (Exception e) {
			logger.error("Error in createOccurrenceCSVJob: "+e.getMessage(), e);
			throw new Exception("Error in createOccurrenceCSVJob: "+e.getMessage());
		}
	}
	
	
	public String createOccurrenceDARWINCOREJob(Stream<String> streamKey) throws Exception{
		
		try {
			ScopeProvider.instance.set(scope);
			return this.executorCall.createDarwincoreFromOccurrenceKeys(streamKey);
		
		} catch (Exception e) {
			logger.error("Error in createOccurrenceDARWINCOREJob: "+e.getMessage(), e);
			throw new Exception("Error in createOccurrenceDARWINCOREJob: "+e.getMessage());
		}
	}
	
	
	public String createOccurrenceCSVOpenModellerJob(Stream<String> streamKey) throws Exception{
	
		try {
			ScopeProvider.instance.set(scope);
			return this.executorCall.createCSVforOM(streamKey);
		
		} catch (Exception e) {
			logger.error("Error in createOccurrenceCSVOpenModellerJob: "+e.getMessage(), e);
			throw new Exception("Error in createOccurrenceCSVOpenModellerJob: "+e.getMessage());
		}
	}


	public void cancelOccurrenceJobById(String jobIdentifier){
		
		try{
			ScopeProvider.instance.set(scope);		
			this.executorCall.removeJob(jobIdentifier); //CHANGE INTO OCCURRENCE JOB ************************************************************************************** ** //TODO
		}catch (Exception e) {
			logger.error("Error on service for remove job: " + e.getMessage(), e);
		}
	}


	public InputStream getOccurrenceJobFileById(String jobIdentifier) throws Exception {
		InputStream is = null;
		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getResultLink(jobIdentifier); //CHANGE INTO OCCURRENCE JOB ************************************************************************************** ** //TODO
			logger.trace("URL returned by species service is: "+url);
			
			if(url==null || url.isEmpty()){
				logger.error("URL returned by species service is: "+url);
				throw new StorageUrlIsEmpty();
			}
			
			is = StorageUtil.getInputStreamByStorageClient(url);
		
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Error saving file: "+e.getMessage(), e);
			throw new Exception("Error saving file: "+e.getMessage());
		}

		return is;
	}
	
	public InputStream getOccurrenceJobErrorFileById(String jobIdentifier) throws Exception {

		InputStream is = null;
		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			logger.trace("URL returned by species service is: "+url);
			
			if(url==null || url.isEmpty()){
				logger.error("URL returned by species service is: "+url);
				throw new StorageUrlIsEmpty();
			}
			
			is = StorageUtil.getInputStreamByStorageClient(url);
		
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Error saving file: "+e.getMessage(), e);
			throw new Exception("Error saving file: "+e.getMessage());
		}

		return is;
	}
	
	public boolean isAvailableOccurrenceJobErrorFileById(String jobIdentifier) throws Exception {

		ScopeProvider.instance.set(scope);
		
		try {
			String url = this.executorCall.getErrorLink(jobIdentifier);
			
			if(url==null || url.isEmpty()){
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			logger.error("Error in is Available Occurrence JobError File: "+e.getMessage(), e);
			throw new Exception("Error in is Available Occurrence JobError File: "+e.getMessage());
		}
	}


	public String getLastQuery() {
		return lastQuery;
	}


}
