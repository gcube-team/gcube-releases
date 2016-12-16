/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.reader.MedataFormatReader;
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
		MedataFormatReader reader;
		try {
			reader = new MedataFormatReader(scope, "78355412-b45a-4519-adce-679452583aa2");
			System.out.println(reader.getMetadataFormat());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
