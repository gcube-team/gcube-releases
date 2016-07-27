/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.impl.ScopeBean;
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

		String scopeString = "/gcube/devsec/devVRE";
		final ScopeBean scope  = new ScopeBean(scopeString);
		MedataFormatDiscovery reader;
		try {
			reader = new MedataFormatDiscovery(scope);
			System.out.println(reader.getMetadataTypes());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
