package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

import junit.framework.TestCase;

public class GenericTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetSocialNetworkingEndpoint() throws Exception {
		final String scope = "/gcube/preprod/preECO";
		final String endpointName = "SocialNetworking";

		String endpoint = null;
		// String endpointName =
		// getPortletContext().getInitParameter(BasePortlet.CTX_PARAM_SOCIAL_NETWORKING_ENDPOINT);
		try {
			ScopeProvider.instance.set(scope);
			// if (logger.isTraceEnabled()) {
			// logger.trace(String.format("Query IS for [%s]", endpointName));
			// }
			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'", endpointName))
					.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");
			DiscoveryClient<String> client = ICFactory.client();
			List<String> results = client.submit(query);
			// TODO I need to query on the Endpoint@EntryName attribute
			Collection apis = CollectionUtils.select(results, new Predicate() {

				@Override
				public boolean evaluate(Object endpoint) {
					return ((String) endpoint).endsWith("rest");
				}
			});

			if (!apis.iterator().hasNext()) {
				throw new Exception(String.format("IS query for [%s] return no results", endpointName));
			}
			endpoint = (String) apis.iterator().next();
			// if (logger.isDebugEnabled())
			// logger.debug(String.format("For [%s] in scope [%s] got [%s]",
			// endpointName, scope, endpoint));
		} catch (Exception e) {
			throw new Exception("Could not setup communication info", e);
		}
		assertNotNull(endpoint);
	}

}
