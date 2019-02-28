/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.reader.MetadataFormatReader;
import org.junit.Test;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class TestMetadataFormatReader {

	@Test
	public void test() {

		String scopeString = "/gcube/devsec/devVRE";
		final ScopeBean scope  = new ScopeBean(scopeString);
		MetadataFormatReader reader;
		try {
			ScopeProvider.instance.set(scopeString);
			reader = new MetadataFormatReader(scope, "0d29d7a9-d779-478c-a13d-d70708dc66c4");
			System.out.println(reader.getMetadataFormat());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
