package org.gcube.search;

import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.search.commons.SearchServiceAPI;
import org.gcube.searchsystem.cache.PlanCache;
import org.gcube.searchsystem.cache.PlanCacheManager;
import org.gcube.searchsystem.environmentadaptor.ResourceRegistryAdapter;
import org.gcube.searchsystem.planning.Orchestrator;
import org.gcube.searchsystem.workflow.PE2ngWorkflowAdaptor;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class SearchSystemService implements SearchServiceAPI {

	private static final Logger logger = LoggerFactory
			.getLogger(SearchSystemService.class);
	// get cache for this scope
	PlanCache pCache = null;

	ResourceRegistryAdapter rradapter = null;
	EnvHintCollection adaptorHints = null;
	String scope = null;

	public SearchSystemService() throws Exception {
	}

	boolean initialized = false;

	synchronized void initialize() throws Exception {

		ServiceContext sc = new ServiceContext();

		if (!initialized) {
			scope = sc.getScope();

			pCache = PlanCacheManager.getCacheWithName(scope);
			adaptorHints = sc.getHints();
			adaptorHints.AddHint(new NamedEnvHint("GCubeActionScope",
					new EnvHint(scope)));

			rradapter = new ResourceRegistryAdapter(adaptorHints);
			initialized = true;
		}
	}
	
	public Response ping(){
		return Response.ok().entity("pong").build();
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response search(@HeaderParam("gcube-scope") String scope,
			@QueryParam("query") String query,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty,
			@QueryParam("names") @DefaultValue("false") Boolean names) {
		logger.info("query : " + query);

		if (query.startsWith("\"") && query.endsWith("\"")) {
			query = query.substring(1, query.length() - 1);
		}

		try {
			long starttime = System.currentTimeMillis();

			PE2ngWorkflowAdaptor workflowadaptor = null;
			try {
				workflowadaptor = new PE2ngWorkflowAdaptor(adaptorHints);
			} catch (Exception e) {
				logger.error("error initializing workflow adaptor:", e);

				String msg = JSONConverter.convertToJSON("msg",
						"error initializing workflow adaptor");
				Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
				return Response.status(status).entity(msg).build();
			}

			Orchestrator orchestrator = new Orchestrator();

			// get the rsEpr
			String rsEpr = orchestrator.search(query, rradapter,
					workflowadaptor, pCache);

			long endtime = System.currentTimeMillis();

			logger.info("Search completed after : " + (endtime - starttime)
					+ " ms. grs : " + rsEpr);

			logger.info("Search completed after : " + (endtime - starttime)
					+ " ms. grs : " + rsEpr);

			// get the warnings list
			ArrayList<String> warnings = orchestrator.getWarnings();
			if (warnings.size() > 0) {
				String msg = JSONConverter.convertToJSON("msg",
						warnings.toString());
				Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
				return Response.status(status).entity(msg).build();
			}

			String msg = null;

			if (all) {
				logger.info("reading records...");
				msg = readResults(rsEpr, pretty, names, fieldNameMap);
				logger.info("reading records...OK");
			} else {
				msg = JSONConverter.convertToJSON("grslocator", rsEpr);
			}
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(msg).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error while searching :", e);

			String msg = JSONConverter.convertToJSON("msg",
					"error while searching");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}

	@GET
	@Path("/searchSec")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response searchSec(@HeaderParam("gcube-scope") String scope,
			@QueryParam("query") String query,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty,
			@QueryParam("names") @DefaultValue("false") Boolean names,
			@QueryParam("sids") Set<String> sids) {
		logger.info("query : " + query);
		logger.info("query : " + query);
		if (query.startsWith("\"") && query.endsWith("\"")) {
			query = query.substring(1, query.length() - 1);
		}

		try {
			long starttime = System.currentTimeMillis();

			PE2ngWorkflowAdaptor workflowadaptor = null;
			try {
				workflowadaptor = new PE2ngWorkflowAdaptor(adaptorHints);
			} catch (Exception e) {
				logger.error("error initializing workflow adaptor:", e);

				String msg = JSONConverter.convertToJSON("msg",
						"error initializing workflow adaptor");
				Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
				return Response.status(status).entity(msg).build();
			}

			Orchestrator orchestrator = new Orchestrator();

			orchestrator.setSids(sids);
			// get the rsEpr
			String rsEpr = orchestrator.search(query, rradapter,
					workflowadaptor, pCache);

			long endtime = System.currentTimeMillis();

			logger.info("Search completed after : " + (endtime - starttime)
					+ " ms. grs : " + rsEpr);

			logger.info("Search completed after : " + (endtime - starttime)
					+ " ms. grs : " + rsEpr);

			// get the warnings list
			ArrayList<String> warnings = orchestrator.getWarnings();
			if (warnings.size() > 0) {
				String msg = JSONConverter.convertToJSON("msg",
						warnings.toString());
				Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
				return Response.status(status).entity(msg).build();
			}

			String msg = null;

			if (all) {
				msg = readResults(rsEpr, pretty, names, fieldNameMap);
			} else {
				msg = JSONConverter.convertToJSON("grslocator", rsEpr);
			}
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(msg).build();
		} catch (Exception e) {
			logger.error("error while searching :", e);

			String msg = JSONConverter.convertToJSON("msg",
					"error while searching");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}

	Map<String, String> fieldNameMap = new HashMap<String, String>();

	static String readResults(String rsLocator, Boolean pretty, Boolean names,
			Map<String, String> fieldNameMap) throws Exception {

		logger.info("Opening stream...");
		ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(
				new URI(rsLocator));

		logger.info("Opening stream...OK");

		List<Map<String, String>> results = new ArrayList<Map<String, String>>();

		try {
			Iterator<GenericRecord> it = reader.iterator();
			while (it.hasNext()) {
				logger.info("\tReading record from stream...");
				GenericRecord rec = it.next();
				Map<String, String> mapRec = new HashMap<String, String>();
				for (Field field : rec.getFields()) {
					String fieldID = field.getFieldDefinition().getName();

					String fieldName = null;
					if (names) {
						if (fieldNameMap.containsKey(fieldID))
							fieldName = fieldNameMap.get(fieldID);
						else {
							fieldName = QueryHelper.GetFieldNameById(fieldID);
							if (fieldName == null)
								fieldName = fieldID;
							fieldNameMap.put(fieldID, fieldName);
						}
					} else {
						fieldName = fieldID;
					}

					String fieldValue = ((StringField) field).getPayload();

					mapRec.put(fieldName, fieldValue);
				}
				results.add(mapRec);
			}
			reader.close();
		} catch (Exception e) {
			logger.warn("error while reading results", e);
			reader.close();
			throw e;
		}

		logger.info("converting records to json");
		String msg = JSONConverter.convertToJSON(results, pretty);

		logger.info("msg : " + msg);
		return msg;
	}
	
	
	@GET
	@Path("/collections")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response collections(@HeaderParam(SCOPE_HEADER) String scope){
		
		try {
			
			Map<String, String> collections = rradapter.getAllCollections(this.scope);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(collections).build();
			
			
		} catch (Exception e) {
			logger.warn("error while getting collections", e);
			
			String msg = JSONConverter.convertToJSON("msg",
					"error while getting collections");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	@GET
	@Path("/collectionsTypes")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response collectionsTypes(@HeaderParam(SCOPE_HEADER) String scope){
		
		try {
			
			Map<String, String> collectionsTypes = rradapter.getAllCollectionsTypes(this.scope);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(collectionsTypes).build();
			
			
		} catch (Exception e) {
			String msg = JSONConverter.convertToJSON("msg",
					"error while collectionsTypes");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	@GET
	@Path("/searchableFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response searchableFields(@HeaderParam(SCOPE_HEADER) String scope){
		
		try {
			Map<String, Set<String>> fields = rradapter.getAllSearchableFieldsPerCollection(this.scope);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(fields).build();
			
			
		} catch (Exception e) {
			logger.warn("error getting searchable fields", e);
			
			String msg = JSONConverter.convertToJSON("msg",
					"error getting searchable fields");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	@GET
	@Path("/presentableFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response presentableFields(@HeaderParam(SCOPE_HEADER) String scope){
		
		try {
			Map<String, Set<String>> fields = rradapter.getAllPresentableFieldsPerCollection(this.scope);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(fields).build();
			
			
		} catch (Exception e) {
			logger.warn("error getting presentable fields", e);
			
			String msg = JSONConverter.convertToJSON("msg",
					"error getting presentable fields");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	@GET
	@Path("/fieldsMapping")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response fieldsMapping(@HeaderParam(SCOPE_HEADER) String scope){
		
		try {
			Map<String, String> fieldsMapping = rradapter.getFieldsMapping(this.scope);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(fieldsMapping).build();
			
			
		} catch (Exception e) {
			logger.warn("error getting fields mapping", e);
			String msg = JSONConverter.convertToJSON("msg",
					"error getting fields mapping");
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}

}
