/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class TestDataCatalogueMetadataFormatReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class TestDataCatalogueMetadataFormatReader {

	private static final Logger logger = LoggerFactory.getLogger(TestDataCatalogueMetadataFormatReader.class);

	/**
	 * Test.
	 */
	@Test
	public void test() {
		try {
			String scopeString = "/gcube/devNext/NextNext";
			ScopeProvider.instance.set(scopeString);
			DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();
			int i = 0;

			List<NamespaceCategory> categs = reader.getListOfNamespaceCategories();
			for (NamespaceCategory namespaceCategory : categs) {
				logger.trace("\n\n "+ ++i +".) Category: "+namespaceCategory);
			}

			i = 0;
			for (MetadataProfile mt : reader.getListOfMetadataProfiles()) {

				if(mt==null)
					continue;

				MetadataFormat metadataFormat = reader.getMetadataFormatForMetadataProfile(mt);
				logger.trace("\n\n "+ ++i +".) Metadata source: "+metadataFormat.getMetadataSource().substring(0, 100));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
