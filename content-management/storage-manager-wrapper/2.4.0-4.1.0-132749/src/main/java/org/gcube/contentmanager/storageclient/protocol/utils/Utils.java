package org.gcube.contentmanager.storageclient.protocol.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.scope.impl.ServiceMapScannerMediator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * Utility class for scope identity
 *
 * @author Roberto Cirillo (ISTI-CNR)
 
 */
public class Utils {
	
	// Temporary CONSTANTS	
		private static final String GCUBE_RESOLVER_HOST = "data.gcube.org";
		private static final String D4SCIENCE_RESOLVER_HOST = "data.d4science.org";	
		private static final String GCUBE_INFRA = "gcube";
		private static final String D4SCIENCE_INFRA = "d4science.research-infrastructures.eu";
		public static final String INFRASTRUCTURE_ENV_VARIABLE_NAME="infrastructure";
		public static final String  URI_RESOLVER_RESOURCE_CATEGORY="Service";
		public static final String  URI_RESOLVER_RESOURCE_NAME="HTTP-URI-Resolver";
	
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	
	public static boolean validationScope2(String scope){
		ScopeBean scopeBean=new ScopeBean(scope);
		if((scopeBean.is(Type.VRE)))
			scope=scopeBean.enclosingScope().toString();
		return ServiceMapScannerMediator.isValid(scope);
	}
	
	public static ArrayList<String> getVOScopes(String scope){
		ArrayList<String> vos=new ArrayList<String>();
		ScopeBean scopeBean=new ScopeBean(scope);
		//retrieve INFRA scope
		while(!scopeBean.is(Type.INFRASTRUCTURE)){
			logger.debug("the scope "+scope+" is not an INFRA scope ");
			scopeBean=new ScopeBean(scopeBean.enclosingScope().toString());
		}
		scope=scopeBean.toString();
		if(scopeBean.is(Type.INFRASTRUCTURE)){
			Set<String> scopeSet=ServiceMapScannerMediator.getScopeKeySet();
			for(String scopeItem : scopeSet){
				//retrieve all Vo scopes
				logger.debug("scope scanned: "+scopeItem);
				if(scopeItem.contains(scope) && (new ScopeBean(scopeItem).is(Type.VO))){
					logger.debug("found vo scope: "+scopeItem);
					vos.add(scopeItem);
				}
			}
		}
		return vos;
	}
	
	public static String extractInfrastructureNewVersion(String urlParam){
		logger.debug("urlParam: "+urlParam);
		String infra= checkVarEnv(INFRASTRUCTURE_ENV_VARIABLE_NAME);
		if (infra != null)
			return infra;
		else
			//only for test return the infra from the uri. In prod will be throws a runtime exception
			return getInfraFromResolverHost(urlParam);
	}
	
	public static String getInfraFromResolverHost(String resolverHost) {
		if(resolverHost.equals(GCUBE_RESOLVER_HOST)){
			return GCUBE_INFRA;
		}else if(resolverHost.equals(D4SCIENCE_RESOLVER_HOST)){
			return D4SCIENCE_INFRA;
		}else return resolverHost;
	}

	public static String checkVarEnv(String name){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        Iterator<String> iter = keys.iterator();
        String value=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase(name)){
            	value=env.get(key);
            	break;
            }
        }
        return value;
	}

	public static boolean isScopeProviderMatch(String infraHost) {
		String currentScope=ScopeProvider.instance.get();
		if(currentScope != null){
			//get vo scope
			String voScope=Utils.getVOScope(currentScope);
			// get the uri resolver host
			List<ServiceEndpoint> services=queryServiceEndpoint(URI_RESOLVER_RESOURCE_CATEGORY, URI_RESOLVER_RESOURCE_NAME);
			String host=null;
			if(services != null && services.size()>0){
				host=getResolverHost(services.get(0));
				if(host!=null){
					if(infraHost.equalsIgnoreCase(host)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static String getVOScope(String currentScope) {
		ScopeBean scopeBean=new ScopeBean(currentScope);
		if((scopeBean.is(Type.VRE)))
			currentScope=scopeBean.enclosingScope().toString();
		return currentScope;
	}

	public static List<ServiceEndpoint> queryServiceEndpoint(String category, String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+category+"' and $resource/Profile/Name eq '"+name+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}

	public static String getResolverHost(ServiceEndpoint serviceEndpoint) {
		return serviceEndpoint.profile().runtime().hostedOn();
		
	}

}
