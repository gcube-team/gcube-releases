package org.gcube.data.analysis.dataminermanagercl.server.is;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfo;
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfoData;
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

	
	private static Logger logger = LoggerFactory.getLogger(InformationSystemUtils.class);

	public static String retrieveServiceAddress(String category, String name, String scope) throws Exception {
		try {
			logger.debug("Retrieve Service Address");
			if (scope == null || scope.length() == 0) {
				logger.error("No DataMiner service address retrieved, invalid scope requested: " + scope);
				throw new ServiceException("No DataMiner service address retrieved, invalid scope requested: " + scope);
			}

			ScopeProvider.instance.set(scope);

			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Category/text() eq '" + category + "'")
					.addCondition("$resource/Profile/Name/text() eq '" + name + "'")
					.setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
			DiscoveryClient<String> client = ICFactory.client();
			List<String> addresses = client.submit(query);

			logger.debug("Service Addresses retrieved:" + addresses);
			if (addresses == null || addresses.isEmpty()) {
				logger.error("No DataMiner service addresses available!");
				throw new ServiceException("No DataMiner service address available!");
			}

			String address = addresses.get(0);
			int wpsWebProcessingServiceIndex = address.indexOf(Constants.WPSWebProcessingService);
			String serviceAddress = null;
			if (wpsWebProcessingServiceIndex > 0) {
				serviceAddress = address.substring(0, wpsWebProcessingServiceIndex);
			} else {
				logger.error("Invalid DataMiner service url retrieved: " + address);
				throw new ServiceException("Invalid DataMiner service url retrieved: " + address);
			}
			logger.info("DataMiner service address found: " + serviceAddress);
			return serviceAddress;

		} catch (Throwable e) {
			logger.error("Error in discovery DataMiner Service Endpoint in scope: " + scope);
			logger.error("Error: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	public static ServiceInfo retrieveServiceInfo(String category, String name, String scope)
			throws Exception {
		try {
			logger.debug("Retrieve Service Properties");
			
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

			logger.debug("Service Info: " + serviceInfo);
			return serviceInfo;

		} catch (Throwable e) {
			logger.error("Error in discovery DataMiner Service Endpoint Info in scope: " + scope);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
	}

}
