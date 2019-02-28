/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatDiscovery;
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
		MetadataFormatDiscovery reader;
		try {
			ScopeProvider.instance.set(scopeString);
			reader = new MetadataFormatDiscovery(scope);
			//System.out.println(reader.getMetadataProfiles());

			for (MetadataProfile metaProfile : reader.getMetadataProfiles()) {
				System.out.println(metaProfile.getId() +", name: "+metaProfile.getName() +", type: "+metaProfile.getMetadataType());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
