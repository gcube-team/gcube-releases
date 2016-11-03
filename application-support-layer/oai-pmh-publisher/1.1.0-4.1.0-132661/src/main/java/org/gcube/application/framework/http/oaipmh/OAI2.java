package org.gcube.application.framework.http.oaipmh;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.oaipmh.Data.Pair;
import org.gcube.application.framework.http.oaipmh.impl.SearchClientImpl;
import org.gcube.application.framework.oaipmh.Response;
import org.gcube.application.framework.oaipmh.constants.MetadataConstants;
import org.gcube.application.framework.oaipmh.constants.ResponseConstants;
import org.gcube.application.framework.oaipmh.objectmappers.Identifier;
import org.gcube.application.framework.oaipmh.objectmappers.Record;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.Toolbox;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.NoSearchMasterEPRFoundException;
import org.gcube.application.framework.search.library.exception.QuerySyntaxException;
import org.gcube.application.framework.search.library.exception.gRS2AvailableRecordsRetrievalException;
import org.gcube.application.framework.search.library.exception.gRS2BufferException;
import org.gcube.application.framework.search.library.exception.gRS2CreationException;
import org.gcube.application.framework.search.library.exception.gRS2NoRecordReadWithinTimeIntervalException;
import org.gcube.application.framework.search.library.exception.gRS2ReaderException;
import org.gcube.application.framework.search.library.exception.gRS2RecordDefinitionException;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.Criterion;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.SearchASLException;
import org.gcube.application.framework.search.library.util.DisableButtons;
import org.gcube.application.framework.search.library.util.SearchType;
import org.gcube.application.framework.search.library.util.SessionConstants;
import org.gcube.search.exceptions.SearchException;
//import org.gcube.search.client.library.exceptions.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class OAI2
 */
//@WebServlet("/OAI2")
public class OAI2 extends HttpServlet {
	
	private static final String HOST_PROPS_FILENAME = "hosting.properties";
	
	private static final long serialVersionUID = 1L;
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(OAI2.class);
	
	private Repository repository;
	private HashMap<CollectionInfo, ArrayList<CollectionInfo>> gCubeCollections;
	
	private static boolean refreshSet;
	
	private boolean usernameSet = false;
	
	private String url;
	private String email;
	
	///////////////////////////
	//these are filled by parsing the above gCubeCollections hashmap. this can be improved.
	              //    id  ,  Name
	private HashMap <String, String> collections;  //this will serve as the repository "sets" as well...
				 //   col ID,   (id,name) pairs
	private HashMap <String, ArrayList<Pair>> browsableFields;
		 		 //   col ID,   (id,name) pairs
	private HashMap <String, ArrayList<Pair>> presentableFields;
	/////////////////////////////
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OAI2() {
        super();
        refreshSet = false;
        gCubeCollections = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
        collections = new HashMap <String, String>();
        browsableFields = new HashMap <String, ArrayList<Pair>>();
        presentableFields = new HashMap <String, ArrayList<Pair>>();
//        repository = DummyRepository.createDummyTestRepository();
        
        Thread.currentThread().getContextClassLoader().getResource(HOST_PROPS_FILENAME).getPath();
		Properties props = new Properties();
		FileInputStream fip;

		try {
			fip = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(HOST_PROPS_FILENAME).getPath());
			props.load(fip);
			url = new URL((String)props.get("protocol"),(String)props.get("host"),Integer.parseInt((String)props.get("port")),"").toString();
			email = (String)props.get("adminEmail");
		} catch (IOException e2) {
			logger.debug("Could not parse properties file " + HOST_PROPS_FILENAME +" Using empty hostname for repoository replies and empty email fields");
			url = "";
			email = "";
		}
		
    }

    public void setRefreshCollections(int interval, final String username, final String sessionID){
	    new java.util.Timer().schedule( 
	            new java.util.TimerTask() {
	                @Override
	                public void run() {
	                	logger.debug("Running setRefreshCollections");
	                	gCubeCollections = GCubeTools.getGCubeCollections(username, sessionID); 
	                }
	            }, 
	            interval
	    );
    }
    
//    public static void println(HttpServletResponse response, String msg){
//    	try {
//			response.getWriter().write(msg+"\n");
//		} catch (IOException e) {
//		}
//    }
    
     
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("doGet()");

		//the authentication below, if called without having previously provided a scope or username, 
		//defaults to anonymous user on scope provided on aslHTTPScope.config
		//file within the tomcat shared/d4s folder (tomcat-x.x/shared/d4s/)
		AuthenticationResponse authenticationResp = CallAuthenticationManager.authenticateCall(request, "ShowCollectionInfos");
		if (!authenticationResp.isAuthenticated()) {
			response.sendError(401, authenticationResp.getUnauthorizedErrorMessage());
			return;
		}
		
		String username = authenticationResp.getUserId();  //this will always be guest.d4science, as it will access open to anonymous collections
		logger.debug("username of authentication: "+ username);
		HttpSession session = request.getSession();
		ASLSession mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
		
		logger.debug("Username: "+username+" sessionID: "+session.getId()+" mysession.getScope(): "+mysession.getScope() );
		
		//Getting collections after first time is limited to the cached version of ASL.
		//It's also very fast, so no need to create periodic, thread-safe functions to update asynchronously.
		
//		if(!refreshSet){
			logger.debug("Getting collections");
//			gCubeCollections = GCubeTools.getGCubeCollections(username, session.getId()); //get them the first time
			gCubeCollections = GCubeTools.getGCubeCollections(mysession); //alternative
//			setRefreshCollections(900000, username, request.getSession().getId()); //run every 15 minutes (900000ms) 
//			refreshSet = true;
//		}
		
		if(gCubeCollections==null){
			response.setContentType("text/html");
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Server is not ready to serve any OAI-PMH requests. Wait for him to synchronize with the available collections.");
			} catch (IOException e) {}
			logger.debug("gCubeCollections is null");
			return;
		}
		
		if(gCubeCollections.isEmpty()){
			response.setContentType("text/html");
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Server is not able to serve any OAI-PMH requests. Server has no available collections.");
			} catch (IOException e) {}
			logger.debug("No collections available");
			return;
		}
		
		
		if(collections!=null) collections.clear();
		if(browsableFields!=null) browsableFields.clear();
		if(presentableFields!=null) presentableFields.clear();
		//get the needed values from the structure.
		for(CollectionInfo group : gCubeCollections.keySet()){
			for(CollectionInfo collectionInfo : gCubeCollections.get(group)){
				ArrayList<Field> colBrowsableFields = collectionInfo.getBrowsableFields();
				ArrayList<Field> colPresentableFields = collectionInfo.getPresentationFields();
				//if it has browsable fields, then add browsable and presentables
				if((colBrowsableFields!=null) && (!colBrowsableFields.isEmpty())){
					collections.put(collectionInfo.getId(), collectionInfo.getName());
					ArrayList<Pair> browsable = new ArrayList<Pair>();
					for(Field field : colBrowsableFields)
						browsable.add(new Pair(field.getId(),field.getName()));
					browsableFields.put(collectionInfo.getId(), browsable);
					ArrayList<Pair> presentable = new ArrayList<Pair>();
					for(Field field : colPresentableFields)
						presentable.add(new Pair(field.getId(),field.getName()));
					presentableFields.put(collectionInfo.getId(), presentable);					
				}
			}
		}
		
		
//		//create the repository -- ATTENTION-> THE REPOSITORY SHOULD BE CREATED ON THE SERVLET INTIATION AND NOT ON EVERY CALL (it might be slow, due to files generation)
		try{
			repository = GCubeRepository.createRepository(url, email, collections, browsableFields, presentableFields);
		}catch (IOException ex){
			logger.debug("could not create the gCube repository, details: "+ex);
		}
		
		//add all the request parameters to the properties
		Properties properties = new Properties();
		//the parameters from the HTTP get request (e.g. "verb")
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			properties.put(paramName, request.getParameterValues(paramName)[0]);
		}
		
		
		
		logger.debug("Serving http get request");
		
//		showRepoInfo();
		
		int cursor;
		if(properties.get("resumptionToken")==null)
			cursor = 0;
		else
			cursor = Integer.parseInt((String)properties.get("resumptionToken"));
		
		
		if("Identify".equalsIgnoreCase((String)properties.get("verb")))
			serveIdentify(properties, response);
		else if("ListMetadataFormats".equalsIgnoreCase((String)properties.get("verb")))
			serveListMetadataFormats(properties, response);
		else if("ListSets".equalsIgnoreCase((String)properties.get("verb")))
			serveListSets(properties, response);
		else if("ListIdentifiers".equalsIgnoreCase((String)properties.get("verb"))){
			logger.debug("Serving Identifiers...");
			try {
				serveListIdentifiers(properties, response, mysession, cursor, 1000); //replace 1000 with the actual cardinality of the set
			} catch (Exception e) {
				logger.debug("Failed to parse identifiers"+e);
			}
		}
		else if("ListRecords".equalsIgnoreCase((String)properties.get("verb")))
			try{
				serveListRecords(properties, response, mysession, cursor, 1000); //replace 1000 with the actual cardinality of the set
			} catch (Exception e){
				logger.debug("Exception occurred while serving the records... Details: "+e);
			}
		else if("GetRecord".equalsIgnoreCase((String)properties.get("verb"))){
			response.setContentType("text/html");
//			try {
//				response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "GetRecord feature is not implemented in this version");
//			} catch (IOException e) {}
			
			try {
				serveGetRecord(properties, response, mysession);				
			} catch (Exception e) {
				logger.debug("Failed to parse identifiers"+e);
			}
		}
		
	}
	
	
	private void serveIdentify(Properties requestParams,  HttpServletResponse httpResponse){
		Response repoResponse = new Response();
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getIdentifyResponse(requestParams,repository));
		} 
		catch (Exception e) {
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {}
		}
	}
	
	private void serveListMetadataFormats(Properties requestParams,  HttpServletResponse httpResponse){
		Response repoResponse = new Response();
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getListMetadataFormatsResponse(requestParams,repository));
		} 
		catch (Exception e) {
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {}
		}
	}
	
	
	private void serveListSets(Properties requestParams,  HttpServletResponse httpResponse){
		Response repoResponse = new Response();
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getListSetsResponse(requestParams,repository));
		} 
		catch (Exception e) {
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {}
		}
	}
	
	
	/**
	 * checks if any of the required parameters for the "ListIdentifiers" verb is missing
	 * @param requestParams
	 * @return true if missing, false if not missing
	 */
	private boolean missingIdentifiersParams(Properties requestParams){
		if(requestParams.getProperty("set")==null)
			return true;
		if(requestParams.getProperty("metadataPrefix")==null)
			return true;
		if(requestParams.getProperty("resumptionToken")==null)
			return true;
		return false;
	}
	
	
	/**
	 * Currently, needs only the 'set' parameter. Its output is always in the custom metadata format (dc needs to be implemented) 
	 * 
	 * @param requestParams
	 * @param selectedSet should be the selected repository set. Remember, that sets are actually the collections in gCube
	 * @param httpResponse
	 * @throws NoSearchMasterEPRFoundException 
	 * @throws SearchException 
	 * @throws InternalErrorException 
	 * @throws InitialBridgingNotCompleteException 
	 * @throws gRS2CreationException 
	 * @throws MalformedURLException 
	 * @throws SearchASLException 
	 */
	private void serveListIdentifiers(Properties requestParams, HttpServletResponse httpResponse, ASLSession aslsession, int cursor, int total) throws InitialBridgingNotCompleteException, InternalErrorException, SearchASLException {
		Response repoResponse = new Response();
		
		String set = requestParams.getProperty("set");
		if(set==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify a 'set' parameter on the get request"));
			} catch (Exception e) {}//no hope!
		}
		
//		String metadataPrefix = requestParams.getProperty("metadataPrefix");
		
		ArrayList<String> presentableFieldsIDs = new ArrayList<String>();
		for(Pair pair : presentableFields.get(Tools.getIDforName(collections, set)))
			presentableFieldsIDs.add(pair.getID());
		//set required parameters into the session.
		aslsession.setAttribute(SessionConstants.presentableFields, presentableFieldsIDs);
		//perform the browse and turn the results into identifiers
		ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
		HashMap<String,String> values = new HashMap<String, String>();
		Query q = new Query();
		
		//select browseby field
//		q.setBrowseBy("d0838339-59c4-4606-9578-bd7632710061");
		q.setBrowseBy(presentableFieldsIDs.get(0)); //let the first one (any one would do)
		
		//get the collection to set the browse on
		q.selectCollections(Arrays.asList(Tools.getIDforName(collections, set)), true, aslsession, false);
		
		logger.debug("Performing the browse query...");
		ResultSetConsumerI rs = q.browse(aslsession, new SearchClientImpl());
		logger.debug("Got the result set. Starting forming the response object.");
		
		List<DigitalObject> digitalObjects = null;
		try {
			if((cursor==0)||(cursor<ResponseConstants.RESULTS_PER_PAGE)){ //means first time serving results
				digitalObjects = rs.getFirst(ResponseConstants.RESULTS_PER_PAGE, new DisableButtons(), aslsession);
			}
			else{
				rs.advanceReaderBy(cursor);
				digitalObjects = rs.getNext(ResponseConstants.RESULTS_PER_PAGE, new DisableButtons(), aslsession);
			}
		} catch (gRS2NoRecordReadWithinTimeIntervalException
				| gRS2RecordDefinitionException | gRS2ReaderException
				| gRS2AvailableRecordsRetrievalException e3) {
			e3.printStackTrace();
		}
		
		for(DigitalObject object : digitalObjects){
			logger.debug("adding object with id: "+object.getObjectId()+" to the response");
			values.clear();
			values.put("id", object.getObjectId());
			values.put("datestamp", Toolbox.dateTimeNow()); //or the original, if available...
			Properties sets = new Properties();
			if(set!=null)
				sets.put(set,set);
			Identifier identifier = new Identifier(values, sets);
			identifiers.add(identifier);
		}
		
		logger.debug("Writing response - serveListIdentifiers()");
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getListIdentifiersResponse(requestParams, repository, identifiers, cursor, total));
		} 
		catch (Exception e) {
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {} //no hope !
		}
	}
	
	
	private void serveListRecords(Properties requestParams, HttpServletResponse httpResponse, ASLSession aslsession, int cursor, int total) throws InitialBridgingNotCompleteException, InternalErrorException, SearchASLException {

		Response repoResponse = new Response();

		String set = requestParams.getProperty("set");
		if(set==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify a 'set' parameter on the request"));
			} catch (Exception e) {}//no hope!
		}
		
		String metadataPrefix = requestParams.getProperty("metadataPrefix");
		if(metadataPrefix==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify a 'metadataPrefix' parameter on the request"));
			} catch (Exception e) {}//no hope!
		}
		
		ArrayList<String> presentableFieldsIDs = new ArrayList<String>();
		for(Pair pair : presentableFields.get(Tools.getIDforName(collections, set)))
			presentableFieldsIDs.add(pair.getID());
		//set required parameters into the session.
		aslsession.setAttribute(SessionConstants.presentableFields, presentableFieldsIDs);
		//perform the browse and turn the results into records
		ArrayList<Record> records = new ArrayList<Record>();
		HashMap<String,String> values = new HashMap<String, String>();
		Query q = new Query();
		
		//select browseby field
//		q.setBrowseBy("d0838339-59c4-4606-9578-bd7632710061");
		q.setBrowseBy(presentableFieldsIDs.get(0)); //let the first one (any one would do)
		
		//get the collection to set the browse on
		q.selectCollections(Arrays.asList(Tools.getIDforName(collections, set)), true, aslsession, false);
		
		logger.debug("Performing the browse query by collection: "+set+"("+Tools.getIDforName(collections, set)+")");
		ResultSetConsumerI rs = q.browse(aslsession, new SearchClientImpl());
		logger.debug("Got the result set. Starting forming the response object.");
		
		List<Properties> nameValuesRec = null;
		try {
			if((cursor==0)||(cursor<ResponseConstants.RESULTS_PER_PAGE)){ //means first time serving results
				nameValuesRec = rs.getFirstRaw(ResponseConstants.RESULTS_PER_PAGE, new DisableButtons(), aslsession);
			}
			else{
				rs.advanceReaderBy(cursor);
				nameValuesRec = rs.getNextRaw(ResponseConstants.RESULTS_PER_PAGE, new DisableButtons(), aslsession);
			}
		} catch (gRS2NoRecordReadWithinTimeIntervalException
				| GRS2BufferException
				| gRS2ReaderException
				| gRS2AvailableRecordsRetrievalException 
				| GRS2RecordDefinitionException e3) {
			e3.printStackTrace();
		}
		
		for(Properties nameValues : nameValuesRec){
//			logger.debug("adding object with id: "+nameValues.getObjectId()+" to the response");
			
			values.clear();
			
			for(String key : nameValues.stringPropertyNames()){
				values.put(key, (String)nameValues.get(key));
			}
			Properties sets = new Properties();
			if(set!=null)
				sets.put(set,set);
			Record record;
			
			if(metadataPrefix.equalsIgnoreCase(MetadataConstants.DCNAME) && repository.getSupportedMetadataPrefixes().contains(MetadataConstants.DCNAME)) //case of dc output format
				record = new Record(values, repository.getRecordTemplateDC(), sets, repository.getOAIDCMetadataXSD());
			else //case of custom (custom is always available)
				record = new Record(values, repository.getRecordTemplateCustom(), sets, repository.getCustomMetadataXSD());
			records.add(record);
		}
		
		logger.debug("Writing response - serveListRecords()");
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getListRecordsResponse(requestParams, repository, records, cursor, total));
		} 
		catch (Exception e) {
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {} //no hope !
		}
	}
	
	
	private void serveGetRecord(Properties requestParams, HttpServletResponse httpResponse, ASLSession aslsession ) throws InitialBridgingNotCompleteException, InternalErrorException, SearchASLException {
		/*
		To be coded, in a similar way the serveListRecords() works.
		Just perform a advanced search to asl for "ObjectID" = recordID
		and show the single result as in ListRecords.
		*/
		
		Response repoResponse = new Response();

		String set = requestParams.getProperty("set");
		if(set==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify a 'set' parameter on the request"));
			} catch (Exception e) {}//no hope!
		}
		
		String metadataPrefix = requestParams.getProperty("metadataPrefix");
		if(metadataPrefix==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify a 'metadataPrefix' parameter on the request"));
			} catch (Exception e) {}//no hope!
		}
		
		String recordID = requestParams.getProperty("identifier");
		if(recordID==null){
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Need to specify an 'identifier' parameter on the request"));
			} catch (Exception e) {}//no hope!
		}
		
		ArrayList<String> presentableFieldsIDs = new ArrayList<String>();
		for(Pair pair : presentableFields.get(Tools.getIDforName(collections, set)))
			presentableFieldsIDs.add(pair.getID());
		//set required parameters into the session.
		aslsession.setAttribute(SessionConstants.presentableFields, presentableFieldsIDs);
		
		Query q = new Query();
		
		q.setSearchType(SearchType.AdvancedSearch);
		
		//in order to have all collection fields in the result
		q.setSemanticEnrichment(true);
		//selectCollections in query class
		q.selectCollections(Arrays.asList(Tools.getIDforName(collections, set)), true, aslsession, true);
		
		ArrayList<Pair> id_names = browsableFields.get(Tools.getIDforName(collections, set));
		
		String identifier = ""; //hope this won't remain empty after the following loop...
		for(Pair id_name: id_names){
			if(id_name.getName().equalsIgnoreCase("identifier")){
				identifier = id_name.getID();
				break;
			}
		}
		
		logger.debug("Setting criterion: identifier("+identifier+")="+recordID);
		
		Criterion newCrit = new Criterion();
		newCrit.setSearchFieldId(identifier);
		newCrit.setSearchFieldName("identifier");
		newCrit.setSearchFieldValue(recordID);
		q.addCriterion(newCrit);
		
		//select browseby field
//		q.setBrowseBy("d0838339-59c4-4606-9578-bd7632710061");
//		q.setBrowseBy(presentableFieldsIDs.get(0)); //let the first one (any one would do)
//		
//		//get the collection to set the browse on
//		q.selectCollections(Arrays.asList(Tools.getIDforName(collections, set)), true, aslsession, true);
//		
//		logger.debug("Performing the browse query by collection: "+set+"("+Tools.getIDforName(collections, set)+")");
//		ResultSetConsumerI rs = q.browse(aslsession, new SearchClientImpl());
		
		logger.debug("Performing the search query on collection: "+set+"("+Tools.getIDforName(collections, set)+") for objectID: "+recordID);
		
		ResultSetConsumerI rs;
		List<Properties> nameValuesRec = null;
		try {
			rs = q.search(aslsession, false, new SearchClientImpl());
			logger.debug("Got the result set. Starting forming the response object.");
			nameValuesRec = rs.getFirstRaw(2, new DisableButtons(), aslsession);
		} catch (gRS2NoRecordReadWithinTimeIntervalException
				| GRS2BufferException
				| gRS2ReaderException
				| gRS2AvailableRecordsRetrievalException 
				| GRS2RecordDefinitionException 
				| QuerySyntaxException 
				| NoSearchMasterEPRFoundException  e3) {
			logger.debug(e3.getLocalizedMessage());
		}
		
		
		//perform the browse and turn the results into records
//		ArrayList<Record> records = new ArrayList<Record>();
		HashMap<String,String> values = new HashMap<String, String>();
		
		Properties nameValues = nameValuesRec.get(0);
			
		values.clear();
		
		for(String key : nameValuesRec.get(0).stringPropertyNames()){
			values.put(key, (String)nameValues.get(key));
		}
		Properties sets = new Properties();
		if(set!=null)
			sets.put(set,set);
		Record record;
		
		if(metadataPrefix.equalsIgnoreCase(MetadataConstants.DCNAME) && repository.getSupportedMetadataPrefixes().contains(MetadataConstants.DCNAME)) //case of dc output format
			record = new Record(values, repository.getRecordTemplateDC(), sets, repository.getOAIDCMetadataXSD());
		else //case of custom (custom is always available)
			record = new Record(values, repository.getRecordTemplateCustom(), sets, repository.getCustomMetadataXSD());
		
		logger.debug("Writing response - serveGetRecord()");
		
		try {
			httpResponse.setContentType("text/xml");
			httpResponse.getWriter().write(repoResponse.getGetRecordResponse(requestParams, repository, record));
		} 
		catch (Exception e) {
			httpResponse.setContentType("text/xml");
			try {
				httpResponse.getWriter().write(repoResponse.getErrorResponse(requestParams, repository, "Internal error, could not serve request"));
			} catch (Exception e1) {} //no hope !
		}
		
		
		
		
	}
	
	
	
	
	/*
	private String getResponse(Properties props){
		Response resp = new Response();
		try {
			return resp.getResponse(props,repository);
		} catch (Exception e) {
			return "";			
		}
	}
	*/
	
	private String getStackTraceString(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	private void showRepoAndCollectionsInfo() throws MalformedURLException{
		try {
			repository = GCubeRepository.createRepository(url, email, collections, browsableFields, presentableFields);
		} catch (IOException e) {
			logger.debug("Error while initiating the GCubeRepository"+e);
		}
		logger.debug("Collections: "+collections);
		for(String key: browsableFields.keySet()){
			logger.debug("browsable fields of "+key+" : ");
			for(Pair p : browsableFields.get(key))
				logger.debug("\t id: "+p.getID()+"\tname: "+p.getName());
		}
		for(String key: presentableFields.keySet()){
			logger.debug("presentable fields of "+key+" : ");
			for(Pair p : presentableFields.get(key))
				logger.debug("\t id: "+p.getID()+"\tname: "+p.getName());
		}
		
	}
	
}
