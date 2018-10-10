package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gcubeClass;
import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gcubeName;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class TestCommon {
	static DateFormat dateFormatter= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS_z");
	static String SCOPE="/gcube/devsec";
	
	public static URI getServiceURI(String endpointName){
		SimpleQuery query=queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+gcubeClass+"'")
        .addCondition("$resource/Profile/ServiceName/text() eq '"+gcubeName+"'");
		DiscoveryClient<GCoreEndpoint> client=clientFor(GCoreEndpoint.class);
		
		GCoreEndpoint retrieved=client.submit(query).get(0);
		URI uri=retrieved.profile().endpointMap().get(endpointName).uri();
		System.out.println("Found service @ "+uri);
		return uri;
	}
}
