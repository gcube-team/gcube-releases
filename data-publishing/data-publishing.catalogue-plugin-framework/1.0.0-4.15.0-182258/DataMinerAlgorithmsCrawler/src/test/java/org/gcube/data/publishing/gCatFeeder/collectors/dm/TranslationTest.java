package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.InternalAlgorithmDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.data.publishing.gCatFeeder.tests.BaseCollectorTest;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.gcube.data.publishing.gCatfeeder.collectors.DataCollector;
import org.gcube.data.publishing.gCatfeeder.collectors.DataTransformer;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class TranslationTest extends BaseCollectorTest{


	@Test
	public void testTranslation() throws CollectorFault, CatalogueNotSupportedException, JsonGenerationException, JsonMappingException, IOException, InternalConversionException {
		Assume.assumeTrue(isTestInfrastructureEnabled());
			System.out.println("Entering Infrastructure enabled tests..");
			ObjectMapper mapper = new ObjectMapper();

			CollectorPlugin plugin=new DataMinerPlugin();
			plugin.setEnvironmentConfiguration(getEnvironmentConfiguration());
			DataCollector collector=plugin.getCollector();
			Collection collected=collector.collect();
			System.out.println("Found "+collected.size()+" elements");
			for(Object obj:collected)
				System.out.println(mapper.writeValueAsString(obj)+"\n");

			for(String destinationcatalogue : (Set<String>)plugin.getSupportedCatalogueTypes()) {
				DataTransformer<? extends CatalogueFormatData, InternalAlgorithmDescriptor> transformer=plugin.getTransformerByCatalogueType(destinationcatalogue);
				for(Object data:transformer.transform(collected))
					System.out.println(((CatalogueFormatData)data).toCatalogueFormat());
			
		}
	}

	
}
