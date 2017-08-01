package org.gcube.data.analysis.dataminermanagercl.server.is;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InformationSystemUtils {

	private static Logger logger = LoggerFactory
			.getLogger(InformationSystemUtils.class);

	public static List<String> retrieveServiceAddress(String category,
			String name, String scope) throws Exception {
		try {

			if (scope == null || scope.length() == 0)
				return new ArrayList<String>();

			ScopeProvider.instance.set(scope);

			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
			query.addCondition(
					"$resource/Profile/Category/text() eq '" + category + "'")
					.addCondition(
							"$resource/Profile/Name/text() eq '" + name + "'")
					.setResult(
							"$resource/Profile/AccessPoint/Interface/Endpoint/text()");
			DiscoveryClient<String> client = ICFactory.client();
			List<String> addresses = client.submit(query);

			return addresses;

		} catch (Throwable e) {
			logger.error("Error in discovery DataMiner Service Endpoint in scope: "
					+ scope);
			logger.error("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

}
