/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: TestProperties.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.util.Properties;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;

/**
 * @author Daniele Strollo (ISTI-CNR)
 */
public class TestProperties {
	public static void main(final String[] args) {
		Properties props = BrokerConfiguration.getProperties();
		System.out.println(props);
		try {
			System.out.println(BrokerConfiguration.getProperty(
					"ENABLE_UPDATE_GHN_HANDLER"));
			System.out.println(BrokerConfiguration.getProperty(
			"GHN_RESERVATION_WEIGHT"));
			System.out.println(BrokerConfiguration.getRawProperty("DEFAULT_SCOPES"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
