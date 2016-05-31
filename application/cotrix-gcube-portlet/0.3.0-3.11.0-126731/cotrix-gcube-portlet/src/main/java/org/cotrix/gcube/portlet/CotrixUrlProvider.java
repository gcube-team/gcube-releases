/**
 * 
 */
package org.cotrix.gcube.portlet;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.cotrix.gcube.stubs.SessionToken;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */

public class CotrixUrlProvider {
	
	private static final String ENDPOINT_CATEGORY = "Cotrix";
	private static final String ENDPOINT_NAME = "Application endpoint";
	private static final String ACCESS_POINT_NAME = "http";
	private static final String TOKEN_PARAMETER_NAME = "token";
	
	
	//WARNING: REFERRED TO FROM JSP PAGE
	public static String getCotrixUrl(HttpSession session, HttpServletRequest request) {
		
		return instance()+params(session, request);
	}
	
	
	
	//helpers
	
	private static String params(HttpSession session, HttpServletRequest request) {
	
		String token = new SessionToken(session.getId(), ScopeProvider.instance.get(),portalUrl(request)).encoded();
		
		StringBuilder parameters = new StringBuilder("?");
		
		parameters.append(TOKEN_PARAMETER_NAME).append('=')
		          .append(token);
		
		return parameters.toString();
	}
	
	private static String portalUrl(HttpServletRequest request) {
		
		StringBuilder portalUrl = new StringBuilder();
		
		portalUrl.append(request.getScheme())
				 .append("://")
				 .append(request.getServerName());
		
		if (request.getServerPort()!=80) 
			portalUrl.append(':').append(request.getServerPort());
		
		return portalUrl.toString();
	}
	
	
	private static String instance() {
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		
		query.addCondition(String.format("$resource/Profile/Category/text() eq '%1$s'",ENDPOINT_CATEGORY));
		query.addCondition(String.format("$resource/Profile/Name/text() eq '%1$s'",ENDPOINT_NAME));
		 
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		 
		List<ServiceEndpoint> resources = client.submit(query);
		
		//filter by scope
		String currentScope = ScopeProvider.instance.get();
		ScopeBean scope = new ScopeBean(currentScope);
		ServiceEndpoint endpoint = searchEnclosing(resources, scope);
		
		if (endpoint == null) throw new IllegalStateException("Cotrix resource not found");
		
		for (AccessPoint accessPoint:endpoint.profile().accessPoints()) {
			if (accessPoint.name().equals(ACCESS_POINT_NAME)) return accessPoint.address();
		}
		
		throw new IllegalStateException("AccessPoint with name "+ACCESS_POINT_NAME+" not found in resource "+endpoint.id());
	}
	
	private static ServiceEndpoint searchEnclosing(List<ServiceEndpoint> endpoints, ScopeBean scope) {
		if (scope == null) return null;
		ServiceEndpoint endpoint = filter(endpoints, scope.toString());
		if (endpoint == null) return searchEnclosing(endpoints, scope.enclosingScope());
		return endpoint;
	}
	
	private static ServiceEndpoint filter(List<ServiceEndpoint> endpoints, String scope) {
		for (ServiceEndpoint endpoint:endpoints) if (endpoint.scopes().contains(scope)) return endpoint;
		return null;
	}

}
