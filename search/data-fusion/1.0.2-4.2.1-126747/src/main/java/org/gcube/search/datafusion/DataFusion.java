package org.gcube.search.datafusion;

import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.store.Directory;
import org.gcube.search.datafusion.datatypes.Pair;
import org.gcube.search.datafusion.datatypes.PositionalRecordWrapper;
import org.gcube.search.datafusion.datatypes.RSFusedIterator;
import org.gcube.search.datafusion.datatypes.RankedRecord;
import org.gcube.search.datafusion.helpers.IndexHelper;
import org.gcube.search.datafusion.helpers.ResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class DataFusion implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String query;
	private int count = -1;
	private Iterator<PositionalRecordWrapper> iter = null;
	private static transient RRadaptor rradaptor;
	private static List<String> snippetFields = null;
	private static List<String> propertyFileSnippetFields = null;
	private static Boolean propertyFileInludePosition = null;
	
	private static final String SNIPPET_FIELDS_PROP = "snippet-fields";
	private static final String INCLUDE_POSITION_PROP = "include-position";
	
	private static final String HOSTNAME_PROP = "hostname";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataFusion.class);
	
	private static final String FUSION_PROPERTIES_FILE = "fusion.properties";
	
	private static final String DEPLOY_PROPERTIES_FILE = "deploy.properties";
	
	static {
		String hostname = null;
		
		try (InputStream is = Resources.getResource(FUSION_PROPERTIES_FILE).openStream()) {
			final Properties prop = new Properties();
			prop.load(is);
            //load a properties file
	 		propertyFileSnippetFields = Splitter.on(",").trimResults().splitToList(prop.getProperty(SNIPPET_FIELDS_PROP));
	 		
	 		if (prop.getProperty("include-position") != null)
	 			propertyFileInludePosition = prop.getProperty(INCLUDE_POSITION_PROP).equalsIgnoreCase("true");
	 		
            LOGGER.info("Fields read from property file : " + propertyFileSnippetFields);
            LOGGER.info("Include Position read from property file : " + propertyFileInludePosition);
		} catch (Exception e) {
			LOGGER.error("Error reading the from propery file", e);
		}
		
		try (InputStream is = Resources.getResource(DEPLOY_PROPERTIES_FILE).openStream()) {
			final Properties prop = new Properties();
			prop.load(is);
			hostname = prop.getProperty(HOSTNAME_PROP);
			
			LOGGER.info("hostname : " + hostname);
		} catch (Exception e) {
			LOGGER.error("Error reading the from deploy propery file", e);
		}
		 
		if (Strings.isNullOrEmpty(hostname)) {
			throw new IllegalArgumentException("Could not initialize datafusion because hostname not found in deploy.properties file");
		} 
		 
		LOGGER.info("Initializing gRS2");
		long starttime = System.currentTimeMillis();
		ResultSetHelper.initializeGRS2(hostname);
		long endtime = System.currentTimeMillis();
		LOGGER.info(" ~> initialize time : " + (endtime- starttime) / 1000.0 + " secs");
		
		LOGGER.info("Initializing ResourceRegistry");
		try {
			
			ResourceRegistry.startBridging();
			TimeUnit.SECONDS.sleep(1);
			while (!ResourceRegistry.isInitialBridgingComplete()) {
				LOGGER.info("registry not ready...sleeping");
				TimeUnit.SECONDS.sleep(10);
			}
			rradaptor = new RRadaptor();
		} catch (ResourceRegistryException e) {
			LOGGER.error("Resource Registry could not be initialized", e);
		} catch (InterruptedException e) {
			LOGGER.error("Resource Registry could not be initialized", e);
		}

		LOGGER.info("Initializing snippet Fields");
		initializeSnippetFields();
	}
	
	/**
	 * Instantiates data fusion for the records in the locators and the given query 
	 * 
	 * @param locators
	 * @param query
	 * @throws GRS2ReaderException
	 */
	public DataFusion(URI[] locators, String query) throws GRS2ReaderException {
		LOGGER.info("Initializing datafusion");
		
		this.iter = new RSFusedIterator(locators);
		this.query = query;
		
		//this.snippetField = snippetField;
	}
	/**
	 * Instantiates data fusion for the records in the locators and the given query 
	 * and returns up to the count records
	 *  
	 * @param locators
	 * @param query
	 * @param count
	 * @throws GRS2ReaderException
	 */
	public DataFusion(URI[] locators, String query, Integer count) throws GRS2ReaderException {
		LOGGER.info("Initializing datafusion");
		
		this.iter = new RSFusedIterator(locators, locators.length * count);
		this.query = query;
		this.count = count;
	}
	
	
	/**
	 * 
	 * @return gRS2 locator of the result 
	 * @throws Exception
	 */
	public URI operate() throws Exception {
		while (!ResourceRegistry.isInitialBridgingComplete()) {
			LOGGER.info("registry not ready...sleeping");
			TimeUnit.SECONDS.sleep(10);
		}
		
		LOGGER.info("in operate");
		return this.rerank();
	}
	
	
	/**
	 * Wrapper for the static {@link DataFusion#rerankRecords}
	 * 
	 * @return gRS2 locator of the reranked records
	 * @throws Exception
	 */
	public URI rerank() throws Exception{
		return rerankRecords(this.iter, this.query, this.count, snippetFields, propertyFileInludePosition);
	}
	
	/**
	 * Takes an iterator of {@link PositionalRecordWrapper}s and puts them in a lucene index. Then it performs the query to rerank the records and 
	 * returns the URI of the gRS of the result. The query is performed on an extra field that is either included the payload of the record field 
	 * in the list of snippetFields or the actual payload of the record (if none of the snippetFields are included in the record). The actual payload
	 * is retrieved from the objectID field which is a URI (text extraction is used to parse the response).
	 *
	 * After the records are reranked based on the query we can rearrange the results based on their initial position of each record by setting includePosition to true.
	 * This rearrangement is done in {@link RankedRecord#recalculateScores} and the actual calculation of the final score based on the lucene score and the initial position
	 * is done in {@link RankedRecord#calcScore}
	 * 
	 * @param iter
	 * @param query
	 * @param count
	 * @param snippetFields
	 * @param includePosition
	 * @return the URI of the gRS of the result
	 * @throws Exception
	 */
	public static URI rerankRecords(Iterator<PositionalRecordWrapper> iter, String query, int count, List<String> snippetFields, Boolean includePosition) throws Exception {
		long starttime = 0;
		long endtime = 0;
		
		starttime = System.currentTimeMillis();
		Directory index = IndexHelper.initializeIndex(); 
		endtime = System.currentTimeMillis();
		LOGGER.info(" ~> initializeIndex time : " + (endtime- starttime) / 1000.0 + " secs");		
		
		Set<String> fields = new HashSet<String>();
		
		//feedLucene
		starttime = System.currentTimeMillis();
		Map<String, Long> recPositions = IndexHelper.feedLucene(index, iter, snippetFields, fields, count);
		endtime = System.currentTimeMillis();
		LOGGER.info(" ~> feedLucene time : " + (endtime- starttime) / 1000.0 + " secs");		
		
		int cnt = count;
		if (cnt > recPositions.size() || cnt == -1)
			cnt = recPositions.size();
		
		LOGGER.info("number of records retrieved : " + recPositions.size() + " cnt : " + cnt);
		
		List<RankedRecord> rankedRecs = null;
		
		if (cnt == 0) {
			rankedRecs = new ArrayList<RankedRecord>();
		} else {
		//rankDocuments
			starttime = System.currentTimeMillis();
			Map<String, Pair> recScores =  IndexHelper.rankDocuments(index, query, cnt);
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~> rankDocuments time : " + (endtime- starttime) / 1000.0 + " secs");		
			
			
			starttime = System.currentTimeMillis();
			rankedRecs = RankedRecord.recalculateScores(recPositions, recScores, includePosition);
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~> recalculateScores time : " + (endtime- starttime) / 1000.0 + " secs");		
		}
		//muiltiget from lucene
		
//		starttime = System.currentTimeMillis();
//		URI locator = ResultSetHelper.multiGetAndWriteNoStream(rankedRecs, index, fields);
//		endtime = System.currentTimeMillis();
//		LOGGER.info(" ~> writeRecords time : " + (endtime- starttime) / 1000.0 + " secs");
		
		starttime = System.currentTimeMillis();
		URI locator = ResultSetHelper.multiGetAndWrite(rankedRecs, index, fields);
		endtime = System.currentTimeMillis();
		LOGGER.info(" ~> writeRecords time : " + (endtime- starttime) / 1000.0 + " secs");		

		LOGGER.info("gr2 locator : " + locator);
		return locator;
		//close and delete index
	}
	
	/**
	 * Gets the id for each field that is read from the property file 
	 */
	private static void initializeSnippetFields() {
		snippetFields = new ArrayList<String>();
		
		for (String field : propertyFileSnippetFields){
			try {
				LOGGER.info("Getting fieldID for field " + field);
				List<String> fieldIDs = rradaptor.getFieldIDsFromName(field);
				LOGGER.info("fieldIDs : " + fieldIDs);
				
				snippetFields.addAll(fieldIDs);
			} catch (Exception e) {
				LOGGER.error("Error while getting " + field, e);
			}
		}
	}

}
