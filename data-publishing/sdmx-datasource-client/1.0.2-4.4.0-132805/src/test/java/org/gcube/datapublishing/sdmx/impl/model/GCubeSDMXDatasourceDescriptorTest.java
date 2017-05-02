package org.gcube.datapublishing.sdmx.impl.model;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.api.model.GCubeSDMXDatasourceDescriptor;
import org.gcube.datapublishing.sdmx.impl.model.GCubeSDMXDatasourceDescriptorIS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GCubeSDMXDatasourceDescriptorTest {
	
	GCubeSDMXDatasourceDescriptor datasource;
	
	@Before
	public void setUp() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
		datasource = new GCubeSDMXDatasourceDescriptorIS();
	}

	@Test
	public void retrieveDescriptor() {
		Assert.assertFalse(datasource.getRest_url_V1().isEmpty());
		Assert.assertFalse(datasource.getRest_url_V2().isEmpty());
		Assert.assertFalse(datasource.getRest_url_V2_1().isEmpty());
		Assert.assertFalse(datasource.getPublishInterfaceUrl().isEmpty());
	}

}
