package org.gcube.vremanagement.vremodeler.impl.util;

import java.util.HashMap;
import java.util.List;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler.NoQueryResultException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceBinderPortType;
import org.gcube.vremanagement.resourcemanager.stubs.binder.service.ResourceBinderServiceAddressingLocator;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.ReportingPortType;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.service.ReportingServiceAddressingLocator;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeControllerPortType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.service.ScopeControllerServiceAddressingLocator;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;

public class ResourceManagerPorts {

	private static GCUBELog logger = new GCUBELog(ResourceManagerPorts.class);
	
	private static HashMap<String, ResourceManagerPorts> mapPortInScope = new HashMap<String, ResourceManagerPorts>();
	
	
	public static ResourceManagerPorts get(GCUBEScope scope) throws Exception{
		if (!mapPortInScope.containsKey(scope.toString())) mapPortInScope.put(scope.toString(), getResourceMangerPT(scope));
		return mapPortInScope.get(scope.toString());
	}
	
	public static void resetPorts(){
		mapPortInScope = new HashMap<String, ResourceManagerPorts>();
	}
	
	private ResourceBinderPortType binder;
	private ScopeControllerPortType scopeController;
	private ReportingPortType reporter;
	
	private ResourceManagerPorts(ResourceBinderPortType binder,
			ScopeControllerPortType scopeController, ReportingPortType reporter) {
		super();
		this.binder = binder;
		this.scopeController = scopeController;
		this.reporter = reporter;
	}
	
	public ResourceBinderPortType getBinder() {
		return binder;
	}
	public ScopeControllerPortType getScopeController() {
		return scopeController;
	}
	public ReportingPortType getReporter() {
		return reporter;
	}
	
	
	private static ResourceManagerPorts getResourceMangerPT(GCUBEScope scope) throws Exception{
		ResourceManagerPorts ports = null;
		int attempt=0;
		do{
			try{
				try{
					Thread.sleep(30000);
				}catch (Exception et){}
				
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
				
				query.setExpression("declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
						" for $outer in collection(\"/db/Profiles/RunningInstance\")//Document/Data/is:Profile/Resource " +
						" let $scope:= $outer/Scopes/Scope[string() eq '"+scope.toString()+"'] " +
						" where count($scope)>0 " +
						" and $outer/Profile/ServiceName/string() eq 'ResourceManager' " +
						" and $outer/Profile/DeploymentData/Status/string() eq 'ready'"+
						" return $outer");
				
				List<GCUBERunningInstance> ris = client.execute(query, scope);
				
				if(ris.size()==0) throw new NoQueryResultException();
				
				ResourceBinderPortType binderPT= new ResourceBinderServiceAddressingLocator().getResourceBinderPortTypePort(ris.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/resourcemanager/binder"));
				binderPT = GCUBERemotePortTypeContext.getProxy(binderPT,scope, Integer.parseInt((String)ServiceContext.getContext().getProperty("resourceManagerTimeout", true)));
			
				ScopeControllerPortType scopeControllerPT= new ScopeControllerServiceAddressingLocator().getScopeControllerPortTypePort(ris.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/resourcemanager/scopecontroller"));
				scopeControllerPT = GCUBERemotePortTypeContext.getProxy(scopeControllerPT,scope, Integer.parseInt((String)ServiceContext.getContext().getProperty("resourceManagerTimeout", true)));
				
				ReportingPortType reporterPT= new ReportingServiceAddressingLocator().getReportingPortTypePort(ris.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/resourcemanager/reporting"));
				reporterPT = GCUBERemotePortTypeContext.getProxy(reporterPT,scope, Integer.parseInt((String)ServiceContext.getContext().getProperty("resourceManagerTimeout", true)));
								
				ports = new ResourceManagerPorts(binderPT, scopeControllerPT, reporterPT);
			
			}catch(Exception e){
				logger.warn(e); 
				logger.warn("the query for resourceManager returned no result, re-trying in 30 secs ("+attempt+")");
			}finally {attempt++;}	
		}while(ports==null && attempt<10);
		
		if (attempt>=10) throw new Exception("no ResourceMaanger can be retrieved for scope "+scope);
		
		return ports;
	}
	
	
}
