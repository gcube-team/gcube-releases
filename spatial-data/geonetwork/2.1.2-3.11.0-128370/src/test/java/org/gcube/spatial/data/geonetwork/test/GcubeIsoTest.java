package org.gcube.spatial.data.geonetwork.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;

public class GcubeIsoTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	 new GcubeISOMetadata();

	}

}
