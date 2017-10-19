package org.gcube.portlets.admin.dataminermanagerdeployer.server.is;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.Constants;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.exception.ServiceException;
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

	public static String retrieveDataMinerPoolManager(String scope) throws ServiceException {
		try {
			logger.debug("Retrieve dataminer-pool-manager");
			if (scope == null || scope.length() == 0)
				return null;

			ScopeProvider.instance.set(scope);
			logger.debug("Retrieve DataMiner Pool Manager resource in scope: " + scope);

			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);

			query.addCondition(
					"$resource/Profile/ServiceClass/text() eq '" + Constants.POOL_MANAGER_SERVICE_CLASS + "'")
					.addCondition(
							"$resource/Profile/ServiceName/text() eq '" + Constants.POOL_MANAGER_SERVICE_NAME + "'")
					.setResult("$resource");

			DiscoveryClient<GCoreEndpoint> client = ICFactory.clientFor(GCoreEndpoint.class);
			List<GCoreEndpoint> dataMinerPoolManagerResources = client.submit(query);
			logger.debug("Resources: " + dataMinerPoolManagerResources);

			String poolManagerURI = null;

			for (GCoreEndpoint gCoreEndpoint : dataMinerPoolManagerResources) {
				if (gCoreEndpoint.scopes() != null) {
					ScopeGroup<String> scopes = gCoreEndpoint.scopes();
					Iterator<String> iterator = scopes.iterator();
					String scopeFound = null;
					boolean found = false;
					while (iterator.hasNext() && !found) {
						scopeFound = iterator.next();
						if (scopeFound.compareTo(scope) == 0) {
							found = true;
						}
					}
					if (found) {
						try {
							logger.debug(
									"DataMiner PoolManager Endpoints map: " + gCoreEndpoint.profile().endpointMap());
							Map<String, Endpoint> endpointMap = gCoreEndpoint.profile().endpointMap();
							Endpoint dataMinerEndpoint = endpointMap.get("REST-API");
							logger.debug("DataMiner PoolManager Endpoint: " + dataMinerEndpoint);

							if (dataMinerEndpoint != null && dataMinerEndpoint.uri() != null) {
								poolManagerURI = dataMinerEndpoint.uri().toString();
							}

						} catch (Throwable e) {
							String error = "Error in discovery DataMiner PoolManager gCubeEndpoint resource on IS in scope: "
									+ scope;
							logger.error(error);
							logger.error(
									"Error {resource=" + gCoreEndpoint + ", error=" + e.getLocalizedMessage() + "}");
							e.printStackTrace();
							throw new ServiceException(error, e);
						}
						break;

					}

				}
			}

			logger.debug("DataMiner PoolManager URI: " + poolManagerURI);
			return poolManagerURI;

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery DataMiner PoolManager gCubeEndpoint resource on IS in scope : " + scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage(), e);
			throw new ServiceException(error, e);
		}
	}

}
