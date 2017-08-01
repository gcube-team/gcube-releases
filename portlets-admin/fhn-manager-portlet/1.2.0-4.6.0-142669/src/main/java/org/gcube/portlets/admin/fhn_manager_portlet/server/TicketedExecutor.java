package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.server.cache.CacheManager;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.Operation;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.SerializableStorableSet;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.UnexpectedException;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNodeStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.InvalidObjectException;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.MissingParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TicketedExecutor implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(TicketedExecutor.class);

	private static final ExecutorService executorService=Executors.newCachedThreadPool();

	/**
	 * Dynamic parameters key 
	 */

	public static String REMOTE_NODE_PARAM=Constants.REMOTE_NODE_ID;
	public static String TEMPLATE_PARAMETER=Constants.VM_TEMPLATE_ID;
	public static String PROVIDER_PARAMETER=Constants.VM_PROVIDER_ID;
	public static String CLEAN_TEMPLATES_FLAG="CLEAN_TEMPLATES";
	public static String CLEAN_NODES_FLAG="CLEAN_NODES";
	public static String SERVICE_PROFILE_PARAMETER=Constants.SERVICE_PROFILE_ID;
	public static String OBJECT_ID="OBJECT_ID";
	public static String OBJECT_PARAMETER="OBJECT_PARAMETER";
	public static String OBJECT_TYPE="OBJECT_TYPE";

	private static ConcurrentHashMap<String, TicketedExecutor> executorsMap=new ConcurrentHashMap<>();

	public static ProgressMessage getProgress(String ticketId) throws UnexpectedException{
		if(executorsMap.containsKey(ticketId)) return executorsMap.get(ticketId).getMessage();
		else throw new UnexpectedException("Operation expired");
	}

	public static OperationTicket submitRequest(UserInformation session, Map<String,Object> parameters, Operation operation){
		TicketedExecutor executor=new TicketedExecutor(session, parameters, operation);
		executorsMap.put(executor.getRequest().getId(), executor);
		OperationTicket toReturn= executor.getRequest();
		executorService.execute(executor);
		return toReturn;
	}


	//******************** INSTANCE

	private Map<String,Object> parameters;
	private OperationTicket request;
	private UserInformation sessionInfo;
	private ProgressMessage message;
	private IsSerializable theResult=null;


	private TicketedExecutor(UserInformation session, Map<String,Object> parameters,Operation operation){
		this.sessionInfo=session;
		this.parameters=parameters;
		request=new OperationTicket(UUID.randomUUID().toString(), operation, System.currentTimeMillis());
		message=new ProgressMessage(0.0d, "Queued operation..", ProgressStatus.PENDING, request,null);
	}

	public OperationTicket getRequest() {
		return request;
	}

	public ProgressMessage getMessage() {
		return message;
	}

	@Override
	public void run() {
		try{
			logger.trace("Setting user in thread {} ",sessionInfo);
			SecurityTokenProvider.instance.set(sessionInfo.getToken());		
			ScopeProvider.instance.set(sessionInfo.getContext());
			logger.trace("Executing operation "+request.getOperation()+" Parameters are :");
			for(Entry<String,Object> parameter: parameters.entrySet())
				logger.trace(parameter.getKey()+" : "+parameter.getValue());
			switch(request.getOperation()){
			case CREATE_OBJECT : {
				updateProgress(0.1d,"Gathering request parameters..", ProgressStatus.ONGOING);
				ObjectType type=(ObjectType) getParam(OBJECT_TYPE);
				switch(type){
				case REMOTE_NODE :{
					String profileId=(String) getParam(SERVICE_PROFILE_PARAMETER);
					String templateId=(String) getParam(TEMPLATE_PARAMETER);
					String providerId=(String) getParam(PROVIDER_PARAMETER);
					updateProgress(0.2d,"Submitting request to service..", ProgressStatus.ONGOING);					
					FHNManagerServiceImpl.getService().createNode(profileId, templateId, providerId);
					
					CacheManager.getCache(sessionInfo).invalidateNodesCache();
					updateProgress(0.9d, "Done.",ProgressStatus.SUCCESS);
					break;
				}	
				default : throw new Exception("Operation not yet supported"); 
				}
				break;
			}
			case DESTROY_OBJECT: {
				updateProgress(0.1d,"Gathering request parameters..", ProgressStatus.ONGOING);	
				ObjectType type=(ObjectType) getParam(OBJECT_TYPE);
				String id=(String) getParam(OBJECT_ID);
				updateProgress(0.2d,"Submitting request to service..", ProgressStatus.ONGOING);
				switch(type){
				case REMOTE_NODE : FHNManagerServiceImpl.getService().destroyNode(id);
				CacheManager.getCache(sessionInfo).invalidateNodesCache();
				break;
				case SERVICE_PROFILE : throw new Exception("Operation not yet supported for this resource type "+type.getLabel());

				case VM_PROVIDER: throw new Exception("Operation not yet supported for this resource type "+type.getLabel());

				case VM_TEMPLATES: throw new Exception("Operation not yet supported for this resource type "+type.getLabel());

				}
				updateProgress(1d,"Successfully removed",ProgressStatus.SUCCESS);
				break;
			}
			case START_NODE : {
				updateProgress(0.1d,"Gathering request parameters..", ProgressStatus.ONGOING);	
				String id=(String) getParam(OBJECT_ID);
				updateProgress(0.2d,"Submitting request to service..", ProgressStatus.ONGOING);
				RemoteNodeStatus previous=FHNManagerServiceImpl.getService().getNodeById(id).getStatus();
				
				FHNManagerServiceImpl.getService().startNode(id);
				long startTime=System.currentTimeMillis();
				RemoteNodeStatus current=null;
				
				int iteration=1;
				
				do{
					String formattedElapsedTime=formatTime(System.currentTimeMillis()-startTime);
					updateProgress(0.8d,"Waiting for node to start. Elapsed time : "+formattedElapsedTime+"",ProgressStatus.ONGOING);
					try{
						Thread.sleep(500*iteration);
					}catch(InterruptedException e){}
					current=FHNManagerServiceImpl.getService().getNodeById(id).getStatus();
					logger.debug("Polling remote node status. Previous was : "+previous+" current is "+current+" Elapsed time from request : "+formattedElapsedTime);
					iteration=iteration<5?iteration+1:iteration;
				}while(current.equals(previous));
				
				CacheManager.getCache(sessionInfo).invalidateNodesCache();
				updateProgress(1d,"Node successfully started.",ProgressStatus.SUCCESS);
				
				break;
			}

			case STOP_NODE : {
				updateProgress(0.1d,"Gathering request parameters..", ProgressStatus.ONGOING);	
				String id=(String) getParam(OBJECT_ID);
				updateProgress(0.2d,"Submitting request to service..", ProgressStatus.ONGOING);				
				
				RemoteNodeStatus previous=FHNManagerServiceImpl.getService().getNodeById(id).getStatus();
				
				FHNManagerServiceImpl.getService().stopNode(id);
				long startTime=System.currentTimeMillis();
				RemoteNodeStatus current=null;
				int iteration=1;
				do{
					String formattedElapsedTime=formatTime(System.currentTimeMillis()-startTime);
					updateProgress(0.8d,"Waiting for node to stop. Elapsed time : "+formattedElapsedTime+"",ProgressStatus.ONGOING);
					try{
						Thread.sleep(500*iteration);
					}catch(InterruptedException e){}
					current=FHNManagerServiceImpl.getService().getNodeById(id).getStatus();
					logger.debug("Polling remote node status. Previous was : "+previous+" current is "+current+" Elapsed time from request : "+formattedElapsedTime);
					iteration=iteration<5?iteration+1:iteration;
				}while(current.equals(previous));
				
				CacheManager.getCache(sessionInfo).invalidateNodesCache();
				updateProgress(1d,"Node successfully stopped.",ProgressStatus.SUCCESS);
				break;
			}

			case GATHER_INFORMATION:{
				updateProgress(0.1d,"Gathering object information..",ProgressStatus.ONGOING);
				Storable theObject=(Storable) getParam(OBJECT_PARAMETER);

				VMManagerServiceInterface service=FHNManagerServiceImpl.getService();
				updateProgress(0.2d,"Gathering object information..",ProgressStatus.ONGOING);				
				DescribedResource toReturn=service.describeResource(theObject.getType(), theObject.getKey());

				switch(theObject.getType()){
				case REMOTE_NODE :{
					RemoteNode theNode=(RemoteNode) theObject;
					updateProgress(0.5d,"Gathering VM Provider information..",ProgressStatus.ONGOING);			
					toReturn.add(service.describeResource(ObjectType.VM_PROVIDER, theNode.getVmProviderId()));
					updateProgress(0.7d,"Gathering VM Template information..",ProgressStatus.ONGOING);
					toReturn.add(service.describeResource(ObjectType.VM_TEMPLATES, theNode.getVmTemplateId()));
					updateProgress(0.9d,"Gathering Service Profile information..",ProgressStatus.ONGOING);
					toReturn.add(service.describeResource(ObjectType.SERVICE_PROFILE, theNode.getServiceProfileId()));
					break;
				}
				case SERVICE_PROFILE : {

				}
				case VM_PROVIDER: {

				}
				case VM_TEMPLATES : {

				}
				}				
				this.theResult=toReturn;
				updateProgress(1d,"Information ready",ProgressStatus.SUCCESS);
				break;
			}
			
			
			case ACCESS_CACHE:{
				updateProgress(0.5d,"Accessing cache..",ProgressStatus.ONGOING);
				
				ObjectType type=(ObjectType) getParam(OBJECT_TYPE);
				
				String serviceProfileId=null;
				String vmProviderId=null;
				String vmTemplateId=null;
				if(parameters!=null){
					if(parameters.containsKey(Constants.SERVICE_PROFILE_ID)) serviceProfileId=(String) parameters.get(Constants.SERVICE_PROFILE_ID);
					if(parameters.containsKey(Constants.VM_PROVIDER_ID)) vmProviderId=(String) parameters.get(Constants.VM_PROVIDER_ID);
					if(parameters.containsKey(Constants.VM_TEMPLATE_ID)) vmTemplateId=(String) parameters.get(Constants.VM_TEMPLATE_ID);
				}
				
				HashSet<Storable> toReturn=null;
				switch(type){
				
				case REMOTE_NODE : {toReturn=new HashSet<Storable>(CacheManager.getCache(sessionInfo).getNodes(serviceProfileId, vmProviderId));
				break;
				}
				case SERVICE_PROFILE : {
					toReturn=new HashSet<Storable>(CacheManager.getCache(sessionInfo).getProfiles());
					break;
				}
				case VM_PROVIDER : {
					toReturn=new HashSet<Storable>(CacheManager.getCache(sessionInfo).getProviders(serviceProfileId, vmTemplateId));
					break;
				}
				case VM_TEMPLATES : {
					toReturn=new HashSet<Storable> (CacheManager.getCache(sessionInfo).getTemplates(serviceProfileId, vmProviderId));
					break;
				}
				default : throw new Exception("Not recognized type "+type); 
				}
				
				this.theResult=new SerializableStorableSet(toReturn);
				updateProgress(1d,"Information ready",ProgressStatus.SUCCESS);
			}
			
			//END SWITCH
			}
		}catch(MissingParameterException e){
			logger.debug("Missing parameter "+logOperation(),e);
			updateProgress("Error : "+e.getMessage(),ProgressStatus.ERROR);
		}catch(InvalidObjectException e){
			logger.debug("Invalid parameter while performing "+logOperation(),e);
			updateProgress("Invalid parameter : "+e.getMessage(),ProgressStatus.ERROR);
		}catch(ClassCastException e){
			logger.error("Wrong parameter cast operation while performing "+logOperation(),e);
			updateProgress("Wrong parameters.",ProgressStatus.ERROR);
		}catch(Throwable t){
			logger.error("Unexpected exception while performing "+logOperation(),t);
			updateProgress("Unexpected error : "+t.getMessage(),ProgressStatus.ERROR);			
		}
	}


	private Object getParam(String key) throws MissingParameterException{
		if(!parameters.containsKey(key)) throw new MissingParameterException(String.format("Parameter %s is mandatory.", key));
		return parameters.get(key);
	}

	// Progress utilities

	private void updateProgress(Double progress,String message,ProgressStatus status){
		this.message=new ProgressMessage(progress,message,status,this.message.getTicket(),theResult);
	}

	private void updateProgress(String message,ProgressStatus status){
		this.message=new ProgressMessage(this.message.getProgressCount(),message,status,this.message.getTicket(),theResult);
	} 

	private String logOperation(){
		return String.format("%s [parameters : %s]", request.getOperation(),parameters);
	}
	
	private static String formatTime(long millis){
		if(millis>3600000)
			return String.format("%02dh %02d' %02d\"", TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		else if(millis>60000) return String.format("%02d' %02d\"",
			    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		else return String.format("%02d\"",			    
			    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
	}
	
	
	
}
