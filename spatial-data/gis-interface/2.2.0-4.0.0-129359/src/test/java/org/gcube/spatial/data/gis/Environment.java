package org.gcube.spatial.data.gis;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;

public class Environment {
	@Test
	public void test() throws Exception{
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		System.out.println(GISInterface.get().getCurrentGeoServerDescriptor());
	}
	
}
