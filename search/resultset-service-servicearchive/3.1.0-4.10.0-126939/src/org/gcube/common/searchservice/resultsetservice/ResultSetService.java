package org.gcube.common.searchservice.resultsetservice;
 
//import java.io.File; //ws-core 4.1 specific for attachments
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.searchservice.resultsetservice.stubs.AccessResourceRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.AccessResourceResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.AddResultsRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.AddTextRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.CanStreamResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.ClearResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.CreateResourceExtendedRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.CreateResourceRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.CreateResourceResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.CreateSerializedResourceRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.CreateSerializedResourceResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.DestroySessionResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.EndAuthoringResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.ExecuteQueryOnDocumentRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.ExecuteQueryOnHeadRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.ExecuteQueryOnResultsRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.ExecuteQueryOnResultsResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.ExtendAccessLeasingRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.ExtendAccessLeasingResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.ExtendTimeLeasingRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.ExtendTimeLeasingResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.FilterRSPropRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.FilterRSRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetAllResultsResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.GetFileContentRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetNextPartRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetNumberOfResultsRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetPropertiesRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetPropertiesResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.GetResultRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetResultsRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.GetResultsResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.KeepTopPropRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.KeepTopRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.SetForwardRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.SplitClearResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.SplitEncodedResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.StartNewPartResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.TransformByXSLTRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.TransformRSPropRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.WrapLocalFileRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.WrapLocalFileResponse;
import org.gcube.common.searchservice.resultsetservice.stubs.WrapResourceRequest;
import org.gcube.common.searchservice.resultsetservice.stubs.WrapResourceResponse;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.CreationParams;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.StreamManager;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.WSRSSessionToken;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.impl.SimpleTopic;
import org.globus.wsrf.utils.AddressingUtils;

import org.gcube.common.searchservice.searchlibrary.GarbageCollector.GarbageCollect;

 
/**
 * Web service fron end to a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class ResultSetService extends GCUBEPortType  {
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(ResultSetService.class);
	/**
	 * The stream manager for streaming rs parts
	 */
	StreamManager sm =null;
	/**
	 * Resource Home used for non WSRF functionality
	 */
	private WSResultSetHome resourceHome=null; 
	/**
	 * The port specified for the transport manager
	 */
	private int port=-1;
	/**
	 * SimpleTopic for notifying about garbage-collected ResultSet EPRs
	 */
	private boolean SSLsupport=true;
	/**
	 * SimpleTopic for notifying about garbage-collected ResultSet EPRs
	 */
	private static SimpleTopic reclaimingEprs;
	
	/**
	 * Intializes a new instance and creates  a tcp listener
	 */
	public ResultSetService(){

		try{
			
			log.info("Initializing");
			if(sm==null){
//				Context envContext = (Context) JNDIUtils.initJNDI().lookup("java:comp/env/services/gcube/searchservice/ResultSetService");
//				Integer port=(Integer)JNDIUtils.lookup(envContext,"streamPort",Integer.class);
				this.port=getPort();
				this.SSLsupport=getSSLsupport();
				if(this.port>0){
					sm=new StreamManager(this.port, this.SSLsupport);
					sm.start();
				}
			}
			if(resourceHome==null){
				this.resourceHome=new WSResultSetHome();
			}
			
		}catch(Exception e){
			log.error("Could not initialize Streammanager. FATAL ERROR",e);
		}
	}
	
	/**
	 * retrieves the port
	 * 
	 * @return the port
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	private int getPort() throws Exception{
/*		Context envContext = (Context) JNDIUtils.initJNDI().lookup("java:comp/env/services/gcube/searchservice/ResultSetService");
		Integer port=(Integer)JNDIUtils.lookup(envContext,"streamPort",Integer.class);*/
		return ((Integer)StatefulContext.getPortTypeContext().getProperty(StatefulContext.STREAM_PORT, true)).intValue();
//		return port.intValue();
	}
	
	/**
	 * retrieves SSL support
	 * 
	 * @return true if SSL is supported
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	private boolean getSSLsupport() throws Exception{
/*		Context envContext = (Context) JNDIUtils.initJNDI().lookup("java:comp/env/services/gcube/searchservice/ResultSetService");
		Integer port=(Integer)JNDIUtils.lookup(envContext,"streamPort",Integer.class);*/
		String SSL = ((String)StatefulContext.getPortTypeContext().getProperty(StatefulContext.SSLSUPPORT, true));
		return SSL.equalsIgnoreCase("enabled");
//		return port.intValue();
	}
	
	/**
	 * 
	 */
	public static void ready() {
		try{
		log.info("Start garbage collector");
		
		//create a new resource that expresses the Garbage Collector used by the Result Set Service
		GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();
				
		GCUBEWSResource resource = null;
		for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
			try {
				//repeat the creation here for the scope
				ServiceContext.getContext().setScope(scope);
				if (resource == null) {
					resource = ptctx.getWSHome().create(new Object[]{new Boolean(true)});
					log.trace("created reclaimed-RS-topic resource in scope: " + scope);
				}else {
					//reuse the resource in the scope
					ptctx.getWSHome().create(resource.getID());
					log.trace("associated reclaimed-RS-topic resource with scope: " + scope);
				}
			} catch(Exception e) {
				log.error("Could not create the global resource for Garbage Collector. Throwing RemoteException",e);
				throw new RemoteException("Could not create the global resource for Garbage Collector");
			}
		}
		
		try{
			//create the topic
			reclaimingEprs = new SimpleTopic(new QName("http://gcube.org/namespaces/searchservice/ResultSetService", "ReclaimingRSEprs"));
			ServiceContext.getContext().getStartScopes()[0].getInfrastructure();
			GCUBEScope[] scopes = new GCUBEScope[ServiceContext.getContext().getInstance().getScopes().values().size()];
			
			log.info("Registering a new topic: " + reclaimingEprs.toString() + " to IS");
			ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
			
			//and register it to the IS
			List <SimpleTopic> list = new ArrayList<SimpleTopic>();
			list.add(reclaimingEprs);
			resource.getTopicList().addTopic(reclaimingEprs);
			notifier.registerISNotification(resource.getEPR(), list, ServiceContext.getContext(), ServiceContext.getContext().getInstance().getScopes().values().toArray(scopes));
		}catch(Exception e)	{
			log.error("Could not register Topic: " + reclaimingEprs.toString(), e);
		}

		GarbageCollect gc = new GarbageCollect(reclaimingEprs);
		new Thread(gc).start();
		log.info("Start garbage collector started");
		}catch(Exception e){
			log.error("Could not start garbage collector.", e);
		}
	}

	
	/**
	 * Retrieves the referenced resource
	 * 
	 * @return The resource
	 * @throws Exception An unrecoveralbe for the operation error occured
	 */
	private ResultSetResource getResource() throws Exception {
		Object resource = null;
		handleScope();
		try {
			resource = ResourceContext.getResourceContext().getResource();
		} catch (Exception e) {
			log.error("could not retrieve ResultSet resource. Throwing Exception",e);
			throw new Exception("could not retrieve ResultSet resource");
		}
		ResultSetResource resultsetResource = (ResultSetResource) resource;
		return resultsetResource;
	}
	
	/**
	 * retrieves the ResultSet associated with the given id in case of a stateless invocation
	 * or the implied one in case of a stateful invocation
	 * 
	 * @param rsID the id
	 * @return the result set
	 * @throws Exception the resource does not exist
	 */
	private ResultSet getResultSet(String rsID) throws Exception{
		ResultSet rs=null;
		handleScope();
		if(rsID==null || rsID.trim().length()==0){
			rs=getResource().getResultSet();
		}
		else if(rsID!=null && rsID.trim().length()!=0){
			rs=this.resourceHome.getResultSet(rsID);
		}
		else{
			log.error("unrecoglinzed session token or resource identifier "+rsID+".Throwing Exception");
			throw new Exception("unrecoglinzed session token or resource identifier "+rsID);
		}
		return rs;
	}
	
	/**
	 * Creates a new resource holding a refernece to a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
	 * 
	 * @param request Autogenerated stub argument
	 * @return Autogenerated stub containing the epr to the created resource
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public CreateResourceResponse createResource(CreateResourceRequest request) throws RemoteException {
		if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WSRF.toString())){
			
			GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();

			EndpointReferenceType epr = null;
			GCUBEWSResource resource = null;
			URL baseURL=null;

//			if (ServiceContext.getContext().getScope() != null) {
			if (false) {
				// creates the resource in the ServiceContext.getContext().getScope() scope
				log.trace("Scope was set to : "+ServiceContext.getContext().getScope().getName());
				try {
					resource = ptctx.getWSHome().create(
							new Object[]{request.getProperties(), 
									request.isDataFlow()});
				} catch(Exception e) {
					log.error("Could not create new resource. Throwing RemoteException",e);
					throw new RemoteException("Could not create new resource");
				}
			} else {
				log.info("No Scope was set. Creating Resoure in all scopes.");
				for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {

					try {
						//repeat the creation here for the scope
						ServiceContext.getContext().setScope(scope);
						if (resource == null) {
							//create the RS from scratch
							resource = ptctx.getWSHome().create(
									new Object[]{request.getProperties(),
											request.isDataFlow()});
						} else {
							//reuse the resource in the scope
							ptctx.getWSHome().create(resource.getID());
						}
					} catch(Exception e) {
						log.error("Could not create new resource. Throwing RemoteException",e);
						throw new RemoteException("Could not create new resource");
					}
				}
			}
			
			
/*			ResourceContext ctx = null;
			ResultSetResourceHome home = null;
			ResourceKey key = null;
			
			try {
				ctx = ResourceContext.getResourceContext();
				home = (ResultSetResourceHome) ctx.getResourceHome();
				key = home.create(request.getProperties(),request.isDataFlow());
			} catch (Exception e) {
				log.error("Could not create new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create new resource");
			}
			EndpointReferenceType epr = null;
	*/
			try {
				baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				epr = AddressingUtils.createEndpointReference(instanceURI, resource.getID());
			} catch (Exception e) {
				log.error("Could not create EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create EPR to new resource");
			}
			
			try {
				((ResultSetResource)ptctx.getWSHome().find(resource.getID())).getResultSet()
				.addWSEPR(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
			} catch (Exception e) {
				log.error("Could not create serialization of EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create serialization of EPR to new resource");
			}
			CreateResourceResponse response = new CreateResourceResponse();
			response.setEndpointReference(epr);
			response.setSessionToken("");
			response.setPort(this.port);
			response.setSSLsupport(this.SSLsupport);
			return response;

		}
		else if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WS.toString())){
			try{
				ResultSet rs=new ResultSet(request.getProperties(),request.isDataFlow());
				URL baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				String token=WSRSSessionToken.generateSessionToken();
				WSRSSessionToken qtoken=new WSRSSessionToken(instanceURI,token);
				rs.addWSEPR(WSRSSessionToken.serialize(qtoken));
				CreateResourceResponse response = new CreateResourceResponse();
				response.setEndpointReference(new EndpointReferenceType());
				response.setSessionToken(WSRSSessionToken.serialize(qtoken));
				response.setPort(this.port);
				response.setSSLsupport(this.SSLsupport);
				this.resourceHome.addResultSet(qtoken.getSessionToken(),rs);
				return response;
			}catch(Exception e){
				log.error("could not create resource. throeing exception",e);
				throw new RemoteException("could not create resource");
			}
		}
		return null;
	}
	
	/**
	 * Creates a new resource holding a refernece to a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
	 * 
	 * @param request Autogenerated stub argument
	 * @return Autogenerated stub containing the epr to the created resource
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public CreateResourceResponse createResourceExtended(CreateResourceExtendedRequest request) throws RemoteException {
		if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WSRF.toString())){
			
			GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();

			EndpointReferenceType epr = null;
			GCUBEWSResource resource = null;
			URL baseURL=null;
//			if (ServiceContext.getContext().getScope() != null) {
			if (false) {
				// creates the resource in the ServiceContext.getContext().getScope() scope
				log.trace("Scope was set to : "+ServiceContext.getContext().getScope().getName());
				try {
							resource = ptctx.getWSHome().create(
									new Object[]{
											request.getProperties(), 
											request.isDataFlow(),
											request.getAccessReads(),
											request.isForward(),
											request.getExpire_date(),
											request.getPKey()
										});
				} catch(Exception e) {
					log.error("Could not create new resource. Throwing RemoteException",e);
					throw new RemoteException("Could not create new resource");
				}
			} else {
				log.trace("No Scope was set. Creating Resoure in all scopes.");
				for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {

					try {
						//repeat the creation here for the scope
						ServiceContext.getContext().setScope(scope);
						if (resource == null) {
							//create the RS from scratch
							resource = ptctx.getWSHome().create(
									new Object[]{
											request.getProperties(), 
											request.isDataFlow(),
											request.getAccessReads(),
											request.isForward(),
											request.getExpire_date(),
											request.getPKey()
										});
						} else {
							//reuse the resource in the scope
							ptctx.getWSHome().create(resource.getID());
						}
					} catch(Exception e) {
						log.error("Could not create new resource. Throwing RemoteException",e);
						throw new RemoteException("Could not create new resource");
					}
				}
			}
			
			
			try {
				baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				epr = AddressingUtils.createEndpointReference(instanceURI, resource.getID());
			} catch (Exception e) {
				log.error("Could not create EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create EPR to new resource");
			}
			
			try {
				((ResultSetResource)ptctx.getWSHome().find(resource.getID())).getResultSet()
				.addWSEPR(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
			} catch (Exception e) {
				log.error("Could not create serialization of EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create serialization of EPR to new resource");
			}
			CreateResourceResponse response = new CreateResourceResponse();
			response.setEndpointReference(epr);
			response.setSessionToken("");
			response.setPort(this.port);
			response.setSSLsupport(this.SSLsupport);
			return response;

		}
		else if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WS.toString())){
			try{
				CreationParams createParams = new CreationParams();
				createParams.properties = new ArrayList<String>(Arrays.asList(request.getProperties()));
				createParams.setDataflow(request.isDataFlow());
				createParams.setAccessReads(request.getAccessReads());
				createParams.setForward(request.isForward());
				Date expire_date = new Date(0);
				expire_date.setTime(request.getExpire_date());
				createParams.setExpire_date(expire_date);
				if (request.getPKey() != null){
					byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(request.getPKey()));
					X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(rawkey);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					PublicKey pk = (RSAPublicKey)kf.generatePublic(publicKeySpec);
					createParams.setPKey(pk);
				}

				ResultSet rs=new ResultSet(createParams);
				
				URL baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				String token=WSRSSessionToken.generateSessionToken();
				WSRSSessionToken qtoken=new WSRSSessionToken(instanceURI,token);
				rs.addWSEPR(WSRSSessionToken.serialize(qtoken));
				CreateResourceResponse response = new CreateResourceResponse();
				response.setEndpointReference(new EndpointReferenceType());
				response.setSessionToken(WSRSSessionToken.serialize(qtoken));
				response.setPort(this.port);
				response.setSSLsupport(this.SSLsupport);
				this.resourceHome.addResultSet(qtoken.getSessionToken(),rs);
				return response;
			}catch(Exception e){
				log.error("could not create resource. throeing exception",e);
				throw new RemoteException("could not create resource");
			}
		}
		return null;
	}
	
	/**
	 * Creates a new resource holding a refernece to a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
	 * 
	 * @param request Autogenerated stub input
	 * @return Autogenerated stub containing the epr to the created resource
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public CreateSerializedResourceResponse createSerializedResource(CreateSerializedResourceRequest request) throws RemoteException {
		if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WSRF.toString())){
			
			GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();
			
			EndpointReferenceType epr = null;
			GCUBEWSResource resource = null;
			URL baseURL=null;
//			if (ServiceContext.getContext().getScope() != null) {
			if (false) {
				log.trace("Scope was set to : "+ServiceContext.getContext().getScope().getName());
				try {
					resource = ptctx.getWSHome().create(
							new Object[]{request.getProperties(), 
									request.isDataFlow()});
				} catch(Exception e) {
					log.error("Could not create new resource. Throwing RemoteException",e);
					throw new RemoteException("Could not create new resource");
				}
			}else{
				log.trace("No Scope was set. Creating Resoure in all scopes.");
				for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
					try {
                    ServiceContext.getContext().setScope(scope);
                    if (resource == null) {
                         //create the RS from scratch
						resource = ptctx.getWSHome().create(
								new Object[]{request.getProperties(), 
										request.isDataFlow()});
                        } else {
                                //reuse the resource in the scope
                               ptctx.getWSHome().create(resource.getID());
                     }

					} catch(Exception e) {
						log.error("Could not create new resource. Throwing RemoteException",e);
						throw new RemoteException("Could not create new resource");
					}

				}
			}
			
		/*
			ResourceContext ctx = null;
			ResultSetResourceHome home = null;
			ResourceKey key = null;
			URL baseURL=null;
			try {
				ctx = ResourceContext.getResourceContext();
				home = (ResultSetResourceHome) ctx.getResourceHome();
				key = home.createSerialized(request.getProperties(),request.isDataFlow());
			} catch (Exception e) {
				log.error("Could not create new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create new resource");
			}
			EndpointReferenceType epr = null;
			*/
			try {
				baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				epr = AddressingUtils.createEndpointReference(instanceURI, resource.getID());
			} catch (Exception e) {
				log.error("Could not create EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create EPR to new resource");
			}
			
			try {
				((ResultSetResource)ptctx.getWSHome().find(resource.getID())).getResultSet().addWSEPR(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
			} catch (Exception e) {
				log.error("Could not create serialization of EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create serialization of EPR to new resource");
			}
			CreateSerializedResourceResponse response = new CreateSerializedResourceResponse();
			response.setEndpointReference(epr);
			response.setSessionToken("");
			response.setPort(this.port);
			response.setSSLsupport(this.SSLsupport);
			return response;
		}
		else if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WS.toString())){
			try{
				ResultSet rs=new ResultSet(request.getProperties(),request.isDataFlow());
				URL baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				String token=WSRSSessionToken.generateSessionToken();
				WSRSSessionToken qtoken=new WSRSSessionToken(instanceURI,token);
				rs.addWSEPR(WSRSSessionToken.serialize(qtoken));
				CreateSerializedResourceResponse response = new CreateSerializedResourceResponse();
				response.setEndpointReference(new EndpointReferenceType());
				response.setSessionToken(WSRSSessionToken.serialize(qtoken));
				response.setPort(this.port);
				response.setSSLsupport(this.SSLsupport);
				this.resourceHome.addResultSet(qtoken.getSessionToken(),rs);
				return response;
			}catch(Exception e){
				log.error("could not create resource. throwing exception",e);
				throw new RemoteException("could not create resource");
			}
		}
		return null;
	}
	
	/**
	 * Creates a new resource holding a refernece to a new {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * which is created based on an existing {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
	 * 
	 * @param request autogenerated stub input
	 * @return Autogenerated stub containing the epr to the created resource
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public WrapResourceResponse wrapResource(WrapResourceRequest request) throws RemoteException {
		if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WSRF.toString())){
			
			GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();
			if(ptctx == null) {
				log.error("StatefulContext is null.");
				throw new RemoteException("StatefulContext is null.");
			} else if(ptctx.getWSHome() == null) {
				log.error("ptctx.getWSHome() is null.");
				throw new RemoteException("ptctx.getWSHome() is null.");
			}
			log.debug("Head filename: " + request.getHeadFileName());
			log.trace("key type QName: " + ptctx.getWSHome().getKeyTypeName());

			EndpointReferenceType epr = null;
			GCUBEWSResource resource = null;
			URL baseURL=null;

//			if (ServiceContext.getContext().getScope() != null) {
			if (false) {
				log.trace("Scope was set to : "+ServiceContext.getContext().getScope().getName());
				try {
					if (request.getPrivateKey() == null)
						resource = ptctx.getWSHome().create(new Object[]{request.getHeadFileName()});
					else
						resource = ptctx.getWSHome().create(new Object[]{
								request.getHeadFileName(),
								request.getPrivateKey()
								});					
				} catch(Exception e) {
					log.error("Could not create new resource. Throwing RemoteException",e);
					throw new RemoteException("Could not create new resource");
				}
			}else{
				for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
					log.trace("No scope was set. Creating resource in scope : "+scope.getName());
					try {
                        ServiceContext.getContext().setScope(scope);
                        if (resource == null) {
                        	log.trace("Create new resource");
                            //create the RS from scratch
            				if (request.getPrivateKey() == null)
            					resource = ptctx.getWSHome().create(new Object[]{request.getHeadFileName()});
            				else
            					resource = ptctx.getWSHome().create(new Object[]{
            							request.getHeadFileName(),
            							request.getPrivateKey()
            							});					
                           } else {
                                   //reuse the resource in the scope
                        	   	  log.trace("Reuse resource");
                                  ptctx.getWSHome().create(resource.getID());
                        }
					} catch(Exception e) {
						log.error("Could not create new resource. Throwing RemoteException",e);
						throw new RemoteException("Could not create new resource");
					}

				}
			}

			
			try {
				baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				epr = AddressingUtils.createEndpointReference(instanceURI, resource.getID());
			} catch (Exception e) {
				log.error("Could not create EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create EPR to new resource");
			}
			
			try {
				((ResultSetResource)ptctx.getWSHome().find(resource.getID())).getResultSet().addWSEPR(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
			} catch (Exception e) {
				log.error("Could not create serialization of EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create serialization of EPR to new resource");
			}
			WrapResourceResponse response = new WrapResourceResponse();
			response.setEndpointReference(epr);
			response.setSessionToken("");
			response.setPort(this.port);
			response.setSSLsupport(this.SSLsupport);
			return response;
		}
		else if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WS.toString())){
			try{
				ResultSet rs = null;
				if (request.getPrivateKey() == null)
					rs = new ResultSet(request.getHeadFileName());
				else{
					byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(request.getPrivateKey()));
					X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(rawkey);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					PrivateKey pk = (RSAPrivateKey)kf.generatePrivate(publicKeySpec);
					rs = new ResultSet(request.getHeadFileName(),pk);
				}

				URL baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				String token=WSRSSessionToken.generateSessionToken();
				WSRSSessionToken qtoken=new WSRSSessionToken(instanceURI,token);
				rs.addWSEPR(WSRSSessionToken.serialize(qtoken));
				WrapResourceResponse response = new WrapResourceResponse();
				response.setEndpointReference(new EndpointReferenceType());
				response.setSessionToken(WSRSSessionToken.serialize(qtoken));
				response.setPort(this.port);
				response.setSSLsupport(this.SSLsupport);
				this.resourceHome.addResultSet(qtoken.getSessionToken(),rs);
				return response;
			}catch(Exception e){
				log.error("could not create resource. throeing exception",e);
				throw new RemoteException("could not create resource");
			}
		}
		return null;
	}
	
	/**
	 * Creates a new resource holding a new reference to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * that was used to access the method
	 * 
	 * @param request autogenerated stub input
	 * @return Autogenerated stub holding the EPR to the created resource
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public AccessResourceResponse accessResource(AccessResourceRequest request) throws RemoteException{
		if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WSRF.toString())){
			
			GCUBEStatefulPortTypeContext ptctx = StatefulContext.getPortTypeContext();

			EndpointReferenceType epr = null;
			GCUBEWSResource resource = null;
			URL baseURL=null;
//          if (ServiceContext.getContext().getScope() != null) {
			if (false) {
				log.trace("Scope was set to : "+ServiceContext.getContext().getScope().getName());

			try {
				if (request.getPrivateKey() == null)
					resource = ptctx.getWSHome().create(new Object[]{getResource().getResultSet().getHeadName()});
				else
					resource = ptctx.getWSHome().create(new Object[]{
							getResource().getResultSet().getHeadName(),
							request.getPrivateKey()
							});					
			} catch(Exception e) {
				log.error("Could not create new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create new resource");
			}
            }else{
				log.info("No Scope was set.");
               for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
                	 
                    try {
                         //repeat the creation here for the scope
                              ServiceContext.getContext().setScope(scope);
                          if (resource == null) {
                               //create the RS from scratch
              				if (request.getPrivateKey() == null)
            					resource = ptctx.getWSHome().create(new Object[]{getResource().getResultSet().getHeadName()});
            				else
            					resource = ptctx.getWSHome().create(new Object[]{
            							getResource().getResultSet().getHeadName(),
            							request.getPrivateKey()
            							});					
                              } else {
                                      //reuse the resource in the scope
                                     ptctx.getWSHome().create(resource.getID());
                           }
                     } catch(Exception e) {
         				log.error("Could not create new resource. Throwing RemoteException",e);
        				throw new RemoteException("Could not create new resource");
                   }
             }

            }

			/*
			ResourceContext ctx = null;
			ResultSetResourceHome home = null;
			ResourceKey key = null;
			URL baseURL=null;
			
			try {
				ctx = ResourceContext.getResourceContext();
				home = (ResultSetResourceHome) ctx.getResourceHome();
				key = home.create(getResource().getResultSet().getHeadName());
			} catch (Exception e) {
				log.error("Could not create new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create new resource");
			}
			EndpointReferenceType epr = null;*/
			
			try {
				baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				epr = AddressingUtils.createEndpointReference(instanceURI, resource.getID());
			} catch (Exception e) {
				log.error("Could not create EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create EPR to new resource");
			}
			
			try {
				((ResultSetResource)ptctx.getWSHome().find(resource.getID())).getResultSet().addWSEPR(ObjectSerializer.toString(epr,ResultSetQNames.RESOURCE_REFERENCE));
			} catch (Exception e) {
				log.error("Could not create serialization of EPR to new resource. Throwing RemoteException",e);
				throw new RemoteException("Could not create serialization of EPR to new resource");
			}
			
			AccessResourceResponse response = new AccessResourceResponse();
			response.setEndpointReference(epr);
			response.setSessionToken("");
			response.setPort(this.port);
			response.setSSLsupport(this.SSLsupport);
			return response;
		}
		else if(request.getResourceType().equalsIgnoreCase(RSConstants.RESOURCETYPE.WS.toString())){
			try{
				String headFileName=this.getResultSet(request.getSessionToken()).getHeadName();
				ResultSet rs = null;
				if (request.getPrivateKey() == null)
					rs = new ResultSet(headFileName);
				else{
					byte[] rawkey = new sun.misc.BASE64Decoder().decodeBuffer(new String(request.getPrivateKey()));
					X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(rawkey);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					PrivateKey pk = (RSAPrivateKey)kf.generatePrivate(publicKeySpec);
					rs = new ResultSet(headFileName,pk);
				}
				URL baseURL = ServiceHost.getBaseURL();
				String instanceService = (String) MessageContext.getCurrentContext().getService().getOption("instance");
				String instanceURI = baseURL.toString() + instanceService;
				String token=WSRSSessionToken.generateSessionToken();
				WSRSSessionToken qtoken=new WSRSSessionToken(instanceURI,token);
				rs.addWSEPR(WSRSSessionToken.serialize(qtoken));
				AccessResourceResponse response = new AccessResourceResponse();
				response.setEndpointReference(new EndpointReferenceType());
				response.setSessionToken(WSRSSessionToken.serialize(qtoken));
				response.setPort(this.port);
				response.setSSLsupport(this.SSLsupport);
				this.resourceHome.addResultSet(qtoken.getSessionToken(),rs);
				return response;
			}catch(Exception e){
				log.error("could not create resource. throeing exception",e);
				throw new RemoteException("could not create resource");
			}
		}
		return null;
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#startNewPart()}
	 * 
	 * @param sessionToken Session token
	 * @return autogenerated stub output
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public StartNewPartResponse startNewPart(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(sessionToken).startNewPart();
			return new StartNewPartResponse();
		}catch(Exception e){
			log.error("Could not create new part. Throwing RemoteException",e);
			throw new RemoteException("Could not create new part");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#clear()}
	 * 
	 * @param sessionToken the session token
	 * @return autogenerated stub output
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public ClearResponse clear(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(sessionToken).clear();
			return new ClearResponse();
		}catch(Exception e){
			log.error("Could not clear underlying structures. Throwing RemoteException",e);
			throw new RemoteException("Could not clear underlying structures");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#endAuthoring()}
	 * 
	 * @param sessionToken the session token
	 * @return autogenerated stub output
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public EndAuthoringResponse endAuthoring(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(sessionToken).endAuthoring();
			return new EndAuthoringResponse();
		}catch(Exception e){
			log.error("Could not end authoring. Throwing RemoteException",e);
			throw new RemoteException("Could not end authoring");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])}
	 * 
	 * @param params Autogenerates stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addResults(java.lang.String[])}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean addResults(AddResultsRequest params) throws RemoteException{
		handleScope();
		try{
			if(params.getResults()==null){
				return true;
			}
			if(params.getResults().length==0){
				return true;
			}
			return this.getResultSet(params.getSessionToken()).addResults(params.getResults());
		}catch(Exception e){
			log.error("Could not add results. Throwing RemoteException",e);
			throw new RemoteException("Could not add results");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addText(java.lang.String[])}
	 * 
	 * @param params Autogenerates stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#addText(java.lang.String[])}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean addText(AddTextRequest params) throws RemoteException{
		handleScope();
		try{
			if(params.getResults()==null){
				return true;
			}
			if(params.getResults().length==0){
				return true;
			}
			return this.getResultSet(params.getSessionToken()).addText(params.getResults());
		}catch(Exception e){
			log.error("Could not add text. Throwing RemoteException",e);
			throw new RemoteException("Could not add text");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#wrapFile(java.lang.String)}
	 * 
	 * @param params Autogenerated stub input
	 * @return autogenerated stub output
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public WrapLocalFileResponse wrapLocalFile(WrapLocalFileRequest params) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(params.getSessionToken()).wrapFile(params.getFilename());
			return new WrapLocalFileResponse();
		}catch(Exception e){
			log.error("Could not wrap local file. Throwing RemoteException",e);
			throw new RemoteException("Could not wrap local file");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHeadName()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHeadName()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getHeadFileName(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getHeadName();
		}catch(Exception e){
			log.error("Could not get head file name. Throwing RemoteException",e);
			throw new RemoteException("Could not get head file name");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getCurrentContentPartName()}
	 * 
	 * @param sessionToken session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getCurrentContentPartName()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartName(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getCurrentContentPartName();
		}catch(Exception e){
			log.error("Could not get current content part name. Throwing RemoteException",e);
			throw new RemoteException("Could not get current content part name");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getCurrentContentPartPayload()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getCurrentContentPartPayload()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getCurrentContentPartPayload(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getCurrentContentPartPayload();
		}catch(Exception e){
			log.error("Could not get current content part payload. Throwing RemoteException",e);
			throw new RemoteException("Could not get current content part payload");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getProperties(java.lang.String)}
	 * 
	 * @param params Autogenerated stub input 
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getProperties(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public GetPropertiesResponse getProperties(GetPropertiesRequest params) throws RemoteException{
		handleScope();
		try{
			String []props=this.getResultSet(params.getSessionToken()).getProperties(params.getType());
			if(props==null) return new GetPropertiesResponse(new String [0]);
			return new GetPropertiesResponse(props);
		}catch(Exception e){
			log.error("Could not get properties for type"+params.getType()+". Throwing RemoteException",e);
			throw new RemoteException("Could not get properties for type"+params.getType());
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNextPart(int)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNextPart(int)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean getNextPart(GetNextPartRequest params) throws RemoteException{
		handleScope();
		try{
			 return this.getResultSet(params.getSessionToken()).getNextPart(params.getMaxWaitTime());
		}catch(Exception e){
			log.error("Could not get next part. Throwing RemoteException",e);
			throw new RemoteException("Could not get next part");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#nextAvailable()}
	 * 
	 * @param sessionToken The session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#nextAvailable()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean nextAvailable(String sessionToken) throws RemoteException{
		handleScope();
		try{
			 return this.getResultSet(sessionToken).nextAvailable();
		}catch(Exception e){
			log.error("Could not check if next part is available. Throwing RemoteException",e);
			throw new RemoteException("Could not check if next part is available");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getPreviousPart()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getPreviousPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean getPreviousPart(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getPreviousPart();
		}catch(Exception e){
			log.error("Could not get previous part. Throwing RemoteException",e);
			throw new RemoteException("Could not get previous part");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getFirstPart()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getFirstPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean getFirstPart(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getFirstPart();
		}catch(Exception e){
			log.error("Could not get head part. Throwing RemoteException",e);
			throw new RemoteException("Could not get head part");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isFirst()}
	 * 
	 * @param sessionToken he session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isFirst()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean isFirst(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).isFirst();
		}catch(Exception e){
			log.error("Could not check if is First. Throwing RemoteException",e);
			throw new RemoteException("Could not check if is First");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isLast()}
	 * 
	 * @param sessionToken he session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#isLast()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean isLast(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).isLast();
		}catch(Exception e){
			log.error("Could not check if is last. Throwing RemoteException",e);
			throw new RemoteException("Could not check if is last");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNumberOfResults(java.lang.String)}
	 * 
	 * @param params Autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getNumberOfResults(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public int getNumberOfResults(GetNumberOfResultsRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).getNumberOfResults(params.getType());
		}catch(Exception e){
			log.error("Could not get number of results. Throwing RemoteException",e);
			throw new RemoteException("Could not get number of results");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResult(int)}
	 * 
	 * @param params Autogenerated stub input
	 * @return The result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResult(int)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getResult(GetResultRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).getResult(params.getIndex());
		}catch(Exception e){
			log.warn("Could not get result. Throwing RemoteException",e);
			throw new RemoteException("Could not get result");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults()} wrapped in an autogenerated stub
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public GetAllResultsResponse getAllResults(String sessionToken) throws RemoteException{
		handleScope();
		try{
			String []res=this.getResultSet(sessionToken).getResults();
			if(res==null) return new GetAllResultsResponse(new String [0]);
			return new GetAllResultsResponse(res);
		}catch(Exception e){
			log.error("Could not get records. Throwing RemoteException",e);
			throw new RemoteException("Could not get records");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults(int, int)}
	 * 
	 * @param params Autogenerates stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getResults(int, int)} wrapped in an autogenerated stub
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public GetResultsResponse getResults(GetResultsRequest params) throws RemoteException{
		handleScope();
		try{
			String []res=this.getResultSet(params.getSessionToken()).getResults(params.getFrom(),params.getTo());
			if(res==null) return new GetResultsResponse(new String [0]);
			return new GetResultsResponse(res);
		}catch(Exception e){
			log.error("Could not get records. Throwing RemoteException",e);
			throw new RemoteException("Could not get records");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHostIP()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHostIP()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getHostIP(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getHostIP();
		}catch(Exception e){
			log.error("Could not get host IP. Throwing RemoteException",e);
			throw new RemoteException("Could not get host IP");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHostName()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getHostName()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getHostName(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getHostName();
		}catch(Exception e){
			log.error("Could not get host Name. Throwing RemoteException",e);
			throw new RemoteException("Could not get host Name");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#splitEncoded()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#splitEncoded()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public SplitEncodedResponse splitEncoded(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return new SplitEncodedResponse(this.getResultSet(sessionToken).splitEncoded());
		}catch(Exception e){
			log.error("Could not split encoded content. Throwing RemoteException",e);
			throw new RemoteException("Could not split encoded content");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#splitClear()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#splitClear()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public SplitClearResponse splitClear(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return new SplitClearResponse(this.getResultSet(sessionToken).splitClear());
		}catch(Exception e){
			log.error("Could not split clear content. Throwing RemoteException",e);
			throw new RemoteException("Could not split clear content");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getFileContent(java.lang.String)}
	 * 
	 * @param params Autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#getFileContent(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String getFileContent(GetFileContentRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).getFileContent(params.getFilename());
		}catch(Exception e){
			log.error("Could not retrieve file content. Throwing RemoteException",e);
			throw new RemoteException("Could not retrieve file content");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#retrieveCustomProperties()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#retrieveCustomProperties()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String retrieveCustomProperties(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).retrieveCustomProperties();
		}catch(Exception e){
			log.error("Could not retrieve Custom Properties. Throwing RemoteException",e);
			throw new RemoteException("Could not retrieve Custom Properties");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#keepTop(int, short)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#keepTop(int, short)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String keepTop(KeepTopRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).keepTop(params.getCount(),params.getType());
		}catch(Exception e){
			log.error("Could not perform keep top service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform keep top service side");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#keepTop(java.lang.String[], int, short)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#keepTop(java.lang.String[], int, short)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String keepTopProp(KeepTopPropRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).keepTop(params.getProperties(),params.getCount(),params.getType());
		}catch(Exception e){
			log.error("Could not perform keep top service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform keep top service side");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnHead(java.lang.String)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnHead(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String executeQueryOnHead(ExecuteQueryOnHeadRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).executeQueryOnHead(params.getXPath());
		}catch(Exception e){
			log.error("Could not execute query on Head. Throwing RemoteException",e);
			throw new RemoteException("Could not execute query on Head");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnDocument(java.lang.String)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnDocument(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String executeQueryOnDocument(ExecuteQueryOnDocumentRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).executeQueryOnDocument(params.getXPath());
		}catch(Exception e){
			log.error("Could not execute query on document. Throwing RemoteException",e);
			throw new RemoteException("Could not execute query on document");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnResults(java.lang.String)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#executeQueryOnResults(java.lang.String)} wrapped in an autogenerated stub
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public ExecuteQueryOnResultsResponse executeQueryOnResults(ExecuteQueryOnResultsRequest params) throws RemoteException{
		handleScope();
		try{
			String []res=this.getResultSet(params.getSessionToken()).executeQueryOnResults(params.getXPath());
			if(res==null) return new ExecuteQueryOnResultsResponse(new String [0]);
			return new ExecuteQueryOnResultsResponse(res);
		}catch(Exception e){
			log.error("Could not execute query on document. Throwing RemoteException",e);
			throw new RemoteException("Could not execute query on document");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#cloneRS()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#cloneRS()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String cloneRS(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).cloneRS();
		}catch(Exception e){
			log.error("Could not clone the rs service side. Throwing RemoteException",e);
			throw new RemoteException("Could not clone the rs service side");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#filterRS(java.lang.String)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#filterRS(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String filterRS(FilterRSRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).filterRS(params.getXpath());
		}catch(Exception e){
			log.error("Could not perform filter by xpath service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform filter by xpath service side");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#filterRS(java.lang.String, java.lang.String[])}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#filterRS(java.lang.String, java.lang.String[])}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String filterRSProp(FilterRSPropRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).filterRS(params.getXpath(),params.getProperties());
		}catch(Exception e){
			log.error("Could not perform filter by xpath service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform filter by xpath service side");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformByXSLT(java.lang.String)}
	 * 
	 * @param params autogenerateed stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformByXSLT(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String transformByXSLT(TransformByXSLTRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).transformByXSLT(params.getTransformation());
		}catch(Exception e){
			log.error("Could not perfom transformation. Throwing RemoteException",e);
			throw new RemoteException("Could not perfom transformation");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformRS(java.lang.String)}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformRS(java.lang.String)}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String transformRS(TransformByXSLTRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).transformRS(params.getTransformation());
		}catch(Exception e){
			log.error("Could not perform transform by xslt service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform transform by xslt service side");
		}
	}
	
	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformRS(java.lang.String, java.lang.String[])}
	 * 
	 * @param params autogenerated stub input
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#transformRS(java.lang.String, java.lang.String[])}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public String transformRSProp(TransformRSPropRequest params) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(params.getSessionToken()).transformRS(params.getXslt(),params.getProperties());
		}catch(Exception e){
			log.error("Could not perform transform by xslt service side. Throwing RemoteException",e);
			throw new RemoteException("Could not perform transform by xslt service side");
		}
	}
	
	/**
	 * checjs whether or not the underying RS is populated with results on demand
	 * 
	 * @param sessionToken the session token
	 * @return <code>true</code> if the RS supports on demand production. <code>fale</code> otherwise
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean isFlowControled(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getRSRef().isDataFlow();
		}catch(Exception e){
			log.error("Could check if RS is flow controled. Throwing RemoteException",e);
			throw new RemoteException("Could check if RS is flow controled");
		}
	}
	
	/**
	 * Attaches the current payload file
	 * 
	 * @param sessionToken the session token
	 * @return true if the payload is attached, false otehrwoise
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean canAttach(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return false;
//			globus 4.1 specific for attachments
//			MessageContext msgContext = MessageContext.getCurrentContext();
//			File file = new File(this.getResultSet(sessionToken).getCurrentContentPartName());
//			AttachmentPart replyPart= new AttachmentPart(new DataHandler(new FileDataSource(file)));
//			Message rspMsg= msgContext.getResponseMessage();
//			rspMsg.addAttachmentPart(replyPart);
//			return true;
		}catch(Exception e){
			log.error("could not attach. returning false",e);
			return false;
		}
	}
	
	/**
	 * Returns availalble port to connect to
	 * 
	 * @param sessionToken the session token
	 * @return the port to connect to or negative otherwise
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public CanStreamResponse canStream(String sessionToken) throws RemoteException{
		handleScope();
		try{
			CanStreamResponse response = new CanStreamResponse();
			response.setPort(port);
			response.setSSLsupport(SSLsupport);
			return response;
		} catch(Exception e){
			log.error("could not find port to connecto to. returning negative",e);
			return null;
		}
	}
	
	/**
	 * Destroys the seesion associated with the session token
	 * 
	 * @param sessionToken the session token
	 * @return Autogenerated stub output
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public DestroySessionResponse destroySession(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.resourceHome.remove(sessionToken);
			return new DestroySessionResponse();
		} catch(Exception e){
			log.error("could not remove session "+sessionToken,e);
			throw new RemoteException("could not remove session "+sessionToken);
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#removePreviousPart()}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#removePreviousPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean removePreviousPart(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).removePreviousPart();
		}catch(Exception e){
			log.error("Could not remove previous part. Throwing RemoteException",e);
			throw new RemoteException("Could not remove previous part");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param sessionToken the session token
	 * @return always true
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean disableAccessLeasing(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(sessionToken).disableAccessLeasing();
			return true;
		}catch(Exception e){
			log.error("Could not disable access leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not disable access leasing");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param request the session token
	 * @return the service responce
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public ExtendAccessLeasingResponse extendAccessLeasing(ExtendAccessLeasingRequest request) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(request.getSessionToken()).extendAccessLeasing(request.getAccessExtend());
			return new ExtendAccessLeasingResponse();
		}catch(Exception e){
			log.error("Could not extend access leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not extend access leasing");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#removePreviousPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public int getAccessLeasing(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getAccessLeasing();
		}catch(Exception e){
			log.error("Could not get access leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not get access leasing");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#removePreviousPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean isForward(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).isForward();
		}catch(Exception e){
			log.error("Could not check if is Forward. Throwing RemoteException",e);
			throw new RemoteException("Could not check if is Forward");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param request the session token
	 * @return the service responce
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean setForward(SetForwardRequest request) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(request.getSessionToken()).setForward(request.isForward());
		}catch(Exception e){
			log.error("Could not set Forward. Throwing RemoteException",e);
			throw new RemoteException("Could not set Forward");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param sessionToken the session token
	 * @return always true
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public boolean disableTimeLeasing(String sessionToken) throws RemoteException{
		handleScope();
		try{
			this.getResultSet(sessionToken).disableTimeLeasing();
			return true;
		}catch(Exception e){
			log.error("Could not disable time leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not disable time leasing");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param request the session token
	 * @return the service responce
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public ExtendTimeLeasingResponse extendTimeLeasing(ExtendTimeLeasingRequest request) throws RemoteException{
		handleScope();
		try{
			Date newDate = new Date();
			newDate.setTime(request.getTimeExtend());
			this.getResultSet(request.getSessionToken()).extendTimeLeasing(newDate);
			return new ExtendTimeLeasingResponse();
		}catch(Exception e){
			log.error("Could not extend time leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not extend time leasing");
		}
	}

	/**
	 * forwards the request to {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * 
	 * @param sessionToken the session token
	 * @return the result of {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet#removePreviousPart()}
	 * @throws RemoteException An unrecoverable for the operation error occured
	 */
	public long getTimeLeasing(String sessionToken) throws RemoteException{
		handleScope();
		try{
			return this.getResultSet(sessionToken).getTimeLeasing().getTime();
		}catch(Exception e){
			log.error("Could not get access leasing. Throwing RemoteException",e);
			throw new RemoteException("Could not get access leasing");
		}
	}

	protected synchronized void handleScope() {
		if (ServiceContext.getContext().getScope() == null) 
			if (ServiceContext.getContext().getInstance().getScopes().get(0) == null)
				log.info("Scope is null: (ServiceContext.getContext().getInstance().getScopes().get(0) == null). Probably, the service has not reached ready state.");
			else{ 
				log.info("Scope set to: "+ ServiceContext.getContext().getInstance().getScopes().get(0).getName());	
				ServiceContext.getContext().setScope(
					ServiceContext.getContext().getInstance().getScopes().get(0));
			}
	}

	
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
