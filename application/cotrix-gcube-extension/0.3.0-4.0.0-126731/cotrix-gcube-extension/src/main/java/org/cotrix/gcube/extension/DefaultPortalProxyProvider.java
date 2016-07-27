/**
 * 
 */
package org.cotrix.gcube.extension;

import static org.cotrix.common.Constants.*;
import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

import org.cotrix.gcube.stubs.SessionToken;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
@Singleton
@Alternative
@Priority(RUNTIME)
public class DefaultPortalProxyProvider implements PortalProxyProvider {
	
	private static final String PORTAL_ENDPOINT_CATEGORY = "Portal";
	
	private static final String ACCESS_POINT_NAME = "Base URI";
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultPortalProxyProvider.class);
	
	protected Portals portals = new Portals();
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public PortalProxy getPortalProxy(SessionToken sessionToken) {
		try {
			String scope = sessionToken.scope();
			String portalUrl = sessionToken.origin();
			String verifiedPortalUrl = null;
			
			if (!portals.in(scope).contains(portalUrl)) {
				verifiedPortalUrl = retrievePortalUrl(scope, portalUrl);
				portals.add(scope, portalUrl);
			} else verifiedPortalUrl = portalUrl;

			return new DefaultPortalProxy(verifiedPortalUrl, sessionToken.id());
		} catch(MalformedURLException e) {
			throw new RuntimeException("PortalProxy creation failed for session token "+sessionToken, e);
		}
	}
	
	private String retrievePortalUrl(String scope, String portalUrl) throws MalformedURLException {
		logger.trace("retrievePortalUrl scope {}, portalUrl: {}", scope, portalUrl);

		String infrastructureScope = getInfrastructureScope(new ScopeBean(scope));
		ScopeProvider.instance.set(infrastructureScope);
		logger.trace("infrastructureScope {}", infrastructureScope);

		URL url = new URL(portalUrl);
		StringBuilder exptectedUrlBuilder = new StringBuilder(url.getHost());
		if (url.getPort() != -1)
			exptectedUrlBuilder.append(':').append(url.getPort());
		String expectedUrl = exptectedUrlBuilder.toString();
		logger.trace("expectedUrl {}", expectedUrl);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition(String.format("$resource/Profile/Category/text() eq '%1$s'", PORTAL_ENDPOINT_CATEGORY));

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		if (resources.isEmpty())
			throw new IllegalStateException("Portal resource not found");

		for (ServiceEndpoint endpoint : resources) {

			for (AccessPoint accessPoint : endpoint.profile().accessPoints()) {
				if (accessPoint.name().equals(ACCESS_POINT_NAME) && expectedUrl.equals(accessPoint.address()))
					return portalUrl;
			}
		}

		throw new IllegalStateException("Portal Service Endpoint for url " + expectedUrl + " not found in scope " + infrastructureScope);
	}
	
	private String getInfrastructureScope(ScopeBean scope) {
		ScopeBean enclosing = scope.enclosingScope();
		if (enclosing == null)
			return scope.toString();
		return getInfrastructureScope(enclosing);
	}
}
