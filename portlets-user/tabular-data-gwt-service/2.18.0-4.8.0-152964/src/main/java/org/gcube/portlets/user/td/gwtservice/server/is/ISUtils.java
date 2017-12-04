package org.gcube.portlets.user.td.gwtservice.server.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ISUtils {

	private static Logger logger = LoggerFactory.getLogger(ISUtils.class);

	public static String retrieveInternalSDMXRegistryURL() {
		String sdmxRegistryURL = null;
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'SDMX'")
				.addCondition("$resource/Profile/Name/text() eq 'SDMXRegistry'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> listService = client.submit(query);
		if (listService.size() > 0) {
			ServiceEndpoint serviceEnd = listService.get(0);
			if (serviceEnd != null) {
				Profile prof = serviceEnd.profile();
				Group<AccessPoint> groupA = prof.accessPoints();
				for (AccessPoint acc : groupA) {
					if (acc.description().compareTo("REST Interface v2.1") == 0) {
						sdmxRegistryURL = acc.address();
						break;
					}
				}
			} else {

			}
		} else {

		}
		logger.debug("Retrieved URL of the default SDMX Registry: " + sdmxRegistryURL);
		return sdmxRegistryURL;

	}

}