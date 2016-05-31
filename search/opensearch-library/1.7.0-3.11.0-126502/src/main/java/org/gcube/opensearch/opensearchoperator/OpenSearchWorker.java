package org.gcube.opensearch.opensearchoperator;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.activation.MimeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.responseelements.HTMLResponse;
import org.gcube.opensearch.opensearchlibrary.responseelements.OpenSearchResponse;
import org.gcube.opensearch.opensearchlibrary.responseelements.XMLResponse;
import org.gcube.opensearch.opensearchlibrary.utils.FactoryPair;
import org.gcube.opensearch.opensearchlibrary.utils.FactoryResolver;
import org.gcube.opensearch.opensearchlibrary.utils.URLEncoder;
import org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Used to perform the actual OpenSearchOp operations needed to obtain results and to return
 * the locator of the output
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class OpenSearchWorker implements Runnable {
	
	/**
	 * Integer wrapper that is used for concurrent access
	 * 
	 * @author gerasimos.farantatos, NKUA
	 *
	 */
	private class IntegerHolder {
		Integer i = null;
		/**
		 * Retrieves the underlying Integer value
		 * 
		 * @return The value of the underlying Integer
		 */
		public Integer get() {
			return i;
		}
		/**
		 * Assigns a value to the underlying Integer
		 * 
		 * @param i The value to set
		 */
		public void set(Integer i) {
			this.i = i;
		}
	}
	
	/**
	 * A utility messenger class containing an input/output encoding pair
	 * 
	 * @author gerasimos
	 *
	 */
	private class EncodingPair {
		public final String inputEncoding;
		public final String outputEncoding;
		
		/**
		 * Creates a new encoding pair
		 * 
		 * @param inputEncoding The input encoding
		 * @param outputEncoding The output encoding
		 */
		public EncodingPair(String inputEncoding, String outputEncoding) {
			this.inputEncoding = inputEncoding;
			this.outputEncoding = outputEncoding;
		}
	}
	
	private String terms;
	private Map<String, String> params;
	private String fixedTerms;
	private Map<String, String> fixedParams;
	private Set<String> queryNamespaces = null;
	private URI outLocator;
	private OpenSearchResource resource;
	private OpenSearchResource topResource;
	private ResourceRepository resources;
	private RecordWriter<GenericRecord> writer = null;
	private Object synchLocator = null;
	private OpenSearchOpConfig config;
	private volatile IntegerHolder resultsRemaining = null;
	private volatile IntegerHolder totalResultCount = null;
	private int resultLimit = -1;
	private boolean noLimit = false;
	private Object synchWriter;
	private Object synchInt;
	private Object synchFinalResultCount = null;
	private volatile Integer finalResultCount = null;
	private boolean emittedFinalEvent = false;
	private Boolean emitProgressiveEvents = null;
	
	private static Logger logger = LoggerFactory.getLogger(OpenSearchWorker.class.getName());
	
	/**
	 * Class used to store tranformation specifications contained in OpenSearchResource objects.
	 * 
	 * @author gerasimos.farantatos
	 *
	 */
	private class TransformationSpec {
		public final String MimeType;
		public final Transformer transformer;
		public final XPathExpression recordSplitXpath;
		public final XPathExpression recordIdXpath;
		public final Map<String, String> presentationInformation;

		public TransformationSpec(String MimeType, Transformer transformer, XPathExpression recordSplitXpath, 
				XPathExpression recordIdXpath, Map<String, String> presentationInformation) {
			this.MimeType = MimeType;
			this.transformer = transformer;
			this.recordSplitXpath = recordSplitXpath;
			this.recordIdXpath = recordIdXpath;
			this.presentationInformation = presentationInformation;
		}
	}
	
	/**
	 * Constructs a new OpenSearchWorker, to be used internally in the case when results from multiple
	 * brokered providers should be retrieved simultaneously by separate threads
	 * 
	 * @param resource The OpenSearchResource associated with the provider that will be queried for results
	 * @param topResource The resource that was originally passed to the OpenSearchWorker by a client
	 * @param resources A repository capable of retrieving OpenSearchResources on demand
	 * @param config The configuration to be used
	 * @param terms The search terms to be used while querying the provider
	 * @param params The OpenSearch parameters that to be used while querying the provider
	 * @param fixedTerms The pre-fixed terms to be used while querying the top-level broker
	 * @param fixedParams The pre-fixed parameters to be used while querying the top-level broker
	 * @param queryNamespaces The namespaces present in the query
	 * @param resultsRemaining The number of results remaining to complete the client's request
	 * @param totalResultCount The number of results obtained so far
	 * @param noLimit true if all search results should be obtained, false otherwise
	 * @param writer The writer to be used, created by the top OpenSearchWorker
	 * @param outLocator The locator of the output, created by the top OpensearchWorker
	 * @param synchWriter Used to synchronized the writer which populates the resultset
	 * @param synchInt Used to synchronize the resultsRemaining and totalResultCount objects
	 */
	private OpenSearchWorker(OpenSearchResource resource, OpenSearchResource topResource, ResourceRepository resources, OpenSearchOpConfig config, 
			String terms, Map<String, String> params, String fixedTerms, Map<String, String> fixedParams, Set<String> queryNamespaces,
			IntegerHolder resultsRemaining, IntegerHolder totalResultCount, boolean noLimit, RecordWriter<GenericRecord> writer, URI outLocator, Object synchWriter, Object synchInt) {
		this.writer = writer;
		this.outLocator = outLocator;
		this.terms = terms;
		this.params = params;
		this.fixedTerms = fixedTerms;
		this.fixedParams = fixedParams;
		this.queryNamespaces = queryNamespaces;
		this.resource = resource;
		this.topResource = topResource;
		this.resources = resources;
		this.config = config;
		this.synchWriter = synchWriter;
		this.synchInt = synchInt;
		this.synchFinalResultCount = new Object();
		this.resultsRemaining = resultsRemaining;
		this.totalResultCount = totalResultCount;
		this.noLimit = noLimit;
	}
	
	/**
	 * Creates a new OpenSearchWorker
	 * 
	 * @param resource The OpenSearchResource associated with the provider that will be queried for results
	 * @param resources A repository capable of retrieving OpenSearchResources on demand
	 * @param config The configuration to be used
	 * @param terms The search terms to be used while querying the provider
	 * @param params The OpenSearch parameters that to be used while querying the provider
	 * @param fixedTerms The pre-fixed terms to be used while querying the top-level broker
	 * @param fixedParams The pre-fixed parameters to be used while querying the top-level broker
	 * @param queryNamespaces The namespaces present in the query
	 * @throws Exception An error has ocurred
	 */
	public OpenSearchWorker(OpenSearchResource resource, ResourceRepository resources, OpenSearchOpConfig config, 
			String terms, Map<String, String> params, String fixedTerms, Map<String, String> fixedParams, 
			Set<String> queryNamespaces, Object synchLocator) throws Exception {
		
		this.terms = terms;
		this.params = params;
		this.fixedTerms = fixedTerms;
		this.fixedParams = fixedParams;
		this.queryNamespaces = queryNamespaces;
		this.resource = resource;
		this.topResource = resource;
		this.resources = resources;
		this.config = config;
		this.resultsRemaining = new IntegerHolder();
		this.totalResultCount = new IntegerHolder();
		this.synchWriter = new Object();
		this.synchInt = new Object();
		this.synchLocator = synchLocator;
	}
	
	/**
	 * Returns the locator of the output
	 * 
	 * @return The locator of the output
	 */
	public URI getLocator(){
		return outLocator;
	}
	
	public Integer getFinalResultCount() {
		synchronized(synchFinalResultCount) {
			while(finalResultCount==null) {
				try { synchFinalResultCount.wait(); } 
				catch(InterruptedException e) { }
			}
			if(finalResultCount == -1) return null;
			else return new Integer(finalResultCount);
		}
	}
	
	private Map<String, Integer> initializeWriter(Map<String, String> presentationInformation) throws Exception {
		Map<String, Integer> positions = new HashMap<String, Integer>();
		Set<String> presentables = presentationInformation.keySet();
		FieldDefinition[] fieldDefs = new FieldDefinition[presentables.size()+1];
		fieldDefs[0] = new StringFieldDefinition(OpenSearchConstants.objectIdFieldName);
		int i = 1;
		for(String presentable: presentables) {
			fieldDefs[i] = new StringFieldDefinition(presentable);
			positions.put(presentable, i++);
		}
		LocalWriterProxy producerProxy = new LocalWriterProxy();
		writer = new RecordWriter<GenericRecord>(producerProxy, new RecordDefinition[]{new GenericRecordDefinition(fieldDefs)}, 100, 
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
		outLocator = producerProxy.getLocator();
		synchronized(synchLocator) {
			synchLocator.notifyAll();
		}
		return positions;
	}
	
	/**
	 * Checks the operator configuration parameters contained in a query for validity
	 * 
	 * @param params The parameters passed to the OpenSearchWorker
	 * @throws Exception If a configuration parameter is found invalid or another error has occurred
	 */
	private void parseConfigParams(Map<String, String> params) throws Exception {
		for(Map.Entry<String, String> en : params.entrySet()) {
			if(en.getKey().compareToIgnoreCase(OpenSearchConstants.configNumOfResultsQName) == 0) {
				if(en.getValue().compareToIgnoreCase("unbounded") == 0)
					continue;
				try {
					Integer.parseInt(en.getValue());
				}catch(Exception e) {
					logger.error("Invalid configuration parameter format: numOfResults");
					throw new Exception("Invalid configuration parameter format: numOfResults", e);
				}
			}
			if(en.getKey().compareToIgnoreCase(OpenSearchConstants.configSequentialResultsQName) == 0) {
				if(en.getValue().compareToIgnoreCase("true") == 0)
					config.sequentialResults = true;
				else if(en.getValue().compareToIgnoreCase("false") == 0)
					config.sequentialResults = false;
				else {
					logger.error("Invalid configuration parameter format: sequentialResults");
					throw new Exception("Invalid configuration parameter format: sequentialResults");
				}
			}
		}
	}
	
	/**
	 * Selects the input and output encodings, checking for potential inconsistencies in the corresponding parameters specified by the client.
	 * If inputEncoding or outputEncoding are among the client's query parameters, the support of the requested encodings by the provired is ensured.
	 * If no inputEncoding or outputEncoding are specified by the client, the default values defined in the OpenSearch specification are
	 * implied only if the provider supports these encodings. Otherwise, the client should specify these encoding values.
	 * 
	 * @param dd The description document of the OpenSearch provider
	 * @return The selected input and output encodings
	 * @throws Exception
	 */
	private EncodingPair selectEncodings(DescriptionDocument dd) throws Exception {
		String selectedInputEncoding = null;
		String selectedOutputEncoding = null;
		List<String> inputEncodings = dd.getSupportedInputEncodings();
		List<String> outputEncodings = dd.getSupportedOutputEncodings();
		
		if(params.containsKey(OpenSearchConstants.inputEncodingQName)) {
			String requestedInputEncoding = params.get(OpenSearchConstants.inputEncodingQName);
			if(!dd.isInputEncodingSupported(requestedInputEncoding))
				throw new Exception("Requested input encoding is not supported by provider");
			selectedInputEncoding = requestedInputEncoding;
		}else {
			if(!inputEncodings.contains(dd.getDefaultInputEncoding()))
				throw new Exception("The providers description document does not support the default inputEncoding. The inputEncoding parameter should be contained in the query");
			else
				selectedInputEncoding = dd.getDefaultInputEncoding();
		}
		
		if(params.containsKey(OpenSearchConstants.outputEncodingQName)) {
			String requestedOutputEncoding = params.get(OpenSearchConstants.outputEncodingQName);
			if(!dd.isOutputEncodingSupported(requestedOutputEncoding))
				throw new Exception("Requested output encoding is not supported by provider");
			if(!Charset.isSupported(requestedOutputEncoding))
				throw new Exception("Requested output encoding is not supported");
		}else {
			if(!outputEncodings.contains(dd.getDefaultOutputEncoding()))
				throw new Exception("The providers description document does not support the default outputEncoding. The outputEncoding parameter should be contained in the query");
			else
				selectedOutputEncoding = dd.getDefaultOutputEncoding();
		}
		
		return new EncodingPair(selectedInputEncoding, selectedOutputEncoding);
	}
	
	/**
	 * Retrieves a list of transformation specifications that can be used to transform a page of search results obtained from an OpenSearch provider to 
	 * individual records of the desired schema.
	 * 
	 * @param dd The description document associated with the OpenSearch provider that is queried for results
	 * @param MimeTypes A list of MIME types for which transformation specifications will be retrieved
	 * @return A list of transformation specifications for all MIME types supplied, containing an XPathExpression to split the search result page into individual records,
	 * a Transformer to transform these individual records to the desired schema and, optionally an XPathExpression to extract an id from each individual record before
	 * transforming it
	 * @throws ParserConfigurationException
	 */
	private List<TransformationSpec> findTransformers(DescriptionDocument dd, List<String> MimeTypes) throws ParserConfigurationException {
	//	URL query = null;
		Transformer tr = null;
		XPathExpression recordSplitXpath = null;
		XPathExpression recordIdXpath = null;
		String MIMEType = null;
		Map<String, String> presentationInformation = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	//	DocumentBuilder builder = factory.newDocumentBuilder();
		
		List<TransformationSpec> specs = new ArrayList<TransformationSpec>();
		
		for(String MimeType : MimeTypes) {
	//		List<QueryBuilder> qbs = null;
			try {
				tr = resource.getTransformer(MimeType);
				recordSplitXpath = resource.getRecordSplitXPath(MimeType);
				recordIdXpath = resource.getRecordIdXPath(MimeType);
				presentationInformation = resource.getPresentationInformation(MimeType);
			//	qbs = dd.getExampleQueryBuilders(MimeType);
			}catch(Exception e) {
				logger.warn("Error while retrieving query builders for " + resource.getName() + " for mime type " + MimeType + " .Ignoring", e);
				continue;
			}
/*Issues example queries in order to leave out potential invalid transformation specs
 *Omitted for performance reasons
 */
//			query = null;
//			for(QueryBuilder qb: qbs) {
//				try {
//					query = new URL(qb.getQuery());
//				}catch(IncompleteQueryException iqe) {
//					List<String> unsetParams = qb.getUnsetParameters();
//					if(unsetParams.contains(OpenSearchConstants.countQName)) {
//						try {
//							qb.setParameter(OpenSearchConstants.countQName, "10");
//							query = new URL(qb.getQuery());
//						}catch(Exception ee) {
//							continue;
//						}
//						break;
//					}
//					else
//						continue;
//				}catch(Exception e) {
//					continue;
//				}
//				break;
//			}
//			
//			if(query == null && !qbs.isEmpty())
//				continue;
//			
//			if(query != null) {
//				try {
//					URLConnection conn = query.openConnection();
//					BufferedReader bIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//					InputSource is = new InputSource(bIn);
//					Document exQueryResponse = builder.parse(is);
//					
//					Object result = recordSplitXpath.evaluate(exQueryResponse, XPathConstants.NODESET);
//				    NodeList nodes = (NodeList) result;
//				    FileOutputStream fOut = new FileOutputStream("/dev/null"); //todo fix
//				    try {
//					    for(int i = 0; i < nodes.getLength(); i++) {
//					    	tr.transform(new DOMSource(nodes.item(i)), new StreamResult(fOut));
//					    }
//				    }finally {
//				    	fOut.flush(); fOut.close();
//				    }
//				}catch(Exception e) {
//					logger.warn("Transformation failed for type " + MimeType + ". Ignoring transformation.", e);
//					continue;
//				}
//			}
			MIMEType = MimeType;
			specs.add(new TransformationSpec(MIMEType, tr, recordSplitXpath, recordIdXpath, presentationInformation));
		}

		return specs;

	}
	
	/**
	 * Assigns the values of the parameters passed to this OpenSearchWorker to the respective parameters of all QueryBuilders contained in the list
	 * The searchTerms and encoding specification parameters are handled separately.
	 * All parameters values set will remain constant through queries refering to different search result pages, hence this operation is dubbed a preparation step
	 * 
	 * @param queryBuilders The list of QueryBuilders to assign parameter values to
	 * @param searchTerms The search terms of the query to be submitted
	 * @param inputEncoding The input encoding to be used in the search query
	 * @param outputEncoding The ouput encoding to be used while processing results obtained from a query response
	 * @throws Exception
	 */
	private void prepareQueryBuilders(List<QueryBuilder> queryBuilders, String searchTerms,  String inputEncoding, String outputEncoding) throws Exception {
		Map<String, String> ddParams = new HashMap<String, String>();
		
		logger.info("will evaluate the following params : " + params);
		
		for(Map.Entry<String, String> e : params.entrySet()) {
			String[] pEntries = e.getKey().split(":");
			if(pEntries.length > 2) {
				logger.warn("Malformed parameter: " + e.getKey() + ". Ignoring.");
				logger.warn("param has " + pEntries.length + " but expected 2");
				
				continue;
			}
			
			ddParams.put(e.getKey(), e.getValue());
		}
		
		for(QueryBuilder qb: queryBuilders) {
			if(qb.hasParameter(OpenSearchConstants.searchTermsQName)) 
				qb.setParameter(OpenSearchConstants.searchTermsQName, searchTerms);
			
			if(qb.hasParameter(OpenSearchConstants.inputEncodingQName))
				qb.setParameter(OpenSearchConstants.inputEncodingQName, inputEncoding);
			if(qb.hasParameter(OpenSearchConstants.outputEncodingQName))
				qb.setParameter(OpenSearchConstants.outputEncodingQName, outputEncoding);
			for(Map.Entry<String, String> e: ddParams.entrySet()) {
				if(qb.hasParameter(e.getKey()))
					qb.setParameter(e.getKey(), e.getValue());
			}
		
		}
	}
	
	/**
	 * Reorders the supplied list of query builders, resulting to a list where the first query builder best
	 * matches the query parameters contained in the query that is processed by this OpenSearchWorker.
	 * In general, if the query that is currently processed contains the same set of parameter as a query builder plus
	 * a number of extra parameters, this query builder is considered a match and the extra parameters will be ignored.
	 * Conversely, if the query parameters are not enough to construct a complete query, the query builder will be excluded from
	 * the final list.
	 * 
	 * @param queryBuilders The query builders to be reordered
	 * @param queryParams The query parameters that the query of this OpenSearchWorker contains
	 * @param queryTerms The query terms that the query of this OpenSearchWorker contains
	 * @return A new list containing the original query builders reordered to best match the search query, possibly excluding some
	 * query builders which cannot lead to complete queries
	 */
	private List<QueryBuilder> reorderQueryBuilders(List<QueryBuilder> queryBuilders, Collection<String> queryParams, String queryTerms) {
		final class MatchScore {
			public Integer id;
			public Integer score;
		}
		
		MatchScore[] matchScores = new MatchScore[queryBuilders.size()];
		for(int i = 0; i < queryBuilders.size(); i++) {
			matchScores[i] = new MatchScore();
			matchScores[i].id = i;
			matchScores[i].score = -1;
		}
		int i = 0;
		for(QueryBuilder qb: queryBuilders) {
			int matchScore = 0;
			List<String> reqParams = qb.getRequiredParameters();
			List<String> qbParams = new ArrayList<String>(reqParams);
			qbParams.addAll(qb.getOptionalParameters());
			List<String> tmpReqParams = new ArrayList<String>(reqParams);
			tmpReqParams.remove(OpenSearchConstants.searchTermsQName);
			tmpReqParams.remove(OpenSearchConstants.startIndexQName);
			tmpReqParams.remove(OpenSearchConstants.startPageQName);
			tmpReqParams.remove(OpenSearchConstants.countQName);
			tmpReqParams.removeAll(queryParams);
			if(!tmpReqParams.isEmpty()) {
				i++;
				continue;
			}
			for(String param: qbParams) {
				if(queryParams.contains(param))
					matchScore++;
			}
			matchScores[i].score = matchScore;
			i++;
		}
		List<QueryBuilder> qbs = new ArrayList<QueryBuilder>();
		Arrays.sort(matchScores, new Comparator<MatchScore>() {
			public int compare(MatchScore a, MatchScore b) {
				if(a.score.equals(b.score))
					return 0;
				else if(a.score < b.score)
					return 1;
				else return -1;
			}
		});
		for(MatchScore entry : matchScores) {
			if(entry.score < 0)
				break;
			qbs.add(queryBuilders.get(entry.id));
		}
		return qbs;
	}
	
	private TransformationSpec switchContext(Iterator<TransformationSpec> tsIt, DescriptionDocument dd, Pager pager, String searchTerms, EncodingPair encodings) {
		TransformationSpec ts;
		List<QueryBuilder> qbs = null;
		while(tsIt.hasNext()) {
			ts = tsIt.next();
			try {
				qbs = dd.getQueryBuilders("results",ts.MimeType);
				qbs = reorderQueryBuilders(qbs, params.keySet(), terms);
				prepareQueryBuilders(qbs, searchTerms, encodings.inputEncoding, encodings.outputEncoding);
			}catch(Exception ee) {
				logger.warn("Unable to formulate query for " + resource.getName() + " MIME Type: " + ts.MimeType + " Page " + pager.getCurrPage() + " after trying all alternatives.");
				continue; //If a new context cannot be set, select the next available context
			}
			pager.setContext(qbs, ts.MimeType);
			return ts;
		}
		return null;
	}
	
	private Map<String, XPathExpression> compileXPathExpressions(Map<String, String> presentationInformation) throws XPathExpressionException {
		Map<String, XPathExpression> compiledXPaths = new HashMap<String, XPathExpression>();
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		for(Map.Entry<String, String> presentable : presentationInformation.entrySet()) {
			compiledXPaths.put(presentable.getKey(), xpath.compile(presentable.getValue()));
		}
		return compiledXPaths;
	}
	
	private boolean appendToResultSet(String record, String recId, Map<String, XPathExpression> presentationInformation, 
			Map<String, Integer> positionInformation) throws GRS2WriterException, XPathExpressionException{
		
		
		logger.info("record                  : " + record);
		logger.info("presentationInformation : " + presentationInformation);
		logger.info("positionInformation     : " + positionInformation);
		
		GenericRecord rec = new GenericRecord();
	//	StringField contentField = new StringField();
	//	contentField.SetStringPayload(record);
	//	rec.AddField(contentField);
		Field[] fields = new Field[presentationInformation.keySet().size()+1];
		if(recId != null && recId.trim().compareTo("") != 0) {
	//		StringField idField = new StringField();
	//		idField.SetStringPayload(recId);
	//		rec.AddField(idField);
			fields[0] = new StringField(recId);
		}else
			fields[0] = new StringField(null);
		
		for(Map.Entry<String, XPathExpression> presentable : presentationInformation.entrySet()) {
			
			logger.info("fieldName : " + presentable.getKey());
			
			
			StringBuilder f = new StringBuilder();
			NodeList nl = (NodeList)presentable.getValue().evaluate(new InputSource(new StringReader(record)), XPathConstants.NODESET);
			
			logger.info("field number of nodes found in record : " + nl.getLength());
			
			for(int i = 0; i < nl.getLength(); i++) {
				Node child = nl.item(i).getFirstChild();
				if(child != null) f.append(child.getNodeValue() + " ");
			}
			int pos = positionInformation.get(presentable.getKey());
			
			logger.info("field pos : " + pos);
			logger.info("field payload : " + f);
			
			fields[pos] = new StringField(f.toString());
		}
		rec.setFields(fields);
		if(writer.getStatus() == Status.Close || writer.getStatus() == Status.Dispose) {
			logger.info("Consumption stopped by consumer side. Stopping.");
			return false;
		}
		
		if(writer.put(rec, 60, TimeUnit.SECONDS) == false) {
			if(writer.getStatus() != Status.Open)
				logger.info("Consumption stopped by consumer side. Stopping.");
			else
				logger.warn("Consumer timed out");
			return false;
		}
		return true;
	//	if(writer.p)
	//	boolean success;
	//	synchronized(synchWriter) {
		
	//	}
	}
	
	private void emitProgressiveEvent(int count) {
		try { writer.emit(new KeyValueEvent(OpenSearchConstants.RESULTSNO_EVENT, ""+count)); }
		catch(Exception e) { logger.warn("Resource " + resource.getName() + " could not emit progressive result count event with value" + count); }
	}
	
	private void emitFinalEvent(int count) {
		try {  writer.emit(new KeyValueEvent(OpenSearchConstants.RESULTSNOFINAL_EVENT, ""+count));}
		catch(Exception e) { logger.warn("Resource " + resource.getName() + " could not emit final result count event with value" + count); }
		emittedFinalEvent = true;
	}

	private void handleBrokerResultCountEvents(int count) {
		if(emitProgressiveEvents == false) {
			if(resource == topResource)
				emitFinalEvent(count);
			else {
				synchronized(synchFinalResultCount) {
					finalResultCount = count;
					synchFinalResultCount.notifyAll();
				}
			}
		}else {
			synchronized(synchFinalResultCount) {
				finalResultCount = -1;
				synchFinalResultCount.notifyAll();
			}
		}
	}
	private void handlePageResultCountEvents(int pageNo, int recordsAppended, OpenSearchResponse response) {
		if(emittedFinalEvent == true) return;
		if(emitProgressiveEvents != null && emitProgressiveEvents == true) {
			if(resource.isBrokered() == true) return;
			int totalRes;
			if(config.sequentialResults == false || (resource == topResource && resource.isBrokered() == false)) totalRes = totalResultCount.get();
			else {
				synchronized(synchInt) {
					totalRes = totalResultCount.get();
				}
			}
			try { emitProgressiveEvent(totalRes); }
			catch(Exception e) { logger.warn("Resource " + resource.getName() + " could not emit progressive result count event"); }
		}
		
		if(resource != topResource) {
			//All checks below are performed only in the first page of the results returned by a provider
			if(resource.isBrokered() == false) {
				if(pageNo == 1) { //The first page potentially contains the total result count
					if(config.sequentialResults == false) { 
						//non-brokered providers queried in multithreaded mode do not emit any events and return their results to broker
						synchronized(synchFinalResultCount) {
							if(response.getTotalResults() != null) {
								int count = response.getTotalResults();
								if(noLimit == false && resultLimit < count) count = resultLimit;
								finalResultCount = count;
							}
							else {
								finalResultCount = -1; 
								int totResults;
								synchronized(synchInt) {
									totResults = totalResultCount.get();
								}
								emitProgressiveEvent(totResults); //switch to progressive event mode
								emitProgressiveEvents = true;
							}
							synchFinalResultCount.notifyAll();
						}
					}else { 
						//non-brokered providers queried in sequential mode will emit one progressive event each
						if(response.getTotalResults() != null) {
							int seqTotal = totalResultCount.get() - recordsAppended + response.getTotalResults();
							if(resultLimit < seqTotal) emitFinalEvent(resultLimit);
							else { 
								emitProgressiveEvent(seqTotal);
								
							}
						}else {
							emitProgressiveEvent(totalResultCount.get()); //no synch needed for sequential mode
							emitProgressiveEvents = true;
						}
					}
				}
			}
		}else {
			if(resource.isBrokered() == false) {
				if(pageNo == 1) {
					if(response.getTotalResults() != null) {
						int count = response.getTotalResults();
						if(noLimit == false && resultLimit < count) count = resultLimit;
						emitFinalEvent(count);
					}
					else {
						emitProgressiveEvents = true;
						emitProgressiveEvent(totalResultCount.get()); //no synch needed for single non-brokered provider
					}
				}
			}
		}
	}
	
	private void writerCleanup() {
	//	synchronized(synchWriter) {
	//		writer.Flush(); 
		try {
			if(resource == topResource)
				writer.close();
			}catch(GRS2WriterException e) {
				logger.warn("Top resource " + resource.getName() + " was unable to close writer");
			}
	//	}
	//	logger.info(resource.getName() + " flushed " + (resource == topResource ? "and closed" : "") + " writer");
	}
	
	private void enrichRecordNamespaces(Document response, Element record) {
		NamedNodeMap attrs = response.getDocumentElement().getAttributes();
		for(int i = 0; i < attrs.getLength(); i++) {
			String attrName = attrs.item(i).getNodeName();
			if(attrName.startsWith("xmlns:")) {
				if(!record.hasAttribute(attrName)) {
					try {
						record.setPrefix(attrName.substring(attrName.indexOf(":")+1));
						record.setAttribute(attrName, attrs.item(i).getNodeValue());
					}catch(Exception e) { }
				}
			}
		}
	}
	
	/**
	 * Performs the operations needed to obtain the requested search results.
	 * After analyzing the description document corresponding to the provider described in the OpenSearchResource that is being processed,
	 * search queries are issued to obtain the results page by page. At each step the results are transformed using a transformation
	 * specification described in the OpenSearchResource, matching one of the result MIME types that the provider supports.
	 * If the provider acts as a broker, the OpenSearchResource objects of the brokered providers are retrieved and are used to obtain
	 * the actual results from the brokered providers, either sequentially or concurrently depending on configuration
	 * 
	 */
	public void run() {
		//System.out.println("Thread: " + Thread.currentThread().toString());
		try {
			//System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.132 Safari/537.36");
			
			long start = Calendar.getInstance().getTimeInMillis();
			DocumentBuilder docBuilder = null;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			docBuilder = docFactory.newDocumentBuilder();
			
			FactoryResolver.initialize(queryNamespaces, config.factories);
			FactoryPair factories = FactoryResolver.getFactories();
			List<String> brokeredDDUrls = null;
			String terms = this.terms;
			Map<String, String> params = this.params;
			Map<String, XPathExpression> compiledXPathExpressions = null;
			
			//If resource corresponds to a brokered provider, take fixed parameters into account
			if(resource == topResource) {
				if(!fixedTerms.equals(""))
					terms = fixedTerms;
				params.putAll(fixedParams);
				brokeredDDUrls = new ArrayList<String>();
			}
			
			int clientStartPage = -1;
			int clientStartIndex = -1;
			int clientCount = -1;
			
			logger.info("params : " + params);
			
			if(this.params.containsKey(OpenSearchConstants.startPageQName)) clientStartPage = Integer.parseInt(this.params.get(OpenSearchConstants.startPageQName));
			if(this.params.containsKey(OpenSearchConstants.startIndexQName)) clientStartIndex = Integer.parseInt(this.params.get(OpenSearchConstants.startIndexQName));
			if(this.params.containsKey(OpenSearchConstants.countQName)) clientCount = Integer.parseInt(this.params.get(OpenSearchConstants.countQName));
			
			logger.info("Parameters from client : ");
			logger.info("startPage        : " + clientStartPage);
			logger.info("clientStartIndex : " + clientStartIndex);
			logger.info("clientCount      : " + clientCount);
			logger.info("------------------------");
			logger.info("params           : " + params);
			logger.info("fixedParams      : " + fixedParams);
			
			DescriptionDocument dd = new DescriptionDocument(resource.getDescriptionDocument(), factories.urlElFactory, factories.queryElFactory);
			parseConfigParams(params);
			
			if(resource == topResource) {	
				//top resource does not participate into any race conditions, so resultsRemaining and totalResultCount don't require synchronization
				totalResultCount.set(0);
				if(params.containsKey(OpenSearchConstants.configNumOfResultsQName)) {
					if(params.get(OpenSearchConstants.configNumOfResultsQName).compareToIgnoreCase("unbounded") == 0) {
						resultsRemaining.set(Integer.MAX_VALUE);
						noLimit = true;
					}
					else {
						resultLimit = Integer.parseInt(params.get(OpenSearchConstants.configNumOfResultsQName));
						resultsRemaining.set(resultLimit);
					}
				}
				else {
					resultsRemaining.set(Integer.MAX_VALUE);
					noLimit = true;
				}
			}
			
			if(dd.canRequest() == false) {
				writerCleanup();
				return;
			}
			
			int currResultsRemaining;
			synchronized (synchInt) {
				currResultsRemaining = resultsRemaining.get();
			}
		
			if(currResultsRemaining <= 0) {
				writerCleanup();
				return;
			}
			
			EncodingPair encodings = selectEncodings(dd);
			List<String> MimeTypes = resource.getTransformationTypes();
			MimeTypes.retainAll(dd.getSupportedMimeTypes("results"));
			if(MimeTypes.isEmpty()) {
				writerCleanup();
				return;
			}
		
			//System.out.println(MimeTypes);
			List<TransformationSpec> tss = findTransformers(dd, MimeTypes);
			////////lists transformers - remove this
			//for(TransformationSpec ts : tss)
			//	System.out.println(ts.MimeType.toString() + " " + ts.transformer.toString());
			/////////
			
			if(tss.isEmpty()) {
				logger.warn("Could not find a tranformation specification matching with the MIME types exposed by the description document.");
				writerCleanup();
				return;
			}
			
			Map<String, Integer> positionInformation = initializeWriter(tss.get(0).presentationInformation); //TODO all presentation information contain the same presentable fields
			
			String searchTerms = URLEncoder.UrlEncode(terms, "UTF-8");
			
			OpenSearchResponse response = null;
			
			URL pageQuery;
			Pager pager;
			synchronized(synchInt) {
					//pager = new Pager(resultsRemaining.get(), config.resultsPerPage, resource.getName(), clientStartPage, clientStartIndex, clientCount);
					if (clientCount > 0)
						pager = new Pager(resultsRemaining.get(), clientCount, resource.getName(), clientStartPage, clientStartIndex, clientCount);
					else
						pager = new Pager(resultsRemaining.get(), config.resultsPerPage, resource.getName(), clientStartPage, clientStartIndex, clientCount);
				
			}
			Iterator<TransformationSpec> tsIt = tss.iterator();
			TransformationSpec ts = tsIt.next();
			compiledXPathExpressions = compileXPathExpressions(ts.presentationInformation);
			List<QueryBuilder> qbs = dd.getQueryBuilders("results", ts.MimeType);
			qbs = reorderQueryBuilders(qbs, params.keySet(), terms);
			prepareQueryBuilders(qbs, searchTerms, encodings.inputEncoding, encodings.outputEncoding);
			pager.setContext(qbs, ts.MimeType);
			NodeList recordNodes = null;
			int pageNo = 0;
			int resultCount = 0;
			boolean stoppedByConsumer = false;
			do {		
				if(resource.isBrokered() == false && noLimit == false) {
					//Do not continue querying if the desired number of requested results is already retrieved
					synchronized(synchInt) {
						if(resultsRemaining.get() <= 0) {
							logger.info(resource.getName() + ": The number of requested results has been retrieved. Stopping.");
							break;
						}
					}
				}
				try {
					pageQuery = pager.getPageQuery();
					pageNo++;
					logger.info("Issuing query to " + resource.getName() + " for page #" + pageNo + ": " + pageQuery);
				}catch(Exception e) {
					if((ts = switchContext(tsIt, dd, pager, searchTerms, encodings))  != null) {
						compiledXPathExpressions = compileXPathExpressions(ts.presentationInformation);
						continue; //Successfully switched to another context, retry query
					}
					else {
						logger.error("Failed to retrieve results from " + resource.getName(), e);
						break; //Finished available context lookup, result retrieval prodedure will halt
					}
				}
		
				MimeType MIMEType;
				InputStream responseStream = null;
				
				logger.info("creating http client... : " + pageQuery.toString());
				try  {
					HttpClient client = new DefaultHttpClient();
					
					logger.info("creating http client...OK");
					//logger.info("will hit url : " + pageQuery.toString());
					//logger.info("will hit url : " + pageQuery.toURI().toASCIIString());
					
					HttpGet request = new HttpGet(pageQuery.toString());
					
					logger.info("executing query...");
					HttpResponse httpResponse = client.execute(request);
					logger.info("executing query...OK");
					responseStream = httpResponse.getEntity().getContent();
					
//					URLConnection con = pageQuery.openConnection();
//					con.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.132 Safari/537.36");
//		            con.addRequestProperty("Accept", "*/*");
//		            con.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
//					
//					responseStream = con.getInputStream();
					MIMEType = new MimeType(ts.MimeType);
					
					logger.info("mime  : " + MIMEType.getPrimaryType());
					
					if((MIMEType.getPrimaryType().compareTo("text") == 0 && MIMEType.getSubType().compareTo("html") == 0) ||
							(MIMEType.getPrimaryType().compareTo("application") == 0 && MIMEType.getSubType().compareTo("xhtml+xml") == 0))
						response = new HTMLResponse(responseStream, encodings.outputEncoding, dd.getURIToPrefixMappings());
					else
						response = new XMLResponse(responseStream, factories.queryElFactory, pager.getQueryBuilder(), encodings.outputEncoding, dd.getURIToPrefixMappings());
				
					
//					URLConnection con2 = pageQuery.openConnection();
//					con2.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.132 Safari/537.36");
//		            con2.addRequestProperty("Accept", "*/*");
//		            con2.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
//					
//					
//					String myString = IOUtils.toString(con2.getInputStream(), "UTF-8");
//					logger.info("all response was : " + myString);
					
					
				}catch(Exception e) {
					logger.warn("Error while parsing response. Ignoring transformation.", e);
					if((ts = switchContext(tsIt, dd, pager, searchTerms, encodings))  != null) {
						compiledXPathExpressions = compileXPathExpressions(ts.presentationInformation);
						continue; //Successfully switched to another context, retry query
					}
					else {
						logger.error("Failed to retrieve results from " + resource.getName(), e);
						break; //Finished available context lookup, result retrieval prodedure will halt
					}
				}finally {
					try {
						if(responseStream != null)
							responseStream.close();
					}catch(Exception e) {
						logger.warn("Could not close query response stream");
					}
				}
				
				try {
					Object result = ts.recordSplitXpath.evaluate(response.getResponse(), XPathConstants.NODESET);
				    recordNodes = (NodeList) result;
				    for(int i = 0; i < recordNodes.getLength(); i++)
				    	enrichRecordNamespaces(response.getResponse(), (Element)recordNodes.item(i));
				}catch(Exception e) {
					logger.warn("XPath evaluation error while extracting records from " + resource.getName() + " MIME Type: " + ts.MimeType, e);
					if((ts = switchContext(tsIt, dd, pager, searchTerms, encodings))  != null) {
						compiledXPathExpressions = compileXPathExpressions(ts.presentationInformation);
						continue; //Successfully switched to another context, retry query
					}
					else {
						logger.error("Failed to retrieve results from " + resource.getName(), e);
						break; //Finished available context lookup, result retrieval prodedure will halt
					}
				}
				
				int recordsToAppend;
				try {
					synchronized(synchInt) {
						if(resource.isBrokered() == false && resultsRemaining.get() < recordNodes.getLength())
							recordsToAppend = resultsRemaining.get() >=0 ? resultsRemaining.get() : 0;
						else
							recordsToAppend = recordNodes.getLength();
					}
					
					if(resource.isBrokered() == false && noLimit == false && recordNodes.getLength() != 0 && recordsToAppend == 0) {
						logger.info(resource.getName() + ": The number of requested results has been retrieved. Discarding results of page #" + pageNo);
						break;
					}
					else
						logger.info(resource.getName() + ": Current page= #" + pageNo +". Retrieved " + recordsToAppend + " results");
					resultCount += recordsToAppend;
//	Prints out xpath-separated xml records
//					for(int i = 0; i < recordsToAppend; i++) {
//						TransformerFactory ttf = TransformerFactory.newInstance();
//						Transformer ttr = ttf.newTransformer();
//					System.out.println("#" + i+ " record is: ");
//						ttr.transform(new DOMSource(recordNodes.item(i)), new StreamResult(System.out));
//						
//					}
					
					for(int i = 0; i < recordsToAppend; i++) {						
						String recId = null;
						try {
							if(ts.recordIdXpath != null) {
								Document doc = docBuilder.newDocument();
								Node dup = doc.importNode(recordNodes.item(i), true);
								doc.appendChild(dup);
								recId = ts.recordIdXpath.evaluate(doc);
								recId = XMLUtils.DoReplaceSpecialCharachters(recId);
							}
						}catch(Exception e) {
							logger.warn("XPath evaluation error while extracting record ids from " + resource.getName() + " MIME Type: " + ts.MimeType, e);
						}
						StringWriter transformed = new StringWriter();
//  Prints out transformed records				
//						TransformerFactory ttf = TransformerFactory.newInstance();
//						Transformer ttr = ttf.newTransformer();
//						ttr.transform(new DOMSource(recordNodes.item(i)), new StreamResult(System.out));
						
						ts.transformer.transform(new DOMSource(recordNodes.item(i)),  new StreamResult(transformed));
						if(resource.isBrokered() == false) {
							if(noLimit == false) {
								boolean stop = false;
								synchronized(synchInt) {
									if(resultsRemaining.get() == 0)
										stop = true;
									else
										resultsRemaining.set(resultsRemaining.get() - 1);
								}
								if(stop == true) {
									logger.info(resource.getName() + ": The number of requested results has been retrieved. Discarding results of page #" + pageNo + " after #" + i);
									break;
								}
							}
							try {
								handlePageResultCountEvents(pageNo, recordsToAppend, response);
								
								if(appendToResultSet(transformed.toString(), recId, compiledXPathExpressions, positionInformation) == false) {
									stoppedByConsumer = true;
									break;
								}
							}catch(GRS2WriterException e) {
								logger.warn("Could not write record", e);
							}
						//	System.out.println(transformed.toString());
						}
						else {
							try {
								new URL(transformed.toString());
							}catch(Exception e) {
								logger.warn("Malformed Url for brokered provider # " + i + ". Ignoring");
								continue;
							}
							brokeredDDUrls.add(transformed.toString());
						}			
					}
				}catch(TransformerException e) {
					logger.warn("Transformation error while transforming results from  " + dd.getShortName() + " MIME Type: " + ts.MimeType, e);
					if((ts = switchContext(tsIt, dd, pager, searchTerms, encodings))  != null)
						continue; //Successfully switched to another context, retry query
					else {
						logger.error("Failed to retrieve results from " + resource.getName(), e);
						break; //Finished available context lookup, result retrieval prodedure will halt
					}
				}
				
				//synchronized(synchInt) {
				//	if(resource.isBrokered() == false && noLimit == false)
				//		resultsRemaining.set(resultsRemaining.get() - recordsToAppend);		
				//}

				logger.info("recordNodes.getLength() : " + recordNodes.getLength());
				
				if(stoppedByConsumer == true)
					break;
				pager.next(response, recordNodes.getLength()); 
			
			}while(pager.hasNext());
			
			if(resource.isBrokered() == false) {
			//	if(noLimit == false)
			//		resultsRemaining.set(resultRemaining.get() - pager.getCurrentPageResultCount();
				writerCleanup();
				logger.info(resource.getName() + ": Results retrieved: " + resultCount);
				synchronized(synchInt) {
					totalResultCount.set(totalResultCount.get() + resultCount);
					long end = Calendar.getInstance().getTimeInMillis();
					if(resource == topResource) {
						float productionRate = (float)totalResultCount.get()/(float)((end - start))*1000;
						logger.info("Top resource " + resource.getName() + ": Total results retrieved: " + totalResultCount.get());
						logger.info("Production rate was: " + productionRate + " records per second");
						logger.info("Production rate per provider was: " + productionRate + " records per second");
					}
				}
				return; 
			}
			else {
				if(stoppedByConsumer == false) {
					//System.out.println(brokeredDDUrls);
					if(config.sequentialResults == true) {
						OpenSearchResource oldResource = resource;
						for(int i = 0; i < brokeredDDUrls.size(); i++) {
							//System.out.println(brokeredDDUrls.get(i));
							OpenSearchResource brokeredResource;
							try {
								brokeredResource = resources.get(brokeredDDUrls.get(i));
							}catch(Exception e) {
								logger.warn("Could not retrieve brokered resource with DD URL: " + brokeredDDUrls.get(i) + ". Ignoring resource", e);
								resultCount--;
								continue;
							}
							if(brokeredResource == null) {
								logger.warn("Missing brokered resource with DD URL: " + brokeredDDUrls.get(i) + " . Ignoring resource");
								resultCount--;
								continue;
							}
							resource = brokeredResource;
							emitProgressiveEvents = false; //Reset result count check
							run();
							synchronized (synchInt) {
								if(resultsRemaining.get() <= 0 && noLimit == false)
									break;
							}
						}
						resource = oldResource;
					}
					else if(brokeredDDUrls.size() > 0){
						//System.out.println(brokeredDDUrls);
						List<OpenSearchWorker> workers = new ArrayList<OpenSearchWorker>();
						ExecutorService es = Executors.newFixedThreadPool(brokeredDDUrls.size());
						ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
						for(int i = 0; i < brokeredDDUrls.size(); i++) {
							OpenSearchResource brokeredResource;
							try {
								brokeredResource = resources.get(brokeredDDUrls.get(i));
							}catch(Exception e) {
								logger.warn("Could not retrieve brokered resource with DD URL: " + brokeredDDUrls.get(i) + ". Ignoring resource", e);
								resultCount--;
								continue;
							}
							if(brokeredResource == null) {
								logger.warn("Missing brokered resource with DD URL: " + brokeredDDUrls.get(i) + " . Ignoring resource");
								resultCount--;
								continue;
							}
							OpenSearchWorker worker = new OpenSearchWorker(brokeredResource, topResource, resources, config, this.terms, this.params, fixedTerms, fixedParams, queryNamespaces,
									resultsRemaining, totalResultCount, noLimit, writer, outLocator, synchWriter, synchInt);
							workers.add(worker);
							tasks.add(Executors.callable(worker));
						}
						
						List<Future<Object>> futures = es.invokeAll(tasks);
						int finalResultCountSum = 0;
						for(OpenSearchWorker worker : workers) {
							Integer frc = worker.getFinalResultCount();
							if(frc == null) {
								emitProgressiveEvents = true;
								break;
							}
							finalResultCountSum += frc;
						}
						handleBrokerResultCountEvents(finalResultCountSum);
	
						for(Future<Object> f : futures) {
							try {
								f.get();
							}catch(ExecutionException ee) {
								logger.error("Brokered result retrieval failure: " + ee.getCause());
							}
						}
						es.shutdown();
					}
				}
				writerCleanup();
				logger.info("Total brokered providers retrieved: " + resultCount);
				synchronized(synchInt) {
						logger.info("Brokered " + ((resource == topResource) ? "Top " : "") + "resource " + resource.getName() + 
								": Total results retrieved: " + totalResultCount.get());
						
						if(resource == topResource) {
							long end = Calendar.getInstance().getTimeInMillis();
							float productionRate = (float)totalResultCount.get()/(float)((end - start))*1000;
							logger.info("Production rate was: " + productionRate + " records per second");
							if(resultCount != 0)
								logger.info("Production rate per provider was: " + productionRate/resultCount + " records per secord");
						}
				}
				
				return;
			}
		}catch(Exception e) {
			logger.error("Error while retrieving results" + (resource != null ? " from " + resource.getName() : "") + ". Stopping.", e);
			writerCleanup();
			return;
		}
	}
	
}
