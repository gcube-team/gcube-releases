/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
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
			String scopeString = "/gcube/devsec/devVRE";
			ScopeProvider.instance.set(scopeString);
			DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();
			logger.trace(""+reader.getListOfMetadataTypes());

			for (MetadataType mt : reader.getListOfMetadataTypes()) {
				logger.trace(""+reader.getMetadataFormatForMetadataType(mt));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
