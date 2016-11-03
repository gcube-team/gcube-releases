package org.gcube.spatial.data.gis;

import org.junit.Test;

public class Environment {
	@Test
	public void test() throws Exception{
		TokenSetter.set("/d4science.research-infrastructures.eu");
		System.out.println(GISInterface.get().getCurrentGeoServerDescriptor());
	}
	
}
