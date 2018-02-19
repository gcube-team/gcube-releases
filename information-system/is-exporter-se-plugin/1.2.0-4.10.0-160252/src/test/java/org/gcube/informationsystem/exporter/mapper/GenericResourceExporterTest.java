package org.gcube.informationsystem.exporter.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.informationsystem.exporter.ScopedTest;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.resource.Configuration;
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
public class GenericResourceExporterTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(GenericResourceExporterTest.class);

	@Test
	public void export() {
		GenericResourceExporter gre = new GenericResourceExporter(false);
		gre.export();
	}

	public void removeExported() throws ObjectNotFound, Exception {
		ResourceRegistryClient client = ResourceRegistryClientFactory.create();
		ResourceRegistryPublisher publisher = ResourceRegistryPublisherFactory.create();

		List<Configuration> configurations = client.getInstances(Configuration.class, false);
		List<Configuration> failed = new ArrayList<>();

		logger.debug("Going to delete {} {}s", configurations.size(), Configuration.NAME);

		int excluded = 0;

		for (Configuration configuration : configurations) {
			try {
				Facet facet = configuration.getIdentificationFacets().get(0);
				String string = (String) facet.getAdditionalProperty(GCoreResourceMapper.EXPORTED);
				publisher.deleteResource(configuration);
				if (string != null && string.compareTo(GCoreResourceMapper.EXPORTED_FROM_OLD_GCORE_IS) == 0) {
					publisher.deleteResource(configuration);
				} else {
					excluded++;
				}
			} catch (Exception e) {
				failed.add(configuration);
			}
		}

		logger.debug(
				"{} of {} ({} failures) {}s were deleted. {} {}s were excluded because there was not exported from gCore IS.",
				configurations.size() - failed.size() - excluded, configurations.size(), failed.size(),
				Configuration.NAME, excluded, Configuration.NAME);
	}

	// @Test
	public void investigateSingleResource() throws Exception {
		//ScopedTest.setContext(ScopedTest.GCUBE_DEVSEC);

		UUID uuid = UUID.fromString("");

		//ResourceRegistryPublisher publisher = ResourceRegistryPublisherFactory.create();
		//publisher.deleteResource(uuid);

		// ResourceRegistryClient resourceRegistryClient = ResourceRegistryClientFactory.create();

		SimpleQuery query = ICFactory.queryFor(GenericResource.class)
				.addCondition(String.format("$resource/ID/text() eq '%1s'", uuid.toString()));
		DiscoveryClient<GenericResource> client = ICFactory.clientFor(GenericResource.class);
		List<GenericResource> seList = client.submit(query);

		GenericResourceExporter see = new GenericResourceExporter(false);
		see.mapAndPublish(seList.get(0));

	}

}
