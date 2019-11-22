package org.gcube.portlets.user.statisticalalgorithmsimporter.server.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.dminfo.ServiceInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.dminfo.ServiceInfoData;
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
			logger.info("Retrieve SAI descriptor in scope: " + scope);

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
									"Error {resource=" + genericResource + ", error=" + e.getLocalizedMessage() + "}",e);
							throw new StatAlgoImporterServiceException(error, e);
						}
						break;

					}

				}
			}
			
			if(saiDescriptorJaxB==null){
				String error = "Error in discovery SAI generic resource on IS in scope " + scope;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			
			}
			return saiDescriptorJaxB;

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery SAI generic resource on IS in scope " + scope;
			logger.error(error);
			logger.error(e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(error, e);
		}
	}

	public static String retrieveDataMinerPoolManager(String scope) throws StatAlgoImporterServiceException {
		try {
			logger.info("Retrieve dataminer-pool-manager in scope: "+scope);
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
			if(poolManagerURI==null||poolManagerURI.isEmpty()){
				String error = "Error in discovery DataMiner PoolManager gCubeEndpoint resource on IS in scope: "
						+ scope;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}
			return poolManagerURI;

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			String error = "Error in discovery DataMiner PoolManager gCubeEndpoint resource on IS in scope : " + scope;
			logger.error(error);
			logger.error(e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(error, e);
		}
	}

	public static String retrieveSocialNetworkingService(String scope) throws StatAlgoImporterServiceException {
		try {
			logger.info("Retrieve SocialNetworkingService in scope: "+scope);
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
			if(socialNetworkingURI==null||socialNetworkingResources.isEmpty()){
				String error = "Error in discovery SocialNetworking gCubeEndpoint resource on IS in scope: "
						+ scope;
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			
			}
			
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

	
	public static ServiceInfo retrieveServiceInfo(String category, String name, String scope)
			throws Exception {
		try {
			logger.debug("Retrieve DataMiner Service Properties");
			
			if (scope == null || scope.length() == 0){
				logger.error("Invalid request scope: "+scope);
				return null;
			}

			ScopeProvider.instance.set(scope);

			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Category/text() eq '" + category + "'")
					.addCondition("$resource/Profile/Name/text() eq '" + name + "'")
					.setResult("$resource/Profile/AccessPoint");
			DiscoveryClient<AccessPoint> client = ICFactory.clientFor(AccessPoint.class);
			List<AccessPoint> accessPointList = client.submit(query);
			
			String serviceAddress=null;
			ArrayList<ServiceInfoData> serviceProperties = new ArrayList<>();
			
			if (accessPointList != null && !accessPointList.isEmpty()) {
				AccessPoint accessPoint = accessPointList.get(0);
				if (accessPoint.address() != null && !accessPoint.address().isEmpty()) {
					String accessPointAddress = accessPoint.address();
					int wpsWebProcessingServiceIndex = accessPointAddress.indexOf(Constants.WPSWebProcessingService);
					if (wpsWebProcessingServiceIndex > 0) {
						serviceAddress = accessPointAddress.substring(0, wpsWebProcessingServiceIndex);
					}
				}

				if (accessPoint.properties() != null && !accessPoint.propertyMap().isEmpty()) {
					for (String key : accessPoint.propertyMap().keySet()) {
						Property property = accessPoint.propertyMap().get(key);
						if (property != null&&property.name() != null && !property.name().isEmpty()){
							if(property.name().contains(":")) {
								String[] propertyWithCategory=property.name().split(":");
								if(propertyWithCategory.length>=2){
									serviceProperties.add(new ServiceInfoData(propertyWithCategory[1],property.value(),propertyWithCategory[0]));
								} else {
									serviceProperties.add(new ServiceInfoData(property.name(), property.value(), Constants.DATA_MINER_DEFAULT_SERVICE_INFO_CATEGORY));
								}
							} else {
								serviceProperties.add(new ServiceInfoData(property.name(), property.value(), Constants.DATA_MINER_DEFAULT_SERVICE_INFO_CATEGORY));
							}
						} 
							

					}
				}

			}
			ServiceInfo serviceInfo=new ServiceInfo(serviceAddress,serviceProperties);

			logger.debug("DataMiner Service Info: " + serviceInfo);
			return serviceInfo;

		} catch (Throwable e) {
			logger.error("Error in discovery DataMiner Service Endpoint Info in scope: " + scope);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
	}
}
