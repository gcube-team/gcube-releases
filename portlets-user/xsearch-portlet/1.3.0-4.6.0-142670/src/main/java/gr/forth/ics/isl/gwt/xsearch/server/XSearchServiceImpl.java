/*
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package gr.forth.ics.isl.gwt.xsearch.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gr.forth.ics.isl.gwt.xsearch.client.XSearchService;
import gr.forth.ics.isl.gwt.xsearch.server.connection.xsearchservice.HttpCon;
import gr.forth.ics.isl.gwt.xsearch.server.gcubeSearch.GCubeSearch;
import gr.forth.ics.isl.gwt.xsearch.server.metadatagroupings.MetadataGroupingsGenerator;
import gr.forth.ics.isl.xsearch.configuration.Conf;
import gr.forth.ics.isl.xsearch.configuration.Resources;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.util.SessionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public class XSearchServiceImpl extends RemoteServiceServlet implements XSearchService{

    ObjectOutputStream Soutput = null;
    boolean useProxy = false;
    private static Logger logger=Resources.initializeLogger(XSearchServiceImpl.class.getName());
    
    /**
     * 
     * @param content
     * @param digitalObjectType
     * @return
     */
    public TreeMap<String, List<String>> getContentURLs(String content, DigitalObjectType digitalObjectType) {
        TreeMap<String, List<String>> contentURLsMap = new TreeMap<>();      
        System.out.println("DigitalObjectType isss" + digitalObjectType.name() + " " + digitalObjectType.toString());
        if(digitalObjectType.toString().equals("OAI")) {
            contentURLsMap = parseOAIDCPayload(content);
            logger.info("derived from OAI");
        }else if(digitalObjectType.toString().equals("FIGIS")){
            logger.info("derived from FIGIS collection");
            contentURLsMap = parseXMLWithAknownSchema(content);
        }else if(digitalObjectType.toString().equals("Generic")){
            logger.info("derived from Generic collection");
            contentURLsMap = parseXMLWithAknownSchema(content);
        }else if(digitalObjectType.toString().equals("SPD")){
            logger.info("derived from SPD collection");
            contentURLsMap = parseXMLWithAknownSchema(content);            
        }else{
            logger.info("derived from a collections with unknown XML schema (unable to parse it for retrieving the URL)");
            contentURLsMap = parseXMLWithAknownSchema(content);      
        }
        return contentURLsMap;
    }
    
    private TreeMap<String, List<String>> parseXMLWithAknownSchema(String XML){
        TreeMap<String, List<String>> contentURLsMap = new TreeMap<>();
        List<String> listOfUrls = new ArrayList<>();

        Document doc = parseXMLFileToDOM(XML);
        NodeList list  = doc.getElementsByTagName("*");
        for (int i =0; i < list.getLength(); i++ ){
            Node node = (Node) list.item(i);
            if(node.getNodeName().toLowerCase().contains("url")){
                 
                 listOfUrls.add(node.getFirstChild().getNodeValue());
                 
            }    
        }
        contentURLsMap.put("Contents List", listOfUrls);
        
        return contentURLsMap;
    }
    
    /**
     * 
     * @param content
     * @return
     */
    private TreeMap<String, List<String>> parseOAIDCPayload(String content) {
        TreeMap<String, List<String>> contentURLs = new TreeMap<>();
        List<String> mainURLsList = new ArrayList<>();
        List<String> altURLsList = new ArrayList<>();

        Document doc = parseXMLFileToDOM(content);
        NodeList list = doc.getElementsByTagName("content");
        if (list != null) {
            for(int i=0; i < list.getLength(); i++ ) {
                Element contentNode = (Element)list.item(i);
                String contentType = "";
                String contentURL = "";

                // Get elements by tag Name "contentType"
                NodeList ctList = contentNode.getElementsByTagName("contentType");
                if(ctList != null && ctList.getLength() > 0) {
                        Element el = (Element)ctList.item(0);
                        contentType = el.getFirstChild().getNodeValue();
                }

                // Get elements by tagName "url"
                NodeList urlList = contentNode.getElementsByTagName("url");
                if(urlList != null && urlList.getLength() > 0) {
                        Element el = (Element)urlList.item(0);
                        contentURL = el.getFirstChild().getNodeValue();
                }

                // Distinguish between Main and Alternative
                if (contentType.equalsIgnoreCase("main"))
                        mainURLsList.add(contentURL);
                else
                        altURLsList.add(contentURL);
            }

            // Add them to the map
            contentURLs.put("MAIN", mainURLsList);
            contentURLs.put("ALTERNATIVE", altURLsList);

        }else{
                return null;
        }

        return contentURLs;
    }
    
    /**
     * Parse XML file to DOM.
     * @param XMLdoc the xml string
     * @return the xml string to Document format
     */
    private static Document parseXMLFileToDOM(String XMLdoc){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try{
            builder = dbFactory.newDocumentBuilder();
        }catch(ParserConfigurationException ex){
            logger.error(ex.toString());
        }

        Document doc = null;
        try{
            doc = builder.parse(new InputSource(new StringReader(XMLdoc)));
        }catch(SAXException | IOException ex){
            logger.error(ex.toString());
        }

        return doc;
    }
    
    /**
     * 
     */
    public TreeMap<String, List<String>> getURIContent(String objectURI){

        // Get Object type
        GCubeSearch gcubeSearch = new GCubeSearch();
        ASLSession aslSession = gcubeSearch.getASLSession(getHttpReq());
//        DigitalObject digitalObject = new DigitalObject(aslSession, objectURI);
        String objectContent="";
//        try {
//            objectContent = digitalObject.getContent();
//        } catch (IllegalStateException | IOException ex) {
//            logger.error(ex.toString());
//        }
        //DigitalObject dObject = new DigitalObject(aslSession, "http://gcube.data.d4science.org/tree/FIGIS/FPP?scope=/gcube/devNext");
        //System.out.println("Digital objectType: " + digitalObject.getType());
       // System.out.println("Is digital the object type of FIGIS: "+dObject.getType().equals(DigitalObjectType.FIGIS));
//        return getContentURLs(objectContent,  digitalObject.getType());
        return getContentURLs(objectContent,  null);
    }

    /**
     * Returns to the client a map which contains the query hits, the query and
     * the collections.
     */
    public Map<String, ArrayList<String>> getQueryResults(int startOffset) {
        Map<String, ArrayList<String>> map=new HashMap<>();
    
        // Initializes XSearch variables.
        String projectName = XSearchServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString().split("webapps/")[1].split("/")[0];
        Conf.InitializeXSearchProperties(System.getProperty("catalina.home") + "/webapps/" + projectName + "/PropertyFiles/XSearch.properties");

        if (!useProxy) {

            
            GCubeSearch gcubeSearch = new GCubeSearch();

            // Get ASLSession
            ASLSession aslSession = gcubeSearch.getASLSession(getHttpReq());

            boolean isActive = gcubeSearch.isSearchActive(aslSession);
            if (!isActive) {
                return initializeMapContainer(null, null, null, null, isActive, null);
            }

            // Get the XSearchServiceIp through IS or keep the one that exists at PropertyFile
            Thread updateCacheThread = null;
            if (Conf.getXSearchServiceURLThroughIS) {
            	// Get XSearch Service URL
            	final ExploitationOfIS expIS = new ExploitationOfIS(aslSession);            	            	
            	expIS.updateXSearchServiceURL();
            	
            	if (expIS.updateCache){
            		 updateCacheThread = new Thread(new Runnable() {
                         public void run() {
                        	 expIS.retrieveXSearchServiceEndpointsThroughIS();
                        	 expIS.updateXSearchServiceURL();
                         }
            		 });
            		 updateCacheThread.start();
            	}
            }

            // Get SearchHelper
//            SearchHelper searchH = new SearchHelper(aslSession);

            // Initialize some 
            Object obj = aslSession.getAttribute(SessionConstants.activePresentationQueryNo);
            if (obj == null) {
                // If the obj is null then return null in order to catch it from Client
                return null;
            }
            int qgid = ((Integer) obj).intValue();
            int qid = 0;

            // Gets the query group for SearchHelper
//            QueryGroup queries = searchH.getQuery(qgid);
//            if (queries == null) {
//                return null;
//            }

            // Gets the query
//            Query q = queries.getQuery(qid);
//            if (q == null) {
//                return null;
//            }

            // Get ResultSetConsumer
//            ResultSetConsumerI resConsumer = q.getSearchResults(aslSession);
//            if (resConsumer == null) {
//                // If the obj is null then return null in order to catch it from Client
//                return null;
//            }

            // Get the concat query from query terms
//            String query = getQueryTermsToOneString(q);

            //Get results from consumer		
//            ArrayList<String> results = gcubeSearch.getResultsFromConsumer(Conf.totalNumOfResConsume, startOffset, aslSession, resConsumer);
            ArrayList<String> results = new ArrayList<>();
            logger.info("=> The #of results returned from consumer is: " + results.size());

            // Init a list results that we are going to present as query's answer
            ArrayList<String> resultsToShow = new ArrayList<>();

            // Iterate through the XMLRepresentations
            XSearchHitPresenter hitPresenter = new XSearchHitPresenter();
            int num = 0;

            MetadataGroupingsGenerator metadataGroupings = new MetadataGroupingsGenerator();
            for (String xmlRepresentation : results) {

            	//System.out.println(xmlRepresentation);
                String hit = hitPresenter.createNewHit(metadataGroupings, num, getHttpReq(), xmlRepresentation);
                num++;
                // Adds to Results of query a new result
                resultsToShow.add(hit);
            }
               
            String metadataGroupingJSONString = "";
//            if(Conf.enableMetadataGroupings){
//            	metadataGroupingJSONString =metadataGroupings.createMetadataGroupingsJSONString(query, startOffset); 
//            }
            
//            String collectionsInOneString = getCollectionsToOneString(q, aslSession);
            String collectionsInOneString = "";

            // Create a Map with two 
//            map = initializeMapContainer(resultsToShow, "", query, collectionsInOneString, isActive, metadataGroupingJSONString);
            
            
            // Wait until the cache update finish
            while (updateCacheThread != null && updateCacheThread.isAlive()){
            	try{
                            Thread.sleep(5);
                   }catch(InterruptedException ex){
                            logger.error(ex.toString());
                   }
            }
        } else {

            logger.info("Attention: The proxy class is being used");
            ProxyClass proxyClass = new ProxyClass();

            // Init a list results that we are going to present as query's answer
            ArrayList<String> resultsToShow = new ArrayList<>();
            // Iterate through the XMLRepresentations
            XSearchHitPresenter hitPresenter = new XSearchHitPresenter();
            int num = 0;

            MetadataGroupingsGenerator metadataGroupings = new MetadataGroupingsGenerator();
            for (String xmlRepresentation : proxyClass.getResultsFromConsumer(10, 0)) {//Conf.startRetrResultsFromOffset, Conf.startRetrResultsFromOffset)) {			
                
                String hit = hitPresenter.createNewHit(metadataGroupings, num, getHttpReq(), xmlRepresentation);
                num++;
                // Adds to Results of query a new result
                resultsToShow.add(hit);
            }
            String metadataGroupingJSONString = "";
            if(Conf.enableMetadataGroupings){
            	metadataGroupingJSONString =metadataGroupings.createMetadataGroupingsJSONString(proxyClass.getQuery(), startOffset); 
            }
            
            // Create a Map with two 
            map = initializeMapContainer(resultsToShow, "", proxyClass.getQuery(), proxyClass.getCollections(), true, metadataGroupingJSONString);
        }

        return map;
    }

    /**
     * Returns to the client a map which contains the query hits, the query and
     * the collections and the results of mining/clustering over the hits
     */
    public Map<String, ArrayList<String>> getSemanticAnalysisResults(int startOffset, int numOfResultsAnalyze) {
        logger.info("=> Get semantic analysis for the results (StartOffset, numOfResAnalyze) => (" + startOffset + ", " + numOfResultsAnalyze + ")");
        // Initializes XSearch variables.
        //InitializeXSearchProperties(System.getProperty("catalina.home")+"/webapps/XSearch-Portlet/PropertyFiles/XSearch.properties");

        // Init a string that will contain the results of XSearchService
        String xSearchResultsJSON = new String();

        Map<String, ArrayList<String>> map = null;
        if (!useProxy) {

            // Init a list results that we are going to present as query's answer
            //ArrayList<String> resultsToShow = new ArrayList<String>();

            GCubeSearch gcubeSearch = new GCubeSearch();

            // Get ASLSession
            ASLSession aslSession = gcubeSearch.getASLSession(getHttpReq());

            boolean isActive = gcubeSearch.isSearchActive(aslSession);
            if (!isActive) {
                return initializeMapContainer(null, null, null, null, isActive, null);
            }

            // Get SearchHelper
//            SearchHelper searchH = new SearchHelper(aslSession);

            // Initialize some 
            Object obj = aslSession.getAttribute(SessionConstants.activePresentationQueryNo);
            if (obj == null) {
                // If the obj is null then return null in order to catch it from Client
                return null;
            }
            int qgid = ((Integer) obj).intValue();
            int qid = 0;

            // Gets the query group for SearchHelper
//            QueryGroup queries = searchH.getQuery(qgid);
//            if (queries == null) {
//                return null;
//            }
//
//            // Gets the query
//            Query q = queries.getQuery(qid);
//            if (q == null) {
//                return null;
//            }
//
//            // Get ResultSetConsumer
//            ResultSetConsumerI resConsumer = q.getSearchResults(aslSession);
//            if (resConsumer == null) {
//                // If the obj is null then return null in order to catch it from Client
//                return null;
//            }

            //Get results from consumer
//            final ArrayList<String> results = gcubeSearch.getResultsFromConsumer(numOfResultsAnalyze, startOffset, aslSession, resConsumer);
            final ArrayList<String> results = new ArrayList<>();

            // Get the concat query from query terms
//            String query = getQueryTermsToOneString(q);
            String query = "";

            try {

                // A gRS can contain a number of diff record definitions
                RecordDefinition[] defs = gcubeSearch.createRecordDefinitionTable();

                // Initialize RecordWriter
                RecordWriter<GenericRecord> writer = gcubeSearch.initRecordWriter(defs);

                // Initialize TCP connectionManager which is used from the tcpLocator
                initiliazeTCPConnectionManager();
                
                // Initiazes TCPConnectionManager & Calls XSearchService to get Json Results
                //TcpCon tcpCon = new TcpCon();
                //Socket socket = tcpCon.tcpRequestToXSearchService(Soutput, writer, query, startOffset);

                final HttpCon httpCon = new HttpCon(writer, query, startOffset);
                HttpURLConnection con = httpCon.httpRequestToXSearchServiceForSemanticEnrichment();
                if(!httpCon.isConnectionEstablished()){
                    return null;
                }

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        // Iterate through the XMLRepresentations
                        int numOfXml = 0;
                        for (String xmlRepresentation : results) {

                            // Create new GenericRecord
                            GenericRecord rec = createNewGenericRecord(xmlRepresentation);

                            // Add new Recond to TCP Locator
                            if (!httpCon.addRecordToTCPLocator(rec)){
                                break;
                            }
                            numOfXml++;
                        }
                        System.out.println("=> The number of Results processed is: " + numOfXml);
                    }
                });
                thread.start();
               
                //xSearchResultsJSON = tcpCon.tcpReceiveReqFromXSearchSerive(Soutput, socket);
                xSearchResultsJSON = httpCon.httpReceiveReqFromXSearchSeriveWithSemanticEnrichment(con);
                if(!httpCon.isConnectionEstablished()){
                    return null;
                }               
                
                writer.dispose();

            } catch (Exception e) {
                e.printStackTrace();
            }

//            String collectionsInOneString = getCollectionsToOneString(q, aslSession);
            String collectionsInOneString = "";

            // Create a Map the results is null because the hits have already send to User with QueryHits
            map = initializeMapContainer(null, xSearchResultsJSON, query, collectionsInOneString, isActive, "");

        } else {
            logger.info("Attention; The proxy class is being used");
            ProxyClass proxyClass = new ProxyClass();
            map = initializeMapContainer(null, proxyClass.getxSearchResultsJSON(), proxyClass.getQuery(), proxyClass.getCollections(), true, "");
        }

        return map;
    }

    /**
     * Initializes TCPConnectionManager
     */
    public void initiliazeTCPConnectionManager(){
            TCPConnectionManager.Init(new TCPConnectionManagerConfig(
                            getExternalIp(), new ArrayList<PortRange>(), true));		
            TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
            TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
    }


    /**
    * @return the Server's external IP
    */
    private String getExternalIp(){		
            return this.getThreadLocalRequest().getServerName();
    }
    
    public GenericRecord createNewGenericRecord(String xmlRepresentation) {
        GenericRecord rec = new GenericRecord();
        Field[] fs = new Field[2];

        // HTMLTag object for the XML data
        HTMLTag xmlReprReader = new HTMLTag(xmlRepresentation);

        // get the index of the first element 'field' in the xml data
        int i = xmlReprReader.getFirstTagIndex("field");

        // Iterate through all 'fields in the XMLRepresentation
        while (i != -1) {

            //Get the content of the current field
            String fieldData = xmlReprReader.getFirstTagData("field", i);

            // Create a HTMLTag object for the content field
            HTMLTag fieldTagger = new HTMLTag(fieldData);

            // Get the element Name
            String name = fieldTagger.getFirstTagData("fieldName");

            // Get the elemen id
            String id = fieldTagger.getFirstTagData("fieldId");

            // Get the element value
            String value = fieldTagger.getFirstTagData("fieldValue");

            // Sets the Value of first field
            if (name.trim().toLowerCase().equals("title")) {
                if (value != null) {
                    value = value.replace("&gt;", ">").replace("&lt;", "<");
                    fs[0] = new StringField(value);
                }
            }

            // Sets the value of second field
            if (name.trim().toLowerCase().equals("s")) {
                if (value != null) {
                    value = value.replace("&gt;", ">").replace("&lt;", "<");
                    fs[1] = new StringField(value);
                }
            }

            // Get DocURI
            if (id.trim().equals("ObjectID")) {
                //docURI =  value;
            }

            // Find the index of the next field
            i = xmlReprReader.getFirstTagIndex("field", i + 1);
        }

        // Check if any field null and if yes initialize it with empty string				
        for (int k = 0; k < fs.length; k++) {
            if (fs[k] == null) {
                fs[k] = new StringField("");
            }
        }

        // Adds a new Field to the Record
        rec.setFields(fs);

        return rec;
    }

//    /**
//     * Returns the query that was sumbited at Search field.
//     *
//     * @param q query
//     * @return
//     */
//    private String getQueryTermsToOneString(Query q) {
//        // Get query terms and Concat them to one query
//        ArrayList<String> queryTerms = q.getSearchQueryTerms();
//        String query = new String();
//        for (String qTerm : queryTerms) {
//            query += qTerm + " ";
//        }
//
//        return query;
//        //return q.getQueryDescription().split("\"")[1];
//    }

    private String getCollectionsToOneString(ASLSession aslSession) {

        String onlyOneCollectionString = "";
//            for (String col : (ArrayList<String>) q.getSelectedCollectionNames(aslSession)) {
//                onlyOneCollectionString += col + ", ";
//            }
            
            
            // Remove last space and comma
            onlyOneCollectionString = onlyOneCollectionString.substring(0, onlyOneCollectionString.length()-2);


        return onlyOneCollectionString;
    }

    /**
     * Gets the query results and the XSearchService results and creates a
     * Map<String, ArrayList<String>> that contains them.
     *
     * @param resultsToShow ArrayList<String> that contains the query results
     * that we will present as query's answer
     * @param XSearchServiceResults json string that contains the answer of
     * XSearchService
     * @return the map that contains all the information that we need to present
     * to the final user.
     */
    private Map<String, ArrayList<String>> initializeMapContainer(ArrayList<String> resultsToShow, String XSearchServiceResults, String query, String collections, boolean isActive, String metadataGroupingJsonString) {

        Map<String, ArrayList<String>> map = new HashMap<>();

        ArrayList<String> isActiveList = new ArrayList<>();
        isActiveList.add(Boolean.toString(isActive));
        map.put("isActive", isActiveList);

        if (!isActive) {
            return map;
        }

        // add Results to the map
        map.put("Results", resultsToShow);
        
        // add xsearch service url
        ArrayList<String> bookmarkletService = new ArrayList<>();
        bookmarkletService.add(Conf.XSearchBookmarkletServiceURL);
        map.put("bookmarkletServiceUrl", bookmarkletService);

        // Add XSearchServise results in json format into the map
        ArrayList<String> jsonString = new ArrayList<>();
        jsonString.add(XSearchServiceResults);
        map.put("Json", jsonString);

        // Add query list to the map
        ArrayList<String> queryList = new ArrayList<>();
        queryList.add(query);
        map.put("Query", queryList);

        // Add collections to the map
        ArrayList<String> collectionsList = new ArrayList<>();
        collectionsList.add(collections);
        map.put("Collections", collectionsList);

        ArrayList<String> numOfResultsLoadedL = new ArrayList<>();
        numOfResultsLoadedL.add(Conf.totalNumOfResConsume + "");
        map.put("numOfResultsConsume", numOfResultsLoadedL);

        ArrayList<String> numOfResultsPerPageList = new ArrayList<>();
        numOfResultsPerPageList.add(Conf.numOfResultsPerPage + "");
        map.put("NumOfResultsPerPage", numOfResultsPerPageList);

        ArrayList<String> numOfResultsToAnalyzeList = new ArrayList<>();
        numOfResultsToAnalyzeList.add(Conf.numOfResultsToAnalyze + "");
        map.put("NumOfResultsAnalyze", numOfResultsToAnalyzeList);

        ArrayList<String> miningNewResultsPerPageList = new ArrayList<>();
        miningNewResultsPerPageList.add(Conf.miningNewResultsPerPage + "");
        map.put("MiningNewResultsPerPage", miningNewResultsPerPageList);

        ArrayList<String> mergeSemanticAnalysisResultsList = new ArrayList<>();
        mergeSemanticAnalysisResultsList.add(Conf.mergeSemanticAnalysisResults + "");
        map.put("MergeSemanticAnalysisResults", mergeSemanticAnalysisResultsList);
                
        ArrayList<String> metadataGroupingsL = new ArrayList<>();
//        metadataGroupingJsonString = metadataGroupingJsonString;
        metadataGroupingsL.add(metadataGroupingJsonString);
        map.put("metadataGroupingsJson", metadataGroupingsL);
        
        ArrayList<String> metadataGroupingEnanledL = new ArrayList<>();
        metadataGroupingEnanledL.add(Boolean.toString(Conf.enableMetadataGroupings));
        map.put("enableMetadataGroupings", metadataGroupingEnanledL);
        
        ArrayList<String> clusteringEnanledL = new ArrayList<>();
        clusteringEnanledL.add(Boolean.toString(Conf.enableClustering));
        map.put("clusteringEnabled", clusteringEnanledL);
        
        ArrayList<String> miningEnanledL = new ArrayList<>();
        miningEnanledL.add(Boolean.toString(Conf.enableMining));
        map.put("miningEnabled", miningEnanledL);
        
        ArrayList<String> explorationSearchTypeList = new ArrayList<>();
        explorationSearchTypeList.add(Conf.explorationSearchType);
        map.put("explorationSearchType", explorationSearchTypeList);
        
        return map;
    }

    /**
     * Gets the HttpServletRequest from current call.
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getHttpReq() {
        return this.getThreadLocalRequest();
    }

    /**
     * Retrieves from the service the semantic information regarding an entity
     *
     * @param entityName The name of the entity
     * @param categoryName The category of the entity
     * @return A string that represents the semantic information of the
     * corresponding entity
     */
    public String getEntityEnrichment(String entityName, String categoryName) {

        String entityEnrichement = "";

        try {

            if (Conf.XSearchServiceURL == null) {
                Conf.XSearchServiceURL = "";
            }
            
            if (Conf.XSearchServiceURL.trim().equals("")) {
                Conf.XSearchServiceURL = "http://83.212.114.9:8080/xsearch-service-1.0.2/";
            }
            
            String xSearchURL = Conf.XSearchServiceURL+"InspectEntity?"
                    + "element=" + URLEncoder.encode(entityName, "utf-8")
                    + "&category=" + URLEncoder.encode(categoryName, "utf-8");

            logger.info("The URL for entity enrichment is: "+ xSearchURL);

            URL url = new URL(xSearchURL);
            URLConnection con = url.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            con.setConnectTimeout(90000);
            con.setReadTimeout(90000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String result = "";
            String input;
            while ((input = in.readLine()) != null) {
                result = result + input + "\n";
            }
            in.close();


            entityEnrichement += result;

        }catch(IOException ex) {
            entityEnrichement += "<font size=\"-1\" color=\"red\"><center>Problem retrieving entities..please try again later!</center></font>";
        }
        return entityEnrichement;
    }

    /**
     * Retrieves from the service the semantic information regarding an entity
     *
     * @param category The category of the entity
     * @param uri The URI of the entity
     * @return A string that represents the properties of the corresponding URI
     * corresponding entity
     */
    public String getURIProperties(String category, String uri) {
        String properties = "";

        try {

            if (Conf.XSearchServiceURL == null) {
                Conf.XSearchServiceURL = "";
            }
            
            if (Conf.XSearchServiceURL.trim().equals("")) {
                Conf.XSearchServiceURL = "http://83.212.114.9:8080/xsearch-service-1.0.2/";
            }
            
            String xSearchURL = Conf.XSearchServiceURL+"ShowProperties?"
                    + "uri=" + URLEncoder.encode(uri, "utf-8")
                    + "&category=" + URLEncoder.encode(category, "utf-8");

            logger.info("The URL for entity enrichmenet is: "+xSearchURL);

            URL url = new URL(xSearchURL);
            URLConnection con = url.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            con.setConnectTimeout(90000);
            con.setReadTimeout(90000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String result = "";
            String input;
            while ((input = in.readLine()) != null) {
                result = result + input + "\n";
            }
            in.close();


            properties += result;

        }catch(IOException e) {
            properties += "<font size=\"-1\" color=\"red\"><center>Problem retrieving entities..please try again later!</center></font>";
        }
        return properties;
    }

    private static class DigitalObjectType {

        public DigitalObjectType() {
        }
        
        public String name(){
            return "";
        }
    }
}
