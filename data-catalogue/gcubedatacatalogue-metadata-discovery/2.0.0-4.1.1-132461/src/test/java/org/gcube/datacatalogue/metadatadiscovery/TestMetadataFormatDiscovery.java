/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.datacatalogue.metadatadiscovery.reader.MedataFormatDiscovery;
import org.junit.Test;


/**
 * The Class TestMetadataFormatDiscovery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class TestMetadataFormatDiscovery {

	/**
	 * Test.
	 */
	@Test
	public void test() {

		String scopeString = "/gcube/devNext/NextNext";
		final ScopeBean scope  = new ScopeBean(scopeString);
		MedataFormatDiscovery reader;
		try {
			ScopeProvider.instance.set(scopeString);
			reader = new MedataFormatDiscovery(scope);
			System.out.println(reader.getMetadataTypes());

			for (MetadataType type : reader.getMetadataTypes()) {
				System.out.println(type);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
