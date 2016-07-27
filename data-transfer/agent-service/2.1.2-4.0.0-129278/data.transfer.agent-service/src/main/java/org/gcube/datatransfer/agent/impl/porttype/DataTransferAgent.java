package org.gcube.datatransfer.agent.impl.porttype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.GenericResource;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.datatransfer.agent.impl.context.AgentContext;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.db.DataTransferDBManager;
import org.gcube.datatransfer.agent.impl.state.AgentResource;
import org.gcube.datatransfer.agent.impl.state.AgentResource.FutureWorker;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.gcube.datatransfer.agent.impl.utils.Utils;
import org.gcube.datatransfer.agent.impl.worker.SyncWorker;
import org.gcube.datatransfer.agent.impl.worker.Worker;
import org.gcube.datatransfer.agent.impl.worker.async.DataStorageASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.LocalFileTransferASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.StorageManagerASyncWorker;
import org.gcube.datatransfer.agent.impl.worker.async.TreeManagerAsyncWorker;
import org.gcube.datatransfer.agent.impl.worker.sync.LocalFileTransferSyncWorker;
import org.gcube.datatransfer.agent.impl.worker.sync.LocalTransferSyncWorker;
import org.gcube.datatransfer.agent.impl.worker.sync.StorageManagerSyncWorker;
import org.gcube.datatransfer.agent.impl.worker.sync.TreeManagerSyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.CancelTransferFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.CancelTransferMessage;
import org.gcube.datatransfer.agent.stubs.datatransferagent.CreateTreeSourceMsg;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.GetTransferOutcomesFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.MonitorTransferFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.MonitorTransferReportMessage;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StartTransferMessage;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;
import org.gcube.datatransfer.common.objs.LocalSource;
import org.gcube.datatransfer.common.objs.LocalSources;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import static org.gcube.resources.discovery.icclient.ICFactory.*;
import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class DataTransferAgent extends GCUBEPortType {

	protected final GCUBELog logger = new GCUBELog(DataTransferAgent.class);

	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	public DataTransferAgent(){
	}

	public AgentResource getResource() throws Exception {
		return (AgentResource) AgentContext.getContext().getAgent();
	}

	/**
	 * Starts a new transfer
	 * @param message
	 * @return
	 * @throws TransferFault
	 */
	public String startTransfer(StartTransferMessage message) throws TransferFault {

		logger.info("Start Transfer invoked in scope " + message.getSource().getScope());

		String id =  uuidgen.nextUUID();
		try {

			//woker which consumes an RS of files
			if (message.getSource().getType().getValue().compareTo(TransferType.LocalFileBasedTransfer.getValue())==0)
			{
				logger.debug("Local transfer from  URI :"+ message.getSource().getInputURIs()[0]);
				LocalTransferSyncWorker worker = new LocalTransferSyncWorker(id,message.getSource(), message.getDest());
				return (String)worker.call();	
			}
			//check if the operation is sync or async
			else if(message.isSyncOp()) {
				logger.debug("Sync operation");
				logger.debug("TransferType: "+ message.getSource().getType().getValue());
				return startSyncTask(id, message.getSource(), message.getDest());
			}
			else {
				logger.debug("ASync operation");
				logger.debug("TransferType: "+ message.getSource().getType().getValue());
				FutureTask<Worker> task = startAsyncTask(id, message.getSource(), message.getDest());
				//CHANGED - Now we set the workerMap inside the startAsyncTask operation..
				//getResource().getWorkerMap().put(id, task);		
			}
		} catch (Exception e) {
			logger.error("Unable to perform the transfer", e);
			throw Utils.newFault(new TransferFault(), e);
		}

		logger.debug("Returning id : "+ id);
		return id;	
	}

	/**
	 * Cancel a  transfer
	 * @param message
	 * @return
	 * @throws TransferFault
	 * @throws GCUBEFault 
	 */
	public String cancelTransfer(CancelTransferMessage message) throws CancelTransferFault {
		String handlerID = message.getTransferID();


		if (handlerID == null)
			throw Utils.newFault(new CancelTransferFault(), new Exception("The Transfer ID is null"));

		FutureTask futureTask = null;
		Worker worker=null;
		FutureWorker futureWorker = null;
		try {
			futureWorker = getResource().getWorkerMap().get(handlerID);
			if (futureWorker != null) {
				futureTask = futureWorker.getFutureTask();
				worker  = futureWorker.getWorker();
				if(futureTask==null)logger.debug("null futureTask");
				else if (worker==null)logger.debug("null worker");
				else{
					if(worker.getThreadList()==null)logger.debug("null threadList");
					else worker.getThreadList().stop();

					futureTask.cancel(message.isForceStop());
				}
			}
			else logger.debug("null futureWorker");
		} catch (Exception e) {
			logger.error("Unable to cancel the transfer", e);
			throw Utils.newFault(new CancelTransferFault(), e);
		}	
		finally{
			try {

				if(ServiceContext.getContext().getDbManager().checkIfTransferExist(handlerID)){
					getResource().getWorkerMap().remove(handlerID);
					//Set status CANCEL on the DB
					ServiceContext.getContext().getDbManager().updateTransferObjectStatus(handlerID, TransferStatus.CANCEL.name());
				}
				else{
					logger.debug("cancelTransfer - transfer with id="+handlerID+" does not exist");
					return handlerID;
				}
			} catch (Exception e) {
				throw Utils.newFault(new CancelTransferFault(), e);
			}
		}
		return handlerID;
	}


	public  FutureTask<Worker>  startAsyncTask(String id,SourceData source ,DestData dest) throws Exception {
		logger.debug("startAsyncTask has been reached ... ");
		Worker worker = null;
		FutureTask<Worker> task = null;
		DataTransferDBManager dbManager = ServiceContext.getContext().getDbManager();

		if (source.getType().getValue().compareTo(TransferType.TreeBasedTransfer.getValue()) ==0){
			worker= new  TreeManagerAsyncWorker(id, source, dest);
			//store transfer for the tree files
			if(dbManager.checkIfTransferExist(id)){
				//update
				dbManager.updateTransfer(id);
			}else{
				//new transfer - store
				dbManager.storeTransfer(TransferUtils.createTransferJDO(id,source.getInputSource().getSourceId(),dest.getOutSourceId()));
			}
		}
		else if (source.getType().getValue().compareTo(TransferType.FileBasedTransfer.getValue()) ==0) {
			if (dest.getOutUri().getOptions().getStorageType().getValue().compareTo(StorageType.StorageManager.getValue()) == 0)
				worker= new  StorageManagerASyncWorker(id,source, dest);
			else if (dest.getOutUri().getOptions().getStorageType().getValue().compareTo(StorageType.DataStorage.getValue()) == 0)
				worker= new  DataStorageASyncWorker(id,source, dest);
			else worker= new  LocalFileTransferASyncWorker(id,source, dest);			

			//store transfer for the regular files
			if(dbManager.checkIfTransferExist(id)){
				//update
				dbManager.updateTransfer(id);
			}else{
				//new transfer - store
				dbManager.storeTransfer(TransferUtils.createTransferJDO(id));
			}
		}		
		task = new FutureTask<Worker> (worker);
		worker.setTask(task);
		Thread t = new Thread(task);
		t.start();

		FutureWorker futureWorker = new AgentResource.FutureWorker();
		futureWorker.setFutureTask(task);
		futureWorker.setWorker(worker);

		getResource().getWorkerMap().put(id, futureWorker);	
		return task;		 
	}

	private  String  startSyncTask(String id,SourceData source ,DestData dest) throws Exception {

		SyncWorker worker =  null;
		FutureTask<Worker> task = null;

		if (source.getType().getValue().compareTo(TransferType.TreeBasedTransfer.getValue()) ==0){
			worker= new  TreeManagerSyncWorker(id,source, dest);
			worker.call();
			return worker.getOutcomeLocator();
		}
		else if (source.getType().getValue().compareTo(TransferType.FileBasedTransfer.getValue()) ==0) {
			if (dest.getOutUri().getOptions().getStorageType().getValue().compareTo(StorageType.StorageManager.getValue()) == 0)
				worker= new  StorageManagerSyncWorker(id,source, dest);
			else 
				worker= new  LocalFileTransferSyncWorker(id,source, dest);
		}
		task = new FutureTask<Worker> (worker);
		Thread t = new Thread(task);
		t.start();

		return worker.getOutcomeLocator(); 
	}


	public String monitorTransfer(String transferId) throws MonitorTransferFault {

		if (transferId == null)
			throw Utils.newFault(new MonitorTransferFault(), new Exception("The Transfer ID is null"));
		String status ="";
		try {
			status = ServiceContext.getContext().getDbManager().getTransferStatus(transferId);
		}catch (Exception e){
			throw Utils.newFault(new MonitorTransferFault(), e);
		}
		return status;
	}

	public String getTransferOutcomes(String transferId) throws GetTransferOutcomesFault {

		if (transferId == null)
			throw Utils.newFault(new GetTransferOutcomesFault(), new Exception("The Transfer ID is null"));

		String rs = "";
		try {
			rs = ServiceContext.getContext().getDbManager().getTransferObjectOutComeAsRS(transferId);
		}catch (Exception e){
			if(e.getMessage().compareTo("The Transfer Objects list is empty")==0){
				return null;
			}
			e.printStackTrace();
			throw  Utils.newFault(new GetTransferOutcomesFault(),e);
		}
		return rs;
	}

	public String getLocalSources(String filePath) {
		String vfsRoot=((String) ServiceContext.getContext().getProperty("vfsRoot", true));

		if(!filePath.endsWith("/"))filePath=filePath+"/";

		String path=null;
		if((!vfsRoot.endsWith("/"))&&(!filePath.startsWith("/"))){
			path = vfsRoot+"/"+filePath;
		}
		else path = vfsRoot+filePath;

		LocalSources sources = new LocalSources();
		List<LocalSource> list = new ArrayList<LocalSource>();

		File main = new File(path);
		if(!main.isDirectory())return null;
		String[] children = main.list();
		if(children!=null){
			if(children.length>0){
				for(String tmp:children){
					File child = new File(path+tmp);
					if(child.isDirectory()){
						LocalSource dir = new LocalSource();
						dir.setDirectory(true);
						dir.setPath(child.getAbsolutePath());
						dir.setVfsRoot(vfsRoot);

						list.add(dir);
					}
					else{
						LocalSource file = new LocalSource();
						file.setDirectory(false);
						file.setPath(child.getAbsolutePath());

						file.setVfsRoot(vfsRoot);

						file.setSize(child.length());
						list.add(file);
					}
				}
			}
		}
		sources.setList(list);
		return sources.toXML();
	}

	public String createTreeSource(CreateTreeSourceMsg msg){
		try{
			if(msg==null){logger.error("input CreateTreeSourceMsg is null");return null;}

			String sourceID=msg.getSourceID();
			String endpoint=msg.getEndpoint();
			int port = msg.getPort();

			TBinder binder=null;
			if(endpoint==null){
				logger.debug("not specific endpoint");
				binder = TServiceFactory.binder().matching(TServiceFactory.plugin("tree-repository")).build();
			}
			else{
				logger.debug("specific endpoint: "+endpoint+":"+port);
				binder = TServiceFactory.binder().at(endpoint, port).build();
			}
			if(binder==null){logger.error("binder is null");return null;}
			if(sourceID==null){logger.debug("sourceID was not given, create a random one");sourceID = uuidgen.nextUUID();}

			BindSource request = new BindSource(sourceID);
			BindRequest params = new BindRequest("tree-repository",request.toElement());

			Binding binding = binder.bind(params).get(0);
			String id = binding.source();
			if(id!=null)logger.debug("tree source='"+id+"' has been created..");
			else logger.debug("creation of tree source='"+id+"' returned null!!");

			return id;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public String removeGenericResource(String id){
		try{
			//first query to get the resources
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/ID/text() eq '"+id+"'");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			 
			List<GenericResource> resources = client.submit(query);
			
			RegistryPublisher rp=RegistryPublisherFactory.create();
			
			for (GenericResource resource :resources)
				rp.remove(resource);
			
			return "OK";
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getTreeSources(String type) {		
		try{	
			String scope=ScopeProvider.instance.get();
			List<String> gresources=new ArrayList<String>();
			ISClient client = GHNContext.getImplementation(ISClient.class);
			if(client==null){logger.error("client=null");return null;}
			WSResourceQuery WSquery = client.getQuery(WSResourceQuery.class);

			List<RPDocument> docList = client.execute(WSquery,GCUBEScope.getScope(scope));
			if(docList!=null){
				if(type!=null)logger.debug("getTreeSources - type="+type);
				else logger.debug("getTreeSources - type=both readers and writers");
			}else{
				logger.error("getTreeSources -list from query=null");
				return null;
			}

			for (RPDocument resource : docList){				
				if(resource.getServiceClass()==null || 
						resource.getServiceClass().compareTo("DataAccess")!=0 ||
						resource.getServiceName()==null || 
						resource.getServiceName().compareTo("tree-manager-service")!=0){
					continue;
				}
				if(!resource.getEndpoint().getAddress().toString().endsWith("reader") &&
						!resource.getEndpoint().getAddress().toString().endsWith("writer")){
					continue;
				}
				if(type!=null){
					if(!resource.getEndpoint().getAddress().toString().endsWith(type))
						continue;
				}
				//getting the cardinality
				int num=0;
				String cardinality=org.gcube.datatransfer.agent.impl.utils.Utils.getParameterFromWSResource(resource,"Cardinality");
				if(cardinality.startsWith("no_")) ;//we keep it - no cardinality info 
				else{
					try{
						num = Integer.valueOf(cardinality);
					}catch(Exception e){e.printStackTrace();num=0;}
				}

				//we omit the empty read sources
				if(resource.getEndpoint().getAddress().toString().endsWith("reader")){
					if(num<1)continue;
				}

				String id = resource.getKey().getValue();
				String name=org.gcube.datatransfer.agent.impl.utils.Utils.getParameterFromWSResource(resource,"Name");
				//there is no 'Description' in wsResources
				//String description=getParameter(resource,"Description");
				//String treeSource=id+"--"+name+"--"+num+"--"+description;
				String treeSource=id+"--"+name+"--"+num;
				logger.debug("treeSource="+treeSource);

				//treeSource structure: 
				// id--name--cardinality
				gresources.add(treeSource);		
			}

			XStream xstreamClient = new XStream();		
			return xstreamClient.toXML(gresources);
		}catch(Exception e){				
			e.printStackTrace();
			return null;
		}		
	}

	public MonitorTransferReportMessage monitorTransferWithProgress(String transferId) throws MonitorTransferFault {

		if (transferId == null)
			throw Utils.newFault(new MonitorTransferFault(), new Exception("The Transfer ID is null"));

		MonitorTransferReportMessage message = null;
		try {
			message = ServiceContext.getContext().getDbManager().getTrasferProgress(transferId);
		}catch (Exception e){
			e.printStackTrace();
			throw  Utils.newFault(new MonitorTransferFault(),e);
		}
		return message;
	}

	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}