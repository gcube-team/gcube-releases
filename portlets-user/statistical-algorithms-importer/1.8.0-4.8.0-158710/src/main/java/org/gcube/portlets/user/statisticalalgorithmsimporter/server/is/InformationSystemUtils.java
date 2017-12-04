package org.gcube.portlets.user.statisticalalgorithmsimporter.server.is;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.impl.JAXBParser;
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

	private static Logger logger = LoggerFactory.getLogger(InformationSystemUtils.class);

	public static SAIDescriptorJAXB retrieveSAIDescriptor(String scope) throws StatAlgoImporterServiceException {
		try {

			if (scope == null || scope.length() == 0)
				return null;

			ScopeProvider.instance.set(scope);
			logger.debug("Retrieve SAI descriptor in scope: " + scope);

			SimpleQuery query = ICFactory.queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '" + Constants.SAI_CATEGORY + "'")
					.addCondition("$resource/Profile/Name/text() eq '" + Constants.SAI_NAME + "'")
					.setResult("$resource");

			DiscoveryClient<GenericResource> client = ICFactory.clientFor(GenericResource.class);
			List<GenericResource> saiProfileResources = client.submit(query);
			logger.debug("Resources: " + saiProfileResources);

			SAIDescriptorJAXB saiDescriptorJaxB = null;

			for (GenericResource genericResource : saiProfileResources) {
				if (genericResource.scopes() != null) {
					ScopeGroup<String> scopes = genericResource.scopes();
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
							JAXBParser<SAIDescriptorJAXB> parser = new JAXBParser<SAIDescriptorJAXB>(
									SAIDescriptorJAXB.class);
							logger.debug("Body: " + genericResource.profile().bodyAsString());
							saiDescriptorJaxB = (SAIDescriptorJAXB) parser
									.parse(genericResource.profile().bodyAsString());
							logger.debug("Enable: " + saiDescriptorJaxB);
						} catch (Throwable e) {
							String error = "Error in discovery SAI profile generic resource on IS in scope " + scope
									+ ". " + "Resource parsing failed!";
							logger.error(error);
							logger.error(
									"Error {resource=" + genericResource + ", error=" + e.getLocalizedMessage() + "}");
							e.printStackTrace();
							throw new StatAlgoImporterServiceException(error, e);
						}
						break;

					}

				}
			}

			return saiDescriptorJaxB;

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery SAI profile generic resource on IS in scope : " + scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(error, e);
		}
	}

	public static String retrieveDataMinerPoolManager(String scope) throws StatAlgoImporterServiceException {
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
							throw new StatAlgoImporterServiceException(error, e);
						}
						break;

					}

				}
			}

			logger.debug("DataMiner PoolManager URI: " + poolManagerURI);
			return poolManagerURI;

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery DataMiner PoolManager gCubeEndpoint resource on IS in scope : " + scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(error, e);
		}
	}

	public static String retrieveSocialNetworkingService(String scope) throws StatAlgoImporterServiceException {
		try {
			logger.debug("Retrieve SocialNetworkingService");
			if (scope == null || scope.length() == 0)
				return null;

			ScopeProvider.instance.set(scope);
			logger.debug("SocialNetworkingService resource in scope: " + scope);

			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);

			query.addCondition("$resource/Profile/ServiceClass/text() eq '" + Constants.SOCIAL_NETWORKING_CLASS + "'")
					.addCondition("$resource/Profile/ServiceName/text() eq '" + Constants.SOCIAL_NETWORKING_NAME + "'")
					.setResult("$resource");

			DiscoveryClient<GCoreEndpoint> client = ICFactory.clientFor(GCoreEndpoint.class);
			List<GCoreEndpoint> socialNetworkingResources = client.submit(query);
			logger.debug("Resources: " + socialNetworkingResources);

			String socialNetworkingURI = null;

			for (GCoreEndpoint gCoreEndpoint : socialNetworkingResources) {
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
							logger.debug("SocialNetworking Endpoints map: " + gCoreEndpoint.profile().endpointMap());
							Map<String, Endpoint> endpointMap = gCoreEndpoint.profile().endpointMap();
							Endpoint dataMinerEndpoint = endpointMap.get("jersey-servlet");
							logger.debug("SocialNetworking Endpoint: " + dataMinerEndpoint);

							if (dataMinerEndpoint != null && dataMinerEndpoint.uri() != null) {
								socialNetworkingURI = dataMinerEndpoint.uri().toString();
							}

						} catch (Throwable e) {
							String error = "Error in discovery SocialNetworking gCubeEndpoint resource on IS in scope: "
									+ scope;
							logger.error(error);
							logger.error(
									"Error {resource=" + gCoreEndpoint + ", error=" + e.getLocalizedMessage() + "}");
							e.printStackTrace();
							throw new StatAlgoImporterServiceException(error, e);
						}
						break;

					}

				}
			}

			logger.debug("SocialNetworking URI: " + socialNetworkingURI);
			return socialNetworkingURI;

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery SocialNetworking gCubeEndpoint resource on IS in scope : " + scope;
			logger.error(error);
			logger.error("Error: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(error, e);
		}
	}

}
