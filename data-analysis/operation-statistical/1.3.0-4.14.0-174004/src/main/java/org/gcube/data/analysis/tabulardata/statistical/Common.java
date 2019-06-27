package org.gcube.data.analysis.tabulardata.statistical;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ImageResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ObjectResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableTableResource;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

public class Common {
	public static final Logger log = LoggerFactory.getLogger(Common.class);

	// Column Names

	public static boolean isValidColumnName(Column col) throws ParseException, IOException, ProcessingException {
		String currentLabel = OperationHelper.retrieveColumnLabel(col);
		return isValidString(currentLabel);
	}

	public static boolean isValidString(String str) throws ParseException, IOException, ProcessingException {
		return str.matches("^[a-z_][a-z_0-9]*") && !ReservedWordsDictionary.getDictionary().isReservedKeyWord(str);
	}

	public static String fixColumnName(String columnName) throws ParseException, IOException, ProcessingException {
		String toReturn = columnName.replaceAll("\\W", "_").toLowerCase();
		if (isValidString(toReturn))
			return toReturn;
		else
			return "_" + toReturn;
	}

	public static String fixColumnName(String columnName, String... additionalReservedWords)
			throws ParseException, IOException, ProcessingException {
		String toCheck = fixColumnName(columnName);
		for (String additional : additionalReservedWords)
			if (toCheck.equalsIgnoreCase(additional))
				toCheck = "_" + toCheck;
		return toCheck;
	}

	public static String fixColumnName(Column col, String... additionalReservedWords)
			throws ParseException, IOException, ProcessingException {
		return fixColumnName(OperationHelper.retrieveColumnLabel(col), additionalReservedWords);
	}

	public static Map<ColumnLocalId, String> curateLabels(Table table, String... additionalReservedWords)
			throws ParseException, IOException, ProcessingException {
		HashMap<ColumnLocalId, String> toReturn = new HashMap<ColumnLocalId, String>();
		HashMap<String, Integer> clashCounter = new HashMap<String, Integer>();
		for (Column col : table.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class)) {
			String originalLabel = OperationHelper.retrieveColumnLabel(col);
			String fixed = fixColumnName(originalLabel, additionalReservedWords);
			if (clashCounter.containsKey(fixed)) {
				clashCounter.put(fixed, clashCounter.get(fixed) + 1);
				fixed = fixed + "_" + clashCounter.get(fixed);
			} else
				clashCounter.put(fixed, 1);
			toReturn.put(col.getLocalId(), fixed);
		}
		return toReturn;
	}

	// Access SM

	// public static StatisticalManagerDataSpace getSMDataSpace(){
	// return StatisticalManagerDSL.dataSpace().build();
	// }
	//
	// public static StatisticalManagerFactory getSMFactory(){
	// return StatisticalManagerDSL.createStateful().build();
	// }
	//
	// public static boolean isSMAlgorithmAvailable(String algorithmId){
	// StatisticalManagerFactory factory=getSMFactory();
	// SMListGroupedAlgorithms groups = factory.getAlgorithms();
	// for (SMGroupedAlgorithms group : groups.thelist()) {
	// for (SMAlgorithm algorithm : group.thelist()) {
	// if(algorithm.name().equals(algorithmId)) return true;
	// }
	// }
	// return false;
	// }

	public static SClient getDMClient() throws Exception {
		return new DataMinerService().getClient();
	}

	public static Operator getOperator(OperationInvocation invocation) {
		Map<String, Operator> param = OperationHelper.getParameter(StatisticalOperationFactory.SM_ENTRIES, invocation);
		return param.get(Constants.OPERATOR_KEY);
	}

	public static URL getURLFromStorageId(String id) throws MalformedURLException, RemoteBackendException {
		return new URL(Utils.getStorageClient().getHttpUrl().RFile(id));
	}

	// Handle SM Resources

	public static void handleSMResource(String storageBasePath, Resource toHandle,
			List<ResourceDescriptorResult> results, Map<String, String> toSerializeValues,
			WorkerWrapper<DataWorker, WorkerResult> wrapper, boolean clearDataSpace) throws WorkerException {
		try {
			log.debug("Handling resource {} : {} [{}]", toHandle.getName(), toHandle.getResourceId(),
					toHandle.getResourceType());
			switch (toHandle.getResourceType()) {
			case TABULAR: {
				String toSetName = retrieveFileName(toHandle.getResourceId(), toHandle.getName());
				Table table = importFromTableSpace(toHandle.getResourceId(), toSetName, wrapper, clearDataSpace);
				results.add(new ImmutableTableResource(
						new org.gcube.data.analysis.tabulardata.model.resources.TableResource(table.getId()),
						OperationHelper.retrieveTableLabel(table), "Imported from SM", ResourceType.GENERIC_TABLE));
				break;
			}
			case MAP: {
				MapResource resource = (MapResource) toHandle;
				for (Entry<String, Resource> entry : resource.getMap().entrySet()) {
					Resource res = entry.getValue();
					handleSMResource(storageBasePath, res, results, toSerializeValues, wrapper, clearDataSpace);
				}
				break;
			}
			case OBJECT: {
				ObjectResource objRes = (ObjectResource) toHandle;
				toSerializeValues.put(String.format("%s [%s]", objRes.getDescription(), objRes.getName()),
						objRes.getResourceId());

				break;
			}
			case FILE: {
				FileResource smFile = (FileResource) toHandle;
				String toSetName = retrieveFileName(smFile.getUrl(), smFile.getName());
				if (smFile.getMimeType().equals("text/csv")) {
					log.debug("Resource {}:ID {} is csv. Importing as table.", toSetName, smFile.getResourceId());
					Table table = importFromTableSpace(smFile.getUrl(), toSetName, wrapper, clearDataSpace);
					results.add(new ImmutableTableResource(
							new org.gcube.data.analysis.tabulardata.model.resources.TableResource(table.getId()),
							OperationHelper.retrieveTableLabel(table), "Imported from SM", ResourceType.GENERIC_TABLE));
				} else {
					log.debug("Resource {}:ID {} is mimetype {} . Importing as FILE .", smFile.getName(),
							smFile.getResourceId(), smFile.getMimeType());
					MyFile file = storeUrl(storageBasePath, toSetName, smFile.getUrl());
					results.add(new ImmutableURIResult(new InternalURI(new URI(file.getId()), smFile.getMimeType()),
							toSetName, smFile.getDescription(), ResourceType.GENERIC_FILE));
				}
				break;
			}
			case IMAGE: {
				ImageResource imgRes = (ImageResource) toHandle;
				String toSetName = retrieveFileName(imgRes.getLink(), imgRes.getName());
				MyFile file = storeUrl(storageBasePath, toSetName, imgRes.getLink());
				results.add(new ImmutableURIResult(new InternalURI(new URI(file.getId()), imgRes.getMimeType()),
						toSetName, imgRes.getDescription(), ResourceType.CHART));

				// results.add(new ImmutableURIResult(
				// new InternalURI(new
				// URI(imgRes.getLink()),imgRes.getMimeType()),toSetName,imgRes.getDescription(),
				// ResourceType.CHART ));
				break;
			}
			}
		} catch (Exception e) {
			log.warn("Unable to get resource " + toHandle, e);
		}
	}

	private static Table importFromTableSpace(String resourceID, String tableName,
			WorkerWrapper<DataWorker, WorkerResult> wrapper, boolean clearDataSpace)
			throws WorkerException, OperationAbortedException {
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();

			params.put(ImportFromStatisticalOperationFactory.RESOURCE_ID.getIdentifier(), resourceID);
			params.put(ImportFromStatisticalOperationFactory.RESOURCE_NAME.getIdentifier(), tableName);
			params.put(ImportFromStatisticalOperationFactory.DELETE_REMOTE_RESOURCE.getIdentifier(), clearDataSpace);
			WorkerStatus status = wrapper.execute(null, null, params);
			if (!status.equals(WorkerStatus.SUCCEDED))
				throw new WorkerException("Failed export to dataspace");
			return wrapper.getResult().getResultTable();
		} catch (InvalidInvocationException e) {
			throw new WorkerException("Unable to import table from dataspace.", e);
		}
	}

	// private static List<ResourceDescriptorResult>
	// getFilesUrlFromFolderUrl(String url, Home home) throws WorkerException{
	// try{
	// ArrayList<ResourceDescriptorResult> toReturn=new
	// ArrayList<ResourceDescriptorResult>();
	//
	// //HL works on infrastructure level
	// String callerScope=ScopeProvider.instance.get();
	// ScopeBean scope=new ScopeBean(callerScope);
	// while(!scope.is(Type.INFRASTRUCTURE))
	// scope=scope.enclosingScope();
	//
	//
	// // Set ROOT Scope
	// ScopeProvider.instance.set(scope.toString());
	//
	// // Get Storage Client as HL
	//
	// String HLServiceName=null;
	// String HLPackage="org.gcube.portlets.user";
	// String HLResourceName="HomeLibraryRepository";
	//
	// // DIscover HL params for storage
	// SimpleQuery query = queryFor(ServiceEndpoint.class);
	//
	// query.addCondition("$resource/Profile/Category/text() eq 'Database' and
	// $resource/Profile/Name eq '"+ HLResourceName + "' ");
	//
	// DiscoveryClient<ServiceEndpoint> client =
	// clientFor(ServiceEndpoint.class);
	//
	// for (AccessPoint ap:client.submit(query).get(0).profile().accessPoints())
	// {
	//
	// if (ap.name().equals("ServiceName")) {
	// HLServiceName = ap.address();
	// break;
	// }
	// }
	//
	//// IClient storage= new StorageClient(HLPackage, HLServiceName,
	// AccessType.SHARED, scope.toString(), true).getClient();
	//
	// IClient storage= Utils.getStorageClient();
	//
	// // Get Folder
	// Workspace ws = home.getWorkspace();
	// WorkspaceItem folderItem = ws.getItemByPath(url);
	//
	// WorkspaceFolder folder = (WorkspaceFolder) folderItem;
	// List<WorkspaceItem> childrenList = folder.getChildren();
	//
	// //For each file in folder get storage id
	// for (WorkspaceItem item : childrenList) {
	// ExternalImage file = (ExternalImage) item;
	// String name = item.getName();
	// String mimeType=file.getMimeType();
	// if(mimeType.equalsIgnoreCase("png"))mimeType="image/png";
	// MyFile storageFile=storage.getMetaFile().RFile(file.getRemotePath());
	// // switched name and description
	// toReturn.add(new ImmutableURIResult(
	// new InternalURI(new URI(storageFile.getId()),mimeType),
	// name,item.getDescription(),ResourceType.GENERIC_FILE));
	//
	// }
	//
	// //Restoring caller'scope
	//
	// ScopeProvider.instance.set(callerScope);
	// return toReturn;
	// }catch(Exception e){
	// throw new WorkerException("Unable to retrieve results from workspace
	// ",e);
	// }
	// }

	private static InputStream getStorageClientInputStream(String url) throws Exception {
		return new URL(url).openConnection().getInputStream();
	}

	private static MyFile storeUrl(String basePath, String fileName, String resourceUrl)
			throws RemoteBackendException, Exception {

		log.debug("Storing {} to {} from url {} ", fileName, basePath, resourceUrl);
		IClient client = Utils.getStorageClient();
		String id = client.put(true).LFile(getStorageClientInputStream(resourceUrl)).RFile(basePath + "/" + fileName);
		log.debug("Copied to new id : " + id);
		return client.getMetaFile().RFile(id);
		//
		//
		//
		//
		//
		// try{
		// return Utils.getStorageClient().getMetaFile().RFile(resourceUrl);
		// }catch(Exception e){
		// log.debug("Not a valid file id "+resourceUrl+", copying by stream");
		// IClient client=Utils.getStorageClient();
		// String
		// id=client.put(true).LFile(getStorageClientInputStream(resourceUrl)).RFile(UUID.randomUUID().toString());
		// log.debug("Copied to new id : "+id);
		// return Utils.getStorageClient().getMetaFile().RFile(id);
		// }
	}

	public static String retrieveFileName(String url, String defaultName) {

		try {
			String fileName = null;
			log.debug("Resolving name for url {} ", url);
			URL urlObj;
			urlObj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			int status = connection.getResponseCode();
			if (status >= 300 && status < 400) {
				String newUrl = connection.getHeaderField("Location");
				log.debug("Following redirect from {} to {} ", url, newUrl);
				return retrieveFileName(newUrl, defaultName);
			}

			String contentDisposition = connection.getHeaderField("Content-Disposition");

			Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
			Matcher regexMatcher = regex.matcher(contentDisposition);
			if (regexMatcher.find()) {
				fileName = regexMatcher.group();
			}

			if (fileName == null || fileName.isEmpty()) {
				throw new Exception("Filename was null or empty.");
			}

			return fileName;
		} catch (Throwable t) {
			log.debug("Unable to retrieve name from url {}, reverting to default {}.", url, defaultName, t);
			return defaultName;
		}
	}
}
