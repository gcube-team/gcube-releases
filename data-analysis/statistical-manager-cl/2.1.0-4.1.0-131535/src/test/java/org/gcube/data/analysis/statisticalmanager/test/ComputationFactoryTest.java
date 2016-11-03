package org.gcube.data.analysis.statisticalmanager.test;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
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
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.thoughtworks.xstream.XStream;

public class ComputationFactoryTest {

	private static Home home;
	
	
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set(TestCommon.SCOPE);
		Handler.activateProtocol();
		home=HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(TestCommon.USER);
		System.out.println("Testing factory under scope "+TestCommon.SCOPE);
		StatisticalManagerFactory factory=StatisticalManagerDSL.createStateful().build();
		
		for(SMGroupedAlgorithms groupedAlgos:factory.getAlgorithmsUser(null).thelist()){
			System.out.println(groupedAlgos.category());
			for(SMAlgorithm algo:groupedAlgos.thelist())
				System.out.println("\t "+algo.name());
		}
		
		
//		for(SMComputation comp:factory.getComputations(TestCommon.USER, null).list()){
//			if(SMOperationStatus.values()[comp.operationStatus()].equals(SMOperationStatus.COMPLETED)){
//				handleSMResource(comp.abstractResource().resource());
//				
//			}				
//		}
//		
		
//		System.out.println("Username "+TestCommon.USER);
//		
//		System.out.println("---------- get ALgorithms ");
//		SMListGroupedAlgorithms listGrouped=factory.getAlgorithms();
//		for(SMGroupedAlgorithms group:listGrouped.thelist()){
//			System.out.println("CATEGORY "+group.category());
//			for(SMAlgorithm algo:group.thelist()){
//				System.out.println("\t"+algo.name());
//				SMParameters params=factory.getAlgorithmParameters(algo.name());
//				System.out.println("\t\tParameters "+params.list());				
//				System.out.println("\t\tGot "+factory.getAlgorithms(params.list().get(0).type())+"filtered similar ");
//				System.out.println("\t\tOUTPUT : "+factory.getAlgorithmOutputs(algo.name()));
//			}
//		}
//		System.out.println("---------- get USER ALgorithms ");
//		for(SMGroupedAlgorithms group:factory.getAlgorithmsUser(null).thelist()){
//			System.out.println("CATEGORY "+group.category());
//			for(SMAlgorithm algo:group.thelist()){
//				System.out.println("\t"+algo.name());
//				SMParameters params=factory.getAlgorithmParameters(algo.name());
//				System.out.println("\t\tParameters "+params.list());				
//				System.out.println("\t\tGot "+factory.getAlgorithms(params.list().get(0).type())+"filtered similar ");
//				System.out.println("\t\tOUTPUT : "+factory.getAlgorithmOutputs(algo.name()));
//			}
//		}		
		
//		int computationId=62;
//		
//		System.out.println("RESUBMITTING "+computationId);
//		
//		factory.resubmitComputation(62+"");
	}
	
	
	private static void handleSMResource(SMResource toHandle) throws Exception{
		try{
			System.out.println("Handling "+toHandle);
			int resourceTypeIndex = toHandle.resourceType();
			SMResourceType smResType = SMResourceType.values()[resourceTypeIndex];
			switch (smResType) {
			case FILE:
				SMFile smFile=(SMFile)toHandle;
				MyFile f= getStorageFileDescriptor(smFile.url());
				System.out.println((new URI(f.getId())));					
				break;

			case TABULAR:
				System.out.println(toHandle);
				break;

			case OBJECT : 
				SMObject objRes = (SMObject) toHandle;
				if(objRes.name().contentEquals(PrimitiveTypes.MAP.toString())){					
					for(Entry<String,SMResource> entry:asMap(objRes).entrySet())
						handleSMResource(entry.getValue());
					break;
				}else if(objRes.name().contentEquals(PrimitiveTypes.FILE.toString())){	
					MyFile file=getStorageFileDescriptor(objRes.url());
					
					System.out.println(new URI(file.getId()));					
					break;					
				}else if(objRes.name().contentEquals(PrimitiveTypes.IMAGES.toString())){
					getFilesUrlFromFolderUrl(objRes.url());					
				}else {
					//to be serialized
					System.out.println("Mapped Params :" +(String.format("%s [%s]", objRes.description(),objRes.name()) + objRes.url()));
				}				
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	
	// handle Map SM Object
		public static Map<String,SMResource> asMap(SMObject theMap) throws Exception{
			InputStream is=null;
			Object obj=null;
			try{			
				is = new URL(theMap.url()).openConnection().getInputStream();
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
			
				throw e;
			}catch (Exception e) {
			
				throw e;
			}finally{
				is.close();
			}
		}
		
		private static MyFile getStorageFileDescriptor(String resourceUrl) throws RemoteBackendException, Exception{
			
				return    new StorageClient("SomeServiceClass", "SomeServiceName", "TDM", AccessType.SHARED).getClient().getMetaFile().RFile(resourceUrl);
			
		}
		
		
		private static void getFilesUrlFromFolderUrl(String url) throws Exception{
				
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
				
				IClient storage= new StorageClient(HLPackage, HLServiceName, TestCommon.USER, AccessType.SHARED, scope.toString(), true).getClient();
				
				
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
					System.out.println(new URI(storageFile.getId()));
					
				}

				//Restoring caller'scope
				
				ScopeProvider.instance.set(callerScope);			
		}
}


