package org.gcube.vremanagement.resourcemanager.impl.resources;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage;

/**
 * A scoped {@link GCUBERunningInstance}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class ScopedRunningInstance extends ScopedResource {
	
	public static final String TYPE = GCUBERunningInstance.TYPE;
	
	/** name of the node hosting the RI */
	private String hostingNodeName = "";
	
	private boolean isWhnManagerEnabled;
	
	/** data of the service from which the RI was generated */
	private ServiceData sourceService = new ServiceData();;
	
	/** Scopes the instance belongs to */
	private List<String> scopes;

	private transient EndpointReferenceType ghnEpr;	
		
	protected ScopedRunningInstance(String id, GCUBEScope scope, String ... hostedOn) {
		super(id, TYPE, scope);
		if ((hostedOn != null) && (hostedOn.length > 0))
			this.hostedOn = hostedOn[0];
	}

	@Override
	protected void find() throws Exception {	
		logger.trace("ScopedRunningInstance find method. Search resource with id: "+this.id);
		//looks for the RI to manage
		ISClient client =  GHNContext.getImplementation(ISClient.class);
		String hostingID = "";
		try {
			 //we cannot use here the GCUBERunningInstanceQuery because it returns only ready instances, 
			 //we need to find a RI, no matter in which state it is 
			 GCUBEGenericQuery query = client.getQuery("GCUBEResourceQuery");
			 query.addParameters(new QueryParameter("FILTER", "$result/ID/text()/string() eq \""+id+"\""),
						new QueryParameter ("RESULT", "$result")); 
			 List<XMLResult> results = client.execute(query, ServiceContext.getContext().getScope());
			 //query.addAtomicConditions(new AtomicCondition("/ID/text()", id));			 
			 
			 if (results == null || results.size() == 0) {					 
				 results = client.execute(query, ServiceContext.getContext().getScope().getEnclosingScope());
					 if (results == null || results.size() == 0) {
						 throw new Exception("unable to find target RI "+ this);
				 }
			 }
			 			 
			 GCUBERunningInstance profile = GHNContext.getImplementation(GCUBERunningInstance.class);
			 profile.load(new StringReader(results.get(0).evaluate("/").get(0)));
		     this.sourceService = new ServiceData();
		     this.sourceService.serviceID = profile.getServiceID();
		     this.sourceService.serviceClass = profile.getServiceClass();
		     this.sourceService.serviceName = profile.getServiceName();
		     this.sourceService.packageVersion = profile.getInstanceVersion();
		     hostingID = profile.getGHNID();
		     this.scopes = new ArrayList<String>();
		     for (GCUBEScope scope : profile.getScopes().values())
		    	 this.scopes.add(scope.getName());
		} catch (Exception e) {
			throw new Exception("unable to find the target RI " + this,e);							
		}
		logger.debug("find method: looks for the GHN or WHN to contact ");
		//looks for the GHN to contact
		try {
			 GCUBEGHNQuery query = client.getQuery(GCUBEGHNQuery.class);
			 query.addAtomicConditions(new AtomicCondition("/ID/text()", hostingID));
			 List<GCUBEHostingNode> hostingNode = client.execute(query, ServiceContext.getContext().getScope());
			 this.hostingNodeName = hostingNode.get(0).getNodeDescription().getName();
		// ADD CHECK on WHN
			 isWhnManagerEnabled=checkGhnType( hostingNode.get(0).getID(), ServiceContext.getContext().getScope());
			 if(isWhnManagerEnabled){
				 logger.debug("The container is a WHN ");
//				 this.loadWHNManager(hostingNodeName);
			 }else
				 this.ghnEpr = this.loadGHNmanager(hostingNode.get(0).getID(), client);	
		} catch (Exception e) {
			this.noHopeForMe("unable to find the hosting GHN (ID=" + hostingID + ")", new ResourceNotFound(e));			
		}

	}
	
	/**
	 * Adds the RI to the scope
	 * 
	 * @throws Exception if it is not possible to add the resource
	 */
	@Override
	protected void addToScope() throws ResourceNotFound, Exception {
		logger.trace("addToScope method: in scope "+ScopeProvider.instance.get());
		if (this.hostingNodeName.compareToIgnoreCase("") == 0)
			this.findResource();
		try {
//			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
//			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(this.ghnEpr), 
//					ServiceContext.getContext().getScope(), ServiceContext.getContext());				
			
			if(isWhnManagerEnabled){
				//contact the WHNManager to add the WHN to the given scope.
				WHNManagerProxy proxy = loadWHNManager(this.hostingNodeName);
				try{
					proxy.addToContext(this.scope);
				}catch(Exception e){
					this.noHopeForMe("Failed to add WHN to scope " + scope.toString(), e);
				}
				isWhnManagerEnabled=false;
				logger.debug(" AddScope operation on WhnManager completed ");
			}else{
				ScopeRIParams params = new ScopeRIParams();			
				params.setClazz(this.sourceService.serviceClass);
				params.setName(this.sourceService.serviceName);
				params.setScope(this.scope);
				GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
				GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(this.ghnEpr), 
						ServiceContext.getContext().getScope(), ServiceContext.getContext());				
				pt.addRIToScope(params);
			}
		} catch (Exception e) {
			this.setStatus(STATUS.LOST);
			this.success = false;
			this.setErrorMessage("Failed to add RunningInstance to scope " + scope);
			getLogger().error("Failed to add RunningInstance to scope " + scope, e);
			throw new Exception("Failed to add RunningInstance to scope " + scope);
		}
	}

	
	/**
	 * Removes the RI from the scope
	 * 
	 * @throws Exception if it is not possible to remove the resource
	 */
	@Override
	protected void removeFromScope() throws ResourceNotFound,ServiceNotFoundException, Exception {			
		logger.trace("removeFromScope method: called in scope "+ScopeProvider.instance.get());
		
		this.findResource();
		try {
//			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
//			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(this.ghnEpr), 
//					ServiceContext.getContext().getScope(), ServiceContext.getContext());
			if(isWhnManagerEnabled){
				//contact the WHNManager to add the WHN to the given scope.
				WHNManagerProxy proxy = loadWHNManager(this.hostingNodeName);
				try{
					proxy.removeFromContext(this.scope);
				}catch(Exception e){
					this.noHopeForMe("Failed to remove resource from scope " + scope.toString(), e);
				}
				isWhnManagerEnabled=false;
				logger.debug(" RemoveScope operation on WhnManager completed ");
			}else{
				GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
				GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(this.ghnEpr), 
						ServiceContext.getContext().getScope(), ServiceContext.getContext());
				ScopeRIParams params = new ScopeRIParams();			
				params.setClazz(this.sourceService.serviceClass);
				params.setName(this.sourceService.serviceName);
				params.setScope(this.scope);
				pt.removeRIFromScope(params);
			}
		} catch (Exception e) {
			this.noHopeForMe("Failed to remove RunningInstance from scope " + scope.toString()  +" "+ e.getMessage(),e);						
		}
	}
	
	protected String getSourceServiceClass() {
		return this.sourceService.serviceClass;
	}
	
	protected String getSourceServiceName() {
		return this.sourceService.serviceName;
	}		

	public boolean isUndeployNeeded() throws ResourceNotFound {
		try {
			this.findResource();
		} catch (ResourceNotFound e) {
			this.noHopeForMe("Failed to remove RunningInstance from scope " + scope.toString()  +". Can't find the related service to undeploy "+ e.getMessage(),e);
		}
		if (this.scopes.size() == 1) {
			getLogger().info(this + " joins only this scope: it can be undeployed");
			return true;
		}
		return false;
	}

	/**
	 * @return the name of the gHN hosting the instance
	 */
	public String getHostedOn() {
		return hostingNodeName;
	}
	
	public void reportFailureOnSourceService(String message, Exception e) {
		try {
			this.noHopeForMe(message, e);
		} catch (Exception e1) {
			getLogger().error(this + "An error has been reported from outside when managing the source service", e1);
		}
	}

	public void wasSuccessful() {
		this.success = true;		
	}
	
	/**
	 * Number of times the resource is searched in the IS before to declare it lost
	 * @return the number of attempts to do 
	 */
	@Override
	protected int getMaxFindAttempts(){
		return 40;
	}
	
	
	/**
	 * Gets the package that generated this instance
	 * @return the service
	 * @throws ServiceNotFoundException if the service is not found
	 */
	public GCUBEPackage getSourcePackage() throws ServiceNotFoundException {
		//retrieve the service from the IS	
		logger.debug("Looking for the RI's source package");
		logger.debug("RI package's version is: " +this.sourceService.packageVersion );
		try {
			ISClient client =  GHNContext.getImplementation(ISClient.class);			
			GCUBEServiceQuery query = client.getQuery(GCUBEServiceQuery.class);
			query.addGenericCondition("$result/Profile/Name/string() eq '"+this.sourceService.serviceName+
					"' and $result/Profile/Class/string() eq '"+ this.sourceService.serviceClass +"'");									
			List<GCUBEService> profiles = client.execute(query, ServiceContext.getContext().getScope());
			if ((profiles == null) || (profiles.size() == 0)) 
				throw new Exception();
			//look for the main package
			for (GCUBEService service : profiles) {
				for (org.gcube.common.core.resources.service.Package p : service.getPackages()) {
					logger.trace("Comparing package:" + p.getName() + ", version " + p.getVersion() + ", class " + p.getClass().getName() );
					if (p.getClass().isAssignableFrom(org.gcube.common.core.resources.service.MainPackage.class)
						&& ((p.getVersion().compareTo(this.sourceService.packageVersion)==0)
								|| ((p.getVersion().compareTo(this.sourceService.packageVersion+"-SNAPSHOT")==0)))) {
						//got it, let's return the service	
						return new GCUBEPackage(this.sourceService.serviceClass,this.sourceService.serviceName,
								null, p.getName(), p.getVersion(), this.hostedOn);
					}
				}
				
			}
		} catch (Exception e) {
			logger.error("Can't find the source package", e);
			throw new ServiceNotFoundException();
		}
		throw new ServiceNotFoundException();
	}
	
	/**
	 * Looks for the GHN manager's endpoint to contact
	 * @param id the identifier of the gHN
	 * @param client the ISClient instance to use
	 * @return the endpoint reference of gHNManager's portType to contact
	 * @throws Exception if the search fails
	 */
	private EndpointReferenceType loadGHNmanager(String id, ISClient client) throws Exception {
		//looks for the GHN manager's endpoint to contact
		 GCUBERIQuery riquery = client.getQuery(GCUBERIQuery.class);
		 riquery.addAtomicConditions(new AtomicCondition("/Profile/GHN/@UniqueID", id), 
					new AtomicCondition("/Profile/ServiceClass", "VREManagement"),
					new AtomicCondition("/Profile/ServiceName", "GHNManager"));
		 List<GCUBERunningInstance> results = client.execute(riquery,ServiceContext.getContext().getScope());
		 return results.get(0).getAccessPoint().getEndpoint("gcube/common/vremanagement/GHNManager");	
	}
		
	
	/** 
	 * 
	 * Groups some service data
	 *
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	class ServiceData {
		String serviceID;
		String serviceName;
		String serviceClass;
		String packageVersion;
	}

}
