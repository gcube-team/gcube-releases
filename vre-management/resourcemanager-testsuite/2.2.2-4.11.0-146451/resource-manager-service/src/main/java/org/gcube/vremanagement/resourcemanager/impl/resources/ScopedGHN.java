package org.gcube.vremanagement.resourcemanager.impl.resources;

import static org.gcube.common.vremanagement.whnmanager.client.plugins.AbstractPlugin.whnmanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedQueryException;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.ghnmanager.stubs.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;

/**
 * Models a scoped {@link GCUBEHostingNode}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ScopedGHN extends ScopedResource {
	
	public static final String TYPE = GCUBEHostingNode.TYPE;
	
	private String nodename = "";
	
	private boolean isWhnManagerEnabled;
	
	private transient EndpointReferenceType ghnEpr;	
	
	protected ScopedGHN(String id, GCUBEScope scope) {
		super(id, TYPE, scope);
	}

	
	@Override
	protected void addToScope() throws ResourceNotFound, Exception {
		if (this.ghnEpr == null)
			this.findResource();
		if(isWhnManagerEnabled){
	//contact the WHNManager to add the WHN to the given scope.
			WHNManagerProxy proxy = loadWHNManager(this.hostedOn);
			try{
				proxy.addToContext(this.scope.toString());
			}catch(Exception e){
				this.noHopeForMe("Failed to add WHN to scope " + scope.toString(), e);
			}
			isWhnManagerEnabled=false;
			logger.debug(" AddScope operation on WhnManager completed ");
		}else{
			//contact the GHNManager to add the GHN to the given scope
			EndpointReferenceType endpoint = new EndpointReferenceType();
			try {			
				endpoint.setAddress(new Address("http://"+ this.nodename +"/wsrf/services/gcube/common/vremanagement/GHNManager"));			
				GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
				GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(endpoint), 
						ServiceContext.getContext().getScope().getEnclosingScope(), ServiceContext.getContext());		 
				AddScopeInputParams params = new AddScopeInputParams();
				params.setScope(this.scope.toString());
				params.setMap(""); //eventually, set here the new Service Map
				pt.addScope(params);	
								
			} catch (Exception e) {
				this.noHopeForMe("Failed to add GHN to scope " + scope.toString(), e);			
			}
		}
	}


	

	@Override
	protected void find() throws Exception {
		logger.trace("ScopedGHN find method. Search ghn with id: "+this.id);
		isWhnManagerEnabled=checkGhnType(this.id, ServiceContext.getContext().getScope());
		if(!isWhnManagerEnabled){
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBEGHNQuery query = null;
			try {
				query = client.getQuery(GCUBEGHNQuery.class);
				query.addAtomicConditions(new AtomicCondition("/ID/text()", this.id));
			} catch (Exception e) {					
				throw new Exception("unable to query of the target GHN (ID=" + this.id + ")", e);			
			}
			try {	
				 logger.debug("find method: execute query");
				 List<GCUBEHostingNode> hostingNodes =  client.execute(query, ServiceContext.getContext().getScope());
				 this.nodename = hostingNodes.get(0).getNodeDescription().getName();
				 this.hostedOn = nodename;
				 this.ghnEpr = this.loadGHNmanager(hostingNodes.get(0).getID(), client, ServiceContext.getContext().getScope());
			} catch (Exception e) {
				//try in the enclosing scope
				isWhnManagerEnabled=checkGhnType(this.id, ServiceContext.getContext().getScope().getEnclosingScope());
				if(!isWhnManagerEnabled){
					try {
						 List<GCUBEHostingNode> hostingNodes = client.execute(query, ServiceContext.getContext().getScope().getEnclosingScope());
						 this.nodename = hostingNodes.get(0).getNodeDescription().getName();
						 this.hostedOn = nodename;
						 this.ghnEpr = this.loadGHNmanager(hostingNodes.get(0).getID(), client,ServiceContext.getContext().getScope().getEnclosingScope());
					} catch (Exception ei) {
						throw new Exception("unable to find the target GHN (ID=" + this.id + ")", e);
					}
				}		
			}
		}else{
			this.nodename=this.hostedOn;
		}
	}

//	protected boolean checkGhnType( String id, GCUBEScope scope) throws Exception{
//		logger.trace("checkGhnType method: check if the ghn is managed by WhnManager ghnId: "+id+ " on scope: "+scope.toString());
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		GCUBEGHNQuery query=null;
//		try {
//			query = client.getQuery(GCUBEGHNQuery.class);
//			query.addAtomicConditions(new AtomicCondition("/ID/text()", this.id));
//// condition for discover if is managed by WHNMAnager
//			query.addGenericCondition("$result/Profile/GHNDescription/RunTimeEnv//Variable[Key/string() eq 'SmartGears']");
//		} catch (Exception e) {					
//			throw new Exception("checkGhnType method: unable to query of the target GHN (ID=" + this.id + ")", e);			
//		}
//		List<GCUBEHostingNode> hostingNodes=null;
//		try {
//			logger.debug("checkGhnType method: execute query");
//			hostingNodes =  client.execute(query, scope);
//		} catch (Exception e) {
//			logger.debug(" WHNManager query failed. Query exception: "+e.getMessage());
//			//try in the enclosing scope ??
////			try {
////				 hostingNodes = client.execute(query, ServiceContext.getContext().getScope().getEnclosingScope());
////				 this.nodename = hostingNodes.get(0).getNodeDescription().getName();
////				 this.hostedOn = nodename;
////				 this.ghnEpr = this.loadGHNmanager(hostingNodes.get(0).getID(), client,ServiceContext.getContext().getScope().getEnclosingScope());
////			} catch (Exception ei) {
////				logger.debug(" WHNManager query failed on enclosing scope. Query exception: "+e.getMessage());
////			}
//		}
//		if(hostingNodes.isEmpty()){
//			logger.debug("GHNProfile is not managed by WHNManager");
//			return false;		
//		}
//		this.nodename = hostingNodes.get(0).getNodeDescription().getName();
//		this.hostedOn = nodename;
//		logger.info("The GHN is managed by WhnManager. Next step: connect to WHNManager");
//		return true;
//	}


	@Override
	protected void removeFromScope() throws ResourceNotFound, Exception {
		if (this.ghnEpr == null)
			this.findResource();
		//EndpointReferenceType endpoint = new EndpointReferenceType();
		if(isWhnManagerEnabled){
//			//contact the WHNManager to add the GHN to the given scope. If not found then contact the GHNManager
			String scopeString=ServiceContext.getContext().getScope().getEnclosingScope().toString();
			logger.debug("contacting the WHNManager  on "+this.nodename+"  with scope "+scopeString+" for remove the scope: "+this.scope+" to the resource with id: "+this.id);
			ScopeProvider.instance.set(scopeString);
			WHNManagerProxy proxy = whnmanager().at(new URL("http://"+ this.nodename +"/whn-manager/gcube/vremanagement/ws/whnmanager")).build();
			try{
				proxy.removeFromContext(this.scope.toString());
			}catch(Exception e){
				this.noHopeForMe("Failed to remove WHN from scope " + scope.toString(), e);
			}
			isWhnManagerEnabled=false;
			logger.debug("RemoveScope operation on WhnManager completed ");
		}else{
			try {			
				//endpoint.setAddress(new Address("http://"+ this.nodename +"/wsrf/services/gcube/common/vremanagement/GHNManager"));			
				GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
				GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(this.ghnEpr), 
							ServiceContext.getContext().getScope().getEnclosingScope(), ServiceContext.getContext());			
				pt.removeScope(this.scope.toString());	
			} catch (Exception e) {
				this.noHopeForMe("Failed to remove GHN from scope " + scope.toString(), e);			
			}
		}
	}

	/**
	 * Looks for the GHN manager's endpoint to contact
	 * @param id the identifier of the gHN
	 * @param client the ISClient instance to use
	 * @param scope 
	 * @return the endpoint reference of gHNManager's portType to contact
	 * @throws Exception if the search fails
	 */
	private EndpointReferenceType loadGHNmanager(String id, ISClient client, GCUBEScope scope) throws Exception {
		//looks for the GHN manager's endpoint to contact
		 GCUBERIQuery riquery = client.getQuery(GCUBERIQuery.class);
		 riquery.addAtomicConditions(new AtomicCondition("/Profile/GHN/@UniqueID", id), 
					new AtomicCondition("/Profile/ServiceClass", "VREManagement"),
					new AtomicCondition("/Profile/ServiceName", "GHNManager"));
		 List<GCUBERunningInstance> results = client.execute(riquery,scope);
		 return results.get(0).getAccessPoint().getEndpoint("gcube/common/vremanagement/GHNManager");	
	}


}
