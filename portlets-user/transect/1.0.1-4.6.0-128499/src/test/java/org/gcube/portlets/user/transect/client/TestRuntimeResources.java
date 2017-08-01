/**
 * 
 */
package org.gcube.portlets.user.transect.client;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.transect.server.TransectServiceImpl;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it May 28, 2015
 */
public class TestRuntimeResources {

	public static void main(String[] args) {

		TransectServiceImpl serviceImpl = new TransectServiceImpl();
		try {
			LexicalEngineConfiguration aquamapsconfig = serviceImpl
					.getDatabaseConfig("/gcube/devsec/devVRE",
							TransectServiceImpl.TransectAuxiliaryDatabase);

			LexicalEngineConfiguration geoserverconfig = serviceImpl
					.getDatabaseConfig("/gcube/devsec/devVRE",
							TransectServiceImpl.TransectGeoDatabase);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
