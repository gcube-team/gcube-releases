/**
 *
 */
package org.gcube.portlets.admin.gcubereleases;

import org.apache.commons.httpclient.HttpStatus;
import org.gcube.portlets.admin.gcubereleases.server.util.HttpCheckAvailabilityUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 16, 2016
 */
public class CheckUrlStatus {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
			System.out.println(HttpCheckAvailabilityUtil.checkUrlStatus("http://www.bdabjdadbasi.com/", 3, 2000, HttpStatus.SC_OK));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
