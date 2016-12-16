package org.gcube.portlets.user.databasesmanager.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ObjectResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.portlets.user.databasesmanager.client.GWTdbManagerService;
import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.datamodel.GeneralOutputFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Parameter;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Result;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Row;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SamplingResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.server.util.DataExchangedThroughQueue;
import org.gcube.portlets.user.databasesmanager.server.util.SessionUtil;
import org.gcube.portlets.user.databasesmanager.shared.AlgorithmsName;
import org.gcube.portlets.user.databasesmanager.shared.Constants;
import org.gcube.portlets.user.databasesmanager.shared.StatisticalManagerException;
import org.gcube.portlets.user.databasesmanager.shared.computation.ComputationOutput;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GWTdbManagerServiceImpl extends RemoteServiceServlet implements
		GWTdbManagerService {

	private static final long serialVersionUID = -3132008420258703064L;

	// logger
	private static Logger logger = Logger
			.getLogger(GWTdbManagerServiceImpl.class);

	// private CacheManager cacheManager;
	private static Cache DBCache;
	private static CacheManager cacheManager;
	public static List<String> listAlgorithms;

	// variables to check thread execution
	private static HashMap<String, Boolean> threadsStarted = new HashMap<String, Boolean>();
	private static boolean endThread = false;
	private static boolean threadExecutionFinished = false;
	private static ConcurrentLinkedQueue<DataExchangedThroughQueue> queue = new ConcurrentLinkedQueue<DataExchangedThroughQueue>();
	private static ThreadDataLoader dataLoader;

	private static int smComputationNumber;
	private static int cacheHitsNumber;

	private static int smComputationQuerySamplingNumber;
	private static int cacheQuerySamplingHitsNumber;
	private static final String CACHE_MAX_HEAP_SIZE = "CACHE_MAX_HEAP_SIZE";

	public GWTdbManagerServiceImpl() throws Exception {
	}

	@Override
	public void init() throws ServletException {
		super.init();

		try {

			// cache folder
			String cachePath = System.getenv("CATALINA_TMPDIR") + "/DBManager";
			logger.info("dbmanager-> Creating cache in folder: " + cachePath);
			// Configuration cacheManagerConfig = new Configuration()
			// .diskStore(new DiskStoreConfiguration()
			// .path(cachePath));
			cacheManager = CacheManager.create();

			if (cacheManager == null) {
				logger.error("dbmanager-> Error while starting the servlet. Failed to get the cacheManager. cacheManager null");
				throw new ServletException(
						"Error while starting the servlet. Failed to get the cacheManager. cacheManager null");
			}

			if (cacheManager != null) {
				// logger.info("dbmanager-> cacheManager not null");
				if (cacheManager.cacheExists("DBCache")) {
					// logger.info("dbmanager-> cache exists");
					cacheManager.removeCache("DBCache");
					logger.info("dbmanager-> cache removed");

				} else {
					File f = new File(cachePath + "/" + "DBCache.data");
					if (f.exists()) {
						logger.info("dbmanager-> File DBCache.data removed: "
								+ f.delete());
					}
				}

				createCache(cachePath);

				if (DBCache == null) {
					logger.error("dbmanager-> Error while starting the servlet. Failed to get the cache. cache null");
					throw new ServletException(
							"Error while starting the servlet. Failed to get the cache. cache null");
				} else {
					cacheManager.addCache(DBCache);
					logger.info("dbmanager-> cache added to the cacheManager");

					logger.info("dbmanager-> ** SIZE OF THE MEMORY STORE: "
							+ DBCache.calculateInMemorySize());
					// logger.trace("dbmanager-> ** SIZE OF THE OFF HEAP"+
					// DBCache.calculateOffHeapSize());
					logger.info("dbmanager-> ** SIZE OF THE DISK STORE SIZE: "
							+ DBCache.calculateOnDiskSize());
				}
			}

			// create folder that will contain file samplings and submitquery
			// result
			// in the /webapps/folder_portlet
			String path = this.getServletContext().getRealPath("") + "/"
					+ "computationResult";

			File computationResult = new File(path);
			if (!computationResult.exists()) {
				computationResult.mkdir();
				logger.info("dbmanager-> Folder computationResult created in : "
						+ this.getServletContext().getRealPath(""));
			}

			// create the thread DataLoader
			dataLoader = new ThreadDataLoader();
			logger.info("dbmanager-> Thread Dataloader created");

			smComputationNumber = 0;
			cacheHitsNumber = 0;

			smComputationQuerySamplingNumber = 0;
			cacheQuerySamplingHitsNumber = 0;
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);

			throw new ServletException(
					"Error while starting the servlet. Exception: " + e);
		}
	}

	private void createCache(String cachePath) throws Exception {

		try {
			CacheConfiguration config = new CacheConfiguration();
			config.setName("DBCache");
			config.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
			config.eternal(true);
			config.timeToLiveSeconds(172800);
			config.timeToIdleSeconds(0);
			// config.maxEntriesLocalHeap(10000);
			config.diskExpiryThreadIntervalSeconds(120);
			config.maxBytesLocalDisk(2, MemoryUnit.GIGABYTES);
			long maxHeapSize = readProperty();
			// config.maxBytesLocalHeap(200, MemoryUnit.MEGABYTES);
			config.maxBytesLocalHeap(maxHeapSize, MemoryUnit.MEGABYTES);
			logger.info("dbmanager-> Max_Bytes_Local_Heap: "
					+ config.getMaxBytesLocalHeap());
			// config.maxBytesLocalHeap(2, MemoryUnit.GIGABYTES);
			config.diskSpoolBufferSizeMB(30);
			config.overflowToDisk(true);
			config.diskPersistent(false);
			// config.diskStorePath(cachePath);
			// SizeOfPolicyConfiguration size = new SizeOfPolicyConfiguration();
			// size.setMaxDepth(1000);
			// size.maxDepthExceededBehavior(MaxDepthExceededBehavior.ABORT);
			// config.sizeOfPolicy(size);
			DBCache = new Cache(config);
		} catch (Exception e) {
			// logger.error("dbmanager-> Error while starting the servlet. Failed to create the cache",
			// e);
			throw new Exception(
					"Error while starting the servlet. Failed to create the cache. Exception: "
							+ e);
		}
	}

	private long readProperty() throws IOException {
		Properties props = new Properties();
		// HttpServletRequest request = this
		// .getThreadLocalRequest();

		String contextPath = this.getServletContext().getRealPath("");
		logger.info("dbmanager-> path file config.properties" + contextPath);
		String propertyfile = contextPath + File.separator + "conf"
				+ File.separator + "configs.properties";
		File propsFile = new File(propertyfile);
		FileInputStream fis = new FileInputStream(propsFile);
		props.load(fis);
		String property = props.getProperty(CACHE_MAX_HEAP_SIZE);

		long value = Long.valueOf(property).longValue();
		return value;
	}

	@Override
	public void destroy() {
		super.destroy();

		// set endThread variable
		setEndThreadvariable(true);

		try {

			CacheManager cacheManager = CacheManager.getInstance();

			if (cacheManager != null) {
				if (cacheManager.cacheExists("DBCache")) {
					// System.out.println("*** cache exist");
					cacheManager.removeCache("DBCache");
					// cacheManager.removalAll();
					// System.out.println("*** cache removed");
					logger.info("dbmanager-> DBCache removed");
				}

				cacheManager.shutdown();
			} else {
				logger.error("dbmanager-> Error while destroying the servlet. Failed to get the cacheManager. cacheManager null");
				throw new Exception(
						"Error while destroying the servlet. Failed to get the cacheManager. cacheManager null");
			}
		} catch (Exception e) {
			logger.error(
					"dbmanager-> Error while destroying the servlet. Exception:",
					e);
			// e.printStackTrace();
		}
	}

	private void initVariables(ASLSession session) {

		// Hashmap that contains computationId with a uid key
		HashMap<String, ComputationId> computationIDMap = new HashMap<String, ComputationId>();
		session.setAttribute("ComputationIDList", computationIDMap);

		// Hashmap that contains the job status with a uid key
		HashMap<String, String> JobStatusMap = new HashMap<String, String>();
		session.setAttribute("JobStatusList", JobStatusMap);

		// map that keeps track if a uid submitQuery request uses cached data
		// and it does not start a computation
		HashMap<String, Boolean> listSubmitQueryUIDCachedData = new HashMap<String, Boolean>();
		session.setAttribute("listSubmitQueryUIDCachedData",
				listSubmitQueryUIDCachedData);

		// map that contain key to retrieve data from cache for each uid
		// submitQuery request
		// map that stores information to send result of the rpc loadsubmitQuery
		// to the client
		HashMap<String, String> listKeySubmitQueryResult = new HashMap<String, String>();
		session.setAttribute("listKeySubmitQueryResult",
				listKeySubmitQueryResult);

		// map that contains for each UID the submit query result in order
		// to face the cache refreshing if a pagination is used
		HashMap<String, List<Result>> listSubmitQueryResult = new HashMap<String, List<Result>>();
		session.setAttribute("listSubmitQueryResult", listSubmitQueryResult);

		// print data
		logger.info("dbmanager-> CheckInformation: cache hits number "
				+ cacheHitsNumber);
		logger.info("dbmanager-> CheckInformation: SM computation number "
				+ smComputationNumber);
		logger.info("dbmanager-> CheckInformation: cache Query Sampling hits number "
				+ cacheQuerySamplingHitsNumber);
		logger.info("dbmanager-> CheckInformation: SM Query Sampling computation number "
				+ smComputationQuerySamplingNumber);
	}

	// to get resources from IS
	@Override
	public List<FileModel> getResource() throws Exception {
		try {

			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// check if the thread is already started
			Boolean value = getThreadStarted(scope);
			if ((value == null) || (value.booleanValue() == false)) {
				DataExchangedThroughQueue dataqueue = new DataExchangedThroughQueue(
						scope);

				queue.offer(dataqueue);
				Thread t = new Thread(dataLoader);
				t.start();

				logger.info("dbmanager-> Thread DataLoader started in order to load data tree");
			}

			// initialize variables with application startup
			initVariables(session);
			return recoverResources(scope);
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw e;
		}
	}

	// to get information about databases of a resource
	@Override
	public LinkedHashMap<String, FileModel> getDBInfo(String resourceName)
			throws Exception {

		try {

			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();
			return recoverDatabases(scope, resourceName);
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw e;
		}
	}

	// to get schema for a database
	@Override
	public List<FileModel> getDBSchema(LinkedHashMap<String, String> dataInput)
			throws Exception {

		try {

			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();
			return recoverSchema(scope, dataInput);
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw e;
		}
	}

	// to get tables
	private List<Result> getTables(LinkedHashMap<String, String> dataInput,
			String elementType) throws Exception {

		try {

			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();
			return recoverTables(scope, dataInput, elementType);
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw e;
		}
	}

	// to load tables
	@Override
	public PagingLoadResult<Result> LoadTables(PagingLoadConfig config,
			LinkedHashMap<String, String> dataInput, String elementType,
			boolean SearchTable, String keyword) throws Exception {

		try {

			List<Result> result = new ArrayList<>();

			// get tables
			// if (result == null)
			// result = getTables(dataInput);
			if (result.size() == 0)
				result = getTables(dataInput, elementType);

			// Create a sublist and add data to list according
			// to the limit and offset value of the config
			List<Result> sublist = new ArrayList<Result>();
			BasePagingLoadResult loadResult = null;

			// print check on the search
			// logger.info("Searching in the table: " + SearchTable);
			// logger.info("Keyword to search: " + keyword);

			int start = config.getOffset();
			int limit = result.size();

			if (config.getLimit() > 0) {
				limit = Math.min(start + config.getLimit(), limit);
			}

			int totalNumber = result.size();

			if ((SearchTable == false) || keyword == null
					|| keyword.length() == 0) {
				sublist = new ArrayList<Result>(result.subList(start, limit));
			} else {
				// print check
				// logger.info("searching the table");

				// search the table
				for (int i = 0; i < result.size(); i++) {
					if ((result.get(i).getValue().toLowerCase())
							.startsWith(keyword.toLowerCase())) {
						sublist.add(result.get(i));
					}
				}

				limit = sublist.size();
				int sublen = sublist.size();
				totalNumber = sublen;

				if (start < sublen - 1) {
					limit = Math.min(sublen, limit);
					totalNumber = sublist.size();
					sublist = new ArrayList<Result>(sublist.subList(start,
							limit));
				}
			}

			// print check
			// logger.info("result size: " + totalNumber);
			// logger.info("limit: " + limit);
			// logger.info("offset: " + config.getOffset());
			// logger.info("start: " + start);

			loadResult = new BasePagingLoadResult<Result>(sublist,
					config.getOffset(), totalNumber);
			// session.setAttribute("TablesResult", result);
			return loadResult;

		} catch (Exception e) {
			logger.error("dbmanager-> ", e);

			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	// to submit a query
	@Override
	public SubmitQueryResultWithFileFromServlet submitQuery(
			LinkedHashMap<String, String> dataDB, String query,
			boolean valueReadOnlyQuery, boolean smartCorrectionQuery,
			String language, String UID) throws Exception {

		try {

			logger.info("Submit Query Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			logger.info("dbmanager-> Dialect used for smart correction: "
					+ language);

			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// data output
			List<Result> output = null;
			SubmitQueryResultWithFileFromServlet result = null;

			// list that contains table attributes
			List<String> listAttributes = null;

			// converted query
			String convertedQuery = "";
			String algorithmId = AlgorithmsName.SUBMITQUERY.name();

			// print check
			String rs = dataDB.get("ResourceName");
			String db = dataDB.get("DatabaseName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);

			logger.info("dbmanager-> Query: " + query);
			logger.info("dbmanager-> SmartCorrections check: "
					+ smartCorrectionQuery);

			if ((rs == null) || (rs.equals(""))) {
				throw new Exception("Unable to load data");
			}
			if ((db == null) || (db.equals(""))) {
				throw new Exception("Unable to load data");
			}
			if ((query == null) || (query.equals(""))) {
				throw new Exception("Unable to load data");
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter readOnlyQuery = new Parameter("Read-Only Query", "",
					"Boolean", "true");
			Parameter applySmartCorrection = new Parameter(
					"Apply Smart Correction", "", "Boolean", "true");
			Parameter lng = new Parameter("Language", "", "NONE", "NONE");
			Parameter q = new Parameter("Query", "", "String", "");

			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(readOnlyQuery);
			inputParameters.add(applySmartCorrection);
			inputParameters.add(lng);
			inputParameters.add(q);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(String.valueOf(valueReadOnlyQuery));
			inputParameters.get(3).setValue(
					String.valueOf(smartCorrectionQuery));
			inputParameters.get(4).setValue(language);
			inputParameters.get(5).setValue(query);

			// get data from cache
			// check if data exist considering as key the input parameters

			// parse the query in order to remove spaces
			String queryParsed = parseQuery(inputParameters.get(5).getValue());
			// get data sent to client calling the submitQuery
			String keyData = scope + algorithmId
					+ inputParameters.get(0).getValue()
					+ inputParameters.get(1).getValue()
					+ inputParameters.get(2).getValue()
					+ inputParameters.get(3).getValue()
					+ inputParameters.get(4).getValue() + queryParsed;

			// System.out.println("submitQuery KEY:" + keyData);
			net.sf.ehcache.Element dataFromCache = getDataFromCache(keyData);

			// key to get query result sent to client calling loadSubmitResult
			String keySubmitQueryResult = keyData + "_SubmitQueryResult";
			updateListKeySubmitQueryResult(UID, keySubmitQueryResult);
			net.sf.ehcache.Element submitQueryResultFromCache = getDataFromCache(keySubmitQueryResult);

			Object data = null;
			Object submitQueryResult = null;

			if ((dataFromCache != null) && (submitQueryResultFromCache != null)) {
				data = dataFromCache.getObjectValue();
				submitQueryResult = submitQueryResultFromCache.getObjectValue();
				// System.out.println("***GETTING DATA FROM CACHE");
			}
			if ((data != null) && (submitQueryResult != null)) {
				result = (SubmitQueryResultWithFileFromServlet) data;

				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
				cacheQuerySamplingHitsNumber++;
				// set variable to true value if cached data are used and a
				// computation is not started
				Boolean val = new Boolean(true);
				updateListSubmitQueryUIDCachedData(UID, val);

			} else {

				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);
				smComputationQuerySamplingNumber++;

				// set variable to false value if cached data are not used and a
				// computation is started
				Boolean val = new Boolean(false);
				updateListSubmitQueryUIDCachedData(UID, val);

				// create data structure
				ComputationOutput outputData = new ComputationOutput();

				// file name
				String FileName = "QueryResult_" + System.currentTimeMillis()
						+ ".csv";

				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, UID, FileName);

				// get JobID
				if (checkJob(UID)) { // if the computation has not been removed
										// the job uid is present
					// computationIDMap.put(id, computationId);

					// print check on retrieving data
					logger.info("output data retrieved");

					// data output values
					LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
					// data output keys
					LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

					mapValues = outputData.getMapValues();
					mapKeys = outputData.getmapKeys();

					if (mapValues != null && !mapValues.isEmpty()) {
						output = new ArrayList<Result>();

						// logger.info("build the result - started");
						for (int i = 0; i < mapValues.size(); i++) {
							Result row = new Result(mapKeys.get(String
									.valueOf(i)), mapValues.get(String
									.valueOf(i)));
							output.add(row);
						}

						// get the converted query
						if (smartCorrectionQuery == true) {
							convertedQuery = output.get(0).getValue();
							output.remove(0);
						}

						// get the attributes list for the result table
						listAttributes = new ArrayList<String>();
						listAttributes = getListAttributes(output.get(0)
								.getValue());

						if (listAttributes == null) {
							logger.error("dbmanager-> Error in server while loading data. variable listAttributes null");
							throw new Exception(
									"Error in server while loading data.");
						}

						// remove job with the specified uid
						removeJob(UID);

						String urlFile = outputData.getUrlFile();

						int submitQueryTotalRows = outputData
								.getSubmitQueryTotalRows();

						result = new SubmitQueryResultWithFileFromServlet(
								listAttributes, convertedQuery, urlFile,
								submitQueryTotalRows);

						// put the two data in cache
						net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
								keyData, result);

						insertDataIntoCache(dataToCache);

						// remove the header in order to parse only the result
						output.remove(0);

						net.sf.ehcache.Element submitQueryResultToCache = new net.sf.ehcache.Element(
								keySubmitQueryResult, output);

						insertDataIntoCache(submitQueryResultToCache);

					}
				} else { // if the computation has been removed the job uid is
							// not present and listAttributes is null.
					listAttributes = null;

				}
			}
			logger.debug("SubmitQuery Result: " + result);

			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("dbmanager-> ", e);
			// Exception Statistical management to remove a
			// computation
			if (e.getMessage()
					.contains(
							"javax.xml.ws.soap.SOAPFaultException: java.lang.IndexOutOfBoundsException")) {
				throw new Exception("ServerException");
			}
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception("Error in server while loading data.");
			}
			throw e;
		} finally {
			// remove the element related to the uid submitQuery request if
			// present
			removeSubmitQueryUIDCachedData(UID);
			// remove jobStatus
			removeJobStatus(UID);
			// remove job
			removeJob(UID);
		}
	}

	@Override
	public SamplingResultWithFileFromServlet sample(
			LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception {

		try {
			logger.info("dbmanager-> Sampling on table Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// output sample result
			List<Result> output = new ArrayList<Result>();
			SamplingResultWithFileFromServlet result;

			String algorithmId = AlgorithmsName.SAMPLEONTABLE.name();

			// print check
			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");
			String scm = dataInput.get("SchemaName");
			String tab = dataInput.get("TableName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);
			logger.info("dbmanager-> SchemaName: " + scm);
			logger.info("dbmanager-> TableName: " + tab);

			if ((elementType != null) && (elementType.equals(Constants.SCHEMA))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((scm == null) || (scm.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}
			if ((elementType != null)
					&& (elementType.equals(Constants.DATABASE))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter schema = new Parameter("SchemaName", "", "String", "");
			Parameter table = new Parameter("TableName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(schema);
			inputParameters.add(table);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(scm);
			inputParameters.get(3).setValue(tab);

			// get data from cache
			// check if data exist considering as key the input parameters
			String key = scope + algorithmId
					+ inputParameters.get(0).getValue()
					+ inputParameters.get(1).getValue()
					+ inputParameters.get(2).getValue()
					+ inputParameters.get(3).getValue();

			// System.out.println("sampling KEY: " + key);
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
				// System.out.println("***GETTING DATA FROM CACHE");
			}
			if (value != null) {
				result = (SamplingResultWithFileFromServlet) value;
				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
				cacheQuerySamplingHitsNumber++;

			} else {

				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);
				smComputationQuerySamplingNumber++;
				// start computation
				// create data structure
				ComputationOutput outputData = new ComputationOutput();
				// file name
				String FileName = "Sampling_" + System.currentTimeMillis()
						+ ".csv";
				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, FileName);

				// print check on retrieving data
				// logger.info("output data retrieved");

				// data output values
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				// data output keys
				LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

				mapValues = outputData.getMapValues();
				mapKeys = outputData.getmapKeys();

				for (int i = 0; i < mapValues.size(); i++) {
					Result row = new Result(mapKeys.get(String.valueOf(i)),
							mapValues.get(String.valueOf(i)));

					output.add(row);
				}

				String urlFile = outputData.getUrlFile();
				result = new SamplingResultWithFileFromServlet(output, urlFile);

				// put data in cache
				net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
						key, result);
				insertDataIntoCache(dataToCache);

			}
			return result;
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	@Override
	public SamplingResultWithFileFromServlet smartSample(
			LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception {

		try {
			logger.info("dbmanager-> Smart Sampling on table Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// output sample result
			List<Result> output = new ArrayList<Result>();
			SamplingResultWithFileFromServlet result;

			String algorithmId = AlgorithmsName.SMARTSAMPLEONTABLE.name();

			// print check
			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");
			String scm = dataInput.get("SchemaName");
			String tab = dataInput.get("TableName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);
			logger.info("dbmanager-> SchemaName: " + scm);
			logger.info("dbmanager-> TableName: " + tab);

			if ((elementType != null) && (elementType.equals(Constants.SCHEMA))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((scm == null) || (scm.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}
			if ((elementType != null)
					&& (elementType.equals(Constants.DATABASE))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter schema = new Parameter("SchemaName", "", "String", "");
			Parameter table = new Parameter("TableName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(schema);
			inputParameters.add(table);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(scm);
			inputParameters.get(3).setValue(tab);

			// get data from cache
			// check if data exist considering as key the input parameters
			String key = scope + algorithmId
					+ inputParameters.get(0).getValue()
					+ inputParameters.get(1).getValue()
					+ inputParameters.get(2).getValue()
					+ inputParameters.get(3).getValue();

			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
				// System.out.println("***GETTING DATA FROM CACHE");
			}
			if (value != null) {
				result = (SamplingResultWithFileFromServlet) value;
				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
				cacheQuerySamplingHitsNumber++;
			} else {
				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);
				smComputationQuerySamplingNumber++;
				// create data structure
				ComputationOutput outputData = new ComputationOutput();

				// file name
				String FileName = "SmartSampling_" + System.currentTimeMillis()
						+ ".csv";
				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, FileName);

				// print check on retrieving data
				// logger.info("dbmanager-> output data retrieved");

				// data output values
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				// data output keys
				LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

				mapValues = outputData.getMapValues();
				mapKeys = outputData.getmapKeys();

				for (int i = 0; i < mapValues.size(); i++) {
					Result row = new Result(mapKeys.get(String.valueOf(i)),
							mapValues.get(String.valueOf(i)));
					output.add(row);
				}

				String urlFile = outputData.getUrlFile();
				result = new SamplingResultWithFileFromServlet(output, urlFile);

				// put data in cache
				net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
						key, result);
				insertDataIntoCache(dataToCache);
			}

			return result;

		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	@Override
	public SamplingResultWithFileFromServlet randomSample(
			LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception {

		try {
			logger.info("dbmanager-> Random Sampling on table Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// output sample result
			List<Result> output = new ArrayList<Result>();

			String algorithmId = AlgorithmsName.RANDOMSAMPLEONTABLE.name();

			// print check
			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");
			String scm = dataInput.get("SchemaName");
			String tab = dataInput.get("TableName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);
			logger.info("dbmanager-> SchemaName: " + scm);
			logger.info("dbmanager-> TableName: " + tab);

			if ((elementType != null) && (elementType.equals(Constants.SCHEMA))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((scm == null) || (scm.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}
			if ((elementType != null)
					&& (elementType.equals(Constants.DATABASE))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((tab == null) || (tab.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter schema = new Parameter("SchemaName", "", "String", "");
			Parameter table = new Parameter("TableName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(schema);
			inputParameters.add(table);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(scm);
			inputParameters.get(3).setValue(tab);

			// create data structure
			ComputationOutput outputData = new ComputationOutput();
			// file name
			String FileName = "RandomSampling_" + System.currentTimeMillis()
					+ ".csv";
			// computation id
			ComputationId computationId = startComputation(algorithmId,
					inputParameters, outputData, scope, FileName);

			// print check on retrieving data
			// logger.info("dbmanager-> output data retrieved");

			// data output values
			LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
			// data output keys
			LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

			mapValues = outputData.getMapValues();
			mapKeys = outputData.getmapKeys();

			for (int i = 0; i < mapValues.size(); i++) {
				Result row = new Result(mapKeys.get(String.valueOf(i)),
						mapValues.get(String.valueOf(i)));
				output.add(row);
			}

			String urlFile = outputData.getUrlFile();
			SamplingResultWithFileFromServlet obj = new SamplingResultWithFileFromServlet(
					output, urlFile);

			return obj;
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	@Override
	public LinkedHashMap<String, FileModel> getTableDetails(
			LinkedHashMap<String, String> dataInput) throws Exception {

		try {
			logger.info("dbmanager-> Table Details Recovery Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// data ouptut
			LinkedHashMap<String, FileModel> outputParameters = new LinkedHashMap<String, FileModel>();

			String algorithmId = AlgorithmsName.GETTABLEDETAILS.name();

			// print check
			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");
			String scm = dataInput.get("SchemaName");
			String tab = dataInput.get("TableName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);
			logger.info("dbmanager-> SchemaName: " + scm);
			logger.info("dbmanager-> TableName: " + tab);

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter schema = new Parameter("SchemaName", "", "String", "");
			Parameter table = new Parameter("TableName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(schema);
			inputParameters.add(table);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(scm);
			inputParameters.get(3).setValue(tab);

			// create data structure
			ComputationOutput outputData = new ComputationOutput();
			// computation id
			ComputationId computationId = startComputation(algorithmId,
					inputParameters, outputData, scope, null);

			// print check on retrieving data
			// logger.info("output data retrieved");

			// output data values
			LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
			// output data keys
			LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

			mapValues = outputData.getMapValues();
			mapKeys = outputData.getmapKeys();

			for (int i = 0; i < mapValues.size(); i++) {
				FileModel obj = new FileModel(mapValues.get(String.valueOf(i)));
				// obj.setIsLoaded(true);
				outputParameters.put(mapKeys.get(String.valueOf(i)), obj);
				// print check
				// logger.info("value: " + outputMap.get(String.valueOf(i)));
				// logger.info("key: " + outputKey.get(String.valueOf(i)));
			}

			return outputParameters;
		} catch (Exception e) {
			logger.error("dbmanager getTableDetails-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	// parse result for Submit query
	@Override
	public PagingLoadResult<Row> loadSubmitResult(PagingLoadConfig config,
			List<String> listAttributes, String UID) throws Exception {

		try {

			// Create a sublist and add data to list according
			// to the limit and offset value of the config
			List<Row> sublist = new ArrayList<Row>();
			BasePagingLoadResult loadResult = null;

			// data parsed
			List<Row> data = new ArrayList<Row>();
			// submit query result
			List<Result> result = new ArrayList<Result>();
			// get the key to retrieve the submitQuery result
			String key = getKeySubmitQueryResult(UID);

			if ((key != null) && (!key.isEmpty())) {
				// load data

				// get data from cache
				net.sf.ehcache.Element dataFromCache = getDataFromCache(key);
				Object value = null;
				if (dataFromCache != null) {
					value = dataFromCache.getObjectValue();
				}
				if (value != null) {
					result = (List<Result>) value;
					logger.trace("dbmanager-> Data recovered from cache");
					updateListSubmitQueryResult(UID, result);

				} else {

					// get the result bound to session
					result = getSubmitQueryResult(UID);
					logger.trace("dbmanager-> Data recovered from ASL session");

				}

				data = parseCVSString(result, listAttributes);
				if (data != null) {
					int start = config.getOffset();
					int limit = data.size();

					if (config.getLimit() > 0) {
						limit = Math.min(start + config.getLimit(), limit);
					}

					int totalNumber = data.size();
					sublist = new ArrayList<Row>(data.subList(start, limit));

					loadResult = new BasePagingLoadResult<Row>(sublist,
							config.getOffset(), totalNumber);

					// System.out.println("start: " + start);
					// System.out.println("limit: " + limit);
					// System.out.println("sublist size: " + sublist.size());
				} else {
					logger.error("dbmanager-> Error in server while loading data. object data null");
					throw new Exception("Error in server while loading data.");
				}

			} else {
				logger.error("dbmanager-> Error in server while loading data. key null");
				throw new Exception("Error in server while loading data.");
			}

			return loadResult;
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw new Exception(
					"Error in server while loading data. Exception: " + e);
		}

	}

	// get attributes list for display the result in a table
	private List<String> getListAttributes(String value) {

		List<String> listAttributes = new ArrayList<String>();
		// recover attribute fields for the result table
		String headers = value;
		// logger.info("Headers fields table: " + headers);
		listAttributes = parseAttributesTableResult(headers);
		// logger.info("attributes number: " + listAttributes.size());
		// logger.info("attributes list: ");
		// print check
		// for (int i = 0; i < listAttributes.size(); i++) {
		// logger.info("attribute: " + listAttributes.get(i));
		// }
		return listAttributes;
	}

	private List<String> parseAttributesTableResult(String phrase) {
		String delimiter = ",";
		List<String> elements = new ArrayList<String>();
		int idxdelim = -1;
		phrase = phrase.trim();

		while ((idxdelim = phrase.indexOf(delimiter)) >= 0) {
			elements.add(phrase.substring(0, idxdelim));
			phrase = phrase.substring(idxdelim + 1).trim();
		}
		elements.add(phrase);
		return elements;
	}

	// parse a csv row in a list of values
	@Override
	public List<Row> parseCVSString(List<Result> results, List<String> attrNames)
			throws Exception {

		List<Row> rows = new ArrayList<Row>();
		try {
			if (results != null && !results.isEmpty()) {
				for (int i = 0; i < results.size(); i++) {
					Result res = results.get(i);
					if (res != null && res.getValue() != null) {
						List<String> attrValues = parse(res.getValue());
						Row element = new Row(attrNames, attrValues, i);
						rows.add(element);
					}
				}
			}

			return rows;
		} catch (Exception e) {
			logger.error("dbmanager parse csv string-> ", e);
			throw new Exception(e);
		}
	}

	private List<String> parse(String row) throws Exception {
		String delimiter = ",";

		List<String> elements = new ArrayList<String>();

		try {
			String phrase = row;
			// logger.info("row: " + phrase);
			int idxdelim = -1;
			boolean quot = false;
			phrase = phrase.trim();

			while ((idxdelim = phrase.indexOf(delimiter)) >= 0) {
				// logger.info("delimiter: " + idxdelim);
				quot = phrase.startsWith("\"");
				if (quot) {
					phrase = phrase.substring(1);
					String quoted = "";
					if (phrase.startsWith("\""))
						phrase = phrase.substring(1);
					else {
						RE regexp = new RE("[^\\\\]\"");
						boolean matching = regexp.match(phrase);

						if (matching) {
							int i0 = regexp.getParenStart(0);
							quoted = phrase.substring(0, i0 + 1).trim();
							phrase = phrase.substring(i0 + 2).trim();
						}
					}
					if (phrase.startsWith(delimiter))
						phrase = phrase.substring(1);

					elements.add(quoted);

				} else {
					elements.add(phrase.substring(0, idxdelim));
					phrase = phrase.substring(idxdelim + 1).trim();
				}
				// logger.info("token: " + phrase);
			}
			if (phrase.startsWith("\"")) {
				phrase = phrase.substring(1);
			}

			if (phrase.endsWith("\"")) {
				phrase = phrase.substring(0, phrase.length() - 1);
			}

			elements.add(phrase);
			// logger.info("server token: " + phrase);
			// logger.info("size: " + elements.size());
		} catch (Exception e) {
			logger.error("dbmanager parse string-> ", e);
			throw e;
		}
		return elements;
	}

	// update job with the related status
	private synchronized void updateJobStatus(String jobID, String status)
			throws Exception {
		if (jobID != null) {
			// add the job status
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			HashMap<String, String> JobStatusMap = (HashMap<String, String>) session
					.getAttribute("JobStatusList");
			JobStatusMap.put(jobID, status);
			session.setAttribute("JobStatusList", JobStatusMap);
		}
	}

	// remove job with the related status
	private synchronized void removeJobStatus(String jobID) throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());
		HashMap<String, String> JobStatusMap = (HashMap<String, String>) session
				.getAttribute("JobStatusList");
		String status = JobStatusMap.get(jobID);
		if (status != null) {
			JobStatusMap.remove(jobID);
			session.setAttribute("JobStatusList", JobStatusMap);
		}
	}

	// get job status
	private synchronized String getJobStatus(String jobID) throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());
		HashMap<String, String> JobStatusMap = (HashMap<String, String>) session
				.getAttribute("JobStatusList");
		String status = JobStatusMap.get(jobID);
		return status;
	}

	// update job with the computation id
	private synchronized void updateJob(String jobID,
			ComputationId computationId) throws Exception {
		if (jobID != null) {
			// add the computation in the map
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			HashMap<String, ComputationId> computationIDMap = (HashMap<String, ComputationId>) session
					.getAttribute("ComputationIDList");
			computationIDMap.put(jobID, computationId);
			session.setAttribute("ComputationIDList", computationIDMap);
		}
	}

	// remove job with the computation id
	private synchronized ComputationId removeJob(String jobID) throws Exception {
		if (jobID != null) {
			// System.out.println("remove jobID " + job);
			// add the computation in the map
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			HashMap<String, ComputationId> computationIDMap = (HashMap<String, ComputationId>) session
					.getAttribute("ComputationIDList");
			ComputationId computationId = computationIDMap.get(jobID);
			if (computationId != null) {
				computationIDMap.remove(jobID);
				session.setAttribute("ComputationIDList", computationIDMap);
				return computationId;
			}
		}
		return null;
	}

	private synchronized boolean checkJob(String jobID) throws Exception {
		boolean isContained = false;
		if (jobID != null) {
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			HashMap<String, ComputationId> computationIDMap = (HashMap<String, ComputationId>) session
					.getAttribute("ComputationIDList");
			if (computationIDMap.containsKey(jobID)) {
				isContained = true;
			} else {
				isContained = false;
			}
		}
		// System.out.println("JobID isContained: " + isContained);
		return isContained;
	}

	private synchronized void updateListSubmitQueryUIDCachedData(String UID,
			Boolean value) throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, Boolean> listSubmitQueryUIDCachedData = (HashMap<String, Boolean>) session
				.getAttribute("listSubmitQueryUIDCachedData");
		listSubmitQueryUIDCachedData.put(UID, value);
		session.setAttribute("listSubmitQueryUIDCachedData",
				listSubmitQueryUIDCachedData);

	}

	private synchronized Boolean checkSubmitQueryUIDCachedData(String UID)
			throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, Boolean> listSubmitQueryUIDCachedData = (HashMap<String, Boolean>) session
				.getAttribute("listSubmitQueryUIDCachedData");
		return listSubmitQueryUIDCachedData.get(UID);
	}

	private synchronized void removeSubmitQueryUIDCachedData(String UID)
			throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, Boolean> listSubmitQueryUIDCachedData = (HashMap<String, Boolean>) session
				.getAttribute("listSubmitQueryUIDCachedData");

		if (listSubmitQueryUIDCachedData.containsKey(UID)) {
			listSubmitQueryUIDCachedData.remove(UID);
			session.setAttribute("listSubmitQueryUIDCachedData",
					listSubmitQueryUIDCachedData);
		}
	}

	private synchronized void removeKeySubmitQueryResult(String UID)
			throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, String> listKeySubmitQueryResult = (HashMap<String, String>) session
				.getAttribute("listKeySubmitQueryResult");

		if (listKeySubmitQueryResult.containsKey(UID)) {
			listKeySubmitQueryResult.remove(UID);
			session.setAttribute("listKeySubmitQueryResult",
					listKeySubmitQueryResult);
		}
	}

	private synchronized void updateListKeySubmitQueryResult(String UID,
			String value) throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, String> listKeySubmitQueryResult = (HashMap<String, String>) session
				.getAttribute("listKeySubmitQueryResult");
		listKeySubmitQueryResult.put(UID, value);
		session.setAttribute("listKeySubmitQueryResult",
				listKeySubmitQueryResult);

	}

	private synchronized String getKeySubmitQueryResult(String UID)
			throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, String> listKeySubmitQueryResult = (HashMap<String, String>) session
				.getAttribute("listKeySubmitQueryResult");
		return listKeySubmitQueryResult.get(UID);
	}

	private synchronized List<Result> getSubmitQueryResult(String UID)
			throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, List<Result>> listSubmitQueryResult = (HashMap<String, List<Result>>) session
				.getAttribute("listSubmitQueryResult");
		return listSubmitQueryResult.get(UID);
	}

	private synchronized void updateListSubmitQueryResult(String UID,
			List<Result> value) throws Exception {
		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, List<Result>> listSubmitQueryResult = (HashMap<String, List<Result>>) session
				.getAttribute("listSubmitQueryResult");
		listSubmitQueryResult.put(UID, value);
		session.setAttribute("listSubmitQueryResult", listSubmitQueryResult);
	}

	private synchronized void removeSubmitQueryResult(String UID)
			throws Exception {

		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		HashMap<String, List<Result>> listSubmitQueryResult = (HashMap<String, List<Result>>) session
				.getAttribute("listSubmitQueryResult");

		if (listSubmitQueryResult.containsKey(UID)) {
			listSubmitQueryResult.remove(UID);
			session.setAttribute("listSubmitQueryResult", listSubmitQueryResult);
		}
	}

	private ComputationId startComputation(String algorithmName,
			List<Parameter> parameters, ComputationOutput outputData,
			String scope, String FileName) throws Exception {
		return startComputation(algorithmName, parameters, outputData, scope,
				null, FileName);
	}

	private ComputationId startComputation(String algorithmName,
			List<Parameter> parameters, ComputationOutput outputData,
			String scopeValue, String jobID, String FileName) throws Exception {
		try {
			DataMinerService dataMinerService = new DataMinerService();
			SClient sClient = dataMinerService.getClient();
			Operator operator = sClient.getOperatorById(AlgorithmsName.valueOf(
					algorithmName).getFullPackageName());

			List<org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter> pars = new ArrayList<>();
			for (Parameter p : parameters) {
				ObjectParameter par = new ObjectParameter();
				par.setName(p.getName());
				par.setValue(p.getValue());
				pars.add(par);

			}
			logger.debug("Parameters: " + pars);
			operator.setOperatorParameters(pars);
			ComputationId computationId = sClient.startComputation(operator);

			// float percentage = 0;
			// String scope = scopeValue;
			// String username = getUsername();

			updateJobStatus(jobID, "computation started");
			updateJob(jobID, computationId);

			logger.info("dbmanager-> startComputation: the computation has started!");
			monitoringComputation(computationId, sClient, outputData, jobID);
			logger.info("dbmanager-> startComputation: the computation has finished!");

			updateJobStatus(jobID, "computation finished");
			// removeJob(jobID);

			return computationId;
		} catch (Exception e) {
			logger.info("dbmanager-> startComputation: the job submit has failed!");
			// e.printStackTrace();
			// logger.error("dbmanager-> ", e);
			throw e;
		}
	}

	private void monitoringComputation(final ComputationId computationId,
			final SClient sClient, final ComputationOutput outputData,
			final String jobID) throws Exception {
		boolean notEnd = true;
		while (notEnd) {
			ComputationStatus computationStatus = sClient
					.getComputationStatus(computationId);
			switch (computationStatus.getStatus()) {
			case ACCEPTED:
				logger.debug("Operation Accepted");
				break;
			case CANCELLED:
				notEnd = false;
				logger.debug("Operation Cancelled");
				logger.info("dbmanager-> startComputation: the computation has finished!");
				updateJobStatus(jobID, "computation finished");
				break;
			case COMPLETE:
				notEnd = false;
				logger.debug("Operation Completed");
				retrieveOutput(computationId, sClient, outputData);
				logger.info("dbmanager-> startComputation: the computation has finished!");
				updateJobStatus(jobID, "computation finished");
				break;
			case FAILED:
				notEnd = false;
				logger.error("Operation Failed");
				logger.error(computationStatus.getMessage(),
						computationStatus.getError());
				updateJobStatus(jobID, "computation finished");
				StatisticalManagerException e = new StatisticalManagerException(
						computationStatus.getMessage());
				logger.error(
						"dbmanager-> Exception generated from the Statistical Manager: ",
						e);
				throw e;

			case RUNNING:
				logger.debug("Operation Running: "
						+ computationStatus.getPercentage());
				break;
			default:
				break;
			}
			Thread.sleep(2000);
		}
	}

	private void retrieveOutput(ComputationId computationId, SClient sClient,
			ComputationOutput outputData) {
		try {
			// output data values
			LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
			// output data keys
			LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

			OutputData output = sClient
					.getOutputDataByComputationId(computationId);
			logger.debug("Output: " + output);
			Resource resource = output.getResource();
			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				int i = 0;
				for (String key : mapResource.getMap().keySet()) {
					Resource value = mapResource.getMap().get(key);
					logger.debug("Entry: " + key + " = " + value);
					if (mapResource.getMap().get(key).isFile()) {
						FileResource fileResource = (FileResource) value;

						outputData.setUrlFile(fileResource.getUrl());
						logger.info("dbmanager-> Http link of the generated File: "
								+ fileResource.getUrl());
					} else {
						ObjectResource objectResource = (ObjectResource) value;
						if (objectResource.getName().compareToIgnoreCase(
								"Total Rows") == 0) {
							outputData.setSubmitQueryTotalRows(Integer
									.parseInt(objectResource.getValue()));
						} else {
							logger.debug("ObjectResource: " + value);
							mapKeys.put(String.valueOf(i), value.getName());
							mapValues.put(String.valueOf(i),
									objectResource.getValue());
							i++;
						}

					}
				}
			}

			outputData.setMapValues(mapValues);
			outputData.setmapKeys(mapKeys);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private InputStream getStorageClientInputStream(String url)
			throws Exception {
		URL u = new URL(null, url, new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return new SMPURLConnection(u);
			}
		});
		return u.openConnection().getInputStream();
	}

	/*
	 * private StatisticalManagerFactory getFactory(String scope) { //
	 * HttpSession httpSession = this.getThreadLocalRequest().getSession();
	 * return SessionUtil.getFactory(scope); }
	 */

	/*
	 * private String getUsername() { // set the username of the user session to
	 * value "database.manager" // HttpSession httpSession =
	 * this.getThreadLocalRequest().getSession(); // return
	 * SessionUtil.getUsername(httpSession); return "database.manager"; }
	 * 
	 * private String getScope() { HttpSession httpSession =
	 * this.getThreadLocalRequest().getSession(); return
	 * SessionUtil.getScope(httpSession); }
	 */

	// remove the computation
	@Override
	public Boolean removeComputation(String uidSubmitQuery) throws Exception {
		// System.out.println("server UID: " + uidSubmitQuery);
		try {

			logger.info("dbmanager-> Remove Computation Request received. Starting to manage the request.");
			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			Boolean isComputationRemoved = false;
			// verify if this uid submitQuery request uses data in cache
			Boolean value = checkSubmitQueryUIDCachedData(uidSubmitQuery);

			if (value != null) {
				// System.out.println("For uid " + uidSubmitQuery
				// + " data are cached? " + value.booleanValue());
				if (!value.booleanValue()) {
					// remove computation
					ComputationId computationId = null;

					if ((uidSubmitQuery != null)
							&& (!(uidSubmitQuery.equals("")))) {
						// get job status
						String status = getJobStatus(uidSubmitQuery);

						if (status == null) {
							// the computation has not started
							while (computationId == null) {
								computationId = removeJob(uidSubmitQuery);
							}
						} else if (status.equals("computation started")) {
							// System.out.println("check status: computation started");
							// the computation has started
							computationId = removeJob(uidSubmitQuery);
						}
					}

					if (computationId != null) {
						try {
							DataMinerService dataMinerService = new DataMinerService();
							SClient sClient = dataMinerService.getClient();
							sClient.cancelComputation(computationId);
							logger.info("dbmanager-> Computation with UID: "
									+ uidSubmitQuery + " removed");
							// remove submit query result
							refreshDataOnServer(uidSubmitQuery);
							// System.out.println("computation removed");
							isComputationRemoved = true;
						} catch (Exception e) {
							// e.printStackTrace();
							logger.info("dbmanager-> Could not remove the computation ID "
									+ computationId
									+ " corresponding to jobID "
									+ uidSubmitQuery);
							logger.error("dbmanager-> ", e);
						}
					}
					// // remove job status
					// removeJobStatus(uidSubmitQuery);
					// //remove the element related to the uid submitQuery
					// request
					// removeSubmitQueryUIDCachedData(uidSubmitQuery);
				}
			}
			return isComputationRemoved;
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw new Exception(
					"Error in server while loading data. Exception: " + e);
		}
		// finally {
		// // remove the element related to the uid submitQuery request
		// removeSubmitQueryUIDCachedData(uidSubmitQuery);
		// // remove job status
		// System.out.println("remove job status");
		// removeJobStatus(uidSubmitQuery);
		// removeJob(uidSubmitQuery);
		// }
	}

	@Override
	public void refreshDataOnServer(String submitQueryUID) throws Exception {

		ASLSession session = SessionUtil.getASLSession(this
				.getThreadLocalRequest().getSession());

		if ((submitQueryUID != null) && (!submitQueryUID.equals(""))) {
			removeKeySubmitQueryResult(submitQueryUID);
			removeSubmitQueryResult(submitQueryUID);
			// removeResultParsed(submitQueryUID);
			// removeResult(submitQueryUID);
			// removeSubmitQueryUIDCachedData(submitQueryUID);
			// System.out.println("data refreshed on server");
		}
	}

	private synchronized void insertDataIntoCache(net.sf.ehcache.Element data)
			throws Exception {

		if (cacheManager.cacheExists("DBCache")) {
			logger.info("dbmanager-> disk store path for cache: " + " none "
					+ " Cache Status: " + DBCache.getStatus().toString());
			if (DBCache.getStatus().toString()
					.equals(Status.STATUS_ALIVE.toString())) {
				DBCache.put(data);
				if (DBCache.isKeyInCache(data.getKey().toString())) {
					logger.trace("dbmanager-> element with key: "
							+ data.getKey().toString() + " added in cache");
				}

				logger.info("dbmanager-> ** SIZE OF THE MEMORY STORE: "
						+ DBCache.calculateInMemorySize());
				// logger.trace("dbmanager-> ** SIZE OF THE OFF HEAP"+
				// DBCache.calculateOffHeapSize());
				logger.info("dbmanager-> ** SIZE OF THE DISK STORE SIZE: "
						+ DBCache.calculateOnDiskSize());
			}
		}
	}

	private synchronized net.sf.ehcache.Element getDataFromCache(String key)
			throws Exception {
		net.sf.ehcache.Element data = null;
		boolean safe = true;
		if (cacheManager.cacheExists("DBCache")) {
			logger.info("dbmanager-> disk store path for cache: " + " none "
					+ ". Cache Status: " + DBCache.getStatus().toString());
			if (DBCache.getStatus().toString()
					.equals(Status.STATUS_ALIVE.toString())) {
				data = DBCache.get(key);
				if (data != null) {
					logger.trace("dbmanager-> element with key: " + key
							+ " is in cache");
					safe = isSafe(data.getObjectValue());
					logger.info("dbmanager-> Safe procedure Status: " + safe);
				}
			}
		}

		if (safe) {
			return data;
		} else {
			return null;
		}
	}

	// method that checks if data retrieved from cache are not corrupted.This
	// fixes an ehcache bug if a big object is moved in the heap.
	private boolean isSafe(Object obj) throws Exception {
		boolean safe = true;
		logger.info("dbmanager-> Starting the safe procedure on data...");

		if (obj instanceof SubmitQueryResultWithFileFromServlet) {
			logger.info("dbmanager-> object is a SubmitQueryResultWithFileFromServlet");
			SubmitQueryResultWithFileFromServlet elem = new SubmitQueryResultWithFileFromServlet();
			elem = (SubmitQueryResultWithFileFromServlet) obj;

			try {
				if ((elem.getProperties() != null)
						&& (elem.getProperties().size() == 0)) {
					safe = false;
					logger.trace("dbmanager-> data not safe");
					// System.out.println("status variable safe: " + safe);
				}
			} catch (NullPointerException e) {
				logger.error("dbmanager-> ", e);
				safe = false;
				logger.trace("dbmanager-> data not safe");
			}
			// if
			// ((elem.getProperties()!=null)&&(elem.getProperties().size()==0)){
			// safe=false;
			// logger.trace("data not safe");
			// // System.out.println("status variable safe: " + safe);
			// }
			// else{
			if (safe == true) {
				// check attributes
				List<String> attributes = new ArrayList<String>();
				attributes = elem.getListOutput();
				if (attributes.size() != 0) {

					checkAttributes: for (int i = 0; i < attributes.size(); i++) {
						if (attributes.get(i) == null) {
							safe = false;
							logger.trace("dbmanager-> attributes data not safe");
							// System.out.println("status variable safe: " +
							// safe);
							break checkAttributes;
						}
					}

				}

				// check converted query
				if (elem.getConvertedQuery() == null) {
					safe = false;
					logger.trace("dbmanager-> converted query not safe");
					// System.out.println("status variable safe: " + safe);
				} else if (elem.getUrlFile() == null) {
					// check url file
					safe = false;
					logger.trace("dbmanager-> file url not safe");
					// System.out.println("status variable safe: " + safe);
				}
			}
			// }
		} else if (obj instanceof SamplingResultWithFileFromServlet) {
			logger.info("dbmanager-> object is a SamplingResultWithFileFromServlet");
			SamplingResultWithFileFromServlet elem = new SamplingResultWithFileFromServlet();
			elem = (SamplingResultWithFileFromServlet) obj;

			try {
				if ((elem.getProperties() != null)
						&& (elem.getProperties().size() == 0)) {
					safe = false;
					logger.trace("dbmanager-> data not safe");
					// System.out.println("status variable safe: " + safe);
				}
			} catch (NullPointerException e) {
				logger.error("dbmanager-> ", e);
				safe = false;
				logger.trace("dbmanager-> data not safe");
			}
			// else{

			if (safe == true) {
				// check output
				List<Result> output = new ArrayList<Result>();
				output = elem.getListOutput();
				if (output.size() != 0) {
					checkOutput: for (int i = 0; i < output.size(); i++) {
						try {
							if ((output.get(i).getProperties() != null)
									&& (output.get(i).getProperties().size() == 0)) {
								safe = false;
								logger.trace("dbmanager-> output not safe");
								// System.out.println("status variable safe: " +
								// safe);
								break checkOutput;
							}
						} catch (NullPointerException e) {
							logger.error("dbmanager-> ", e);
							safe = false;
							logger.trace("dbmanager-> output not safe");
							// System.out.println("status variable safe: " +
							// safe);
							break checkOutput;
						}

					}

				}

				// check file name
				if (elem.getUrlFile() == null) {
					safe = false;
					logger.trace("dbmanager-> file name not safe");
					// System.out.println("status variable safe: " + safe);
				}
			}
			// }

		} else if (obj instanceof ArrayList<?>) {
			try {

				List<Result> list = new ArrayList<Result>();
				list = (ArrayList<Result>) obj;
				// System.out.println("size: " + list.size());
				checkResult: for (int i = 0; i < list.size(); i++) {
					try {
						if ((list.get(i).getProperties() != null)
								&& (list.get(i).getProperties().size() == 0)) {
							safe = false;
							logger.trace("dbmanager-> data not safe");
							// System.out.println("status variable safe: " +
							// safe);
							break checkResult;
						}
						;
					} catch (NullPointerException e1) {
						logger.error("dbmanager-> ", e1);
						safe = false;
						logger.trace("dbmanager-> data not safe");
						// System.out.println("status variable safe: " + safe);
						break checkResult;
					}

				}
				logger.info("dbmanager-> object is a list<Result>");
			} catch (java.lang.ClassCastException e) {
				List<FileModel> list = new ArrayList<FileModel>();
				list = (ArrayList<FileModel>) obj;
				// System.out.println("size: " + list.size());
				checkFileModel: for (int i = 0; i < list.size(); i++) {
					try {
						if ((list.get(i).getProperties() != null)
								&& (list.get(i).getProperties().size() == 0)) {
							safe = false;
							logger.trace("dbmanager-> data not safe");
							// System.out.println("status variable safe: " +
							// safe);
							break checkFileModel;
						}
						;
					} catch (NullPointerException excp) {
						logger.error("dbmanager-> ", excp);
						safe = false;
						logger.trace("dbmanager-> data not safe");
						// System.out.println("status variable safe: " + safe);
						break checkFileModel;
					}

				}
				logger.info("dbmanager-> object is a list<FileModel>");
			}

		} else if (obj instanceof LinkedHashMap) {
			logger.info("dbmanager-> object is a LinkedHashMap");
			LinkedHashMap<String, FileModel> map = new LinkedHashMap<String, FileModel>();
			map = (LinkedHashMap<String, FileModel>) obj;
			// System.out.println("size: " + map.size());
			Set<String> keys = map.keySet();
			Object[] array = keys.toArray();

			checkHashMap: for (int i = 0; i < map.size(); i++) {
				try {
					if ((map.get(array[i].toString()).getProperties() != null)
							&& (map.get(array[i].toString()).getProperties()
									.size() == 0)) {
						safe = false;
						logger.trace("dbmanager-> data not safe");
						// System.out.println("status variable safe: " + safe);
						break checkHashMap;
					}
				} catch (NullPointerException e) {
					logger.error("dbmanager-> ", e);
					safe = false;
					logger.trace("dbmanager-> data not safe");
					// System.out.println("status variable safe: " + safe);
					break checkHashMap;
				}
			}
		}
		logger.info("dbmanager-> Safe procedure finished.");
		return safe;
	}

	// clear the cache on the user request
	@Override
	public GeneralOutputFromServlet refreshDataTree(String ElementType,
			LinkedHashMap<String, String> inputData, FileModel element)
			throws Exception {

		logger.info("dbmanager-> Refresh data request received from element "
				+ ElementType + ". Starting to manage the request.");

		try {

			ASLSession session = SessionUtil.getASLSession(this
					.getThreadLocalRequest().getSession());
			// get scope
			String scope = session.getScope();

			// call the method related to the element selected
			String resourceName = "";
			String databaseName = "";

			GeneralOutputFromServlet result = null;

			if (inputData != null && inputData.size() != 0) {

				DataExchangedThroughQueue dataQueue = null;

				String key = "";
				String keyUsedForQueryRefresh = "";
				String keyUsedForSamplingsRefresh = "";
				String keyUsedForSmartSamplingRefresh = "";
				String keyUsedForRandomSamplingRefresh = "";
				boolean requestToAddInQueue = false;

				if (!ElementType.equals("")) {
					// build key
					switch (ElementType) {
					case Constants.RESOURCESLIST:
						key = scope + inputData.get(Constants.RESOURCESLIST);
						dataQueue = new DataExchangedThroughQueue(scope);
						requestToAddInQueue = true;
						break;
					case Constants.RESOURCE:
						key = scope + inputData.get("ResourceName");
						dataQueue = new DataExchangedThroughQueue(scope,
								Constants.RESOURCE,
								inputData.get("ResourceName"), null, null, null);
						requestToAddInQueue = true;
						break;
					case Constants.DATABASE:
						if (element.getDatabaseType()
								.equals(Constants.POSTGRES)) { // refresh schema
																// list
							key = inputData.get("ResourceName")
									+ inputData.get("DatabaseName");

							// refresh submitted queries
							keyUsedForQueryRefresh = scope
									+ AlgorithmsName.SUBMITQUERY.name() + key;

							refreshSubmittedQueryInCache(keyUsedForQueryRefresh);

							dataQueue = new DataExchangedThroughQueue(scope,
									Constants.DATABASE,
									inputData.get("ResourceName"),
									inputData.get("DatabaseName"), null,
									element.getDatabaseType());
							requestToAddInQueue = true;
							key = scope + key;
						}
						if (element.getDatabaseType().equals(Constants.MYSQL)) { // refresh
																					// table
																					// list
							key = inputData.get("ResourceName")
									+ inputData.get("DatabaseName")
									+ inputData.get("SchemaName");

							keyUsedForQueryRefresh = scope
									+ AlgorithmsName.SUBMITQUERY.name() + key;
							refreshSubmittedQueryInCache(keyUsedForQueryRefresh);

							keyUsedForSamplingsRefresh = scope
									+ AlgorithmsName.SAMPLEONTABLE.name() + key;
							keyUsedForSmartSamplingRefresh = scope
									+ AlgorithmsName.SMARTSAMPLEONTABLE.name()
									+ key;

							key = scope + key;

							refreshSamplingsInCache(keyUsedForSamplingsRefresh,
									keyUsedForSmartSamplingRefresh);

							requestToAddInQueue = false;
						}
						break;

					case Constants.SCHEMA:

						key = inputData.get("ResourceName")
								+ inputData.get("DatabaseName")
								+ inputData.get("SchemaName");

						// refresh submitted query and samplings and tables list
						keyUsedForQueryRefresh = scope
								+ AlgorithmsName.SUBMITQUERY.name()
								+ inputData.get("ResourceName")
								+ inputData.get("DatabaseName");
						refreshSubmittedQueryInCache(keyUsedForQueryRefresh);
						keyUsedForSamplingsRefresh = scope
								+ AlgorithmsName.SAMPLEONTABLE.name() + key;
						keyUsedForSmartSamplingRefresh = scope
								+ AlgorithmsName.SMARTSAMPLEONTABLE.name()
								+ key;

						key = scope + key;

						refreshSamplingsInCache(keyUsedForSamplingsRefresh,
								keyUsedForSmartSamplingRefresh);

						requestToAddInQueue = false;
						break;
					}
				}

				// logger.trace("dbmanager-> Check if data of the node is present in cache with key: : "
				// + key);
				net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

				if (dataFromCache != null) {
					// logger.trace("dbmanager-> Data of the node is in cache");

					logger.info("dbmanager-> Starting the data removing process in cache from the node with key: "
							+ key);

					// refresh data in cache. Remove data related to the subtree
					// with the selected element as root
					refreshDataInCache(element, ElementType, scope, key, null);

					// recover the refresh data of the item selected
					if (!ElementType.equals("")) {
						switch (ElementType) {
						case Constants.RESOURCESLIST:
							List<FileModel> output1 = getResource();
							result = new GeneralOutputFromServlet(output1);
							// System.out.println("server-> output generated");
							break;
						case Constants.RESOURCE:
							resourceName = inputData.get("ResourceName");
							LinkedHashMap<String, FileModel> output2 = getDBInfo(resourceName);
							result = new GeneralOutputFromServlet(output2);
							break;
						case Constants.DATABASE:
							if (element.getDatabaseType() != null
									&& (element.getDatabaseType()
											.equals(Constants.POSTGRES))) { // refresh
								// schema list
								List<FileModel> output3 = getDBSchema(inputData);
								result = new GeneralOutputFromServlet(output3);
							}
							if (element.getDatabaseType() != null
									&& element.getDatabaseType().equals(
											Constants.MYSQL)) { // refresh
								// table list
								getTables(inputData, Constants.DATABASE);
							}

							break;
						case Constants.SCHEMA:
							getTables(inputData, Constants.SCHEMA);
							break;
						}
					}

					// check if the thread execution is terminated. If yes, a
					// new thread is started otherwise no because the thread in
					// running state will also serve this request received in
					// the queue.
					// The thread will run in backgroung to load the data
					// related to the subtree with the item selected as root.
					if (requestToAddInQueue == true) {
						if (isThreadExecutionFinished()) {
							logger.info("dbmanager-> Starting the launch of the Thread DataLoader execution");
							queue.offer(dataQueue);
							Thread t = new Thread(dataLoader);
							t.start();
						} else {
							logger.info("dbmanager-> The Thread DataLoader is already running. Tree Refresh request put in Queue");
							queue.offer(dataQueue);
						}
					}

				}
				// else{
				// logger.trace("dbmanager-> Data of the node is not in cache");
				// }
			}
			return result;

		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	private void refreshDataInCache(FileModel element, String ElementType,
			String scope, String key, String DBType) {
		logger.info("dbmanager-> Refresh Data Request of element "
				+ ElementType + " in Cache with KEY: " + key);

		try {
			// get data from cache
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				// logger.info("dbmanager-> Data is in cache");

				value = dataFromCache.getObjectValue();
				if (value != null) {
					if (!ElementType.equals("")) {
						switch (ElementType) {
						case Constants.RESOURCESLIST:
							// refresh resources
							List<FileModel> resources = (List<FileModel>) value;
							// refresh cache
							refreshCache(key);
							// logger.trace("dbmanager-> element: " +
							// ElementType
							// + " with key: " + key + " removed in cache");
							// apply the refresh on children
							for (int i = 0; i < resources.size(); i++) {
								key = scope + resources.get(i).getName();
								refreshDataInCache(element, Constants.RESOURCE,
										null, key, null);
							}
							break;
						case Constants.RESOURCE:
							// refresh databases
							LinkedHashMap<String, FileModel> DBdata = (LinkedHashMap<String, FileModel>) value;
							// refresh cache
							refreshCache(key);
							// logger.trace("dbmanager-> element " + ElementType
							// + " with key: " + key + " removed in cache");
							// apply the refresh on children
							HashMap<String, String> DBlist = new HashMap<String, String>();

							if (DBdata != null) {
								Set<String> keys = DBdata.keySet();
								Object[] array = keys.toArray();

								int numIterations = (DBdata.size()) / 5;
								int i = 0;
								int j = 0;
								for (i = 0; i < numIterations; i++) {
									String DBName = "";

									for (j = (i * 5); j < (i + 1) * 5; j++) {

										if (array[j].toString().contains(
												"Database Name")) {
											DBName = DBdata.get(
													array[j].toString())
													.getName();
										}

										if (array[j].toString().contains(
												"Driver Name")) {
											String driver = DBdata.get(
													array[j].toString())
													.getName();

											if (driver.toUpperCase().contains(
													Constants.POSTGRES)) {

												DBlist.put(DBName,
														Constants.POSTGRES);
											}

											if (driver.toUpperCase().contains(
													Constants.MYSQL)) {
												DBlist.put(DBName,
														Constants.MYSQL);
											}
										}
									}
								}
							}

							Set<String> keys = DBlist.keySet();
							Object[] array = keys.toArray();

							for (int i = 0; i < array.length; i++) {
								String databaseType = DBlist.get(array[i]
										.toString());
								// if (databaseType
								// .equals(Constants.POSTGRES)) {
								String newkey = key + array[i].toString();
								refreshDataInCache(element, Constants.DATABASE,
										null, newkey, databaseType);
								// }
							}

							break;
						case Constants.DATABASE:
							if (DBType == null) {
								DBType = element.getDatabaseType();
							}
							// refresh schema (db postgres) or tables (db mysql)
							List<FileModel> schemaList = (List<FileModel>) value;

							refreshCache(key); // refresh schema
							// logger.trace("dbmanager-> element " + ElementType
							// + " with key: " + key + " removed in cache");
							if (DBType.equals(Constants.POSTGRES)) {
								// SCHEMA
								for (int i = 0; i < schemaList.size(); i++) {
									String newkey = key
											+ schemaList.get(i).getName();
									refreshDataInCache(element,
											Constants.SCHEMA, null, newkey,
											null);
								}
							}

							if (DBType.equals(Constants.MYSQL)) {
								// refresh tables
								key = key + "";
								refreshCache(key); // refresh tables
								// logger.trace("dbmanager-> element " +
								// ElementType
								// + " with key: " + key
								// + " removed in cache");
							}

							break;
						case Constants.SCHEMA:
							// refresh tables (db postgres)
							refreshCache(key);
							// logger.trace("dbmanager-> element " + ElementType
							// + " with key: " + key + " removed in cache");
							break;
						}
					}
				}
			}
			// else {
			// logger.info("dbmanager-> Data not in cache");
			// }
		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
		}
	}

	private synchronized void refreshCache(String key) throws Exception {
		if (cacheManager.cacheExists("DBCache")) {
			logger.info("dbmanager-> disk store path for cache: " + "none"
					+ "Cache Status: " + DBCache.getStatus().toString());
			if (DBCache.getStatus().toString()
					.equals(Status.STATUS_ALIVE.toString())) {
				DBCache.remove(key);
				logger.trace("dbmanager-> element with key: " + key
						+ " removed in cache");
			}
		}
	}

	private synchronized void refreshSubmittedQueryInCache(String key)
			throws Exception {

		if (cacheManager.cacheExists("DBCache")) {
			logger.info("dbmanager-> disk store path for cache: " + " none "
					+ "Cache Status: " + DBCache.getStatus().toString());
			if (DBCache.getStatus().toString()
					.equals(Status.STATUS_ALIVE.toString())) {
				// logger.info("dbmanager-> Cache Status:"+ "STATUS_ALIVE");

				List<String> keysInCache = DBCache.getKeys();
				int listSize = keysInCache.size();
				List<String> keysToBeRemoved = new ArrayList<>();

				// recover keys list that match the key
				for (int i = 0; i < listSize; i++) {
					if (keysInCache.get(i).startsWith(key)) {
						// System.out.println("data removed with key: " +
						// keysInCache.get(i));
						keysToBeRemoved.add(keysInCache.get(i));
					}
				}
				// remove keys
				DBCache.removeAll(keysToBeRemoved);
				logger.trace("dbmanager-> submitted queries refreshed in cache with key:"
						+ key);
			}
		}
	}

	private synchronized void refreshSamplingsInCache(
			String keyUsedForSamplingsRefresh,
			String keyUsedForSmartSamplingRefresh) throws Exception {

		if (cacheManager.cacheExists("DBCache")) {
			logger.info("dbmanager-> disk store path for cache: " + " none "
					+ "Cache Status: " + DBCache.getStatus().toString());
			if (DBCache.getStatus().toString()
					.equals(Status.STATUS_ALIVE.toString())) {

				List<String> keysInCache = DBCache.getKeys();
				int listSize = keysInCache.size();
				List<String> keysToBeRemoved = new ArrayList<>();

				// recover keys list that match the key
				for (int i = 0; i < listSize; i++) {
					if ((keysInCache.get(i)
							.startsWith(keyUsedForSamplingsRefresh))
							|| (keysInCache.get(i)
									.startsWith(keyUsedForSmartSamplingRefresh))) {
						keysToBeRemoved.add(keysInCache.get(i));
					}
				}
				// remove keys
				DBCache.removeAll(keysToBeRemoved);
				logger.trace("dbmanager-> samplings and smart sampling refreshed in cache with keys: "
						+ keyUsedForSamplingsRefresh
						+ " "
						+ keyUsedForSmartSamplingRefresh);
			}
		}
	}

	private String storeResultIntoCSVFile(List<Result> result, String n)
			throws Exception {

		if (result == null) {
			logger.info("Error in server while loading data. object result null");
			throw new Exception("Error in server while loading data");
		}

		// file that will contain result
		BufferedWriter out = null;

		String path = this.getServletContext().getRealPath("");
		String fileName = "";
		fileName = n + "_" + System.currentTimeMillis() + ".csv";

		String filePath = path + "/computationResult/" + fileName;
		File file = new File(filePath);

		try {

			// create the file
			if (!file.exists()) {
				file.createNewFile();
			}

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			// write into file
			for (int i = 0; i < result.size(); i++) {
				out.write(result.get(i).getValue());
				out.newLine();
			}

		} catch (Exception e) {
			logger.error("dbmanager-> ", e);
			throw e;

		} finally {
			if (out != null) {
				out.close();
			}
		}
		return fileName;
	}

	// to delete more space occurences in order to have only one space between
	// two words in a query
	private String parseQuery(String query) {
		String queryParsed;

		queryParsed = query.trim();
		queryParsed = queryParsed.replaceAll(" +", " ");
		return queryParsed;
	}

	private synchronized void setEndThreadvariable(boolean value) {
		endThread = value;
		logger.info("dbmanager-> Variable EndThread set in order to stop the thread execution");

	}

	private synchronized boolean getEndThreadvariable() {
		return endThread;
	}

	private synchronized void setThreadExecutionFinished(boolean value) {
		threadExecutionFinished = value;
	}

	private synchronized boolean isThreadExecutionFinished() {
		return threadExecutionFinished;
	}

	private List<FileModel> recoverResources(String scope) throws Exception {

		try {
			logger.info("dbmanager-> Resources Recovery Request received. Starting to manage the request.");
			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// data output
			List<FileModel> outputParameters = new ArrayList<FileModel>();

			// get algorithmId
			String algorithmId = AlgorithmsName.LISTDBNAMES.name();

			Parameter maxNumber = new Parameter("MaxNumber", "", "String", "-1");
			inputParameters.add(maxNumber);

			// check if the value is in cache. If data does not exist in cache
			// the computation is started otherwise data are retrieved from
			// cache.

			// get data from cache
			// check if data exist considering as key the input parameters
			// String key = inputParameters.get(0).getDefaultValue();
			String key = scope + Constants.RESOURCESLIST;
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
				// System.out.println("***GETTING DATA FROM CACHE");
			}
			if (value != null) {
				// System.out.println("***GETTING DATA FROM CACHE");
				outputParameters = (List<FileModel>) value;

				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
			} else {
				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);

				// start the computation
				// System.out.println("***STARTING THE COMPUTATION");
				// create data structure for data output
				ComputationOutput outputData = new ComputationOutput();
				// computationId
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, null);

				// print check
				// retrieve data
				// logger.info("output data retrieved");

				// data output
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				mapValues = outputData.getMapValues();

				for (int i = 0; i < mapValues.size(); i++) {
					FileModel obj = new FileModel(mapValues.get(String
							.valueOf(i)));
					// obj.setIsLoaded(true);
					outputParameters.add(obj);
				}

				if (outputParameters != null && outputParameters.size() != 0) {
					// put data in cache
					net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
							key, outputParameters);

					insertDataIntoCache(dataToCache);
					// logger.trace("dbmanager-> element added in cache with key: "
					// + key);
				}
			}
			return outputParameters;
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new Exception("Failed to load data. " + e);
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	private LinkedHashMap<String, FileModel> recoverDatabases(String scope,
			String resourceName) throws Exception {
		try {
			logger.info("dbmanager-> Databases Recovery Request received. Starting to manage the request.");

			String algorithmId = AlgorithmsName.LISTDBINFO.name();

			// print check
			logger.info("dbmanager-> ResourceName: " + resourceName);

			if (resourceName == null || resourceName.isEmpty()) {
				throw new Exception(
						"Unable to load data resource name invalid: "
								+ resourceName);
			}

			// get data from cache
			// check if data exist considering as key the input parameters
			String key = scope + resourceName;
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
			}

			// data output
			LinkedHashMap<String, FileModel> outputParameters = new LinkedHashMap<>();

			if (value != null) {
				outputParameters = (LinkedHashMap<String, FileModel>) value;
				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
				// System.out.println("***GETTING DATA FROM CACHE");
			} else {
				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);
				// start the computation
				// System.out.println("***STARTING THE COMPUTATION");
				// create data structure
				Parameter resource = new Parameter("ResourceName", "",
						"String", "");
				resource.setValue(resourceName);
				List<Parameter> inputParameters = new ArrayList<Parameter>();
				inputParameters.add(resource);
				logger.debug("InputParameters: " + inputParameters);

				ComputationOutput outputData = new ComputationOutput();
				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, null);

				// print check
				// retrieve data
				// logger.info("output data retrieved");

				// data output values
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				// data output keys
				LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

				mapValues = outputData.getMapValues();
				mapKeys = outputData.getmapKeys();

				for (int i = 0; i < mapValues.size(); i++) {
					FileModel obj = new FileModel(mapValues.get(String
							.valueOf(i)));
					// obj.setIsLoaded(true);

					// print check
					// logger.info("value: " +
					// mapValues.get(String.valueOf(i)));
					// logger.info("key: " +
					// System.out.println(mapKeys.get(String.valueOf(i)));
					outputParameters.put(mapKeys.get(String.valueOf(i)), obj);
				}

				// write data in cache
				if (outputParameters.size() > 0) {
					// put data in cache
					net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
							key, outputParameters);

					insertDataIntoCache(dataToCache);
					// logger.trace("dbmanager-> element added in cache with key: "
					// + key);
				}
			}
			logger.debug("dbmanager outputParameters-> " + outputParameters);

			return outputParameters;
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new Exception("Failed to load data " + );
			logger.error("dbmanager-> ", e);

			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	private List<FileModel> recoverSchema(String scope,
			LinkedHashMap<String, String> dataInput) throws Exception {

		try {
			logger.info("dbmanager-> Schema Recovery Request received. Starting to manage the request.");
			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// data output
			List<FileModel> outputParameters = new ArrayList<FileModel>();

			String algorithmId = AlgorithmsName.LISTDBSCHEMA.name();
			// print check
			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");

			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);

			if ((rs == null) || (rs.equals(""))) {
				throw new Exception("Unable to load data");
			}
			if ((db == null) || (db.equals(""))) {
				throw new Exception("Unable to load data");
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);

			// print check algorithm input parameters
			// for (int i = 0; i < inputParameters.size(); i++) {
			// logger.info(inputParameters.get(i).getName());
			// }

			// get data from cache
			// check if data exist considering as key the input parameters
			String key = scope + inputParameters.get(0).getValue()
					+ inputParameters.get(1).getValue();
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
			}
			if (value != null) {
				outputParameters = (List<FileModel>) value;
				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);
			} else {
				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);

				// start the computation
				// create data structure
				ComputationOutput outputData = new ComputationOutput();
				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, null);

				// print check
				// retrieve data
				// logger.info("dbmanager-> output data retrieved");

				// data output values
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				// data output keys
				LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

				mapValues = outputData.getMapValues();
				mapKeys = outputData.getmapKeys();

				for (int i = 0; i < mapValues.size(); i++) {
					FileModel obj = new FileModel(mapValues.get(String
							.valueOf(i)));
					// obj.setIsSchema(true);
					// obj.setIsLoaded(true);
					outputParameters.add(obj);
				}

				// write data in cache
				if (outputParameters != null && outputParameters.size() != 0) {
					// put data in cache

					net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
							key, outputParameters);

					insertDataIntoCache(dataToCache);
					// logger.trace("dbmanager-> element added in cache with key: "
					// + key);
					// DBCache.put(dataToCache);
				}
			}
			return outputParameters;
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new Exception("Failed to load data. " + e);
			logger.error("dbmanager-> ", e);
			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}
	}

	private List<Result> recoverTables(String scope,
			LinkedHashMap<String, String> dataInput, String elementType)
			throws Exception {
		try {

			logger.info("dbmanager-> Tables Recovery Request received. Starting to manage the request.");
			// data input
			List<Parameter> inputParameters = new ArrayList<Parameter>();
			// data output
			List<Result> outputParameters = new ArrayList<Result>();

			String algorithmId = AlgorithmsName.LISTTABLES.name();

			String rs = dataInput.get("ResourceName");
			String db = dataInput.get("DatabaseName");
			String scm = dataInput.get("SchemaName");

			// print check
			logger.info("dbmanager-> ResourceName: " + rs);
			logger.info("dbmanager-> DatabaseName: " + db);
			logger.info("dbmanager-> SchemaName: " + scm);

			if ((elementType != null) && (elementType.equals(Constants.SCHEMA))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((scm == null) || (scm.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}
			if ((elementType != null)
					&& (elementType.equals(Constants.DATABASE))) {
				if ((rs == null) || (rs.equals(""))) {
					throw new Exception("Unable to load data");
				}
				if ((db == null) || (db.equals(""))) {
					throw new Exception("Unable to load data");
				}
			}

			// set input parameters
			Parameter resource = new Parameter("ResourceName", "", "String", "");
			Parameter database = new Parameter("DatabaseName", "", "String", "");
			Parameter schema = new Parameter("SchemaName", "", "String", "");
			inputParameters.add(resource);
			inputParameters.add(database);
			inputParameters.add(schema);

			inputParameters.get(0).setValue(rs);
			inputParameters.get(1).setValue(db);
			inputParameters.get(2).setValue(scm);

			// get data from cache
			// check if data exist considering as key the input parameters
			String key = scope + inputParameters.get(0).getValue()
					+ inputParameters.get(1).getValue()
					+ inputParameters.get(2).getValue();
			net.sf.ehcache.Element dataFromCache = getDataFromCache(key);

			Object value = null;
			if (dataFromCache != null) {
				value = dataFromCache.getObjectValue();
				// System.out.println("***GETTING DATA FROM CACHE");
			}
			if (value != null) {
				outputParameters = (List<Result>) value;
				cacheHitsNumber++;
				logger.info("dbmanager-> CheckDataInCache: data found in cache. cacheHitsNumber: "
						+ cacheHitsNumber);

			} else {

				smComputationNumber++;
				logger.info("dbmanager-> CheckDataInCache: data not found in cache. Starting the Statistical Computation. smComputationNumber: "
						+ smComputationNumber);
				// start computation
				// create data structure
				ComputationOutput outputData = new ComputationOutput();
				// computation id
				ComputationId computationId = startComputation(algorithmId,
						inputParameters, outputData, scope, null);

				// print check on retrieving data
				// logger.info("output data retrieved");

				// data output values
				LinkedHashMap<String, String> mapValues = new LinkedHashMap<String, String>();
				// data output keys
				LinkedHashMap<String, String> mapKeys = new LinkedHashMap<String, String>();

				mapValues = outputData.getMapValues();
				mapKeys = outputData.getmapKeys();

				for (int i = 0; i < mapValues.size(); i++) {
					Result row = new Result(String.valueOf(i),
							mapValues.get(String.valueOf(i)));
					outputParameters.add(row);
				}

				// write data in cache
				if (outputParameters != null && outputParameters.size() != 0) {
					// put data in cache
					net.sf.ehcache.Element dataToCache = new net.sf.ehcache.Element(
							key, outputParameters);

					insertDataIntoCache(dataToCache);
					// logger.trace("dbmanager-> element added in cache with key: "
					// + key);
				}

			}
			return outputParameters;

		} catch (Exception e) {
			// e.printStackTrace();
			// throw new Exception("Failed to load data. " + e);
			// logger.error("dbmanager-> ", e);

			if (!(e instanceof StatisticalManagerException)) {
				// GWT can't serialize all exceptions
				throw new Exception(
						"Error in server while loading data. Exception: " + e);
			}
			throw e;
		}

	}

	private synchronized void updateThreadsStarted(String scope, Boolean value) {
		threadsStarted.put(scope, value);
	}

	private synchronized Boolean getThreadStarted(String scope) {
		Boolean value = threadsStarted.get(scope);
		return value;
	}

	// thread that loads data on the resources
	private class ThreadDataLoader implements Runnable {

		public ThreadDataLoader() {
		}

		@Override
		public void run() {
			logger.info("dbmanager-> Thread DataLoader running");
			try {

				setThreadExecutionFinished(false);

				while (!queue.isEmpty()) {

					logger.info("dbmanager-> Queue to exchange data with the thread not empty");

					// recover data from queue
					DataExchangedThroughQueue node = queue.poll();
					if (node != null) {
						String scope = node.getScope();
						// add an element related to the thread in the hashmap
						updateThreadsStarted(scope, true);
						boolean loadTree = node.treeToBeLoaded();

						// System.out.println("value loadTree: " + loadTree);
						if (loadTree == false) { // load the subtree with the
													// node as root
							if (!getEndThreadvariable()) {
								String elementType = node.elementType();
								String resource = node.resource();

								logger.info("dbmanager-> Starting the node refreshing process");

								switch (elementType) {
								case Constants.RESOURCE:
									getDatabase(scope, resource);
									break;
								case Constants.DATABASE:
									String DBType = node.DBType();
									String database = node.database();
									if (DBType.equals(Constants.POSTGRES)) {
										getSchema(scope, resource, database);
									}
									if (DBType.equals(Constants.MYSQL)) {
										String schema = node.schema();
										getTables(scope, resource, database,
												schema, Constants.DATABASE);
									}
									break;
								case Constants.SCHEMA:
									String db = node.database();
									String schema = node.schema();
									getTables(scope, resource, db, schema,
											Constants.SCHEMA);
									break;
								}
							}

						} else { // load the tree
							logger.info("dbmanager-> Starting the tree loading");

							if (!getEndThreadvariable()) {
								List<FileModel> resources = recoverResources(scope);

								int i = 0;
								while ((!getEndThreadvariable())
										&& (i < resources.size())) {
									getDatabase(scope, resources.get(i)
											.getName());
									i++;
									// logger.info("dbmanager-> ***thread inside the while checking the EndThread variable");
								}
								// logger.info("dbmanager-> ***thread outside the while checking the EndThread variable");
							}
						}
					}
				}
			} catch (Throwable e) {
				logger.error("dbmanager-> ", e);
			} finally {
				// thread terminates its execution
				setThreadExecutionFinished(true);
				logger.info("dbmanager-> Thread DataLoader execution terminated");
			}
		}

		private void getDatabase(String scope, String resourceName) {

			try {

				LinkedHashMap<String, FileModel> DBdata = recoverDatabases(
						scope, resourceName);
				if (DBdata != null) {
					Set<String> keys = DBdata.keySet();
					Object[] array = keys.toArray();

					int numIterations = (DBdata.size()) / 5;
					int i = 0;
					int j = 0;
					for (i = 0; i < numIterations; i++) {
						// String DBName = "";
						// for (j = (i * 5); j < (i + 1) * 5; j++) {
						String DBName = "";
						j = (i * 5);
						while ((!getEndThreadvariable()) && (j < ((i + 1) * 5))) {

							if (array[j].toString().contains("Database Name")) {
								DBName = DBdata.get(array[j].toString())
										.getName();
							}

							if (array[j].toString().contains("Driver Name")) {
								String driver = DBdata.get(array[j].toString())
										.getName();

								if (driver.toUpperCase().contains(
										Constants.POSTGRES)) {
									// get schema
									List<FileModel> schemaList = getSchema(
											scope, resourceName, DBName);
								}

								if (driver.toUpperCase().contains(
										Constants.MYSQL)) {
									// get tables
									getTables(scope, resourceName, DBName, "",
											Constants.DATABASE);
								}
							}
							j++;
						}
					}
				}
			} catch (Exception e) {
				logger.error("dbmanager-> ", e);
			}
		}

		private List<FileModel> getSchema(String scope, String resourceName,
				String databaseName) {

			List<FileModel> schemaList = null;
			try {

				LinkedHashMap<String, String> dataInputForSchema = new LinkedHashMap<String, String>();
				dataInputForSchema.put("ResourceName", resourceName);
				dataInputForSchema.put("DatabaseName", databaseName);

				schemaList = recoverSchema(scope, dataInputForSchema);

				// recover tables
				if (schemaList != null) {
					int z = 0;
					while ((!getEndThreadvariable()) && (z < schemaList.size())) {
						// for (int i = 0; i <
						// schemaList.size(); i++) {
						String schemaName = schemaList.get(z).getName();
						getTables(scope, resourceName, databaseName,
								schemaName, Constants.SCHEMA);
						z++;
					}
				}

			} catch (Exception e) {
				logger.error("dbmanager-> ", e);
			}
			return schemaList;
		}

		private void getTables(String scope, String resourceName,
				String databaseName, String schemaName, String elementType) {

			try {
				LinkedHashMap<String, String> dataInputForTables = new LinkedHashMap<String, String>();
				dataInputForTables.put("ResourceName", resourceName);
				dataInputForTables.put("DatabaseName", databaseName);
				dataInputForTables.put("SchemaName", schemaName);
				recoverTables(scope, dataInputForTables, Constants.DATABASE);
			} catch (Exception e) {
				logger.error("dbmanager-> ", e);
			}
		}
	}
}
