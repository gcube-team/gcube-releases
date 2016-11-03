package org.gcube.opensearch.opensearchoperator;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.opensearch.opensearchlibrary.DescriptionDocument;
import org.gcube.opensearch.opensearchlibrary.query.IncompleteQueryException;
import org.gcube.opensearch.opensearchlibrary.query.NonExistentParameterException;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.queryelements.BasicQueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.BasicURLElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.ExtendedURLElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;
import org.gcube.opensearch.opensearchlibrary.utils.FactoryResolver;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResourceCache;
import org.gcube.opensearch.opensearchoperator.resource.ISResourceRepository;
import org.gcube.opensearch.opensearchoperator.resource.LocalOpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.LocalResourceRepository;
import org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource;
import org.gcube.opensearch.opensearchoperator.resource.ResourceRepository;

/**
 * Operator class used to query and obtain results from an external OpenSearch provider identified by
 * an OpenSearchResource. An OpenSearch provider can be either direct or brokered. In the second case,
 * the top provider is used to obtain a list of providers which will provide the actual results. The
 * brokered providers are also described by their OpenSearchResource objects, which are automatically
 * retrieved using a ResourceRepository. The search results undergo a transformation step using a 
 * suitable transformation specification that is contained as well in the OpenSearchResource.
 * The transformation allows the operator to split a search result page to individual records and
 * to transform these records to the desired schema, optionally adding an id field extracted from the data.
 * The {@link OpenSearchOp#query(String)} method returns an IProxyLocator identifying the ResultSet
 * which contains GenericRecords comprised of one or (optionally) two StringFields, with the first
 * containing the record content and the second containing the record id. 
 * 
 * @author gerasimos.farantatos
 *
 */
public class OpenSearchOp {

	private Logger logger = LoggerFactory.getLogger(OpenSearchOp.class.getName());
	private OpenSearchResource resource;
	private ResourceRepository resources;
	private OpenSearchOpConfig config;
	private String[] fixedParams;
	private EnvHintCollection envHints = null;
	
	/**
	 * Creates a new OpenSearchOp using the default configuration specified in OpenSearchOpConfig.
	 * 
	 * @param resource The OpenSearch Resource identifying an OpenSearch provider and the transformations necessary to obtain the results
	 * @param fixedParams A set of pre-fixed OpenSearch parameters that override user parameters and are to be used while querying a broker. 
	 * The user parameters are used while querying the retrieved OpenSearch providers.
	 * @param envHints An Environment Hint collection to be passed to the InformationSystem, if it is available.
	 * @throws Exception In case of error
	 */
	public OpenSearchOp(OpenSearchResource resource, String[] fixedParams, EnvHintCollection envHints) throws Exception {
		this.resource = resource;
		this.config = new OpenSearchOpConfig();
		this.envHints = envHints;
		this.fixedParams = fixedParams;
		if(this.config.useLocalResourceRepository == false) {
			ISOpenSearchResourceCache cache = config.ISCache == null ? new ISOpenSearchResourceCache() : config.ISCache;
			this.resources = new ISResourceRepository(cache, envHints);
		}
		else {
			this.resources = new LocalResourceRepository(new File(config.schemaPath));
			((LocalResourceRepository)resources).add(config.dirPath);
		}
	}
	
	/**
	 * Creates a new OpenSearchOp using the configuration specified in the respective argument.
	 * In addition to using an InformationSystem to retrieve OpenSearch resources, a non-IS local mode
	 * is supported, mainly for the purpose of using the operator in the absence of an InformationSystem.
	 * 
	 * @param resource The OpenSearch Resource identifying an OpenSearch provider and the transformations necessary to obtain the results
	 * @param fixedParams A set of pre-fixed OpenSearch parameters that override user parameters and are to be used while querying a broker. 
	 * The user parameters are used while querying the retrieved OpenSearch providers.
	 * @param config The configuration to be used
	 * @param envHints An Environment Hint collection to be passed to the InformationSystem, if it is available.
	 * @throws Exception In case of error
	 */
	public OpenSearchOp(OpenSearchResource resource,  String[] fixedParams, OpenSearchOpConfig config,  EnvHintCollection envHints) throws Exception {
		this.resource = resource;
		this.config = config;
		this.envHints = envHints;
		this.fixedParams = fixedParams;
		if(this.config.useLocalResourceRepository == false) {
			ISOpenSearchResourceCache cache = config.ISCache == null ? new ISOpenSearchResourceCache() : config.ISCache;
			this.resources = new ISResourceRepository(cache, envHints);
		}
		else {
			this.resources = new LocalResourceRepository(new File(config.schemaPath));
			((LocalResourceRepository)resources).add(config.dirPath);
		}
	}
	
	/**
	 * Performs a query in order to obtain results from an OpenSearch provider
	 * 
	 * @param queryString The operator query, in the form of <code>qualified OpenSearch parameter, value</code> pairs, where value should
	 * be double-quoted.
	 * The qualified OpenSearch parameters consist of the parameter names prefixed by their URl-encoded URI namespace.
	 * The namespace prefix should also be present in the case of standard OpenSearch parameters such as <code>searchTerms</code>.
	 * Some configuration parameters of the operator are exposed through the query interface, namely sequentialResults and
	 * numOfResults. In that case, their name should be prefixed by the <code>config</code> prefix, e.g. <code>config:numOfResults="100"</code>
	 * @return The locator of the query output
	 * @throws Exception In case of error while creating an {@link OpenSearchWorker} to handle the request
	 */
	public URI query(String queryString) throws Exception {
		try{
			long start = Calendar.getInstance().getTimeInMillis();
			logger.info("OpenSearch operator received query: " + queryString);
			
			Object synchLocator = new Object();
			QueryParser parser = new QueryParser(queryString, fixedParams);
			FactoryResolver.initialize(parser.getNamespaces(), config.factories);
			OpenSearchWorker osw = new OpenSearchWorker(resource, resources, config, parser.getTerms(), parser.getParams(), parser.getFixedTerms(), parser.getFixedParamsMap(), parser.getNamespaces(), synchLocator);
			Thread worker = new Thread(osw);
			worker.start();
			URI outLocator = null;
			synchronized(synchLocator) {
				 while((outLocator= osw.getLocator()) == null)
					 synchLocator.wait();
			}

			long initStop = Calendar.getInstance().getTimeInMillis();
			logger.info("Time to initialize: " + (initStop - start));
			return outLocator;
		}catch(Exception e){
			logger.error("Could not start background process. Throwing Exception", e);
			throw new Exception("Could not start background process.");
		}
	}

	public static void main(String[] args) throws Exception {
		OpenSearchResource res = new LocalOpenSearchResource(new File(System.getenv("HOME")+"/workspace/OpenSearch_Library/Resources/Nature.xml"), null);
		EnvHintCollection hints = new EnvHintCollection();
		OpenSearchOp op = new OpenSearchOp(res, new String[]{}, new EnvHintCollection());
		op.test(res);
	}
		
	//TODO: remove
	public void test(OpenSearchResource res) throws Exception {
	
		URLElementFactory urlFactory = new ExtendedURLElementFactory(new BasicURLElementFactory());
		QueryElementFactory queryFactory = new BasicQueryElementFactory();
		DescriptionDocument parser = new DescriptionDocument(res.getDescriptionDocument(), urlFactory, queryFactory);
		
		System.out.println("Name = " + res.getName());
		System.out.println("Brokered = " + res.isBrokered());
		System.out.println("Secure = " + res.isSecure());
		System.out.println("text/html transformer: " + res.getTransformer("text/html"));
		System.out.println("Can request = " + parser.canRequest());
		System.out.println("Can send to end users: " + parser.canSendToEndUsers());
		System.out.println("Can send to other clients: " + parser.canSendToClients());
		System.out.println("ShortName: " + parser.getShortName());
		System.out.println("LongName: " + parser.getLongName());
		System.out.println("Contact: " + parser.getContact());
		System.out.println("Description: " + parser.getDescription());
		System.out.println("Developer: " + parser.getDeveloper());
		URI imageURI = parser.getImageURI();
		if(imageURI != null)
			System.out.println("Image URI: " + parser.getImageURI().toString());
		System.out.println("Syndication Right: " + parser.getSyndicationRight());
		System.out.println("Attribution: " + parser.getAttribution());
		System.out.println("Tags: " + parser.getTags());
		System.out.println("Language restriction: " + parser.hasLangRestriction());
		System.out.println("Languages supported: " + parser.getSupportedLanguages());
		System.out.println("Input encodings supported: " + parser.getSupportedInputEncodings());
		System.out.println("Output encodings supported: " + parser.getSupportedOutputEncodings());
	    System.out.println("Result MIME types supported: " + parser.getSupportedMimeTypes("results"));
	    
		System.out.println("-----------------------------------");
		List<QueryBuilder> qb = parser.getQueryBuilders("results", "application/atom+xml");
		System.out.println("Query builders: " + qb);
		System.out.println(qb.get(0).getRequiredParameters());
		System.out.println(qb.get(0).getOptionalParameters());
		qb.get(0).setParameter("searchTerms", "earth");
		qb.get(0).setParameter("count", "100").setParameter("startIndex", "5");
		System.out.println(qb.get(0).getQuery());
		
		List<QueryBuilder> exq = parser.getExampleQueryBuilders("application/atom+xml");
		System.out.println(exq.size() + " Example queries: " + exq);
		for(int i = 0; i < exq.size(); i++) {
			try {
				System.out.println("Example query: " + exq.get(i).getQuery());
				exq.get(i).setParameter("startPage", "2");
			}catch(IncompleteQueryException e) {
				System.out.println(">>>Caught incomplete query exception<<<");
				System.out.println("Unset parameters: " + exq.get(i).getUnsetParameters());
				exq.get(i).setParameter(exq.get(i).getUnsetParameters().get(0), "10");
				System.out.println("Example query: " + exq.get(i).getQuery());
			}
			catch(NonExistentParameterException npe) {
				System.err.println(npe);
				continue;
			}

			URL exampleResults = new URL(exq.get(i).getQuery());
			URLConnection exampleResultsCon = exampleResults.openConnection();
			BufferedReader exIn = new BufferedReader(new InputStreamReader(exampleResultsCon.getInputStream()));
			BufferedWriter exOut = new BufferedWriter(new FileWriter("Output/exampleQueryResults_" + i + ".xml"));
			String line;
			while((line = exIn.readLine()) != null)
				exOut.write(line);
			exOut.close();
		
		}
		
		URL results = new URL(qb.get(0).getQuery());
		URLConnection resultsCon = results.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(resultsCon.getInputStream()));
		BufferedWriter out = new BufferedWriter(new FileWriter("Output/queryResults.xml"));
		String line;
		while((line = in.readLine()) != null)
			out.write(line);
		out.close();
		
		System.out.println("-----------------------------------");
		URL exResults = new URL(exq.get(0).getQuery());
		URLConnection exResultsCon = exResults.openConnection();
		Transformer tr = res.getTransformer("application/atom+xml");
		System.out.println(tr);
		tr.setOutputProperty(OutputKeys.METHOD, "text");
		//tr.transform(new StreamSource(new InputStreamReader(exResultsCon.getInputStream())), new StreamResult(System.out));
		tr.transform(new StreamSource(new InputStreamReader(exResultsCon.getInputStream())), new StreamResult(new FileOutputStream("Output/tranformed.txt")));
		
	}
}

