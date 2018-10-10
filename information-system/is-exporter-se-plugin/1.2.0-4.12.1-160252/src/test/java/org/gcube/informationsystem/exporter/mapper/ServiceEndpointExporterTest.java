package org.gcube.informationsystem.exporter.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.informationsystem.exporter.ScopedTest;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClientFactory;
import org.gcube.informationsystem.resourceregistry.publisher.ResourceRegistryPublisher;
import org.gcube.informationsystem.resourceregistry.publisher.ResourceRegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ServiceEndpointExporterTest extends ScopedTest{
	
	private static Logger logger = LoggerFactory.getLogger(ServiceEndpointExporterTest.class);
	
	@Test
	public void export(){
		ServiceEndpointExporter see = new ServiceEndpointExporter(false);
		see.export();
	}
	
	public void removeExported() throws ObjectNotFound, Exception{
		ResourceRegistryClient client = ResourceRegistryClientFactory.create();
		ResourceRegistryPublisher publisher = ResourceRegistryPublisherFactory.create();
		
		List<EService> eServices = client.getInstances(EService.class, false);
		List<EService> failed = new ArrayList<>();
		
		logger.debug("Going to delete {} {}s", 
				eServices.size(), EService.NAME);
		
		int excluded = 0;
		
		for(EService eService : eServices){
			try {
				Facet facet = eService.getIdentificationFacets().get(0);
				String string = (String) facet.getAdditionalProperty(GCoreResourceMapper.EXPORTED);
				if(string!=null && string.compareTo(GCoreResourceMapper.EXPORTED_FROM_OLD_GCORE_IS)==0){
					publisher.deleteResource(eService);
				}else{
					excluded++;
				}
			}catch (Exception e) {
				failed.add(eService);
			}
		}
		
		logger.debug("{} of {} ({} failures) {}s were deleted.  {} {}s were excluded because there was not exported from gCore IS.", 
				eServices.size()-failed.size()-excluded, eServices.size(), failed.size(), EService.NAME, excluded, EService.NAME);
	}
	
	// @Test
	public void investigateSingleResource() throws Exception {
		// ScopedTest.setContext(GCUBE);
		
		UUID uuid = UUID.fromString("");
		//ResourceRegistryPublisher publisher = ResourceRegistryPublisherFactory.create();
		//publisher.deleteResource(uuid);
		//ResourceRegistryClient resourceRegistryClient = ResourceRegistryClientFactory.create();
		
		
		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class)
				.addCondition(String.format("$resource/ID/text() eq '%1s'", uuid.toString()));
		DiscoveryClient<ServiceEndpoint> client = ICFactory.clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> seList = client.submit(query);
		
		ServiceEndpointExporter see = new ServiceEndpointExporter(false);
		see.notifyFailures(seList.size(), seList);
		
	}
	
}
