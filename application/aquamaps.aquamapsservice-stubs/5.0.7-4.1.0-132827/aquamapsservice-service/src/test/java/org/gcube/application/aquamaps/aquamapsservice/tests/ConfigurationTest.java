package org.gcube.application.aquamaps.aquamapsservice.tests;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.common.core.scope.GCUBEScope;

public class ConfigurationTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		GCUBEScope infrastructureScope=GCUBEScope.getScope("/gcube/devsec");
//		GCUBEScope infrastructureScope=GCUBEScope.getScope("/d4science.research-infrastructures.eu/gCubeApps");
		ConfigurationManager.init(infrastructureScope);
		System.out.println(ConfigurationManager.getVODescriptor());

	}

}
