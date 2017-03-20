package org.gcube.data.analysis.tabulardata.statistical;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
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
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class Common {

	private static Logger logger = LoggerFactory.getLogger(Common.class);
	
	// Column Names
	
	public static boolean isValidColumnName(Column col) throws ParseException, IOException, ProcessingException{
		String currentLabel=OperationHelper.retrieveColumnLabel(col);
		return isValidString(currentLabel);
	}
	
	public static boolean isValidString(String str) throws ParseException, IOException, ProcessingException{
		return str.matches("^[a-z_][a-z_0-9]*")&&!ReservedWordsDictionary.getDictionary().isReservedKeyWord(str);
	}
	public static String fixColumnName(String columnName) throws ParseException, IOException, ProcessingException{
		String toReturn=columnName.replaceAll("\\W", "_").toLowerCase();
		if(isValidString(toReturn)) return toReturn;
		else return "_"+toReturn;
	}
	
	public static String fixColumnName(String columnName,String... additionalReservedWords) throws ParseException, IOException, ProcessingException{
		String toCheck=fixColumnName(columnName);
		for(String additional:additionalReservedWords)
			if(toCheck.equalsIgnoreCase(additional)) toCheck="_"+toCheck;
		return toCheck;
	}
	
	public static String fixColumnName(Column col,String... additionalReservedWords) throws ParseException, IOException, ProcessingException{
		return fixColumnName(OperationHelper.retrieveColumnLabel(col),additionalReservedWords);
	}
	
	
	public static Map<ColumnLocalId,String> curateLabels(Table table,String... additionalReservedWords) throws ParseException, IOException, ProcessingException{
		HashMap<ColumnLocalId,String> toReturn=new HashMap<ColumnLocalId,String>();
		HashMap<String,Integer> clashCounter=new HashMap<String,Integer>();
		for(Column col:table.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			String originalLabel=OperationHelper.retrieveColumnLabel(col);
			String fixed=fixColumnName(originalLabel,additionalReservedWords);
			if(clashCounter.containsKey(fixed)){
				clashCounter.put(fixed, clashCounter.get(fixed)+1);
				fixed=fixed+"_"+clashCounter.get(fixed);
			}else clashCounter.put(fixed, 1);
			toReturn.put(col.getLocalId(), fixed);
		}
		return toReturn;
	}
	
	
	// Access SM
	
	public static StatisticalManagerDataSpace getSMDataSpace(){
		return StatisticalManagerDSL.dataSpace().build();
	}
	
	public static StatisticalManagerFactory getSMFactory(){
		return StatisticalManagerDSL.createStateful().build();
	}
	
	public static boolean isSMAlgorithmAvailable(String algorithmId){
		StatisticalManagerFactory factory=getSMFactory();
		SMListGroupedAlgorithms groups = factory.getAlgorithms();
		for (SMGroupedAlgorithms group : groups.thelist()) {
		 for (SMAlgorithm algorithm : group.thelist()) {
			 if(algorithm.name().equals(algorithmId)) return true;
		 }
		}
		return false;
	}
	
	
	//Handle SM Resources
	
	public static void handleSMResource(SMResource toHandle,List<ResourceDescriptorResult> results,
			Map<String,String> toSerializeValues,
			WorkerWrapper<DataWorker,WorkerResult> wrapper,
			boolean clearDataSpace,
			String user, Home home) throws WorkerException{
		try{
			int resourceTypeIndex = toHandle.resourceType();
			SMResourceType smResType = SMResourceType.values()[resourceTypeIndex];
			switch (smResType) {
			case FILE:
				SMFile smFile=(SMFile)toHandle;
				MyFile f= getStorageFileDescriptor(smFile.url());
				
				results.add(new ImmutableURIResult(
						new InternalURI(new URI(f.getId()),fileNameToMimeType(smFile.name())), smFile.description(), "-", ResourceType.GENERIC_FILE));					
				break;

			case TABULAR:
				Table table=importFromTableSpace((SMTable) toHandle,wrapper,clearDataSpace);
				results.add(new ImmutableTableResource(new TableResource(table.getId()),OperationHelper.retrieveTableLabel(table),"Imported from SM",ResourceType.GENERIC_TABLE));
				break;

			case OBJECT : 
				SMObject objRes = (SMObject) toHandle;
				if(objRes.name().contentEquals(PrimitiveTypes.MAP.toString())){					
					for(Entry<String,SMResource> entry:asMap(objRes).entrySet()){
						SMResource res=entry.getValue();
						res.description(entry.getKey());
						handleSMResource(res,results,toSerializeValues,wrapper,clearDataSpace,user,home);
					}
					break;
				}else if(objRes.name().contentEquals(PrimitiveTypes.FILE.toString())){	
					MyFile file=getStorageFileDescriptor(objRes.url());
					
					results.add(new ImmutableURIResult(new InternalURI(new URI(file.getId()),fileNameToMimeType(objRes.name())), objRes.description(), "-", ResourceType.GENERIC_FILE));					
					break;					
				}else if(objRes.name().contentEquals(PrimitiveTypes.IMAGES.toString())){
					results.addAll(getFilesUrlFromFolderUrl(objRes.url(),user,home));					
				}else {
					//to be serialized
					toSerializeValues.put(String.format("%s [%s]", objRes.description(),objRes.name()),objRes.url());
				}				
			}
		}catch(Exception e){
			logger.warn("Unable to get resource "+toHandle,e);
		}
	}



	// handle Map SM Object
	public static Map<String,SMResource> asMap(SMObject theMap) throws Exception{
		logger.debug("the url for map object is "+theMap.url());
		InputStream is=null;
		Object obj=null;
		try{			
			is = getStorageClientInputStream(theMap.url());
			// object serializer
			XStream xstream = new XStream();
			xstream.alias("org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMObject", SMObject.class);
			xstream.alias("org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile", SMFile.class);
			xstream.alias("org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource", SMResource.class);
			xstream.alias("org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMTable", SMTable.class);		
			obj=(xstream.fromXML(is));
			Map<String, SMResource> smMap = (Map<String, SMResource>) (obj);
			return smMap;			
		}catch(ClassCastException e){
			logger.debug("Error on casting map, obj was "+obj,e);
			throw e;
		}catch (Exception e) {
			logger.debug("Error while deserializing map, url is "+theMap.url());
			throw e;
		}finally{
			is.close();
		}
	}
	
	
	private static Table importFromTableSpace(SMTable table,WorkerWrapper<DataWorker,WorkerResult> wrapper, boolean clearDataSpace)throws WorkerException,OperationAbortedException{
		try{
			HashMap<String,Object> params= new HashMap<String, Object>();

			params.put(ImportFromStatisticalOperationFactory.RESOURCE_ID.getIdentifier(), table.resourceId());
			params.put(ImportFromStatisticalOperationFactory.RESOURCE_NAME.getIdentifier(), table.name());
			params.put(ImportFromStatisticalOperationFactory.DELETE_REMOTE_RESOURCE.getIdentifier(),clearDataSpace);
			WorkerStatus status=wrapper.execute(null, null, params);
			if(!status.equals(WorkerStatus.SUCCEDED)) throw new WorkerException("Failed export to dataspace");
			return wrapper.getResult().getResultTable();
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to import table from dataspace.",e);
		} 
	}
	
	private static List<ResourceDescriptorResult> getFilesUrlFromFolderUrl(String url, String user,Home home) throws WorkerException{
		try{
			ArrayList<ResourceDescriptorResult> toReturn=new ArrayList<ResourceDescriptorResult>();
			
			//HL works on infrastructure level
			String callerScope=ScopeProvider.instance.get();
			ScopeBean scope=new ScopeBean(callerScope);
			while(!scope.is(Type.INFRASTRUCTURE))
				scope=scope.enclosingScope();
			
			
			// Set ROOT Scope
			ScopeProvider.instance.set(scope.toString());
			
			// Get Storage Client as HL
			
			String HLServiceName=null;
			String HLPackage="org.gcube.portlets.user";
			String HLResourceName="HomeLibraryRepository";
			
			// DIscover HL params for storage
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			 
			query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ HLResourceName + "' ");
			 
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			 
			for (AccessPoint ap:client.submit(query).get(0).profile().accessPoints()) {
				 
				if (ap.name().equals("ServiceName")) {	
					HLServiceName = ap.address();
					break;
				}
			}
			
			IClient storage= new StorageClient(HLPackage, HLServiceName, user, AccessType.SHARED, scope.toString(), true).getClient();
			
			
			// Get Folder
			Workspace ws = home.getWorkspace();
			WorkspaceItem folderItem = ws.getItemByPath(url);

			WorkspaceFolder folder = (WorkspaceFolder) folderItem;
			List<WorkspaceItem> childrenList = folder.getChildren();
			
			//For each file in folder get storage id
			for (WorkspaceItem item : childrenList) {
				ExternalImage file = (ExternalImage) item;
				String name = item.getName();
				String mimeType=file.getMimeType();
				if(mimeType.equalsIgnoreCase("png"))mimeType="image/png";
				MyFile storageFile=storage.getMetaFile().RFile(file.getRemotePath());
				// switched name and description
				toReturn.add(new ImmutableURIResult(
						new InternalURI(new URI(storageFile.getId()),mimeType), name,item.getDescription(),ResourceType.GENERIC_FILE));
				
			}

			//Restoring caller'scope
			
			ScopeProvider.instance.set(callerScope);			
			return toReturn;
		}catch(Exception e){
			throw new WorkerException("Unable to retrieve results from workspace ",e);
		}
	}
	
	private static InputStream getStorageClientInputStream(String url) throws Exception{
//		URL u = new URL(null, url, new URLStreamHandler() {
//			@Override
//			protected URLConnection openConnection(URL u) throws IOException {
//				return new SMPURLConnection(u);
//			}
//		});
		return new URL(url).openConnection().getInputStream();
	}
	
	private static MyFile getStorageFileDescriptor(String resourceUrl) throws RemoteBackendException, Exception{
		try{
			return   Utils.getStorageClient().getMetaFile().RFile(resourceUrl);
		}catch(Exception e){
			logger.debug("Not a valid file id "+resourceUrl+", copying by stream");
			IClient client=Utils.getStorageClient();
			String id=client.put(true).LFile(getStorageClientInputStream(resourceUrl)).RFile(UUID.randomUUID().toString());
			logger.debug("Copied to new id : "+id);
			return Utils.getStorageClient().getMetaFile().RFile(id);
		}
	}
	
	
	private static String fileNameToMimeType(String fileName){
		try{
			String extension=fileName.substring(fileName.lastIndexOf('.')+1);
		switch(extension){
		case "zip" : return "application/zip";
		case "png" : return "image/png";
		case "csv" : return "text/csv";
		case "gif" : return "image/gif";		
		case "txt" : return "text/plain";
		default : return "";
		}
		}catch(Exception e){
			logger.debug("Unable to understand file extension, name is "+fileName,e);
			return "";
		}
	}
}
