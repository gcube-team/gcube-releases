/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import org.gcube.portlets.user.performfishanalytics.server.util.GcoreEndpointReader;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 23, 2019
 */
public class TestGcoreEndpointReader {


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {

			GcoreEndpointReader reader = new GcoreEndpointReader("/gcube/preprod/preVRE", "perform-service", "Application", "org.gcube.application.perform.service.PerformService");
			System.out.println(reader);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
