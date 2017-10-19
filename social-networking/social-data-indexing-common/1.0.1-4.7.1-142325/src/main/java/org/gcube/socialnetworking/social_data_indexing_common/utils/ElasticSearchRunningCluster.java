package org.gcube.socialnetworking.social_data_indexing_common.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.socialnetworking.social_data_indexing_common.ex.NoElasticSearchRuntimeResourceException;
import org.gcube.socialnetworking.social_data_indexing_common.ex.ServiceEndPointException;
import org.gcube.socialnetworking.social_data_indexing_common.ex.TooManyRunningClustersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieve elasticsearch's running instance information in the infrastructure.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class ElasticSearchRunningCluster {

	//logger
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchRunningCluster.class);

	//properties 
	private final static String RUNTIME_RESOURCE_NAME = "SocialPortalDataIndex";
	private final static String PLATFORM_NAME = "ElasticSearch";

	// retrieved data
	private List<String> hosts = new ArrayList<String>();
	private List<Integer> ports = new ArrayList<Integer>();
	private String clusterName;

	public ElasticSearchRunningCluster(String infrastructure) throws Exception{

		try {

			List<ServiceEndpoint> resources = getConfigurationFromIS(infrastructure);

			if (resources.size() > 1) {
				logger.error("Too many Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" in this scope");
				throw new TooManyRunningClustersException("There exist more than 1 Runtime Resource in this scope having name " 
						+ RUNTIME_RESOURCE_NAME + " and Platform " + PLATFORM_NAME + ". Only one allowed per infrasrtucture.");
			}
			else if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Platform " + PLATFORM_NAME + " in this scope.");
				throw new NoElasticSearchRuntimeResourceException();
			}
			else {

				try{

					logger.debug(resources.toString());
					for (ServiceEndpoint res : resources) {

						Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

						while (accessPointIterator.hasNext()) {
							ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
									.next();

							// add this host
							hosts.add(accessPoint.address().split(":")[0]);

							// save the port
							int port = Integer.parseInt(accessPoint.address().split(":")[1]);
							ports.add(port);

							// save the name of the cluster (this should be unique)
							clusterName = accessPoint.name();

						}
					}
				}catch(Exception e ){

					logger.error(e.toString());
					throw new ServiceEndPointException();
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
		}

	}

	/**
	 * Retrieve endpoints information from IS
	 * @return list of endpoints for elasticsearch
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getConfigurationFromIS(String infrastructure) throws Exception{

		PortalContext context = PortalContext.getConfiguration();		
		String scope = "/";
		if(infrastructure != null && !infrastructure.isEmpty())
			scope += infrastructure;
		else
			scope += context.getInfrastructureName();

		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+ PLATFORM_NAME +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;

	}		

	public List<String> getHosts() {
		return hosts;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public String getClusterName() {
		return clusterName;
	}

}
