package org.gcube.index;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.gcube.index.entities.AutocompleteResponse;
import org.gcube.index.entities.SearchResponse;
import org.gcube.index.entities.Snippet;
import org.gcube.index.entities.Title;
import org.gcube.index.exceptions.BadRequestException;
import org.gcube.index.exceptions.InternalServerErrorException;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.common.Constants;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/")
public class Index {
	private static final Gson gson = new Gson();

	
	public static final String CACHED_FILEPATH_PROP = "cached_filepath";

	private String scope = null;
	public String cachedFilepath;
	private IndexClient indexClient = null;
	private List<String> projectedFields = new ArrayList<String>();

	// rank, ObjectID, gDocCollectionID, gDocCollectionLang, S, title

	private String collectionID;
	private List<String> searchFields;
	private String projections;
	
	
	private List<String> autocompleteProjectedFields = new ArrayList<String>();
	private List<String> autocompleteProjectedFields2 = new ArrayList<String>();
	
	private String autocompleteCollectionID;
	private List<String> autocompleteSearchFields;
	private List<String> autocompleteSearchFields2;
	private String autocompleteProjections;
	private String autocompleteProjections2;
	
	private List<String> autocompleteLabelFields;
	
	private String snippetField;

	private static final Logger logger = LoggerFactory.getLogger(Index.class);
	
	private static Index INSTANCE = new Index();
	
	public static Index getInstance() {
		return INSTANCE;
	}
	
	private Index() {
		readPropertyFile();
		renewProxy();
		createProjections();
	}

	static String createProjectionString(final List<String> projectionFields) {
		
		Predicate<String> predicate = new Predicate<String>() {
		    @Override
		    public boolean apply(String input) {
		    	return !(input.equalsIgnoreCase("rank") || input.equalsIgnoreCase("ObjectID"));
		    }
		};
		
		List<String> fields = Lists.newArrayList(Iterables.filter(projectionFields, predicate));
		
		return Joiner.on(" ").join(fields);
	}
	
	private void createProjections() {
		
		this.projections = createProjectionString(this.projectedFields);
		
		this.autocompleteProjections = createProjectionString(this.autocompleteProjectedFields);
		
		this.autocompleteProjections2 = createProjectionString(this.autocompleteProjectedFields2);
		
	}

	private void readPropertyFile() {
		
		try {
			final Properties prop = new Properties();
			try (InputStream is = Resources.getResource("index.properties").openStream()) {
				prop.load(is);
			} catch (Exception e) {
				throw new IllegalArgumentException("could not load property file  : " + Constants.PROPERTIES_FILE);
			}
			// load a properties file
			this.scope = prop.getProperty("scope");
			
			this.cachedFilepath = prop.getProperty(CACHED_FILEPATH_PROP);
			
			this.collectionID = prop.getProperty("collectionID");
			
			this.searchFields =  Splitter.on(",").trimResults().splitToList(prop.getProperty("searchFields"));
			
			this.snippetField = prop.getProperty("snippetField");
			
			this.projectedFields = Splitter.on(",").trimResults().splitToList(prop.getProperty("projectedFields"));
			
			this.autocompleteCollectionID = prop.getProperty("autocompleteCollectionID");
			
			this.autocompleteSearchFields = Splitter.on(",").trimResults().splitToList(prop.getProperty("autocompleteSearchFields"));
			
			this.autocompleteSearchFields2 = Splitter.on(",").trimResults().splitToList(prop.getProperty("autocompleteSearchFields2"));

			this.autocompleteProjectedFields = Splitter.on(",").trimResults().splitToList(prop.getProperty("autocompleteProjectedFields"));
			
			this.autocompleteProjectedFields2 = Splitter.on(",").trimResults().splitToList(prop.getProperty("autocompleteProjectedFields2"));
			
			this.autocompleteLabelFields = Splitter.on(",").trimResults().splitToList(prop.getProperty("autocompleteLabelFields"));

		} catch (Exception e) {
			logger.error("error while reading property file");
		}
	}

	
	
	private synchronized void renewProxy() {
		try {
			this.indexClient = new IndexClient.Builder()
				.scope(this.scope)
				.build();
		} catch (Exception e) {
			logger.error("Error while renewing the client");
		}
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response query(
                @QueryParam("query") String query, 
                @QueryParam("count") @DefaultValue("10") Integer count,
                @QueryParam("from") @DefaultValue("0") Integer from, 
                @QueryParam("snippet") @DefaultValue("false") Boolean snippet, 
                @QueryParam("pretty") @DefaultValue("false") Boolean pretty, 
                @QueryParam("overridenSearchFields") List<String> overridenSearchFields, 
                @QueryParam("concept_filter") List<String> conceptFilter){
		
		String msg = null;
		Response.Status status = null;
		try {
			msg = queryAPI(query, count, from, snippet, pretty, overridenSearchFields, conceptFilter);
			status = Response.Status.OK; 
		} catch (BadRequestException e) {
			logger.warn("error while querying : " + query, e);
			
			msg = JSONConverter.convertToJSON("msg",
					"query " + query + " not valid");
			status = Response.Status.BAD_REQUEST;
		} catch (InternalServerErrorException e) {
			logger.warn("error while querying : " + query, e);
			
			msg = "";
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		
		return Response.status(status).entity(msg).build();
	} 
	
	
	public String queryAPI(String query, Integer count,  Integer from, Boolean snippet, Boolean pretty, List<String> overridenSearchFields, List<String> conceptFilter) throws BadRequestException, InternalServerErrorException {

		long starttime = System.currentTimeMillis();
		String fullQuery = constructFullQueryForQuery(query, this.collectionID, this.projections, this.snippetField, overridenSearchFields, this.searchFields, count, snippet);
		long endtime = System.currentTimeMillis();
		
		logger.info("query construction time : " + (endtime - starttime)
				/ 1000.0 + " secs");
		
		String msg = null;
		
		logger.info("full query : " + fullQuery);
		
		starttime = System.currentTimeMillis();
		
		List<Map<String, String>> records = null;
		try {
			records = this.indexClient.queryAndReadClientSide(fullQuery, null, from, count, false);
			logger.info("num of records retrieved : " + records.size());
		} catch (Exception e) {
			logger.error("error while querying for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		
		endtime = System.currentTimeMillis();
		logger.info("time to query : " + (endtime - starttime)
				/ 1000.0 + " secs");

		if (records.size() == 0) {
			logger.info("no records found");
			renewProxy();

			throw new InternalServerErrorException("no records found");
		}

		starttime = System.currentTimeMillis();
		try {
			msg = postProcess(query, records, count, pretty, snippet, conceptFilter);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("error while post processing the results for : " + fullQuery, e);
			renewProxy();
			
			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		
		logger.info("post process time : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	
	@GET
	@Path("/getDoc")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response getDoc(@QueryParam("doc_uri") String doc_uri, @QueryParam("pretty") @DefaultValue("false") Boolean pretty){
		String msg = null;
		Response.Status status = null;
		try {
			msg = getDocAPI(doc_uri, pretty);
			status = Response.Status.OK; 
		} catch (BadRequestException e) {
			logger.warn("error while getDoc : " + doc_uri, e);
			
			msg = JSONConverter.convertToJSON("msg",
					"doc_uri " + doc_uri + " not valid");
			status = Response.Status.BAD_REQUEST;
		} catch (InternalServerErrorException e) {
			logger.warn("error while getDoc : " + doc_uri, e);
			
			msg = "";
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
		
	}
	
	
	
	
	@GET
	@Path("/getDocs")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response getDocs(
			@QueryParam("doc_uris") List<String> doc_uris, 
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty,
			@QueryParam("count") @DefaultValue("10") Integer count, 
			@QueryParam("from") @DefaultValue("0") Integer from){
		
		String msg = null;
		Response.Status status = null;
		try {
			msg = getDocsAPI(doc_uris, pretty, count, from);
			status = Response.Status.OK; 
		} catch (InternalServerErrorException e) {
			logger.warn("error while getDocs : " + doc_uris, e);
			
			msg = "";
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}
	
	
	
	public String getDocsAPI(List<String> doc_uris, Boolean pretty, Integer count, Integer from) throws InternalServerErrorException {
		
		if (checkIfConceptFilterIsEmpty(doc_uris)){
			logger.info("doc_uris list is null or empty");
			return JSONConverter.convertToJSON(new ArrayList<String>(), pretty);
		}
		
		long starttime = System.currentTimeMillis();
		String fullQuery = constructFullQueryForGetDocs(doc_uris, this.collectionID, this.projections);
		long endtime = System.currentTimeMillis();
		
		logger.info("full query : " + fullQuery);
		String msg = null;
		List<Map<String, String>> records = null;
		
		try {
			records = this.indexClient.queryAndReadClientSide(fullQuery, null, from, count, false);
			logger.info("records found : " + records.size());
		} catch (Exception e) {
			logger.error("error while querying for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		
		endtime = System.currentTimeMillis();
		logger.info("time to query : " + (endtime - starttime)
				/ 1000.0 + " secs");

		if (records.size() == 0) {
			logger.info("no records found");
			renewProxy();

			throw new InternalServerErrorException("no records found");
		}

		starttime = System.currentTimeMillis();
		try {
			msg = postProcess("", records, count, pretty, false, null);
		} catch (Exception e) {
			logger.error("error while postprocessing the results for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		
		logger.info("post process time : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	
	@GET
	@Path("/autocomplete")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response autocomplete(
			@QueryParam("query") String query, 
			@QueryParam("lang") String lang, 
			@QueryParam("count") @DefaultValue("10") Integer count, 
			@QueryParam("from") @DefaultValue("0") Integer from, 
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty){
		
		String msg = null;
		Response.Status status = null;
		try {
			msg = autocompleteAPI(query, lang, count, from, pretty);
			status = Response.Status.OK; 
		} catch (BadRequestException e) {
			logger.warn("error while autocomplete : " + query, e);
			
			msg = JSONConverter.convertToJSON("msg",
					"query " + query + " not valid");
			status = Response.Status.BAD_REQUEST;
		} catch (InternalServerErrorException e) {
			logger.warn("error while autocomplete : " + query, e);
			
			msg = "";
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
		
	}
	
	public String autocompleteAPI(String query, String lang, Integer count, Integer from, Boolean pretty) throws BadRequestException, InternalServerErrorException {

		String msg = null;
		
		long starttime = System.currentTimeMillis();
		String fullQuery = constructFullQueryForAutocomplete(query, this.autocompleteCollectionID, lang, this.autocompleteSearchFields, this.autocompleteProjections);
		long endtime = System.currentTimeMillis();
		
		//fullQuery = "((gDocCollectionID == \"faoAutocompleteCollection\") and ((label = atlantic*) and (label = cod*))) project gDocCollectionID gDocCollectionLang label type";
		
		logger.info("full query : " + fullQuery);
		List<Map<String, String>> records = null;
		
		try {
			records = this.indexClient.queryAndReadClientSide(fullQuery, null, from, count, false);
			logger.info("records found : " + records.size());
		} catch (Exception e) {
			logger.error("error while querying for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		logger.info("time to query : " + (endtime - starttime)
				/ 1000.0 + " secs");

		if (records.size() == 0) {
			logger.info("no records found");
			renewProxy();

			throw new InternalServerErrorException("no records found");
		}

		starttime = System.currentTimeMillis();
		try {
			msg = postProcessAutocomplete(query, records, count, pretty);
		} catch (Exception e) {
			logger.error("error while post processing the autocomplete for : " + fullQuery, e);
			renewProxy();
			
			throw new InternalServerErrorException(e);
		}
		
		endtime = System.currentTimeMillis();
		
		logger.info("post process time : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	@GET
	@Path("/autocomplete_title")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response autocompleteTitle(
			@QueryParam("query") String query, 
			@QueryParam("lang") String lang, 
			@QueryParam("count") @DefaultValue("10") Integer count, 
			@QueryParam("from") @DefaultValue("0") Integer from, 
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty){
		String msg = null;
		Response.Status status = null;
		try {
			msg = autocompleteTitleAPI(query, lang, count, from, pretty);
			status = Response.Status.OK;
		} catch (BadRequestException e) {
			logger.warn("error while autocompleteTitle : " + query, e);
			
			msg = JSONConverter.convertToJSON("msg",
					"query " + query + " not valid");
			status = Response.Status.BAD_REQUEST;
		} catch (InternalServerErrorException e) {
			logger.warn("error while autocompleteTitle : " + query, e);
			
			msg = "";
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).entity(msg).build();
	}
	
	public String autocompleteTitleAPI(String query, String lang, Integer count, Integer from, Boolean pretty) throws BadRequestException, InternalServerErrorException {

		long starttime = System.currentTimeMillis();
		String fullQuery = constructFullQueryForAutocomplete(query, this.autocompleteCollectionID, lang, this.autocompleteSearchFields2, this.autocompleteProjections2);
		long endtime = System.currentTimeMillis();
		
		
		//fullQuery = "((gDocCollectionID == \"faoAutocompleteCollection\") and ((label = atlantic*) and (label = cod*))) project gDocCollectionID gDocCollectionLang label type";
		
		logger.info("full query : " + fullQuery);
		List<Map<String, String>> records = null;
		
		String msg = null;
		
		try {
			records = this.indexClient.queryAndReadClientSide(fullQuery, null, from ,count, false);
			logger.info("record found : " + records.size());
		} catch (Exception e) {
			logger.error("error while querying for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		logger.info("time to query : " + (endtime - starttime)
				/ 1000.0 + " secs");

		if (records.size() == 0) {
			logger.info("no records found");
			renewProxy();

			throw new InternalServerErrorException("no records found");
		}

		starttime = System.currentTimeMillis();
		try {
			msg = postProcessAutocompleteTitle(query, records, count, pretty);
		} catch (Exception e) {
			logger.error("error while postprocessing autocomplete title for : " + fullQuery, e);
			renewProxy();

			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		
		logger.info("post process time : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}

	private String postProcess(List<Map<String, String>> records, Boolean pretty) {
		return postProcess("", records, -1, pretty, false, null); 
	}
	
	private String postProcess(String qterm, List<Map<String, String>> records, Integer count, Boolean pretty, Boolean includeSnippet, List<String> conceptFilter) {

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		//List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		
		logger.info("conceptFilter      : " + conceptFilter);
		if (conceptFilter != null)
			logger.info("conceptFilter size : " + conceptFilter.size());
		
		List<SearchResponse> results = new ArrayList<SearchResponse>();

		starttime = System.currentTimeMillis();
		
		int cnt = 0;
		for (Map<String, String> record : records){
			if (cnt++>=count)
				break;
			//while (stream.hasNext() && (cnt++<count || count != -1)) {
			try {
				SearchResponse sr = new SearchResponse();
				sr.qterm = qterm;
				sr.score = Double.valueOf(record.get("rank"));
				sr.uri = record.get("ObjectID");
				
				sr.uri = StringEscapeUtils.unescapeXml(sr.uri);
				
//				sr.doc_uri = ((StringField) rec.getField("doc_uri")).getPayload();
//				sr.doc_uri = escapeResults(sr.doc_uri);
				
				String provenance = record.get("provenance");
				provenance = escapeResults(provenance);
				provenance = provenance.toLowerCase();
				
				
				if (provenance.equalsIgnoreCase("StatBase")){
					
					sr.seealso = sr.uri;
					
					sr.prov.put("label", "statbase");
					sr.prov.put("uri", "http://smartfish.collection/statbase");
					
					
				} else if (provenance.equalsIgnoreCase("WIOFish")){
					String[] parts = sr.uri.split("/");
					String id = null;
					if (parts != null && parts.length > 0)
						id = parts[parts.length - 1];
					
					sr.seealso = cachedFilepath + "wiofish/" + id + ".html";
					
					sr.prov.put("label", "wiofish");
					sr.prov.put("uri", "http://smartfish.collection/wiofish");
					
					sr.uri = "http://smartfish.collection/wiofish/" + id;
					
				} else if (provenance.equalsIgnoreCase("FIRMS")){
					String[] parts = sr.uri.split("/");
					String id = null;
					if (parts != null && parts.length > 0)
						id = parts[parts.length - 1];
					sr.seealso = cachedFilepath + "firms/" + id + ".xml";
					
					sr.prov.put("label", "firms");
					sr.prov.put("uri", "http://smartfish.collection/firms");
					
					sr.uri = "http://smartfish.collection/firms/" + id;
					
				} else {
					logger.info("NO provenance found for : " + provenance);
				}
				
				//sr.uri = sr.seealso;
				
				
				sr.country = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY));
				
				sr.gear = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR));

				sr.vessel = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL));
				
				sr.management = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT));
				
				sr.status = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS));
				
				sr.access_control = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL));
				
				sr.fishing_control = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL));
				
				sr.enforcement_method = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD));
				
				sr.sector = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.SECTOR));
				
				sr.other_income_source = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.OTHER_INCOME_SOURCE));
				
				sr.market = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.MARKET));
				
				sr.year = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR));

                                sr.statistics = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.STATISTICS));
				
				sr.post_processing_method = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.POST_PROCESSING_METHOD));
				
				sr.management_indicator = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT_INDICATOR));
				
				sr.finance_mgmt_authority = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.FINANCE_MGT_AUTHORITY));
				
				sr.species = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES));
				
				sr.bycatch = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.BYCATCH));
				
				sr.target = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.TARGET));
				
				sr.thretened = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.THRETENED));
				
				sr.discard = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.DISCARD));
				
				sr.seasonality = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.SEASONALITY));
				
				sr.decision_maker = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.DECISION_MAKER));
				
				sr.owner_of_access_right = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.OWNER_OF_ACCESS_RIGHT));
				
				sr.applicant_for_access_right = getRecordValue(conceptFilter, record, ANNOTATIONS.getLocalName(ANNOTATIONS.APPLICANT_FOR_ACCESS_RIGHT));
				
					if (!Strings.isNullOrEmpty(record.get("technology_used"))){
	
					String technology = record.get("technology_used");
					technology = escapeResults(technology);
					List<String> techList = Splitter.on(",").trimResults().splitToList(technology);
					
					if (techList.size() > 0){
						sr.technology = techList;
					}
				}
//				if (species.split(",").length > 0){
//					sr.species = Arrays.asList(species.split("\\s*,\\s*"));
//				}
				
				Title docTitle = new Title();
				docTitle.title = record.get("title");
				docTitle.title = escapeResults(docTitle.title);
				
				sr.titles = Arrays.asList(docTitle);
				
				if (includeSnippet) {
					//logger.info("snippets are included");
					
					List<String> snippets = Lists.newArrayList(Splitter.on("...").split(record.get(this.snippetField)));
					
					sr.snippets = new ArrayList<Snippet>();
					for (String snippetText : snippets)
						sr.snippets.add(new Snippet(escapeResults(snippetText.trim()).trim()));
					if (sr.snippets.size() == 0)
						sr.snippets = null;
				}
				results.add(sr);
			} catch (NullPointerException e) {
				logger.error("error while postprocessing the record : " + record);
				logger.error("", e);
			}
			
			
//			for (String fieldName : projectedFields) {
//				try {
//					StringField f = (StringField) rec.getField(fieldName);
//					if (f == null)
//						continue;
//					String val = f.getPayload();
//					r.put(fieldName, val);
//				} catch (GRS2RecordDefinitionException e) {
//					e.printStackTrace();
//				} catch (GRS2BufferException e) {
//					e.printStackTrace();
//				}
//
//			}
//			
//			if (includeSnippet) {
//				try {
//					StringField f = (StringField) rec.getField(this.snippetField);
//					if (f == null)
//						continue;
//					String val = f.getPayload();
//					r.put(this.snippetField, val);
//				} catch (GRS2RecordDefinitionException e) {
//					e.printStackTrace();
//				} catch (GRS2BufferException e) {
//					e.printStackTrace();
//				}
//			}
//
//			results.add(r);
		}
		endtime = System.currentTimeMillis();
		logger.info("time to read records : " + (endtime - starttime)
				/ 1000.0 + " secs");

		starttime = System.currentTimeMillis();
		String msg = JSONConverter.convertToJSON(results, pretty);
		endtime = System.currentTimeMillis();
		logger.info("time to conver to json : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}

	private static boolean checkIfConceptFilterIsEmpty(List<String> conceptFilter) {
		return conceptFilter == null || conceptFilter.size() == 0 || (conceptFilter.size() == 1 && Strings.isNullOrEmpty(conceptFilter.get(0)));
	}
	
	
	private String postProcessAutocomplete(String qterm, List<Map<String, String>> records, Integer count, Boolean pretty) {

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		//List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		List<AutocompleteResponse> results = new ArrayList<AutocompleteResponse>();

		starttime = System.currentTimeMillis();
		
		int cnt = 0;
		for (Map<String, String> record : records) {
			if (cnt++>=count)
				break;
			try {
				AutocompleteResponse ar = new AutocompleteResponse();
				ar.qterm = qterm;
				ar.score = Double.valueOf(record.get("rank"));
				ar.uri = escapeResults(record.get("ObjectID"));
				ar.doc_uri = escapeResults(record.get("doc_uri"));
				
				
				List<String> fields = new ArrayList<String>();
				
//				for (Field f : rec.getFields())
//					logger.info("field name : " + f.getFieldDefinition().getName());
				
				for (String labelField : this.autocompleteLabelFields){
					String payload = escapeResults(record.get(labelField));
					fields.add(payload);
				}
				
				logger.info("autocomplete returned : " + fields);
				
				/*
				StringBuffer label = new StringBuffer();
				for (String term : qterm.split("\\s+")){
					String matchedField = null;
					
					
					for (String field : fields){
						
						if (field.contains(",")){
							for (String speciesName : field.split("\\s*,\\s*")){
								if (speciesName.toLowerCase().startsWith(term.toLowerCase())){
									label.append(speciesName);
									label.append(" ");
									matchedField = field;
									break;
								}
							}
							
							if (matchedField == null)
								for (String speciesName : field.split("\\s*,\\s*")){
									if (speciesName.contains(term.toLowerCase())){
										label.append(speciesName);
										label.append(" ");
										matchedField = field;
										break;
									}
								}
						}
						
						if (matchedField != null)
							break;
						
						
						//logger.info("testing if " + term + " is " + field);
						
						if (field.toLowerCase().startsWith(term.toLowerCase())){
							label.append(field);
							label.append(" ");
							matchedField = field;
							break;
						}
					}
					if (matchedField == null)
						for (String field : fields){
							//logger.info("testing if " + term + " is " + field);
							
							if (field.toLowerCase().contains(term.toLowerCase())){
								label.append(field);
								label.append(" ");
								break;
							}
						}
					
					logger.info("added : " + matchedField + " in the first round");
					fields.remove(matchedField);
				}
	
				for (String field : fields){
					for (String term : qterm.split("\\s+")){
						logger.info("checking field : " + field + " with term : " + term);
						if (field.toLowerCase().contains(term.toLowerCase())){
							label.append(field);
							label.append(" ");
							break;
						}
					}
				}
				
				
				//ar.label = ((StringField) rec.getField("label")).getPayload();//label
				
				ar.label = label.toString().trim();
				//ar.label = escapeResults(ar.label);
				//ar.type = ((StringField) rec.getField("type")).getPayload();//label
				*/
				ar.label = getMatchedLabel(fields, qterm.split("\\s+"));
				results.add(ar);
			} catch (Exception e) {
				logger.error("error while postprocessing the record : " + record);
			}
			
			
		}
		endtime = System.currentTimeMillis();
		logger.info("time to read records : " + (endtime - starttime)
				/ 1000.0 + " secs");

		starttime = System.currentTimeMillis();
		String msg = JSONConverter.convertToJSON(results, pretty);
		endtime = System.currentTimeMillis();
		logger.info("time to conver to json : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	private String getMatchedLabel(List<String> fields, String[] terms){
		return getMatchedLabel(fields, Arrays.asList(terms));
	}
	
	
	private String getMatchedLabel(List<String> fields, List<String> terms){
		logger.info("matching fields : " + fields);
		return StringUtils.join(getMatchedList(fields, terms), " ");
	}
	
	private List<String> getMatchedList(List<String> fields, List<String> terms){
		//contained at start
		List<String> matchedList = new ArrayList<String>();
		for (String field : fields){
			String matched = matchField(field, terms, true);
			logger.info("matched field for : " + field + " : " + matched);
			if (matched != null)
				matchedList.add(matched);
		}
		
		//contained anywhere
		List<String> restFields = new ArrayList<String>(fields);
		restFields.removeAll(matchedList);
		for (String field : restFields){
			String matched = matchField(field, terms, false);
			logger.info("matched field for : " + field + " : " + matched);
			if (matched != null)
				matchedList.add(matched);
		}
		
		return matchedList;
	}
	
	private String matchField(String field, List<String> terms, boolean onlyStart){
		if (field.contains(",")){
			return matchMultiValueField(field, terms, onlyStart);
		} else {
			return matchSingleValueField(field, terms, onlyStart);
		}
	}
	
	private String matchMultiValueField(String multiValueField, List<String> terms, boolean onlyStart){
		for (String term : terms){
			for (String field : Splitter.on(",").trimResults().omitEmptyStrings().splitToList(multiValueField)){
				if (onlyStart){
					if (field.toLowerCase().startsWith(term.toLowerCase())){
						return field;
					}
				}
				else {
					if (field.toLowerCase().contains(term.toLowerCase())){
						return field;
					}
				}
			}
		}
		return null;
	}
	
	private String matchSingleValueField(String field, List<String> terms, boolean onlyStart){
		for (String term : terms){
			if (onlyStart){
				if (field.toLowerCase().startsWith(term.toLowerCase())){
					return field;
				}
			}
			else {
				if (field.toLowerCase().contains(term.toLowerCase())){
					return field;
				}
			}
		}
		return null;
	}
	
	private String postProcessAutocompleteTitle(String qterm, List<Map<String, String>> records, Integer count, Boolean pretty) {

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		logger.info("time to init stream : " + (endtime - starttime)
				/ 1000.0 + " secs");

		//List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		List<AutocompleteResponse> results = new ArrayList<AutocompleteResponse>();

		starttime = System.currentTimeMillis();
		
		int cnt = 0;
		for (Map<String, String> record : records) {
			if (cnt++>=count)
				break;
			try {
				AutocompleteResponse ar = new AutocompleteResponse();
				ar.qterm = qterm;
				ar.score = Double.valueOf(record.get("rank"));
				ar.uri = escapeResults(record.get("ObjectID"));
				
				ar.doc_uri = escapeResults(record.get(("doc_uri")));
				
				//ar.label = ((StringField) rec.getField("label")).getPayload();//label
				
				ar.label = escapeResults(record.get(("title")));
				//ar.label = escapeResults(ar.label);
				//ar.type = ((StringField) rec.getField("type")).getPayload();//label
				
				results.add(ar);
			} catch (Exception e) {
				logger.error("error while postprocessing the record : " + record);
			}
			
			
		}
		endtime = System.currentTimeMillis();
		logger.info("time to read records : " + (endtime - starttime)
				/ 1000.0 + " secs");

		starttime = System.currentTimeMillis();
		String msg = JSONConverter.convertToJSON(results, pretty);
		endtime = System.currentTimeMillis();
		logger.info("time to convert to json : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	
	public String getDocAPI(String doc_uri, Boolean pretty) throws InternalServerErrorException, BadRequestException {
		long starttime = System.currentTimeMillis();
		String fullQuery = constructFullQueryForGetDoc(doc_uri, this.collectionID, this.projections);
		long endtime = System.currentTimeMillis();
		
		String msg = null;
		
		logger.info("full query : " + fullQuery);
		List<Map<String, String>> records = null;
		
		try {
			records = this.indexClient.queryAndReadClientSide(fullQuery, null, false);
			logger.info("records found : " + records.size());
		} catch (Exception e) {
			logger.error("error while querying for : " + fullQuery, e);
			renewProxy();
			throw new InternalServerErrorException(e);
		}
		
		endtime = System.currentTimeMillis();
		logger.info("time to query : " + (endtime - starttime)
				/ 1000.0 + " secs");

		if (records.size() == 0) {
			logger.info("no records found");
			renewProxy();

			throw new InternalServerErrorException("no records found");
		}

		starttime = System.currentTimeMillis();
		try {
			msg = postProcess(records, pretty);
		} catch (Exception e) {
			logger.error("error while postprocessing the results for : " + fullQuery, e);
			renewProxy();
			
			throw new InternalServerErrorException(e);
		}
		endtime = System.currentTimeMillis();
		
		logger.info("post process time : " + (endtime - starttime)
				/ 1000.0 + " secs");

		return msg;
	}
	
	
	private static String constructFullQueryForQuery(String query, String collectionID, String projectionsString, String snippetField, List<String> overridenSearchFields, List<String> searchFields, Integer count, Boolean snippet) throws BadRequestException{
		logger.info("received query : " + query);
		logger.info("count : " + count);
		query = sanitizeQuery(query);
		logger.info("sanitized query : " + query);
		
		if (query.contains("*") || query.contains("+") || query.contains(".") /*|| query.split("\\s+").length > 1*/) {
			throw new BadRequestException();
		}

		boolean exactQuery = query.startsWith("\"") &&  query.endsWith("\"");
		List<String> fieldsToBeSearched = null;
		if (overridenSearchFields != null && overridenSearchFields.size() > 0){
			fieldsToBeSearched = overridenSearchFields;
			logger.info("Using overridenSearchFields");
		} else {
			fieldsToBeSearched = searchFields;
			logger.info("Using default searchFields");
		}
		
		logger.info("fieldsToBeSearched : " + fieldsToBeSearched);
		
		List<String> terms = null;
		
		if (exactQuery) {
			terms = Lists.newArrayList(
					query
					//query.replace("&quot;", "\"")
					);
		} else {
			terms = Splitter.on(CharMatcher.WHITESPACE).trimResults().splitToList(query);
		}
		
		List<String> relations = new ArrayList<String>();
		for (String term : terms) {
			for (String field : fieldsToBeSearched){
				relations.add(field + " = " + term);
			}
		}
		
		StringBuffer strBuf = new StringBuffer();
		strBuf
			.append("gDocCollectionID == ").append(collectionID)
			.append(" AND ")
			.append("(")
				.append(Joiner.on(" OR ").join(relations))
			.append(")")
			.append(" project ")
				.append(projectionsString);
		
		if (snippet)
			strBuf.append(" ").append(snippetField);
		
		return strBuf.toString();
	}
	
	private static String constructFullQueryForGetDocs(List<String> doc_uris, String collectionID, String projectionsStr){
		List<String> relations = new ArrayList<String>();
		for (String docUri : doc_uris){
			relations.add("ObjectID = " + "\"" +  docUri + "\"");
		}
		
		StringBuffer strBuf = new StringBuffer();
		strBuf
			.append("gDocCollectionID == ").append(collectionID)
			.append(" AND ")
			.append("(")
				.append(Joiner.on(" OR ").join(relations))
			.append(")")
			.append(" project ")
				.append(projectionsStr);
		
		return strBuf.toString();
	}
	
	private static String constructFullQueryForAutocomplete(String query, String collectionID, String lang, List<String> autocompleteSearchFields, String autocompleteProjectionStr) throws BadRequestException {
		logger.info("received query : " + query);
		query = sanitizeQuery(query);
		logger.info("sanitized query : " + query);
		
		if (query.contains("*") || query.contains("+") || query.contains(".") /*|| query.split("\\s+").length > 1*/) {
			throw new BadRequestException();
		}

		List<String> terms = Splitter.on(CharMatcher.WHITESPACE).trimResults().splitToList(query);
		
		List<String> relations = new ArrayList<String>();
		for (String term : terms) {
			for (String field : autocompleteSearchFields){
				relations.add(field + " = " + term);
			}
		}
		
		StringBuffer strBuf = new StringBuffer();
		strBuf
			.append("(")
			.append("gDocCollectionID == ").append(collectionID);
		
		if (lang != null){
			strBuf
				.append(" AND ")
				.append("gDocCollectionLang == ").append("\"").append(lang).append("\"")
				.append(")");
		}
			
		strBuf
			.append(" AND ")
			.append("(")
				.append(Joiner.on(" OR ").join(relations))
			.append(")")
			.append(" project ")
				.append(autocompleteProjectionStr);
		
		
		return strBuf.toString();
		
	}
	
	private static String constructFullQueryForGetDoc(String doc_uri, String collectionID, String projectionsStr) throws BadRequestException {
		logger.info("received doc_uri : " + doc_uri);
		doc_uri = sanitizeQuery(doc_uri);
		logger.info("sanitized doc_uri : " + doc_uri);
		
		if (doc_uri.contains("*") || doc_uri.contains("+") || doc_uri.contains(".") /*|| query.split("\\s+").length > 1*/) {
			throw new BadRequestException();
		}
		
		StringBuffer strBuf = new StringBuffer();
		
		
		strBuf
			.append("gDocCollectionID == ").append(collectionID)
			.append(" AND ")
			.append("ObjectID = ").append("\"").append(doc_uri).append("\"")
			.append(" project ").append(projectionsStr);
		
		return strBuf.toString();
	}
	
	public static void main(String[] args) {
		String query = null;
		
		query = "abc or (cdb)";
		
		//System.out.println(sanitizeQuery(query));
		
		
		query = " \"abc or (cdb) and lpo \" paok or (pao) \"()()()\" ";
		System.out.println(sanitizeQuery(query));
		query = "\"asc\"";
		System.out.println(sanitizeQuery(query));
	}
	
	static String sanitizeQuery(final String query){
		
		List<String> q = Splitter.on("\"").trimResults().splitToList(query);
		
		if (q.size() % 2 != 1){
			logger.warn("number of quotes should be even");
			throw new IllegalArgumentException("number of quotes should be even");
		}
		
		List<String> sanitizedQuery = Lists.newArrayList();
		
		String subTerm = null;
		for (int i = 0 ; i != q.size() ; ++i){
			subTerm = q.get(i);
			
			if (i % 2 == 0) // out of quotes
				sanitizedQuery.addAll(sanitizeSubQuery(subTerm));
			else // in quotes
				sanitizedQuery.add("\"" + subTerm + "\"");
		}
		
		return Joiner.on(" ").join(sanitizedQuery);
	}
	
	static List<String> sanitizeSubQuery(final String query) {
		
		//breaks the french queries
		
//		String sanitized = StringEscapeUtils
//				.escapeHtml(query);
		
		String sanitized = query;
		
		String queryWithoutSymbols = CharMatcher
				.anyOf(reservedSymbolsCharset)
				.replaceFrom(sanitized, "");
		
		List<String> terms = Splitter.on(CharMatcher.WHITESPACE)
				.omitEmptyStrings()
				.trimResults()
				.splitToList(queryWithoutSymbols);
		
		List<String> sanitizedTerms = Lists.newArrayList();
		
		for (String term : terms){
			if (reservedKeywords.contains(term.toLowerCase()))
				sanitizedTerms.add("\"" + term + "\"");
			else
				sanitizedTerms.add(term);
		}
				
		return sanitizedTerms;
	}
	
	static List<String> sanitizeSubQuery2(final String query) {
		
		String sanitized = StringEscapeUtils
				.escapeHtml(query);
		
		String queryWithoutSymbols = CharMatcher
				.anyOf(reservedSymbolsCharset)
				.replaceFrom(sanitized, "");
		
		List<String> terms = Splitter.on(CharMatcher.WHITESPACE)
				.omitEmptyStrings()
				.trimResults()
				.splitToList(queryWithoutSymbols);
		
		return terms;
	}
	
	static String escapeResults(String result){
		return StringEscapeUtils.unescapeXml(StringEscapeUtils.unescapeHtml(result));
	}
	
	
	
	final static Set<String> reservedKeywords = Sets.newHashSet(
			"and",
			"or",
			"not",
			"prox",
			"fuse",
			"sortby",
			"project"
			);
	
	final static List<String> reservedSymbols = Lists.newArrayList(
			",",
			".",
			"-",
			"&",
			")",
			"(",
			"]",
			"[",
			"=",
			"==",
			">",
			"<",
			"<=",
			">=",
			"<>",
			"/"
			);
	
	final static  CharSequence reservedSymbolsCharset = Joiner.on("").join(reservedSymbols);
	
	private static List<Map<String, String>> getRecordValue(List<String> conceptFilter, Map<String, String> record, String fieldName){
		List<Map<String, String>> resp = null;
		
		if (checkIfConceptFilterIsEmpty(conceptFilter) || conceptFilter.contains(fieldName)){
			String value = record.get(fieldName + "_uris");
			value = escapeResults(value);
			try {
				resp = gson.fromJson(value, new TypeToken<List<Map<String, String>>>(){}.getType());

				return resp;
				
			} catch (Exception e) {
				logger.info("error parsing country : " + value);
			}
		}
		return null;
	}
	
}

