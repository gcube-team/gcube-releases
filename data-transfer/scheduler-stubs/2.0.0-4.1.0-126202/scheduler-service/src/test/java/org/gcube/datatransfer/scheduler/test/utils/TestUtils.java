/**
 * 
 */
package org.gcube.datatransfer.scheduler.test.utils;

import org.gcube.common.mycontainer.Gar;


public class TestUtils {

	public static Gar SAMPLE_GAR() {
		return new Gar("scheduler-service").
				addConfigurations("../config").
				addInterfaces("../wsdl");
	}
}
