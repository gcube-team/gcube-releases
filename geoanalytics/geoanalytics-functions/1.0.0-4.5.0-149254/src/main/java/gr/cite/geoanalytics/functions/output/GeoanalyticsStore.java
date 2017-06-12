package gr.cite.geoanalytics.functions.output;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.spark.api.java.JavaRDD;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.trafficshaping.SimpleTrafficShaper;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.client.GeoanalyticsImportManagement;

@Component
public class GeoanalyticsStore {

	private final String GEOANALYTICS_ENDPOINT;
	
	 @Autowired private GeoanalyticsImportManagement geoanalyticsImportManagement;
	
	public GeoanalyticsStore(String scope) throws Exception {
		//TODO: consider moving the discovery of geoanalytics endpoints on zookeeper
		List<String> eps = discoverGeoanalyticsEndpoints(scope);
		GEOANALYTICS_ENDPOINT = eps.get(new Random().nextInt(eps.size()));
		
//		//Override for testing... please use the code above
//		GEOANALYTICS_ENDPOINT = "http://dionysus.di.uoa.gr:8080/geoanalytics";

		
	}
	
	
	public String storeToGeoanalytics(String layerName, String tenantID, String creatorID, GosDefinition gosDefinition, JavaRDD<List<ShapeMessenger>> featuresRDD, String srid) throws Exception{
		return geoanalyticsImportManagement.importLayerFromRDD(GEOANALYTICS_ENDPOINT, gosDefinition, layerName, tenantID, creatorID, featuresRDD, srid);
	}
	
	
	
	public static List<String> discoverGeoanalyticsEndpoints(String scope) {

		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(GCoreEndpoint.class);

		String serviceName = "geoanalytics-main-service";
		String serviceClass = "geoanalytics";

		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceClass + "'")
			 .addCondition("$resource/Profile/ServiceName/text() eq '" + serviceName + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> eprs = client.submit(query);

		Set<String> geoanalyticsEPs = new HashSet<String>();
		if(eprs != null){
			for (GCoreEndpoint epr : eprs) {
				if (!"ready".equals(epr.profile().deploymentData().status().toLowerCase()))
					continue;
				for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
					if (e.uri().toString().endsWith("/"))
						geoanalyticsEPs.add(e.uri().toString());
			}
		}
		
		return new ArrayList<String>(geoanalyticsEPs);
	}
	
	
}
